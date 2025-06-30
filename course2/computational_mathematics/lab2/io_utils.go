package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
)

func readInt() int {
	var n int
	_, _ = fmt.Scan(&n)
	return n
}

func readFloat() float64 {
	var f float64
	_, _ = fmt.Scan(&f)
	return f
}

func readString() string {
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	return scanner.Text()
}

func printResults(root float64, iterations int, eq Equation) {
	fmt.Println("\nРезультаты:")
	fmt.Printf("Корень уравнения: %.6f\n", root)
	fmt.Printf("Значение функции в корне: %.6f\n", eq.F(root))
	fmt.Printf("Число итераций: %d\n", iterations)
}

func selectSolutionMethod() int {
	for {
		fmt.Println("Выберите метод решения:")
		fmt.Println("1) Метод половинного деления")
		fmt.Println("2) Метод Ньютона")
		fmt.Println("3) Метод простой итерации")
		fmt.Print("Введите номер метода: ")

		methodChoice := readInt()
		if methodChoice >= 1 && methodChoice <= 3 {
			return methodChoice
		}
		fmt.Println("Неверный выбор. Попробуйте снова.")
	}
}

func solveEquation(eq Equation, a, b, epsilon float64, methodChoice int) (float64, int) {

	if a >= b {
		fmt.Println("Ошибка: a должно быть меньше b.")
		os.Exit(1)
	}
	if epsilon <= 0 {
		fmt.Println("Ошибка: epsilon должен быть положительным.")
		os.Exit(1)
	}

	if eq.F(a)*eq.F(b) > 0 {
		fmt.Println("На интервале нет корня или их несколько.")
		os.Exit(1)
	}

	switch methodChoice {
	case 1:
		return bisection(eq.F, a, b, epsilon)
	case 2:
		x0 := (a + b) / 2
		return newton(eq.F, eq.Df, x0, epsilon)
	case 3:
		if !checkIterationConvergence(eq.Phi, a, b) {
			fmt.Println("Условие сходимости метода простой итерации не выполнено.")
			os.Exit(1)
		}
		x0 := b
		return simpleIteration(eq.Phi, x0, epsilon, 100)
	default:
		fmt.Println("Неверный выбор метода.")
		os.Exit(1)
		return 0, 0
	}
}

func selectEquation() Equation {
	for {
		fmt.Println("Выберите уравнение:")
		for i, eq := range equations {
			fmt.Printf("%d) %s\n", i+1, eq.Name)
		}

		fmt.Print("Введите номер уравнения: ")
		choice := readInt()
		if choice >= 1 && choice <= len(equations) {
			return equations[choice-1]
		}
		fmt.Println("Неверный выбор. Попробуйте снова.")
	}
}

func selectInputMethod() (a, b, epsilon float64) {
	for {
		fmt.Print("Выберите способ ввода данных:\n1) С клавиатуры\n2) Из файла\nВведите номер способа: ")
		inputChoice := readInt()

		if inputChoice == 1 {
			return readFromConsole()
		} else if inputChoice == 2 {
			return readFromFile()
		}
		fmt.Println("Неверный выбор. Попробуйте снова.")
	}
}

func readFromConsole() (a, b, epsilon float64) {
	for {
		fmt.Print("Введите левую границу интервала (a): ")
		a = readFloat()
		fmt.Print("Введите правую границу интервала (b): ")
		b = readFloat()
		fmt.Print("Введите точность (epsilon): ")
		epsilon = readFloat()

		if a < b && epsilon > 0 {
			break
		}
		fmt.Println("Ошибка: убедитесь, что a < b и epsilon > 0. Попробуйте снова.")
	}
	return
}

func readFromFile() (a, b, epsilon float64) {
	for {
		fmt.Print("Введите имя файла: ")
		filename := readString()
		file, err := os.Open(filename)
		if err != nil {
			fmt.Println("Ошибка открытия файла:", err, "Попробуйте снова.")
			continue
		}
		defer file.Close()

		scanner := bufio.NewScanner(file)
		validInput := true

		if scanner.Scan() {
			a, err = strconv.ParseFloat(scanner.Text(), 64)
			if err != nil {
				validInput = false
			}
		}
		if scanner.Scan() {
			b, err = strconv.ParseFloat(scanner.Text(), 64)
			if err != nil {
				validInput = false
			}
		}
		if scanner.Scan() {
			epsilon, err = strconv.ParseFloat(scanner.Text(), 64)
			if err != nil {
				validInput = false
			}
		}

		if validInput && a < b && epsilon > 0 {
			break
		}
		fmt.Println("Ошибка: убедитесь, что a < b и epsilon > 0. Попробуйте снова.")
	}
	return
}
