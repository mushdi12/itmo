package input

import (
	"fmt"
)

func Manual() (float64, []float64, []float64) {
	var x float64
	var xs, ys []float64
	var n int

	fmt.Print("Введите точку интерполяции: ")
	fmt.Scan(&x)

	fmt.Print("Введите количество узлов интерполяции: ")
	fmt.Scan(&n)

	fmt.Println("Введите узлы интерполяции (формат: x y):")
	for i := 0; i < n; i++ {
		var xi, yi float64
		_, err := fmt.Scan(&xi, &yi)
		if err != nil {
			fmt.Println("Ошибка ввода. Попробуйте снова.")
			i-- // повторить ввод этого узла
			continue
		}
		xs = append(xs, xi)
		ys = append(ys, yi)
	}

	return x, xs, ys
}
