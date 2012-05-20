; tree.ss

;; define tree datatype using define-struct
;;   this is an n-ary tree in which only leaves have numerical
;;   it is isomorphic to a list of numbers

(module tree mzscheme
  (define-struct tree ())                           ; parent type (never instantiated)
  (define-struct (interior-node tree) (children))   ; children is a list of trees
  (define-struct (leaf-node tree) (value))          ; value is a number

  ; build-tree converts a list of numbers into a tree

  (define build-tree
    (lambda (l)
      (if (number? l) (make-leaf-node l)
          (make-interior-node (map build-tree l)))))

  ; sample tree

  (define tree1 (build-tree '((1 (2 (3 4)) 5) 6 (7 (8 9)))))
 
  (provide (all-defined))
  )






