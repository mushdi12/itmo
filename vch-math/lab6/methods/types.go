package methods

type ODEFunc func(x, y float64) float64

type Solution struct {
	X []float64
	Y []float64
}
