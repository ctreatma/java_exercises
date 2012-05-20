;; parse-f.ss
;;   Datatype and parser for Mini-Scheme-F

(require (lib "eopl.ss" "eopl"))

(define-datatype exp-f exp-f?
  (exp-f-lit (datum number?))
  (exp-f-varref (var symbol?))
  (exp-f-app (rator varref?) (rands list-of-exp-f?))
  (exp-f-if (test-exp exp-f?) (then-exp exp-f?) (else-exp exp-f?)))

(define varref?
  (lambda (x) (cases exp-f x (exp-f-varref (var) #t) (else #f))))

(define list-of-exp-f?
  (lambda (l) 
    (andmap exp-f? l)))

(define parse
  (lambda (datum)
    (cond
     ((number? datum) (exp-f-lit datum))
     ((symbol? datum) (exp-f-varref datum))
     ((pair? datum) (if (eqv? (car datum) 'if) (exp-f-if
                                                         (parse (cadr datum)) 
                                                         (parse (caddr datum))
                                                         (parse (cadddr datum)))
      (exp-f-app (parse (car datum)) (map parse (cdr datum)))))
     (else (error 'parse "Invalid concrete syntax ~s" datum)))))