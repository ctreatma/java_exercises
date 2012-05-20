(require "smm.ss" "graphics-mm.ss" "polygon.ss")

(define <filled-shape>
  (class ()
    ()
    (<shape>)
    ([width
      (method (value)
              (call configure super 'width (exact->inexact value)))]
     [outline-color
      (method (value)
              (call configure super 'outline value))]
     [fill-color
      (method (value)
              (call configure super 'fill value))]
     [color
      (method (value)
                (call outline-color this value)
                (call fill-color this value))])))

(define <rectangle>
  (class (x1 y1 x2 y2)
    ()
    (<polygon-2> x1 y1 x1 y2 x2 y2 x2 y1)
    ()))

(define <square>
  (class (x y size)
    ()
    (<rectangle> x y (+ x size) (+ y size))
    ()))

(define <framedsquare>
  (class (x y size)
    ()
    (<square> x y size)
    ([color
      (method (value)
              (call fill-color super value))])))

(define <polygon-2>
  (class points
    ()
    (<filled-shape>)
    ([draw (method () (call fill-shape super 'polygon points))])))