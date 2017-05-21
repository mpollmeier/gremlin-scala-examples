![logo](https://github.com/mpollmeier/gremlin-scala/raw/master/doc/images/gremlin-scala-logo.png)
[![Build Status](https://secure.travis-ci.org/mpollmeier/gremlin-scala-examples.png?branch=master)](http://travis-ci.org/mpollmeier/gremlin-scala-examples)
 [![Join the chat at https://gitter.im/mpollmeier/gremlin-scala](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mpollmeier/gremlin-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Gremlin-Scala examples
A collection of example projects and recipies for [Gremlin-Scala](https://github.com/mpollmeier/gremlin-scala) that you can use as templates for your own tinkering.

### Tinkergraph

* [MovieLens - a large collection of traversals](tinkergraph/src/test/scala/MovieLensSpec.scala) based on a [presentation by Marko Rodriguez and Daniel Kuppitz](http://www.slideshare.net/slidarko/the-gremlin-traversal-language)

### OrientDB

* [Simple traversals](orientdb/src/test/scala/SimpleSpec.scala)

### Neo4j

* [Simple traversal](neo4j/src/test/scala/SimpleSpec.scala)
* [Indexes](neo4j/src/test/scala/IndexSpec.scala)
* [Shortest path](neo4j/src/test/scala/ShortestPathSpec.scala) - [Blog post](http://www.michaelpollmeier.com/2014/12/27/gremlin-scala-shortest-path)

### Neo4j Bolt protocol
* [Simple traversal](neo4j-bolt/src/test/scala/SimpleSpec.scala)

### DSE graph
Note: to start DSE, first run: `docker run --name dseg51 -p 9042:9042 -d luketillman/datastax-enterprise:5.1.0 -g`
To stop DSE, run `docker stop dseg51`.
To start it again, run `docker start dseg51`.
* [Simple traversal, bulk scenario](dse-graph/src/test/scala/SimpleSpec.scala)


### Janusgraph
* [Simple traversal](janusgraph/src/test/scala/SimpleSpec.scala)

## Usage
First install jdk 8 and configure it to be your `JAVA_HOME` and in your path (check with `echo $JAVA_HOME` and `java -version`). 
```
cd tinkergraph
sbt test

cd orientdb
sbt test

cd neo4j
sbt test

cd janusgraph
sbt test
```
