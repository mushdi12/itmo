    .data
input_addr:      .word  0x80
output_addr:     .word  0x84
default_zero_cont: .word  32
const_1:         .word  1
cur_val:         .word  0
counter:         .word  0

    .text
_start:
    load_ind     input_addr
    beqz         trailing_zero               ; if acc == 0 then go trailing_zero

trailing_loop:
    store        cur_val
    and          const_1
    bnez         trailing_end                ; if acc != 0 then go trailing_end
    load         counter
    add          const_1
    store        counter
    load         cur_val
    shiftr       const_1                     ; shift right by number from address
    jmp          trailing_loop

trailing_end:
    load         counter
    store_ind    output_addr
    halt

trailing_zero:
    load         default_zero_cont
    store_ind    output_addr
    halt         .data





