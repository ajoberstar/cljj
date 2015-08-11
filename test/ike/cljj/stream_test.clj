(ns ike.cljj.stream-test
  (:require [clojure.test :refer :all]
            [ike.cljj.stream :refer :all])
  (:import (java.util.stream IntStream)))

(deftest stream-reduce
  (let [stream (IntStream/range 0 10)]
    (is (= 45 (reduce + stream)))))

(deftest stream-into
  (let [stream (IntStream/range 0 5)]
    (is (= [0 1 2 3 4] (into [] stream)))))

(deftest stream-transduce
  (let [stream (IntStream/range 0 10)]
    (is (= 25 (transduce (filter odd?) + stream)))))

(deftest stream-sequence
  (let [stream (IntStream/range 0 10)
        sseq (sequence (filter odd?) (stream-seq stream))]
    (is (seq? sseq))
    (is (= '(1 3 5 7 9) sseq))))

(deftest stream-eduction
  (let [stream (IntStream/range 0 10)
        educ (eduction (filter odd?) (stream-seq stream))]
    (is (seq? educ))
    (is (= 25 (reduce + 0 educ)))))