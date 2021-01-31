package ru.johnspade.tgbot.callbackqueries

trait CallbackDataDecoder[F[_], T] {
  def decode(queryData: String): DecodeResult[F, T]
}
