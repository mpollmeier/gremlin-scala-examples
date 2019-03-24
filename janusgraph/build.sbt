name := "gremlin-scala-examples-janusgraph"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.4.0.4",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.3.3",
  "org.janusgraph" % "janusgraph-core" % "0.3.1",
  // "org.janusgraph" % "janusgraph-cql" % "0.3.1",
  // "org.janusgraph" % "janusgraph-berkeleyje" % "0.3.1",
  // "org.janusgraph" % "janusgraph-cassandra" % "0.3.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true

enablePlugins(JavaAppPackaging)

resolvers ++= Seq(
  Resolver.mavenLocal,
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Oracle Releases" at "http://download.oracle.com/maven")
