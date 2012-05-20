; Lab 3
; Charles Treatman, Dan Herr, Nick Miller
(require (lib "compat.ss"))
(require (lib "trace.ss"))

(define firsts
  (lambda (llyst)
    (cond [(null? llyst) '()]
          [else (cons (car (car llyst)) (firsts (cdr llyst)))])))

(define rests
   (lambda (llyst)
    (cond [(null? llyst) '()]
          [else (cons (cdr (car llyst)) (rests (cdr llyst)))])))


(define addvec
  (lambda (vec1 vec2)
    (cond [(null? vec1) '()]
          [else (cons (+ (car vec1) (car vec2)) (addvec (cdr vec1) (cdr vec2)))])))

(define dot-product
  (lambda (vec1 vec2)
    (cond [(null? vec1) 0]
          [else (+ (* (car vec1) (car vec2)) (dot-product (cdr vec1) (cdr vec2)))])))

(define dot-row
  (lambda (vec mat)
    (cond [(null? mat) '()]
          [(null? (car mat)) '()]
          [else (cons (dot-product vec (car mat)) (dot-row vec (cdr mat)))])))


(define transpose
  (lambda (mat)
    (cond [(null? mat) '()]
          [(null? (car mat)) '()]
          [else (cons (firsts mat) (transpose (rests mat)))])))

(define matmult
  (lambda (mat1 mat2)
    (matmult1 mat1 (transpose mat2))))

(define matmult1
  (lambda (mat1 mat2)
    (cond [(null? mat1) ()]
          [(null? (car mat1)) ()]
          [else (cons (dot-row (car mat1) mat2) (matmult1 (cdr mat1) mat2))])))


(define list+
  (lambda (lyst)
    (cond [(null? lyst) 0]
          [(list? (car lyst)) (+ (list+ (car lyst)) (list+ (cdr lyst)))]
          [(number? (car lyst)) (+ (car lyst) (list+ (cdr lyst)))]
          [else (list+ (cdr lyst))])))

(define occur
  (lambda (a lat)
    (cond [(null? lat) 0]
          [(eq? a (car lat)) (+ 1 (occur a (cdr lat)))]
          [else (occur a (cdr lat))])))

(define union
  (lambda (lat1 lat2)
    (cond [(null? lat2) lat1]
         [(zero? (occur (car lat2) lat1)) (cons (car lat2) (union lat1 (cdr lat2)))]
         [else (union lat1 (cdr lat2))])))

(define intersect
  (lambda (lat1 lat2)
    (cond [(null? lat1) ()]
          [(zero? (occur (car lat1) lat2)) (intersect lat2 (cdr lat1))]
          [else (cons (car lat1) (intersect lat2 (cdr lat1)))])))


(define occurN
  (lambda (lat1 lat2)
    (cond [(null? lat1) 0]
          [else (+ (occur (car lat1) lat2) (occurN (cdr lat1) lat2))]
          )))
