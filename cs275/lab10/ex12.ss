(require "coroutine.ss")

(define foo
  (coroutine
   (lambda (m)
     (begin (printf "foo: ~a~n" m) (if (<= m 100) (foo (goo (+ m 2))))))))
 
(define goo
  (coroutine
   (lambda (n)
     (begin (printf "goo: ~a~n" n) (goo (foo (- n 1)))))))

;x^2 + 2x + 1
  
(define sqfoo
  (coroutine
   (lambda (m)
     (begin (printf "sqfoo: ~a~n" m) (if (<= m 50)
                                         (sqfoo (sqgoo m)))))))
 
(define sqgoo
  (coroutine
   (lambda (n)
     (let ([ans 0])
       (let loop ([x n])
         (if (= x 0) (+ ans 0) (begin (set! ans (+ ans (* 2 (sub1 x)) 1)) (loop (sub1 x)))))
     (printf "sqgoo: ~a~n" ans))
     (sqgoo (sqfoo (+ n 1))))))

;(+ square (* 2 (sub1 n)) 1)