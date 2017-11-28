name := "gremlin-scala-examples-dse-graph"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.4"

// Note: This is needed to work around an issue with the DataStax driver and the Scala Compiler.
// See: https://datastax-oss.atlassian.net/browse/JAVA-1252 for more details.
scalacOptions += "-Ybreak-cycles"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.3.0.5",
  "com.datastax.dse" % "dse-java-driver-graph" % "1.4.2",
  "ch.qos.logback" % "logback-classic" % "1.1.9" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true
resolvers += Resolver.mavenLocal
