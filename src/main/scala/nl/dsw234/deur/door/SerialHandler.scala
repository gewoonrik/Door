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
    val observable = getObservable
    Observable.create[FromDoorMessage]((observer: Observer[FromDoorMessage]) => {
      val buffer = scala.collection.mutable.ArrayBuffer[Byte]()
      var lengthNeeded = 0
      observable.subscribe((byte: Byte) => {
        if (lengthNeeded == 0) {
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

  private def getMessageSize(byte: Byte): Int = FromDoorMessage.getLengthMessage(byte)

  private def getMessage(bytes: Seq[Byte]): FromDoorMessage = FromDoorMessage.createMessage(bytes)

  def sendMessage(message : ToDoorMessage): Unit = serialPort.writeBytes(message.toBytes())




}
