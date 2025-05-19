#!/bin/bash

# Создание базы данных
psql -U postgres -c "DROP DATABASE IF EXISTS weblab4;"
psql -U postgres -c "CREATE DATABASE weblab4;"

# Применение схемы
psql -U postgres -d weblab4 -f src/main/resources/schema.sql 