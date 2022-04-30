package ru.johnspade.tgbot.messageentities

import ru.johnspade.tgbot.messageentities.TypedMessageEntity._
import telegramium.bots.{BoldMessageEntity, ItalicMessageEntity}
import zio.test.Assertion.*
import zio.test.*
import zio.Scope

object TypedMessageEntitySpec extends ZIOSpecDefault:
  override def spec: ZSpec[TestEnvironment with Scope, Any] = suite("TypedMessageEntitySpec")(
    suite("convertation")(
      test("toMessageEntities should convert all contained entities") {
        val stringMessageEntities = List(Bold("bold"), Plain("plain"), Italic("italic"))
        val expected              = List(BoldMessageEntity(0, 4), ItalicMessageEntity(9, 6))
        assert(TypedMessageEntity.toMessageEntities(stringMessageEntities))(hasSameElements(expected))
      }
    ),
    suite("interpolation")(
      test("plain") {
        assert(plain"1${1 + 1}3")(equalTo(Plain("123")))
      },
      test("bold") {
        assert(bold"1${1 + 1}3")(equalTo(Bold("123")))
      },
      test("italic") {
        assert(italic"1${1 + 1}3")(equalTo(Italic("123")))
      }
    )
  )
