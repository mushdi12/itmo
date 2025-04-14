package main

import "math"

func calculateRSquared(y, yPred []float64) float64 {
	n := len(y)
	if n != len(yPred) {
		return 0
	}

	var sumY float64
	for _, yi := range y {
		sumY += yi
	}
	meanY := sumY / float64(n)

	var ssTotal, ssResidual float64
	for i := 0; i < n; i++ {
		ssTotal += (y[i] - meanY) * (y[i] - meanY)
		ssResidual += (y[i] - yPred[i]) * (y[i] - yPred[i])
	}

	if ssTotal == 0 {
		return 1
	}

	return 1 - ssResidual/ssTotal
}

func pearsonCorrelation(x, y []float64) float64 {
	n := len(x)
	if n != len(y) || n == 0 {
		return 0
	}

	var sumX, sumY, sumXY, sumX2, sumY2 float64
	for i := 0; i < n; i++ {
		sumX += x[i]
		sumY += y[i]
		sumXY += x[i] * y[i]
		sumX2 += x[i] * x[i]
		sumY2 += y[i] * y[i]
	}

	numerator := float64(n)*sumXY - sumX*sumY
	denominator := math.Sqrt((float64(n)*sumX2 - sumX*sumX) * (float64(n)*sumY2 - sumY*sumY))

	if denominator == 0 {
		return 0
	}

	return numerator / denominator
}

func findBestApproximation(results []ApproximationResult) ApproximationResult {
	var best ApproximationResult
	minRMSE := math.MaxFloat64

	for _, res := range results {
		if res.CanDraw && res.RMSE < minRMSE {
			minRMSE = res.RMSE
			best = res
		}
	}

	return best
}
