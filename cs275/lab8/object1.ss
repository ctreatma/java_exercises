(require "smm.ss")

(define <point-class>
  (class (x y)
	 ([visible #f])
	 (<object>)
	 ([Location?
	   (method ()	
		   (if (numbers? x y)
		       (list x y)
		       (begin
			 (printf "I don't know~%")
			 (list x y))))]
	  [Visible?
	   (method ()
		   visible)]
	  [MoveTo
	   (method (newx newy)
		   (set! x newx)
		   (set! y newy)
		   (list x y))]
	  [MoveRel
	   (method (newx newy)
		   (if (numbers? x y newx newy) 
		       (begin
			 (set! x (+ x newx))
			 (set! y (+ y newy))
			 (list x y))
		       (error 'MoveRel "no move to non-numeric position")))]
	  [SetVisible
	   (method (value)
		   (set! visible (if value #t #f))
		   visible)])))

(define <colored-point>
  (class (x y color)
    ()
    (<point-class> x y)
    ([Color?
       (method ()
         color)]
     [Paint
       (method (hue)
         (set! color hue)
	 color)])))

(define numbers? (lambda n (andmap number? n)))

