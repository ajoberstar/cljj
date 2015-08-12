(ns ike.cljj.stream
  (:require [clojure.core.protocols :refer [CollReduce coll-reduce]]
            [ike.cljj.function :refer [sam]])
  (:import (java.util.stream BaseStream Stream StreamSupport)
           (java.util.function BiFunction BinaryOperator)
           (java.util Spliterator)))

(defn stream-seq
  "Converts a stream to a Seq. Unfortunately clojure.core/seq integration
  isn't possible due to two things:
  - BaseStream/Stream don't implement Iterable (though they have an iterator() method)
  - No protocols exist that can make something work with clojure.core/seq"
  [^BaseStream stream]
  (-> stream .iterator iterator-seq))

(defprotocol Streamable
  "Extend this protocol if your type should be able to be converted to a Stream."
  (to-stream [x]))

(extend-protocol Streamable
  nil
  (to-stream [_] (Stream/empty))
  Spliterator
  (to-stream [spliterator] (StreamSupport/stream spliterator false)))

(defn- ^Spliterator early-spliterator
  "Wrap a Spliterator such that it will terminate early once
  the promise 'done' is realized."
  [^Spliterator spliterator done]
  (reify Spliterator
    (characteristics [_]
      (-> spliterator
          .characteristics
          (bit-and-not Spliterator/SIZED Spliterator/SORTED)))
    (estimateSize [_]
      (if (realized? done)
        0
        (.estimateSize spliterator)))
    (tryAdvance [_ action]
      (and (not (realized? done))
           (.tryAdvance spliterator action)))
    (trySplit [_] nil)))

(defn- ^BaseStream early-stream
  "Wrap a Stream such that it will terminate early once
  the promise 'done' is realized."
  [^BaseStream stream done]
  (-> stream
      .spliterator
      (early-spliterator done)
      to-stream))

(defn- accumulator
  "Creates an accumulator function for use by Stream.reduce()
  that handles reduced values."
  [f done]
  (sam
    BiFunction
    [acc val]
    (let [ret (f acc val)]
      (if (reduced? ret)
        (do
          (deliver done true)
          @ret)
        ret))))

(extend-protocol CollReduce
  BaseStream
  (coll-reduce
    ([stream f] (coll-reduce stream f (f)))
    ([stream f init]
      (let [done (promise)]
        (with-open [estream (early-stream stream done)]
          (.reduce estream
                   init
                   (accumulator f done)
                   (sam BinaryOperator [_] (throw (Exception. "Combine should not be called.")))))))))
