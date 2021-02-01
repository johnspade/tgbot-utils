package ru.johnspade.tgbot

import cats.data.{EitherT, Kleisli, OptionT}
import cats.implicits._
import cats.{Applicative, Defer, Monad}
import telegramium.bots.CallbackQuery

package object callbackqueries {
  type DecodeResult[F[_], I] = EitherT[F, DecodeFailure, I]

  type CallbackQueries[I, Res, F[_]] = Kleisli[F, CallbackQueryData[I], Res]

  type CallbackQueryRoutes[I, Res, F[_]] = CallbackQueries[I, Res, OptionT[F, *]]

  type CallbackQueryContextRoutes[I, A, Res, F[_]] = Kleisli[OptionT[F, *], ContextCallbackQuery[I, A], Res]

  type Middleware[F[_], A, B, C, D] = Kleisli[F, A, B] => Kleisli[F, C, D]

  type CallbackQueryContextMiddleware[I, A, Res, F[_]] = Middleware[
    OptionT[F, *],
    ContextCallbackQuery[I, A], Res,
    CallbackQueryData[I], Res
  ]

  object CallbackQueryRoutes {
    def of[I, Res, F[_]: Defer: Applicative](
      pf: PartialFunction[CallbackQueryData[I], F[Res]]
    ): CallbackQueryRoutes[I, Res, F] =
      Kleisli(input => OptionT(Defer[F].defer(pf.lift(input).sequence)))
  }

  object CallbackQueryContextRoutes {
    def of[I, A, Res, F[_]: Defer: Applicative](
      pf: PartialFunction[ContextCallbackQuery[I, A], F[Res]]
    ): CallbackQueryContextRoutes[I, A, Res, F] =
      Kleisli(cb => OptionT(Defer[F].defer(pf.lift(cb).sequence)))
  }

  object CallbackQueryContextMiddleware {
    def apply[F[_]: Monad, I, A, Res](
      retrieveContext: Kleisli[OptionT[F, *], CallbackQuery, A]
    ): CallbackQueryContextMiddleware[I, A, Res, F] =
      _.compose(Kleisli((cb: CallbackQueryData[I]) => retrieveContext(cb.cb).map(ContextCallbackQuery(_, cb))))
  }
}
