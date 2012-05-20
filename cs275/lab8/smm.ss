;;; SCHEME--, a small extension of Scheme to demonstrate OO programming.

;;; A new edition without inits, with letrec*, and written 
;;; by John Lacey, 5/4/95.
;;; rewritten as module for Dr. Scheme with define-syntax by Richard Salter, 10/02

(module smm mzscheme
  
  (define-syntax call 
    (lambda (stx)
      (syntax-case stx (super)
        [(src-call name super operand1 ...)
         (with-syntax ([super (datum->syntax-object (syntax src-call) 'super)])
           (syntax ((lookup-method 'name super) this operand1 ...)))]
        [(_ name operand0 operand1 ...)
         (syntax (let ([instance operand0])
                   ((lookup-method 'name instance) instance operand1 ...)))])))
  
  (define-syntax method 
    (lambda (stx)
      (syntax-case stx ()
        [(src-method parameter-list expr1 ...)
         (with-syntax ([this (datum->syntax-object (syntax src-method) 'this)])
           (syntax (lambda (this . parameter-list) expr1 ...)))])))
  
  (define-syntax letrec* 
    (lambda (stx)
      (syntax-case stx ()
        [(_ ([var val] ...) expr1 expr2 ...)
         (syntax (let ([var 'any] ...)
                   (set! var val) ...
                   expr1 expr2 ...))])))
  
  (define-syntax class 
    (lambda (stx)
      (syntax-case stx ()
        [(src-class parameter-list
                    ([var val] ...)
                    super-expr
                    ([method-name method-expr] ...))
         (with-syntax ([super (datum->syntax-object (syntax src-class) 'super)])
           (syntax (lambda parameter-list
                     (letrec* ([var val] ...)
                       (let ([super super-expr])
                         (append
                          (list (cons 'method-name method-expr) ...)
                          super))))))])))
  
  (define <object> (lambda () '()))
  
  (define lookup-method
    (lambda (name instance)
      (let ((x (assq name instance)))
        (if (pair? x)
            (cdr x)
            (error 'call "Bad Method: ~s" name)))))
  
; to see expansions

(define expand-to
  (lambda (x)
    (syntax-object->datum (expand-to-top-form x))))

  (provide call method class <object> expand-to)

  )



