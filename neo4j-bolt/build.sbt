name := "gremlin-scala-examples-neo4j-bolt"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.3.0.5",
  "com.steelbridgelabs.oss" % "neo4j-gremlin-bolt" % "0.2.27",
  "org.slf4j" % "slf4j-nop" % "1.7.25" % Test,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

fork in Test := true
resolvers += Resolver.mavenLocal
