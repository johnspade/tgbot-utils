# tgbot-utils

![Bintray](https://img.shields.io/bintray/v/johnspade/maven/tgbot-utils)

Collection of extensions for [Telegramium](https://github.com/apimorphism/telegramium) 
(a pure functional Telegram Bot API implementation for Scala) that I use to build my bots.

## tgbot-callback-data

Represent your callback query data types as an ADT and use kantan.csv to (de)serialize them.

```scala
import kantan.csv.DecodeError.TypeError
import kantan.csv.ops._
import kantan.csv._
import ru.johnspade.tgbot.callbackdata.named.MagnoliaRowEncoder._
import ru.johnspade.tgbot.callbackdata.named.MagnoliaRowDecoder._

sealed abstract class CallbackData {
  def toCsv: String = this.writeCsvRow(rfc)
}

final case class BuyIcecream(flavor: String) extends CallbackData
case object SayHello extends CallbackData

object CallbackData {
  implicit val buyIcecreamRowCodec: RowCodec[BuyIcecream] = RowCodec.caseOrdered(BuyIcecream.apply _)(BuyIcecream.unapply)
  implicit val sayHelloRowCodec: RowCodec[SayHello.type] = RowCodec.from(_ => Right(SayHello))(_ => Seq.empty)

  def decode(csv: String): ReadResult[CallbackData] =
    csv.readCsv[List, CallbackData](rfc).headOption.getOrElse(Left(TypeError("Callback data is missing")))
}

val csv = BuyIcecream("vanilla").toCsv
// BuyIceCream,vanilla

val callbackData = CallbackData.decode(csv).getOrElse(sys.error(""))
// BuyIcecream(vanilla)
```

## tgbot-callback-queries

[http4s](https://github.com/http4s/http4s) -like DSL to handle callback queries.

## tgbot-message-entities

String interpolators to create [message entities](https://core.telegram.org/bots/api#messageentity) with auto-calculated 
offsets and lenghts.

```scala
import ru.johnspade.tgbot.messageentities.TypedMessageEntity
import ru.johnspade.tgbot.messageentities.TypedMessageEntity.Plain.lineBreak
import ru.johnspade.tgbot.messageentities.TypedMessageEntity._

val messageEntities = List(
  plain"bold: ", bold"bold text", lineBreak,
  plain"italic: ", italic"italic text", lineBreak,
  plain"email: ", email"do-not-reply@telegram.org", lineBreak,
  plain"pre: ", Pre(text = "monowidth block", language = "Scala")
)

val tgMessageEntities = TypedMessageEntity.toMessageEntities(messageEntities)
/*
  List(
    BoldMessageEntity(6,9), 
    ItalicMessageEntity(24,11), 
    EmailMessageEntity(43,25), 
    PreMessageEntity(74,15,Scala)
  )
 */

val text = messageEntities.map(_.text).mkString
/*
  bold: bold text
  italic: italic text
  email: do-not-reply@telegram.org
  pre: monowidth block
 */

```
