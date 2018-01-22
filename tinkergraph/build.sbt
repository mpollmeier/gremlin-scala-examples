name := "gremlin-scala-examples-tinkergraph"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.3.1.1",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.3.1",
  "org.slf4j" % "slf4j-nop" % "1.7.25" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true
resolvers += Resolver.mavenLocal
resolvers += "Apache public" at "https://repository.apache.org/content/groups/public/"
