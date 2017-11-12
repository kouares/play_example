package controllers.forms

import play.api.data._
import play.api.data.Forms._

object Memo {
  case class MemoForm(id: Option[Int], title: String, mainText: Option[String], tags: Seq[String])

  val memoForm = Form(
    mapping(
      "id" -> optional(number),
      "title" -> nonEmptyText(maxLength = 200),
      "mainText" -> optional(text),
      "tags" -> seq(text))(MemoForm.apply)(MemoForm.unapply))
}
