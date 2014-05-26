package seven

/// ----------------------
sealed trait QueryResult

case class AppInfo(name: String, versionCode: Int, versionName: Option[String], lastUpdate: Option[String]) extends QueryResult

case class Error(statusCode: Int, message: String) extends QueryResult

/// ----------------------
trait AppVersionDetecter {
  def lookup(packageName: String): QueryResult
}

import play.api._
import play.api.libs.ws._
import play.api.libs.json._

import scala.concurrent._
import scala.concurrent.duration._


/**
 * An implimentation based on 42matters web api: https://42matters.com/api/lookup
 */
class MattersAppVersionDetecter extends AppVersionDetecter {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  private val conf = Play.current.configuration

  val accessToken = conf.getString("api.access_token")
  val lookupUrl = conf.getString("api.lookup.url")
  val threshold = conf.getInt("api.lookup.threshold")

  override def lookup(packageName: String): QueryResult = {
    val future = WS.url(lookupUrl.get)
      .withQueryString("access_token" -> accessToken.get, "p" -> packageName)
      .get.map(resp => {

      val json = resp.json
      json \ "market_update" match {
        case _: JsValue =>
          // parse message response
          val versionCode = (json \ "version_code").as[Int]
          val versionName = (json \ "version").as[String]
          val lastUpdate = (json \ "market_update").as[String]

          AppInfo(packageName, versionCode, Some(versionName), Some(lastUpdate))

        case _ =>
          // error happened, parse result as Error Status message
          val statusCode = (json \ "statusCode").as[Int]
          val message = (json \ "message").as[String]

          Error(statusCode, message)
      }

    })

    Await.result(future, Duration(3000, MILLISECONDS))
  }
}

object MattersAppVersionDetecter {
  def run = {
    new play.core.StaticApplication(new java.io.File("."))

    val d = new MattersAppVersionDetecter
    println(d.lookup("com.google.android.apps.plus"))
  }
}