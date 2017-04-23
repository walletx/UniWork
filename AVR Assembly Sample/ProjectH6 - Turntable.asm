.include "m2560def.inc"

//Register Definitions
.def row = r16; current row number
.def col = r17; current column number
.def rmask = r18; mask for current row during scan
.def cmask = r19 ; mask for current column during scan
.def temp1 = r20
.def temp2 = r21
.def temp3 = r22
.def macrotemp = r23
.def functiontemp = r24
.def timertemp = r25

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
.equ ENTRYPOWER = 4

.equ DOOROPEN = 'O'
.equ DOORCLOSED = 'C'

.equ POWER100 = 0
.equ POWER50 = 1
.equ POWER25 = 2

.equ BACKSLASH = 164

//Macros
//LCD Macros
.macro do_lcd_command
	ldi r16, @0
	rcall lcd_command
	rcall lcd_wait
.endmacro

.macro do_lcd_data_r
	mov r16, @0
	rcall lcd_data
	rcall lcd_wait
.endmacro

.macro do_lcd_data_d
	ldi r16, @0
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

//Data Memory
.dseg
CurrentMode: .byte 1
MicrowaveTimer: .byte 2

DigitsEntered: .byte 1 
SecondsDigit1: .byte 1
SecondsDigit2: .byte 1
MinutesDigit1: .byte 1
MinutesDigit2: .byte 1

DoorStatus: .byte 1

PowerLevel: .byte 1

Turntable: .byte 1
TurntableStatus: .byte 1
TurntableTimer: .byte 2

//Program Memory
.cseg

//Interrupt Adresses
.org 0x0000
	jmp RESET
.org INT0addr
	jmp EXT_INT0
.org INT1addr
	jmp EXT_INT1
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
	do_lcd_command 0b00000100 ; decrement, no display shift
	do_lcd_command 0b00001110 ; Cursor on, no bar, no blink

	//Keypad Setup
	ldi temp1, PORTLDIR
	sts DDRL, temp1

	//LED Setup
	ser temp1
	out DDRC, temp1

	//Push Button Setup
	ser temp1
	out DDRG, temp1
	clr temp1
	out DDRD, temp1
	ser temp1
	out PORTD, temp1
	
	ldi temp1, (2 << ISC01)
	sts EICRA, temp1
	in temp1, EIMSK
	ori temp1, (1<<INT1)
	out EIMSK, temp1

	ldi temp1, (2 << ISC00)
	sts EICRA, temp1
	in temp1, EIMSK
	ori temp1, (1<<INT0)
	out EIMSK, temp1
	
	//Microwave Setup
	set_byte_immediate CurrentMode, ENTRY
	set_byte_immediate DoorStatus, DOORCLOSED
	set_byte_immediate TurnTable, '-'
	clear_byte TurnTableStatus
	increment_byte TurnTableStatus
	clear_byte DigitsEntered
	clear_byte SecondsDigit1
	clear_byte SecondsDigit2
	clear_byte MinutesDigit1
	clear_byte MinutesDigit2

	do_lcd_command 0b11001111
	lds temp1, DoorStatus
	do_lcd_data_r temp1
	do_lcd_command 0b10001111
	lds temp1, TurnTable
	do_lcd_data_r temp1

	//Timer Setup
	clear_word MicrowaveTimer
	clear_word TurntableTimer
	ldi temp1, 0b00000000
	out TCCR0A, temp1
	ldi temp1, 0b00000010
	out TCCR0B, temp1
	ldi temp1, 1<<TOIE0 
	sts TIMSK0, temp1
	cli	

//Keypad Handling
keypad:
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

pullUpReset:
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
	brne pullUpReset ; If yes, find which row is low
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
	rjmp checkMode

letters:
	ldi temp1, 'A'
	add temp1, row 
	jmp checkMode

symbols:
	cpi col, 0
	breq star
	cpi col, 1
	breq zero
	ldi temp1, '#'
	jmp checkMode

star:
	ldi temp1, '*'
	rjmp checkMode

zero:
	ldi temp1, '0'
	rjmp checkMode

checkMode:
	lds temp2, CurrentMode
	cpi temp2, ENTRY
	breq entryMode
	cpi temp2, RUNNING
	breq runningModeJump
	cpi temp2, PAUSE
	breq pauseModeJump
	cpi temp2, FINISHED
	breq finishedModeJump
	cpi temp2, ENTRYPOWER
	breq entryPowerModeJump

runningModeJump:
	jmp runningMode

pauseModeJump:
	jmp pauseMode

