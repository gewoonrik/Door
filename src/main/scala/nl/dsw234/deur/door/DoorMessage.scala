package nl.dsw234.deur.door

trait DoorMessage

trait FromDoorMessage extends DoorMessage {

}

object FromDoorMessage {
  private def unsignedIntToLong(b : Seq[Byte]) : Long = {
    var l  = 0L
    l |= b(0) & 0xFF
    l <<= 8
    l |= b(1) & 0xFF
    l <<= 8
    l |= b(2) & 0xFF
    l <<= 8
    l |= b(3) & 0xFF
    l
  }

  def createMessage(bytes : Seq[Byte]) : FromDoorMessage = bytes(0) match {
    case 'i' =>
      val id = unsignedIntToLong(bytes.toArray.drop(1))
      IdentificationMessage(id)
    case 'b' =>
      BellIsRinging()
  }

  def getLengthMessage(byte : Byte) : Int = byte match  {
    case 'i' => 5
  }
}

case class IdentificationMessage(cardId : Long) extends FromDoorMessage

case class BellIsRinging() extends FromDoorMessage



trait ToDoorMessage extends DoorMessage {
  def toBytes() : Array[Byte]
}


case class OpenDoorMessage() extends ToDoorMessage {
  override def toBytes(): Array[Byte] = Array('o'.toByte)
}