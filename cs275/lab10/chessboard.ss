(module chessboard mzscheme
  (require "smm.ss" "graphics-mm.ss" "polygon.ss")
  
  (define <rectangle>
    (class (left top right bottom)
      ()
      (<polygon> left top left bottom right bottom right top)
      ()))
  
  (define <square>
    (class (left top size)
      ()
      (<rectangle> left top (+ left size) (+ top size))
      ()))
  
  (define <ellipse>
    (class (left top right bottom)
      ()
      (<filled-shape>)
      ([draw
        (method ()
                (call fill-shape super 'oval left top right bottom))])))
  
  (define <circle>
    (class (cx cy radius)
      ()
      (<ellipse> (- cx radius) (- cy radius)
                 (+ cx radius) (+ cy radius))
      ([draw-text
        (method (text) (call draw-shape super 'text text (- cx .30) (- cy .30)))])))
                
  
  (define <red-square>
    (class (left top size)
      ()
      (<square> left top size)
      ([draw
        (method ()
                (call color super "red")
                (call draw super))])))
  
  (define theboard ())
  
  (define-syntax for
    (lambda (stx)
      (syntax-case stx ()
        [(_ v bot top e1 ...)
         (identifier? (syntax v))
         (let* ([botd (syntax-object->datum (syntax bot))]
                [topd (syntax-object->datum (syntax top))]
                [inc (if (< botd topd) 'add1 'sub1)]
                [test (if (< botd topd) '<= '>=)])
           (with-syntax ([inc inc][test test])
             (syntax
              (let loop ([v bot])
                (if (test v top) 
                    (begin e1 ... (loop (inc v))))))))])))
  
  (define chessboard
    (lambda (title)
      (let ([board (<square> 1 1 24)])
        (init-graphics 520 540 title)
        (call color board "white")
        (call draw board)
        (set! theboard (fill-board)))))
  
  (define fill-board
    (lambda ()
      (let ([ans (make-vector 8 ())])
        (for i 0 7
             (let ([vec (make-vector 8 ())])
               (for j 0 7 (vector-set! vec j (sqat i j)))
               (vector-set! ans i vec)))
        ans)))
  
  (define call-at
    (lambda (i j f)
      (f i j (vector-ref (vector-ref theboard j) i))))
  
  (define sqat
    (lambda (i j)
      (let ([sq (<square> (add1 (* i 3)) (add1 (* j 3)) 3)])
        (init-sq i j sq)
        sq)))
  
  (define init-sq
    (lambda (i j sq) 
      (if (zero? (remainder (+ i j) 2))
          (call color sq "red")
          (call color sq "black"))
      (call draw sq)))
  
  (define draw-piece-at
     (let ([draw
            (lambda (j i text)
              (if (and (>= i 0) (>= j 0) (< i 8) (< j 8))
                  (let ([queen (<circle> (+ 2.5 (* i 3)) (+ 2.5 (* j 3)) 1.0)])
                    (call color queen "white")
                    (call draw queen)
                    (call draw-text queen text))))])
       (case-lambda 
         ((j i) (draw j i ""))
         ((j i text) (draw j i text)))))

  (define erase-piece-at
    (lambda (i j)
      (if (and (>= i 0) (>= j 0) (< i 8) (< j 8))
          (call-at i j init-sq))))
  
  (define clear-board
    (lambda ()
      (for i 0 7 (for j 0 7 (erase-piece-at i j)))))
  
  (provide for draw-piece-at erase-piece-at chessboard clear-board)
  )