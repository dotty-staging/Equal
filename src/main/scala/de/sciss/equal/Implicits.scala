/*
 * Implicits.scala
 * (Equal)
 *
 * Copyright (c) 2016-2018 Hanns Holger Rutz. All rights reserved.
 *
 * This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.equal

import scala.language.experimental.macros

object Implicits {
  implicit class TripleEquals[A](a: A) {
    def === [B >: A](b: B): Boolean = macro Macros.equalsImpl   [A, B]
    def !== [B >: A](b: B): Boolean = macro Macros.notEqualsImpl[A, B]
  }
}