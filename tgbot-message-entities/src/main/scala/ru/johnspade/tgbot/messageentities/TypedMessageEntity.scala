package ru.johnspade.tgbot.messageentities

import telegramium.bots.{BoldMessageEntity, BotCommandMessageEntity, CashtagMessageEntity, CodeMessageEntity, EmailMessageEntity, HashtagMessageEntity, ItalicMessageEntity, MentionMessageEntity, MessageEntity, PhoneNumberMessageEntity, PreMessageEntity, StrikethroughMessageEntity, TextLinkMessageEntity, TextMentionMessageEntity, UnderlineMessageEntity, UrlMessageEntity, User}

sealed abstract class TypedMessageEntity extends Product with Serializable {
  def text: String
}

object TypedMessageEntity {
  def toMessageEntities(entities: List[TypedMessageEntity]): List[MessageEntity] =
    entities.foldLeft((List.empty[MessageEntity], 0)) { case ((acc, offset), entity) =>
      def accumulate(me: MessageEntity) = (me :: acc, offset + me.length)

      entity match {
        case Plain(text) => (acc, offset + text.length)

        case Mention(text) => accumulate(MentionMessageEntity(offset, text.length))
        case Cashtag(text) => accumulate(CashtagMessageEntity(offset, text.length))
        case Code(text) => accumulate(CodeMessageEntity(offset, text.length))
        case BotCommand(text) => accumulate(BotCommandMessageEntity(offset, text.length))
        case Email(text) => accumulate(EmailMessageEntity(offset, text.length))
        case Bold(text) => accumulate(BoldMessageEntity(offset, text.length))
        case Pre(text, language) => accumulate(PreMessageEntity(offset, text.length, language))
        case Italic(text) => accumulate(ItalicMessageEntity(offset, text.length))
        case Strikethrough(text) => accumulate(StrikethroughMessageEntity(offset, text.length))
        case Underline(text) => accumulate(UnderlineMessageEntity(offset, text.length))
        case Hashtag(text) => accumulate(HashtagMessageEntity(offset, text.length))
        case TextMention(text, user) => accumulate(TextMentionMessageEntity(offset, text.length, user))
        case TextLink(text, url) => accumulate(TextLinkMessageEntity(offset, text.length, url))
        case Url(text) => accumulate(UrlMessageEntity(offset, text.length))
        case PhoneNumber(text) => accumulate(PhoneNumberMessageEntity(offset, text.length))
      }
    }
      ._1
      .reverse

  final case class Plain(text: String) extends TypedMessageEntity {
    def value: String = text
  }

  object Plain {
    val lineBreak: Plain = Plain("\n")
  }

  final case class Mention(text: String) extends TypedMessageEntity

  final case class Cashtag(text: String) extends TypedMessageEntity

  final case class Code(text: String) extends TypedMessageEntity

  final case class BotCommand(text: String) extends TypedMessageEntity

  final case class Email(text: String) extends TypedMessageEntity

  final case class Bold(text: String) extends TypedMessageEntity

  final case class Pre(text: String, language: String) extends TypedMessageEntity

  final case class Italic(text: String) extends TypedMessageEntity

  final case class Strikethrough(text: String) extends TypedMessageEntity

  final case class Underline(text: String) extends TypedMessageEntity

  final case class Hashtag(text: String) extends TypedMessageEntity

  final case class TextMention(text: String, user: User) extends TypedMessageEntity

  final case class TextLink(text: String, url: String) extends TypedMessageEntity

  final case class Url(text: String) extends TypedMessageEntity

  final case class PhoneNumber(text: String) extends TypedMessageEntity

  implicit class StringMessageEntityHelper(val sc: StringContext) extends AnyVal {
    def plain(args: Any*): Plain = Plain(build(args: _*))

    def cashtag(args: Any*): Cashtag = Cashtag(build(args: _*))

    def code(args: Any*): Code = Code(build(args: _*))

    def botCommand(args: Any*): BotCommand = BotCommand(build(args: _*))

    def email(args: Any*): Email = Email(build(args: _*))

    def bold(args: Any*): Bold = Bold(build(args: _*))

    def italic(args: Any*): Italic = Italic(build(args: _*))

    def strikethrough(args: Any*): Strikethrough = Strikethrough(build(args: _*))

    def underline(args: Any*): Underline = Underline(build(args: _*))

    def hashtag(args: Any*): Hashtag = Hashtag(build(args: _*))

    def url(args: Any*): Url = Url(build(args: _*))

    def phoneNumber(args: Any*): PhoneNumber = PhoneNumber(build(args: _*))

    private def build(args: Any*): String = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buf = new java.lang.StringBuilder(strings.next())
      while (expressions.hasNext) {
        buf.append(expressions.next().toString)
        buf.append(strings.next())
      }
      buf.toString
    }
  }
}
