package ru.johnspade.tgbot.callbackdata.annotated

import kantan.csv.RowEncoder
import magnolia._

object MagnoliaRowEncoder {
  type Typeclass[T] = RowEncoder[T]

  def combine[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] =
    (d: T) =>
      ctx.parameters.foldLeft(Seq.empty[String]) {
        (acc, p) => acc ++ p.typeclass.encode(p.dereference(d))
      }

  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] =
    (d: T) =>
      ctx.dispatch(d) { sub =>
        val typeId = sub.annotations.collectFirst {
          case TypeId(id) => id.toString
        }
          .getOrElse(throw new RuntimeException("TypeId not found"))
        typeId +: sub.typeclass.encode(sub.cast(d))
      }

  implicit def deriveRowEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
