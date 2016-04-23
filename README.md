# Equal

[![Flattr this](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=sciss&url=https%3A%2F%2Fgithub.com%2FSciss%2FEqual&title=Equal&language=Scala&tags=github&category=software)
[![Build Status](https://travis-ci.org/Sciss/Equal.svg?branch=master)](https://travis-ci.org/Sciss/Equal)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/equal_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/equal_2.11)

## statement

Equal is a small library for Scala to provide a zero-overhead type-safe equals operator `===`.

It is (C)opyright 2016 by Hanns Holger Rutz. All rights reserved. Equal is released under the [GNU Lesser General Public License](https://raw.github.com/Sciss/Equal/master/LICENSE) v2.1+ and comes with absolutely no warranties. To contact the author, send an email to `contact at sciss.de`.

__Warning:__ This is not yet thoroughly tested.

## requirements / installation

This project compiles against Scala 2.11, 2.10 using sbt 0.13.

To use the library in your project:

    "de.sciss" %% "equal" % v

The current version `v` is `"0.1.0"`

## contributing

Please see the file [CONTRIBUTING.md](CONTRIBUTING.md)

## documentation

You import the operator using

    import de.sciss.equal.Implicits._
    
Then the following compiles

    Vector(1, 2, 3) === List(1, 2, 3)           // true   
    "hello" === "world"                         // false
    4 === 5                                     // false
    Some("foo") === None                        // false
    List(Some(1), None) === Seq(Some(1), None)  // true
    
While the following does not:

    Vector(1, 2, 3) === Set(1, 2, 3)    // to-do: mysterious error message
    List(1, 2) === ((1, 2))
    4 === 5f
    "hello" === Some("hello")
