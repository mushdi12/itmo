package input

import (
	"errors"
	"sort"
)

func Validate(xs []float64) error {
	if !sort.Float64sAreSorted(xs) {
		return errors.New("X должны быть отсортированы по возрастанию")
	}

	seen := make(map[float64]bool)
	for _, x := range xs {
		if seen[x] {
			return errors.New("Узлы интерполяции не должны повторяться")
		}
		seen[x] = true
	}

	return nil
}
