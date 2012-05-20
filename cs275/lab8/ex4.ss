(require "smm.ss" "graphics-mm.ss" "polygon.ss")

(define <rectangle>
  (class (x1 y1 x2 y2)
    ()
    (<polygon> x1 y1 x1 y2 x2 y2 x2 y1)
    ()))

(define <square>
  (class (x y size)
    ()
    (<rectangle> x y (+ x size) (+ y size))
    ()))