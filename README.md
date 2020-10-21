# Equal

[![Build Status](https://travis-ci.org/Sciss/Equal.svg?branch=main)](https://travis-ci.org/Sciss/Equal)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/equal_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/equal_2.13)

## statement

Equal is a small library for Scala to provide zero-overhead type-safe equals and not-equals operators `===` and `!==`.

It is (C)opyright 2016&ndash;2020 by Hanns Holger Rutz. All rights reserved. Equal is released under 
the [GNU Lesser General Public License](https://raw.github.com/Sciss/Equal/main/LICENSE) v2.1+ and comes with 
absolutely no warranties. To contact the author, send an e-mail to `contact at sciss.de`.

__Warning:__ This is not yet thoroughly tested.

__N.B.:__ Currently only Scala 2 on JVM performs extra type-safety checks, whereas Scala.js and Dotty use a
simple equality check without added type-safety. So make sure you always cross-compile your project with
Scala 2.12 or 2.13 on the JVM.

## requirements / installation

This project builds with sbt against Scala 2.13, 2.12, Dotty (JVM) and Scala 2.13 (JS).
The last version to support Scala 2.11 was v0.1.4.

To use the library in your project:

    "de.sciss" %% "equal" % v

The current version `v` is `"0.1.6"`

## contributing

Please see the file [CONTRIBUTING.md](CONTRIBUTING.md)

## documentation

You import the operators using

    import de.sciss.equal.Implicits._
    
Then the following compiles:

    Vector(1, 2, 3) === List(1, 2, 3)   
    "hello" !== "world"
    4 !== 5
    Option("foo") !== None
    List(Some(1), None) === Seq(Some(1), None)
    
    def contains[A](in: Option[A], elem: A): Boolean = in === Some(elem)
    
While the following does not:

    Vector(1, 2, 3) === Set(1, 2, 3)    // to-do: mysterious error message
    List(1, 2) === ((1, 2))
    4 !== 5f
    "hello" === Some("hello")
    
    def contains[A, B](in: Option[A], elem: B): Boolean = in === Some(elem)
