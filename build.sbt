scalaJSSettings

name := "stellar-coldsign"

scalaVersion := "2.11.4"

ScalaJSKeys.persistLauncher in Compile := true

//ScalaJSKeys.jsDependencies += scala.scalajs.sbtplugin.RuntimeDOM
// ScalaJSKeys.jsDependencies += "org.webjars" % "react" % "0.12.2" / "react-with-addons.js" commonJSName "React"
skip in ScalaJSKeys.packageJSDependencies := false

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "0.7.1"

// Extra features
libraryDependencies += "com.github.japgolly.scalajs-react" %%% "extra" % "0.7.1"

