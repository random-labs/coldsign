
import scala.scalajs.js.JSApp

import org.strllar.js.stellar

object Main extends JSApp{
  def main(): Unit = {
    println("hello stellar")
    val seed = new stellar.Seed
    seed.random()
    println(seed.to_json(), seed.get_key().get_address().to_json())
  }
}
