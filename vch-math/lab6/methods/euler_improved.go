package methods

func ImprovedEuler(f ODEFunc, x0, y0, xn, h float64) Solution {
	xs, ys := []float64{x0}, []float64{y0}
	for x0 < xn {
		k1 := f(x0, y0)
		yPredict := y0 + h*k1
		k2 := f(x0+h, yPredict)
		y0 += h * (k1 + k2) / 2
		x0 += h
		xs = append(xs, x0)
		ys = append(ys, y0)
	}
	return Solution{X: xs, Y: ys}
}
