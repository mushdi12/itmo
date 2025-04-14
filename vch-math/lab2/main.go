package main

import (
	"fmt"
	"gonum.org/v1/plot"
	"gonum.org/v1/plot/plotter"
	"gonum.org/v1/plot/vg"
	"os"
)

func main() {
	eq := selectEquation()
	a, b, epsilon := selectInputMethod()
	methodChoice := selectSolutionMethod()

	root, iterations := solveEquation(eq, a, b, epsilon, methodChoice)
	printResults(root, iterations, eq)
	plotEquation(eq.F, a, b, root)

}

func plotEquation(eq func(float64) float64, a, b, root float64) {
	p := plot.New()
	p.Title.Text = "График функции"
	p.X.Label.Text = "X"
	p.Y.Label.Text = "Y"

	f := plotter.NewFunction(eq)
	f.Color = plotter.DefaultLineStyle.Color

	rootPoint, _ := plotter.NewScatter(plotter.XYs{{X: root, Y: eq(root)}})
	rootPoint.GlyphStyle.Color = plotter.DefaultLineStyle.Color

	p.Add(f, rootPoint)
	p.X.Min, p.X.Max = a, b
	p.Y.Min, p.Y.Max = eq(a), eq(b)

	if err := p.Save(6*vg.Inch, 6*vg.Inch, "plot.png"); err != nil {
		fmt.Println("Ошибка сохранения графика:", err)
		os.Exit(1)
	}
	fmt.Println("График сохранен в plot.png")
}
