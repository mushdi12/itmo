package main

import (
	"bufio"
	"fmt"
	"gonum.org/v1/plot/vg/draw"
	"image/color"
	"math"
	"os"
	"strconv"

	"gonum.org/v1/plot"
	"gonum.org/v1/plot/plotter"
	"gonum.org/v1/plot/vg"
)

type System struct {
	Name     string
	F        func(x, y float64) float64
	G        func(x, y float64) float64
	Solution [][]float64
}

func plotSystem(sys System, x, y float64) {
	if math.IsNaN(x) || math.IsNaN(y) {
		fmt.Println("Error: Cannot plot invalid solution (NaN values).")
		return
	}

	fmt.Printf("x: %f, y: %f\n", x, y)

	p := plot.New()
	p.Title.Text = "График системы уравнений"
	p.X.Label.Text = "X"
	p.Y.Label.Text = "Y"

	fData, err := plotter.NewScatter(generatePoints(sys.F))
	if err != nil {
		fmt.Println("Error creating scatter plot for F:", err)
		return
	}
	gData, err := plotter.NewScatter(generatePoints(sys.G))
	if err != nil {
		fmt.Println("Error creating scatter plot for G:", err)
		return
	}

	solution, err := plotter.NewScatter(plotter.XYs{{X: x, Y: y}})
	if err != nil {
		fmt.Println("Error creating scatter plot for solution:", err)
		return
	}
	solution.GlyphStyle.Shape = draw.PyramidGlyph{}
	solution.GlyphStyle.Color = color.RGBA{R: 255, G: 0, B: 0, A: 255}

	p.Add(fData, gData, solution)

	if err := p.Save(6*vg.Inch, 6*vg.Inch, "plot.png"); err != nil {
		fmt.Println("Ошибка сохранения графика:", err)
		os.Exit(1)
	}
	fmt.Println("График сохранен в plot.png")
}

func generatePoints(f func(x, y float64) float64) plotter.XYs {
	points := plotter.XYs{}
	step := 0.1
	threshold := 0.1

	for x := -2.0; x <= 2.0; x += step {
		for y := -2.0; y <= 2.0; y += step {
			if math.Abs(f(x, y)) < threshold {
				points = append(points, plotter.XY{X: x, Y: y})
			}
		}
	}

	if len(points) == 0 {
		fmt.Println("Warning: No points generated!")
	}

	return points
}

func main() {
	systems := initializeSystems()
	sys := selectSystem(systems)

	for {
		fmt.Println("Выберите способ ввода данных:")
		fmt.Println("1 - С клавиатуры")
		fmt.Println("2 - Из файла")
		fmt.Print("Введите номер метода: ")
		var inputType int
		fmt.Scan(&inputType)

		if inputType == 1 {
			x0, y0 := getInitialApproximation()
			x, y, iterations := solveSystem(sys, x0, y0)
			printResults(sys, x, y, iterations)
			plotSystem(sys, x, y)
			break
		} else if inputType == 2 {
			x0, y0 := readFromFile()
			x, y, iterations := solveSystem(sys, x0, y0)
			printResults(sys, x, y, iterations)
			plotSystem(sys, x, y)
			break
		}
		fmt.Println("Неверный выбор. Попробуйте снова.")
	}
}

