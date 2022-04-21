package ru.johnspade.tgbot.callbackdata

import zio.test.Assertion.equalTo
import zio.test._
import cats.syntax.either._

object CallbackDataSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[TestEnvironment, Throwable] = suite("CallbackDataSpec")(
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
}
