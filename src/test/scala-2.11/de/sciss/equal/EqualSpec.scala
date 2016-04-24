package de.sciss.equal

import org.scalatest.{FlatSpec, Matchers}

class EqualSpec extends FlatSpec with Matchers { me =>
  // disable ScalaTest's own === support
  override def convertToEqualizer[A](left: A): Equalizer[A] = null

  import Implicits._

  "Equal" should "work for known positive cases" in {
    assert(Vector(1, 2, 3) === List(1, 2, 3))
    assert("hello" !== "world")
    assert(4 !== 5)
    assert(Option("foo") !== None)
    assert(List(Some(1), None) === Seq(Some(1), None))  // true
  }

  it should "work for known negative cases" in {
    assertTypeError("""Vector(1, 2, 3) === Set(1, 2, 3)""")
    assertTypeError("""List(1, 2) === ((1, 2))""")
    assertTypeError("""4 !== 5f""")
    assertTypeError(""""hello" === Some("hello")""")
  }
}