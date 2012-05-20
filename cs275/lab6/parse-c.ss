;; parse-c.ss
;;   Datatype and parser for Mini-Scheme-C

(require (lib "eopl.ss" "eopl"))

(define-datatype exp-c exp-c?
  (exp-c-lit (datum number?))
  (exp-c-varref (var symbol?))
  (exp-c-app (rator varref?) (rands list-of-exp-c?)))

(define varref?
  (lambda (x) (cases exp-c x (exp-c-varref (var) #t) (else #f))))

(define list-of-exp-c?
  (lambda (l) 
    (andmap exp-c? l)))

(define parse
  (lambda (datum)
    (cond
     ((number? datum) (exp-c-lit datum))
     ((symbol? datum) (exp-c-varref datum))
     ((pair? datum) (exp-c-app (parse (car datum)) (map parse (cdr datum))))
     (else (error 'parse "Invalid concrete syntax ~s" datum)))))
