;; parse-g.ss
;;   Datatype and parser for Mini-Scheme-G

(require (lib "eopl.ss" "eopl"))

(define-datatype exp-g exp-g?
  (exp-g-lit (datum number?))
  (exp-g-varref (var symbol?))
  (exp-g-app (rator varref?) (rands list-of-exp-g?))
  (exp-g-if (test-exp exp-g?) (then-exp exp-g?) (else-exp exp-g?)))

(define varref?
  (lambda (x) (cases exp-g x (exp-g-varref (var) #t) (else #f))))

(define list-of-exp-g?
  (lambda (l) 
    (andmap exp-g? l)))

(define parse
  (lambda (datum)
    (cond
     ((number? datum) (exp-g-lit datum))
     ((symbol? datum) (exp-g-varref datum))
     ((pair? datum) (if (eqv? (car datum) 'if) (exp-g-if
                                                         (parse (cadr datum)) 
                                                         (parse (caddr datum))
                                                         (parse (cadddr datum)))
      (exp-g-app (parse (car datum)) (map parse (cdr datum)))))
     (else (error 'parse "Invalid concrete syntax ~s" datum)))))