package main

import "math"

type Equation struct {
	F    func(float64) float64
	Df   func(float64) float64
	Phi  func(float64) float64
	Name string
}

var equations = []Equation{
	{
		Name: "x^3 - 2x - 5 = 0", // Корень: x ≈ 2.09 (промежуток [2.0, 2.2])
		F:    func(x float64) float64 { return math.Pow(x, 3) - 2*x - 5 },
		Df:   func(x float64) float64 { return 3*math.Pow(x, 2) - 2 },
		Phi:  func(x float64) float64 { return math.Pow(2*x+5, 1.0/3.0) },
	},
	{
		Name: "e^x - 3x = 0", // Корни: x ≈ 0.619 ([0.4, 0.8]) и x ≈ 1.512 ([1.4, 1.6])
		F:    func(x float64) float64 { return math.Exp(x) - 3*x },
		Df:   func(x float64) float64 { return math.Exp(x) - 3 },
		Phi:  func(x float64) float64 { return math.Exp(x) / 3 },
	},
	{
		Name: "sin(x) + 0.5x - 1 = 0", // Корень: x ≈ 0.704 ([0.5, 1.0])
		F:    func(x float64) float64 { return math.Sin(x) + 0.5*x - 1 },
		Df:   func(x float64) float64 { return math.Cos(x) + 0.5 },
		Phi:  func(x float64) float64 { return (1 - math.Sin(x)) / 0.5 },
	},
	{
		Name: "2.74x^3 - 1.93x^2 - 15.28x - 3.72 = 0", // Корни: x ≈ -2.15 ([-2.5, -2.0]), x ≈ -0.5 ([-0.6, -0.4]), x ≈ 2.5 ([2.0, 3.0])
		F:    func(x float64) float64 { return 2.74*math.Pow(x, 3) - 1.93*math.Pow(x, 2) - 15.28*x - 3.72 },
		Df:   func(x float64) float64 { return 8.22*math.Pow(x, 2) - 3.86*x - 15.28 },
		Phi:  func(x float64) float64 { return math.Pow((1.93*math.Pow(x, 2)+15.28*x+3.72)/2.74, 1.0/3.0) },
	},
	{
		Name: "-1.38x^3 - 5.42x^2 + 2.57x + 10.95 = 0", // Корни: x ≈ -3.88 ([-4.0, -3.5]), x ≈ -1.45 ([-1.5, -1.0]), x ≈ 1.41 ([1.0, 1.5])
		F:    func(x float64) float64 { return -1.38*math.Pow(x, 3) - 5.42*math.Pow(x, 2) + 2.57*x + 10.95 },
		Df:   func(x float64) float64 { return -4.14*math.Pow(x, 2) - 10.84*x + 2.57 },
		Phi:  func(x float64) float64 { return (-10.95 + 1.38*math.Pow(x, 3) + 5.42*math.Pow(x, 2)) / 2.57 },
	},
	{
		Name: "x^3 + 2.84x^2 - 5.606x - 14.766 = 0", // Корни: x ≈ -4.1 ([-4.5, -3.5]), x ≈ -1.8 ([-2.0, -1.5]), x ≈ 2.1 ([1.5, 2.5])
		F:    func(x float64) float64 { return math.Pow(x, 3) + 2.84*math.Pow(x, 2) - 5.606*x - 14.766 },
		Df:   func(x float64) float64 { return 3*math.Pow(x, 2) + 5.68*x - 5.606 },
		Phi:  func(x float64) float64 { return math.Pow(-2.84*math.Pow(x, 2)+5.606*x+14.766, 1.0/3.0) },
	},
	{
		Name: "x^3 - 1.89x^2 - 2x + 1.76 = 0", // Корни: x ≈ -1.1 ([-1.5, -0.5]), x ≈ 0.8 ([0.5, 1.0]), x ≈ 2.2 ([2.0, 2.5])
		F:    func(x float64) float64 { return math.Pow(x, 3) - 1.89*math.Pow(x, 2) - 2*x + 1.76 },
		Df:   func(x float64) float64 { return 3*math.Pow(x, 2) - 3.78*x - 2 },
		Phi:  func(x float64) float64 { return (1.89*math.Pow(x, 2) + 2*x - 1.76) / math.Pow(x, 2) },
	},
	{
		Name: "2.3x^3 + 5.75x^2 - 7.41x - 10.6 = 0", // Корни: x ≈ -3.2 ([-3.5, -3.0]), x ≈ -1.1 ([-1.5, -0.5]), x ≈ 1.4 ([1.0, 1.5])
		F:    func(x float64) float64 { return 2.3*math.Pow(x, 3) + 5.75*math.Pow(x, 2) - 7.41*x - 10.6 },
		Df:   func(x float64) float64 { return 6.9*math.Pow(x, 2) + 11.5*x - 7.41 },
		Phi:  func(x float64) float64 { return math.Pow((-5.75*math.Pow(x, 2)+7.41*x+10.6)/2.3, 1.0/3.0) },
	},
}
