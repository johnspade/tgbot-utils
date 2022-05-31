package ru.johnspade.tgbot.callbackdata.annotated

import magnolia1.*
import ru.johnspade.csv3s.codecs.*
import ru.johnspade.csv3s.core.CSV

import scala.util.Try

object MagnoliaRowDecoder extends Derivation[RowDecoder]:
  override def join[A](ctx: CaseClass[Typeclass, A]): Typeclass[A] = value =>
    ctx
      .constructEither { param =>
        param.typeclass.decode(CSV.Row(Seq(value.l(param.index))))
      }
      .left
      .map(_.head)

  override def split[A](ctx: SealedTrait[Typeclass, A]): Typeclass[A] =
    (e: CSV.Row) =>
      if e.l.isEmpty then Left(DecodeError.OutOfBounds(0))
      else
        (for {
          typeId  <- Try(e.l.head.x.toInt).toOption
          subtype <- ctx.subtypes.find(_.annotations.contains(TypeId(typeId)))
        } yield subtype.typeclass.decode(CSV.Row(e.l.tail)))
          .toRight(DecodeError.TypeError(s"Invalid type tag: ${e.l.head}"))
          .flatMap(identity)
