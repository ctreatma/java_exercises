(require "smm.ss")

(define <opoint-class>
  (class (x y)
         ([origin-x 0]
          [origin-y 0]
          [visible #f])
	 (<object>)
	 ([WhereLoc
	   (method ()	
		   (if (numbers? x y)
		       (list x y)
		       (begin
			 (printf "I don't know~%")
			 (list x y))))]
	  [WhereGlob
           (method ()
                   (if (numbers? x y)
                       (list (+ x origin-x) (+ y origin-y))
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
	  [MoveOrigin
           	   (method (newox newoy)
                   (if (numbers? newox newoy)
                       (if (and (and (<= 0 newox) (>= 1023 newox))
                                (and (<= 0 newoy) (>= 1023 newoy)))
                           (begin
                             (set! origin-x newox)
                             (set! origin-y newoy)
                             (list origin-x origin-y))
                           (error 'MoveOrigin "move outside coordinate range 0, 1023"))
                       (error 'MoveOrigin "no move to non-numeric position")))]
         ;My method MoveRel, shifts the point by the input x and y values
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

