name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.4.0",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.2.3.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % Test
)
resolvers += Resolver.mavenLocal

fork := true
