package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import javax.inject.Inject
import play.api.Logger
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents

class MemoController @Inject() (cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val logger = Logger(classOf[MemoController])

  def search = Action.async { implicit rs =>
    Future { Ok("") }
  }

  def showCreate = Action.async { implicit rs =>
    Future { Ok("") }
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
