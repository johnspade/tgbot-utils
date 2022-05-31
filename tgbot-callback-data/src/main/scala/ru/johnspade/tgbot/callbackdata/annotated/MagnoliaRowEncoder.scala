package ru.johnspade.tgbot.callbackdata.annotated

import ru.johnspade.csv3s.codecs.*
import magnolia1.*
import ru.johnspade.csv3s.core.CSV

object MagnoliaRowEncoder extends Derivation[RowEncoder]:
  override def join[A](ctx: CaseClass[Typeclass, A]): Typeclass[A] = value =>
    val encodedFields = ctx.params.foldLeft(Seq.empty[CSV.Field]) { (acc, p) =>
      acc ++ p.typeclass.encode(p.deref(value)).l
    }
    CSV.Row(encodedFields)

  override def split[A](ctx: SealedTrait[Typeclass, A]): Typeclass[A] =
    (d: A) =>
      ctx.choose(d) { sub =>
        val typeId = sub.annotations
          .collectFirst { case TypeId(id) =>
            id.toString
          }
          .getOrElse(throw new RuntimeException("TypeId not found"))
        CSV.Row(CSV.Field(typeId) +: sub.typeclass.encode(sub.cast(d)).l)
      }
