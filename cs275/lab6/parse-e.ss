;; parse-e.ss
;;   Datatype and parser for Mini-Scheme-E

(require (lib "eopl.ss" "eopl"))

(define-datatype exp-e exp-e?
  (exp-e-lit (datum number?))
  (exp-e-varref (var symbol?))
  (exp-e-app (rator varref?) (rands list-of-exp-e?)))

(define varref?
  (lambda (x) (cases exp-e x (exp-e-varref (var) #t) (else #f))))

(define list-of-exp-e?
  (lambda (l) 
    (andmap exp-e? l)))

(define parse
  (lambda (datum)
    (cond
     ((number? datum) (exp-e-lit datum))
     ((symbol? datum) (exp-e-varref datum))
     ((pair? datum) (exp-e-app (parse (car datum)) (map parse (cdr datum))))
     (else (error 'parse "Invalid concrete syntax ~s" datum)))))
