# tgbot-utils

![Bintray](https://img.shields.io/bintray/v/johnspade/maven/tgbot-utils)

Collection of extensions for [Telegramium](https://github.com/apimorphism/telegramium) 
(a pure functional Telegram Bot API implementation for Scala) that I use to build my bots.

## callback-queries

[http4s](https://github.com/http4s/http4s) -like DSL to handle callback queries.

## message-entities

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
