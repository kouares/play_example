package genarator

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

import slick.codegen.SourceCodeGenerator
import slick.jdbc.meta.MTable
import slick.model.Model

object SlickModelGenerator extends App {

  val slickDriver = "slick.jdbc.MySQLProfile"
  val jdbcDriver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://localhost/memo"
  val user = "memo"
  val password = "memo"
  val outputDir = "app"
  val pkg = "models"
  val topTraitName = "Tables"
  val scalaFileName = "Tables.scala"
  val tableNames: Option[Seq[String]] = None
  val profile = slick.jdbc.MySQLProfile
  val db = profile.api.Database.forURL(url, driver = jdbcDriver, user = user, password = password)

  try {
    import scala.concurrent.ExecutionContext.Implicits.global
    val mTablesAction = MTable.getTables.map { _.map { mTable => mTable.copy(name = mTable.name.copy(catalog = None)) } }

    val allModel = Await.result(db.run(profile.createModel(Some(mTablesAction), false)(ExecutionContext.global).withPinnedSession), Duration.Inf)

    val modelFiltered = tableNames.fold(allModel) { tableNames =>
      Model(tables = allModel.tables.filter { aTable =>
        tableNames.contains(aTable.name.table)
      })
    }

    new SourceCodeGeneratorEx(modelFiltered).writeToFile(slickDriver, outputDir, pkg, topTraitName, scalaFileName)
  } finally db.close

  class SourceCodeGeneratorEx(model: Model) extends SourceCodeGenerator(model) {
    override def Table = new Table(_) {
      //auto_incrementを識別できるようにする
      //生成されるモデルはOption型になる
      override def autoIncLastAsOption = true
      override def Column = new Column(_) {
        override def rawType = model.tpe match {
          case "java.sql.Blob" =>
            "Array[Byte]"
          case _ =>
            super.rawType
        }
      }
    }
  }
}
