package nl.dsw234.deur

import nl.dsw234.deur.user.{User}
import nl.dsw234.deur.door._
import nl.dsw234.deur.gcm.{SmackCcsClient, GCMMessageObservable}
import spray.json._

import rx.lang.scala.schedulers.IOScheduler

import collection.JavaConversions._

object App extends App {

  def getCcsClient : SmackCcsClient = {
    import ConfigJsonProtocol._
    val config = scala.io.Source.fromFile("config.json").getLines.mkString.parseJson.convertTo[Config]
    val ccsClient = new SmackCcsClient()
    ccsClient.connect(config.senderId, config.password)
    ccsClient
  }

  override
  def main(args: Array[String]) {
    val ccsClient = getCcsClient
    val serialHandler = new SerialHandler()
    val sound = new SoundPlayer("sounds")

    val androidObservable = GCMMessageObservable.getObservable(ccsClient).observeOn(IOScheduler())
    val doorObservable = serialHandler.getMessageObservable.observeOn(IOScheduler())

    val openDoorObservable = doorObservable
      .filter(_.isInstanceOf[IdentificationMessage]).map(_.asInstanceOf[IdentificationMessage])
      .filter(message => User.isAuthenticatedCardId(message.cardId))
      .merge(androidObservable)

    openDoorObservable
      .subscribe(_ => serialHandler.sendMessage(OpenDoorMessage()))
    openDoorObservable
      .subscribe(_ => sound.playRandom())

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
