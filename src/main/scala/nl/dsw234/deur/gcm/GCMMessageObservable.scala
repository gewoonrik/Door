package nl.dsw234.deur.gcm

import nl.dsw234.deur.user.User
import rx.lang.scala.{Observable, Subscriber}
import scala.collection.JavaConverters._

object GCMMessageObservable {
  private def createObservable(client : SmackCcsClient) : Observable[Map[String, AnyRef]] = {
    Observable((subscriber: Subscriber[Map[String, AnyRef]]) =>{
      client.addMessageListener(new MessageListener {
        override def handleMessage(message: java.util.Map[String, AnyRef]): Unit = {
          subscriber.onNext(message.asScala.toMap)
        }
      })

    })
  }

  def getObservable(client: SmackCcsClient) = {
    //we only accept messages from valid users
    val observable = createObservable(client)
    observable.map(message => {
      val appId = message.get("from").get.asInstanceOf[String]
      val maybeUser = User.getUserByAppId(appId)
      if(maybeUser.isDefined) {
        Option(OpenDoorMessage(maybeUser.get))
      }
      else {
        None
      }
    })
      .filter(_.isDefined).map[GCMMessage](_.get)
  }
}
