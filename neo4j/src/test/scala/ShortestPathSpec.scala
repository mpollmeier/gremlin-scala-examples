import gremlin.scala._
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
import org.apache.tinkerpop.gremlin.process.traversal.Path
import scala.util.Random
import collection.JavaConversions._
import org.scalatest._
import org.apache.tinkerpop.gremlin.process.traversal.P

// calculate the shortest path while travelling New Zealand
// http://www.michaelpollmeier.com/2014/12/27/gremlin-scala-shortest-path/
class ShortestPathSpec extends WordSpec with Matchers {
  val Distance = Key[Int]("distance")
  val Location = "location"
  val Road = "road"
  val Name = Key[String]("name")

  "finds the shortest path between two vertices" in {
    val dbPath = "target/shortestpath"
    FileUtils.removeAll(dbPath)
    val graph = Neo4jGraph.open(dbPath).asScala
    println("opened empty graph, setting it up now")

    def addLocation(name: String): Vertex =
      graph + (Location, Name -> name)

    def addRoad(from: Vertex, to: Vertex, distance: Int): Unit = {
      // two way road ;)
      from --- ("road", Distance -> distance) --> to
      from <-- ("road", Distance -> distance) --- to
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
    val startTime = System.currentTimeMillis

    val paths = auckland.asScala
      .repeat(_.outE.inV)
      .untilWithTraverser { t: Traverser[Vertex] ⇒
        val city = t.get.value[String]("name")
        t.loops > 5 || city == "Cape Reinga" || city == "Auckland"
      }
      .emit()
      .filter(_.value[String]("name") == "Cape Reinga")
      .path
      .dedup()
      .toList
    // val paths = auckland.repeat(_.outE.inV.simplePath()).until(is(y)).path().limit(1)
    // auckland.repeat(_.outE.inV.simplePath).untilWithTraverser(_.get.value[String]("name") == "Cape Reinga").limit(1).path().toList

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
    println("time elapsed: " + (System.currentTimeMillis - startTime) + "ms")

    println("done - closing graph")
    graph.close
  }
}
