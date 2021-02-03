# tgbot-utils

![Bintray](https://img.shields.io/bintray/v/johnspade/maven/tgbot-utils)

Collection of extensions for [Telegramium](https://github.com/apimorphism/telegramium) 
(a pure functional Telegram Bot API implementation for Scala) that I use to build my bots.

# Setup

tgbot-utils is currently available for Scala 2.12 and 2.13.

Whole utils pack:

```scala
resolvers += Resolver.bintrayRepo("johnspade", "maven")

libraryDependencies += "ru.johnspade" %% "tgbot-utils" % "latest version in badge"
```
A specific module:
```scala
libraryDependencies += "ru.johnspade" %% "module name" % "latest version in badge"
```

## tgbot-callback-data

Represent your callback query data types as an ADT and use [kantan.csv](https://github.com/nrinaudo/kantan.csv) 
to (de)serialize them to CSV strings.

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

```scala
import cats.effect.IO
import ru.johnspade.tgbot.callbackqueries.CallbackQueryDsl._
import ru.johnspade.tgbot.callbackqueries.CallbackQueryRoutes
import telegramium.bots.client.Method

val routes = CallbackQueryRoutes.of[CallbackData, Option[Method[_]], IO] {
  case BuyIcecream(flavor) in cb =>
    IO {
      println(s"${cb.from.firstName} have chosen: $flavor")
      None
    }
}
```

Extract and use context information from callback queries with `CallbackQueryContextRoutes`:

```scala
import cats.effect.IO
import ru.johnspade.tgbot.callbackqueries.CallbackQueryDsl._
import ru.johnspade.tgbot.callbackqueries.CallbackQueryContextRoutes
import telegramium.bots.high.Methods

case class User(id: Int, firstName: String, language: String)

val contextRoutes = CallbackQueryContextRoutes.of[CallbackData, User, Option[Method[_]], IO] {
  case SayHello in cb as user =>
    IO {
      Some(Methods.answerCallbackQuery(cb.id, text = Some(s"Hello, ${user.firstName}")))
    }
}
```

We use `CallbackQueryContextMiddleware` for that:

```scala
import cats.data.{Kleisli, OptionT}
import ru.johnspade.tgbot.callbackqueries.{CallbackQueryContextMiddleware, CallbackQueryData, ContextCallbackQuery}

val userMiddleware: CallbackQueryContextMiddleware[CallbackData, User, Option[Method[_]], IO] =
  _.compose(
    Kleisli { (cb: CallbackQueryData[CallbackData]) =>
      val from = cb.cb.from
      val user = User(from.id, from.firstName, from.languageCode.getOrElse("en"))
      OptionT.liftF(IO(ContextCallbackQuery(user, cb)))
    }
  )
```

Combine multiple routes with `<+>` (`combineK`) and handle queries with `CallbackQueryHandler`:

```scala
import cats.syntax.semigroupk._
import cats.syntax.either._
import ru.johnspade.tgbot.callbackqueries.{CallbackQueryHandler, CallbackDataDecoder, ParseError, DecodeError}

val allRoutes = routes <+> userMiddleware(contextRoutes)

private val cbDataDecoder: CallbackDataDecoder[IO, CallbackData] =
  CallbackData.decode(_).left.map {
    case error: kantan.csv.ParseError => ParseError(error.getMessage)
    case error: kantan.csv.DecodeError => DecodeError(error.getMessage)
  }
    .toEitherT[IO]

// query: CallbackQuery

val handler = CallbackQueryHandler.handle(
  query,
  routes = allRoutes,
  decoder = cbDataDecoder, 
  onNotFound = _ => IO(Option.empty[Method[_]])
)
```

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
