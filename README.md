# tgbot-utils

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.johnspade/tgbot-utils_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ru.johnspade/tgbot-utils_3)

Collection of extensions for [Telegramium](https://github.com/apimorphism/telegramium) 
(a pure functional Telegram Bot API implementation for Scala) that I use to build my bots.

# Setup

tgbot-utils is currently available for Scala 3.

Whole utils pack:

```scala
libraryDependencies += "ru.johnspade" %% "tgbot-utils" % "<latest version in badge>"
```
A specific module:
```scala
libraryDependencies += "ru.johnspade" %% "<module name>" % "<latest version in badge>"
```

## tgbot-callback-data

Represent your callback query data types as an ADT and use [csv3s](https://github.com/johnspade/csv3s) 
to (de)serialize them to CSV strings.

```scala
import ru.johnspade.csv3s.codecs.*
import ru.johnspade.csv3s.codecs.instances.given
import ru.johnspade.csv3s.parser.*
import ru.johnspade.csv3s.printer.CsvPrinter

import ru.johnspade.tgbot.callbackdata.named.*
import ru.johnspade.tgbot.callbackqueries.*

sealed abstract class CallbackData {
  import CallbackData.{encoder, csvPrinter}

  def toCsv: String = csvPrinter.print(encoder.encode(this))
}

final case class BuyIcecream(flavor: String) extends CallbackData
case object SayHello extends CallbackData

object CallbackData {
  given encoder: RowEncoder[CallbackData] = MagnoliaRowEncoder.derived
  given decoder: RowDecoder[CallbackData] = MagnoliaRowDecoder.derived

  val Separator: Char = ','
  private val csvParser = new CsvParser(Separator)
  val csvPrinter: CsvPrinter = CsvPrinter.withSeparator(Separator)

  def decode(csv: String): Either[DecodeFailure, CallbackData] =
    parseRow(csv, csvParser).left
      .map(e => ru.johnspade.tgbot.callbackqueries.ParseError(e.toString))
      .flatMap(
        decoder
          .decode(_)
          .left
          .map { e =>
            ru.johnspade.tgbot.callbackqueries.DecodeError(e.getMessage)
          }
      )
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

**REMOVED**: It is now a part of [Telegramium](https://github.com/apimorphism/telegramium) and has been removed.

# Usages

- [s10ns_bot](https://github.com/johnspade/s10ns_bot) – Subscription Management Telegram Bot
- [Taskobot](https://github.com/johnspade/taskobot-scala) – task collaboration inline Telegram bot 
