
import scala.scalajs.js

import org.strllar.js.{stellar, sjcl}

import japgolly.scalajs.react._, vdom.prefix_<^._, extra._

import org.scalajs.dom

import org.scalajs.jquery.{jQuery, JQuery};

import scala.scalajs.js.annotation.JSExport



@JSExport("Wallet")
object Wallet{
  var theWallet = None:Option[String]
  @JSExport
  def parse(hexstr: String) {
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
        ^.classSet1("determinate"),
        ^.width := perc,
        <.span(^.`class` := "sr-only", perc),
        perc
      )
    )
  }

  def entropyControl = ReactComponentB[Unit](getClass().getName())
    .initialState((false, 0.0))
    .backend(_ => new MyBackend)
    .render((_, S, B) => {
      <.div( ^.cls := "row",
        <.div( ^.cls := "col l11", coloredProgressBar(S._1, S._2)),
        <.div( ^.cls := "col l1",
          <.a(^.classSet1("btn-flat tooltipped", "disabled"->(S._1 == false)),
            "data-tooltip".reactAttr := "Reset entropy",
            "data-delay".reactAttr := "50",
            "data-position".reactAttr := "left",
            ^.onClick --> sjcl.veryrandom.reset(),
            <.i(^.cls := "tiny mdi-navigation-refresh")))
      )
    })
    .componentDidMount(c => {
      JQueryMaterialize.tooltip(jQuery(".tooltipped"))
      c.backend.setInterval({c.setState((sjcl.veryrandom.isReady(), sjcl.veryrandom.getProgress()))}, 500)
    })
    .configure(SetInterval.install)
    .buildU


  def spinningCog = <.i(^.`class` := "fa fa-cog fa-spin fa-3x")

  import japgolly.scalajs.react.vdom.all._
  val page = ReactComponentB[Unit](getClass().getName())
    .render((_) => {
      <.div(
        ^.cls := "row",
        <.div(
          ^.cls := "col s4 offset-s4",
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
          entropyControl()// ,
          // <.div(
          //   if (S._1)
          //     <.button(
          //       ^.cls := "btn waves-effect waves-light",
          //       ^.`type` := "button",
          //       Wallet.theWallet match {
          //         case Some(_) => "Open Wallet";
          //         case None => "Create Wallet";
          //       }
          //     )
          //   else
          //     <.span("")
          // )
        )
      )
      
    })
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

object JQueryKeypad {
  trait prototype extends JQuery {
    def keypad(s :js.Any): this.type = js.native
  }
  implicit def jq2keypad(jq: JQuery): prototype = jq.asInstanceOf[prototype]

  def apply(jq: JQuery) {
    jq.keypad(js.Dynamic.literal(
      showOn = "focus",
      showAnim = "",
      randomiseAlphabetic = true,
      randomiseNumeric = true,
      // randomiseOther = true,
      layout = js.Dynamic.global.`$`.keypad.qwertyLayout)
    )
  }
}

object JQueryMaterialize {
  trait prototype extends JQuery {
    def tooltip(s :js.Object): this.type = js.native
  }
  implicit def jq2mat(jq: JQuery): prototype = jq.asInstanceOf[prototype]

  def tooltip(jq: JQuery) {
    jq.tooltip(js.Dynamic.literal(
      delay = 50)
    )
  }
}

object WalletApp {
  import japgolly.scalajs.react.vdom.all._


  val newSeedPanel = ReactComponentB[Unit](getClass().getName())
    .render((_) => {
      <.h1("Create Wallet")
    }).buildU

  val authPanel = ReactComponentB[Unit](getClass().getName())
      .render((_) => {
        <.p(<.span("Password"), <.input(^.`type` := "password", ^.id := "randomKeyboard"))
      })
    .componentDidMount(c => {
      JQueryKeypad(jQuery("#randomKeyboard"))
    })
    .buildU

  val page = ReactComponentB[Unit](getClass().getName())
    .render((_) => {      
      <.div(
        ^.cls := "row",
        <.div(
          ^.cls := "col l4",
          "Stellar"),
        <.div(
          ^.cls := "col l8",
          "Wallet"),
        newSeedPanel(),
        authPanel()
      )

    }).buildU
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
      WalletApp.page() render dom.document.getElementById("content")
      EntropyPreparePage.entropyControl() render dom.document.getElementById("entropy_ctrl")
    })
  }
}
