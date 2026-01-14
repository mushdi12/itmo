package handlers

import (
	"fmt"
	"strings"
)

import (
	. "witcher-bestiary/ui"
	. "witcher-bestiary/utils"
	. "witcher-bestiary/prolog"
	
)

func showAllMonsters() {
	query := "findall(M, monster(M), L), write(L)."
	output, err := RunPrologQuery(query)
	if err != nil {
		fmt.Println("Ошибка при получении списка монстров:", err)
		return
	}
	fmt.Println("\nСписок всех монстров:")
	fmt.Println(FormatList(output))
}

func showHuntAdvice(monster string) {
	query := fmt.Sprintf("hunt_advice(%s, 100, Strong, Weak, Potions, Signs), write(Strong), write(' | '), write(Weak), write(' | '), write(Potions), write(' | '), write(Signs).", monster)
	output, err := RunPrologQuery(query)
	if err != nil {
		fmt.Printf("Ошибка при получении информации о %s: %v\n", monster, err)
		return
	}
	if output == "" {
		fmt.Printf("Нет данных о %s.\n", monster)
		return
	}


	parts := strings.Split(output, "|")
	if len(parts) != 4 {
		fmt.Println(output)
		return
	}

	strong := FormatList(parts[0])
	weak := FormatList(parts[1])
	potions := FormatList(parts[2])
	signs := FormatList(parts[3])

	fmt.Println(Blue + "═══════════════════════════════════════════" + Reset)
	fmt.Printf("ОХОТА НА %s\n", strings.ToUpper(monster))
	fmt.Println(Blue + "───────────────────────────────────────────" + Reset)
	fmt.Printf("Сильные стороны : %s\n", strong)
	fmt.Printf("Слабости        : %s\n", weak)
	fmt.Printf("Рекомендуемые зелья : %s\n", potions)
	fmt.Printf("Рекомендуемые знаки : %s\n", signs)
	fmt.Println(Blue + "═══════════════════════════════════════════" + Reset)
}


func showWeaknesses(monster string) {
	query := fmt.Sprintf("monster_traits(%s, _, Weak), write(Weak).", monster)
	output, _ := RunPrologQuery(query)
	fmt.Printf("\nСлабости %s: %s\n", monster, FormatList(output))
}

func showStrengths(monster string) {
	query := fmt.Sprintf("monster_traits(%s, Strong, _), write(Strong).", monster)
	output, _ := RunPrologQuery(query)
	fmt.Printf("\nСильные стороны %s: %s\n", monster, FormatList(output))
}

func showSigns(monster string) {
	query := fmt.Sprintf("recommend_signs(%s, Signs), write(Signs).", monster)
	output, _ := RunPrologQuery(query)
	fmt.Printf("\nРекомендуемые знаки против %s: %s\n", monster, FormatList(output))
}

func showPotions(monster string) {
	query := fmt.Sprintf("recommend_potions(%s, Potions), write(Potions).", monster)
	output, _ := RunPrologQuery(query)
	fmt.Printf("\nРекомендуемые зелья против %s: %s\n", monster, FormatList(output))
}

func showFullInfo(monster string) {
	query := fmt.Sprintf("monster_traits(%s, Strong, Weak), write(Strong), write(' | '), write(Weak).", monster)
	output, _ := RunPrologQuery(query)
	fmt.Printf("\nИнформация о существе: %s\n", strings.Title(monster))
	printMonsterInfo(monster, output)
}

func showPotionInfo(potion string) {
	query := fmt.Sprintf(`potion(%s, Effect, Bonus, Toxicity), potion_description(%s, Desc),
		write(Effect), write(' | '), write(Bonus), write(' | '), write(Toxicity), write(' | '), write(Desc).`, potion, potion)
	output, err := RunPrologQuery(query)
	if err != nil {
		fmt.Printf("Ошибка при получении информации о зелье %s: %v\n", potion, err)
		return
	}
	if output == "" {
		fmt.Printf("Нет данных о зелье %s.\n", potion)
		return
	}

	parts := strings.Split(output, "|")
	fmt.Printf("\nИнформация о зелье: %s\n", strings.Title(potion))
	fmt.Println(Blue + "──────────────────────────────────────────────" + Reset)
	fmt.Printf("  Эффект       : %s\n", strings.TrimSpace(parts[0]))
	fmt.Printf("  Бонус        : %s%%\n", strings.TrimSpace(parts[1]))
	fmt.Printf("  Интоксикация : %s\n", strings.TrimSpace(parts[2]))
	fmt.Printf("  Описание     : %s\n", strings.TrimSpace(parts[3]))
	fmt.Println(Blue + "──────────────────────────────────────────────" + Reset)
}

func showSignInfo(sign string) {
	query := fmt.Sprintf(`sign(%s, Effect, Bonus), sign_description(%s, Desc),
		write(Effect), write(' | '), write(Bonus), write(' | '), write(Desc).`, sign, sign)
	output, err := RunPrologQuery(query)
	if err != nil {
		fmt.Printf("Ошибка при получении информации о знаке %s: %v\n", sign, err)
		return
	}
	if output == "" {
		fmt.Printf("Нет данных о знаке %s.\n", sign)
		return
	}

	parts := strings.Split(output, "|")
	fmt.Printf("\nИнформация о знаке: %s\n", strings.Title(sign))
	fmt.Println(Blue + "──────────────────────────────────────────────" + Reset)
	fmt.Printf("  Эффект : %s\n", strings.TrimSpace(parts[0]))
	fmt.Printf("  Бонус  : %s%%\n", strings.TrimSpace(parts[1]))
	fmt.Printf("  Описание: %s\n", strings.TrimSpace(parts[2]))
	fmt.Println(Blue + "──────────────────────────────────────────────" + Reset)
}

func printMonsterInfo(monster, output string) {
	if output == "" {
		fmt.Printf("Нет данных о %s.\n", monster)
		return
	}
	parts := strings.Split(output, "|")
	if len(parts) >= 2 {
		fmt.Println(Blue + "──────────────────────────────────────────────" + Reset)
		fmt.Printf("  Сильные стороны : %s\n", FormatList(parts[0]))
		fmt.Printf("  Слабости        : %s\n", FormatList(parts[1]))
		fmt.Println(Blue + "──────────────────────────────────────────────" + Reset)
	} else {
		fmt.Println(output)
	}
}

func showAllSigns() {
	query := "findall(S, sign(S, _, _), L), write(L)."
	output, err := RunPrologQuery(query)
	if err != nil {
		fmt.Println("Ошибка при получении списка знаков:", err)
		return
	}
	fmt.Println("\nСписок всех знаков:")
	fmt.Println(FormatList(output))
}


func showAllPotions() {
	query := "findall(P, potion(P, _, _, _), L), write(L)."
	output, err := RunPrologQuery(query)
	if err != nil {
		fmt.Println("Ошибка при получении списка зелий:", err)
		return
	}
	fmt.Println("\nСписок всех зелий:")
	fmt.Println(FormatList(output))
}
