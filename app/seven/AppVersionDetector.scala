package seven

/// ----------------------
sealed trait QueryResult

case class AppInfo(name: String, versionCode: Int, versionName: Option[String], lastUpdate: Option[String]) extends QueryResult

case class Error(statusCode: Int, message: String) extends QueryResult

/// ----------------------
trait AppVersionDetector {
  def lookup(packageName: String): QueryResult
}

import play.api._
import play.api.libs.ws._
import play.api.libs.json._

import scala.concurrent._
import scala.concurrent.duration._


/**
 * An implementation based on 42matters web api: https://42matters.com/api/lookup
 */
class MattersAppVersionDetector(app: Application) extends AppVersionDetector {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  private val conf = app.configuration

  val accessToken = conf.getString("api.access_token")
  val lookupUrl = conf.getString("api.lookup.url")
  val threshold = conf.getInt("api.lookup.threshold")

  override def lookup(packageName: String): QueryResult = {
    val future = WS.url(lookupUrl.get)
      .withQueryString("access_token" -> accessToken.get, "p" -> packageName)
      .get.map(resp => {

      Logger.debug(resp.body)

      val json = resp.json
      json \ "market_update" match {
        case _: JsUndefined =>
          // error happened, parse result as Error Status message
          val statusCode = (json \ "statusCode").as[Int]
          val message = (json \ "message").as[String]

          Error(statusCode, message)

        case _: JsValue =>
          // parse message response
          val versionCode = (json \ "version_code").as[Int]
          val versionName = (json \ "version").as[String]
          val lastUpdate = (json \ "market_update").as[String]

          AppInfo(packageName, versionCode, Some(versionName), Some(lastUpdate))
      }

    })

    Await.result(future, Duration(3000, MILLISECONDS))
  }
}

object MattersAppVersionDetector {
  def run = {
    new play.core.StaticApplication(new java.io.File("."))

    val d = new MattersAppVersionDetector(Play.current)
    println(d.lookup("com.google.android.apps.plus"))
  }
}