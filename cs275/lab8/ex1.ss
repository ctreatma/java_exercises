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
                   (if (numbers? newx newy)
                       (if (and (and (<= 0 newx) (>= 1023 newx))
                                (and (<= 0 newy) (>= 1023 newy)))
                           (begin
                             (set! x newx)
                             (set! y newy)
                             (list x y))
                           (error 'MoveTo "move outside coordinate range 0, 1023"))
                       (error 'MoveTo "no move to non-numeric position")))]
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

(define numbers? (lambda n (andmap number? n)))

