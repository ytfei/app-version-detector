package models

/**
 * Created by evans on 5/27/14.
 *
 * AppInfo: version info and basic info
 */
case class AppInfo(id: Long, name: String,
                   initVersionCode: Long, initVersionName: String,
                   currentVersionCode: Option[Long] = None, currentVersionName: Option[String] = None,
                   lastUpdate: Option[String] = None)

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.sql.Date
import scala.util.Try
import scala.util.{Success => Succ}
import scala.util.Failure
import play.Logger

object AppInfo {

  def create(info: AppInfo) = {
    val sql =
      """
        | insert into app_info(name, init_version_code, init_version_name, last_update)
        | values({name}, {version_code}, {version_name}, {last_update})
      """.stripMargin

    Try(DB.withConnection(implicit conn => SQL(sql).on('name -> info.name,
      'version_code -> info.initVersionCode, 'version_name -> info.initVersionName,
      'last_update -> new Date(System.currentTimeMillis())).executeUpdate())) match {
      case Succ(v) => v
      case Failure(e) =>
        if (e.getMessage.contains("Unique index")) {
          Logger.warn(s"App ${info.name} exists already!")
        }

        1 // return 1, assume the 'create' action is success
    }
  }

  def delete(id: Long) = {
    val sql = "delete from app_info where id = {id}"

    DB.withConnection(implicit conn => SQL(sql).on('id -> id).executeUpdate())
  }

  val appInfo =
    get[Long]("id") ~
      get[String]("name") ~
      get[Long]("init_version_code") ~
      get[String]("init_version_name") ~
      get[Option[Long]]("curr_version_code") ~
      get[Option[String]]("curr_version_name") ~
      get[Option[String]]("last_update") map {
      case id ~ name ~ initVersionCode ~ initVersionName ~ currVersionCode ~ currVersionName ~ lastUpdate =>
        AppInfo(id, name, initVersionCode, initVersionName, currVersionCode, currVersionName, lastUpdate)
    }

  def read(appId: Long): AppInfo = {
    val sql =
      """
        | select id, name, init_version_code, init_version_name,
        | curr_version_code, curr_version_name, FORMATDATETIME(last_update, 'yyyy-MM-dd HH:mm:ss') last_update
        | from app_info where id = {id}
      """.stripMargin

    DB.withConnection(implicit conn => SQL(sql).on('id -> appId).single(appInfo))
  }

  def readAll: List[AppInfo] = {
    val sql =
      """
        | select id, name, init_version_code, init_version_name,
        | curr_version_code, curr_version_name, FORMATDATETIME(last_update, 'yyyy-MM-dd HH:mm:ss') last_update
        | from app_info order by last_update desc
      """.stripMargin

    DB.withConnection(implicit conn => SQL(sql).as(appInfo *))
  }

  def reset(id: Long) = {
    val sql =
      """
        | update app_info set init_version_code = curr_version_code, init_version_name = curr_version_name,
        | last_update = CURRENT_TIMESTAMP(), curr_version_code = NULL, curr_version_name = NULL
        | where curr_version_code is not null and id = {id}
      """.stripMargin

    DB.withConnection(implicit conn => SQL(sql).on('id -> id).executeUpdate())
  }

  def update(app: AppInfo) = {
    val sql =
      """
        | update app_info set init_version_code = {init_code}, init_version_name = {init_name},
        | curr_version_code = {curr_code}, curr_version_name = {curr_name},
        | last_update = CURRENT_TIMESTAMP()
        | where id = {id}
      """.stripMargin

    DB.withConnection(implicit conn => SQL(sql).on('init_code -> app.initVersionCode,
      'init_name -> app.initVersionName, 'curr_code -> app.currentVersionCode,
      'curr_name -> app.currentVersionName, 'id -> app.id).executeUpdate())
  }
}