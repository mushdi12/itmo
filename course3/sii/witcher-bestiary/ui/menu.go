package ui

import (
	"fmt"
	"time"
)

func ShowMenu() {
	fmt.Println(Green + "\nВы можете спрашивать в свободной форме, но обязаны использовать следующие макеты:" + Reset)
	time.Sleep(150 * time.Millisecond)

	fmt.Println(Blue + "  🐉 Монстры:" + Reset)
	fmt.Println("    • расскажи про монстра <monster>")
	fmt.Println("    • охотиться на <monster>") 
	fmt.Println("    • слабости у <monster>?")
	fmt.Println("    • сильные стороны у <monster>?")
	fmt.Println("    • какие знаки помогут против <monster>?")
	fmt.Println("    • какие зелья помогут против <monster>?")
	fmt.Println("    • список всех монстров")
	time.Sleep(150 * time.Millisecond)

	fmt.Println(Blue + "\n  🔮 Ведьмачьи знаки:" + Reset)
	fmt.Println("    • расскажи про знак <name>")
	fmt.Println("    • выведи список всех знаков")
	time.Sleep(150 * time.Millisecond)

	fmt.Println(Blue + "\n  ⚗️ Ведьмачьи зелья:" + Reset)
	fmt.Println("    • расскажи про зелье <name>")
	fmt.Println("    • выведи список всех зелий")
	time.Sleep(150 * time.Millisecond)

	fmt.Println(Yellow + "\nВведите 'выход' для завершения." + Reset)
	time.Sleep(200 * time.Millisecond)
	fmt.Println(Blue + "───────────────────────────────────────────────────────────────" + Reset)
}
