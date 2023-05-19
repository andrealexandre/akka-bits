import sbt._

object Dependencies {
  lazy val slf4j = "org.slf4j" % "slf4j-api" % "1.7.30"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3"

  lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % "2.6.18"
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.6.18"
  lazy val akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % "2.6.18"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.8"
  lazy val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % "2.6.18"
  lazy val akkaActorTestkit = "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.18"
  lazy val testContainers = "org.testcontainers" % "testcontainers" % "1.15.3"
}
