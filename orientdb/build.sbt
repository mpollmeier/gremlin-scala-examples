name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.0.M9-incubating",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.0.0.M1",
  // "com.orientechnologies" % "orientdb-core" % "2.1-rc4",
  "org.scalatest" %% "scalatest" % "2.2.4" % Test
)
