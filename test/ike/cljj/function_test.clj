(ns ike.cljj.function-test
	(:require [clojure.test :refer :all]
            [ike.cljj.function :refer :all])
  (:import (java.util.function Supplier)))

(deftest supplier
  (let [f (fn->sam (fn [] "supplied") Supplier)]
    (is (= "supplied" (.get f)))))
