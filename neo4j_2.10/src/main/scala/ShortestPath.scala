import com.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
import com.tinkerpop.gremlin.scala._
import org.neo4j.graphdb.DynamicLabel
import scala.util.Random
import com.tinkerpop.gremlin.process.T

// calculate the shortest path while travelling New Zealand
// inspired by Stefan Bleibinhaus
// http://bleibinha.us/blog/2013/10/scala-and-graph-databases-with-gremlin-scala
object Neo4jShortestPath extends App {
  FileUtils.removeAll("neo4j")
  val graph: Neo4jGraph = Neo4jGraph.open("neo4j")
  val gs = GremlinScala(graph)

  val auckland = addLocation("Auckland")
  val whangarei = addLocation("Whangarei")
  val dargaville = addLocation("Dargaville")
  val kaikohe = addLocation("Kaikohe")
  val kerikeri = addLocation("Kerikeri")
  val kaitaia = addLocation("Kaitaia")
  val capeReinga = addLocation("Cape Reinga")

  addRoad(auckland, whangarei, 158)
  addRoad(whangarei, kaikohe, 85)
  addRoad(kaikohe, kaitaia, 82)
  addRoad(kaitaia, capeReinga, 111)
  addRoad(whangarei, kerikeri, 85)
  addRoad(kerikeri, kaitaia, 88)
  addRoad(auckland, dargaville, 175)
  addRoad(dargaville, kaikohe, 77)
  addRoad(kaikohe, kerikeri, 36)

  // val routes = auckland.both.both.path//.jump()
  val routes = auckland.as("a")
    .both.jump("a", loops = 5, 
      { v => v.value[String]("name") != "Auckland" }
    )
    // .filter(_.value[String]("name") != "Cape Reinga")
    .path
  println("all routes from auckland to cape reinga:")
  val allRoutes = routes.toSet
  allRoutes foreach println
  println(s"found ${allRoutes.size} routes")

  // routes.toSet foreach println
  println("")

  // println("shortest route from auckland to cape reinga:")
  // println(routes.orderBy....head)

  graph.close

  def addLocation(name: String): ScalaVertex = {
    val v = gs.addVertex()
    v.setProperty("name", name)
    v
  }

  def addRoad(from: ScalaVertex, to: ScalaVertex, distance: Int): Unit = 
    from.addEdge(label = "road", to, Map.empty).setProperty("distance", distance)
}
