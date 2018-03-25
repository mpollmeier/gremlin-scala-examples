name := "gremlin-scala-examples-janusgraph"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.3.1.2-SNAPSHOT",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.3.1",
  "org.janusgraph" % "janusgraph-core" % "0.2.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true

resolvers ++= Seq(
  Resolver.mavenLocal,
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public" 
)
