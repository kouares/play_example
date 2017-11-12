package dao

import play.api.Logger
import scala.concurrent.Future
import models.Tables._
import scala.concurrent.ExecutionContext
import javax.inject.Inject
import javax.inject.Singleton
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider

case class MemoInfo(id: Int, title: String, mainText: String, tags: Option[Seq[TagMstRow]])

trait MemoDao extends HasDatabaseConfigProvider[JdbcProfile] {

  private val logger = Logger(classOf[MemoDao])

  def search(mainText: Option[String]): Future[Seq[MemoInfo]]

}

@Singleton class MemoDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends MemoDao {

  def search(mainText: Option[String]) = {
    val memoQuery = mainText.fold(Memo.sortBy(_.id).map(result => (result.id, result.title, result.mainText.getOrElse(""))))(mainText =>
      Memo.filter(_.mainText like s"%${mainText}%").map(result => (result.id, result.title, result.mainText.getOrElse(""))))
      .result

    for {
      memos <- db.run(memoQuery)
      tags <- {
        db.run(Memo.filter(_.id.inSetBind(memos.map(_._1)))
          .join(TagMapping).on((m, t) => m.id === t.memoId)
          .joinLeft(TagMst).on((mt, tm) => mt._2.tagId === tm.id)
          .map(result => result._2)
          .result)
          .map(result => if (result.flatten.isEmpty) None else Some(result.flatten))
      }
    } yield memos.map(memo => MemoInfo(memo._1, memo._2, memo._3, tags))
  }

}
