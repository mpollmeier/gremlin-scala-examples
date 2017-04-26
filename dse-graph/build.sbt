name := "gremlin-scala-examples-dse-graph"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.1"

// Note: This is needed to work around an issue with the DataStax driver and the Scala Compiler.
// See: https://datastax-oss.atlassian.net/browse/JAVA-1252 for more details.
scalacOptions += "-Ybreak-cycles"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.4.11-SNAPSHOT",
  "com.datastax.dse" % "dse-java-driver-graph" % "1.2.3",
  "ch.qos.logback" % "logback-classic" % "1.1.9" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

fork in Test := true

resolvers += Resolver.mavenLocal
// Note:  This is needed to resolve jBcrypt which TinkerPop depends on.  This will no longer
// be needed when 3.2.5 comes out as it adjusts it's jBCrypt dependency to something in maven
// central.
resolvers += "jitpack" at "https://jitpack.io"
