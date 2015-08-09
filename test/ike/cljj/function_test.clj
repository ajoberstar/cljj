(ns ike.cljj.function-test
	(:require [clojure.test :refer :all]
            [ike.cljj.function :refer :all])
  (:import (java.util.function Supplier Consumer Function BiFunction)))

(deftest supplier
  (let [f (fn->sam (fn [] "supplied") Supplier)]
    (is (= "supplied" (.get f)))))

(deftest consumer
  (let [p (promise)
        f (fn->sam (fn [x] (deliver p x)) Consumer)]
    (is (nil? (.accept f 123)))
    (is (= 123 @p))))

(deftest function
  (let [f (fn->sam (fn [x] (* x 2)) Function)]
    (is (= 4 (.apply f 2)))))

(deftest bifunction
  (let [f (fn->sam (fn [x y] (* x y)) BiFunction)]
    (is (= 6 (.apply f 2 3)))))
