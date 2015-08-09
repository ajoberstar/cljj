(ns ike.cljj.function
  (:import (java.util.function BiFunction Supplier Consumer Function)
           (java.lang.invoke MethodHandles MethodHandleProxies MethodType)))
  
(defn method-type [ret & parms]
    (let [parm-array (into-array Class parms)]
        (MethodType/methodType ret parm-array)))

(defmulti method-handle (fn [kind receiver name type] kind))

(defmethod method-handle :instance [kind receiver name type]
    (.bind (MethodHandles/publicLookup) receiver name type))

(defmethod method-handle :static [kind receiver name type]
    (.findStatic (MethodHandles/publicLookup) receiver name type))

(defmethod method-handle :virtual [kind receiver name type]
    (.findVirtual (MethodHandles/publicLookup) receiver name type))

(def array-type (class (into-array Object [])))

(def seq-handle
    (let [type (method-type clojure.lang.ISeq Object)]
        (method-handle :static clojure.lang.RT "seq" type)))

(def apply-handle
    (let [type (method-type Object clojure.lang.ISeq)]
        (method-handle :virtual clojure.lang.IFn "applyTo" type)))

(defn invoke-handle [f]
    (.asVarargsCollector (MethodHandles/collectArguments (.bindTo apply-handle f) 0 seq-handle) array-type))

(defn sam-proxy [sam f]
    (MethodHandleProxies/asInterfaceInstance sam (invoke-handle f)))

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
