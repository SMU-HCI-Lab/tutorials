name := "Study-ScalaTest"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.rabbitmq" % "amqp-client" % "5.8.0"
)
