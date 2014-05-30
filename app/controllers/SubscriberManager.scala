package controllers

import play.api.mvc.{Action, Controller}
import models.Subscriber
import play.api.data._
import play.api.data.Forms._
import play.api.Play
import Play.current

/**
 * Created by evans on 5/27/14.
 */
object SubscriberManager extends Controller {

  def index = Action { implicit req =>
    Ok(views.html.subscriberList(Subscriber.readAll, subscriberForm))
  }

  val subscriberForm = Form(
    tuple("email" -> email.verifying(_.contains("@seven.com")),
      "name" -> text)
  )

  def addSubscriber = Action { implicit req =>
    subscriberForm.bindFromRequest().fold(
      formWithError => {
        BadRequest(views.html.subscriberList(Subscriber.readAll, formWithError))
      }, data => {
        if (Subscriber.create(Subscriber(0, data._1, Option(data._2))) > 0) {
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

  // For test
  def sendMail(content: String) = Action {
    import com.typesafe.plugin._

    val mail = use[MailerPlugin].email
    mail.setSubject("mailer")
    mail.setRecipient("dduyoung@yahoo.com")
    mail.setFrom("dduyoung@yahoo.com")
    mail.send(content)

    Ok
  }
}
