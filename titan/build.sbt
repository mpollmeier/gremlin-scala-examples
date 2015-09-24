name := "gremlin-scala-examples-titan"
organization := "com.michaelpollmeier"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"
val titanV = "0.9.0-M2"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.1-incubating",
	"com.thinkaurelius.titan" % "titan-core" % titanV,
	"com.thinkaurelius.titan" % "titan-cassandra" % titanV,
	"com.thinkaurelius.titan" % "titan-es" % titanV,
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)

//titan is only compatible with hppc 0.6.x - see https://github.com/thinkaurelius/titan/pull/1101
dependencyOverrides += "com.carrotsearch" % "hppc" % "0.6.1"

resolvers += Resolver.mavenLocal
