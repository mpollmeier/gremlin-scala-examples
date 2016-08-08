name := "gremlin-scala-examples-neo4j"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.1.0",
  "org.apache.tinkerpop" % "neo4j-gremlin" % "3.2.1",
  "org.neo4j" % "neo4j-tinkerpop-api-impl" % "0.4-3.0.3",
  "de.svenkubiak" % "jBCrypt" % "0.4", // neo4j depends on it, for some reason has a problem resolving if not explicitly stated
  "org.slf4j" % "slf4j-simple" % "1.7.12",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)
resolvers += Resolver.mavenLocal
