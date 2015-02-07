package nl.dsw234.deur.gcm

import nl.dsw234.deur.user.User

class GCMMessage {
}

class FromMessage(from: User) extends GCMMessage

case class OpenDoorMessage(from : User) extends FromMessage(from)

class ToMessage(to: User) extends GCMMessage


