;; Good Sequences Problem
;;  -- using "solve" design pattern

;; try
;; > (goodseq 4)
;; > (goodseq 6)
;; > (goodseq 10)
;; etc.

(require "solve.ss")

(define goodseq
  (lambda (len)
    (solve *initial* (*complete?* len) *extensions* *first* *rest* 
           *empty?* *extend* *legal?* *top-k* 
           (lambda (x) ()) (lambda (x) ()))))

(define *initial* (lambda () ()))
(define *complete?* (lambda (len) (lambda (x) (= (length x) len))))
(define *extensions* (lambda (x) 3))
(define *first* (lambda (x) x))
(define *rest* (lambda (x) (sub1 x)))
(define *return* (lambda (x q) (list x q)))
(define *empty?* zero?)
(define *extend* (lambda (x y) (cons y x)))

(define *top-k*
  (lambda (x q)
    (printf "~s~n" x)
    (q)))

(define *legal?*
  (lambda (str) 
    (let legal1 ([left (list (car str))]
		 [right (cdr str)])
      (if (null? right) #t
	  (if (check left right) #f
	      (legal1 (append left (list (car right))) (cdr right)))))))

(define check
  (lambda (pat dat)
    (if (null? pat) #t
	(if (null? dat) #f
	    (if (eq? (car pat) (car dat)) (check (cdr pat) (cdr dat))
		#f)))))
