(require "spm.ss")

(define <account>
  (class ()
    ([all-instances '()]     
     [num-transactions 0]           ;; class variables
     [vol-transactions 0]
     [interest-rate 0.025]
     [bank-code 12345]
     [output (lambda (a) (printf "The balance is $~s~%" a))])
    ([user-pin (random 10000)]
     [balance 0])                   ;; instance variables
    (<object>)                   ;; superclass
    ([balance?                      ;; methods
       (method () balance)]
     [deposit
       (method (amount)
	 (if (> amount 0)
	     (begin
               (set! num-transactions (+ 1 num-transactions))
               (set! vol-transactions (+ amount vol-transactions))
	       (set! balance (+ balance amount))
	       (output balance))))]
     [withdraw
       (method (amount pin)
         (if (and (= user-pin pin) (> amount 0) (>= balance amount))
	     (begin
	       (set! num-transactions (+ 1 num-transactions))
               (set! vol-transactions (+ amount vol-transactions))
               (set! balance (- balance amount))
	       (output balance))
	     (error 'withdraw 
		    "check your pin or withdrawal amount")))]
     [transaction-info
      (method (code)
              (if (= code bank-code)
                  (begin
                    (printf "There have been ~s" num-transactions)
                    (printf " transactions with a total volume of &~s~%"
                            vol-transactions))
                  (error 'transaction-info "invalid bank access code")))]
     [set-interest
      (method (rate code)
              (if (= code bank-code)
                  (set! interest-rate rate)
                  (error 'set-interest "invalid bank access code")))]
     [add-interest
      (method (code)
              (if (= code bank-code)
                  (for-each
                   (lambda (i)
                     (call deposit i (* (call balance? i) interest-rate)))
                   all-instances)
                  (error 'add-interest "invalid bank access code")))]
     [change-pin
      (method ()
              (printf "Enter your old pin >")
              (let ([old (read)]) (if (= old user-pin)
                                    (begin
                                      (printf "Enter new pin >")
                                      (let ([new1 (read)]) (printf "Verify new pin >")
                                        (let ([new2 (read)]) (if (= new1 new2) (set! user-pin new1) (error 'change-pin "No match on retype"))))) (error 'change-pin "Wrong old pin entered"))))]
     [bank-change-pin
      (method (pin code)
              (if (= code bank-code)
                  (set! user-pin pin)
                  (error 'bank-change-pin "invalid bank access code")))]) 
      (set! all-instances (cons this all-instances))
      (printf "Your pin number is ~s" user-pin)
      (printf ".  Don't forget your pin!")
    ))


