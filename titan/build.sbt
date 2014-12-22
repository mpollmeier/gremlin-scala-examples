name := """titan-graphdb-scala-playground"""

version := "1.0"

scalaVersion := "2.11.4"

val titanV = "0.9.0-M1"
val gremlinScalaV = "3.0.0.M6a"
val specs2V = "2.4.15"

libraryDependencies ++= Seq(
	"com.thinkaurelius.titan" % "titan-core" % titanV,
	"com.thinkaurelius.titan" % "titan-cassandra" % titanV,
	"com.thinkaurelius.titan" % "titan-es" % titanV
)

libraryDependencies += "com.michaelpollmeier" %% "gremlin-scala" % gremlinScalaV

libraryDependencies += "org.specs2" %% "specs2-core" % specs2V % "test"
