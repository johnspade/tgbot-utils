package ru.johnspade.tgbot.callbackdata

import ru.johnspade.tgbot.callbackdata.named.MagnoliaRowEncoder.given
import ru.johnspade.tgbot.callbackdata.named.MagnoliaRowDecoder.given
import ru.johnspade.tgbot.callbackdata.named.MagnoliaRowDecoder
import ru.johnspade.zcsv.codecs.FieldEncoder.given
import ru.johnspade.zcsv.codecs.RowEncoder.fromFieldEncoder
import ru.johnspade.zcsv.codecs.FieldDecoder.given
import ru.johnspade.zcsv.codecs.RowDecoder.fromFieldDecoder
import ru.johnspade.zcsv.codecs.instances.*
import ru.johnspade.zcsv.codecs.instances.given
import ru.johnspade.zcsv.codecs.*
import ru.johnspade.zcsv.printer.CsvPrinter
import ru.johnspade.zcsv.parser.parseRow
import cats.syntax.either._

sealed trait TestCallbackData {
  def toCsv: String = CsvPrinter.default.print(summon[RowEncoder[TestCallbackData]].encode(this))
}

final case class BuyIcecream(flavor: String) extends TestCallbackData
case object SayHello                         extends TestCallbackData

object TestCallbackData:
  given decoder: RowDecoder[TestCallbackData] = MagnoliaRowDecoder.derived
  def decode(csv: String) =
    val row = parseRow(csv).left.map(e => new RuntimeException(e.toString)).valueOr(throw _)
    decoder.decode(row)
