package de.sciss.equal

import scala.reflect.macros.blackbox

object Macros {
  private[this] val positiveList = Set(
    "scala.Boolean", "scala.Int", "scala.Long", "scala.Float", "scala.Double", "java.lang.String"
    )
  private[this] val topLevelList = Set("scala.Option", "scala.Tuple2")
  private[this] val negativeList = Set(/* "java.lang.Object", "java.io.Serializable", */ "<refinement>")

  private[this] val skipList = Set(
    "scala.collection.TraversableLike", "scala.collection.generic.HasNewBuilder", "scala.collection.GenIterable",
    "scala.collection.IterableLike", "scala.collection.generic.GenericTraversableTemplate",
    "scala.collection.GenTraversableOnce", "scala.collection.Traversable",
    "scala.collection.GenTraversable", "scala.collection.immutable.Iterable",
    "scala.collection.GenIterableLike", "scala.collection.Parallelizable", "scala.collection.Iterable",
    "scala.collection.immutable.Traversable", "scala.collection.GenTraversableLike",
    "scala.collection.generic.FilterMonadic", "scala.collection.TraversableOnce",
    "java.io.Serializable", "scala.collection.AbstractTraversable",
    "scala.collection.GenTraversable", "scala.collection.GenSeq", "scala.collection.SeqLike",
    "scala.collection.AbstractIterable", "scala.collection.GenSeqLike", "java.lang.Object",
    "scala.collection.AbstractSeq",
    // Scala 2.13:
    "scala.collection.IterableOnceOps", "scala.collection.IterableOnce", "scala.collection.IterableOps"
  )

  def equalsImpl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: blackbox.Context)(b: c.Expr[A]): c.Tree =
    impl[A, B](c: c.type, invert = false)(b)

  def notEqualsImpl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: blackbox.Context)(b: c.Expr[A]): c.Tree =
    impl[A, B](c: c.type, invert = true)(b)

  private[this] val verbose = false

  private[this] def impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: blackbox.Context, invert: Boolean)
                                                            (b: c.Expr[A]): c.Tree = {
    import c.universe._
    val aTpe0 = weakTypeOf[A]
    val bTpe0 = weakTypeOf[B]

    sealed trait Cmp
    case object Ok    extends Cmp
    sealed trait Err  extends Cmp { def message: String }
//    case object Skip  extends Err {
//
//    }
    case class TooGeneric(a: Type, b: Type) extends Err {
      def message = s"Inferred type is too generic: `$a` vs `$b`"
    }
    case class TypeNumMismatch(a: Type, b: Type) extends Err {
      def message = s"Number of type parameters does not match: `$a` vs `$b`"
    }

    if (verbose) {
      println(s"Equal: typeOf[A] = $aTpe0; typeOf[B] = $bTpe0")
    }

    def checkTypes(aTpe: Type, bTpe: Type, level: Int): Cmp = {
      if (verbose) {
        println(s"--- LEVEL $level ---")
      }

      val aSym = aTpe.typeSymbol
      val bSym = bTpe.typeSymbol

      // skip identical abstract types, such as `A` in `Option[A]`
      if (aTpe =:= bTpe && !aSym.isClass && !bSym.isClass) {
        if (verbose) {
          println(s"matching abstract types $aTpe and $bTpe")
        }
        return Ok
      }

      def mkBase(in: Type): Set[ClassSymbol] =
        in.baseClasses.iterator.collect {
          case sym if sym.isClass => sym.asClass
        } .toSet

      val baseA = mkBase(aTpe)
      val baseB = mkBase(bTpe)

      val baseCommon0 = baseA intersect baseB
      val baseCommon = baseCommon0.filterNot { sym =>
        skipList.contains(sym.fullName)
      }

      val namesB: Set[String] = baseCommon.map(_.fullName)

      if (verbose) println(namesB.mkString(s"namesB = ", "\n         ", ""))

      // if a primitive is inferred, we're good.
      if (namesB.intersect(positiveList).nonEmpty) {
        if (verbose) {
          println(s"matching primitive types $aTpe and $bTpe")
        }
        return Ok
      }

      // --- otherwise ---

      // exclude all such as scala.Product, scala.Equals
      val withoutTopLevel = namesB.filterNot { n =>
        val i = n.lastIndexOf('.')
        i == 5 && n.startsWith("scala") && !topLevelList.contains(n)
      }
      // exclude refinements and known Java types
      val excl = withoutTopLevel.diff(negativeList)

      if (verbose) println(namesB.mkString(s"excl   = ", "\n         ", ""))

      if (excl.isEmpty) {
        return TooGeneric(aTpe0, bTpe0) // Some(s"Inferred type is too generic: `$bTpe0`")
      }

      // now (crude) check type parameters
      def collectTypeArgs(in: Type): List[List[Type]] = {
        val all = in.dealias match {
          case x @ RefinedType(parents, _) => x.typeArgs :: parents.map(_.typeArgs)
          case x => x.typeArgs :: Nil
        }
        all.filter(_.nonEmpty)
      }

      val argsA = collectTypeArgs(aTpe)
      val argsB = collectTypeArgs(bTpe)

      if (verbose) {
        println(s"A: $aTpe: --- args ---")
        println(argsA)
        println(s"B: $bTpe: --- args ---")
        println(argsB)
      }

      val cand: Seq[Cmp] = argsA.flatMap { ap =>
        argsB.flatMap { bp =>
          if (ap.size != bp.size) {
            TypeNumMismatch(aTpe, bTpe) :: Nil // Some(s"Number of type parameters does not match: `$aTpe` (${ap.size}) vs `$bTpe` (${bp.size})") :: Nil
          } else {
            (ap zip bp).map { case (aTpe1, bTpe1) =>
              checkTypes(aTpe1, bTpe1, level = level + 1)
            }
          }
        }
      }

      val res: Cmp = if (cand.isEmpty || cand.contains(Ok)) {
        Ok
      } else {
        cand.collectFirst { case t: TooGeneric => t } .orElse (cand.headOption) .getOrElse(TooGeneric(aTpe, bTpe))
      }

      res
    }

    val opt = checkTypes(aTpe0, bTpe0, level = 0)
    opt match {
      case Ok =>
      case err: Err => c.abort(c.enclosingPosition, err.message)
    }

    // now simply rewrite as `a == b`
    val q"$conv($a)" = c.prefix.tree
    val tree = if (invert) q"$a != $b" else q"$a == $b"
    tree
  }
}
