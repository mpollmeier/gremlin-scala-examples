name := "gremlin-scala-examples-server"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.3.1.2",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.3.1",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.3.1", // for serialisation of elements
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true

resolvers ++= Seq(
  Resolver.mavenLocal,
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public" 
)
