(ns ike.cljj.function
  (:require [ike.cljj.invoke :as invoke])
  (:import (clojure.lang RT ISeq IFn)
           (java.util.function BiFunction Supplier Consumer Function)))

(def ^:private seq-handle
  (invoke/static-handle RT "seq" (invoke/method-type ISeq Object)))

(def ^:private apply-handle
  (invoke/virtual-handle IFn "applyTo" (invoke/method-type Object ISeq)))

(defn fn->handle [f]
  (-> (invoke/bind apply-handle f)
      (invoke/collect-args seq-handle)
      invoke/as-varargs))

(defn fn->sam [f sam]
  (invoke/proxy-sam sam (fn->handle f)))

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
