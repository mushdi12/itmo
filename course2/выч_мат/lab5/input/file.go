package input

import (
	"bufio"
	"errors"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func FromFile() (float64, []float64, []float64, error) {
	var xs, ys []float64

	reader := bufio.NewReader(os.Stdin)
	fmt.Print("Введите имя файла: ")
	filename, _ := reader.ReadString('\n')
	filename = strings.TrimSpace(filename)

	file, err := os.Open(filename)
	if err != nil {
		return 0, nil, nil, err
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)

	if !scanner.Scan() {
		return 0, nil, nil, errors.New("файл пуст")
	}
	x, err := strconv.ParseFloat(scanner.Text(), 64)
	if err != nil {
		return 0, nil, nil, err
	}

	for scanner.Scan() {
		line := strings.Fields(scanner.Text())
		if len(line) == 2 {
			xi, _ := strconv.ParseFloat(line[0], 64)
			yi, _ := strconv.ParseFloat(line[1], 64)
			xs = append(xs, xi)
			ys = append(ys, yi)
		}
	}

	return x, xs, ys, nil
}
