package ru.johnspade.tgbot.callbackdata

import kantan.csv.DecodeError.TypeError
import kantan.csv.ops._
import kantan.csv._
import ru.johnspade.tgbot.callbackdata.named.MagnoliaRowEncoder._
import ru.johnspade.tgbot.callbackdata.named.MagnoliaRowDecoder._

sealed abstract class TestCallbackData {
  def toCsv: String = this.writeCsvRow(rfc)
}

final case class BuyIcecream(flavor: String) extends TestCallbackData
case object SayHello extends TestCallbackData

object TestCallbackData {
  implicit val buyIcecreamRowCodec: RowCodec[BuyIcecream] = RowCodec.caseOrdered(BuyIcecream.apply _)(BuyIcecream.unapply)
  implicit val sayHelloRowCodec: RowCodec[SayHello.type] = RowCodec.from(_ => Right(SayHello))(_ => Seq.empty)

  def decode(csv: String): ReadResult[TestCallbackData] =
    csv.readCsv[List, TestCallbackData](rfc).headOption.getOrElse(Left(TypeError("Callback data is missing")))
}
