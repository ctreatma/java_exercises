;; Scheme-- polygon base classes for lab 8
;; Author - R. Salter, 11/02

(module polygon mzscheme
  (require "graphics-mm.ss" "smm.ss")
  
  (define scale
    (lambda (x) (* x 20)))

  (define <shape>
    (class ()
      ([name (newsymbol)]
       [base-color 0])
      (<object>)
      ([draw-shape
	(method (type . args) 
	   (case type
	     [(text) (dc-draw draw-text (car args) (scale (cadr args)) (scale (caddr args)))]
	     [(lines) (dc-draw draw-lines (list-of-point% (car args)))]
	     [(oval) (dc-draw draw-ellipse (scale (car args)) (scale (cadr args))
			      (scale (- (caddr args) (car args)))
			      (scale (- (cadddr args) (cadr args))))]
	     [(polygon) (dc-draw draw-polygon (list-of-point% (car args)))]))]
       
       [fill-shape
	(method (type . args) 
	   (case type
	     [(oval) (dc-fill draw-ellipse (scale (car args)) (scale (cadr args))
			      (scale (- (caddr args) (car args)))
			      (scale (- (cadddr args) (cadr args))))]
	     [(polygon) (dc-fill draw-polygon (list-of-point% (car args)))]))]
       [configure
	(method (type . args)
	   (case type
	     [(width) (set-pen (pen-color current-pen) (scale (car args)))]
	     [(outline) (set-pen (car args))]
	     [(fill) (set-brush (car args))]))]
       [animate-hsv
        (method (steps)
                (let loop ((i 0))
                  (when (< i steps)
                    (call color this
                          (hsv->xcolor (exact->inexact (/ i steps)) 1.0 1.0))
		    (call draw this)
                    (wait 50)
                    (loop (add1 i)))))]
       [color (method (value) (call configure this 'fill value))])))
  
  (define <text>
    (class (x y string)
      ()
      (<shape>)
      ([draw (method () (call draw-shape super 'text string x y))]
       [font (method (size) (set-font size))])))
  
  (define <poly-line>
    (class points
      ()
      (<shape>)
      ([draw
        (method () (call draw-shape super 'lines points))])))

  (define <line>
    (class (x1 y1 x2 y2)
      ()
      (<poly-line> x1 y1 x2 y2)
      ()))
  
  (define <filled-shape>
    (class ()
      ()
      (<shape>)
      ([width
        (method (value)
                (call configure super 'width (exact->inexact value)))])))
  
  (define <polygon>
    (class points
      ()
      (<filled-shape>)
      ([draw (method () (call fill-shape super 'polygon points))])))
  
  (define list-of-point%
    (lambda (points)
      (if (null? points) ()
	  (cons (make-object point% (scale (car points)) (scale (cadr points)))
		(list-of-point% (cddr points))))))

  (provide (all-defined-except list-of-point%))
  )
