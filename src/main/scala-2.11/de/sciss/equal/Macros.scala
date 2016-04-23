/*
 * Macros.scala (scala-2.11)
 * (Equal)
 *
 * Copyright (c) 2016 Hanns Holger Rutz. All rights reserved.
 *
 * This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.equal

import scala.collection.breakOut
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Macros {
  private[this] val positiveList = Set("scala.Boolean", "scala.Int", "scala.Long", "scala.Float", "scala.Double",
    "scala.Option", "scala.Tuple2")
  private[this] val negativeList = Set("java.lang.Object", "java.io.Serializable", "<refinement>")

  def equalsImpl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: blackbox.Context)(b: c.Expr[A]): c.Tree = {
    import c.universe._
    def checkTypes(aTpe: Type, bTpe: Type): Unit = {
      val baseB = bTpe.baseClasses.collect {
        case sym if sym.isClass => sym.asClass
      }
      val names: Set[String] = baseB.map(_.fullName)(breakOut)

      // if a primitive is inferred, we're good. otherwise:
      if (names.intersect(positiveList).isEmpty) {
        // exclude all such as scala.Product, scala.Equals
        val withoutTopLevel = names.filterNot { n =>
          val i = n.lastIndexOf('.')
          i == 5 && n.startsWith("scala")
        }
        // exclude refinements and known Java types
        val excl = withoutTopLevel.diff(negativeList)
        if (excl.isEmpty) {
          c.abort(c.enclosingPosition, s"Inferred type is too generic: `${weakTypeOf[B]}`")
        }
      }

      // now (crude) check type parameters
      def collectArgs(in: Type): List[List[Type]] = {
        val all = in /* not in Scala 2.10: .dealias */ match {
          case RefinedType(parents, _) => parents.map(x => x.asInstanceOf[TypeApi].typeArgs)
          case x => x.typeArgs :: Nil
        }
        all.filter(_.nonEmpty)
      }

      val argsA = collectArgs(aTpe)
      val argsB = collectArgs(bTpe)

      for {
        ap <- argsA
        bp <- argsB
      } {
        println(s"a = $aTpe, ap = $ap, b = $bTpe, bp = $bp")
        if (ap.size != bp.size) {
          c.abort(c.enclosingPosition,
            s"Number of type parameters does not match: `$aTpe` (${ap.size}) vs `$bTpe` (${bp.size})")
        }

        (ap zip bp).foreach { case (aTpe1, bTpe1) =>
          checkTypes(aTpe1, bTpe1)
        }
      }
    }

    checkTypes(weakTypeOf[A], weakTypeOf[B])

    // now simply rewrite as `a == b`
    val q"$conv($a)" = c.prefix.tree
    q"$a == $b"
  }
}