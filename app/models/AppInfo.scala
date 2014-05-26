package models

/**
 * Created by evans on 5/27/14.
 *
 * AppInfo: version info and basic info
 */
case class AppInfo(app: App, versionCode: Int, versionName: String, lastUpdate: String)

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.sql.Date

object AppInfo {

  def create(info: AppInfo) = {
    val sql =
      """
        | insert into version(app_id, version_code, version_name, last_update
        | values({app_id}, {code}, {name}, {last_update})
      """.stripMargin

    DB.withConnection(implicit conn => SQL(sql).on('app_id -> info.app.id,
      'code -> info.versionCode, 'name -> info.versionName,
      'last_update -> new Date(System.currentTimeMillis())).executeUpdate())
  }

  val appInfo =
    get[Long]("id") ~
      get[String]("package") ~
      get[Int]("version_code") ~
      get[String]("version_name") ~
      get[String]("last_update") map {
      case id ~ name ~ versionCode ~ versionName ~ lastUpdate =>
        AppInfo(App(id, name), versionCode, versionName, lastUpdate)
    }

  def read(appId: Long): AppInfo = {
    val sql =
      """
        | select app.id, app.package, version_code, version_name, last_update from app, version
        | where version.app_id = app.id where app.id = {app_id}
        | order by app.package, version.last_update desc
      """.stripMargin

    DB.withConnection(implicit conn => SQL(sql).on('app_id -> appId).single(appInfo))
  }

  def readAll: List[AppInfo] = {
    val sql =
      """
        | select app.id, app.package, version_code, version_name, last_update from app, version
        | where version.app_id = app.id
        | order by app.package, version.last_update desc
      """.stripMargin

    DB.withConnection(implicit conn => SQL(sql).as(appInfo *))
  }

}