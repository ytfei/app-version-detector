package seven

import akka.actor.{Cancellable, Actor}
import scala.concurrent.duration._
import play.api._
import models.App

/**
 * Created by evans on 5/27/14.
 */
class PollingActor(app: Application) extends Actor {

  val detector = new MattersAppVersionDetector(app)
  var timerRef: Option[Cancellable] = None

  import context.dispatcher

  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()

    val interval = app.configuration.getInt("api.lookup.interval").getOrElse(10)
    scheduleTrigger(interval)
  }

  override def receive = {
    case DoPoll => checkAppVersion
    case UpdateInterval(v) if v > 10 => scheduleTrigger(v)
    case _ =>
  }

  def checkAppVersion: Unit = {
    val apks = App.readAll

    for (apk <- apks) {
      detector.lookup(apk.name) match {
        case info@AppInfo(_, versionCode, versionName, lastUpdate) =>
          Logger.info(s"Found app info: ${info.toString}")
        case error: Error =>
          Logger.error(s"Failed to lookup app info for: $apk with error: ${error.toString}")
      }
    }
  }

  def scheduleTrigger(interval: Int) = {
    timerRef match {
      case Some(ref) => ref.cancel()
      case None =>
    }

    timerRef = Option(context.system.scheduler.schedule(Duration(1000, MILLISECONDS), Duration(interval, MINUTES), self, DoPoll))
  }
}

sealed trait Command

object DoPoll extends Command

case class UpdateInterval(value: Int) extends Command
