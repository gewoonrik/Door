package nl.dsw234.deur.user

import spray.json._


//cardIds is a list of nfc card ids
//appIds is a list of GCM Ids


case class User(name: String, cardIds : Seq[Long], appIds: Seq[String])

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(User.apply)
}

object User {
  import UserJsonProtocol._


  def getUsers: Seq[User] =  {
    scala.io.Source.fromFile("users.json").getLines().mkString.parseJson.convertTo[List[User]]
  }

  def isAuthenticatedCardId(cardId : Long) : Boolean = {
    getUsers
      .exists(_.cardIds.contains(cardId))
  }

  def isAuthenticatedAppId(appId : String) : Boolean = {
    getUsers
      .exists(_.appIds.contains(appId))
  }

  def getUserByAppId(appId : String) : Option[User] = {
    getUsers
      .find(_.appIds.contains(appId))
  }
}
