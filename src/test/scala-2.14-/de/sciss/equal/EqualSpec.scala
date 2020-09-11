package de.sciss.equal

import de.sciss.equal.EqualSpec._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.{IndexedSeq => Vec}

object EqualSpec {
  final case class ControlValues(seq: Vec[Float])

  sealed trait State { def id: Int }
  case object Stopped   extends State { final val id = 0 }
  case object Preparing extends State { final val id = 1 }

}
class EqualSpec extends AnyFlatSpec with Matchers { me =>
  // disable ScalaTest's own === support
  override def convertToEqualizer[A](left: A): Equalizer[A] = null

  import Implicits._

  "Equal" should "work for known positive cases" in {
    assert(Vector(1, 2, 3) === List(1, 2, 3))
    assert("hello" !== "world")
    assert(4 !== 5)
    assert(Option("foo") !== None)
    assert(Some("foo") !== Some("bar"))
    assert(Option(ControlValues(Vec(1f, 2f))) !== Option(ControlValues(Vec(1f, 3f))))
    val st: State = Stopped
    assert(st === Stopped)
    assert(List(Some(1), None) === Seq(Some(1), None))  // true
  }

  it should "work for known negative cases" in {
    assertTypeError("""Vector(1, 2, 3) === Set(1, 2, 3)""")
    assertTypeError("""List(1, 2) === ((1, 2))""")
    assertTypeError("""4 !== 5f""")
    assertTypeError(""""hello" === Some("hello")""")
    assertTypeError("""Some(1234) === Some("hello")""")
    assertTypeError("""def contains[A, B](in: Option[A], elem: B): Boolean = in === Some(elem)""")
  }

  it should "work with abstract types" in {
    def contains[A](in: Option[A], elem: A): Boolean = in === Some(elem)

    assert(contains(Some(123), 123))
  }
}