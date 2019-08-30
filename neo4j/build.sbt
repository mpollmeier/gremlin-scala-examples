name := "gremlin-scala-examples-neo4j"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.9"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.4.0.4",
  "org.apache.tinkerpop" % "neo4j-gremlin" % "3.4.3",
  "org.neo4j" % "neo4j-tinkerpop-api-impl" % "0.7-3.2.3",
  "org.slf4j" % "slf4j-nop" % "1.7.25" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true
resolvers += Resolver.mavenLocal
