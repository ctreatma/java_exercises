(require "tree.ss")

(define traverse-q
  (lambda (tree leaf-visitor)
    (let try ([tree-list (list tree)]
              [q (lambda () 'done)])
      (if (null? tree-list) q
          (let* ([current (car tree-list)]
                 [rest (cdr tree-list)]
                 [new-q (try rest q)])
            (if (leaf-node? current)
                (leaf-visitor current new-q)
                (try (interior-node-children current) new-q)))))))

(define leaf-visitor-q
  (lambda (z q)
     (printf "~s~n" (leaf-node-value z))
     q))

(define leaf-visitor-q1
  (lambda (z q)
     (printf "~s~n" (leaf-node-value z))
     q))


