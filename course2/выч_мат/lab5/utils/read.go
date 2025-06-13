package utils

import (
	"encoding/csv"
	"errors"
	"os"
	"strconv"
)

func ReadCSV(filename string) ([]float64, []float64, error) {
	file, err := os.Open(filename)
	if err != nil {
		return nil, nil, err
	}
	defer file.Close()

	reader := csv.NewReader(file)
	records, err := reader.ReadAll()
	if err != nil {
		return nil, nil, err
	}

	var xs, ys []float64
	for _, record := range records {
		if len(record) < 2 {
			return nil, nil, errors.New("каждая строка должна содержать минимум 2 значения")
		}
		x, err1 := strconv.ParseFloat(record[0], 64)
		y, err2 := strconv.ParseFloat(record[1], 64)
		if err1 != nil || err2 != nil {
			return nil, nil, errors.New("ошибка парсинга значений")
		}
		xs = append(xs, x)
		ys = append(ys, y)
	}
	return xs, ys, nil
}
