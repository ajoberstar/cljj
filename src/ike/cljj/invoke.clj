(ns ike.cljj.invoke
  (:import (java.lang.invoke MethodType MethodHandles MethodHandleProxies MethodHandles$Lookup MethodHandle)))

(defn array-class
  "Gets the array class for the given class."
  [^Class clazz]
  (class (into-array clazz [])))

(defn ^MethodType method-type
  "Finds or creates a method type with the given return type 'ret'
  and parameters 'parms'."
  [ret & parms]
  (let [parm-array (into-array Class parms)]
    (MethodType/methodType ret parm-array)))

(defn ^MethodType unwrap
  "Convert all wrapper types to primitives."
  [^MethodType handle]
  (.unwrap handle))

(defn ^MethodType wrap
  "Convert all primitive types to wrappers."
  [^MethodType handle]
  (.wrap handle))

(def ^MethodHandles$Lookup lookup (MethodHandles/publicLookup))

(defn ^MethodHandle static-handle
  "Looks up a static method handle.
  - receiver is the class that holds the method
  - name is the name of the method
  - type is the method type"
  [^Class receiver ^String name ^MethodType type]
  (.findStatic lookup receiver name type))

(defn ^MethodHandle virtual-handle
  "Looks up a virtual method handle.
  - receiver is the class that holds the method
  - name is the name of the method
  - type is the method type"
  [^Class receiver ^String name ^MethodType type]
  (.findVirtual lookup receiver name type))

(defn ^MethodHandle bind
  "Binds 'arg' as the first parameter to 'handle'
  returning a new handle."
  [^MethodHandle handle ^Object arg]
  (.bindTo handle arg))

(defn ^MethodHandle collect-args
  ""
  ([^MethodHandle target ^MethodHandle combiner]
   (collect-args target 0 combiner))
  ([^MethodHandle target ^long index ^MethodHandle combiner]
   (MethodHandles/collectArguments target index combiner)))

(def ^:private array-type (class (into-array Object [])))

(defn ^MethodHandle as-varargs
  "Converts the passed handle to a handle that accepts
  varargs "
  ([^MethodHandle handle]
   (let [last-arg (-> handle .type .parameterArray last)]
     (if (.isArray last-arg)
       (as-varargs handle last-arg)
       (as-varargs handle (array-class Object)))))
  ([^MethodHandle handle ^Class clazz]
   (.asVarargsCollector handle clazz)))

(defn invoke
  "Invokes the method handle with any passed parameters."
  [^MethodHandle handle & parms]
  (.invokeWithArguments handle parms))

(defn proxy-sam
  "Proxies a SAM (Single Abstrace Method) type by wrapping
  the passed method handle."
  [^Class sam ^MethodHandle handle]
  (MethodHandleProxies/asInterfaceInstance sam handle))
