name := "gremlin-scala-examples-dse-graph"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.2"

// Note: This is needed to work around an issue with the DataStax driver and the Scala Compiler.
// See: https://datastax-oss.atlassian.net/browse/JAVA-1252 for more details.
scalacOptions += "-Ybreak-cycles"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.5.0",
  "com.datastax.dse" % "dse-java-driver-graph" % "1.3.0",
  "ch.qos.logback" % "logback-classic" % "1.1.9" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

fork in Test := true
resolvers += Resolver.mavenLocal
