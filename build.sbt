import Dependencies._

name             := "akka-bits"
scalaVersion     := "2.13.8"
scalacOptions    ++= Seq(
  "-Xfatal-warnings"
)
version          := "0.1.0-SNAPSHOT"
organization     := "org.aalexandre"
organizationName := "aalexandre"
Test / parallelExecution := true

libraryDependencies ++= Seq(
  slf4j,
  logback,
  scalaLogging,

  akkaSlf4j,
  akkaActor,
  akkaActorTyped,

  scalaTest % Test,
  akkaTestkit % Test,
  akkaActorTestkit % Test,
  testContainers % Test,
)