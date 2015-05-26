import scala.util.{Try, Success, Failure}
import scala.concurrent.duration._

import scala.scalajs.js

import org.strllar.js.{stellar, sjcl}

import japgolly.scalajs.react._, vdom.prefix_<^._, extra._

import org.scalajs.dom

import org.scalajs.jquery.{jQuery, JQuery};

import scala.scalajs.js.annotation.JSExport

import japgolly.scalajs.react.extra._

case class WalletData (name :String, cold_seed :String, warm_seed :String, as_gateway :Boolean)
object Wallet{
  def parse(hexstr: String) :Option[WalletData] = {
    dom.console.log("parsing" + hexstr)
    Some(WalletData("", "", "", false))
  }
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
    def openModal(s :js.Object): this.type = js.native
  }
  implicit def jq2mat(jq: JQuery): prototype = jq.asInstanceOf[prototype]

  def tooltip(jq: JQuery) {
    jq.tooltip(js.Dynamic.literal(
      delay = 50)
    )
  }

  def openModal(jq: JQuery) {
    jq.openModal(js.Dynamic.literal(dismissible = false))
  }
}


object WalletApp {

  case class EntropyStatus(ready :Boolean, progress :Double)
  class AppBackend extends SetInterval with Broadcaster[EntropyStatus] {
    def doTick() = {
      broadcast(EntropyStatus(sjcl.veryrandom.isReady(), sjcl.veryrandom.getProgress()))
    }
  }

  val coloredProgressBar = ReactComponentB[AppBackend](getClass().getName())
    .initialState(EntropyStatus(false, 0.0))
    .backend(_ => new OnUnmount.Backend)
    .render((P, S, B) => {
      val progress = S.progress
      val perc = "%2.1f%%".format(progress*100)
      <.div(^.`class` := "progress",
        <.div(
          ^.classSet1("determinate"),
          ^.width := perc,
          <.span(^.`class` := "sr-only", perc),
          perc
        )
      )
    })
    .configure(Listenable.install(identity, (c) => {(a:EntropyStatus) =>{c.setState(a)}}))
    .build

  val refreshBtn = ReactComponentB[AppBackend](getClass().getName())
    .initialState(false)
    .backend(_ => new OnUnmount.Backend)
    .render((P, S, B) => {
      <.a(^.classSet1("btn-flat tooltipped", "disabled"->(S==false)),
        "data-tooltip".reactAttr := "Reset entropy",
        "data-delay".reactAttr := "50",
        "data-position".reactAttr := "left",
        ^.onClick --> sjcl.veryrandom.reset(),
        <.i(^.cls := "tiny mdi-navigation-refresh"))
    })
    .configure(Listenable.install(identity, (c) => {(a:EntropyStatus) =>{
      if (a.ready != c.state) c.setState(a.ready)
    }}))
    .componentDidMount(c => {
      JQueryMaterialize.tooltip(jQuery(".tooltipped"))
    })
    .build

  def entropyBar = ReactComponentB[AppBackend](getClass().getName())
    .render(P => {
      <.div( ^.cls := "row",
        <.div( ^.cls := "col l11", coloredProgressBar(P)),
        <.div( ^.cls := "col l1",
          refreshBtn(P)
        )
      )
    })
    .build

  val nextBtn = ReactComponentB[AppBackend](getClass().getName())
    .initialState(false)
    .backend(_ => new OnUnmount.Backend)
    .render((P, S, B) => {
      <.a(^.href := "#",
        ^.classSet1(
          "waves-effect waves-green btn-flat modal-action modal-close",
          "disabled" -> (S == false)
        ),
        "Next")

    })
    .configure(Listenable.install(identity, (c) => {(a:EntropyStatus) =>{
      if (a.ready != c.state) c.setState(a.ready)
    }}))
    .componentDidMount(c => {
      JQueryMaterialize.tooltip(jQuery(".tooltipped"))
    })
    .build

  def entropyModal = ReactComponentB[AppBackend](getClass().getName())
    .render(P => {
      <.div(^.cls := "modal", ^.id := "prompt_entropy",
        <.div(^.cls := "modal-content",
          <.h4("Create New Wallet"),
          <.p("Randomness is ", <.em("VERY"), " important to ensure wallet secure."),
          <.p("Now move the mouse around and/or monkey typing to fill the entropy(randomness) pool.")
        ),
        <.div(^.cls := "modal-footer",
          coloredProgressBar(P),
          nextBtn(P)
        )
      )

    })
    .build

