(ns ike.cljj.file-test
  (:refer-clojure :exclude [list])
  (:require [clojure.test :refer :all]
            [ike.cljj.file :as file]
            [clojure.java.io :as io])
  (:import (java.nio.file Path Paths)
           (java.io File)
           (java.net URI)))

;; test clojure interop

(deftest spit-slurp-rount-trip
  (let [tmp (file/temp-file "spit-slurp" ".txt")
        text "onetwothree"]
    (spit tmp text)
    (is (= text (slurp tmp)))))

;; test ike.cljj.file API

(deftest as-path-accepts-nil
  (is (= nil (file/as-path nil))))

(deftest as-path-accepts-string
  (is (instance? Path (file/as-path "/etc/fstab"))))

(deftest as-path-accepts-file
  (is (instance? Path (file/as-path (File. "/etc/fstab")))))

(deftest as-path-accepts-path
  (is (instance? Path (file/as-path (Paths/get "/" (into-array String ["etc" "fstab"]))))))

(deftest as-path-accepts-uri
  (is (instance? Path (file/as-path (URI. "file:///etc/fstab")))))

(deftest path-accepts-single-arg
  (is (instance? Path (file/path "/etc"))))

(deftest path-accepts-multiple-args
  (is (instance? Path (file/path "/etc" "fstab"))))

(deftest make-dir-test
  (let [path (.resolve (file/temp-dir "make-dir") "the-dir")]
    (is (not (file/exists? path)))
    (file/make-dir path)
    (is (file/exists? path))
    (is (file/dir? path))
    (is (not (file/file? path)))
    (is (not (file/link? path)))))

(deftest make-file-test
  (let [path (.resolve (file/temp-dir "make-file") "the-file")]
    (is (not (file/exists? path)))
    (file/make-file path)
    (is (file/exists? path))
    (is (file/file? path))
    (is (not (file/dir? path)))
    (is (not (file/link? path)))))

(deftest make-link-to-file-test
  (let [tmp (file/temp-dir "make-link")
        path (.resolve tmp "the-link")
        target (file/temp-file tmp "target" ".txt")]
    (is (not (file/exists? path)))
    (file/make-link path target)
    (is (= target (file/read-link path)))
    (is (file/exists? path))
    (is (file/file? path))
    (is (not (file/dir? path)))
    (is (file/link? path))))

(deftest make-link-to-dir-test
  (let [tmp (file/temp-dir "make-link")
        path (.resolve tmp "the-link")
        target (file/temp-dir tmp "target")]
    (is (not (file/exists? path)))
    (file/make-link path target)
    (is (= target (file/read-link path)))
    (is (file/exists? path))
    (is (not (file/file? path)))
    (is (file/dir? path))
    (is (file/link? path))))

(deftest same-test
  (let [tmp (file/temp-dir "same")
        a (file/make-file (.resolve tmp "one"))
        b (file/make-link (.resolve tmp "two") (.resolve tmp "one"))
        c (file/make-file (.resolve tmp "three"))]
    (is (file/same? a b))
    (is (not (file/same? a c)))))

(deftest size-test
  (let [tmp (file/temp-file "size" ".txt")
        linesep-size (.length (System/lineSeparator))]
    (is (= 0 (file/size tmp)))
    (file/write-lines tmp ["one" "two"])
    (is (= (+ 6 (* 2 linesep-size)) (file/size tmp)))))

(deftest write-read-lines-round-trip
  (let [tmp (file/temp-file "lines" ".txt")
        test-lines ["one" "two" "three"]]
    (file/write-lines tmp test-lines)
    (is (= test-lines (into [] (file/read-lines tmp))))
    (is (= test-lines (file/read-all-lines tmp)))))

(deftest write-read-bytes-round-trip
  (let [tmp (file/temp-file "bytes" ".txt")
        test-bytes (.getBytes "onetwothree")]
    (file/write-bytes tmp test-bytes)
    (is (= (seq test-bytes) (seq (file/read-bytes tmp))))))

