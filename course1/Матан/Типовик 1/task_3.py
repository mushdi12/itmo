
from matplotlib import pyplot as plt
# Метод трапеции
def partitioning(lower, upper, n):
    splitting = [lower + (upper - lower) * i / n for i in range(n + 1)]
    return splitting
def trapezoid(f, split):
    s = 0
    for q in range(1, len(split) - 1):
        s += (f(split[q - 1]) + f(split[q])) * (split[q] - split[q - 1]) / 2
    return s

# Метод Симпсона
def simpson(f, split):
    s = 0
    for e in range(1, len(split) - 1):
        s += (f(split[e - 1]) + 4 * f((split[e - 1] + split[e]) / 2) + f(split[e])) * (split[e] - split[e - 1]) / 6
    return s

# Метод Прямоугольника
def rectangle(f,split):
    s = 0
    for e in range(1, len(split) - 1):
        s += f(split[e - 1]) * (split[e] - split[e-1])  # Левая точка
    return s



# Нижняя граница
lower_limit = float(input())
# Верхняя граница
upper_limit = float(input())
a = 0

if upper_limit < lower_limit:
    a = upper_limit
    upper_limit = lower_limit
    lower_limit = a
    print("Во избежания отрицательной ")
elif upper_limit == lower_limit:
    print("Ответ:", 0, "\nP.S. - Интеграл равен 0 так как нижняя сумма равна верхней")
    exit(0)

function = lambda x: 1 / x ** 2
# На сколько частей
n_parts = int(input())

def mae(tex, upper):
    return abs(upper - tex)


def mse(tex, upper):
    return (upper - tex) ** 2

number_of_segments = 10
res_rectangle, res_trapezoid, res_simpson = [], [], []
for n in map(lambda x: x ** 2, range(1, number_of_segments)):
    part = partitioning(lower_limit, upper_limit, n)
    res_rectangle.append(rectangle(function, part))
    res_trapezoid.append(trapezoid(function, part))
    res_simpson.append(simpson(function, part))

x = list(map(lambda x: x ** 2, range(1, number_of_segments)))
fig, ax = plt.subplots()
gr = [(mae, " (через MAE)"), (mse, " (через MAE)")]
r = (1/2)
res = [(res_rectangle, "Метод прямоугольников"), (res_trapezoid, "Метод трапеций"), (res_simpson, "Метод Симпсона")]
for g, gname in gr:
    for k, b in res:
        ax.plot(x, [g(k[i], r) for i in range(number_of_segments - 1)], label=b+gname)
ax.legend()

plt.show()