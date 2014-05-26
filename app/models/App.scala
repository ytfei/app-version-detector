package models

/**
 * Created by evans on 5/26/14.
 *
 * App Model
 */

case class App(id: Long, name: String)

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

object App {
  def create(app: App) = {
    val sql = "insert into app (package) values ({name})"

    DB.withConnection(implicit conn => SQL(sql).on('name -> app.name).executeUpdate())
  }

  def update(app: App) = {}

  val app =
    get[Long]("id") ~ get[String]("package") map {
      case id ~ name => App(id, name)
    }

  def read(id: Long): App = {
    val sql = "select id, package from app where id = {id}"
    DB.withConnection(implicit conn => SQL(sql).on('id -> id).single(app))
  }

  def readAll: List[App] = {
    val sql = "select id, package from app"
    DB.withConnection(implicit conn => SQL(sql).as(app *))
  }

  def delete(id: Long) = {
    val sql = "delete from app where id = {id}"

    DB.withConnection(implicit conn => SQL(sql).on('id -> id).executeUpdate())
  }
}
