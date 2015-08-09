(ns ike.cljj.function-test
	(:require [clojure.test :refer :all]
            [ike.cljj.function :refer :all])
  (:import (java.util.function Supplier Consumer Function BiFunction)))

(deftest supplier
  (let [f (sam* Supplier (fn [] "supplied"))]
    (is (= "supplied" (.get f)))))

(deftest consumer
  (let [p (promise)
        f (sam* Consumer (fn [x] (deliver p x)))]
    (is (nil? (.accept f 123)))
    (is (= 123 @p))))

(deftest function
  (let [f (sam* Function (fn [x] (* x 2)))]
    (is (= 4 (.apply f 2)))))

(deftest bifunction
  (let [f (sam* BiFunction (fn [x y] (* x y)))]
    (is (= 6 (.apply f 2 3)))))