(deftest write-read-str-round-trip
  (let [tmp (file/temp-file "str" ".txt")
        test-str "onetwothree"]
    (file/write-str tmp test-str)
    (is (= test-str (file/read-str tmp)))))

(deftest write-append
  (let [tmp (file/temp-file "append" ".txt")
        lines1 ["one"]
        lines2 ["two"]]
    (file/write-lines tmp lines1)
    (file/write-lines tmp lines2 :append true)
    (is (= ["one" "two"] (file/read-all-lines tmp)))))

(deftest write-encoding
  (let [tmp (file/temp-file "str" ".txt")
        test-str "onetwothree"]
    (file/write-str tmp test-str :encoding "UTF-16")
    (is (not (= test-str (file/read-str tmp))))
    (is (= test-str (file/read-str tmp :encoding "UTF-16")))))

(deftest list-test
  (let [tmp (file/temp-dir "list")
        one (.resolve tmp "one")
        two (.resolve tmp "two")]
    (is (empty? (into [] (file/list tmp))))
    (file/make-file one)
    (file/make-dir two)
    (is (= #{one two} (into #{} (file/list tmp))))))

(deftest walk-test
  (let [tmp (file/temp-dir "walk")
        d1 (file/make-dir (.resolve tmp "1"))
        f2 (file/make-file (.resolve tmp "2"))
        d1f3 (file/make-file (.resolve tmp "1/3"))
        d1d4 (file/make-dir (.resolve tmp "1/4"))
        d1d5 (file/make-dir (.resolve tmp "1/5"))
        d1d4f6 (file/make-file (.resolve tmp "1/4/6"))]
    ;; how to test the ordering?
    (is (= #{tmp f2 d1 d1d5 d1d4 d1d4f6 d1f3} (into #{} (file/walk tmp))))
    (is (= #{tmp f2 d1 d1d5 d1d4 d1f3} (into #{} (file/walk tmp 2))))))

(deftest move-test
  (let [tmp (file/temp-dir "move")
        one (file/make-file (.resolve tmp "one"))
        two (.resolve tmp "two")]
    (file/write-lines one ["a" "b"])
    (is (not (file/exists? two)))
    (file/move one two)
    (is (not (file/exists? one)))
    (is (file/exists? two))
    (is (= ["a" "b"] (into [] (file/read-lines two))))))

(deftest delete-recursive-test
  (let [tmp (file/temp-dir "delete")
        d1 (file/make-dir (.resolve tmp "1"))
        f2 (file/make-file (.resolve tmp "2"))
        d1f3 (file/make-file (.resolve tmp "1/3"))
        d1d4 (file/make-dir (.resolve tmp "1/4"))
        d1d5 (file/make-dir (.resolve tmp "1/5"))
        d1d4f6 (file/make-file (.resolve tmp "1/4/6"))]
    (file/delete tmp :recurse true)
    (is (not (file/exists? tmp)))))

(deftest copy-recursive-test
  (let [tmp (file/temp-dir "copy")
        d1 (file/make-dir (.resolve tmp "1"))
        f2 (file/make-file (.resolve tmp "2"))
        d1f3 (file/make-file (.resolve tmp "1/3"))
        d1d4 (file/make-dir (.resolve tmp "1/4"))
        d1d5 (file/make-dir (.resolve tmp "1/5"))
        d1d4f6 (file/make-file (.resolve tmp "1/4/6"))
        tmp2 (file/temp-dir "copy2")]
    (is (not= (into #{} (file/walk tmp)) (into #{} (file/walk tmp2))))
    (file/copy tmp tmp2 :recurse true)
    (letfn [(walk-rel [dir] (let [xform (comp (drop 1) (map #(.relativize dir %)))]
                              (into #{} xform (file/walk dir))))]
      ;; how to test the ordering?
      (is (= (walk-rel tmp) (walk-rel tmp2))))))
