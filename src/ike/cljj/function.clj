(ns ike.cljj.function
  (:import (java.util.function BiFunction Supplier Consumer Function)))

(defn lambda [f]
  (reify
    Supplier
    (get [_] (f))
    Consumer
    (accept [_ x] (f x))
    Function
    (apply [_ x] (f x))
    BiFunction
    (apply [_ x y] (f x y))))
