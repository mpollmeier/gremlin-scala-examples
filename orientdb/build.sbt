name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.0-incubating",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.0.0.M1a",
  "com.orientechnologies" % "orientdb-graphdb" % "2.1-rc5",
  "org.scalatest" %% "scalatest" % "2.2.4" % Test
)
resolvers += Resolver.mavenLocal
