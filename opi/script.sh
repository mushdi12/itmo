#!/bin/bash

auth_red() {
    git config --local user.name "red"
    git config --local user.email "red@yandex.ru"
}

auth_blue() {
    git config --local user.name "blue"
    git config --local user.email "blue@yandex.ru"
}

copy() {
    cp -r ../commits/commit$1/* .
}

commit() {
    copy $1
    git add .
    git commit -am "r$1"
}


rm -rf opi2
mkdir opi2
cd opi2
git init


auth_red
git checkout -b main

commit 0

git checkout -b red

commit 1

git checkout main
commit 2

git checkout red
commit 3

git checkout main
commit 4
commit 5

git checkout red
commit 6
commit 7
commit 8
commit 9

auth_blue
git checkout -b blue
commit 10

auth_red
git checkout red
commit 11

auth_blue
git checkout blue
git merge red --no-commit
commit 12

auth_red
git checkout main
git merge blue --no-commit
commit 13
commit 14

git log --all --graph --decorate --oneline
