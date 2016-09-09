name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.1.0",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.2.1.1" exclude("com.github.jeremyh", "jBCrypt"), // travis can't find jBCrypt...
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)
resolvers += Resolver.mavenLocal

fork := true
