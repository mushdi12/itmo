package utils

import (
	"lab6/methods"
	"math"
)

type Point struct {
	X, Y float64
}

func ComputeExactSolution(f func(float64) float64, x0, xn, h float64) methods.Solution {
	xs, ys := []float64{}, []float64{}
	for x := x0; x <= xn+1e-10; x += h {
		xs = append(xs, x)
		ys = append(ys, f(x))
	}
	return methods.Solution{X: xs, Y: ys}
}

func MaxError(sol methods.Solution, exact func(float64) float64) float64 {
	maxErr := 0.0
	for i := range sol.X {
		err := math.Abs(sol.Y[i] - exact(sol.X[i]))
		if err > maxErr {
			maxErr = err
		}
	}
	return maxErr
}

func ToPoints(sol methods.Solution) []Point {
	pts := make([]Point, len(sol.X))
	for i := range sol.X {
		pts[i] = Point{sol.X[i], sol.Y[i]}
	}
	return pts
}

// RungeError вычисляет оценку погрешности по правилу Рунге
// solution1 - решение с шагом h (Y-значения)
// solution2 - решение с шагом h/2 (Y-значения)
// p - порядок метода
func RungeError(solution1, solution2 []float64, p int) float64 {
	maxError := 0.0
	// Берем только точки, которые есть в обоих решениях
	n := len(solution1)
	if len(solution2) < 2*n-1 {
		n = len(solution2)/2 + 1
	}

	for i := 0; i < n; i++ {
		// Для solution2 берем каждую вторую точку (соответствующую solution1)
		error := math.Abs(solution1[i] - solution2[2*i])
		// Масштабируем ошибку согласно правилу Рунге
		rungeError := error / (math.Pow(2, float64(p)) - 1)
		if rungeError > maxError {
			maxError = rungeError
		}
	}
	return maxError
}
