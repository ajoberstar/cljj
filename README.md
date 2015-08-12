# ike.cljj

[![Maintainer Status](http://stillmaintained.com/ike-tools/ike.cljj.svg)](http://stillmaintained.com/ike-tools/ike.cljj)
[![Travis](https://img.shields.io/travis/ike-tools/ike.cljj.svg?style=flat-square)](https://travis-ci.org/ike-tools/ike.cljj)
[![GitHub license](https://img.shields.io/github/license/ike-tools/ike.cljj.svg?style=flat-square)](https://github.com/ike-tools/ike.cljj/blob/master/LICENSE)

[![Clojars Project](http://clojars.org/ike/ike.cljj/latest-version.svg)](http://clojars.org/ike/ike.cljj)

## Why do you care?

Clojure provides some nice Java interop features, but they are missing clean support for newer APIs added to
Java 7 and 8. 

## What is it?

ike.cljj is a Clojure library of wrappers around Java APIs.

### Current Support

* Clojure functions to arbitrary Single Abstract Method (SAM) types (`ike.cljj.function` -> `java.util.function` and others)
* Streams (`ike.cljj.stream` -> `java.util.stream`)
* MethodHandles (`ike.cljj.invoke` -> `java.lang.invoke`)

### Planned Support

* NIO File APi (`ike.cljj.file` -> `java.nio.file`)

## Usage

**NOTE:** ike.cljj depends on APIs added in Java 7 and/or 8.

* [Release Notes](https://github.com/ajoberstar/semver-vcs/releases)

For now, directly looking at the source and docstrings is the best bet. There's not a lot there.

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
