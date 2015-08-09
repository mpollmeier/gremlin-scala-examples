import gremlin.scala._
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
import org.apache.tinkerpop.gremlin.process.traversal.Path
import scala.util.Random
import collection.JavaConversions._
import org.scalatest._

// calculate the shortest path while travelling New Zealand
// inspired by http://bleibinha.us/blog/2013/10/scala-and-graph-databases-with-gremlin-scala
class ShortestPathSpec extends FlatSpec with Matchers {

  "Gremlin-Scala" should "find the shortest path between two vertices" in {
    val dbPath = "target/shortestpath"
    FileUtils.removeAll(dbPath)
    val graph: Neo4jGraph = Neo4jGraph.open(dbPath)
    val gs = GremlinScala(graph)
    val sg = ScalaGraph(graph)

    def addLocation(name: String): ScalaVertex =
      sg.addVertex().setProperty("name", name)

    def addRoad(from: ScalaVertex, to: ScalaVertex, distance: Int): Unit = {
      // two way road ;)
      from.addEdge(label = "road", to, Map("distance" -> distance))
      to.addEdge(label = "road", from, Map("distance" -> distance))
    }

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

    println(s"finding shortest routes from auckland ($auckland) to cape reinga ($capeReinga)")

    val paths = auckland
      .repeat(_.outE.inV)
      // .times(6)
      .untilWithTraverser { t: Traverser[Vertex] ⇒
        val city = t.get.value[String]("name")
        t.loops > 5 || city == "Cape Reinga" || city == "Auckland"
      }
      .emit()
      .filter(_.value[String]("name") == "Cape Reinga")
      .path
      .dedup()
      .toList

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

    println(s"Paths from Auckland to Cape Reinga:")
    descriptionAndDistances foreach println
    descriptionAndDistances.size shouldBe 23

    val shortestPath = descriptionAndDistances.sortBy(_.distance).head
    shortestPath.distance shouldBe 436
    shortestPath.description shouldBe "Auckland -> Whangarei -> Kaikohe -> Kaitaia -> Cape Reinga"

    graph.close
  }
}
