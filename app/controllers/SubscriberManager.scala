package controllers

import play.api.mvc.{Action, Controller}
import models.Subscriber

/**
 * Created by evans on 5/27/14.
 */
object SubscriberManager extends Controller {

  def index = Action { implicit req =>
    Ok(views.html.subscriberList(Subscriber.readAll))
  }

  def addSubscriber(email: String, name: Option[String]) = Action {
    Ok
  }

  def removeSubscriber(id: Long) = Action {
    Ok
  }
}
