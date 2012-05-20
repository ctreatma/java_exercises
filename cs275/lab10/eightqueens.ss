;; Eight Queens Problem
;;  -- using "solve" design pattern

;; try
;; > (define foo (eightqueens))
;; > (set! foo ((cdr foo)))
;; > (set! foo ((cdr foo)))
;; > (set! foo ((cdr foo)))
;; etc.

(require "chessboard.ss" "solve.ss")

(define eightqueens
  (lambda ()
    (clear-board)
    (solve *initial* *complete?* *extensions* *first* *rest* 
           *empty?* *extend* *legal?* *top-k* *do* *undo*)))

(define *initial* (lambda () ()))
(define *complete?* (lambda (psol) (= (length psol) 8)))
(define *extensions* (lambda (psol) 0))
(define *empty?* (lambda (choices) (= choices 8)))
(define *first* (lambda (x) x))
(define *rest* add1)
(define *extend* (lambda (psol next) (cons next psol)))
(define *do* (lambda (psol) (draw-piece-at (car psol) (sub1 (length psol)))))
(define *undo*
  (lambda (psol) (erase-piece-at (car psol) (sub1 (length psol)))))
(define *top-k* (lambda (psol q) (cons psol q)))

(define *legal?*
  (lambda (psol)
    (let ([newrow (car psol)]
          [newcol (sub1 (length psol))]
          [notcovers?
           (lambda (oldrow oldcol newrow newcol)
             (not
              (or (= oldrow newrow)
                  (= (+ oldrow oldcol) (+ newrow newcol))
                  (= (- oldrow oldcol) (- newrow newcol)))))])
      (let loop ([l (cdr psol)])
        (if (null? l) #t
            (let ([oldrow (car l)]
                  [oldcol (sub1 (length l))])
              (and (notcovers? oldrow oldcol newrow newcol)
                   (loop (cdr l)))))))))

; init graphic board

(chessboard "Eight Queens")
