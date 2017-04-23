//Acknowledgements: Parts of code are based on code from lecture slides
//and/or those provided for use for labs

.include "m2560def.inc"

//Register Definitions
.def row = r16; current row number
.def col = r17; current column number
.def rmask = r18; mask for current row during scan
.def cmask = r19 ; mask for current column during scan
.def temp1 = r20 ; general purpose used outside
.def temp2 = r21 ; general purpose used outside
.def temp3 = r22 ; general purpose used outside
.def macrotemp = r23 ; register inside macros and functions ONLY
//Don't touch r24,r25 they are used in timer
.def timertemp = r26 ; register inside timer ONLY
.def buttontemp = r27 ; register inside push buttons ONLY

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
//Mode Indicator Constants
.equ ENTRY = 0
.equ RUNNING = 1
.equ PAUSE = 2
.equ FINISHED = 3
.equ ENTRYPOWER = 4

//Door Status Indicator Constants
.equ DOOROPEN = 'O'
.equ DOORCLOSED = 'C'

//Power Level Indicator Constants
.equ POWER100 = 0
.equ POWER50 = 1
.equ POWER25 = 2

//Backslash Substitute Character
.equ BACKSLASH = 164

//Macros
//LCD Macros
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

//Data Memory
.dseg
CurrentMode: .byte 1 ; Indicates 'Current Mode'
MicrowaveTimer: .byte 2 ; Used for Microwave timer

DigitsEntered: .byte 1 ; Number of digits entered in entry mode
SecondsDigit1: .byte 1 ; Time digit -  XT:TT
SecondsDigit2: .byte 1 ; TX:TT
MinutesDigit1: .byte 1 ; TT:XT
MinutesDigit2: .byte 1 ; TT:TX

DoorStatus: .byte 1 ; Indicates door closed or open

PowerLevel: .byte 1 ; Indicates power level

Turntable: .byte 1 ; Stores current turntable character
TurntableStatus: .byte 1 ; When 0 rotates 1 way, otherwise rotate other way
TurntableTimer: .byte 2 ; Used for the timing of turntable rotation

RunningProcessing: .byte 1 ; Used so timer does not interrupt

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
	//out PORTC, temp1 ; LED used for debugging for now
	; will turn this on in final version to show power level

	//Push Button Setup
	ser temp1
	out DDRG, temp1 ; Top LED Setup
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
	set_byte_immediate PowerLevel, POWER100
	clear_byte TurnTableStatus
	clear_byte DigitsEntered
	clear_byte SecondsDigit1
	clear_byte SecondsDigit2
	clear_byte MinutesDigit1
	clear_byte MinutesDigit2
	clear_byte RunningProcessing

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
	sei

//Keypad Handling
keypad:
	compare_byte DoorStatus, DOORCLOSED ; checks if door closed
	brne keypad ; if not then do not allow keypad enter
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
	lds temp1, PINL ; Read PORTL
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
	lds temp2, PINL ; Read PORTL
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
	cpi col, 2 ; might make it more stable I really dunno
	breq hash ; as opposed to just loading it in
	rjmp invalidKey

star:
	ldi temp1, '*'
	rjmp checkMode

zero:
	ldi temp1, '0'
	rjmp checkMode

hash:
	ldi temp1, '#'
	rjmp checkMode

checkMode: ; check which mode currently in and branch
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

runningModeJump: ; jump handling, branch too long
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
entryMode: ; check which button was pressed
	cpi temp1, '*'
	breq entryModeStarJump
	cpi temp1, '#'
	breq entryModeHashJump
	cpi temp1, 'A'
	breq entryModeAJump
	subi temp1, '0'
	cpi temp1, 10 ; check if one of the number keys
	brlo entryModeNumber
	rjmp keypad ; else undefined

entryModeStarJump:
	rjmp entryModeStar

entryModeHashJump:
	rjmp entryModeHash

entryModeAJump:
	rjmp entryModeA

entryModeNumber:
	subi temp1, -'0'
	do_lcd_command 0b00000100 ; sets LCD entry mode
	increment_byte DigitsEntered
	compare_byte DigitsEntered, 5 ; if 4 digits have been entered
	brsh invalidKey ; do not process 5th digit
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

	entryDigit1: ; Basically moves digits from addresses appropriately
		do_lcd_command 0b10000100 ; and displays them in correct place
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

		do_lcd_command 0b10000100
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

		do_lcd_command 0b10000011
		lds temp2, SecondsDigit2
		set_byte_register SecondsDigit1, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b10000100
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

		do_lcd_command 0b10000001
		lds temp2, SecondsDigit1
		set_byte_register MinutesDigit2, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b10000011
		lds temp2, SecondsDigit2
		set_byte_register SecondsDigit1, temp2
		subi temp2, -'0'
		do_lcd_data_r temp2

		do_lcd_command 0b10000100
		do_lcd_data_r temp1
		subi temp1, '0'
		set_byte_register SecondsDigit2, temp1
		rjmp keypad
	
