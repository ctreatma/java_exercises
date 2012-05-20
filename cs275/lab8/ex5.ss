(require "smm.ss" "graphics-mm.ss" "polygon.ss")

(define <filled-shape>
  (class ()
    ()
    (<shape>)
    ([width
      (method (value)
              (call configure super 'width (exact->inexact value)))])))

(define <ellipse>
  (class (x1 y1 x2 y2)
    ()
    (<filled-shape>)
    ([draw (method () (call fill-shape this 'oval x1 y1 x2 y2))])))

(define <circle>
  (class (x y r)
    ()
    (<ellipse> (- x r) (- y r) (+ x r) (+ y r))
    ()))