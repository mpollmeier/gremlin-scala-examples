name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"
val orientDBVersion = "2.1-rc4"

libraryDependencies ++= Seq(
  "com.orientechnologies" % "orientdb-core" % orientDBVersion,
  "com.orientechnologies" % "orientdb-graphdb" % orientDBVersion,
  "com.orientechnologies" % "orientdb-client" % orientDBVersion,
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.0.M9-incubating",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.0.0.M1",
  "org.scalatest" %% "scalatest" % "2.2.4" % Test
)
