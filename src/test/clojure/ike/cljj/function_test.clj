(ns ike.cljj.function-test
  (:require [clojure.test :refer :all]
            [ike.cljj.function :refer :all])
  (:import (java.util.function Supplier Consumer Function BiFunction)))

(deftest supplier
  (let [^Supplier f (sam* Supplier (fn [] "supplied"))]
    (is (= "supplied" (.get f)))))

(deftest consumer
  (let [p (promise)
        ^Consumer f (sam* Consumer (fn [x] (deliver p x)))]
    (is (nil? (.accept f 123)))
    (is (= 123 @p))))

(deftest function
  (let [^Function f (sam* Function (fn [x] (* x 2)))]
    (is (= 4 (.apply f 2)))))

(deftest bifunction
  (let [^BiFunction f (sam* BiFunction (fn [x y] (* x y)))]
    (is (= 6 (.apply f 2 3)))))

(deftest sam-macro-supplier
  (let [^Supplier f (sam Supplier [] "supplied")]
    (is (= "supplied" (.get f)))))

(deftest defsam-macro-supplier
  (defsam f1 Supplier [] "supplied")
  (is (= "supplied" (.get ^Supplier f1))))

(deftest defsam-docstring-macro-supplier
  (defsam f2 "It worked!" Supplier [] "supplied")
  (is (= "supplied" (.get ^Supplier f2)))
  (is (= "It worked!" (:doc (meta #'f2)))))
