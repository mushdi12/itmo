package prolog

import (
	"fmt"
	"os/exec"
	"strings"
)

func RunPrologQuery(query string) (string, error) {
	cmd := exec.Command("swipl", "-s", "./resource/witcher_kb.pl", "-g", query, "-t", "halt.")
	out, err := cmd.CombinedOutput()
	if err != nil {
		return "", fmt.Errorf("%v\nProlog output:\n%s", err, string(out))
	}

	lines := strings.Split(string(out), "\n")
	var clean []string
	for _, line := range lines {
		line = strings.TrimSpace(line)
		if line != "" && !strings.HasPrefix(line, "Warning:") {
			clean = append(clean, line)
		}
	}

	if len(clean) == 0 {
		return "", nil
	}
	return strings.Join(clean, "\n"), nil
}
