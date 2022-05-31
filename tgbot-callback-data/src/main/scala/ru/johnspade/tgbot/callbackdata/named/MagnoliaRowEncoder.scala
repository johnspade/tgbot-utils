package ru.johnspade.tgbot.callbackdata.named

import magnolia1.*
import ru.johnspade.csv3s.codecs.*
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
        CSV.Row(CSV.Field(sub.typeInfo.short) +: sub.typeclass.encode(sub.cast(d)).l)
      }
