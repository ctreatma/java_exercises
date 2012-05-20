	
## fib.asm -- computes fibonacci numbers ;  adheres to MIPS conventions
# algorithm:
# fib(int k) {	
#   if (k == 0) return 0;
#   else if (k == 1) return 1 ;
#   else return (fib(k - 2) + fib(k - 1)) ;
# }
##
			.data

prompt1:		.asciiz "Fibonacci program\n"
prompt2:		.asciiz "Please enter a number\n"
prompt3:		.asciiz	"The result of the Fibonacci:	\n"
linefeed:		.byte	'\n'

			.text
			.globl main
## main program

main:		li	$v0, 4			#
		la	$a0, prompt1		# say howdy
		syscall				#
		li	$v0, 4			#
		la	$a0, prompt2		# prompt for input
		syscall				#
		li	$v0, 5			# get input
		syscall				#
		move	$a0, $v0		# result is in $v0:	  move to $a0 for call
		jal	fib			# call fibonacci
		move	$t0, $v0		# Store answer in $t0
						# End of procedure call
		li	$v0, 4			#
		la	$a0, prompt3		# Print output prompt
		syscall				#
		li	$v0, 1			#
		move	$a0, $t0		# Print fib result
		syscall				#
		li	$v0, 4			#
		la	$a0, linefeed		# Print linefeed
		syscall				#
		li	$v0, 10			# Load exit code
		syscall

## procedure fib - recursive fibonacci procedure goes here
#
#	parameter:	  $a0
#	return value:	  $v0
#	register usage:	  $fp -- stack pointer for fib values
#			  $t1 -- temporary used to hold values until needed
	.text
fib:		subu $sp, $sp, 32		# Stack frame is 32 bytes long
		sw $ra, 20($sp)			# Save return address
		sw $fp, 16($sp)			# Save frame pointer
		addiu $fp, $sp, 28		# Set up frame pointer
		sw $a0, 0($fp)			# Save argument (n)
		
		lw	$t1, 0($fp)		# Load n
		bgt	$t1, 1, $L2		# if the n > 1, branch
		move	$v0, $t1		# move n to $v0, return
		jr $L1
$L2:		sub	$a0, $t1, 1		# decrement n and store in $a0 for call
		jal fib				# call fib(n-1)
		sw	$v0, 4($fp)		# save fib(n-1)
		lw	$t1, 0($fp)		# Load n
		sub	$a0, $t1, 2		# store n-2 in $a0 for call
		jal fib				# call fib(n-2)
		lw	$t1, 4($fp)		# load fib(n-1)
		add	$v0, $v0, $t1		# store fib(n-1) + fib(n-2) in $v0
	
$L1:						# Result is in $v0
		lw	$ra,20($sp)		# Restore $ra
		lw	$fp,16($sp)		# Restore $fp
		addiu	$sp,$sp,32		# Pop stack
		jr	$ra			# Return to caller
## end fib






