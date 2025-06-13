package methods

func Lagrange(xs, ys []float64, x float64) float64 {
	n := len(xs)
	result := 0.0

	for i := 0; i < n; i++ {
		li := 1.0
		for j := 0; j < n; j++ {
			if j != i {
				li *= (x - xs[j]) / (xs[i] - xs[j])
			}
		}
		result += li * ys[i]
	}
	return result
}
