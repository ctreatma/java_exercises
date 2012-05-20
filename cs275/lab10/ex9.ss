(require "tree.ss")

(define traverse-kq
  (lambda (tree leaf-visitor)
    (let try ([tree-list (list tree)]
              [k leaf-visitor]
              [q (lambda () 'done)])
      (if (null? tree-list) q
          (let* ([current (car tree-list)]
                 [rest (cdr tree-list)]
                 [new-k (lambda (x y)
                          (try (interior-node-children x) k y))]
                 [new-q (try rest k q)])
            (if (leaf-node? current)
                (k current new-q)
                (new-k current new-q)))))))

(define leaf-visitor-q
  (lambda (z q)
     (printf "~s~n" (leaf-node-value z))
     q))

(define leaf-visitor-q1
  (lambda (z q)
     (printf "~s~n" (leaf-node-value z))
     q))
