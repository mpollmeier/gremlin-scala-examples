import com.tinkerpop.gremlin.scala._

object Neo4jExample extends App {
  import com.tinkerpop.gremlin.neo4j.structure.Neo4jGraph

  val graph: Neo4jGraph = Neo4jGraph.open("neo4j")
  val gs = GremlinScala(graph)

  (1 to 5) foreach { i ⇒ 
    gs.addVertex().setProperty("name", s"vertex $i")
  }

  val traversal = gs.V.value[String]("name")
  println(traversal.toList)

  graph.close()
}
