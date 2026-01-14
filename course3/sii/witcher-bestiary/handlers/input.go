package handlers

import (
	"fmt"
	"regexp"
	"strings"
)

func DetectIntent(input string) (intent, entity string) {
	replacer := strings.NewReplacer("?", "", ".", "", ",", "")
	input = replacer.Replace(input)

	if match := regexp.MustCompile(`расскажи про монстра ([а-яa-z]+)`).FindStringSubmatch(input); len(match) > 1 {
		return "monster_info", match[1]
	}
	if match := regexp.MustCompile(`охотиться на ([а-яa-z]+)`).FindStringSubmatch(input); len(match) > 1 {
		return "hunt", match[1]
	}
	if match := regexp.MustCompile(`какие знаки.*(против|для) ([а-яa-z]+)`).FindStringSubmatch(input); len(match) > 2 {
		return "signs", match[2]
	}
	if match := regexp.MustCompile(`какие зелья.*(против|для) ([а-яa-z]+)`).FindStringSubmatch(input); len(match) > 2 {
		return "potions", match[2]
	}
	if match := regexp.MustCompile(`слабости у ([а-яa-z]+)`).FindStringSubmatch(input); len(match) > 1 {
		return "weaknesses", match[1]
	}
	if match := regexp.MustCompile(`сильные стороны у ([а-яa-z]+)`).FindStringSubmatch(input); len(match) > 1 {
		return "strengths", match[1]
	}
	if strings.Contains(input, "список всех монстров") {
		return "list_monsters", ""
	}
	if strings.Contains(input, "список всех знаков") {
		return "list_signs", ""
	}
	if strings.Contains(input, "список всех зел") {
		return "list_potions", ""
	}
	if match := regexp.MustCompile(`расскажи про знак ([а-яa-z]+)`).FindStringSubmatch(input); len(match) > 1 {
		return "sign_info", match[1]
	}
	if match := regexp.MustCompile(`расскажи про зелье ([а-яa-z]+)`).FindStringSubmatch(input); len(match) > 1 {
		return "potion_info", match[1]
	}
	return "unknown", ""
}

func HandleInput(input string) {
	intent, entity := DetectIntent(input)
	switch intent {
	case "monster_info":
		showFullInfo(entity)
	case "hunt":
		showHuntAdvice(entity)
	case "signs":
		showSigns(entity)
	case "potions":
		showPotions(entity)
	case "weaknesses":
		showWeaknesses(entity)
	case "strengths":
		showStrengths(entity)
	case "list_monsters":
		showAllMonsters()
	case "list_signs":
		showAllSigns()
	case "list_potions":
		showAllPotions()
	case "sign_info":
		showSignInfo(entity)
	case "potion_info":
		showPotionInfo(entity)
	default:
		fmt.Println("Команда не распознана. Попробуйте иначе.")
	}
}