finishedModeJump:
	jmp finishedMode

entryPowerModeJump:
	jmp entryPowerMode

invalidKey:
	rjmp keypad

//Entry Mode Functions
entryMode:
	cpi temp1, '*'
	breq entryModeStarJump
	cpi temp1, '#'
	breq entryModeHashJump
	cpi temp1, 'A'
	breq entryModeAJump
	rjmp entryModeNumber

entryModeStarJump:
	rjmp entryModeStar

entryModeHashJump:
	rjmp entryModeHash

entryModeAJump:
	rjmp entryModeA

entryModeNumber:
	do_lcd_command 0b00000100
	increment_byte DigitsEntered
	compare_byte DigitsEntered, 5
	brsh invalidKey
	compare_byte DigitsEntered, 1
	breq entryDigit1
	compare_byte DigitsEntered, 2
	breq entryDigit2
	compare_byte DigitsEntered, 3
	breq entryDigit3Jump
	compare_byte DigitsEntered, 4
	breq entryDigit4Jump

	entryDigit3Jump:
		rjmp entryDigit3

	entryDigit4Jump:
		rjmp entryDigit4

	entryDigit1:
		do_lcd_command 0b10000100
		do_lcd_data_r temp1
		subi temp1, '0'
		set_byte_register SecondsDigit2, temp1

		do_lcd_command 0b00010000
		do_lcd_data_d ':'
		rjmp keypad

	entryDigit2:
		do_lcd_command 0b10000011
		lds temp2, SecondsDigit2
		set_byte_register SecondsDigit1, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b00010100
		do_lcd_command 0b00010100
		do_lcd_data_r temp1
		subi temp1, '0'
		set_byte_register SecondsDigit2, temp1
		rjmp keypad

	entryDigit3:
		do_lcd_command 0b10000001
		lds temp2, SecondsDigit1
		set_byte_register MinutesDigit2, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b00010100
		do_lcd_command 0b00010100
		do_lcd_command 0b00010100
		lds temp2, SecondsDigit2
		set_byte_register SecondsDigit1, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b00010100
		do_lcd_command 0b00010100
		do_lcd_data_r temp1
		subi temp1, '0'
		set_byte_register SecondsDigit2, temp1
		rjmp keypad

	entryDigit4:
		do_lcd_command 0b10000000
		lds temp2, MinutesDigit2
		set_byte_register MinutesDigit1, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b00010100
		do_lcd_command 0b00010100
		lds temp2, SecondsDigit1
		set_byte_register MinutesDigit2, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b00010100
		do_lcd_command 0b00010100
		do_lcd_command 0b00010100
		lds temp2, SecondsDigit2
		set_byte_register SecondsDigit1, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b00010100
		do_lcd_command 0b00010100
		do_lcd_data_r temp1
		subi temp1, '0'
		set_byte_register SecondsDigit2, temp1
		rjmp keypad
	
entryModeStar:
	compare_byte DigitsEntered, 0
	brne startCooking
	set_byte_immediate MinutesDigit2, 1
	set_byte_immediate SecondsDigit2, 1

	startCooking:
		set_byte_immediate CurrentMode, RUNNING
		sei
		rjmp keypad

entryModeHash:
	rjmp RESET

entryModeA:
	do_lcd_command 0b10000000
	do_lcd_command 0b00000110
	do_lcd_data_d 'S'
	do_lcd_data_d 'e'
	do_lcd_data_d 't'
	do_lcd_data_d ' '
	do_lcd_data_d 'P'
	do_lcd_data_d 'o'
	do_lcd_data_d 'w'
	do_lcd_data_d 'e'
	do_lcd_data_d 'r'
	do_lcd_command 0b11000000
	do_lcd_data_d '1'
	do_lcd_data_d '/'
	do_lcd_data_d '2'
	do_lcd_data_d '/'
	do_lcd_data_d '3'
	set_byte_immediate CurrentMode, ENTRYPOWER
	rjmp keypad

