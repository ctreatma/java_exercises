;; interp-b.ss
;;   Interpreter for Mini-Scheme-B

(require (lib "eopl.ss" "eopl"))

(define eval-exp
   (lambda (exp)
      (cases exp-b exp
        (exp-b-lit (datum) datum)
        (exp-b-varref (var) (apply-env init-env var))
        (else (error 'eval-exp
                  "Invalid abstract syntax: ~s"
                  exp)))))


; primop definitions

(define-datatype prim prim?
  (prim-proc (prim-op proc-symbol?)))

(define proc-symbol? (lambda (sym) (member sym '(+ - * /))))

(define prim-op-names '(+ - *))

(define init-env
	(extend-env
	   prim-op-names
	   (map prim-proc prim-op-names)
           the-empty-env))
