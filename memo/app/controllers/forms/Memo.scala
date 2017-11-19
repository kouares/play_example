package controllers.forms

import play.api.data._
import play.api.data.Forms._
import dao.MemoInfo

object Memo {
  case class MemoForm(id: Option[Int], title: String, mainText: Option[String], tags: Seq[String])

  object MemoForm {
    def apply(memo: MemoInfo): MemoForm =
      new MemoForm(Some(memo.id), memo.title, Some(memo.mainText), memo.tags.map(_.name.getOrElse("")).filter(_.nonEmpty))
  }

  val memoForm = Form(
    mapping(
      "id" -> optional(number),
      "title" -> nonEmptyText(maxLength = 200),
      "mainText" -> optional(text),
      "tags" -> seq(text))(MemoForm.apply)(MemoForm.unapply))
}
