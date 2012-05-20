
## bases -- print integer in specified base
# synopsis:
#	prompts for integer input
#	prompts for base input
#	prints integer in specified base
# procedure
#	print_int:	  prints ans message & integer in specified base
#				(converting to ascii using base)
#		parameters:	  $a0 = integer to be printed
#			     $a1 = base in which it is to be printed
#		return vals:	 none
##
	.data

prompt1:		.asciiz "Bases program\n"
prompt2:		.asciiz "Please enter a number\n"
prompt3:		.asciiz "Please enter a base\n"

## main program
	.text
	.globl main
main:		li	$v0, 4			#
		la	$a0, prompt1		# say howdy
		syscall				#
		li	$v0, 4			#
		la	$a0, prompt2		# prompt for input
		syscall				#
		li	$v0, 5			# get input
		syscall				#
		move	$t0, $v0		# result is in $v0:	  move to $t0 for call
		li	$v0, 4			#
		la	$a0, prompt3		# prompt for input
		syscall				#
		li	$v0, 5			# get input
		syscall				#
		move	$a0, $t0		# load number into $a0
		move	$a1, $v0		# load base into $a1
		jal	print_int		# print answer
		li	$v0, 10			# exit
		syscall
## end main

## print_int -- procedure to print out an integer in specified base
#
#	return values:	  none
#	parameters:	$a0 -- integer to be printed
#			$a1 -- base for printing
#	register use:	$t0 -- used as a temporary in various functions
#			$t1 -- used as a temporary in various functions
#			$t2 -- used as a temporary in various functions
#			$fp -- used as a stack pointer for the char stack
			
			.data
msg:			.asciiz	"In that base this integer is:	\n"
linefeed:		.asciiz	"\n"
outbuf:			.asciiz " "		# output buffer for printing individual characters
		.text

print_int:	subu $sp, $sp, 32		# Stack frame is 32 bytes long
		sw $ra, 20($sp)			# Save return address
		sw $fp, 16($sp)			# Save frame pointer
		addiu $fp, $sp, 28		# Set up frame pointer
		sw $0, 12($sp)			# Set up stack counter

$L2:		beq $a0,$0, zero		# If n = 0, we're done
		rem $t0, $a0, $a1		# d = n rem b
		div $a0, $a0, $a1		# Divide n by b
		ble $t0, 9, push		# If 0 < d <= 9, push d
		addiu $t0, $t0, 7		# Shift char up to skip non-alpha chars
push:		addiu $t0, $t0, 48		# Add 0x30 to put char in the right place in ASCII
		lw $t1, 12($sp)			# load stack counter
		sw $t0, ($fp)			# push the char onto the stack
		add $fp, $fp, 4			# move fp to next word
		add $t1, $t1, 1			# increment stack counter
		sw $t1, 12($sp)			# store stack counter
		jr $L2				# return to loop
					
$L1:		lw $t1, 12($sp)			# load stack counter
		beq $t1, $0, return		# If stack counter = 0, printing is done
		sub $fp, $fp, 4			# move fp to previous word
		lw $t2, ($fp)			# load char value into $t2
		sb $t2, outbuf			# store char in outbuf
		li	$v0, 4			#
		la	$a0, outbuf		# Print outbuf
		syscall				#
		sub $t1, $t1, 1			# decrement stack counter value
		sw $t1, 12($sp)			# store in stack counter
		jr $L1				# return to loop

zero:		li $v0, 4			#
		la $a0, msg			# Prepare for output...print end greeting
		syscall
		lw $t1, 12($sp)			# Load stack counter
		bgtz $t1, $L1			# If stack counter > 0, original n > 0, branch
		move $t2, $0 			# Store 0 in temporary
		add $t2, $t2, 48		# Add 48 to get ASCII value of 0
		sb $t2, outbuf			# Store value into outbuf
		li	$v0, 4			#
		la	$a0, outbuf		# Print outbuf
		syscall				#
		j return			# Return
			
return:		li $v0, 4			# 
		la $a0, linefeed		# Print a newline
		syscall				#
		sw $0, 12($sp)			# Restore stack counter
		lw	$ra,20($sp)		# Restore $ra
		lw	$fp,16($sp)		# Restore $fp
		addiu	$sp,$sp,32		# Pop stack
		jr	$ra
## end print_int





















