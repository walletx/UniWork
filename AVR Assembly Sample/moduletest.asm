
.include "m2560def.inc"
.include "macro.asm"


.org 0x2F11
hashpress:
	clear_byte SecondsDigit1
	clear_byte DigitsEntered 
	clear_byte SecondsDigit2
	clear_byte MinutesDigit1
	clear_byte MinutesDigit2
	do_lcd_command 0b00000001
	do_lcd_command 0b10001111
	do_lcd_data_d '-'
	do_lcd_command 0b11001111
	lds temp1, DoorStatus
	do_lcd_data_r temp1
	ret
