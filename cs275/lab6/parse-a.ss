;; parse-a.ss
;;   Datatype and parser for Mini-Scheme-A

(require (lib "eopl.ss" "eopl"))

(define-datatype exp-a exp-a?
  (exp-a-lit (datum number?)))

(define parse
  (lambda (exp)
    (cond
     ((number? exp) (exp-a-lit exp))
     (else (error 'parse "Invalid concrete syntax ~s" exp)))))
