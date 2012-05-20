;Charles Treatman
;Lab 4
(require (lib "compat.ss"))

;Exercise 1
(define mag
  (lambda ls
    (sqrt (squaresum ls))))

(define squaresum
  (lambda (lat)
    (cond [(null? lat) 0]
          [else (+ (* (car lat) (car lat)) (squaresum (cdr lat)))])))

;Exercise 2

(define norm
  (lambda (lat)
    (map (lambda (item) (/ item (apply mag lat))) lat)))

;Exercise 3

(define and-map
  (lambda (proc l)
    (cond [(null? l) #t]
          [(eq? (proc (car l)) #t) (and-map proc (cdr l))]
          [else #f])))

;Exercise 4

(define fold
  (lambda (recur-case base-case lyst)
    (letrec ([help-fold
              (lambda (l)
                (if (null? l)
                    base-case
                    (recur-case (car l) (help-fold (cdr l)))))])
      (help-fold lyst))))

(define bags '((duffle 8) (garment-bag 2) (briefcase 5) (valise 7) (steamer-trunk 65)))

(define weigh
  (lambda (lat)
    (fold (lambda (ca cd)
          (+ (cadr ca) cd)) 0 lat)))

(define heaviest
  (lambda (lat)
    (fold (lambda (ca cd)
            (if (> (cadr ca) (cadr cd)) ca cd)) '(0 0) lat)))

(define filter
  (lambda (lat)
    (fold (lambda (ca cd)
            (cond [(> (cadr ca) 0) (cons ca cd)]
                  [else cd])) '() lat)))

;Exercise 5

(define find-and-report
  (lambda (pred report-fn otherwise lyst)
    (letrec ([help-find
	  	(lambda (l)
		  (cond [(null? l) otherwise]
                        [(pred (car l)) (report-fn (car l))]
                        [else (help-find (cdr l))]))])
      (help-find lyst))))

(define are-there-any-zeros?
  (lambda (ls)
    (find-and-report zero? (lambda (x) #t) #f ls)))

;Exercise 6

(define deep-recur-check
  (lambda (shallow-proc deep-proc pred succ-proc base-case ls)
    (letrec ((helper
	      (lambda (l)
		(cond
		 [(null? l) base-case]
                 [(pred (car l)) (succ-proc l)]
		 [(atom? (car l)) (shallow-proc
				    (car l)
				    (helper (cdr l)))]
		 [else
		  (deep-proc (helper (car l)) (helper (cdr l)))]))))
      (helper ls))))

(define tree-mul-abst
  (lambda (ls)
    (deep-recur-check *
		      *
		      (lambda (x) (and (number? x) (zero? x)))
		      (lambda (x) 0)
		      1
		      ls)))

(define member*
  (lambda (a ls)
    (deep-recur-check (lambda (x y) (or (eq? a x) y))
                      (lambda (x y) (or x y))
                      (lambda (x) (eq? a x)) (lambda (x) #t) #f ls)))