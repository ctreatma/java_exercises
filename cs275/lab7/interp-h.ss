;; interp-h.ss
;;   Datatype, parser, environment definition and interpreter for Mini-Scheme-H
;;   Identical to Mini-Scheme-G, except that the environment is passed to eval-exp

(require (lib "eopl.ss" "eopl"))

;; abstract syntax datatype and parser
 
(define-datatype expression expression?
  (lit-exp (datum number?))
  (varref-exp (var symbol?))
  (if-exp (test-exp expression?) (then-exp expression?) (else-exp expression?))
  (app-exp (rator expression?) (rands (list-of expression?))))

(define varref?
  (lambda (x) (cases expression x (varref-exp (var) #t) (else #f))))

(define parse
  (lambda (datum)
    (cond
     ((number? datum) (lit-exp datum))
     ((symbol? datum) (varref-exp datum))
     ((pair? datum) 
      (if (eq? (car datum) 'if)
	  (if (eq? (length datum) 4)
	      (if-exp (parse (cadr datum)) (parse (caddr datum)) (parse (cadddr datum)))
	      (error 'parse "Bad if syntax: ~s" datum))
	  (app-exp (parse (car datum)) (map parse (cdr datum)))))
     (else (error 'parse "Invalid concrete syntax ~s" datum)))))

;; eval-exp

(define eval-exp
   (lambda (exp env)
      (cases expression exp
        (lit-exp (datum) datum)
        (varref-exp (var) (apply-env env var))
	(if-exp (test-exp then-exp else-exp)
		  (let ([val (eval-exp test-exp env)])
		    (if (or (and (number? val) (zero? val)) (eq? val 'False))
			(eval-exp else-exp env)
			(eval-exp then-exp env))))
	(app-exp (rator rands)
           (let ([proc (eval-exp rator env)]
		 [args (eval-rands rands env)])
              (apply-proc proc args)))
        (else (error 'eval-exp
                  "Invalid abstract syntax: ~s"
                  exp)))))

(define eval-rands (lambda (rands env) (map (lambda (z) (eval-exp z env)) rands)))

(define apply-prim-op
   (lambda (prim-op args)
      (case prim-op
         [(+) (+ (car args) (cadr args))]
         [(-) (- (car args) (cadr args))]
         [(*) (* (car args) (cadr args))]
	 [(add1) (add1 (car args))]
	 [(sub1) (sub1 (car args))]
	 [(minus) (- (car args))]
	 [(list) (apply list args)]
	 [(first) (car (car args))]
	 [(rest) (cdr (car args))]
	 [(build) (cons (car args) (cadr args))]
	 [(equals?) (if (eqv? (car args) (cadr args)) 'True 'False)]
	 [(lt?) (if (< (car args) (cadr args)) 'True 'False)]
	 [(gt?) (if (> (car args) (cadr args)) 'True 'False)]
         [else
          (error 'apply-prim-op
             "Invalid prim-op name: ~s"
             prim-op)])))

; primop definitions

(define-datatype procval procval?
  (prim-proc (prim-op proc-symbol?)))

(define proc-symbol? (lambda (sym) (member sym '(+ - * / add1
						   sub1 minus first rest build list
						   equals? lt? gt?))))

(define prim-op-names '(+ - * add1 sub1 minus first rest build list equals? lt? gt?))

;; Environment implementation -- see EOPL, 2.3.3

(require (lib "eopl.ss" "eopl"))

; datatype definition

(define-datatype environment environment? 
  (empty-env-record)             
  (extended-env-record
    (syms (list-of symbol?))
    (vals (list-of scheme-value?))
    (env environment?)))

(define scheme-value? (lambda (v) #t))

(define empty-env
  (lambda ()
    (empty-env-record)))

; The only empty environment we will need

(define the-empty-env (empty-env))

; extend-env -- creates a new environment extending env in which the
;		symbols in syms are bound to vals

(define extend-env
  (lambda (syms vals env)
    (extended-env-record syms vals env)))

; apply-env -- looks up sym in env and returns value, or throws error if 
;              symbol is not bound in env

(define apply-env
  (lambda (env sym)
    (cases environment env
      (empty-env-record ()
        (error 'apply-env "No binding for ~s" sym))
      (extended-env-record (syms vals env)
        (let ((pos (list-find-position sym syms)))
          (if (number? pos)
            (list-ref vals pos)
            (apply-env env sym)))))))

(define list-find-position
  (lambda (sym los)
    (list-index (lambda (sym1) (eqv? sym1 sym)) los)))

(define list-index
  (lambda (pred ls)
    (cond
      ((null? ls) #f)
      ((pred (car ls)) 0)
      (else (let ((list-index-r (list-index pred (cdr ls))))
              (if (number? list-index-r)
                (+ list-index-r 1)
                #f))))))


;; initial environment

(define init-env
  (extend-env '(nil True False) '(() True False)
	      (extend-env
	       prim-op-names
	       (map prim-proc prim-op-names)
	       the-empty-env)))

; apply-proc

(define apply-proc
  (lambda (proc args)
    (cases procval proc
	(prim-proc (prim-op) (apply-prim-op prim-op args))
	(else (error 'apply-proc "Invalid procedure: ~s" proc)))))

;; Read-Eval-Print for Mini-Scheme-H

(define read-eval-print
  (lambda ()
    (let ([orig (error-escape-handler)])
      (let/ec exit
        (let retry-loop ()
          (let/ec escape
            (error-escape-handler
             (lambda () (escape #f)))
            (let loop ()
              (begin
                (display "MS> ")
                (let ([in (read)])
                  (if (eq? in 'exit )
                      (begin
                        (printf "returning to Scheme proper~n")
                        (exit #f))
                      (begin
                        (display (eval-exp (parse in) init-env))
                        (newline)
                        (loop)))))))
          (retry-loop)))
      (error-escape-handler orig))))


