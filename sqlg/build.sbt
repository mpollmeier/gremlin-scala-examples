name := "gremlin-scala-examples-sqlg"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.4.8",
  "org.umlg" % "sqlg-hsqldb" % "1.3.3",
  "org.umlg" % "sqlg-postgres" % "1.3.3",
  "org.slf4j" % "slf4j-nop" % "1.7.25" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

fork in Test := true
resolvers += Resolver.mavenLocal
