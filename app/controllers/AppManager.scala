package controllers

import play.api.mvc.{Action, Controller}
import models.AppInfo
import play.api.data._
import play.api.data.Forms._
import scala.collection.mutable.ArrayBuffer
import seven.{UpdateInterval, PollingActor}

import play.api.Play.current

/**
 * Created by evans on 5/27/14.
 *
 * Add / Remove app
 */
object AppManager extends Controller {
  def index = Action { implicit req =>
    Ok(views.html.appList(AppInfo.readAll, appForm))
  }

  val appForm: Form[AppData] = Form(
    mapping(
      "name" -> nonEmptyText,
      "version_code" -> longNumber,
      "version_name" -> optional(text)
    )(AppData.apply)(AppData.unapply)
  )

  def addApp = Action { implicit req =>
    appForm.bindFromRequest().fold(
      formWithError => {
        val buff = new ArrayBuffer[String]

        formWithError.errors.foreach(e => (e.key, e.message) match {
          case ("version_code", _) => buff += "Malformed version code, integer number required!"
          case ("name", _) => buff += "Malformed app package name"
        })

        BadRequest(views.html.appList(AppInfo.readAll, formWithError, Option(buff.mkString("<br />"))))
      }, app => {
        if (AppInfo.create(AppInfo(0, app.name, app.versionCode, app.versionName.getOrElse(""))) > 0)
          Redirect(routes.AppManager.index)
        else
          BadRequest("Failed to create app: " + app)
      })
  }

  def removeApp(id: Long) = Action { implicit req =>
    if (AppInfo.delete(id) > 0)
      Redirect(routes.AppManager.index)
    else
      BadRequest("Failed to remove app: " + id)
  }

  def resetApp(id: Long) = Action { implicit req =>
    if (AppInfo.reset(id) > 0) {
      Redirect(routes.AppManager.index)
    } else {
      Redirect(routes.AppManager.index) // todo: how to deal with the failure
    }
  }

  def updateInterval(interval: Int) = Action { implicit req =>
    import play.api.libs.concurrent.Akka

    val x = Akka.system.actorSelection("/user/" + PollingActor.name)
    x ! UpdateInterval(interval)
    Ok(x.toString())
  }
}

// Form model
case class AppData(name: String, versionCode: Long, versionName: Option[String])
