lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
    guice,
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0" % "test"
)
// libraryDependencies += "com.typesafe.play" %% "play-guice" % "2.6.5"
// libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test"