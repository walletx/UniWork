.include "m2560def.inc"

.def row = r16; current row number
.def col = r17; current column number
.def rmask = r18; mask for current row during scan
.def cmask = r19 ; mask for current column during scan
.def temp1 = r20
.def temp2 = r21
.def temp3 = r22
.def macrotemp = r23

//Keypad Constants
.equ PORTLDIR = 0xF0 ; PD7-4: output, PD3-0, input
.equ INITCOLMASK = 0xEF ; scan from the rightmost column,
.equ INITROWMASK = 0x01 ; scan from the top row
.equ ROWMASK = 0x0F ; for obtaining input from Port D

//LCD Constants
.equ LCD_RS = 7
.equ LCD_E = 6
.equ LCD_RW = 5
.equ LCD_BE = 4
.equ F_CPU = 16000000
.equ DELAY_1MS = F_CPU / 4 / 1000 - 4; 4 cycles per iteration - setup/call-return overhead

//Microwave Constants
.equ ENTRY = 0
.equ RUNNING = 1
.equ PAUSE = 2
.equ FINISHED = 3

//Macros
.macro do_lcd_command
	ldi r16, @0
	rcall lcd_command
	rcall lcd_wait
.endmacro

.macro do_lcd_data
	mov r16, @0
	rcall lcd_data
	rcall lcd_wait
.endmacro

.macro clear
	ldi YL, low (@0)
	ldi YH, high(@0)
	clr temp1
	st Y+, temp1
	st Y, temp1
.endmacro

.macro lcd_set
	sbi PORTA, @0
.endmacro

.macro lcd_clr
	cbi PORTA, @0
.endmacro

//Data Memory
.dseg
CurrentMode: .byte 1
MicrowaveTimer: .byte 2
MicrowaveSeconds: .byte 1
MicrowaveMinutes: .byte 1

//Program Memory
.cseg

//Interrupt Adresses
.org 0
	jmp RESET
.org OVF0addr
	jmp Timer0OVF
	jmp DEFAULT
DEFAULT:
	reti

RESET:
	//LCD Setup
	ldi temp1, low(RAMEND)
	out SPL, temp1
	ldi temp1, high(RAMEND)
	out SPH, temp1

	ser temp1
	out DDRF, temp1
	out DDRA, temp1
	clr temp1
	out PORTF, temp1
	out PORTA, temp1

	do_lcd_command 0b00111000 ; 2x5x7
	rcall sleep_5ms
	do_lcd_command 0b00111000 ; 2x5x7
	rcall sleep_1ms
	do_lcd_command 0b00111000 ; 2x5x7
	do_lcd_command 0b00111000 ; 2x5x7
	do_lcd_command 0b00001000 ; display off?
	do_lcd_command 0b00000001 ; clear display
	do_lcd_command 0b00000110 ; increment, no display shift
	do_lcd_command 0b00001110 ; Cursor on, bar, no blink

	//Keypad Setup
	ldi temp1, PORTLDIR
	sts DDRL, temp1

	//LED Setup
	ser temp1
	out DDRC, temp1

	//Microwave Setup
	clear CurrentMode

	//Timer Setup
	clear MicrowaveTimer
	clear MicrowaveSeconds
	clear MicrowaveMinutes
	ldi temp1, 0b00000000
	out TCCR0A, temp1
	ldi temp1, 0b00000010
	out TCCR0B, temp1
	ldi temp1, 1<<TOIE0 
	sts TIMSK0, temp1
	sei

//Keypad Handling
keypad:
	cli ; disable interrupts so the keyboard scan is uninterrupted
	ldi cmask, INITCOLMASK
	clr col

colloop:
	cpi col, 4
	breq keypad ; If all keys are scanned, repeat.
	sts PORTL, cmask ; Otherwise, scan a column.
	ldi temp1, 0xFF ; Slow down the scan operation. 

delay:
	dec temp1
	brne delay
	lds temp1, PINL ; Read PORTA
	andi temp1, ROWMASK ; Get the keypad output value
	cpi temp1, 0xF ; Check if any row is low
	breq nextcol ; If yes, find which row is low

waitForRelease:
	ser temp2
	sts PORTL, temp2
	sts PORTL, cmask
	ldi temp2, 0xFF

delay2:
	dec temp2
	brne delay2
	lds temp2, PINL ; Read PORTA
	andi temp2, ROWMASK ; Get the keypad output value
	cpi temp2, 0xF ; Check if any row is low
	brne waitForRelease ; If yes, find which row is low
	ldi rmask, INITROWMASK ; Initialize for row check
	clr row ;

rowloop:
	cpi row, 4
	breq nextcol ; the row scan is over.
	mov temp2, temp1
	and temp2, rmask ; check un-masked bit
	breq convert ; if bit is clear, the key is pressed
	inc row ; else move to the next row
	lsl rmask
	jmp rowloop

nextcol: ; if row scan is over
	lsl cmask
	inc col	; increase column value
	jmp colloop

convert:
	cpi col, 3
	breq letters
	cpi row, 3
	breq symbols
	mov temp1, row ; Otherwise we have a number in 1-9
	lsl temp1
	add temp1, row
	add temp1, col ; temp1 = row*3 + col
	subi temp1, -'1' ; Add the value of character ‘1’
	lds temp2, CurrentMode

	//Check Mode
	cpi temp2, ENTRY
	breq entryModeNumber
	cpi temp2, RUNNING
	cpi temp2, PAUSE
	cpi temp2, FINISHED

letters:
	ldi temp1, 'A'
	add temp1, row 
	jmp end

symbols:
	jmp end

end:
	rjmp keypad

//Entry Mode Functions
entryModeNumber:
	do_lcd_data temp1
	rjmp keypad

entryModeStar:

entryModeHash:

//Running Mode Functions
runningModeNumber:
	do_lcd_data temp1
	rjmp keypad

runningModeStar:

runningModeHash:

//Pause Mode Functions

//Finished Mode Functions

//Timer Handling
Timer0OVF:
	push r24
	push r25
	lds r24, MicrowaveTimer
	lds r25, MicrowaveTimer+1
	adiw r24, 1
	cpi r24, low(7812)
	ldi temp1, high(7812)
	cpc r25, temp1
	brne NotSecond
	clear MicrowaveTimer
	rjmp EndIF

NotSecond:
	sts MicrowaveTimer, r24 
	sts MicrowaveTimer, r25

EndIF:
	pop r25
	pop r24
	reti

//LCD Handling
lcd_command:
	out PORTF, r16
	rcall sleep_1ms
	lcd_set LCD_E
	rcall sleep_1ms
	lcd_clr LCD_E
	rcall sleep_1ms
	ret

lcd_data:
	out PORTF, r16
	lcd_set LCD_RS
	rcall sleep_1ms
	lcd_set LCD_E
	rcall sleep_1ms
	lcd_clr LCD_E
	rcall sleep_1ms
	lcd_clr LCD_RS
	ret

lcd_wait:
	push r16
	clr r16
	out DDRF, r16
	out PORTF, r16
	lcd_set LCD_RW
lcd_wait_loop:
	rcall sleep_1ms
	lcd_set LCD_E
	rcall sleep_1ms
	in r16, PINF
	lcd_clr LCD_E
	sbrc r16, 7
	rjmp lcd_wait_loop
	lcd_clr LCD_RW
	ser r16
	out DDRF, r16
	pop r16
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
