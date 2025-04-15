package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func inputData() []Point {
	fmt.Println("Выберите способ ввода данных:")
	fmt.Println("1 - Ввод из файла")
	fmt.Println("2 - Ввод вручную")
	fmt.Print("Ваш выбор: ")

	var choice int
	_, _ = fmt.Scanln(&choice)

	var points []Point

	if choice == 1 {
		fmt.Print("Введите имя файла: ")
		var filename string
		_, _ = fmt.Scanln(&filename)

		file, err := os.Open(filename)
		if err != nil {
			fmt.Printf("Ошибка открытия файла: %v\n", err)
			os.Exit(1)
		}
		defer func(file *os.File) {
			_ = file.Close()
		}(file)
		fmt.Println("Содержимое файла:")

		scanner := bufio.NewScanner(file)
		for scanner.Scan() {
			line := scanner.Text()
			fields := strings.Fields(line)
			if len(fields) != 2 {
				continue
			}

			x, err1 := strconv.ParseFloat(fields[0], 64)
			y, err2 := strconv.ParseFloat(fields[1], 64)
			if err1 != nil || err2 != nil {
				continue
			}
			fmt.Println(x, y)
			points = append(points, Point{X: x, Y: y})
		}

		if err := scanner.Err(); err != nil {
			fmt.Printf("Ошибка чтения файла: %v\n", err)
			os.Exit(1)
		}

	} else {
		fmt.Print("Введите количество точек (8-12): ")
		var n int
		_, _ = fmt.Scanln(&n)

		if n < 8 || n > 12 {
			fmt.Println("Некорректное количество точек!")
			os.Exit(1)
		}

		fmt.Println("Введите точки в формате x y:")
		scanner := bufio.NewScanner(os.Stdin)
		for i := 0; i < n; i++ {
			fmt.Printf("Точка %d: ", i+1)
			scanner.Scan()
			line := scanner.Text()
			fields := strings.Fields(line)
			if len(fields) != 2 {
				fmt.Println("Ошибка: введите два числа через пробел")
				i--
				continue
			}

			x, err1 := strconv.ParseFloat(fields[0], 64)
			y, err2 := strconv.ParseFloat(fields[1], 64)
			if err1 != nil || err2 != nil {
				fmt.Println("Ошибка: введите корректные числа")
				i--
				continue
			}

			points = append(points, Point{X: x, Y: y})
		}
	}

	return points
}

func printResults(results []ApproximationResult) {
	fmt.Println("\nРезультаты аппроксимации:")
	fmt.Println(strings.Repeat("=", 80))

	for _, res := range results {
		if !res.CanDraw {
			fmt.Printf("\n%s функция: не удалось аппроксимировать - %s\n", res.Name, res.DrawError)
			continue
		}

		fmt.Printf("\n%s функция: %s\n", res.Name, res.Function)
		fmt.Printf("Параметры: %v\n", res.Params)
		fmt.Printf("Среднеквадратичное отклонение: %.6f\n", res.RMSE)
		fmt.Printf("Коэффициент детерминации (R²): %.6f\n", res.RSquared)

		if res.Name == "Линейная" {
			fmt.Printf("Коэффициент корреляции Пирсона: %.6f\n", res.PearsonR)
		}

		// Интерпретация R²
		r2 := res.RSquared
		switch {
		case r2 >= 0.9:
			fmt.Println("Очень хорошее соответствие (R² ≥ 0.9)")
		case r2 >= 0.7:
			fmt.Println("Хорошее соответствие (0.7 ≤ R² < 0.9)")
		case r2 >= 0.5:
			fmt.Println("Умеренное соответствие (0.5 ≤ R² < 0.7)")
		default:
			fmt.Println("Слабое соответствие (R² < 0.5)")
		}
	}
}
