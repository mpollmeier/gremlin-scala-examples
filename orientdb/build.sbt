name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.2.0",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.2.2.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)
resolvers += Resolver.mavenLocal

fork := true
