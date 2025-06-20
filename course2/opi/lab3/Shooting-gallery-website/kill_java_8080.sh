#!/bin/bash

# Найти PID процесса java, использующего порт 8080
PID=$(lsof -ti :8080 -sTCP:LISTEN -a -c java)

# Проверка найден ли процесс
if [ -n "$PID" ]; then
  echo "Процесс java на порту 8080 найден: PID=$PID"
  kill -9 "$PID"
  echo "Процесс убит."
else
  echo "Процесс java на порту 8080 не найден."
fi
