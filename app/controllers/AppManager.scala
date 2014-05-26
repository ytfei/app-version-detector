package controllers

import play.api.mvc.{Action, Controller}
import models.App

/**
 * Created by evans on 5/27/14.
 *
 * Add / Remove app
 */
object AppManager extends Controller {
  def index = Action { implicit req =>
    Ok(views.html.appList(App.readAll))
  }

  def addApp(app: String) = Action { implicit req =>
    if (App.create(App(0, app)) > 0)
      Redirect(routes.AppManager.index)
    else
      BadRequest("Failed to create app: " + app)
  }

  def removeApp(id: Long) = Action { implicit req =>
    if (App.delete(id) > 0)
      Redirect(routes.AppManager.index)
    else
      BadRequest("Failed to remove app: " + id)
  }
}
