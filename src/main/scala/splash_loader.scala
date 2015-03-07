import org.scalajs.dom, dom._
import scala.concurrent._, duration.Duration

object SplashLoader {

  val result = Promise[String]()

  type renderType = (dom.Element) => Unit

  def loadWithRender(src :String, render :renderType, timeout :Duration): Future[String] = {

    val div = dom.document.createElement("div")
    dom.document.body.appendChild(div)
    render(div)

    val frame = dom.document.createElement("iframe").asInstanceOf[html.IFrame]
    val id = "splashLoader_"+src
    frame.id = id
    frame.src = src
    frame.onload = (e: dom.Event) => {onLoad(id)}
    frame.style.display = "none"
    dom.document.body.appendChild(frame)

    dom.setTimeout(() => {result.tryFailure(new Exception(s"SplashLoader: load $src time out"))}, timeout.toMillis)

    result.future
  }

  def onLoad(id :String) {
    val frame = dom.document.getElementById(id).asInstanceOf[html.IFrame]
    val content = frame.contentWindow.document.body.children(0).innerHTML
    result.success(content)
  }
}