func readFromFile() (float64, float64) {
	fmt.Print("Введите имя файла: ")
	var filename string
	_, _ = fmt.Scan(&filename)

	file, err := os.Open(filename)
	if err != nil {
		fmt.Println("Ошибка открытия файла:", err)
		os.Exit(1)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	var x, y float64

	if scanner.Scan() {
		x, _ = strconv.ParseFloat(scanner.Text(), 64)
	}
	if scanner.Scan() {
		y, _ = strconv.ParseFloat(scanner.Text(), 64)
	}

	return x, y
}

func initializeSystems() []System {
	return []System{
		{
			Name: "tg(xy + 0.1) = x²\nx² + 2y² = 1",
			F:    func(x, y float64) float64 { return math.Tan(x*y+0.1) - x*x },
			G:    func(x, y float64) float64 { return x*x + 2*y*y - 1 },
			Solution: [][]float64{
				{0.6508, 0.5252},   // 1 решение
				{-0.1214, 0.7019},  // 2 решение
				{0.1214, -0.7019},  // 3 решение
				{-0.6508, -0.5252}, // 4 решение
			},
		},
		{
			Name: "sin(x) + y = 0.5\nx + cos(y) = 0.5",
			F:    func(x, y float64) float64 { return math.Sin(x) + y - 0.5 },
			G:    func(x, y float64) float64 { return x + math.Cos(y) - 0.5 },
			Solution: [][]float64{
				{0.2127, 0.2873},  // 1 решение
				{-1.0689, 1.3996}, // 2 решение
				{1.7873, -0.9273}, // 3 решение
			},
		},
		{
			Name: "sin(x+y) = 1.5x - 0.1\nx² + 2y² = 1",
			F:    func(x, y float64) float64 { return math.Sin(x+y) - 1.5*x + 0.1 },
			G:    func(x, y float64) float64 { return x*x + 2*y*y - 1 },
			Solution: [][]float64{
				{0.696, 0.5138},   // 1 решение
				{-0.5333, -0.598}, // 1 решение
			},
		},
		{
			Name: "x² + y² = 1\nx³ - y = 0",
			F:    func(x, y float64) float64 { return x*x + y*y - 1 },
			G:    func(x, y float64) float64 { return x*x*x - y },
			Solution: [][]float64{
				{0.78615, 0.6138},  // 1 решение
				{-0.78615, 0.6138}, // 1 решение
			},
		},
	}
}

func selectSystem(systems []System) System {
	fmt.Println("Выберите систему уравнений:")
	for i, sys := range systems {
		fmt.Printf("%d) %s\n", i+1, sys.Name)
	}

	var choice int
	for {
		fmt.Print("Введите номер системы: ")
		_, err := fmt.Scan(&choice)
		if err == nil && choice >= 1 && choice <= len(systems) {
			break
		}
		fmt.Println("Неверный ввод, попробуйте еще раз")
	}
	return systems[choice-1]
}

func getInitialApproximation() (float64, float64) {
	var x0, y0 float64

	fmt.Print("Введите начальное x0: ")
	for {
		_, err := fmt.Scan(&x0)
		if err == nil {
			break
		}
		fmt.Print("Неверный ввод, попробуйте еще раз :")
	}

	fmt.Print("Введите начальное y0: ")
	for {
		_, err := fmt.Scan(&y0)
		if err == nil {
			break
		}
		fmt.Print("Неверный ввод, попробуйте еще раз : ")
	}

	return x0, y0
}

func solveSystem(sys System, x0, y0 float64) (float64, float64, int) {
	const (
		eps     = 1e-6
		maxIter = 1000
	)
	return newtonMethod(sys, x0, y0, eps, maxIter)
}

func printResults(sys System, x, y float64, iterations int) {
	fmt.Printf("\nНайденное решение:\nx = %.6f\ny = %.6f\n", x, y)
	fmt.Printf("Количество итераций: %d\n", iterations)

	var closestSolution []float64
	minDistance := math.MaxFloat64

	for _, sol := range sys.Solution {
		distance := math.Sqrt(math.Pow(x-sol[0], 2) + math.Pow(y-sol[1], 2))
		if distance < minDistance {
			minDistance = distance
			closestSolution = sol
		}
	}

	if closestSolution != nil {
		dx := math.Abs(x - closestSolution[0])
		dy := math.Abs(y - closestSolution[1])
		fmt.Printf("\nПогрешности решения (%.6f, %.6f):\n",
			closestSolution[0], closestSolution[1])
		fmt.Printf("Δx = %.6f\nΔy = %.6f\n", dx, dy)
		fmt.Printf("Евклидова норма погрешности = %.6f\n", math.Sqrt(dx*dx+dy*dy))
	}
}

func newtonMethod(sys System, x0, y0, eps float64, maxIter int) (float64, float64, int) {
	x, y := x0, y0
	for iter := 0; iter < maxIter; iter++ {
		// Вычисляем значения функций
		F := sys.F(x, y)
		G := sys.G(x, y)

		// Численно вычисляем якобиан (или аналитически, если известен)
		h := 1e-6
		J11 := (sys.F(x+h, y) - sys.F(x, y)) / h
		J12 := (sys.F(x, y+h) - sys.F(x, y)) / h
		J21 := (sys.G(x+h, y) - sys.G(x, y)) / h
		J22 := (sys.G(x, y+h) - sys.G(x, y)) / h

		det := J11*J22 - J12*J21
		if math.Abs(det) < eps {
			fmt.Println("Детерминант Якобиана близок к нулю.")
			return x, y, iter
		}

		deltaX := (-J22*F + J12*G) / det
		deltaY := (J21*F - J11*G) / det

		x += deltaX
		y += deltaY

		if math.Abs(deltaX) < eps && math.Abs(deltaY) < eps {
			return x, y, iter + 1
		}
	}
	return x, y, maxIter
}
