(ns ike.cljj.function
  (:require [ike.cljj.invoke :as invoke])
  (:import (clojure.lang RT ISeq IFn)))

(def ^:private seq-handle
  (invoke/static-handle RT "seq" (invoke/method-type ISeq Object)))

(def ^:private apply-handle
  (invoke/virtual-handle IFn "applyTo" (invoke/method-type Object ISeq)))

(defn fn->handle [f]
  "Gets a method handle for IFn, wrapped appropriately to support
  variadic arguments"
  (-> (invoke/bind apply-handle f)
      (invoke/collect-args seq-handle)
      invoke/as-varargs))

(defn fn->sam [f sam]
  "Converts an IFn to an arbitrary SAM (Single Abstract Method)
  interface."
  (invoke/proxy-sam sam (fn->handle f)))
