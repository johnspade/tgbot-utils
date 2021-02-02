package ru.johnspade.tgbot.callbackdata.annotated

import scala.annotation.StaticAnnotation

final case class TypeId(id: Int) extends StaticAnnotation
