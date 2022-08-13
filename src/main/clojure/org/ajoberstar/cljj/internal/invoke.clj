(ns org.ajoberstar.cljj.internal.invoke
  "Wrapper API for the java.lang.invoke API. Primarily this was limited
  to the methods functions needed to support ike.cljj.function."
  (:import (java.lang.invoke MethodType MethodHandles MethodHandleProxies MethodHandles$Lookup MethodHandle)
           (java.util List)
           (clojure.lang RT)))

(defn ^Class array-class
  "Gets the array class for the given class."
  [^Class clazz]
  (class (into-array clazz [])))

(defn ^MethodType method-type
  "Finds or creates a method type with the given return type 'ret'
  and parameters 'parms'."
  [^Class ret & parms]
  (let [^"[Ljava.lang.Class;" parm-array (into-array Class parms)]
    (MethodType/methodType ret parm-array)))

(defn ^MethodType unwrap
  "Converts a method type so that any parameters that were wrappers
  are now primitives."
  [^MethodType handle]
  (.unwrap handle))

(defn ^MethodType wrap
  "Converts a method type so that any parameters that were primitives
  are now wrappers."
  [^MethodType handle]
  (.wrap handle))

(def ^MethodHandles$Lookup lookup (.in (MethodHandles/publicLookup) RT))

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

(defn ^MethodHandle filter-args
  "Creates a new method handle that will pass each argument, in sequence,
  to each arity-1 filter handle. The results of each filter handle
  will replace the original arguments."
  ([^MethodHandle target & filters]
   (let [^"[Ljava.lang.invoke.MethodHandle;" filters (into-array MethodHandle filters)]
     (MethodHandles/filterArguments target 0 filters))))

(defn ^MethodHandle as-varargs
  "Converts the passed handle to a handle that accepts
  varargs. If no clazz is passed, the final parameter
  on the handle will be checked to see if it is an array
  type. If it is, that class will be used. If not, an
  Object[] class will be used."
  ([^MethodHandle handle]
   (let [^Class last-arg (-> handle .type .parameterArray last)]
     (if (.isArray last-arg)
       (as-varargs handle last-arg)
       (as-varargs handle (array-class Object)))))
  ([^MethodHandle handle ^Class clazz]
   (.asVarargsCollector handle clazz)))

(defn invoke
  "Invokes the method handle with any passed parameters."
  [^MethodHandle handle & parms]
  (let [^List parms (or parms ())]
    (.invokeWithArguments handle parms)))

(defn proxy-sam
  "Proxies a SAM (Single Abstrace Method) type by wrapping
  the passed method handle."
  [^Class sam ^MethodHandle handle]
  (MethodHandleProxies/asInterfaceInstance sam handle))
