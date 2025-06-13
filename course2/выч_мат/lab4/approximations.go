package main

import (
	"gonum.org/v1/gonum/mat"
	"math"
)

type ApproximationResult struct {
	Name      string
	Function  string
	Params    []float64
	RMSE      float64 // сред.квадр. отклонение
	RSquared  float64
	PearsonR  float64
	YPred     []float64
	CanDraw   bool
	DrawError string
}

func linearApproximation(x, y []float64) ApproximationResult {
	n := len(x)
	result := ApproximationResult{
		Name:     "Линейная",
		Function: "y = a + b*x",
		CanDraw:  true,
	}

	// Вычисляем суммы
	var sumX, sumY, sumXY, sumX2 float64
	for i := 0; i < n; i++ {
		sumX += x[i]
		sumY += y[i]
		sumXY += x[i] * y[i]
		sumX2 += x[i] * x[i]
	}

	// Решаем систему уравнений
	a := (sumY*sumX2 - sumX*sumXY) / (float64(n)*sumX2 - sumX*sumX)
	b := (float64(n)*sumXY - sumX*sumY) / (float64(n)*sumX2 - sumX*sumX)

	result.Params = []float64{a, b}

	// Вычисляем предсказанные значения и среднеквадратичное отклонение
	yPred := make([]float64, n)
	var RMSE float64
	for i := 0; i < n; i++ {
		yPred[i] = a + b*x[i]
		RMSE += (y[i] - yPred[i]) * (y[i] - yPred[i])
	}
	result.RMSE = math.Sqrt(RMSE / float64(n))

	result.YPred = yPred

	// Вычисляем R-квадрат
	result.RSquared = calculateRSquared(y, yPred)

	// Вычисляем коэффициент корреляции Пирсона
	result.PearsonR = pearsonCorrelation(x, y)

	return result
}

func quadraticApproximation(x, y []float64) ApproximationResult {
	n := len(x)
	result := ApproximationResult{
		Name:     "Квадратичная",
		Function: "y = a + b*x + c*x^2",
		CanDraw:  true,
	}

	// Вычисляем суммы
	var sumX, sumY, sumXY, sumX2, sumX3, sumX4, sumX2Y float64
	for i := 0; i < n; i++ {
		sumX += x[i]
		sumY += y[i]
		sumXY += x[i] * y[i]
		x2 := x[i] * x[i]
		sumX2 += x2
		sumX3 += x2 * x[i]
		sumX4 += x2 * x2
		sumX2Y += x2 * y[i]
	}

	// Создаем матрицу коэффициентов
	A := mat.NewDense(3, 3, []float64{
		float64(n), sumX, sumX2,
		sumX, sumX2, sumX3,
		sumX2, sumX3, sumX4,
	})

	// Создаем вектор правой части
	B := mat.NewDense(3, 1, []float64{sumY, sumXY, sumX2Y})

	// Решаем систему уравнений
	var X mat.Dense
	err := X.Solve(A, B)
	if err != nil {
		result.CanDraw = false
		result.DrawError = "Не удалось решить систему уравнений"
		return result
	}

	a := X.At(0, 0)
	b := X.At(1, 0)
	c := X.At(2, 0)

	result.Params = []float64{a, b, c}

	// Вычисляем предсказанные значения и среднеквадратичное отклонение
	yPred := make([]float64, n)
	var RMSE float64
	for i := 0; i < n; i++ {
		yPred[i] = a + b*x[i] + c*x[i]*x[i]
		RMSE += (y[i] - yPred[i]) * (y[i] - yPred[i])
	}
	result.RMSE = math.Sqrt(RMSE / float64(n))

	result.YPred = yPred

	// Вычисляем R-квадрат
	result.RSquared = calculateRSquared(y, yPred)

	return result
}

func cubicApproximation(x, y []float64) ApproximationResult {
	if len(x) != len(y) || len(x) == 0 {
		return ApproximationResult{
			Name:      "Кубическая",
			Function:  "y = a + b*x + c*x^2 + d*x^3",
			CanDraw:   false,
			DrawError: "Несоответствие размеров x и y или пустые данные",
		}
	}

	n := len(x)
	result := ApproximationResult{
		Name:     "Кубическая",
		Function: "y = a + b*x + c*x^2 + d*x^3",
		CanDraw:  true,
	}

	var (
		sumX, sumY, sumXY, sumX2, sumX3, sumX4, sumX5, sumX6 float64
		sumX2Y, sumX3Y                                       float64
	)

	for i := 0; i < n; i++ {
		xi := x[i]
		xi2 := xi * xi
		xi3 := xi2 * xi
		xi4 := xi2 * xi2
		yi := y[i]

		sumX += xi
		sumY += yi
		sumXY += xi * yi
		sumX2 += xi2
		sumX3 += xi3
		sumX4 += xi4
		sumX5 += xi4 * xi
		sumX6 += xi4 * xi2
		sumX2Y += xi2 * yi
		sumX3Y += xi3 * yi
	}

	A := mat.NewDense(4, 4, []float64{
		float64(n), sumX, sumX2, sumX3,
		sumX, sumX2, sumX3, sumX4,
		sumX2, sumX3, sumX4, sumX5,
		sumX3, sumX4, sumX5, sumX6,
	})

	B := mat.NewDense(4, 1, []float64{sumY, sumXY, sumX2Y, sumX3Y})

	var coef mat.Dense
	if err := coef.Solve(A, B); err != nil {
		return ApproximationResult{
			Name:      "Кубическая",
			Function:  "y = a + b*x + c*x^2 + d*x^3",
			CanDraw:   false,
			DrawError: "Система уравнений вырождена или не имеет решения",
		}
	}

	a := coef.At(0, 0)
	b := coef.At(1, 0)
	c := coef.At(2, 0)
	d := coef.At(3, 0)

	result.Params = []float64{a, b, c, d}

	yPred := make([]float64, n)
	var se float64

	for i := 0; i < n; i++ {
		xi := x[i]
		yPred[i] = a + b*xi + c*xi*xi + d*xi*xi*xi
		se += (y[i] - yPred[i]) * (y[i] - yPred[i])
	}

	result.YPred = yPred
	result.RMSE = math.Sqrt(se / float64(n))

	result.RSquared = calculateRSquared(y, yPred)

	return result
}

