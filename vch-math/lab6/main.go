package main

import (
	"bufio"
	"fmt"
	"lab6/methods"
	"lab6/utils"
	"log"
	"math"
	"os"
	"strconv"
	"strings"
)

func main() {
	reader := bufio.NewReader(os.Stdin)

	// Выбор уравнения
	choice := 0
	for choice < 1 || choice > 3 {
		fmt.Println("Выберите уравнение:")
		fmt.Println("1: y' = x + y\n2: y' = x * y\n3: y' = sin(x) - y")
		fmt.Print("Введите номер (1-3): ")
		choiceStr, _ := reader.ReadString('\n')
		choiceStr = strings.TrimSpace(choiceStr)
		var err error
		choice, err = strconv.Atoi(choiceStr)
		if err != nil || choice < 1 || choice > 3 {
			fmt.Println("Ошибка: введите число от 1 до 3")
			choice = 0
		}
	}

	var f methods.ODEFunc
	var exact func(float64) float64

	switch choice {
	case 1:
		f = func(x, y float64) float64 { return x + y }
		exact = func(x float64) float64 { return -x - 1 + 2*math.Exp(x) }
	case 2:
		f = func(x, y float64) float64 { return x * y }
		exact = func(x float64) float64 { return math.Exp(x * x / 2) }
	case 3:
		f = func(x, y float64) float64 { return math.Sin(x) - y }
		exact = func(x float64) float64 {
			return (math.Exp(-x) * (math.Exp(x) - math.Cos(x) - math.Sin(x))) / 2
		}
	}

	// Ввод начальных значений
	x0 := readFloatWithRetry(reader, "Введите x0: ")
	y0 := readFloatWithRetry(reader, "Введите y0: ")

	// Ввод конечной точки
	xn := 0.0
	for {
		xn = readFloatWithRetry(reader, "Введите xn: ")
		if xn <= x0 {
			fmt.Printf("Ошибка: xn (%.2f) должен быть больше x0 (%.2f)\n", xn, x0)
		} else {
			break
		}
	}

	// Ввод шага
	h := 0.0
	for {
		h = readFloatWithRetry(reader, "Введите шаг h: ")
		if h <= 0 {
			fmt.Println("Ошибка: шаг h должен быть положительным")
		} else if h > xn-x0 {
			fmt.Println("Ошибка: шаг h слишком большой")
		} else {
			break
		}
	}

	// Вычисление решений
	euler := methods.ImprovedEuler(f, x0, y0, xn, h)
	eulerHalf := methods.ImprovedEuler(f, x0, y0, xn, h/2)
	rk4 := methods.RungeKutta4(f, x0, y0, xn, h)
	rk4Half := methods.RungeKutta4(f, x0, y0, xn, h/2)
	milne := methods.Milne(f, rk4, h)

	// Точное решение
	exactSol := utils.ComputeExactSolution(exact, x0, xn, h)

	// Оценка точности для одношаговых методов по правилу Рунге
	eulerRungeErr := utils.RungeError(euler.Y, eulerHalf.Y, 1) // Порядок метода Эйлера = 1
	rk4RungeErr := utils.RungeError(rk4.Y, rk4Half.Y, 4)       // Порядок метода РК4 = 4

	// Оценка точности для многошагового метода через точное решение
	milneExactErr := utils.MaxError(milne.Y, exact)

	fmt.Println("\nОценки точности:")
	fmt.Printf("Метод Эйлера (правило Рунге): %e\n", eulerRungeErr)
	fmt.Printf("Метод Рунге-Кутты 4 порядка (правило Рунге): %e\n", rk4RungeErr)
	fmt.Printf("Метод Милна (сравнение с точным решением): %e\n", milneExactErr)

	// Вывод максимальных ошибок по сравнению с точным решением
	eulerExactErr := utils.MaxError(euler.Y, exact)
	rk4ExactErr := utils.MaxError(rk4.Y, exact)

	fmt.Println("\nМаксимальные ошибки относительно точного решения:")
	fmt.Printf("Метод Эйлера: %e\n", eulerExactErr)
	fmt.Printf("Метод Рунге-Кутты 4 порядка: %e\n", rk4ExactErr)
	fmt.Printf("Метод Милна: %e\n", milneExactErr)

	// Построение графиков
	solutions := map[string][]utils.Point{
		"exact": utils.ToPoints(exactSol),
		"euler": utils.ToPoints(euler),
		"rk4":   utils.ToPoints(rk4),
		"milne": utils.ToPoints(milne),
	}

	if err := utils.PlotSolutions(solutions, "solution.png"); err != nil {
		log.Fatal(err)
	}

	fmt.Println("\nГрафик сохранён в solution.png")
}

func readFloatWithRetry(reader *bufio.Reader, prompt string) float64 {
	for {
		fmt.Print(prompt)
		input, _ := reader.ReadString('\n')
		input = strings.TrimSpace(input)
		value, err := strconv.ParseFloat(input, 64)
		if err != nil {
			fmt.Printf("Ошибка: '%s' не является числом. Попробуйте снова.\n", input)
			continue
		}
		return value
	}
}
