package ui

import (
	"fmt"
	"math/rand"
	"time"
	"strings"
	"witcher-bestiary/utils"
)


var phrases = []string{
	"Пахнет утопцами...",
	"Опять эта сырость... чудовища выползут на охоту.",
	"Ласточка бы не помешала...",
	"Чувствую магию... слишком сильную, чтобы быть природной.",
	"Серебро пора наточить.",
	"Кажется, кто-то шевелится в тумане.",
	"Надо было остаться в Каэр Морхене...",
	"Следы свежие. Очень свежие.",
	"Чёрт, опять гули.",
	"Пахнет кровью и гнилью — весёлый денёк намечается.",
	"Знаки плохо слушаются... буря близко.",
	"В такие ночи даже ведьмаки предпочитают не выходить наружу.",
	"Главное — не забыть масло против реликтов.",
}

func ShowIntroAnimation() {
	utils.ClearScreen()
	fmt.Println(Blue + "Загрузка ведьмачьих свитков..." + Reset)
	time.Sleep(400 * time.Millisecond)

	barLength := 26
	for i := 0; i <= barLength; i++ {
		progress := strings.Repeat("▓", i) + strings.Repeat("░", barLength-i)
		fmt.Printf("\r[%s] %3d%%", progress, i*100/barLength)
		time.Sleep(50 * time.Millisecond)
	}
	fmt.Println()
	time.Sleep(400 * time.Millisecond)

	utils.ClearScreen()
	fmt.Println(Blue + "───────────────────────────────────────────────────────────────" + Reset)
	fmt.Println(Green + "         ⚔️   Б Е С Т И А Р И Й   В Е Д Ь М А К А   ⚔️" + Reset)
	fmt.Println(Blue + "───────────────────────────────────────────────────────────────" + Reset)
	time.Sleep(500 * time.Millisecond)

	rand.Seed(time.Now().UnixNano())
	fmt.Println(Italic + Gray + "“" + phrases[rand.Intn(len(phrases))] + "”" + Reset)
	time.Sleep(1200 * time.Millisecond)
	fmt.Println(Blue + "───────────────────────────────────────────────────────────────" + Reset)
	time.Sleep(400 * time.Millisecond)
}
