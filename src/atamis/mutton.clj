(ns atamis.mutton
  (:require [clojure.spec.alpha :as s]
            [clojure.core.match :refer [match]]))

(s/def ::variable symbol?)

(s/def ::application (s/cat :fun ::term :arg ::term))

(s/def ::abstraction (s/cat :l #(= % 'l)
                            :bind ::variable
                            :body ::term))

(s/def ::term (s/or :application ::application
                    :variable ::variable
                    :abstraction ::abstraction))

(defn unconform
  "Takes a lambda term conformed to `::term` and converts it back
  to normal form."
  [conformed]
  (match conformed
    [:variable v] v
    [:abstraction m] (let [{:keys [bind body]} m]
                       `(~'l ~bind ~(unconform body)))
    [:application m] (let [{:keys [fun arg]} m]
                       (list (unconform fun) (unconform arg)))))

(defn simplify
  "Simplify a lambda calculus term. If the term can't be simplified,
  it returns the term as simplified as possible. Returns a value
  conformed to the `::term` spec. See `unconform` to turn it back
  into a normal term.

    x -> x
    (l x y) -> function that binds x in y body
    (x y) -> applies function x to body y
  "
  ([term] (simplify (s/conform ::term term) {}))
  ([term binds]
   (match term
     [:variable v] (if-let [val (binds v)] val term)
     [:abstraction m] (let [bind (:bind m)
                            body (simplify (:body m) binds)]
                        [:abstraction
                         {:bind bind
                          :body (simplify (:body m) binds)}])

     [:application m] (let [fun (simplify (:fun m) binds)
                            arg (simplify (:arg m) binds)]
                        (match fun
                          [:abstraction a]
                          (let [{:keys [bind body]} a]
                            (simplify body
                                      (assoc binds bind arg)))
                          :else [:application
                                 {:fun fun
                                  :arg arg}]))

     :else term)))
