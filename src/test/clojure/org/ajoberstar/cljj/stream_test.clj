(ns org.ajoberstar.cljj.stream-test
  (:require [clojure.test :refer :all]
            [org.ajoberstar.cljj.stream :refer :all]
            [org.ajoberstar.cljj.function :refer [sam*]])
  (:import [java.util ArrayList]
           [java.util.function Predicate UnaryOperator]))

(defn range-stream [from to]
  (-> (range from to)
      (ArrayList.)
      (.stream)))

(deftest stream-reduce
  (let [stream (range-stream 0 10)]
    (is (= 45 (reduce + stream)))))

(deftest stream-into
  (let [stream (range-stream 0 5)]
    (is (= [0 1 2 3 4] (into [] stream)))))

(deftest stream-transduce
  (let [stream (range-stream 0 10)]
    (is (= 25 (transduce (filter odd?) + stream)))))

(deftest stream-sequence
  (let [stream (range-stream 0 10)
        sseq (sequence (filter odd?) (stream-seq stream))]
    (is (seq? sseq))
    (is (= '(1 3 5 7 9) sseq))))

(deftest stream-eduction
  (let [stream (range-stream 0 10)
        educ (eduction (filter odd?) (stream-seq stream))]
    (is (= 25 (reduce + 0 educ)))))

(deftest stream-prefiltered-into
  (let [stream (-> (range-stream 0 10)
                   (.filter (sam* Predicate even?))
                   (.map (sam* UnaryOperator (fn [x] (int (* x 3))))))]
    (is (= [0 6 12 18 24] (into [] stream)))))
