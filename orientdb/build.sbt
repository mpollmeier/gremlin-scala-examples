name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.1-incubating3",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.0.0.M3",
  //"com.orientechnologies" % "orientdb-graphdb" % "2.1.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)
resolvers += Resolver.mavenLocal
