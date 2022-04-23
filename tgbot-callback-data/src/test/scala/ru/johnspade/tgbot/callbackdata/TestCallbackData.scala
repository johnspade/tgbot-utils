package ru.johnspade.tgbot.callbackdata

import ru.johnspade.tgbot.callbackdata.named.{MagnoliaRowEncoder, MagnoliaRowDecoder}
import ru.johnspade.tgbot.callbackdata.TestCallbackData.given
import ru.johnspade.zcsv.codecs.*
import ru.johnspade.zcsv.codecs.instances.given
import ru.johnspade.zcsv.printer.CsvPrinter
import ru.johnspade.zcsv.parser.parseRow
import cats.syntax.either.*

sealed trait TestCallbackData {
  def toCsv: String = CsvPrinter.default.print(RowEncoder[TestCallbackData].encode(this))
}

final case class BuyIcecream(flavor: String) extends TestCallbackData
case object SayHello                         extends TestCallbackData

object TestCallbackData:
  given encoder: RowEncoder[TestCallbackData] = MagnoliaRowEncoder.derived
  given decoder: RowDecoder[TestCallbackData] = MagnoliaRowDecoder.derived
  def decode(csv: String) =
    val row = parseRow(csv).left.map(e => new RuntimeException(e.toString)).valueOr(throw _)
    decoder.decode(row)
