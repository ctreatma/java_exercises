;; Good Sequences Problem
;;  -- using "solve" design pattern

;; try
;; > (goodseq 4)
;; > (goodseq 6)
;; > (goodseq 10)
;; etc.

(define goodseq
  (lambda (len)
    (solve *initial* (*complete?* len) *extensions* *first* *rest* 
           *empty?* *extend* *legal?* *print-it*)))

(define *initial* (lambda () ()))
(define *complete?* (lambda (len) (lambda (x) (= (length x) len))))
(define *extensions* (lambda (x) 3))
(define *first* (lambda (x) x))
(define *rest* (lambda (x) (sub1 x)))
(define *return* (lambda (x q) (list x q)))
(define *empty?* zero?)
(define *extend* (lambda (x y) (cons y x)))

(define *print-it*
  (lambda (x)
    (printf "~s~n" x)))

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

(define solve
  (lambda (*initial* *complete?* *extensions* *first* 
                     *rest* *empty?* *extend* *legal?* *top-k*)
    (let try ([psol (*initial*)]
              [choices (*extensions* (*initial*))])
      (if (*complete?* psol) (*top-k* psol)
          (if (*empty?* choices) ()
              (let* ([new-psol (*extend* psol (*first* choices))])
                (if (*legal?* new-psol)
                    (try new-psol (*extensions* new-psol)))
                (try psol (*rest* choices))))))))