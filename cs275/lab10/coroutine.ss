;; coroutine.ss

; A coroutine is modeled in Scheme as a procedure of 1 argument 
;  which never returns to its caller.  Instead, it may at any time 
;  evaluate a resume expression:  (resume *cr* *val*), where
;  *cr* must evaluate to another coroutine.  The coroutine evaluating
;  this expression immediately blocks (i.e. quits processing) and the
;  coroutine *cr* is activated.  If this is the first time *cr* has 
;  executed, it runs from the beginning with *val* as its argument.
;  Otherwise, *cr* must have blocked on its own call to resume, and so it
;  restarts by returning *val* as the value of that call

;  Implementation:  (coroutine f) {where f = (lambda (v) ....)}
;   is a macro which expands into (make-coroutine (lambda (resume) f))

;  make-coroutine introduces a local state variable LCS which will be bound
;   to the successive continuations created when the coroutine blocks
;   (LCS stands for local control state).

;  Free use of resume within the coroutine is made possible by applying
;  (lambda (resume) f) to a resume procedure which grabs the
;  current continuation, stores it in LCS, and dispatches to the coroutine
;  passed as the first parameter.  Since the coroutine itself has the form 
;  (lambda (v) (LCS v)), this dispatch will always revive the target
;  coroutine with the continuation stored in LCS.

;  To initialize the coroutine for its first run, we apply f to a resume of the
;  caller (i.e. the procedure invoking (make-coroutine ...)).  This has the
;  effect of storing a continuation in LCS that is about to start processing
;  f with the value returned by the call to resume -- which will be the
;  initial value passed to the coroutine.

;  Dr. Scheme module version by Richard Salter, 11/02

(module coroutine mzscheme
  (define make-coroutine
    (lambda (f)
      (call/cc
       (lambda (caller)
         (let ([LCS '*])
           (let ([resume 
                  (lambda (dest val)
                    (call/cc
                     (lambda (k)
                       (set! LCS k)
                       (dest val))))])
             (begin
               ((f resume)
                (resume caller (lambda (v) (LCS v))))
               (error 'coroutine "fell off end"))))))))
  
  (define-syntax coroutine
    (lambda (stx)
      (syntax-case stx ()
        [(src_class lamexp) 
         (with-syntax ([resume (datum->syntax-object (syntax src_class) 'resume)])
           (syntax (make-coroutine (lambda (resume) lamexp))))])))
  
  (provide coroutine)
  )
