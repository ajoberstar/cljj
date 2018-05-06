# ike.cljj

[![Clojars](https://img.shields.io/clojars/v/org.ajoberstar/ike.cljj.svg?style=flat-square)](http://clojars.org/org.ajoberstar/ike.cljj)
[![CircleCI](https://circleci.com/gh/ajoberstar/ike.cljj.svg?style=svg)](https://circleci.com/gh/ajoberstar/ike.cljj)

## Why do you care?

Clojure provides some nice Java interop features, but they are missing clean support for newer APIs added to
Java 7 and 8.

## What is it?

ike.cljj is a Clojure library of wrappers around Java APIs.

### Current Support

* Clojure functions to arbitrary Single Abstract Method (SAM) types (`ike.cljj.function` -> `java.util.function` and others)
* Streams (`ike.cljj.stream` -> `java.util.stream`)
* NIO2 File API (`ike.cljj.file` -> `java.nio.file`)

## Usage

**NOTE:** ike.cljj depends on APIs added in Java 7 and/or 8.

* [Release Notes](https://github.com/ajoberstar/ike.cljj/releases)

### Reducing on a Stream

Requiring the `ike.cljj.stream` namespace will add support for two main things:

- Turning a Stream into an ISeq with `stream-seq`.
- Reducing/Transducing/etc over a Stream (due to `CollReduce` impl)

```clojure
(let [stream (IntStream/range 0 10)]
  (= 25 (transduce (filter odd?) + stream))))
```

### Converting Clojure functions to Java SAMs

As of Java 8, there is powerful support in the Java language for lambdas and
method references. One of these features is that methods that accept an argument
which is a Single Abstract Method (SAM) interface can also accept any method reference
or lambda of the same shape/type.

This unforunately does not translate to Clojure users.

The `ike.cljj.function` namespace includes three main helpers for this:

* `sam*` - function converting a Clojure function to an arbitrary SAM interface
* `sam` - creating an anonymous SAM impl, as it were a Clojure function
* `defsam` - defining a named SAM impl, as if it were a Clojure function

**WARNING:** You may need type hints to avoid `IllegalAccessError` in Java 9+.

```clojure
(defsam my-sam
  java.util.function.Predicate
  [x]
  (= x "it matched"))

;; ignore that I'm not using ike.cljj.stream here
(-> (Stream/of "not a match" "it matched")
    (.filter my-sam)
    (.collect Collectors/toList)
```

Note that primitive streams require different SAM types.

```clojure
;; ignore that I'm not using ike.cljj.stream here
(-> (IntStream/range 0 10)
    (.filter (sam* java.util.function.IntPredicate odd?))
    (.collect Collectors/toList)
```

### File API

The NIO2 API for files is much improved over `java.io.File`, but has some headaches from
Clojure, namely the extensive use of varargs. The `ike.cljj.file` namespace provides wrappers
over these functions for two benefits:

- more natural variadic functions for Clojure use (no explicit `into-array` calls)
- flexible argument types using the `Pathish` protocol that already converts many common types
to `Path` (e.g. `String`, `File`, `URI`).

## Questions, Bugs, and Features

Please use the repo's [issues](https://github.com/ajoberstar/ike.cljj/issues)
for all questions, bug reports, and feature requests.

## Contributing

Contributions are very welcome and are accepted through pull requests.

Smaller changes can come directly as a PR, but larger or more complex
ones should be discussed in an issue first to flesh out the approach.

If you're interested in implementing a feature on the
[issues backlog](https://github.com/ajoberstar/ike.cljj/issues), add a comment
to make sure it's not already in progress and for any needed discussion.

## License

Copyright © 2015-2016 Andrew Oberstar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
