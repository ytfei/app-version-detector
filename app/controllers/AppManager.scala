package controllers

import play.api.mvc.{Action, Controller}
import models.App
import play.api.data._
import play.api.data.Forms._

/**
 * Created by evans on 5/27/14.
 *
 * Add / Remove app
 */
object AppManager extends Controller {
  def index = Action { implicit req =>
    Ok(views.html.appList(App.readAll))
  }

  case class AppData(name: String) // Form model

  val appForm = Form(mapping("name" -> nonEmptyText)(AppData.apply)(AppData.unapply))

  def addApp = Action { implicit req =>
    appForm.bindFromRequest().fold(
      formWithError => {
        BadRequest("Data you submitted is invalidate.")
      },
      appData => {
        val app = appData.name
        if (App.create(App(0, app)) > 0)
          Redirect(routes.AppManager.index)
        else
          BadRequest("Failed to create app: " + app)
      })
  }

  def removeApp(id: Long) = Action { implicit req =>
    if (App.delete(id) > 0)
      Redirect(routes.AppManager.index)
    else
      BadRequest("Failed to remove app: " + id)
  }
}
