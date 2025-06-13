	.data
buffer: 		  		.byte 7, 'Hello, ________________________'  \ наша исходная строка в которую вставляем
buffer_end: 			.byte '___'
question:	   	  		.byte 19, 'What is your name?\n'            \ начальный вопрос
input_addr:       		.word 0x80      \ адресс ввода
output_addr:      		.word 0x84      \ адресс вывода
excl_mark: 				.word '!'       \ восклицательный знак в конце
limit:					.word 31        \ максимальная длина

	.text
	.org 	0x88                        \ начало программы
_start:
	@p output_addr b!                   \ принимаем адресс для вывода и записываем его в регистр <b>
	lit question a!                     \ загружаем на стек и потом на <a> вопрос для вывода
	print_text                          \ выводим

	lit buffer lit 1 +                  \ кладем указатель куда нужно записывать имя + пропускаем байт длины
	@p	buffer lit 0xFF and +           \ загружаем длину на a
	a!
	@p input_addr b!                    \ каладем указател куда нужно

read_loop:
	@b                                  \
	dup
	lit 10                              \ загружаем символ и если он "\n" то идем писать !
	xor
	if add_excl_mark

	write_char                          \ иначе записываем его в буфер
	a
	dup
	@p limit xor
	if overflow                         \ проверяем не достигли ли мы лимита по вводу имени
	a!
	read_loop ;                         \ идем заново

add_excl_mark:
	@p excl_mark            \ закидываем ! знак на стек и
	write_char

print_buf:
	@p output_addr b!            \ загружаем адрес вывода в регистр b
	lit buffer a!               \ загружаем адрес буфера в регистр a
	print_text                       \ вызываем подпрограмму печати
	done ;                            \ переход к завершению

overflow:
	lit 0xCCCC_CCCC             \ значение ошибки
	@p	output_addr a! !        \ кладём адрес вывода в `a`  ; записываем 0xCCCCCCCC в output
done:
	halt

write_char:
	@ lit 0xFFFFFF00 and +      \ берём адрес `a`
	!+                          \ очищаем младший байт (добавляется к символу, приведённому к байту)
    @p buffer                   \ складываем — итоговый адрес записи
	lit 1 +                     \ записываем символ по адресу и увеличиваем `a` на 1
    !p buffer                   \ Таким образом, длина строки поддерживается актуальной при каждой записи символа.
	;

print_text:
	@+
    lit 0xFF and            ; читаем первый символ это длина в Pascal строке

print_loop:
	@+                       ;
	lit 0xFF and            ; читаем символ
	!b                      ; выводим
	lit -1 +                ; делаем длина -1
	dup
	if again                ; проверяем на 0, если не то заново
	print_loop ;

again:
	;