entryModeStar:
	compare_byte DigitsEntered, 0 ; If no number is entered
	brne startCooking
	set_byte_immediate MinutesDigit2, 1 ; default 1 minute

	startCooking:
		set_byte_immediate CurrentMode, RUNNING ; Start running timer
		rjmp keypad

entryModeHash: ; Clear variables and return to entry mode
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
	rjmp keypad

entryModeA: ; Power select mode
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
	rjmp keypad

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

	entryPowerModeHash: ; return to entry mode ; restore previously entered time
		do_lcd_command 0b00000100 
		do_lcd_command 0b00000001
		lds temp1, DoorStatus
		do_lcd_command 0b11001111
		do_lcd_data_r temp1
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
set_byte_immediate RunningProcessing, 1
cpi temp1, '*'
breq runningModeStar
cpi temp1, '#'
breq runningModeHashJump
cpi temp1, 'C'
breq runningModeCJump
cpi temp1, 'D'
breq runningModeDJump
clear_byte RunningProcessing
rjmp keypad

runningModeHashJump:
	rjmp runningModeHash

runningModeCJump:
	rjmp runningModeC

runningModeDJump:
	rjmp runningModeD

runningModeStar: ; Adds 1 minute with special case handling
	rcall correctTimeFormat
	compare_byte MinutesDigit2, 9
	breq runningStar1
	increment_byte MinutesDigit2
	rjmp runningStarFinish

	runningStar1:
		compare_byte MinutesDigit1, 9
		breq runningStar2
		set_byte_immediate MinutesDigit2, 0
		increment_byte MinutesDigit1
		rjmp runningStarFinish

	runningStar2:
		compare_byte SecondsDigit1, 4
		brge runningStar3
		lds temp1, SecondsDigit1
		subi temp1, -6
		sts SecondsDigit1, temp1
		rjmp runningStarFinish
		
	runningStar3:
		set_byte_immediate SecondsDigit1, 9
		set_byte_immediate SecondsDigit2, 9

	runningStarFinish:
		clear_byte RunningProcessing
		rjmp keypad

runningModeHash: 
	ldi temp1, 0b10101010 ; I put this in for debugging
	out PORTC, temp1 ; It sometimes enter this branch for no reason
	set_byte_immediate CurrentMode, PAUSE
	clear_byte RunningProcessing
	rjmp keypad

runningModeC: ; Add 30 second with special case handling
	rcall correctTimeFormat
	compare_byte SecondsDigit1, 3
	brge runningC1
	lds temp1, SecondsDigit1
	subi temp1, -3
	sts SecondsDigit1, temp1
	rjmp runningCFinish

	runningC1:
		compare_byte MinutesDigit2, 9
		breq runningC2
		increment_byte MinutesDigit2
		lds temp1, SecondsDigit1
		subi temp1, 3
		sts SecondsDigit1, temp1
		rjmp runningCFinish

	runningC2:
		compare_byte MinutesDigit1, 9
		breq runningC3
		increment_byte MinutesDigit1
		clear_byte MinutesDigit2
		lds temp1, SecondsDigit1
		subi temp1, 3
		sts SecondsDigit1, temp1
		rjmp runningCFinish

	runningC3:
		compare_byte SecondsDigit1, 7
		brge runningC4
		lds temp1, SecondsDigit1
		subi temp1, -3
		sts SecondsDigit1, temp1
		rjmp runningCFinish

	runningC4:
		set_byte_immediate SecondsDigit1, 9
		set_byte_immediate SecondsDigit2, 9
		rjmp runningCFinish

	runningCFinish:
		clear_byte RunningProcessing
		rjmp keypad

runningModeD: ; Add 30 second with special case handling
	compare_byte SecondsDigit1, 3
	brlt runningD1
	lds temp1, SecondsDigit1
	subi temp1, 3
	sts SecondsDigit1, temp1
	rjmp runningDFinish

	runningD1:
		compare_byte MinutesDigit2, 0
		breq runningD2
		decrement_byte MinutesDigit2
		lds	temp1, SecondsDigit1
		subi temp1, -3
		sts SecondsDigit1, temp1
		rjmp runningDFinish

	runningD2:
		compare_byte MinutesDigit1, 0
		breq runningD3
		decrement_byte MinutesDigit1
		clear_byte MinutesDigit2
		lds	temp1, SecondsDigit1
		subi temp1, -3
		sts SecondsDigit1, temp1
		rjmp runningDFinish


	runningD3:
		clear_byte SecondsDigit1
		clear_byte SecondsDigit2
		rjmp runningDFinish

	runningDFinish:
		clear_byte RunningProcessing
		rjmp keypad

