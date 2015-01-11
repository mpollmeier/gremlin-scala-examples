name := "gremlin-scala-examples-neo4j"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.4" //2.11 doesn't work because neo4j transitively depends on 2.10..
val gremlinScalaV = "3.0.0.M7"
val gremlinV = "3.0.0.M7"
val scalatestV = "2.2.1"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % gremlinScalaV exclude("org.slf4j", "slf4j-log4j12"),
  "com.tinkerpop" % "neo4j-gremlin" % gremlinV,
  "org.scalatest" %% "scalatest" % scalatestV % "test"
)
resolvers += Resolver.mavenLocal
