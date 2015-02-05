name := "gremlin-scala-examples-titan"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.5"
val gremlinScalaV = "3.0.0.M6c" //can only upgrade to M7 if also upgrading titan - see https://github.com/thinkaurelius/titan/tree/titan09
val titanV = "0.9.0-M1"
val scalatestV = "2.2.1"

libraryDependencies ++= Seq(
	"com.thinkaurelius.titan" % "titan-core" % titanV,
	"com.thinkaurelius.titan" % "titan-cassandra" % titanV,
	"com.thinkaurelius.titan" % "titan-es" % titanV,
  "com.michaelpollmeier" %% "gremlin-scala" % gremlinScalaV,
  "org.scalatest" %% "scalatest" % scalatestV % "test"
)

net.virtualvoid.sbt.graph.Plugin.graphSettings
