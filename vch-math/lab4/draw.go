package main

import (
	"fmt"
	"gonum.org/v1/plot"
	"gonum.org/v1/plot/plotter"
	"gonum.org/v1/plot/plotutil"
	"gonum.org/v1/plot/vg"
	"math"
)

type Point struct {
	X, Y float64
}

func drawPlots(points []Point, results []ApproximationResult) {
	p := plot.New()
	p.Title.Text = "Аппроксимация данных различными функциями"
	p.X.Label.Text = "x"
	p.Y.Label.Text = "y"

	// Добавляем исходные точки
	pts := make(plotter.XYs, len(points))
	for i, pt := range points {
		pts[i].X = pt.X
		pts[i].Y = pt.Y
	}

	scatter, err := plotter.NewScatter(pts)
	if err != nil {
		fmt.Printf("Ошибка создания графика: %v\n", err)
		return
	}
	scatter.GlyphStyle.Color = plotutil.Color(0)
	p.Add(scatter)
	p.Legend.Add("Исходные данные", scatter)

	// Определяем границы для плавных кривых
	minX, maxX := points[0].X, points[0].X
	for _, pt := range points {
		if pt.X < minX {
			minX = pt.X
		}
		if pt.X > maxX {
			maxX = pt.X
		}
	}
	step := (maxX - minX) / 100
	xSmooth := make([]float64, 101)
	for i := range xSmooth {
		xSmooth[i] = minX + float64(i)*step
	}

	// Добавляем графики аппроксимирующих функций
	for i, res := range results {
		if !res.CanDraw {
			continue
		}

		// Создаем точки для плавной кривой
		linePts := make(plotter.XYs, 0, len(xSmooth))
		for _, x := range xSmooth {
			switch res.Name {
			case "Линейная":
				a, b := res.Params[0], res.Params[1]
				y := a + b*x
				linePts = append(linePts, plotter.XY{X: x, Y: y})
			case "Квадратичная":
				a, b, c := res.Params[0], res.Params[1], res.Params[2]
				y := a + b*x + c*x*x
				linePts = append(linePts, plotter.XY{X: x, Y: y})
			case "Кубическая":
				a, b, c, d := res.Params[0], res.Params[1], res.Params[2], res.Params[3]
				y := a + b*x + c*x*x + d*x*x*x
				linePts = append(linePts, plotter.XY{X: x, Y: y})
			case "Экспоненциальная":
				a, b := res.Params[0], res.Params[1]
				y := a * math.Exp(b*x)
				linePts = append(linePts, plotter.XY{X: x, Y: y})
			case "Логарифмическая":
				a, b := res.Params[0], res.Params[1]
				if x > 0 {
					y := a + b*math.Log(x)
					linePts = append(linePts, plotter.XY{X: x, Y: y})
				}
			case "Степенная":
				a, b := res.Params[0], res.Params[1]
				if x > 0 {
					y := a * math.Pow(x, b)
					linePts = append(linePts, plotter.XY{X: x, Y: y})
				}
			}
		}

		line, err := plotter.NewLine(linePts)
		if err != nil {
			continue
		}
		line.Color = plotutil.Color(i + 1)
		p.Add(line)
		p.Legend.Add(res.Name, line)
	}

	// Сохраняем график в файл
	if err := p.Save(10*vg.Inch, 8*vg.Inch, "approximation.png"); err != nil {
		fmt.Printf("Ошибка сохранения графика: %v\n", err)
		return
	}

	fmt.Println("\nГрафик сохранен в файл approximation.png")
}