  val seedCtrl = ReactComponentB[(String, String, AppBackend)](getClass().getName())
    .initialState(("", true))
    .backend(c => new {
      def gen() {
        val seed = (new stellar.Seed).random()
        c.setState((seed.to_json().toString, c.state._2))
      }
      def gen(s:String) {
        c.setState((s, c.state._2))
      }
      def toggle(hidden :Boolean) {
        c.setState((c.state._1, hidden))
      }
    })
    .render((P,S,B) => {
      val seed = new stellar.Seed
      val addr = Try{
        seed.parse_json(S._1);
        seed.get_key().get_address().to_json().toString;
      }
      <.div(
        <.div(^.cls := "row",
          <.div(^.cls := "input-field col s8",
            <.i(^.cls := P._2, ^.cls := "prefix"),
            <.label(P._1+" Address", ^.cls := "active"),
            addr match {
              case Success(s) => <.input(^.`type` := "text", ^.readOnly := "readonly", ^.cls := "validate", ^.value := s)
              case Failure(e) => <.input(^.`type` := "text", ^.readOnly := "readonly", ^.cls := "validate", ^.value := "-Invalid Seed-") //e.toString()
            }
          ),
          <.div(^.cls := "col s4 small",
            <.div(<.a("Generate randomly", ^.href := "#", ^.onClick --> {B.gen()})),
            <.div(<.a(^.cls := "light-blue-text text-lighten-4",(if (S._2) "edit" else "hide")+" secret key",
              ^.href := "#", ^.onClick --> {B.toggle(S._2 == false)}
            ))
          )
        ),
        <.div(^.classSet1("row", "credential" -> S._2),
          <.div(^.cls := "input-field col s12",
            <.label("Secret Key", ^.cls := "active"),
            <.input(^.value := S._1, ^.`type` := "text",
              ^.cls := "validate light-blue-text text-lighten-4", ^.onChange ==> ({x :ReactEventI =>                
                B.gen(x.target.value)
              }))
          )
        )
      )
    })
    .build

  val walletPanel = ReactComponentB[(String, AppBackend)](getClass().getName())
    .render(P => {
      <.div(
        <.h3("Setup Stellar Wallet"+P._1),
        // <.div(^.cls := "row",
        //   <.form(^.cls := "col s12",

        //     <.input(^.`type` := "text", ^.`class` := "validate")
        //   )),
        <.form(
          <.div(^.cls := "row",
            <.div(^.cls := "input-field col s8",
              <.i(^.cls := "mdi-action-account-circle prefix"),
              <.label("Wallet Name"),
              <.input(^.`type` := "text", ^.cls := "validate")
            )
          ),
          seedCtrl(("Offline", "mdi-action-account-balance", P._2)),
          <.div(^.cls := "row",
            <.div(^.cls := "input-field col s8",
              <.i(^.cls := "mdi-editor-mode-edit prefix"),
              <.label("Memo"),
              <.textarea(^.cls := "materialize-textarea")
            )
          ),
          seedCtrl(("Online", "mdi-action-wallet-travel", P._2))
        )
      )
    })
    .build

  val novaPage = ReactComponentB[Unit](getClass().getName())
    .initialState()
    .backend(_ => new AppBackend)
    .render((_, S, B) => {
      // val seed = new stellar.Seed
      // seed.parse_json(S.seed)

      <.div(
        entropyBar(B),
        <.div(^.cls := "row",
          <.div(^.cls := "container z-depth-1",
            walletPanel(("", B)),
            // <.div(
            //   <.p("Secret seed"),
            //   <.input(^.value := seed.to_json, ^.`type` := "text"),
            //   <.button("Generate", ^.onClick --> B.genRandom),
            //   <.p("Public Address"),
            //   <.p(seed.get_key().get_address().to_json().toString),
            //   <.span()
            // ),
            entropyModal(B)
          )
        )
      )
    })
    .componentDidMount(c => {
      JQueryMaterialize.openModal(jQuery("#prompt_entropy"))
      c.backend.setInterval({c.backend.doTick()}, 50 millisecond)
    })
    .configure(SetInterval.install)
    .buildU

  val authPanel = ReactComponentB[Unit](getClass().getName())
      .render((_) => {
        <.p(<.span("Password"), <.input(^.`type` := "password", ^.id := "randomKeyboard"))
      })
    .componentDidMount(c => {
      JQueryKeypad(jQuery("#randomKeyboard"))
    })
    .buildU
}

object CogSplash{
  val page = ReactComponentB[Unit](getClass().getName())
    .render((_) => {
      <.i(^.cls := "fa fa-cog fa-spin fa-3x")
    }).buildU

  def render(ele :dom.Element) = page().render(ele)
}

object Main extends js.JSApp{

  def main(): Unit = {

    import scalajs.concurrent.JSExecutionContext.Implicits.runNow

    dom.console.log(">main()");

    jQuery(() => {
      sjcl.veryrandom.reset()


      SplashLoader.loadWithRender("wallet.txt", (div) => {
        CogSplash render div
      }, 1 second)
      .onComplete{
        case Success(v) =>
          //WalletApp.page() render dom.document.getElementById("content")
          Wallet.parse(v) match {
            case Some(wallet) => {
              //EntropyWrapper()
              "normal operation"
            }
            case None => {
              "wallet malform"
            }
          }
        case Failure(e) => {
          dom.console.log("not found:"+e)
          WalletApp.novaPage() render dom.document.body
        }
      }
    })
  }
}
