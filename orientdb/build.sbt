name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.3.1",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.2.3.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)
resolvers += Resolver.mavenLocal

fork := true
