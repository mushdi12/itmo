package main

import (
	"fmt"
	"math/rand"
	"time"
)

var DIST = [][]int{
	{0, 4, 5, 3, 8},
	{4, 0, 7, 6, 8},
	{5, 7, 0, 7, 9},
	{3, 6, 7, 0, 9},
	{8, 8, 9, 9, 0},
}

const (
	POP_SIZE     = 4
	MUT_PROB     = 0.01
	GENERATIONS  = 2000
	COUNT_CITIES = 5
)

func routeLength(route []int) int {
	total := 0
	for i := 0; i < COUNT_CITIES; i++ {
		from := route[i]
		to := route[(i+1)%COUNT_CITIES]
		total += DIST[from][to]
	}
	return total
}

func tournament(population [][]int, k int) []int {
	best := population[rand.Intn(len(population))]
	bestLength := routeLength(best)

	for i := 1; i < k; i++ {
		candidate := population[rand.Intn(len(population))]
		length := routeLength(candidate)
		if length < bestLength {
			best = candidate
			bestLength = length
		}
	}

	return append([]int(nil), best...)
}

func crossover(p1, p2 []int) ([]int, []int) {
	size := len(p1)
	a, b := rand.Intn(size), rand.Intn(size)
	if a > b {
		a, b = b, a
	}

	child1 := make([]int, size)
	child2 := make([]int, size)
	for i := range child1 {
		child1[i], child2[i] = -1, -1
	}

	copy(child1[a:b], p2[a:b])
	copy(child2[a:b], p1[a:b])

	fillRemaining := func(child, parent []int) {
		index := a + 1
		for _, gene := range parent {
			found := false
			for _, g := range child {
				if g == gene {
					found = true
					break
				}
			}
			if !found {
				for child[index%size] != -1 {
					index++
				}
				child[index%size] = gene
			}
		}
	}

	fillRemaining(child1, p1)
	fillRemaining(child2, p2)

	return child1, child2
}

func mutate(route []int) {
	if rand.Float64() < MUT_PROB {
		i := rand.Intn(COUNT_CITIES)
		j := rand.Intn(COUNT_CITIES)
		route[i], route[j] = route[j], route[i]
	}
}

func geneticTSP() ([]int, int) {
	population := make([][]int, POP_SIZE)
	for i := 0; i < POP_SIZE; i++ {
		individual := rand.Perm(COUNT_CITIES)
		population[i] = individual
	}

	for g := 0; g < GENERATIONS; g++ {
		newPopulation := make([][]int, 0, POP_SIZE)
		elite := population[0]
		bestLen := routeLength(elite)

		for _, ind := range population {
			length := routeLength(ind)
			if length < bestLen {
				elite = ind
				bestLen = length
			}
		}
		newPopulation = append(newPopulation, append([]int(nil), elite...))

		for len(newPopulation) < POP_SIZE {
			p1 := tournament(population, 2)
			p2 := tournament(population, 2)

			child1, child2 := crossover(p1, p2)
			mutate(child1)
			mutate(child2)

			newPopulation = append(newPopulation, child1)
			if len(newPopulation) < POP_SIZE {
				newPopulation = append(newPopulation, child2)
			}
		}

		population = newPopulation
	}

	best := population[0]
	bestLen := routeLength(best)
	for _, ind := range population {
		length := routeLength(ind)
		if length < bestLen {
			best = ind
			bestLen = length
		}
	}

	return best, bestLen
}

func main() {
	rand.Seed(time.Now().UnixNano())
	bestRoute, bestLen := geneticTSP()
	fmt.Println("Лучший найденный маршрут:", bestRoute)
	fmt.Println("Его длина:", bestLen)
}
