package dao

import java.sql.Timestamp

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import controllers.forms.Memo.MemoForm
import javax.inject.Inject
import javax.inject.Singleton
import models.Tables.Memo
import models.Tables.MemoRow
import models.Tables.TagMapping
import models.Tables.TagMappingRow
import models.Tables.TagMst
import models.Tables.TagMstRow
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

case class MemoInfo(id: Int, title: String, mainText: String, tags: Option[Seq[TagMstRow]])

trait MemoDao extends HasDatabaseConfigProvider[JdbcProfile] {

  private val logger = Logger(classOf[MemoDao])

  def search(mainText: Option[String]): Future[Seq[MemoInfo]]

  def create(form: MemoForm): Future[Int]

  def update(form: MemoForm): Future[Option[Int]]

  def delete(id: Int): Future[Int]
}

@Singleton class MemoDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends MemoDao {

  def search(mainText: Option[String]) = {
    for {
      // 検索条件が存在していれば条件を使って検索、存在していないなら全件取得
      memos <- db.run(mainText.fold(
        Memo.sortBy(_.id).map(result =>
          (result.id, result.title, result.mainText.getOrElse(""))))(mainText =>
          Memo.filter(_.mainText like s"%${mainText}%").map(result =>
            (result.id, result.title, result.mainText.getOrElse("")))).result)

      // 取得したメモに紐づくタグを取得する
      tags <- db.run(Memo.filter(_.id.inSetBind(memos.map(_._1)))
        .join(TagMapping).on((m, t) => m.id === t.memoId)
        .joinLeft(TagMst).on((mt, tm) => mt._2.tagId === tm.id)
        .map(result => result._2).result)
        .map(result => if (result.flatten.isEmpty) None else Some(result.flatten))
    } yield {
      // 取得したメモと紐づくタグのリストを持つ型に変換
      memos.map(memo => MemoInfo(memo._1, memo._2, memo._3, tags))
    }
  }

  def create(form: MemoForm) = {
    // トランザクションを作成
    val actions = (for {
      // メモを登録してidを取得
      memoId <- Memo returning Memo.map(_.id) += MemoRow(form.title, form.mainText, None, new Timestamp(System.currentTimeMillis()))

      // ひも付けられたタグがマスタに存在しない場合、タグを登録
      _ <- DBIO.sequence(form.tags.map(tag =>
        TagMst.filter(_.name === tag).exists.result.flatMap {
          case true => DBIO.successful(0)
          case false => (TagMst returning TagMst.map(_.id) += TagMstRow(Some(tag))).map(DBIO.successful(_))
        }))

      // メモとタグのマッピングを登録
      _ <- TagMst.filter(_.name.inSetBind(form.tags)).result.flatMap(tags =>
        TagMapping ++= tags.map(tag => TagMappingRow(Some(memoId), tag.id)))
    } yield (memoId)).transactionally

    db.run(actions)
  }

  def update(form: MemoForm) = {
    // トランザクションを作成
    val actions = (for {
      // メモをアップデートして更新したメモのidを取得
      updatedMemoId <- Memo.filter(memo => memo.id === form.id.getOrElse(-1).bind)
        .map(result => (result.title, result.mainText, result.upadtedAt))
        .update((form.title, form.mainText, Some(new Timestamp(System.currentTimeMillis())))).map(_ match {
          case updated if updated == 1 => form.id
          case _ => None
        })

      // タグのひも付き解除でないタグでマスタに存在しない場合、タグを登録
      _ <- DBIO.sequence(form.tags.filter(!_.endsWith("-remove")).map(tag =>
        TagMst.filter(_.name === tag)
          .exists.result.flatMap {
            case true => DBIO.successful(0)
            case false => (TagMst returning TagMst.map(_.id) += TagMstRow(Some(tag))).map(DBIO.successful(_))
          }))

      // タグのひも付きの削除と登録
      _ <- DBIO.sequence(form.tags.map(tag => tag.endsWith("-remove") match {
        case true => TagMst.filter(_.name === tag.replace("-remove", "")).result.headOption.map(_.map(tagMstRow =>
          TagMapping.filter(tagMapping => tagMapping.memoId === updatedMemoId && tagMapping.tagId === tagMstRow.id).delete))
        case false => TagMst.filter(_.name === tag).result.headOption.map(_.map(tagMstRow =>
          TagMapping.filter(tagMapping => tagMapping.memoId === updatedMemoId && tagMapping.tagId === tagMstRow.id).exists.result
            .map {
              case true => DBIO.successful(false)
              case false => TagMapping += TagMappingRow(updatedMemoId, tagMstRow.id)
            }))
      }))
    } yield (updatedMemoId)).transactionally

    db.run(actions)
  }

  def delete(id: Int) = {
    // メモとタグのひも付きを削除
    db.run(Memo.filter(_.id === id).delete.flatMap(_ => TagMapping.filter(_.memoId === id).delete))
  }
}
