package nl.dsw234.deur.gcm

import nl.dsw234.deur.user.User

class GCMMessage(from: User) {
}

case class OpenDoorMessage(from : User) extends GCMMessage(from)
{

}