//Entry Power Mode Functions
entryPowerMode:

	cpi temp1, '1'
	breq entryPowerModeOne
	cpi temp1, '2'
	breq entryPowerModeTwo
	cpi temp1, '3'
	breq entryPowerModeThree
	cpi temp1, '#'
	breq entryPowerModeHash

	entryPowerModeOne:
		ser temp1
		out PORTC, temp1
		set_byte_immediate PowerLevel, POWER100
		rjmp keypad

	entryPowerModeTwo:
		ldi temp1, 0b00001111
		out PORTC, temp1
		set_byte_immediate PowerLevel, POWER50
		rjmp keypad

	entryPowerModeThree:
		ldi temp1, 0b00000011
		out PORTC, temp1
		set_byte_immediate PowerLevel, POWER25
		rjmp keypad

	entryPowerModeHash:
		do_lcd_command 0b00000100
		do_lcd_command 0b00000001
		lds temp3, DoorStatus
		do_lcd_command 0b11001111
		do_lcd_data_r temp3
		do_lcd_command 0b10000010
		do_lcd_data_d ':'
		set_byte_immediate CurrentMode, ENTRY

		compare_byte DigitsEntered, 1
		breq PowerDigits1
		compare_byte DigitsEntered, 2
		breq PowerDigits2
		compare_byte DigitsEntered, 3
		breq PowerDigits3
		compare_byte DigitsEntered, 4
		breq PowerDigits4
		rjmp keypad

		PowerDigits4:
			do_lcd_command 0b10000000
			lds temp1, MinutesDigit1
			subi temp1, -'0'
			do_lcd_data_r temp1
		PowerDigits3:
			do_lcd_command 0b10000001
			lds temp1, MinutesDigit2
			subi temp1, -'0'
			do_lcd_data_r temp1
		PowerDigits2:
			do_lcd_command 0b10000011
			lds temp1, SecondsDigit1
			subi temp1, -'0'
			do_lcd_data_r temp1
		PowerDigits1:
			do_lcd_command 0b10000100
			lds temp1, SecondsDigit2
			subi temp1, -'0'
			do_lcd_data_r temp1

		rjmp keypad

//Running Mode Functions
runningMode:

cpi temp1, '*'
breq runningModeStar
cpi temp1, '#'
breq runningModeHash

runningModeNumber:
	rjmp keypad

runningModeStar:
	ldi temp3, 0b10101010
	out PORTC, temp3
	increment_byte MinutesDigit2
	sei
	rjmp keypad

runningModeHash:
	set_byte_immediate CurrentMode, PAUSE
	rjmp keypad

//Pause Mode Functions
pauseMode:
	rjmp keypad

//Finished Mode Functions
finishedMode:
	rjmp keypad

//Push Button Handling

//Close Door
EXT_INT0:
	clr temp3
	out PORTG, temp3
	do_lcd_command 0b11000000
	set_byte_immediate DoorStatus, DOORCLOSED
	lds temp3, DoorStatus
	do_lcd_command 0b11001111
	do_lcd_data_r temp3
	
	//compare_byte CurrentMode, FINISHED
	//breq setEntryC
	rjmp endint0

	/*setEntryC:
		do_lcd_command 0b00000001
		set_byte_immediate CurrentMode, ENTRY*/	

	endint0:
		reti

//Open Door
EXT_INT1:
	ser temp3
	out PORTG, temp3
	do_lcd_command 0b11000000
	set_byte_immediate DoorStatus, DOOROPEN
	lds temp3, DoorStatus
	do_lcd_command 0b11001111
	do_lcd_data_r temp3
	
	//Needs to have different handling for other modes
	compare_byte CurrentMode, RUNNING
	breq setPause
	//compare_byte CurrentMode, FINISHED
	//breq setEntryO
	rjmp endint1

	/*setEntryO:
		do_lcd_command 0b00000001
		set_byte_immediate CurrentMode, ENTRY
		rjmp endint1*/

	setPause:
		set_byte_immediate CurrentMode, PAUSE

	endint1:
		lds temp3, CurrentMode
		out PORTC, temp3
		reti


//Timer Handling
Timer0OVF:
	compare_byte CurrentMode, RUNNING
	brne EndIFJump
	rjmp MicrowaveTime

EndIFJump:
	rjmp EndIF

MicrowaveTime:
	//in temp1, SREG
	//push temp1
	//push r24
	//push r25
	lds r24, MicrowaveTimer
	lds r25, MicrowaveTimer+1
	adiw r24, 1
	cpi r24, low(7812)
	ldi temp1, high(7812)
	cpc r25, temp1
	brne NotSecond
	clear_word MicrowaveTimer
	rjmp CountDown4thDigit

NotSecond:
	sts MicrowaveTimer, r24 
	sts MicrowaveTimer+1, r25
	rjmp TurntableTime
	
CountDown4thDigit:
	compare_byte SecondsDigit2, 0
	breq CountDown3rdDigit
	decrement_byte SecondsDigit2
	rjmp showTime

