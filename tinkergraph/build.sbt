name := "gremlin-scala-examples-tinkergraph"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.7"
fork in Test := true

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.1.0-incubating.1",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.1.0-incubating" exclude("org.slf4j", "slf4j-log4j12"),
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)

resolvers += Resolver.mavenLocal
resolvers += "Apache public" at "https://repository.apache.org/content/groups/public/"
