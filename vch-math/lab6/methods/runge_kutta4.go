package methods

func RungeKutta4(f ODEFunc, x0, y0, xn, h float64) Solution {
	xs, ys := []float64{x0}, []float64{y0}
	for x0 < xn {
		k1 := h * f(x0, y0)
		k2 := h * f(x0+h/2, y0+k1/2)
		k3 := h * f(x0+h/2, y0+k2/2)
		k4 := h * f(x0+h, y0+k3)
		y0 += (k1 + 2*k2 + 2*k3 + k4) / 6
		x0 += h
		xs = append(xs, x0)
		ys = append(ys, y0)
	}
	return Solution{X: xs, Y: ys}
}
