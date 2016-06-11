(ns ike.cljj.internal.invoke-test
  (:require [clojure.test :refer :all]
            [ike.cljj.internal.invoke :refer :all]))

(deftest static-twoarg-method
  (let [mtype (method-type String CharSequence Iterable)
        handle (static-handle String "join" mtype)]
    (is (= "foo,bar" (invoke handle "," ["foo" "bar"])))))

(deftest static-varargs-method
  (let [mtype (method-type String CharSequence (array-class CharSequence))
        handle (static-handle String "join" mtype)]
    (is (= "foo,bar" (invoke handle "," "foo" "bar")))))

(deftest virtual-unbound-noarg-method
  (let [mtype (method-type String)
        handle (virtual-handle String "toUpperCase" mtype)]
    (is (= "CAR" (invoke handle "car")))))

(deftest virtual-bound-noarg-method
  (let [mtype (method-type String)
        handle (virtual-handle String "toUpperCase" mtype)]
    (is (= "CAR" (invoke (bind handle "car"))))))

(deftest virtual-unbound-onearg-method
  (let [mtype (unwrap (method-type Character Integer))
        handle (virtual-handle String "charAt" mtype)]
    (is (= \a (invoke handle "car" (int 1))))))
