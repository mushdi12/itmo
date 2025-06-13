package methods

import (
	"errors"
	"lab5/utils"
)

func Bessel(xs, ys []float64, x float64) (float64, error) {
	n := len(xs)
	if n%2 == 0 {
		return 0, errors.New("метод Бесселя требует нечётного количества узлов")
	}
	mid := n / 2
	h := xs[1] - xs[0]
	t := (x - (xs[mid]+xs[mid-1])/2) / h

	diffTable := utils.BuildFiniteDiffs(ys)

	sum := (diffTable[mid-1][0] + diffTable[mid][0]) / 2
	p := 1.0
	fact := 1.0
	var k int
	for i := 1; i < n; i++ {
		if i%2 == 1 {
			k = (i + 1) / 2
			p *= t - 0.5
		} else {
			k = i / 2
			p *= t*t - float64(k*k)
		}
		fact *= float64(i)
		idx := mid - (i / 2) - 1
		if idx < 0 || idx >= len(diffTable) {
			break
		}
		sum += p * diffTable[idx][i] / fact
	}

	return sum, nil
}
