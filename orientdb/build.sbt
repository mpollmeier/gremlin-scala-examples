name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.4.15",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.2.3.0",
  "org.slf4j" % "slf4j-nop" % "1.7.25" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true
resolvers += Resolver.mavenLocal
