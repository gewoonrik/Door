package nl.dsw234.deur.user

//cardIds is a list of nfc card ids
//appIds is a list of GCM Ids
case class User(name: String, cardIds : Seq[Long], appIds: Seq[String])

object User {
  private val users = List(User("Rik", List(), List()))

  def getUsers: Seq[User] =  {
    users
  }

  def isAuthenticatedCardId(cardId : Long) : Boolean = {
    users
      .exists(_.cardIds.contains(cardId))
  }

  def isAuthenticatedAppId(appId : String) : Boolean = {
    users
      .exists(_.appIds.contains(appId))
  }

  def getUserByAppId(appId : String) : Option[User] = {
    users
      .find(_.appIds.contains(appId))
  }
}
