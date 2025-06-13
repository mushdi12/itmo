import numpy as np
import matplotlib.pyplot as plt


def generate_set(condition_func, x_range=(-10, 10), y_range=(-10, 10), resolution=1000):
    """Генерация множества на основе заданного условия."""
    x = np.linspace(x_range[0], x_range[1], resolution)
    y = np.linspace(y_range[0], y_range[1], resolution)
    X, Y = np.meshgrid(x, y)
    Z = X + 1j * Y


    mask = condition_func(Z)
    return X[mask], Y[mask]


# Функция для построения множества
def plot_domain(x, y, title, color='blue'):
    """Функция для построения множества с заполнением области."""
    plt.figure(figsize=(6, 6))
    plt.fill(x, y, color=color, alpha=0.5)  # Заполнение области
    plt.axhline(0, color='black', linewidth=0.5)
    plt.axvline(0, color='black', linewidth=0.5)
    plt.grid(color='lightgray', linestyle='--', linewidth=0.5)
    plt.xlim(-10, 10)  # Фиксируем масштаб по оси x
    plt.ylim(-10, 10)  # Фиксируем масштаб по оси y
    plt.title(title)
    plt.gca().set_aspect('equal', adjustable='box')
    plt.show()

# Условие для множества 1: x + y <= -1
def condition_1(z):
    return np.real(z) + np.imag(z) <= -1

# Условие для множества 2: y > 0, x любой
def condition_2(z):
    return np.imag(z) > 0

# Условие для множества 3: y > 1
def condition_3(z):
    return np.imag(z) > 1

# Условие для множества 4: точка вне круга с центром в (1, 0) и радиусом 1
def condition_4(z):
    x = np.real(z)
    y = np.imag(z)
    return ((x - 1) ** 2 + y ** 2 > 1) & (y > 0)

def plot_domain_with_filled_circle(x, y, title, circle_center, circle_radius, color='blue', circle_color='white'):
    plt.figure(figsize=(6, 6))
    plt.fill(x, y, color=color, alpha=0.5)
    circle = plt.Circle(circle_center, circle_radius, color=circle_color, alpha=1)
    plt.gca().add_artist(circle)
    plt.axhline(0, color='black', linewidth=0.5)
    plt.axvline(0, color='black', linewidth=0.5)
    plt.grid(color='lightgray', linestyle='--', linewidth=0.5)
    plt.xlim(-10, 10)
    plt.ylim(-10, 10)
    plt.title(title)
    plt.gca().set_aspect('equal', adjustable='box')
    plt.show()

# Генерация множества для всех условий
x1, y1 = generate_set(condition_1)
x2, y2 = generate_set(condition_2)
x3, y3 = generate_set(condition_3)
x4, y4 = generate_set(condition_4)

# Построение и отображение первой картинки: x + y <= -1
plot_domain(x1, y1, "Изначальное множество A", color='blue')

# Построение и отображение третьей картинки: y > 1
plot_domain(x3, y3, "1 преобразование", color='blue')

# Построение и отображение второй картинки: y > 0, x любой
plot_domain(x2, y2, "2 преобразование", color='blue')

plot_domain_with_filled_circle(x4, y4,"3 Преобразование",circle_center=(1, 0),circle_radius=1,color='blue',circle_color='white'
)
