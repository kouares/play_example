package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import controllers.forms.Memo.MemoForm
import controllers.forms.Memo.memoForm
import dao.MemoDao
import javax.inject.Inject
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import controllers.forms.Memo

class MemoController @Inject() (cc: ControllerComponents, memoDao: MemoDao)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  private val logger = Logger(classOf[MemoController])

  def search(mainText: Option[String]) = Action.async { implicit rs =>
    memoDao.search(mainText).map(result =>
      Ok(views.html.memo.list(memoForm.fill(MemoForm(None, "", mainText, Seq.empty[String])), result.map(MemoForm(_)))))
  }

  def showCreate = Action.async { implicit rs =>
    Future { Ok(views.html.memo.create(memoForm.fill(MemoForm(None, "", None, Seq.empty[String])))) }
  }

  def create = Action.async { implicit rs =>
    memoForm.bindFromRequest().fold(errors => Future {
      BadRequest(views.html.memo.create(errors))
    }, form => memoDao.create(form).map(_ => Redirect(routes.MemoController.search(None))))
  }

  def showUpdate(id: Int) = Action.async { implicit rs =>
    memoDao.findById(id).map(_.fold(
      Redirect(routes.MemoController.search(None)))(memo => {
        memo.tags.foreach(row => logger.info(row.name.getOrElse("empty")))
        Ok(views.html.memo.update(memoForm.fill(MemoForm(memo))))
      }))
  }

  def update(id: Int) = Action.async { implicit rs =>
    memoForm.bindFromRequest().fold(errors => Future {
      BadRequest(views.html.memo.update(errors))
    }, form => memoDao.update(id, form).map(_ => Redirect(routes.MemoController.search(None))))
  }

  def delete(id: Int) = Action.async { implicit rs =>
    memoDao.delete(id).map(_ => Redirect(routes.MemoController.search(None)))
  }
}
