package ru.johnspade.tgbot.callbackqueries

final case class ContextCallbackQuery[I, A](context: A, query: CallbackQueryData[I])
