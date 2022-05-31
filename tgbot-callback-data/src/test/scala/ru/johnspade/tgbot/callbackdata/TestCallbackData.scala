package ru.johnspade.tgbot.callbackdata

import ru.johnspade.tgbot.callbackdata.named.{MagnoliaRowEncoder, MagnoliaRowDecoder}
import ru.johnspade.tgbot.callbackdata.TestCallbackData.given
import ru.johnspade.csv3s.codecs.*
import ru.johnspade.csv3s.codecs.instances.given
import ru.johnspade.csv3s.printer.CsvPrinter
import ru.johnspade.csv3s.parser.parseRow
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