CountDown3rdDigit:
	set_byte_immediate SecondsDigit2, 9
	compare_byte SecondsDigit1, 0
	breq CountDown2ndDigit
	decrement_byte SecondsDigit1
	rjmp showTime

CountDown2ndDigit:
	set_byte_immediate SecondsDigit1, 5
	set_byte_immediate SecondsDigit2, 9
	compare_byte MinutesDigit2, 0
	breq CountDown1stDigit
	decrement_byte MinutesDigit2
	rjmp showTime

CountDown1stDigit:
	set_byte_immediate SecondsDigit1, 5
	set_byte_immediate SecondsDigit2, 9
	set_byte_immediate MinutesDigit2, 9
	compare_byte MinutesDigit1, 0
	breq cooked
	decrement_byte MinutesDigit1
	rjmp showTime

cooked:
	set_byte_immediate SecondsDigit1, 0
	set_byte_immediate SecondsDigit2, 0
	set_byte_immediate MinutesDigit2, 0
	set_byte_immediate CurrentMode, FINISHED

showTime:
	do_lcd_command 0b10000000
	do_lcd_command 0b00000110
	lds timertemp, MinutesDigit1
	subi timertemp, -'0'
	do_lcd_data_r timertemp
	
	lds timertemp, MinutesDigit2
	subi timertemp, -'0'
	do_lcd_data_r timertemp
	
	ldi timertemp, ':'
	do_lcd_data_r timertemp
	
	lds timertemp, SecondsDigit1
	subi timertemp, -'0'
	do_lcd_data_r timertemp
	
	lds timertemp, SecondsDigit2
	subi timertemp, -'0'
	do_lcd_data_r timertemp

	compare_byte CurrentMode, FINISHED
	breq finishedText
	rjmp TurnTableTime

finishedText:
	do_lcd_command 0b10000000
	do_lcd_command 0b00000110
	do_lcd_data_d 'D'
	do_lcd_data_d 'o'
	do_lcd_data_d 'n'
	do_lcd_data_d 'e'
	do_lcd_data_d ' '
	do_lcd_command 0b10001111
	do_lcd_data_d '-'
	do_lcd_command 0b11000000
	do_lcd_data_d 'R'
	do_lcd_data_d 'e'
	do_lcd_data_d 'm'
	do_lcd_data_d 'o'
	do_lcd_data_d 'v'
	do_lcd_data_d 'e'
	do_lcd_data_d ' '
	do_lcd_data_d 'f'
	do_lcd_data_d 'o'
	do_lcd_data_d 'o'
	do_lcd_data_d 'd'
	rjmp EndIF

TurntableTime:
	lds r24, TurntableTimer
	lds r25, TurntableTimer+1
	adiw r24, 1
	cpi r24, low(39060)
	ldi temp1, high(39060)
	cpc r25, temp1
	brne NotFiveSecond
	clear_word TurntableTimer
	rjmp TurntableTurn

NotFiveSecond:
	sts TurntableTimer, r24 
	sts TurntableTimer+1, r25
	rjmp EndIF

TurntableTurn:
	compare_byte Turntable, '-'
	breq turn10
	compare_byte Turntable, BACKSLASH
	breq turn20
	compare_byte Turntable, '|'
	breq turn30
	compare_byte Turntable, '/'
	breq turn40

	turn10:
		compare_byte TurntableStatus, 0
		breq turn11
		set_byte_immediate Turntable, BACKSLASH
		rjmp ShowTurnTable

	turn11:
		set_byte_immediate Turntable, '/'
		rjmp ShowTurnTable

	turn20:
		compare_byte TurntableStatus, 0
		breq turn21
		set_byte_immediate Turntable, '|'
		rjmp ShowTurnTable

	turn21:
		set_byte_immediate Turntable, '-'
		rjmp ShowTurnTable

	turn30:
		compare_byte TurntableStatus, 0
		breq turn31
		set_byte_immediate Turntable, '/'
		rjmp ShowTurnTable

	turn31:
		set_byte_immediate Turntable, BACKSLASH
		rjmp ShowTurnTable

	turn40:
		compare_byte TurntableStatus, 0
		breq turn41
		set_byte_immediate Turntable, '-'
		rjmp ShowTurnTable

	turn41:
		set_byte_immediate Turntable, '|'
		rjmp ShowTurnTable

ShowTurnTable:
	do_lcd_command 0b10001111
	lds temp1, TurnTable
	do_lcd_data_r temp1

EndIF:
	//pop r25
	//pop r24
	//pop temp1
	//out SREG, temp1
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
