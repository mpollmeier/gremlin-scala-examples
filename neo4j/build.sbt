name := "gremlin-scala-examples-neo4j"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.1.0-incubating",
  "org.apache.tinkerpop" % "neo4j-gremlin" % "3.1.0-incubating",
  "org.neo4j" % "neo4j-tinkerpop-api-impl" % "0.3-2.3.0",
  "org.slf4j" % "slf4j-simple" % "1.7.12",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)
resolvers += Resolver.mavenLocal
