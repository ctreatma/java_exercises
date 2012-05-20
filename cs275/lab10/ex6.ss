(require (lib "compat.ss"))

(define length-k
  (lambda (l k)
    (cond [(null? l) (k 0)]
          [else (length-k (cdr l) (lambda (v) (k (add1 v))))])))


(define make-palindrome-k
  (lambda (l k)
    (cond [(or (null? l) (null? (cdr l))) (k l)]
          [else (make-palindrome-k (cdr l) (lambda (v) (k (cons (car l) (append v (list (car l)))))))])))

(define flatten-k
  (lambda (l k)
    (if (null? l) (k ())
      (if (atom? l) (list l)
          (flatten-k (cdr l) (lambda (v) (k (append (flatten-k (car l) (lambda (x) x)) v))))))))
