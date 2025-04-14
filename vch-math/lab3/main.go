package main

import (
	"fmt"
	"math"
)

type Integral struct {
	Name     string
	Function func(float64) float64
	Exact    float64
}

func main() {
	integrals := []Integral{
		{
			Name: "-x³ - x² - 2x + 1 на [0,2]",
			Function: func(x float64) float64 {
				return -math.Pow(x, 3) - math.Pow(x, 2) - 2*x + 1
			},
			Exact: -26.0 / 3.0,
		},
		{
			Name: "-3x³ - 5x² + 4x - 2 на [-3,-1]",
			Function: func(x float64) float64 {
				return -3*math.Pow(x, 3) - 5*math.Pow(x, 2) + 4*x - 2
			},
			Exact: -10.0 / 3.0,
		},
		{
			Name: "-x³ - x² + x + 3 на [0,2]",
			Function: func(x float64) float64 {
				return -math.Pow(x, 3) - math.Pow(x, 2) + x + 3
			},
			Exact: 4.0 / 3.0,
		},
	}

	fmt.Println("Выберите функцию для интегрирования:")
	for i, integral := range integrals {
		fmt.Printf("%d) %s\n", i+1, integral.Name)
	}
	var choice int
	fmt.Print("Ваш выбор: ")
	fmt.Scan(&choice)
	f := integrals[choice-1].Function

	var a, b, epsilon float64
	fmt.Print("Введите нижний предел интегрирования (a): ")
	fmt.Scan(&a)
	fmt.Print("Введите верхний предел интегрирования (b): ")
	fmt.Scan(&b)
	fmt.Print("Введите точность вычисления (ε): ")
	fmt.Scan(&epsilon)

	fmt.Println("Выберите метод интегрирования:")
	fmt.Println("1) Метод левых прямоугольников")
	fmt.Println("2) Метод правых прямоугольников")
	fmt.Println("3) Метод средних прямоугольников")
	fmt.Println("4) Метод трапеций")
	fmt.Println("5) Метод Симпсона")
	fmt.Print("Ваш выбор: ")
	var method int
	fmt.Scan(&method)

	var result float64
	var n int
	switch method {
	case 1:
		result, n = rectangleLeft(f, a, b, epsilon)
	case 2:
		result, n = rectangleRight(f, a, b, epsilon)
	case 3:
		result, n = rectangleMid(f, a, b, epsilon)
	case 4:
		result, n = trapezoidal(f, a, b, epsilon)
	case 5:
		result, n = simpson(f, a, b, epsilon)
	default:
		fmt.Println("Неверный выбор метода")
		return
	}

	fmt.Printf("\nРезультат интегрирования: %.6f\n", result)
	fmt.Printf("Число разбиений: %d\n", n)
	fmt.Printf("Приблизительное значение: %.6f\n", integrals[choice-1].Exact)
	fmt.Printf("Абсолютная погрешность: %.6f\n", math.Abs(result-integrals[choice-1].Exact))
	fmt.Printf("Относительная погрешность: %.6f%%\n",
		math.Abs(result-integrals[choice-1].Exact)/math.Abs(integrals[choice-1].Exact)*100)
}

func rectangleLeft(f func(float64) float64, a, b, epsilon float64) (float64, int) {
	n := 10
	var prev, result float64
	for {
		h := (b - a) / float64(n)
		sum := 0.0
		for i := 0; i < n; i++ {
			x := a + float64(i)*h
			sum += f(x)
		}
		result = sum * h

		if n > 4 && math.Abs(result-prev) < epsilon {
			break
		}
		prev = result
		n *= 2
	}
	return result, n
}

func rectangleRight(f func(float64) float64, a, b, epsilon float64) (float64, int) {
	n := 10
	var prev, result float64
	for {
		h := (b - a) / float64(n)
		sum := 0.0
		for i := 1; i <= n; i++ {
			x := a + float64(i)*h
			sum += f(x)
		}
		result = sum * h

		if n > 10 && math.Abs(result-prev) < epsilon {
			break
		}
		prev = result
		n *= 2
	}
	return result, n
}

func rectangleMid(f func(float64) float64, a, b, epsilon float64) (float64, int) {
	n := 10
	var prev, result float64
	for {
		h := (b - a) / float64(n)
		sum := 0.0
		for i := 0; i < n; i++ {
			x := a + (float64(i)+0.5)*h
			sum += f(x)
		}
		result = sum * h

		if n > 10 && math.Abs(result-prev) < epsilon {
			break
		}
		prev = result
		n *= 2
	}
	return result, n
}

func trapezoidal(f func(float64) float64, a, b, epsilon float64) (float64, int) {
	n := 10
	var prev, result float64
	for {
		h := (b - a) / float64(n)
		sum := (f(a) + f(b)) / 2
		for i := 1; i < n; i++ {
			x := a + float64(i)*h
			sum += f(x)
		}
		result = sum * h

		if n > 10 && math.Abs(result-prev) < epsilon {
			break
		}
		prev = result
		n *= 2
	}
	return result, n
}

func simpson(f func(float64) float64, a, b, epsilon float64) (float64, int) {
	n := 10
	var prev, result float64
	for {
		h := (b - a) / float64(n)
		sum := f(a) + f(b)
		for i := 1; i < n; i++ {
			x := a + float64(i)*h
			if i%2 == 0 {
				sum += 2 * f(x)
			} else {
				sum += 4 * f(x)
			}
		}
		result = sum * h / 3

		if n > 10 && math.Abs(result-prev) < epsilon {
			break
		}
		prev = result
		n *= 2
	}
	return result, n
}
