package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"

	. "witcher-bestiary/handlers"
	"witcher-bestiary/ui"
	"witcher-bestiary/utils"
)

func main() {
	utils.ClearScreen()
	ui.ShowIntroAnimation()
	ui.ShowMenu()

	reader := bufio.NewReader(os.Stdin)

	for {
		fmt.Print(ui.Blue + "\n> " + ui.Reset)
		input, _ := reader.ReadString('\n')
		input = strings.ToLower(strings.TrimSpace(input))

		if input == "выход" || input == "exit" {
			fmt.Println("\nДо встречи на Тропе Ведьмака.")
			break
		}

		HandleInput(input)
	}
}



