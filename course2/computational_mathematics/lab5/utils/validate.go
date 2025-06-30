package utils

func BuildFiniteDiffs(ys []float64) [][]float64 {
	n := len(ys)
	diff := make([][]float64, n)
	for i := range diff {
		diff[i] = make([]float64, n)
	}

	for i := 0; i < n; i++ {
		diff[i][0] = ys[i]
	}

	for j := 1; j < n; j++ {
		for i := 0; i < n-j; i++ {
			diff[i][j] = diff[i+1][j-1] - diff[i][j-1]
		}
	}
	return diff
}
