package methods

func Newton(xs, ys []float64, x float64) float64 {
	n := len(xs)
	coeff := make([]float64, n)
	copy(coeff, ys)

	for j := 1; j < n; j++ {
		for i := n - 1; i >= j; i-- {
			coeff[i] = (coeff[i] - coeff[i-1]) / (xs[i] - xs[i-j])
		}
	}

	result := coeff[0]
	mult := 1.0
	for i := 1; i < n; i++ {
		mult *= (x - xs[i-1])
		result += coeff[i] * mult
	}

	return result
}
