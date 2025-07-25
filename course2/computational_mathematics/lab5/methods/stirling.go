package methods

import (
	"errors"
	"lab5/utils"
)

func Stirling(xs, ys []float64, x float64) (float64, error) {
	n := len(xs)
	if n%2 == 0 {
		return 0, errors.New("метод Стирлинга требует нечётного количества узлов")
	}
	mid := n / 2
	h := xs[1] - xs[0]
	t := (x - xs[mid]) / h
	diffTable := utils.BuildFiniteDiffs(ys)

	sum := diffTable[mid][0]
	p := 1.0
	fact := 1.0
	for i := 1; i < n; i++ {
		if i%2 == 0 {
			k := i / 2
			p *= (t*t - float64(k*k))
		} else {
			p *= t
		}
		fact *= float64(i)
		idx := mid - (i / 2)
		sum += p * diffTable[idx][i] / fact
	}

	return sum, nil
}
