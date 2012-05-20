;; MrEd Graphics routines to be used with smm.ss/spm.ss
;; Based on R.P. Jones "graphics.ss"
;; Author - R. Salter, 11/02

(module graphics-mm mzscheme
  
  (require (lib "mred.ss" "mred")
           (lib "class.ss"))
  
  (define frame ())
  (define canvas ())
  (define dc ())
  (define bitmap ())
  
  (define-struct brush (color style))
  (define-struct pen (color width style))
  (define-struct font (size family style weight))
  
  (define no-pen (make-pen "black" 0 'transparent))
  (define no-brush (make-brush "black" 'transparent))
  (define black-pen (make-pen "black" 1 'solid))
  (define black-brush (make-brush "black" 'solid))
  (define default-font (make-font 10 'default 'normal 'normal))
  
  (define current-pen black-pen)
  (define current-brush black-brush)
  (define current-font default-font)
  
  (define init-graphics
    (case-lambda 
      (() (init-graphics 400 400 "Scheme Graphics"))
      ((x y) (init-graphics x y "Scheme Graphics"))
      ((x y name)
       (set! frame (instantiate frame% (name) (width x) (height y)))
       (set! bitmap (instantiate bitmap% (1000 1000)))
       (set! dc (instantiate bitmap-dc% (bitmap)))
       (set! canvas (instantiate canvas% (frame)
                      (paint-callback (lambda (canvas dc)
                                        (send dc draw-bitmap bitmap 0 0)))))
       (reset-graphics)
       (send frame show #t) 
       (void))))
  
  (define clear-graphics
    (lambda ()
      (send dc clear)
      (send canvas on-paint)))

  (define reset-graphics
    (lambda ()
      (send dc clear)
      (send canvas on-paint)
      (set! current-pen black-pen)
      (set! current-brush black-brush)
      (set! current-font default-font)))

  (define set-pen
    (case-lambda
      ((color) (set-pen-color! current-pen color))
      ((color width) (begin
                       (set-pen-color! current-pen color)
                       (set-pen-width! current-pen width)))
      ((color width style)
       (set! current-pen (make-pen color width style)))))
  
  (define set-brush
    (case-lambda
      ((color) (set-brush-color! current-brush color))
      ((color style)
       (set! current-brush (make-brush color style)))))
  
  (define set-font
    (case-lambda
      ((size) (set-font-size! current-font size))
      ((size family) (begin (set-font-size! current-font size)
                            (set-font-family! current-font family)))
      ((size family style) (begin (set-font-size! current-font size)
                                  (set-font-family! current-font family)
                                  (set-font-style! current-font style)))
      ((size family style weight)
       (set! current-font (make-font size family style weight)))))
  
  (define-syntax dc-draw
    (lambda (stx)
      (syntax-case stx (draw-text)
        [(_ draw-text arg ...)
         (syntax
          (begin
            (send dc set-font
                  (send the-font-list find-or-create-font
                        (font-size current-font) (font-family current-font)
                        (font-style current-font) (font-weight current-font)))
            (send dc draw-text arg ...)
            (send canvas on-paint)))]
        [(_ msg arg ...)
         (member (cadr (syntax-object->datum stx)) '(draw-lines draw-polygon draw-ellipse))
         (syntax 
          (begin
            (send dc set-brush 
                  (send the-brush-list find-or-create-brush (brush-color no-brush) (brush-style no-brush)))
            (send dc set-pen 
                  (send the-pen-list find-or-create-pen 
                        (pen-color current-pen) (pen-width current-pen) (pen-style current-pen)))
            (send dc msg arg ...)
            (send canvas on-paint)
	    (void)))])))
  
  (define-syntax dc-fill
    (lambda (stx)
      (syntax-case stx ()
        [(_ msg arg ...)
         (member (cadr (syntax-object->datum stx)) '(draw-polygon draw-ellipse))
         (syntax 
          (begin
            (send dc set-brush 
                  (send the-brush-list find-or-create-brush 
                        (brush-color current-brush) (brush-style current-brush)))
            (send dc set-pen 
                  (send the-pen-list find-or-create-pen 
                        (pen-color current-pen) (pen-width current-pen) (pen-style current-pen)))
            (send dc msg arg ...)
            (send canvas on-paint)
	    (void)))])))
  
  (define newsymbol
    (let ((count 0))
      (lambda ()
        (set! count (+ 1 count))
        (string->symbol (format "o~s" count)))))
  
  (define wait
    (lambda (milliseconds)
      (sleep/yield (/ milliseconds 1000.0))))
  
  (define hsv->xcolor
    (lambda l
      (let ((hue (* 6.0 (car l)))
            (sat (cadr l))
            (val (caddr l))
            (rgb->xcolor
             (lambda l
               (apply make-object (cons color% 
					(map (lambda (i)
					       (remainder (inexact->exact (floor (* 65535.0 i))) 256))
					     l))))))
        (if (= sat 0) ; if gray
            (rgb->xcolor val val val)
            (let* ((i (inexact->exact (floor hue)))
                   (frac (- hue i))
                   (p (* val (- 1.0 sat)))
                   (q (* val (- 1.0 (* sat frac))))
                   (t (* val (- 1.0 (* sat (- 1.0 frac))))))
              (case i
                ((0) (rgb->xcolor val t p))
                ((1) (rgb->xcolor q val p))
                ((2) (rgb->xcolor p val t))
                ((3) (rgb->xcolor p q val))
                ((4) (rgb->xcolor t p val))
                ((5) (rgb->xcolor val p q))
                (else "error")))))))
  
  (provide init-graphics reset-graphics clear-graphics
	   dc-fill dc-draw newsymbol set-font set-brush set-pen 
	   (struct brush (color style))
	   (struct pen (color width style))
	   (struct font (size family style weight))
  	   current-pen wait hsv->xcolor make-object point%)
  )
