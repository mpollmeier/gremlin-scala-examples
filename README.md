![logo](https://github.com/mpollmeier/gremlin-scala/raw/master/doc/images/gremlin-scala-logo.png)

## Gremlin-Scala examples
A collection of example projects and recipies for Gremlin-Scala that you can use as templates. I am always looking for more examples - keep the PRs coming ;)

### Neo4j

* [Simple traversal](neo4j/src/test/scala/SimpleSpec.scala)
* [Indexes](neo4j/src/test/scala/IndexSpec.scala)
* [Shortest path](neo4j/src/test/scala/ShortestPathSpec.scala) - [Blog post](http://www.michaelpollmeier.com/2014/12/27/gremlin-scala-shortest-path/)

### Titan

* [Simple traversal](titan/src/test/scala/SimpleSpec.scala)

## Usage
First install jdk 8 and configure it to be your `JAVA_HOME` and in your path (check with `echo $JAVA_HOME` and `java -version`). 
```
#for neo4j
cd neo4j
sbt test

#for titan
cd titan
sbt test
```
