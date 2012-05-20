;; Knight's Tour Problem
;;  -- using "solve-cc" design pattern

;; try 
;; > (knight '(0 . 0))

(require "chessboard.ss" "solve.ss")

(define knight
  (lambda (start)
    (clear-board)
    (solve (*initial* start) *complete?* *extensions* 
           *first* *rest* *empty?* *extend* *legal?* *top-k*
           *do* *undo*)))

; solve parameters
        
(define *initial*
  (lambda (start)
    (lambda ()
      (init-kboard)
      (let ([psol (*extend* () start)])
        (*do* psol)
        psol))))

(define *extend*
  (lambda (psol choice)
    (cons choice psol)))

(define *complete?*
  (lambda (psol) (= (length psol) 64)))

(define *empty?* null?)
(define *first* car)
(define *rest* cdr)
(define *top-k* (lambda (k q) (cons k q)))

(define *do*
  (lambda (psol)
    (draw-piece-at (caar psol) (cdar psol) 
                   (number->string (length psol)))
    (kboard-set! (caar psol) (cdar psol) (length psol))))

(define *undo* 
  (lambda (psol)
    (if (= (length psol) (kboard-get (caar psol) (cdar psol)))
        (begin (erase-piece-at (caar psol) (cdar psol))
               (kboard-set! (caar psol) (cdar psol) -1)))))

(define *extensions*
  (lambda (psol)
    (if (null? psol) ()
        (let ([deltas '((-1 . -2) (-2 . -1) (-2 . 1) (-1 . 2)
                   (1 . 2) (2 . 1) (2 . -1) (1 . -2))]
              [current (car psol)])
          (map (lambda (delta) 
                 (cons (+ (car delta) (car current))
                       (+ (cdr delta) (cdr current))))
               deltas)))))

(define *legal?*
  (lambda (psol)
    (= (kboard-get (caar psol) (cdar psol)) -1)))

; local chessboard data structure for recording moves

(define kboard ())
(define init-kboard
  (lambda ()
    (let ([ans (make-vector 8 ())])
      (for i 0 7
           (vector-set! ans i (make-vector 8 -1)))
      (set! kboard ans))))
(define kboard-set!
  (lambda (i j v) 
    (if (and (>= i 0) (>= j 0) (< i 8) (< j 8))    
        (vector-set! (vector-ref kboard j) i v))))
(define kboard-get
  (lambda (i j) 
    (if (and (>= i 0) (>= j 0) (< i 8) (< j 8))
        (vector-ref (vector-ref kboard j) i)
        -10)))

; init graphic board

(chessboard "Knight's Tour")
