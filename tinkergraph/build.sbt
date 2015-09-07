name := "gremlin-scala-examples-tinkergraph"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.7"

val gremlinVersion = "3.0.0-incubating"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % gremlinVersion,
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % gremlinVersion,// exclude("org.slf4j", "slf4j-log4j12"),
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)
resolvers += Resolver.mavenLocal
