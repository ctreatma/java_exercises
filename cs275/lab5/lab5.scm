;Charles Treatman, Nick Miller, Dan Herr
;Lab 4
(require (lib "compat.ss"))

; tree.ss -- define tree datatype
; recognizer fo the empty tree

(define empty-tree?
  (lambda (tree) (null? tree)))

; constructor

(define makeEmptyTree
  (lambda () '()))

(define makeTree
  (lambda lst lst))

; destructors (accessors)

(define value
  (lambda (tree) (car tree)))

(define children
  (lambda (tree) (cdr tree)))

; other useful functions

(define number-of-children
  (lambda (tree)
    (length (children tree))))

(define leaf?
  (lambda (tree) (zero? (number-of-children tree))))

(define Empty (makeEmptyTree))
(define T1 (makeTree 50))
(define T2 (makeTree 22))
(define T3 (makeTree 10))
(define T4 (makeTree 5))
(define T5 (makeTree 17))
(define T6 (makeTree 73 T1 T2 T3))
(define T7 (makeTree 100 T4 T5))
(define T8 (makeTree 16 T6 T7))

(define fold
  (lambda (recur-case base-case lyst)
    (letrec ([help-fold
	  	(lambda (l)
		  (if (null? l)
             	      base-case
              	      (recur-case (car l) (help-fold (cdr l)))))])
	(help-fold lyst))))

;Exercise 1

(define childSum
  (lambda (tree)
    (apply + (map value (children tree)))))

;Ex. 2

(define allSum
  (lambda (tree)
    (+ (value tree) (fold + 0 (map allSum (children tree))))))

;Ex. 3

(define visitTree
  (lambda (f tree)
    (cond [(empty-tree? tree) (makeEmptyTree)]
          [else (apply makeTree (cons (f (value tree)) (map (lambda (x) (visitTree f x)) (children tree))))])))

;Ex. 4

(define upTree
  (lambda (tree)
    (visitTree (lambda (x) (+ x 1)) tree)))

;Ex. 5

(define visitTree-maker
  (lambda (f)
    (lambda (tree) (visitTree f tree))))


(define addOneToAll (visitTree-maker add1))

;Ex. 6

(define height
  (lambda (tree)
    (cond [(empty-tree? tree) 0]
          [else (+ 1 (fold max 0 (map height (children tree))))])))

;Ex. 7

(define preorder
  (lambda (tree)
    (cons (value tree) (fold append '() (map preorder (children tree))))))

(define snoc
  (lambda (l x)
    (append l (list x))))

(define postorder
  (lambda (tree)
    (snoc (fold append '() (map postorder (children tree)))(value tree))))

;Ex. 8

(define visitAndCollect
  (lambda (visit-fn co-fn base-value tree)
    (fold co-fn base-value (map visit-fn (preorder tree))))) 

;Ex. 9

(define allSum-vac
  (lambda (tree)
    (visitAndCollect (lambda (x) x) + 0 tree)))

(define preorder-vac
  (lambda (tree)
    (visitAndCollect (lambda (x) x) cons () tree)))

(define someValueIs
  (lambda (pred tree)
    (visitAndCollect pred (lambda (x y) (or x y)) #f tree)))

(define allValuesAre
  (lambda (pred tree)
    (visitAndCollect pred (lambda (x y) (and x y)) #t tree)))