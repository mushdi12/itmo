package utils

import (
	"log"
	"os"
	"path/filepath"

	"gonum.org/v1/plot"
	"gonum.org/v1/plot/plotter"
	"gonum.org/v1/plot/plotutil"
	"gonum.org/v1/plot/vg"
)

func PlotGraphs(xs, ys []float64, interpFunc func(float64) float64, filename string) {
	dir := filepath.Dir(filename)
	if err := os.MkdirAll(dir, os.ModePerm); err != nil {
		log.Fatalf("Не удалось создать папку %s: %v", dir, err)
	}

	p := plot.New()

	p.Title.Text = "Интерполяция"
	p.X.Label.Text = "x"
	p.Y.Label.Text = "y"

	pts := make(plotter.XYs, len(xs))
	for i := range xs {
		pts[i].X = xs[i]
		pts[i].Y = ys[i]
	}

	err := plotutil.AddScatters(p, "Узлы", pts)
	if err != nil {
		log.Fatalf("Ошибка при добавлении точек: %v", err)
	}

	line := plotter.NewFunction(interpFunc)
	line.Color = plotutil.Color(1)
	p.Add(line)
	p.Legend.Add("Интерполяционный многочлен", line)

	if err := p.Save(6*vg.Inch, 4*vg.Inch, filename); err != nil {
		log.Fatalf("Не удалось сохранить график: %v", err)
	}
}
