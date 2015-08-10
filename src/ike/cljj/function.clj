(ns ike.cljj.function
  "Support for using Clojure functions in Java APIs requiring
  a SAM (Single Abstrace Method) interface.

  Currently uses java.lang.invoke.MethodHandleProxies to
  convert an method handle to a SAM type. The method handle
  generated for the IFn will translate to something akin to
  the below (using method handle notation for signatures):

  (-> SAM call
      (Object...)Object[]
      RT.seq(Object[])ISeq
      IFN.applyTo(ISeq)Object)

  This is not intended to be highly performant, but, as it
  has not been benchmarked, may or may not be. It is mainly
  intended to provide a more convenient interop with Java
  APIs that take SAM arguments."
  (:require [ike.cljj.invoke :as invoke]
            [clojure.tools.macro :as macro])
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

(defn sam* [sami f]
  "Converts an IFn to an arbitrary SAM (Single Abstract Method)
  interface."
  (invoke/proxy-sam sami (fn->handle f)))

(defmacro sam
  "Defines an anonymous SAM instance implementing the SAM interface
  in the first argument. Remaining arguments are treated as if to
  clojure.core/fn."
  [sami & forms]
  `(sam* ~sami (fn ~forms)))

(defmacro defsam
  "Defines a SAM instance bound to a Var named using the symbol
  passed in the first argument. Optional docstring and/or 
  attribute map can be passed next. The following argument will
  be the SAM interface that should be implemented. The remaining
  arguments are treated as if to clojure.core/fn."
  {:arglists '([name docstring? attr-map? sami & fdecl])}
  [name & forms]
  (let [[name [sami & forms]] (macro/name-with-attributes name forms)]
    `(def ~name (sam ~sami ~@forms))))
