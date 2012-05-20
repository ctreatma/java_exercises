;; parse-b.ss
;;   Datatype and parser for Mini-Scheme-B

(require (lib "eopl.ss" "eopl"))

(define-datatype exp-b exp-b?
  (exp-b-lit (datum number?))
  (exp-b-varref (var symbol?)))


(define parse
  (lambda (exp)
    (cond
     ((number? exp) (exp-b-lit exp))
     ((symbol? exp) (exp-b-varref exp))
     (else (error 'parse "Invalid concrete syntax ~s" exp)))))

