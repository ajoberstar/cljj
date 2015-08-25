# ike.cljj

[![Travis](https://img.shields.io/travis/ike-tools/ike.cljj.svg?style=flat-square)](https://travis-ci.org/ike-tools/ike.cljj)
[![GitHub license](https://img.shields.io/github/license/ike-tools/ike.cljj.svg?style=flat-square)](https://github.com/ike-tools/ike.cljj/blob/master/LICENSE)
[![Clojars](https://img.shields.io/clojars/v/ike/ike.cljj.svg?style=flat-square)](http://clojars.org/ike/ike.cljj)

## Why do you care?

Clojure provides some nice Java interop features, but they are missing clean support for newer APIs added to
Java 7 and 8.

## What is it?

ike.cljj is a Clojure library of wrappers around Java APIs.

### Current Support

* Clojure functions to arbitrary Single Abstract Method (SAM) types (`ike.cljj.function` -> `java.util.function` and others)
* Streams (`ike.cljj.stream` -> `java.util.stream`)
* MethodHandles (`ike.cljj.invoke` -> `java.lang.invoke`)

## Usage

**NOTE:** ike.cljj depends on APIs added in Java 7 and/or 8.

* [Release Notes](https://github.com/ajoberstar/semver-vcs/releases)

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

## Questions, Bugs, and Features

Please use the repo's [issues](https://github.com/ike-tools/ike.cljj/issues)
for all questions, bug reports, and feature requests.

## Contributing

Contributions are very welcome and are accepted through pull requests.

Smaller changes can come directly as a PR, but larger or more complex
ones should be discussed in an issue first to flesh out the approach.

If you're interested in implementing a feature on the
[issues backlog](https://github.com/ike-tools/ike.cljj/issues), add a comment
to make sure it's not already in progress and for any needed discussion.

## License

Copyright © 2015 Andrew Oberstar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
