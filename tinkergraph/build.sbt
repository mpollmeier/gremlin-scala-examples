name := "gremlin-scala-examples-tinkergraph"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.8"
fork in Test := true

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.4.0",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.2.3",
  "org.scalatest" %% "scalatest" % "3.0.0" % Test
)

resolvers += Resolver.mavenLocal
resolvers += "Apache public" at "https://repository.apache.org/content/groups/public/"
