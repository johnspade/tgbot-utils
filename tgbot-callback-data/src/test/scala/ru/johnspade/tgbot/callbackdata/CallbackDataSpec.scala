package ru.johnspade.tgbot.callbackdata

import zio.test.Assertion.equalTo
import zio.test.*
import cats.syntax.either.*
import zio.Scope

object CallbackDataSpec extends ZIOSpecDefault:
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("CallbackDataSpec")(
    suite("toCsv")(
      test("should encode case class to CSV with discriminator") {
        val cbData = BuyIcecream("vanilla")
        assert(cbData.toCsv)(equalTo("BuyIcecream,vanilla"))
      }
    ),
    suite("decode")(
      test("should decode CSV as a case class") {
        val csv = "BuyIcecream,vanilla"
        assert(TestCallbackData.decode(csv).valueOr(throw _))(equalTo(BuyIcecream("vanilla")))
      }
    )
  )
