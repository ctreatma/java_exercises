(require (lib "compat.ss"))

(define pairone-k
  (lambda (a l k)
     (if (null? l) (k ())
         (k (pairone-k a (cdr l) (lambda (x) (cons (list a (car l)) x)))))))
                                       
(define pairall-k
  (lambda (l1 l2 k)
     (if (null? l1) (k ())
         (k (pairall-k (cdr l1) l2
                       (lambda (x)
                         (append (pairone-k (car l1) l2 (lambda (y) y)) x)))))))