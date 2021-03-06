package ru.johnspade.tgbot.callbackqueries

import cats.MonadError
import cats.data.OptionT
import telegramium.bots.CallbackQuery

object CallbackQueryHandler {
  def handle[F[_], I, Res](
    cb: CallbackQuery,
    routes: CallbackQueryRoutes[I, Res, F],
    decoder: CallbackDataDecoder[F, I],
    onNotFound: CallbackQuery => F[Res]
  )(implicit F: MonadError[F, Throwable]): F[Res] =
    (for {
      queryData <- OptionT.fromOption[F](cb.data)
      data <- OptionT.liftF(F.rethrow(decoder.decode(queryData).value))
      res <- routes.run(CallbackQueryData(data, cb))
    } yield res)
      .getOrElseF(onNotFound(cb))
}
