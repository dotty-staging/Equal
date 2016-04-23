/*
 * Macros.scala (scala-2.10)
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
import scala.reflect.macros.Context // blackbox

object Macros {
  def equalsImpl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: Context)(b: c.Expr[A]): c.Expr[Boolean] /* c.Tree */ = {
    import c.universe._
    // the macro does not do anything under Scala 2.10.
    // simply rewrite as `a == b`
    val q"$conv($a)" = c.prefix.tree
    val tree = q"$a == $b"
    c.Expr[Boolean](tree)
  }
}