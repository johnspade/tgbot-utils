package ru.johnspade.tgbot.callbackdata.annotated

import magnolia1.*
import ru.johnspade.zcsv.codecs.*
import ru.johnspade.zcsv.core.CSV

import scala.util.Try
import zio.prelude.NonEmptyList

object MagnoliaRowDecoder extends AutoDerivation[RowDecoder]:
  override def join[A](ctx: CaseClass[Typeclass, A]): Typeclass[A] = value =>
    ctx
      .constructEither { param =>
        param.typeclass.decode(CSV.Row(NonEmptyList(value.l(param.index))))
      }
      .left
      .map(_.head)

  override def split[A](ctx: SealedTrait[Typeclass, A]): Typeclass[A] =
    (e: CSV.Row) =>
      if (e.l.tail.isEmpty)
        Left(DecodeError.OutOfBounds(0))
      else
        (for {
          typeId  <- Try(e.l.head.x.toInt).toOption
          subtype <- ctx.subtypes.find(_.annotations.contains(TypeId(typeId)))
        } yield subtype.typeclass.decode(CSV.Row(nelFromListUnsafe(e.l.tail))))
          .toRight(DecodeError.TypeError(s"Invalid type tag: ${e.l.head}"))
          .flatMap(identity)
