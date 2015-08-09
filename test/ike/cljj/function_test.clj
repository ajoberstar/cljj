(ns ike.cljj.function-test
	(:require [clojure.test :refer :all]
		      [ike.cljj.function :refer :all]))

(defn promising-fn [p]
	(fn ([] (deliver p []))
		([x] (deliver p [x]) x)
		([x y] (deliver p [x y]) [x y])
		([x y & zs] (deliver p [x y zs] [x y zs]))))

(deftest supplier-works
	(let [p (promise)
		  f (promising-fn p)]
		(is (= [] (.get (sam-proxy java.util.function.Supplier f))))
		(is (= [] @p))))
