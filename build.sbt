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

resolvers += "Apache OSS Snapshots" at "https://repository.apache.org/content/groups/snapshots"


libraryDependencies ++= Seq(
  slf4j,
  logback,
  scalaLogging,

  pekkoSlf4j,
  pekkoActor,
  pekkoActorTyped,

  scalaTest % Test,
  pekkoTestkit % Test,
  pekkoActorTestkit % Test,
  testContainers % Test,
)