enablePlugins(ScalaJSPlugin)

name := "stellar-coldsign"

scalaVersion := "2.11.5"

persistLauncher in Compile := true

//jsDependencies += "org.webjars" % "react" % "0.12.2" / "react-with-addons.js" commonJSName "React"

requiresDOM := true

//!not work! ScalaJSKeys.jsDependencies += "org.webjars" % "materializecss" % "0.95.0" / "js/materialize.js"

skip in packageJSDependencies := false

libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "0.8.1"

// Extra features
libraryDependencies += "com.github.japgolly.scalajs-react" %%% "extra" % "0.8.1"

