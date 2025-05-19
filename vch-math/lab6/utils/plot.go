package utils

import (
	"gonum.org/v1/plot"
	"gonum.org/v1/plot/plotter"
	"gonum.org/v1/plot/plotutil"
	"gonum.org/v1/plot/vg"
)

func PlotSolutions(data map[string][]Point, filename string) error {
	p := plot.New()
	p.Title.Text = "Сравнение решений"
	p.X.Label.Text = "x"
	p.Y.Label.Text = "y"

	lines := []interface{}{}
	for name, pts := range data {
		line := make(plotter.XYs, len(pts))
		for i, pt := range pts {
			line[i].X = pt.X
			line[i].Y = pt.Y
		}
		lines = append(lines, name, line)
	}

	if err := plotutil.AddLines(p, lines...); err != nil {
		return err
	}
	return p.Save(6*vg.Inch, 4*vg.Inch, filename)
}
