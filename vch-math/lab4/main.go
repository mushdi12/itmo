package main

import (
	"fmt"
)

func main() {
	fmt.Println("Программа аппроксимации данных различными функциями")
	points := inputData()

	if len(points) < 8 || len(points) > 12 {
		fmt.Println("Ошибка: количество точек должно быть от 8 до 12")
		return
	}

	x := make([]float64, len(points))
	y := make([]float64, len(points))
	for i, p := range points {
		x[i] = p.X
		y[i] = p.Y
	}

	results := []ApproximationResult{
		linearApproximation(x, y),
		quadraticApproximation(x, y),
		cubicApproximation(x, y),
		exponentialApproximation(x, y),
		logarithmicApproximation(x, y),
		powerApproximation(x, y),
	}

	printResults(results)

	best := findBestApproximation(results)
	fmt.Printf("\nНаилучшая аппроксимирующая функция: %s (Среднеквадратичное отклонение = %.6f)\n", best.Name, best.RMSE)

	drawPlots(points, results)
}
