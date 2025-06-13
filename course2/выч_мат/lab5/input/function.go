package input

import (
	"fmt"
	"math"
)

func FromFunction() (float64, []float64, []float64) {
	var choice int
	var f func(float64) float64

	fmt.Println("1. 2*x^2 - 5*x")
	fmt.Println("2. x^5")
	fmt.Println("3. sin(x)")
	fmt.Println("4. sqrt(x)")
	for {
		fmt.Print("Выберите функцию [1-4]: ")
		fmt.Scan(&choice)

		switch choice {
		case 1:
			f = func(x float64) float64 { return 2*x*x - 5*x }
		case 2:
			f = func(x float64) float64 { return math.Pow(x, 5) }
		case 3:
			f = math.Sin
		case 4:
			f = math.Sqrt
		default:
			continue
		}
		break
	}

	var n int
	var x0, xn float64
	fmt.Print("Число узлов: ")
	fmt.Scan(&n)
	fmt.Print("x0: ")
	fmt.Scan(&x0)
	fmt.Print("xn: ")
	fmt.Scan(&xn)

	h := (xn - x0) / float64(n-1)
	xs := make([]float64, n)
	ys := make([]float64, n)

	for i := 0; i < n; i++ {
		xs[i] = x0 + float64(i)*h
		ys[i] = f(xs[i])
	}

	var x float64
	fmt.Print("Введите точку интерполяции: ")
	fmt.Scan(&x)

	return x, xs, ys
}
