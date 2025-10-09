package utils

import "strings"

func FormatList(raw string) string {
	raw = strings.TrimSpace(raw)
	raw = strings.Trim(raw, "[]")
	raw = strings.ReplaceAll(raw, "_", " ")
	raw = strings.ReplaceAll(raw, ",", ", ")
	raw = strings.ReplaceAll(raw, "  ", " ")
	return raw
}
