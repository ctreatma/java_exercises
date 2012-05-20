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

(define <red-square>
  (class (left top size)
    ()
    (<square> left top size)
    ([draw
      (method ()
	(call color super "red")
        (call draw super))])))

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

(define <polygon-2>
  (class points
    ()
    (<filled-shape>)
    ([draw (method () (call fill-shape super 'polygon points))])))

