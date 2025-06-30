package main

import "math"

func bisection(f func(float64) float64, a, b, epsilon float64) (float64, int) {
	var x float64
	iterations := 0
	for math.Abs(b-a) > epsilon {
		x = (a + b) / 2
		if f(a)*f(x) < 0 {
			b = x
		} else {
			a = x
		}
		iterations++
	}
	return x, iterations
}

func newton(f, df func(float64) float64, x0, epsilon float64) (float64, int) {
	x := x0
	iterations := 0
	for {
		fx := f(x)
		if math.Abs(fx) < epsilon {
			break
		}
		x = x - fx/df(x)
		iterations++
	}
	return x, iterations
}

func simpleIteration(phi func(float64) float64, x0, epsilon float64, maxIterations int) (float64, int) {
	x := x0
	for iterations := 0; iterations < maxIterations; iterations++ {
		nextX := phi(x)

		if math.Abs(nextX-x) < epsilon {
			return nextX, iterations + 1
		}

		x = nextX
	}

	return 0, maxIterations
}

func checkIterationConvergence(phi func(float64) float64, a, b float64) bool {
	const (
		step          = 1e-4 // Шаг для численного дифференцирования
		points        = 100  // Количество проверяемых точек
		maxDerivative = 1    // Максимальное значение производной
	)

	if a >= b {
		return false
	}

	// Проверяем условие Липшица (|φ'(x)| < 1) на интервале
	h := (b - a) / points
	for x := a; x <= b; x += h {
		derivative := (phi(x+step) - phi(x)) / step

		if math.Abs(derivative) >= maxDerivative {
			return false
		}
	}

	return true
}
