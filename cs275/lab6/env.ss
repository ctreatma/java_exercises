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
