(require "smm.ss")

(define <point-class>
  (class (x y)
	 ([visible #f]
          [resolution 1024]
          [numbers? (lambda n (andmap number? n))]
          [set-res (lambda (x) (if (number? x) (set! resolution x)
                                   (error 'set-res "invalid resolution")))]
          [hiding (lambda () (printf "Sorry, I'm hiding :)"))]
          [printloc (lambda ()
                    (if (numbers? x y)
		       (printf "I am at ~s" (list x y))
		       (begin
			 (printf "I don't know~%")
			 (list x y))))]
          [printvis (lambda () (if visible (printf "I am visible.")
                                   (printf "I am invisible.")))])
	 (<object>)
	 ([Location?
	   (method ()
                   (if visible (printloc) (hiding)))]
	  [Visible?
	   (method ()
		   (printvis))]
	  [MoveTo
	   (method (newx newy)
                   (if (numbers? newx newy)
                       (if (and (and (<= 0 newx) (> resolution newx))
                                (and (<= 0 newy) (> resolution newy)))
                           (begin
                             (set! x newx)
                             (set! y newy)
                             (if visible (printloc) (hiding)))
                           (error 'MoveTo "move outside coordinate range 0, 1023"))
                       (error 'MoveTo "no move to non-numeric position")))]
	  [MoveRel
	   (method (newx newy)
		   (if (numbers? x y newx newy) 
		       (begin
			 (set! x (+ x newx))
			 (set! y (+ y newy))
			 (if visible (printloc) (hiding)))
		       (error 'MoveRel "no move to non-numeric position")))]
	  [SetVisible
	   (method (value)
		   (set! visible (if value #t #f))
		   visible)])))

