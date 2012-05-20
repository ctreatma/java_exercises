; Lab 2, Part 2
; Charles Treatman, Patrick Sullivan, Nick Miller, Dan Herr
(require (lib "compat.ss"))
(define member (lambda (a lat)
                 (cond [(null? lat) #f]
                       [(eq? (car lat) a) #t]
                       [#t (member a (cdr lat))])))

(define rember (lambda (a lat)
                 (cond [(null? lat) ()]
                       [(eq? (car lat) a) (cdr lat)]
                       [#t (cons (car lat) (rember a (cdr lat)))])))

(define double 
  (lambda (lat) 
    (cond ((null? lat) ())
          (else (cons (car lat) (cons (car lat) (double (cdr lat))))))))

(define rember2 (lambda (a lat)
                  (cond [(null? lat) ()]
                        [(eq? (car lat) a) (cons (car lat) (rember a (cdr lat)))]
                        [#t (cons (car lat) (rember2 a (cdr lat)))])))


(define duplicate 
  (lambda (n exp)
    (cond [(eq? n 0) ()]
          [else (cons exp (duplicate (- n 1) exp))])))


(define splice
  (lambda (lyst1 lyst2)
    (cond [(null? lyst1) lyst2]
          (#t (cons (car lyst1) (splice (cdr lyst1) lyst2))))))

(define multiple 
  (lambda (n lat)
    (cond ((null? lat) ())
          (#t (splice (duplicate n (car lat)) (multiple n (cdr lat)))))))

(define index1
  (lambda (a lat)
    (cond [(eq? (car lat) a) 0]
          [else (+ 1 (index1 a (cdr lat)))])))

(define index2
  (lambda (a lat)
    (cond [(not (member a lat)) -1]
          [(eq? (car lat) a) 0]
          [else (+ 1 (index1 a (cdr lat)))])))

(define subst-from-list
  (lambda (lat1 new lat2)
    (cond [(null? lat2) ()]
          [(member (car lat2) lat1)
           (cons new (cdr lat2))]
          [else (cons (car lat2) (subst-from-list lat1 new (cdr lat2)))]
          )))

                  