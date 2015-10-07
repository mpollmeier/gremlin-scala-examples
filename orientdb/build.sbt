name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.7"

lazy val root = project.in(file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.1-incubating",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.0.0.M3",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)
resolvers += Resolver.mavenLocal
