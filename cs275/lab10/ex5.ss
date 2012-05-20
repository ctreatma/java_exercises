(require (lib "compat.ss"))

(define length-aps
  (lambda (l aps)
    (cond [(null? l) aps]
          [else (length-aps (cdr l) (add1 aps))])))

(define make-palindrome-aps
  (lambda (l aps1 aps2)
    (cond [(null? l) '()]
          [(null? (cdr l)) (append aps2 (list (car l)) aps1)]
          [else (make-palindrome-aps (cdr l) (cons (car l) aps1) (append aps2 (list (car l))))])))

(define flatten-aps
  (lambda (l acc)
    (if (null? l) acc
      (if (atom? l) (cons l acc)
          (flatten-aps (car l) (flatten-aps (cdr l) acc))))))