//Pause Mode Functions
pauseMode:
	cpi temp1, '*'
	breq PauseModeStar
	cpi temp1, '#'
	breq PauseModeHash
	rjmp keypad

	PauseModeStar: ; resumes running
	set_byte_immediate CurrentMode, RUNNING
	rjmp keypad

	PauseModeHash: ; Clear variables ready for going into entry mode
	clear_byte DigitsEntered 
	clear_byte SecondsDigit1
	clear_byte SecondsDigit2
	clear_byte MinutesDigit1
	clear_byte MinutesDigit2
	clear_word TurntableTimer
	do_lcd_command 0b00000001
	do_lcd_command 0b10001111
	do_lcd_data_d '-'
	do_lcd_command 0b11001111
	lds temp1, DoorStatus
	do_lcd_data_r temp1
	set_byte_immediate CurrentMode, ENTRY
	rjmp keypad

//Finished Mode Functions
finishedMode:
	cpi temp1, '#'
	breq FinishedModeHash
	rjmp keypad

	FinishedModeHash: ; clearing handled by timer0 already ; goes to entry mode
		do_lcd_command 0b00000001
		do_lcd_command 0b10001111
		do_lcd_data_d '-'
		do_lcd_command 0b11001111
		lds temp1, DoorStatus
		do_lcd_data_r temp1
		set_byte_immediate CurrentMode, ENTRY

	rjmp keypad

//Other Functions

correctTimeFormat:
	compare_byte SecondsDigit1, 6
	brge correctTime1
	rjmp correctTimeReturn

	correctTime1:
		compare_byte MinutesDigit2, 9
		breq correctTime2
		increment_byte MinutesDigit2
		lds macrotemp, SecondsDigit1
		subi macrotemp, 6
		sts SecondsDigit1, macrotemp
		rjmp correctTimeReturn

	correctTime2:
		compare_byte MinutesDigit1, 9
		breq correctTimeReturn
		increment_byte MinutesDigit1
		clear_byte MinutesDigit2
		lds macrotemp, SecondsDigit1
		subi macrotemp, 6
		sts SecondsDigit1, macrotemp

	correctTimeReturn:
		ret

//Push Button Handling

//Close Door
EXT_INT0:
	clr buttontemp
	out PORTG, buttontemp
	do_lcd_command 0b11000000
	set_byte_immediate DoorStatus, DOORCLOSED
	lds buttontemp, DoorStatus
	do_lcd_command 0b11001111
	do_lcd_data_r buttontemp

	endint0:
		reti

//Open Door
EXT_INT1:
	ser buttontemp
	out PORTG, buttontemp
	do_lcd_command 0b11000000
	set_byte_immediate DoorStatus, DOOROPEN
	lds buttontemp, DoorStatus
	do_lcd_command 0b11001111
	do_lcd_data_r buttontemp
	
	compare_byte CurrentMode, RUNNING ; if open when running pause
	breq setPause
	compare_byte CurrentMode, FINISHED ; if open when finish return to entry
	breq setEntryO
	rjmp endint1

	setEntryO: ; clearing handled by timer0 already
		do_lcd_command 0b00000001
		do_lcd_command 0b10001111
		do_lcd_data_d '-'
		do_lcd_command 0b11001111
		lds buttontemp, DoorStatus
		do_lcd_data_r temp1
		set_byte_immediate CurrentMode, ENTRY
		rjmp endint1

	setPause:
		set_byte_immediate CurrentMode, PAUSE

	endint1:
		reti


//Timer Handling
Timer0OVF:
	compare_byte CurrentMode, RUNNING ;
	brne EndIFJump
	compare_byte RunningProcessing, 1 ; so timer doesn't interrupt buttons
	breq EndIFJump ; not exactly sure if it helps
	rjmp MicrowaveTime

EndIFJump:
	rjmp EndIF

MicrowaveTime:
	//in temp1, SREG ; don't think all this is needed
	//push temp1
	//push r24
	//push r25
	lds r24, MicrowaveTimer
	lds r25, MicrowaveTimer+1
	adiw r24, 1
	cpi r24, low(7812)
	ldi timertemp, high(7812)
	cpc r25, timertemp
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

cooked: ; Clear variables ready for going into entry mode after finish mode
	clear_byte DigitsEntered 
	clear_byte SecondsDigit1
	clear_byte SecondsDigit2
	clear_byte MinutesDigit1
	clear_byte MinutesDigit2
	clear_word TurntableTimer
	set_byte_immediate PowerLevel, POWER100
	set_byte_immediate Turntable, '-'
	set_byte_immediate CurrentMode, FINISHED

showTime: ; displays time counting down
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
	breq finishedCooking
	rjmp TurnTableTime

finishedCooking:
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

	compare_byte TurntableStatus, 0 ; set turntablestatus for next turn
	breq TurntableStatusChange
	clear_byte TurntableStatus
	rjmp EndIF

	TurntableStatusChange:
	set_byte_immediate TurntableStatus, 1
	rjmp EndIF

TurntableTime: ; different comparator for turntable
	lds r24, TurntableTimer
	lds r25, TurntableTimer+1
	adiw r24, 1
	cpi r24, low(39060)
	ldi timertemp, high(39060)
	cpc r25, timertemp
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
	lds timertemp, TurnTable
	do_lcd_data_r timertemp

EndIF:
//	pop r25
//	pop r24
//	pop temp1
//	out SREG, temp1
	reti

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
