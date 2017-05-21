name := "gremlin-scala-examples-janusgraph"
organization := "com.michaelpollmeier"
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.4.13",
  "org.janusgraph" % "janusgraph-core" % "0.1.1",
  "ch.qos.logback" % "logback-classic" % "1.1.9" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

fork in Test := true

resolvers ++= Seq(
  Resolver.mavenLocal,
  // Note:  This is needed to resolve jBcrypt which TinkerPop depends on.  This will no longer
  // be needed when 3.2.5 comes out as it adjusts it's jBCrypt dependency to something in maven
  // central.
  "jitpack" at "https://jitpack.io",
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public" 
)
