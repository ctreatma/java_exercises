(define-syntax while
  (syntax-rules (do)
    [(_ c do s1 ...)
     (let loop () (if c (begin s1 ... (loop))))]))

(define fact-loop
  (lambda (n)
    (let ([answer 1])
       (while (<= 0 n) do (printf "~s~n" answer)
              (set! answer (* answer n)) (set! n (sub1 n))))))

