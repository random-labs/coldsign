
import scala.scalajs.js.JSApp

import org.strllar.js.stellar

object Main extends JSApp{
  def main(): Unit = {
    println("hello stellar")
    println((new stellar.Seed).random().to_json)
  }
}
