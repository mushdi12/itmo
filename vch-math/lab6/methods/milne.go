package methods

func Milne(f ODEFunc, rk4 Solution, h float64) Solution {
	xs := append([]float64(nil), rk4.X[:4]...)
	ys := append([]float64(nil), rk4.Y[:4]...)

	for i := 3; i < len(rk4.X)-1; i++ {
		xp := xs[i] + h
		fp := ys[i-3] + (4*h/3)*(2*f(xs[i-2], ys[i-2])-f(xs[i-1], ys[i-1])+2*f(xs[i], ys[i]))
		fc := ys[i-1] + (h/3)*(f(xs[i-1], ys[i-1])+4*f(xs[i], ys[i])+f(xp, fp))
		xs = append(xs, xp)
		ys = append(ys, fc)
	}
	return Solution{X: xs, Y: ys}
}
