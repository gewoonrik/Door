package nl.dsw234.deur.door

trait DoorMessage

trait FromDoorMessage extends DoorMessage

case class IdentificationMessage(cardId : Long) extends FromDoorMessage

case class BellIsRinging() extends FromDoorMessage



trait ToDoorMessage extends DoorMessage

case class OpenDoorMessage() extends ToDoorMessage