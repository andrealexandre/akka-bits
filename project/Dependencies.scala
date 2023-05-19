import sbt._

object Dependencies {

  object Versions {
    val pekko = "2.6.18"
  }


  lazy val slf4j = "org.slf4j" % "slf4j-api" % "1.7.30"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3"

  lazy val pekkoSlf4j = "org.apache.pekko" %% "pekko-slf4j" % Versions.pekko
  lazy val pekkoActor = "org.apache.pekko" %% "pekko-actor" % Versions.pekko
  lazy val pekkoActorTyped = "org.apache.pekko" %% "pekko-actor-typed" % Versions.pekko

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.8"
  lazy val pekkoTestkit = "org.apache.pekko" %% "pekko-testkit" % Versions.pekko
  lazy val pekkoActorTestkit = "org.apache.pekko" %% "pekko-actor-testkit-typed" % Versions.pekko
  lazy val testContainers = "org.testcontainers" % "testcontainers" % "1.15.3"
}
