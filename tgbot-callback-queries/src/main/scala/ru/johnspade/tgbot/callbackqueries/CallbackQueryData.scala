package ru.johnspade.tgbot.callbackqueries

import telegramium.bots.CallbackQuery

final case class CallbackQueryData[I](
  data: I,
  cb: CallbackQuery
)
