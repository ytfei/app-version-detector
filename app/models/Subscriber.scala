package models

/**
 * Created by evans on 5/26/14.
 */
case class Subscriber(id: Long, email: String, name: Option[String])

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

object Subscriber {
  def create(subscriber: Subscriber) = {
    val sql = "insert into subscriber(name, email) values({name}, {email})"

    DB.withConnection(implicit conn => SQL(sql).on('name -> subscriber.name.getOrElse(""),
      'email -> subscriber.email).executeUpdate())
  }

  val subscriber = long("id") ~ str("name") ~ str("email") map {
    case id ~ name ~ email => Subscriber(id, email, Option(name))
  }

  def read(subId: Int): Subscriber = {
    val sql = "select id, name, email from subscriber where id = {id}"

    DB.withConnection(implicit conn => SQL(sql).on('id -> subId).single(subscriber))
  }

  def readAll: List[Subscriber] = {
    val sql = "select id, name, email from subscriber"

    DB.withConnection(implicit conn => SQL(sql).as(subscriber *))
  }

  def delete(subId: Int) = {
    val sql = "delete from subscriber where id = {id}"

    DB.withConnection(implicit conn => SQL(sql).on('id -> subId).executeUpdate())
  }
}