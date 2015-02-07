package nl.dsw234.deur.door


import jssc.{SerialPortEvent, SerialPortEventListener, SerialPort}
import rx.lang.scala.{Observer, Subscriber, Observable}

class SerialHandler {
  val serialPort = new SerialPort("COM1");
  serialPort.openPort()
  serialPort.setParams(9600, 8, 1, 0)
  serialPort.setEventsMask(SerialPort.MASK_RXCHAR)

  private def getObservable = {
    Observable[Byte]((subscriber: Subscriber[Byte]) => {
      serialPort.addEventListener(new SerialPortEventListener {
        override def serialEvent(event: SerialPortEvent): Unit = {
          if(event.isRXCHAR) {
            val bytes = serialPort.readBytes()
            bytes.foreach((byte: Byte) =>
              subscriber.onNext(byte)
            )
          }
        }
      })
    })
  }

  def getMessageObservable = {
    Observable.create[FromDoorMessage]((observer: Observer[FromDoorMessage]) => {
      val buffer = scala.collection.mutable.ArrayBuffer[Byte]()
      var lengthNeeded = 0
      val observable = getObservable
      observable.subscribe((byte: Byte) => {
        if (buffer.size == 0) {
          lengthNeeded = getMessageSize(byte)
        }
        buffer += byte
        if (buffer.size == lengthNeeded) {
          observer.onNext(getMessage(buffer))
          buffer.clear()
          lengthNeeded = 0
        }
      })
    })
  }

  private def getMessageSize(byte: Byte): Int = byte match  {
    case 'i' => 5
    case _ => 0
  }

  private def getMessage(bytes: Seq[Byte]): FromDoorMessage = bytes(0) match {
    case 'i' =>
      val id = unsignedIntToLong(bytes.toArray.drop(1))
      IdentificationMessage(id)
    case 'b' =>
      BellIsRinging()
  }

  def sendMessage(message : ToDoorMessage): Unit = message match  {
    case OpenDoorMessage() => serialPort.writeByte('o')
    case _ => //doe niks
  }

  def unsignedIntToLong(b : Seq[Byte]) : Long = {
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



}
