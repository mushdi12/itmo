    .data

input_addr:      .word  0x80                 ; Адрес, откуда читаем число (n)
output_addr:     .word  0x84                 ; Адрес, куда запишем результат
stack_addr:      .word  0xffc                ; Адрес вершины стека (указывает на конец памяти)

    .text
    .org 0x100                               ; Программа начинается с 100 адреса, чтобы не перетереть input_addr и stack_addr

_start:
    lui      sp, %hi(stack_addr)             ; Загружаем верхние 20 бит stack_addr // sp - это регистр, который указывает на вершину стека
    addi     sp, sp, %lo(stack_addr)         ; Добавляем нижние 12 бит stack_addr
    lw       sp, 0(sp)                       ; Загружаем значение в указатель на стек

    lui      t0, %hi(input_addr)             ; Загружаем верхние 20 бит input_addr в t0
    addi     t0, t0, %lo(input_addr)         ; Добавляем верхние 12 бит input_addr в t0

    lw       t0, 0(t0)                       ; t0 = значение по адресу 0x80 (адрес числа n)
    lw       a0, 0(t0)                       ; a0 = само число n (считываем его)

    jal      ra, func_count_divisors         ; Вызываем функцию подсчёта делителей
    jal      ra, func_end                    ; Завершаем программу

func_validate_n:
    mv       t5, a0                          ; сохраняем n в регистр t5
    mv       a0, ra                          ; сохраняем адресс возврата на стеке
    jal      ra, func_push
    mv       a0, t5                          ; возвращаем n обратно в в a0

    bgt      a0, zero, validate_n_correct    ; возвращаемся в исходную  // TODO: ИЗМЕНИТЬ НАЗВАНИЕ ФУНКЦИИ

    mv       a0, zero                        ; иначе мы обнуляем регистры
    addi     a0, zero, 1                     ; и делааем записываем -1 в a0 и выходим
    sub      a0, zero, a0

    jal      ra, func_end                    ; переходим в func_end

validate_n_correct:
    jal      ra, func_pop                    ; возвращаем адресс возврата и возвращаемся в исходную функцию
    mv       ra, a0                          ; берем этот адрес из регистра a0 который получили в func_pop
    jr       ra                              ; возвращаемся в func_count_divisors

func_count_divisors:
    mv       t5, a0                          ; сохраняем n в регистр t5
    mv       a0, ra                          ; сохраняем ra (адрес возврата) в регистр a0
    jal      ra, func_push                   ; Кладём ra на стек
    mv       a0, t5                          ; Восстанавливаем n в a0 (мб убрать)

    jal      ra, func_validate_n             ; идем проверять, что n > 0
    mv       t2, zero                        ; инициализируем i n
    mv       t4, zero                        ; инициализируем (0) cnt

count_divisors_while:
    addi     t2, t2, 1                       ; i++
    bgtu     t2, t5, count_divisors_while_end; если i > n то выйдем из цикла //TODO: МОЖНО ПОПРОБОВВТЬ ОПТИМИЗИРОВАТЬ ДО КОРНЯ ИЗ N
    rem      t3, t5, t2                      ; в t3 = n%i
    bnez     t3, count_divisors_while        ; если t3 != 0, то заново в цикл
    addi     t4, t4, 1                       ; если t3 == 0; то cnt++
    j        count_divisors_while            ; повторяем цикл


count_divisors_while_end:
    jal      ra, func_pop                   ; востанавливаем адрес возврата из стека
    mv       ra, a0                         ; возвращаемся по адресу
    mv       a0, t4                         ; записываем резульат
    jr       ra

func_push:
    sw       a0, 0(sp)                      ; Сохраняем a0 по адресу sp
    addi     sp, sp, -4                     ; Уменьшаем sp (стек растёт вниз)
    jr       ra                             ; Возврат


func_pop:
    addi     sp, sp, 4                      ; увеличивает стек на 4
    lw       a0, 0(sp)
    jr       ra


func_end:
    lui      t0, %hi(output_addr)           ; записываем адрес в который нужно записать вывести результат
    addi     t0, t0, %lo(output_addr)
    lw       t0, 0(t0)
    sw       a0, 0(t0)                      ; Записываем результат по адресу 0x84
    halt
