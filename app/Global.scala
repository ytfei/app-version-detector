/**
 * Created by evans on 5/27/14.
 */

import akka.actor.Props
import play.api._
import play.api.libs.concurrent.Akka
import seven.PollingActor

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")

    val ref = Akka.system(app).actorOf(Props(new PollingActor(app)), name = PollingActor.name)
    Logger.info(ref.path.toString)
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }
}