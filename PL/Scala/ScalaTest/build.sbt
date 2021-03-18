// build.sbt

name := "Study-ScalaTest"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "3.2.4" % Test

Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
