(require "spm.ss")

(define <account>
  (class (user-pin)
    ([num-transactions 0]           ;; class variables
     [vol-transactions 0]
     [interest-rate 0.025]
     [bank-code 12345]
     [output (lambda (a) (printf "The balance is $~s~%" a))])
    ([balance 0])                   ;; instance variables
    (<object>)                   ;; superclass
    ([balance?                      ;; methods
       (method () balance)]
     [deposit
       (method (amount)
	 (if (> amount 0)
	     (begin
	       (set! balance (+ balance amount))
	       (output balance))))]
     [withdraw
       (method (amount pin)
         (if (and (= user-pin pin) (> amount 0) (>= balance amount))
	     (begin
	       (set! balance (- balance amount))
	       (output balance))
	     (error 'withdraw 
		    "check your pin or withdrawal amount")))]
     [add-interest
       (method (code)
	 (if (= code bank-code)
	     (call deposit this (* balance interest-rate)) ;self reference
	     (error 'add-interest "invalid bank access code")))])))
