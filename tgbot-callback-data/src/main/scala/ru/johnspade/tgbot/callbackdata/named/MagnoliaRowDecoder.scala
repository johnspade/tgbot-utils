package ru.johnspade.tgbot.callbackdata.named

import magnolia1.*
import ru.johnspade.zcsv.codecs.*
import ru.johnspade.zcsv.core.CSV
import zio.prelude.NonEmptyList

object MagnoliaRowDecoder extends Derivation[RowDecoder]:
  override def join[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] = value =>
    ctx
      .constructEither { param =>
        param.typeclass.decode(CSV.Row(NonEmptyList(value.l(param.index))))
      }
      .left
      .map(_.head)

  override def split[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] =
    (e: CSV.Row) =>
      if (e.l.tail.isEmpty) Left(DecodeError.OutOfBounds(0))
      else
        ctx.subtypes
          .find(_.typeInfo.short == e.l.head.x)
          .map(_.typeclass.decode(CSV.Row(nelFromListUnsafe(e.l.tail))))
          .getOrElse(Left(DecodeError.TypeError(s"Invalid type tag: ${e.l.head}")))
