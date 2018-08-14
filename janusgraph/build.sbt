name := "gremlin-scala-examples-janusgraph"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.3.3.3",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.3.3",
  "org.janusgraph" % "janusgraph-berkeleyje" % "0.3.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true

resolvers ++= Seq(
  Resolver.mavenLocal,
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)
