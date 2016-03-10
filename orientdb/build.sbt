name := "gremlin-scala-examples-orientdb"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.8"

// for some weired classloader issue we have to load this within a play app
// otherwise we get a java.lang.NoClassDefFoundError: Could not initialize class com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal
lazy val root = project.in(file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.1.1-incubating.0",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.1.1-incubating.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)
resolvers += Resolver.mavenLocal
