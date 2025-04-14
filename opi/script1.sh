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

    cp -r ../com/commit$1/* .

}

commit() {

    copy $1

    git add .

    git commit -am "r$1"

}

rm -rf opi_dop

mkdir opi_dop

cd opi_dop

git init

auth_red

git checkout -b main

commit 0

git checkout -b red

commit 1

commit 2

commit 3

