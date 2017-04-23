.include "m2560def.inc"
//Macros
//LCD Macros
.org 0x3FFF
.macro do_lcd_command
	ldi macrotemp, @0
	rcall lcd_command
	rcall lcd_wait
.endmacro

.macro do_lcd_data_r
	mov macrotemp, @0
	rcall lcd_data
	rcall lcd_wait
.endmacro

.macro do_lcd_data_d
	ldi macrotemp, @0
	rcall lcd_data
	rcall lcd_wait
.endmacro

.macro lcd_set
	sbi PORTA, @0
.endmacro

.macro lcd_clr
	cbi PORTA, @0
.endmacro

//Other Macros
.macro clear_byte
	ldi YL, low (@0)
	ldi YH, high(@0)
	clr macrotemp
	st Y, macrotemp
.endmacro

.macro clear_word
	ldi YL, low (@0)
	ldi YH, high(@0)
	clr macrotemp
	st Y+, macrotemp
	st Y, macrotemp
.endmacro

.macro increment_byte
	ldi YL, low(@0)
	ldi YH, high(@0)
	ld macrotemp, Y
	inc macrotemp
	st Y, macrotemp
.endmacro

.macro decrement_byte
	ldi YL, low(@0)
	ldi YH, high(@0)
	ld macrotemp, Y
	dec macrotemp
	st Y, macrotemp
.endmacro

.macro set_byte_immediate
	ldi YL, low (@0)
	ldi YH, high(@0)
	ldi macrotemp, @1
	st Y, macrotemp
.endmacro

.macro set_byte_register
	ldi YL, low (@0)
	ldi YH, high(@0)
	mov macrotemp, @1
	st Y, macrotemp
.endmacro

.macro compare_byte
	ldi YL, low (@0)
	ldi YH, high(@0)
	ld macrotemp, Y
	cpi macrotemp, @1
.endmacro

//LCD Handling
lcd_command:
	out PORTF, macrotemp
	rcall sleep_1ms
	lcd_set LCD_E
	rcall sleep_1ms
	lcd_clr LCD_E
	rcall sleep_1ms
	ret

lcd_data:
	out PORTF, macrotemp
	lcd_set LCD_RS
	rcall sleep_1ms
	lcd_set LCD_E
	rcall sleep_1ms
	lcd_clr LCD_E
	rcall sleep_1ms
	lcd_clr LCD_RS
	ret

lcd_wait:
	push macrotemp
	clr macrotemp
	out DDRF, macrotemp
	out PORTF, macrotemp
	lcd_set LCD_RW

lcd_wait_loop:
	rcall sleep_1ms
	lcd_set LCD_E
	rcall sleep_1ms
	in macrotemp, PINF
	lcd_clr LCD_E
	sbrc macrotemp, 7
	rjmp lcd_wait_loop
	lcd_clr LCD_RW
	ser macrotemp
	out DDRF, macrotemp
	pop macrotemp
	ret

sleep_1ms:
	push r24
	push r25
	ldi r25, high(DELAY_1MS)
	ldi r24, low(DELAY_1MS)

delayloop_1ms:
	sbiw r25:r24, 1
	brne delayloop_1ms
	pop r25
	pop r24
	ret

sleep_5ms:
	rcall sleep_1ms
	rcall sleep_1ms
	rcall sleep_1ms
	rcall sleep_1ms
	rcall sleep_1ms
	ret

