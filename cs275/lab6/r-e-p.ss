;; Read-Eval-Print for Mini-Scheme

(require (lib "eopl.ss" "eopl"))

(define read-eval-print
  (lambda ()
    (let ([orig (error-escape-handler)])
      (let/ec exit
        (let retry-loop ()
          (let/ec escape
            (error-escape-handler
             (lambda () (escape #f)))
            (let loop ()
              (begin
                (display "MS> ")
                (let ([in (read)])
                  (if (eq? in 'exit )
                      (begin
                        (printf "returning to Scheme proper~n")
                        (exit #f))
                      (begin
                        (display (eval-exp (parse in)))
                        (newline)
                        (loop)))))))
          (retry-loop)))
      (error-escape-handler orig))))

