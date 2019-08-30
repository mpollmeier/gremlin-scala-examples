name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.9"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.4.0.4",
  "com.orientechnologies" % "orientdb-gremlin" % "3.1.0-M3",
  "org.slf4j" % "slf4j-nop" % "1.7.25" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true
resolvers += Resolver.mavenLocal
