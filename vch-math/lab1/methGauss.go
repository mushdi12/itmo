package main

import (
	"bufio"
	"fmt"
	"log"
	"math"
	"os"
	"strings"

	"gonum.org/v1/gonum/mat"
)

// метод Гаусса Основан на приведении матрицы системы к треугольному виду так,
// чтобы ниже ее главной диагонали находились только нулевые элементы -> по известному находим решение

var (
	in = bufio.NewReader(os.Stdin)
)

func inputParameters() string {
	fmt.Print("Введите метод ввода данных: \nКлавиатура (1), Файл (2) : ")
	input, _ := in.ReadString('\n')
	input = strings.TrimSpace(input)
	return input
}

func fileInput() ([][]float64, []float64) {
	var path string
	fmt.Scan(&path)

	file, err := os.Open(path)
	if err != nil {
		log.Fatal("Ошибка открытия файла:", err)
	}

	defer file.Close()

	var n int
	_, err = fmt.Fscanf(file, "%d", &n)
	if err != nil {
		log.Fatal("Ошибка чтения n:", err)
	}

	A := make([][]float64, n)
	B := make([]float64, n)
	for i := 0; i < n; i++ {
		A[i] = make([]float64, n)
		for j := 0; j < n; j++ {
			_, err = fmt.Fscanf(file, "%f", &A[i][j])
			if err != nil {
				log.Fatal("Ошибка чтения элемента A:", err)
			}
		}
	}

	for i := 0; i < n; i++ {
		_, err = fmt.Fscanf(file, "%f", &B[i])
		if err != nil {
			log.Fatal("Ошибка чтения B:", err)
		}
	}

	fmt.Println("Матрица A:")
	for i := 0; i < n; i++ {
		fmt.Println(A[i])
	}
	fmt.Println("Вектор B:", B)

	return A, B
}

func consoleInput() ([][]float64, []float64) {
	var n int
	fmt.Print("Размерность матрицы (n <= 20): ")
	fmt.Scan(&n)
	A := make([][]float64, n)
	B := make([]float64, n)
	fmt.Println("Введите коэффициенты матрицы A:")
	for i := 0; i < n; i++ {
		A[i] = make([]float64, n)
		for j := 0; j < n; j++ {
			fmt.Scan(&A[i][j])
		}

	}

	fmt.Println("Введите столбец B:")
	for i := 0; i < n; i++ {
		fmt.Scan(&B[i])
	}

	return A, B
}

func Gauss(A [][]float64, b []float64) ([]float64, float64, [][]float64, []float64) {
	n := len(A)
	det := 1.0
	// Прямой ход
	for i := 0; i < n; i++ {
		// Поиск максимального элемента в столбце для выбора главного элемента
		maxRow := i
		for k := i + 1; k < n; k++ {
			if math.Abs(A[k][i]) > math.Abs(A[maxRow][i]) {
				maxRow = k
			}
		}

		// Обмен строк
		A[i], A[maxRow] = A[maxRow], A[i]
		b[i], b[maxRow] = b[maxRow], b[i]

		// Нормализация ведущего элемента
		det *= A[i][i]
		for k := i + 1; k < n; k++ {
			factor := A[k][i] / A[i][i]
			b[k] -= factor * b[i]
			for j := i; j < n; j++ {
				A[k][j] -= factor * A[i][j]
			}
		}
	}

	// Обратный ход
	x := make([]float64, n)
	for i := n - 1; i >= 0; i-- {
		sum := b[i]
		for j := i + 1; j < n; j++ {
			sum -= A[i][j] * x[j]
		}
		x[i] = sum / A[i][i]
	}

	return x, det, A, b
}

func controller(mode string) ([][]float64, []float64) {
	var (
		kf [][]float64
		v  []float64
	)
	switch mode {
	case "1":
		kf, v = consoleInput()
	case "2":
		fmt.Print("Введите путь к файлу: ")
		kf, v = fileInput()
	default:
		log.Fatal("Нет такого способа ввода!")
	}

	return kf, v

}

func myOutput(x []float64, det float64, triangMatrix [][]float64, modifiedB []float64, A [][]float64, b []float64) {
	fmt.Println()
	fmt.Println("--------------- Вывод ----------------")
	fmt.Println("Треугольная матрица :")
	for i := 0; i < len(triangMatrix); i++ {
		fmt.Print("| ")
		for j := 0; j < len(triangMatrix[i]); j++ {
			fmt.Printf("%.2f | ", triangMatrix[i][j])
		}
		fmt.Printf("%.2f |\n", modifiedB[i])
	}
	r := make([]float64, len(b))
	for i := range A {
		sum := 0.0
		for j := range A[i] {
			sum += A[i][j] * x[j]
		}
		r[i] = sum - b[i]
	}
	fmt.Println("Вектор невязок:", r)

	fmt.Println()

	fmt.Println("------- Вывод моей программы --------")

	fmt.Println("Определитель:", det)

	fmt.Println("Вектор неизвестных:", x)

	fmt.Println()
	fmt.Println("--------- Вывод библиотеки ----------")
	matA := mat.NewDense(len(A), len(A[0]), nil)
	matB := mat.NewVecDense(len(b), b)
	for i := 0; i < len(A); i++ {
		matA.SetRow(i, A[i])
	}

	// Определитель через Gonum
	detLib := mat.Det(matA)
	fmt.Println("Определитель :", detLib)

	var xLib mat.VecDense
	err := xLib.SolveVec(matA, matB)
	if err != nil {
		fmt.Println("Ошибка при решении через библиотеку:", err)
	} else {
		fmt.Println("Вектор неизвестных:", xLib.RawVector().Data)
	}

}

func main() {
	param := inputParameters()
	kf, v := controller(param)
	x, det, triangMatrix, modifiedB := Gauss(kf, v)
	myOutput(x, det, triangMatrix, modifiedB, kf, v)

}
