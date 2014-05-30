package seven

import akka.actor.{Cancellable, Actor}
import scala.concurrent.duration._
import play.api._
import scala.collection.mutable.ArrayBuffer
import models.Subscriber
import scala.util.Try
import scala.util.{Failure, Success => Succ}

/**
 * Created by evans on 5/27/14.
 */

object PollingActor {
  val name = "PollingActor"
}

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
    case DoPoll => checkAppVersion()
    case UpdateInterval(v) if v > 10 => scheduleTrigger(v)
    case _ =>
  }

  def checkAppVersion(): Unit = {
    val message = new ArrayBuffer[String]()

    def doPoll() = {
      val apks = models.AppInfo.readAll
      for (apk <- apks) {
        detector.lookup(apk.name) match {
          case info@AppInfo(_, versionCode, versionName, lastUpdate) =>
            Logger.info(s"Found app info: ${info.toString}")


            if (versionCode > 0) {
              val (v, n) = if (apk.currentVersionCode.isDefined)
                (apk.currentVersionCode.get, apk.currentVersionName.getOrElse(""))
              else (apk.initVersionCode, apk.initVersionName)

              if (v != versionCode) {
                models.AppInfo.update(apk.copy(currentVersionCode = Option(versionCode),
                  currentVersionName = versionName))

                message +=
                  s"""
                    | ${apk.name} upgraded to "$versionCode / ${versionName.getOrElse("unknown")}" from "$v / ${if (n != null && n.size > 0) n else "unknown"}"
                  """.stripMargin
              }
            }

          case error: Error =>
            Logger.error(s"Failed to lookup app info for: $apk with error: ${error.toString}")
        }
      }
    }

    Try(doPoll()) match {
      case Failure(e) =>
        Logger.warn(e.getMessage, e)

      case _ => // Success
    }

    if (message.size > 0) {
      val recipients = Subscriber.readAll.map(_.email).toSeq
      sendMail(message.mkString("\n"), recipients)
    }
  }

  def sendMail(content: String, recipients: Seq[String]) = {
    Logger.info("Send Mail" + content + " to " + recipients.mkString(","))
    MailSender.send(recipients.mkString(","), "", "App updated!", content)
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
