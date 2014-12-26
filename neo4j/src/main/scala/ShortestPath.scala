import scala.util.Random

import collection.JavaConversions._
import com.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
import com.tinkerpop.gremlin.process.Path
import com.tinkerpop.gremlin.process.T
import com.tinkerpop.gremlin.process.Traverser
import com.tinkerpop.gremlin.scala._
import org.neo4j.graphdb.DynamicLabel

// calculate the shortest path while travelling New Zealand
// inspired by Stefan Bleibinhaus
// http://bleibinha.us/blog/2013/10/scala-and-graph-databases-with-gremlin-scala
object Neo4jShortestPath extends App {
  FileUtils.removeAll("neo4j")
  val graph = Neo4jGraph.open("neo4j")
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

  val paths = auckland.as("a").outE.inV.jump(
    to = "a",
    jumpPredicate = { t: Traverser[Vertex] ⇒
      t.loops < 6 &&
        t.get.value[String]("name") != "Cape Reinga" &&
        t.get.value[String]("name") != "Auckland"
    }
  ).filter(_.value[String]("name") == "Cape Reinga").path.toList

  case class DescriptionAndDistance(description: String, distance: Int)

  val descriptionAndDistances: List[DescriptionAndDistance] = paths map { p: Path ⇒
    val pathDescription = p.objects collect {
      case v: Vertex ⇒ v.value[String]("name")
    } mkString(" -> ")

    val pathTotalKm = p.objects collect {
      case e: Edge => e.value[Int]("distance")
    } sum
    
    DescriptionAndDistance(pathDescription, pathTotalKm)
  } 

  println(s"found ${paths.size} paths from Auckland to Cape Reinga:")
  descriptionAndDistances foreach println

  val shortestPath = descriptionAndDistances.sortBy(_.distance).head
  println(s"\nshortest path: $shortestPath")

  graph.close

  def addLocation(name: String): ScalaVertex =
    gs.addVertex().setProperty("name", name)

  def addRoad(from: ScalaVertex, to: ScalaVertex, distance: Int): Unit = {
    // two way road ;)
    from.addEdge(label = "road", to, Map.empty).setProperty("distance", distance)
    to.addEdge(label = "road", from, Map.empty).setProperty("distance", distance)
  }
}
