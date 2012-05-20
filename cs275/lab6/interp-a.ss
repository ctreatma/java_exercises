;; interp-a.ss
;;   Interpreter for Mini-Scheme-A

(require (lib "eopl.ss" "eopl"))

(define eval-exp
   (lambda (exp)
      (cases exp-a exp
         (exp-a-lit (datum) datum)
         (else (error 'eval-exp
                  "Invalid abstract syntax: ~s"
                  exp)))))