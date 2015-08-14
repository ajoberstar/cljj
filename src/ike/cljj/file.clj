(ns ike.cljj.file
  (:refer-clojure :exclude [list])
  (:require [ike.cljj.stream :as stream]
            [ike.cljj.function :refer [defsam]])
  (:import (java.nio.file Path Paths Files CopyOption LinkOption OpenOption FileVisitOption)
           (java.nio.file.attribute FileAttribute)
           (java.nio.charset StandardCharsets)
           (java.io File)
           (java.net URI)))

(defprotocol Pathable
  (as-path [x]))

(extend-protocol Pathable
  ;;nil
  ;;(as-path [x] nil)
  String
  (as-path [x] (Paths/get x))
  Path
  (as-path [x] x)
  File
  (as-path [x] (.toPath x))
  URI
  (as-path [x] (Paths/get x)))

(defn exists?
  [path]
  (Files/exists (as-path path) (into-array LinkOption [])))

(defn file?
  [path]
  (Files/isRegularFile (as-path path) (into-array LinkOption [])))

(defn dir?
  [path]
  (Files/isDirectory (as-path path) (into-array LinkOption [])))

(defn link?
  [path]
  (Files/isSymbolicLink (as-path path)))

(defn same?
  [x y]
  (Files/isSameFile (as-path x) (as-path y)))

(defn size
  [path]
  (Files/size (as-path path)))

(defn list
  [path]
  (Files/list (as-path path)))

(defn walk
  ([path] (Files/walk (as-path path) (into-array FileVisitOption [])))
  ([path max-depth] (Files/walk (as-path path) max-depth (into-array FileVisitOption []))))

(defn make-dir
  ([path] (make-dir path false))
  ([path parents?]
   (let [path (as-path path)]
     (if parents?
       (Files/createDirectories path (into-array FileAttribute []))
       (Files/createDirectory path (into-array FileAttribute []))))))

(defn make-file
  [path]
  (Files/createFile (as-path path) (into-array FileAttribute [])))

(defn make-link
  [path target]
  (Files/createSymbolicLink (as-path path) (as-path target) (into-array FileAttribute [])))

(defn temp-dir
  ([prefix]
   (Files/createTempDirectory prefix (into-array FileAttribute [])))
  ([dir prefix]
   (Files/createTempDirectory (as-path dir) prefix (into-array FileAttribute []))))

(defn temp-file
  ([prefix suffix]
   (Files/createTempFile prefix suffix (into-array FileAttribute [])))
  ([dir prefix suffix]
   (Files/createTempFile (as-path dir) prefix suffix (into-array FileAttribute []))))

(declare delete)

(def ^:private delete-visitor
  (proxy [SimpleFileVisitor] []
    (visitFile [file attrs] (delete file))
    (postVisitDirectory [dir e] (delete dir))))

(defn delete
  ([path] (delete false))
  ([path recurse]
   (let [path (as-path path)]
    (if (and recurse (dir? path))
      (Files/walkFileTree path delete-visitor)
      (Files/deleteIfExists path)))))

(defn move
  [path target]
  (Files/move (as-path path) (as-path target) (into-array CopyOption [])))

(defn copy
  ([from to] (copy from to false))
  ([from to recurse]
   (if (and recurse (dir? path))
    (with-open [stream (-> from walk stream/stream-seq)]
      (doseq [ffile stream]
        (let [rpath (.relativize from ffile)
              tfile (.resolve to rpath)]
          (copy ffile tfile))))
    (Files/copy (as-path from) (as-path to) (into-array CopyOption [])))))

(defn read-bytes
  [path]
  (Files/readAllBytes (as-path path)))

(defn read-lines
  [path]
  (Files/lines (as-path path) (StandardCharsets/UTF_8)))

(defn write-bytes
  [path bytes]
  (Files/write (as-path path) bytes (into-array OpenOption [])))

(defn write-lines
  [path lines]
  (Files/write (as-path path) lines (StandardCharsets/UTF_8) (into-array OpenOption [])))
