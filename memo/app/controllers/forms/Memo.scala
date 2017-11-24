package controllers.forms

import dao.MemoInfo
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.data.Forms.optional
import play.api.data.Forms.seq
import play.api.data.Forms.text

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