func exponentialApproximation(x, y []float64) ApproximationResult {
	n := len(x)
	result := ApproximationResult{
		Name:     "Экспоненциальная",
		Function: "y = a * exp(b*x)",
		CanDraw:  true,
	}

	// Линеаризация: ln(y) = ln(a) + b*x
	lnY := make([]float64, n)
	for i := 0; i < n; i++ {
		if y[i] <= 0 {
			result.CanDraw = false
			result.DrawError = "y должен быть положительным для экспоненциальной аппроксимации"
			return result
		}
		lnY[i] = math.Log(y[i])
	}

	// Линейная аппроксимация ln(y) от x
	linResult := linearApproximation(x, lnY)
	if !linResult.CanDraw {
		result.CanDraw = false
		result.DrawError = linResult.DrawError
		return result
	}

	a := math.Exp(linResult.Params[0])
	b := linResult.Params[1]

	result.Params = []float64{a, b}

	// Вычисляем предсказанные значения и среднеквадратичное отклонение
	yPred := make([]float64, n)
	var RMSE float64
	for i := 0; i < n; i++ {
		yPred[i] = a * math.Exp(b*x[i])
		RMSE += (y[i] - yPred[i]) * (y[i] - yPred[i])
	}
	result.RMSE = math.Sqrt(RMSE / float64(n))

	result.YPred = yPred

	// Вычисляем R-квадрат
	result.RSquared = calculateRSquared(y, yPred)

	return result
}

func logarithmicApproximation(x, y []float64) ApproximationResult {
	n := len(x)
	result := ApproximationResult{
		Name:     "Логарифмическая",
		Function: "y = a + b*ln(x)",
		CanDraw:  true,
	}

	// Проверка, что все x > 0
	for i := 0; i < n; i++ {
		if x[i] <= 0 {
			result.CanDraw = false
			result.DrawError = "x должен быть положительным для логарифмической аппроксимации"
			return result
		}
	}

	// Линеаризация: y = a + b*ln(x)
	lnX := make([]float64, n)
	for i := 0; i < n; i++ {
		lnX[i] = math.Log(x[i])
	}

	// Линейная аппроксимация y от ln(x)
	linResult := linearApproximation(lnX, y)
	if !linResult.CanDraw {
		result.CanDraw = false
		result.DrawError = linResult.DrawError
		return result
	}

	a := linResult.Params[0]
	b := linResult.Params[1]

	result.Params = []float64{a, b}

	// Вычисляем предсказанные значения и среднеквадратичное отклонение
	yPred := make([]float64, n)
	var RMSE float64
	for i := 0; i < n; i++ {
		yPred[i] = a + b*math.Log(x[i])
		RMSE += (y[i] - yPred[i]) * (y[i] - yPred[i])
	}
	result.RMSE = math.Sqrt(RMSE / float64(n))

	result.YPred = yPred

	// Вычисляем R-квадрат
	result.RSquared = calculateRSquared(y, yPred)

	return result
}

func powerApproximation(x, y []float64) ApproximationResult {
	n := len(x)
	result := ApproximationResult{
		Name:     "Степенная",
		Function: "y = a * x^b",
		CanDraw:  true,
	}

	// Проверка, что все x > 0 и y > 0
	for i := 0; i < n; i++ {
		if x[i] <= 0 || y[i] <= 0 {
			result.CanDraw = false
			result.DrawError = "x и y должны быть положительными для степенной аппроксимации"
			return result
		}
	}

	// Линеаризация: ln(y) = ln(a) + b*ln(x)
	lnX := make([]float64, n)
	lnY := make([]float64, n)
	for i := 0; i < n; i++ {
		lnX[i] = math.Log(x[i])
		lnY[i] = math.Log(y[i])
	}

	// Линейная аппроксимация ln(y) от ln(x)
	linResult := linearApproximation(lnX, lnY)
	if !linResult.CanDraw {
		result.CanDraw = false
		result.DrawError = linResult.DrawError
		return result
	}

	a := math.Exp(linResult.Params[0])
	b := linResult.Params[1]

	result.Params = []float64{a, b}

	// Вычисляем предсказанные значения и среднеквадратичное отклонение
	yPred := make([]float64, n)
	var sumSquaredError float64
	for i := 0; i < n; i++ {
		yPred[i] = a * math.Pow(x[i], b)
		diff := y[i] - yPred[i]
		sumSquaredError += diff * diff
	}

	result.RMSE = math.Sqrt(sumSquaredError / float64(n))

	result.YPred = yPred

	// Вычисляем R-квадрат
	result.RSquared = calculateRSquared(y, yPred)

	return result
}
