
import scala.scalajs.js

import org.strllar.js.{stellar, sjcl}

import japgolly.scalajs.react._, vdom.prefix_<^._, extra._

import org.scalajs.dom

import org.scalajs.jquery.jQuery;

import scala.scalajs.js.annotation.JSExport



@JSExport("Wallet")
object Wallet{
  var theWallet = None:Option[String]
  @JSExport
  def parse(hexstr: js.String) {
    dom.console.log("parsing" + hexstr)
    theWallet = Some(hexstr)
  }
}

object EntropyPreparePage {
  class MyBackend extends SetInterval

  def coloredProgressBar(ready :Boolean, progress :Double) = {
    val perc = "%2.1f%%".format(progress*100)

    <.div(^.`class` := "progress",
      <.div(
        ^.classSet1("progress-bar",
          "progress-bar-success" -> ready,
          "progress-bar-danger" -> (ready == false)),
        ^.role := "progressbar",
        "aria-valuenow".reactAttr := "40",
        "aria-valuemin".reactAttr := "0",
        "aria-valuemax".reactAttr := "100",
        ^.width := perc,
        <.span(^.`class` := "sr-only", perc),
        perc
      )
    )
  }

  def spinningCog = <.i(^.`class` := "fa fa-cog fa-spin fa-3x")

  val page = ReactComponentB[Unit](getClass().getName())
    .initialState((false, 0.0))
    .backend(_ => new MyBackend)
    .render((_, S, B) => {
      <.div(
        ^.cls := "container-fluid",
        <.div(
          ^.cls := "row",
          <.div(
            ^.cls := "col-md-4 col-md-offset-4",
            <.div(
              <.p(<.b("STR38 Wallet")),
              <.p(<.i("accumulating randomness...")),
              <.p("something may help"),
              <.ul(<.li( "move mouse around..."),
                <.li( "monkey typing...")
              )
            ),
            <.div(
              spinningCog
            ),
            coloredProgressBar(S._1, S._2),

            <.div(
              if (S._1)
                <.button(
                  ^.cls := "btn btn-primary btn-lg btn-block",
                  ^.`type` := "button",
                  Wallet.theWallet match {
                    case Some(_) => "Open Wallet";
                    case None => "Create Wallet";
                  }
                )
              else
                <.span("")
            )
          )
        )
      )
      
    })
    .componentDidMount(c => {
      c.backend.setInterval({c.setState((sjcl.veryrandom.isReady(), sjcl.veryrandom.getProgress()))}, 500)
    })
    .configure(SetInterval.install)
    .buildU
}

object GenerateAddressPage {
  case class State(seed: String)

  class Backend(T :BackendScope[Unit, State]) {
    def genRandom() = {
      T.setState(randomState)
    }
  }
  def randomState = {
    val seed = new stellar.Seed
//    seed.random()
    State(seed.to_json().toString)
  }

  val page = ReactComponentB[Unit](getClass().getName())
    .initialState{randomState}
    .backend(new Backend(_))
    .render((_, S, B) => {
      val seed = new stellar.Seed
      seed.parse_json(S.seed)


      <.div(
        <.p("Secret seed"),
        <.input(^.value := seed.to_json, ^.`type` := "text"),
        <.button("Generate", ^.onClick --> B.genRandom),
        <.p("Public Address"),
        <.p(seed.get_key().get_address().to_json().toString),
        <.span()
      )
    })
      .buildU
}

object Main extends js.JSApp{

  def checkWallet() {
    if (Wallet.theWallet isDefined) {
    }
    else {
    }
  }

  def main(): Unit = {
    dom.console.log(">main()");
    jQuery(() => {
      sjcl.veryrandom.reset()
      EntropyPreparePage.page() render dom.document.getElementById("content")
    })

  }
}
