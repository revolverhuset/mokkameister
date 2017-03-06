(ns mokkameister.random)

(defn rand-nth-weighted
  "Return a random element from the weighted collection.

  A weighted collection can be any seq of [choice, weight] elements.  The
  weights can be arbitrary numbers -- they do not need to add up to anything
  specific.

  Examples:
  (rand-nth-weighted [[:a 0.50] [:b 0.20] [:c 0.30]])
  (rand-nth-weighted {:a 10 :b 200})
  "
  [coll]
  (let [total (reduce + (map second coll))]
    (loop [i (rand total)
           [[choice weight] & remaining] (seq coll)]
      (if (>= weight i)
        choice
        (recur (- i weight) remaining)))))
