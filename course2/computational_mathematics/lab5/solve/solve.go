package solve

import (
	"errors"
	"lab5/methods"
)

type Method string

const (
	Lagrange Method = "lagrange"
	Newton   Method = "newton"
	Gauss    Method = "gauss"
	Stirling Method = "stirling"
	Bessel   Method = "bessel"
)

func Solve(xs, ys []float64, x float64, method Method) (float64, error) {
	switch method {
	case Lagrange:
		return methods.Lagrange(xs, ys, x), nil
	case Newton:
		return methods.Newton(xs, ys, x), nil
	case Gauss:
		return methods.Gauss(xs, ys, x)
	case Stirling:
		return methods.Stirling(xs, ys, x)
	case Bessel:
		return methods.Bessel(xs, ys, x)
	default:
		return 0, errors.New("неизвестный метод")
	}
}
