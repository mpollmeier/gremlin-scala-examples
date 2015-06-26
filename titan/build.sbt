name := "gremlin-scala-examples-titan"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"
val gremlinScalaV = "3.0.0-SNAPSHOT"
val titanV = "0.9.0-M2"
val scalatestV = "2.2.1"

libraryDependencies ++= Seq(
	"com.thinkaurelius.titan" % "titan-core" % titanV,
	"com.thinkaurelius.titan" % "titan-cassandra" % titanV,
	"com.thinkaurelius.titan" % "titan-es" % titanV,
  "com.michaelpollmeier" %% "gremlin-scala" % gremlinScalaV,
  "org.scalatest" %% "scalatest" % scalatestV % "test"
)
resolvers += Resolver.mavenLocal
