package nl.dsw234.deur.door

import java.io.{FileInputStream, File}
import javazoom.jl.player.Player

import scala.util.Random

class SoundPlayer(directory: String) {
  var isPlaying = false;

  def playRandom(): Unit =  {
    if(!isPlaying) {
      val files = new File(directory).listFiles().toList.filter(_.isFile)
      isPlaying = true;
      val rand = new Random()
      val randIndex = rand.nextInt(files.length)
      val randomFile = files(randIndex)
      new Thread {
        override def run(): Unit = {
          new Player(new FileInputStream(randomFile)).play()
          isPlaying = false
        }
      }.start()
    }
  }
}
