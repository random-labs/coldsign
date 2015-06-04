enablePlugins(ScalaJSPlugin)

name := "stellar-coldsign"

scalaVersion := "2.11.6"

persistLauncher in Compile := true

requiresDOM := true

skip in packageJSDependencies := false

libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "0.8.4"

// Extra features
libraryDependencies += "com.github.japgolly.scalajs-react" %%% "extra" % "0.8.4"

libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.2.8"

jsDependencies += "org.webjars" % "react" % "0.13.3" / "react-with-addons.js" commonJSName "React" minified "react-with-addons.min.js"

jsDependencies += ProvidedJS / "js/stellar-base.js" minified "js/stellar-base.min.js"

jsDependencies += ProvidedJS / "js/sjcl.js"
