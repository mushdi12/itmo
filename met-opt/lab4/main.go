package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"net/url"
	"strings"
	"time"
)

type Event struct {
	ID        string    `json:"id"`
	Type      string    `json:"type"`
	Actor     Actor     `json:"actor"`
	CreatedAt time.Time `json:"created_at"`
	Repo      Repo      `json:"repo"`
}

type Actor struct {
	Login string `json:"login"`
}

type Repo struct {
	Name string `json:"name"`
}

func main() {
	repoURL := "https://github.com/mushdi12/Tg-LinkTracker"

	owner, repo, err := extractOwnerRepo(repoURL)
	if err != nil {
		panic(err)
	}

	targetRepo := fmt.Sprintf("%s/%s", owner, repo)
	apiURL := fmt.Sprintf("https://api.github.com/repos/%s/events", targetRepo)
	client := &http.Client{Timeout: 10 * time.Second}

	var lastEventTime time.Time

	for {
		req, err := http.NewRequest("GET", apiURL, nil)
		if err != nil {
			fmt.Println("Ошибка запроса:", err)
			time.Sleep(5 * time.Second)
			continue
		}
		req.Header.Set("User-Agent", "GoBot/1.0")

		resp, err := client.Do(req)
		if err != nil {
			fmt.Println("Ошибка ответа:", err)
			time.Sleep(5 * time.Second)
			continue
		}

		var events []Event
		if err := json.NewDecoder(resp.Body).Decode(&events); err != nil {
			fmt.Println("Ошибка декодирования:", err)
			resp.Body.Close()
			time.Sleep(5 * time.Second)
			continue
		}
		resp.Body.Close()

		if len(events) == 0 {
			fmt.Println("Нет событий в репозитории.")
			time.Sleep(5 * time.Second)
			continue
		}

		for _, event := range events {
			if event.Repo.Name != targetRepo {
				continue // Событие не из нашего репозитория
			}

			// Только новые события
			if event.CreatedAt.After(lastEventTime) {
				lastEventTime = event.CreatedAt

				fmt.Println("-------------------------------------")
				fmt.Println("Репозиторий:", event.Repo.Name)
				fmt.Println("Событие:", event.Type)
				fmt.Println("Автор:", event.Actor.Login)
				fmt.Println("Дата:", event.CreatedAt)
				fmt.Println("-------------------------------------")
			}
		}

		time.Sleep(5 * time.Second) // Пауза между проверками
	}
}

func extractOwnerRepo(repoURL string) (owner, repo string, err error) {
	parsedURL, err := url.Parse(repoURL)
	if err != nil {
		return "", "", err
	}

	parts := strings.Split(strings.Trim(parsedURL.Path, "/"), "/")
	if len(parts) < 2 {
		return "", "", fmt.Errorf("некорректная ссылка на репозиторий")
	}

	owner = parts[0]
	repo = parts[1]
	return owner, repo, nil
}
