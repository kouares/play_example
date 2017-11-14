package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import controllers.forms.Memo.MemoForm
import controllers.forms.Memo.memoForm
import javax.inject.Inject
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import dao.MemoDao

class MemoController @Inject() (cc: ControllerComponents, memoDao: MemoDao)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  private val logger = Logger(classOf[MemoController])

  def search(mainText: Option[String]) = Action.async { implicit rs =>
    Future { Ok("") }
  }

  def showCreate = Action.async { implicit rs =>
    Future { Ok(views.html.memo.create(memoForm.fill(MemoForm(None, "", None, Seq.empty[String])))) }
  }

  def create = Action.async { implicit rs =>
    Future { Ok("") }
  }

  def showUpdate(id: Int) = Action.async { implicit rs =>
    Future { Ok("") }
  }

  def update(id: Int) = Action.async { implicit rs =>
    Future { Ok("") }
  }

  def delete(id: Int) = Action.async { implicit rs =>
    Future { Ok("") }
  }
}
