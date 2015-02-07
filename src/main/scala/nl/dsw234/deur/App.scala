package nl.dsw234.deur

import nl.dsw234.deur.user.{User}
import nl.dsw234.deur.door._
import nl.dsw234.deur.gcm.{SmackCcsClient, GCMMessageObservable}
import rx.lang.scala.schedulers.IOScheduler

import collection.JavaConversions._
object App extends App {

  def getCcsClient : SmackCcsClient = {
    val senderId = 0
    val password = ""

    val ccsClient = new SmackCcsClient()

    ccsClient.connect(senderId, password)
    ccsClient
  }

  override
  def main(args: Array[String]) {

    val serialHandler = new SerialHandler()

    val ccsClient = getCcsClient
    val androidObservable = GCMMessageObservable.getObservable(ccsClient).observeOn(IOScheduler())
    androidObservable
      .subscribe(_ => serialHandler.sendMessage(OpenDoorMessage()))



    val doorObservable = serialHandler.getMessageObservable.observeOn(IOScheduler())

    doorObservable
      .filter(_.isInstanceOf[IdentificationMessage]).map(_.asInstanceOf[IdentificationMessage])
      .filter(message => User.isAuthenticatedCardId(message.cardId))
      .subscribe(_ => serialHandler.sendMessage(OpenDoorMessage()))


    doorObservable
      .filter(_.isInstanceOf[BellIsRinging])
      .zipWith(User.getUsers)((_, user) => user)
      .subscribe(user => {
        user
          .appIds
          .foreach(sendMessage(_, ccsClient))
      })


    while (true){
      Thread.sleep(10000);
    }
  }

  def sendMessage(regId : String, client : SmackCcsClient): Unit =  {
    val messageId = client.nextMessageId()
    val payload = scala.collection.mutable.Map[String, String]()
    val collapseKey = "sample"
    val timeToLive = 10000L
    val message = SmackCcsClient.createJsonMessage(regId, messageId, payload,
      collapseKey, timeToLive, true)
    client.sendDownstreamMessage(message)
  }
}
