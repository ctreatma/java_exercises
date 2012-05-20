;; coroutines foo and goo

(require "coroutine.ss")

(define foo
  (coroutine
   (lambda (m)
     (letrec ([loop
	       (lambda ()
		 (printf "foo: ~a~n" m)
		 (if (<= m 100)
		     (begin
		       (set! m (resume goo (+ m 2)))
		       (loop))))])
       (loop)))))

(define goo  
  (coroutine   
   (lambda (n) 
     (letrec ([loop 
	       (lambda () 
		 (printf "goo: ~a~n" n)
		 (set! n (resume foo (- n 1)))
		 (loop))])
       (loop)))))
