package main

import (
	"fmt"
	"math"
	"sort"
)

const (
	eps = 0.17
)

func f(x float64) float64 {
	return math.Log(1+x*x) - math.Sin(x)
}

func quadraticApproximationMethod(x1, x2, x3, eps1, eps2, dx float64) float64 {
	for {
		// шаг 3 и 4
		if (x2-x1)*(x3-x2) < 0 {
			x2 = x1 + dx
			if f(x1) < f(x2) {
				x3 = x1 - dx
			} else {
				x3 = x1 + 2*dx
			}
		}

		// шаг 5
		f1, f2, f3 := f(x1), f(x2), f(x3) // вычисление значений функции в точках

		// шаг 6 7
		num := (x2-x1)*(x2-x1)*(f2-f3) - (x2-x3)*(x2-x3)*(f2-f1) // вычисление вершины параболы
		den := (x2-x1)*(f2-f3) - (x2-x3)*(f2-f1)
		if den == 0 {
			x1 = x2
			continue
		}
		xBar := x2 - 0.5*num/den
		fBar := f(xBar)

		// проверка условий завершения итераций
		if math.Abs(fBar-f2)/fBar < eps1 && math.Abs(xBar-x2)/xBar < eps2 {
			return xBar
		}

		// сортируем точки при случае x3 < x1 < x2
		x1, xBar, x3 = sortNum(x1, xBar, x3)

		// шаг 8
		if xBar >= x1 && xBar <= x3 {
			// Если хотя бы одно условие не выполняется, выбрать наименьшую точку и две точки по обе стороны от нее
			if f(xBar) < f2 {
				x1, x2, x3 = x1, xBar, x2
			} else {
				x1, x2, x3 = x2, xBar, x3
			}
		} else {
			// Если xBar вышел за границы, начинаем новую итерацию с x1 = xBar
			x1 = xBar
		}
	}
}
func sortNum(x1, x2, x3 float64) (float64, float64, float64) {
	arr := []float64{x1, x2, x3}
	sort.Float64s(arr)
	return arr[0], arr[1], arr[2]
}

func main() {
	x := quadraticApproximationMethod(0, 0.5, 1, eps, eps, 0.01)
	fmt.Printf("Квадратичная аппроксимация: x ≈ %.6f, f(x) = %.6f\n", x, f(x))
}
