name := "gremlin-scala-examples-elasticsearch"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.2-incubating.1",
  "unipop" % "unipop-elastic" % "0.1",
  "org.slf4j" % "slf4j-simple" % "1.7.12",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)
resolvers += Resolver.mavenLocal
