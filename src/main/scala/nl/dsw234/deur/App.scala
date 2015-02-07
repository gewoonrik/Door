package nl.dsw234.deur

import nl.dsw234.deur.user.{User}
import nl.dsw234.deur.door._
import nl.dsw234.deur.gcm.{SmackCcsClient, GCMMessageObservable}
import org.json.simple.{JSONObject, JSONValue}
import rx.lang.scala.schedulers.IOScheduler

import collection.JavaConversions._

object App extends App {

  def getCcsClient : SmackCcsClient = {
    val config = JSONValue.parse(scala.io.Source.fromFile("config.json").getLines.mkString).asInstanceOf[JSONObject]

    val gcm = config.get("GCM").asInstanceOf[JSONObject]
    val senderId = gcm.get("senderId").asInstanceOf[Long]
    val password = gcm.get("password").asInstanceOf[String]
    val ccsClient = new SmackCcsClient()

    ccsClient.connect(senderId, password)
    ccsClient
  }

  override
  def main(args: Array[String]) {


    val ccsClient = getCcsClient

    val serialHandler = new SerialHandler()

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
