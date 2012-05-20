;; Generalized backtrack module using CPS

;;  Application-specific functions:
;;   (*initial*) -- initial empty solution
;;   (*complete?* psol)  -- true if psol is a  complete solution
;;   (*extensions* psol) -- creates set of all possible choices to extend psol
;;   (*first* choices) -- select next choice
;;   (*rest* choices) -- returns remaining choices
;;   (*empty?* choices) -- choices are exhausted
;;   (*extend* psol choice) -- extends solution by given choice
;;   (*legal?* psol) -- true if psol is a legal partial solution
;;   (*top-k* sol q) -- top-level success continuation
;;   (*do* psol) -- performs any required state change when recording new choice (e.g. graphics)
;;   (*undo* psol) -- retracts any required state change when backtracking

(module solve mzscheme
  
  (define solve
    (lambda (*initial* *complete?* *extensions* *first* 
                       *rest* *empty?* *extend* *legal?* *top-k* *do* *undo*)
      (let try ([psol (*initial*)]
                [choices (*extensions* (*initial*))]
                [k *top-k*]
                [q (lambda () 'failed)])
        (if (*complete?* psol) (k psol q)
            (if (*empty?* choices) (q)
                (let* ([new-psol (*extend* psol (*first* choices))]
                       [new-k (lambda (new-psol new-q)
                                (*do* new-psol)
                                (try new-psol (*extensions* new-psol) k new-q))]
                       [new-q (lambda () 
                                (*undo* new-psol)
                                (try psol (*rest* choices) k q))])
                  (if (*legal?* new-psol)
                      (new-k new-psol new-q)
                      (new-q))))))))
  (provide solve)
  )