package nl.dsw234.deur

import spray.json.DefaultJsonProtocol


object ConfigJsonProtocol extends DefaultJsonProtocol {
  implicit val configFormat = jsonFormat2(Config.apply)
}
case class Config(senderId : Long, password: String)
