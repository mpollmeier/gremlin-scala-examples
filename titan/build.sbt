name := "gremlin-scala-examples-titan"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"
val titanV = "1.0.0"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.2-incubating.1",
	"com.thinkaurelius.titan" % "titan-core" % titanV,
	// "com.thinkaurelius.titan" % "titan-cassandra" % titanV,
	// "com.thinkaurelius.titan" % "titan-es" % titanV,
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)

resolvers += Resolver.mavenLocal
