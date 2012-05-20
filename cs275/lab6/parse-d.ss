;; parse-d.ss
;;   Datatype and parser for Mini-Scheme-D

(require (lib "eopl.ss" "eopl"))

(define-datatype exp-d exp-d?
  (exp-d-lit (datum number?))
  (exp-d-varref (var symbol?))
  (exp-d-app (rator varref?) (rands list-of-exp-d?)))

(define varref?
  (lambda (x) (cases exp-d x (exp-d-varref (var) #t) (else #f))))

(define list-of-exp-d?
  (lambda (l) 
    (andmap exp-d? l)))

(define parse
  (lambda (datum)
    (cond
     ((number? datum) (exp-d-lit datum))
     ((symbol? datum) (exp-d-varref datum))
     ((pair? datum) (exp-d-app (parse (car datum)) (map parse (cdr datum))))
     (else (error 'parse "Invalid concrete syntax ~s" datum)))))
