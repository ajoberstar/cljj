(ns ike.cljj.file-test
  (:refer-clojure :exclude [list])
  (:require [clojure.test :refer :all]
            [ike.cljj.file :refer :all])
  (:import (java.nio.file Path Paths)
           (java.io File)
           (java.net URI)))

(deftest as-path-accepts-nil
  (is (= nil (as-path nil))))

(deftest as-path-accepts-string
  (is (instance? Path (as-path "/etc/fstab"))))

(deftest as-path-accepts-file
  (is (instance? Path (as-path (File. "/etc/fstab")))))

(deftest as-path-accepts-path
  (is (instance? Path (as-path (Paths/get "/" (into-array String ["etc" "fstab"]))))))

(deftest as-path-accepts-uri
  (is (instance? Path (as-path (URI. "file:///etc/fstab")))))

(deftest path-accepts-single-arg
  (is (instance? Path (path "/etc"))))

(deftest path-accepts-multiple-args
  (is (instance? Path (path "/etc" "fstab"))))

(deftest make-dir-test
  (let [path (.resolve (temp-dir "make-dir") "the-dir")]
    (is (not (exists? path)))
    (make-dir path)
    (is (exists? path))
    (is (dir? path))
    (is (not (file? path)))
    (is (not (link? path)))))

(deftest make-file-test
  (let [path (.resolve (temp-dir "make-file") "the-file")]
    (is (not (exists? path)))
    (make-file path)
    (is (exists? path))
    (is (file? path))
    (is (not (dir? path)))
    (is (not (link? path)))))

(deftest make-link-to-file-test
  (let [tmp (temp-dir "make-link")
        path (.resolve tmp "the-link")
        target (temp-file tmp "target" ".txt")]
    (is (not (exists? path)))
    (make-link path target)
    (is (exists? path))
    (is (file? path))
    (is (not (dir? path)))
    (is (link? path))))

(deftest make-link-to-dir-test
  (let [tmp (temp-dir "make-link")
        path (.resolve tmp "the-link")
        target (temp-dir tmp "target")]
    (is (not (exists? path)))
    (make-link path target)
    (is (exists? path))
    (is (not (file? path)))
    (is (dir? path))
    (is (link? path))))

(deftest same-test
  (let [tmp (temp-dir "same")
        a (make-file (.resolve tmp "one"))
        b (make-link (.resolve tmp "two") (.resolve tmp "one"))
        c (make-file (.resolve tmp "three"))]
    (is (same? a b))
    (is (not (same? a c)))))

(deftest size-test
  (let [tmp (temp-file "size" ".txt")]
    (is (= 0 (size tmp)))
    (write-lines tmp ["one" "two"])
    (is (= 8 (size tmp)))))

(deftest write-read-lines-round-trip
  (let [tmp (temp-file "lines" ".txt")
        test-lines ["one" "two" "three"]]
    (write-lines tmp test-lines)
    (is (= test-lines (into [] (read-lines tmp))))))

(deftest write-read-bytes-round-trip
  (let [tmp (temp-file "bytes" ".txt")
        test-bytes (.getBytes "onetwothree")]
    (write-bytes tmp test-bytes)
    (is (= (seq test-bytes) (seq (read-bytes tmp))))))

(deftest list-test
  (let [tmp (temp-dir "list")
        one (.resolve tmp "one")
        two (.resolve tmp "two")]
    (is (empty? (into [] (list tmp))))
    (make-file one)
    (make-dir two)
    (is (= #{one two} (into #{} (list tmp))))))

(deftest walk-test
  (let [tmp (temp-dir "walk")
        d1 (make-dir (.resolve tmp "1"))
        f2 (make-file (.resolve tmp "2"))
        d1f3 (make-file (.resolve tmp "1/3"))
        d1d4 (make-dir (.resolve tmp "1/4"))
        d1d5 (make-dir (.resolve tmp "1/5"))
        d1d4f6 (make-file (.resolve tmp "1/4/6"))]
    ;; this may be depending on device/platform specific encounter order
    (is (= [tmp f2 d1 d1d5 d1d4 d1d4f6 d1f3] (into [] (walk tmp))))
    (is (= [tmp f2 d1 d1d5 d1d4 d1f3] (into [] (walk tmp 2))))))

(deftest move-test
  (let [tmp (temp-dir "move")
        one (make-file (.resolve tmp "one"))
        two (.resolve tmp "two")]
    (write-lines one ["a" "b"])
    (is (not (exists? two)))
    (move one two)
    (is (not (exists? one)))
    (is (exists? two))
    (is (= ["a" "b"] (into [] (read-lines two))))))

(deftest delete-recursive-test
  (let [tmp (temp-dir "delete")
        d1 (make-dir (.resolve tmp "1"))
        f2 (make-file (.resolve tmp "2"))
        d1f3 (make-file (.resolve tmp "1/3"))
        d1d4 (make-dir (.resolve tmp "1/4"))
        d1d5 (make-dir (.resolve tmp "1/5"))
        d1d4f6 (make-file (.resolve tmp "1/4/6"))]
    (delete tmp true)
    (is (not (exists? tmp)))))

(deftest copy-recursive-test
  (let [tmp (temp-dir "copy")
        d1 (make-dir (.resolve tmp "1"))
        f2 (make-file (.resolve tmp "2"))
        d1f3 (make-file (.resolve tmp "1/3"))
        d1d4 (make-dir (.resolve tmp "1/4"))
        d1d5 (make-dir (.resolve tmp "1/5"))
        d1d4f6 (make-file (.resolve tmp "1/4/6"))
        tmp2 (temp-dir "copy2")]
    (is (not= (into #{} (walk tmp)) (into #{} (walk tmp2))))
    (copy tmp tmp2 true)
    (letfn [(walk-rel [dir] (let [xform (comp (drop 1) (map #(.relativize dir %)))]
                              (into #{} xform (walk dir))))]
      (is (= (walk-rel tmp) (walk-rel tmp2))))))
