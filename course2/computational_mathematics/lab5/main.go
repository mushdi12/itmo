package main

import (
	"fmt"
	"lab5/input"
	"lab5/solve"
	"lab5/utils"
)

func main() {
	var x float64
	var xs, ys []float64
	var err error

	for {
		fmt.Println("Введите:")
		fmt.Println("/file - для ввода из файла;")
		fmt.Println("/example - для примера;")
		fmt.Println("/console - для ручного ввода;")

		var option string
		fmt.Scan(&option)

		switch option {
		case "/file":
			x, xs, ys, err = input.FromFile()
			if err != nil {
				fmt.Println("Ошибка чтения файла:", err)
				continue
			}
		case "/example":
			x, xs, ys = input.Example()
		case "/console":
			x, xs, ys = input.Manual()
		//case "fu":
		//	x, xs, ys = input.FromFunction()
		default:
			fmt.Println("Некорректный ввод. Повторите.")
			continue
		}

		if err := input.Validate(xs); err != nil {
			fmt.Println("Ошибка валидации данных:", err)
			continue
		}
		break
	}

	methods := []struct {
		name   string
		method solve.Method
	}{
		{"Ньютона", solve.Newton},
		{"Гаусса", solve.Gauss},
		{"Стирлинга", solve.Stirling},
		{"Лагранжа", solve.Lagrange},
		{"Бесселя", solve.Bessel},
	}

	for _, m := range methods {
		result, err := solve.Solve(xs, ys, x, m.method)
		if err != nil {
			fmt.Printf("Метод %s: ошибка - %v\n", m.name, err)
			continue
		}
		fmt.Printf("Метод %s: значение функции ≈ %f\n", m.name, result)

		interpFunc := func(xx float64) float64 {
			val, err := solve.Solve(xs, ys, xx, m.method)
			if err != nil {
				return 0
			}
			return val
		}

		filename := fmt.Sprintf("graphs/interpolation_%s.png", m.name)
		utils.PlotGraphs(xs, ys, interpFunc, filename)
		fmt.Println("График сохранен в", filename)
	}
}

//150
//3
//100 10
//121 11
//144 12
// ответ 12
