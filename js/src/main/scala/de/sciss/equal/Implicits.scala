/*
 * Implicits.scala
 * (Equal)
 *
 * Copyright (c) 2016-2020 Hanns Holger Rutz. All rights reserved.
 *
 * This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.equal

object Implicits {
  /** Note: on Scala.js, the check is trivial and does not add type-safety */
  implicit class TripleEquals[A](a: A) {
    @inline def === [B >: A](b: B): Boolean = a == b
    @inline def !== [B >: A](b: B): Boolean = a != b
  }
}