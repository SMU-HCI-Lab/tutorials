lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

val scalikejdbcVersion = "3.4.0"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  cacheApi,
//  caffeine,
  "org.mockito" % "mockito-core" % "3.2.4" % Test,
  "com.rabbitmq" % "amqp-client" % "5.8.0",
  "org.im4java" % "im4java" % "1.4.0",
  // "org.scala-lang.modules" %% "scala-pickling" % "0.10.1",
//  evolutions,
//  filters,
//  "com.h2database" % "h2" % "1.4.192",
  "org.scalikejdbc"        %% "scalikejdbc"                  % scalikejdbcVersion,
  "org.scalikejdbc"        %% "scalikejdbc-config"           % scalikejdbcVersion,
  "org.scalikejdbc"        %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0" % "test"
)
