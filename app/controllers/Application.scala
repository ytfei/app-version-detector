package controllers

import play.api._
import play.api.mvc._
import models.AppInfo

object Application extends Controller {

  def index = Action {
    Ok(views.html.index(AppInfo.readAll))
  }

}