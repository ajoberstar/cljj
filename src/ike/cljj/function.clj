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

(defn process-defsam-form [decl form]
  (let [ndecl (update decl :fdecl rest)]
    (cond
      (string? form) (assoc-in ndecl [:meta :doc] form)
      (map? form) (update ndecl :meta merge form)
      (symbol? form) (reduced (assoc ndecl :sami (resolve form)))
      :else ndecl)))

(defn process-defsam [[name & forms]]
  (reduce process-defsam-form
          {:name name :meta (meta name) :fdecl forms}
          forms))

(defmacro defsam
  "Defines a SAM instance bound to a Var named using the symbol
  passed in the first argument. The instance will implement the
  SAM interface specified in the second argument. The remaining
  arguments are treated as if to clojure.core/fn."
  [& forms]
  (let [{name :name meta :meta sami :sami fdecl :fdecl} (process-defsam forms)
        name (with-meta name meta)
        asam (list* `sam sami fdecl)]
    `(def ~name ~asam)))
