package ru.johnspade.tgbot.callbackdata.annotated

import kantan.csv.{DecodeError, RowDecoder}
import magnolia._

import scala.util.Try

object MagnoliaRowDecoder {
  type Typeclass[T] = RowDecoder[T]

  def combine[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] =
    (e: Seq[String]) =>
      ctx.constructEither { p =>
        p.typeclass.decode(Seq(e(p.index)))
      }
        .left
        .map(_.head)

  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] =
    (e: Seq[String]) =>
      if (e.isEmpty)
        Left(DecodeError.OutOfBounds(0))
      else {
        (for {
          typeId <- Try(e.head.toInt).toOption
          subtype <- ctx.subtypes.find(_.annotations.contains(TypeId(typeId)))
        } yield subtype.typeclass.decode(e.tail))
          .toRight(DecodeError.TypeError(s"Invalid type tag: ${e.head}"))
          .flatMap(identity)
      }

  implicit def deriveRowDecoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
