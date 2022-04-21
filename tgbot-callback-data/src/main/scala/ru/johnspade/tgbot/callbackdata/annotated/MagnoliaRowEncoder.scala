package ru.johnspade.tgbot.callbackdata.annotated

import ru.johnspade.zcsv.codecs.*
import magnolia1.*
import ru.johnspade.zcsv.core.CSV
import zio.prelude.NonEmptyList

object MagnoliaRowEncoder extends AutoDerivation[RowEncoder]:
  override def join[A](ctx: CaseClass[Typeclass, A]): Typeclass[A] = value =>
    val encodedFields = ctx.params.foldLeft(List.empty[CSV.Field]) { (acc, p) =>
      acc ++ p.typeclass.encode(p.deref(value)).l.toSeq
    }
    CSV.Row(nelFromListUnsafe(encodedFields))

  override def split[A](ctx: SealedTrait[Typeclass, A]): Typeclass[A] =
    (d: A) =>
      ctx.choose(d) { sub =>
        val typeId = sub.annotations
          .collectFirst { case TypeId(id) =>
            id.toString
          }
          .getOrElse(throw new RuntimeException("TypeId not found"))
        CSV.Row(NonEmptyList.cons(CSV.Field(typeId), sub.typeclass.encode(sub.cast(d)).l))
      }
