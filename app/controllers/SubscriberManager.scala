package controllers

import play.api.mvc.{Action, Controller}
import models.Subscriber
import play.api.data._
import play.api.data.Forms._

/**
 * Created by evans on 5/27/14.
 */
object SubscriberManager extends Controller {

  def index = Action { implicit req =>
    Ok(views.html.subscriberList(Subscriber.readAll))
  }

  // Form Model
  case class SubscriberData(email: String, name: String)

  val subscriberForm = Form(mapping("email" -> email, "name" -> text)(SubscriberData.apply)(SubscriberData.unapply))

  def addSubscriber = Action { implicit req =>
    subscriberForm.bindFromRequest().fold(
      formWithError => {
        BadRequest("Invalid subscriber info")
      }, data => {
        if (Subscriber.create(Subscriber(0, data.email, Option(data.name))) > 0) {
          Redirect(routes.SubscriberManager.index)
        } else {
          BadRequest("Failed to create subscriber: " + data)
        }
      })
  }

  def removeSubscriber(id: Long) = Action {
    if (Subscriber.delete(id) > 0) {
      Redirect(routes.SubscriberManager.index)
    } else {
      BadRequest("Failed to create subscriber: " + id)
    }
  }
}
