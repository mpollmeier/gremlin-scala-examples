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
  val Name = Key[String]("name")
  val Distance = Key[Int]("distance")
  val Location = "location"
  val Road = "road"

  "finds the shortest path between two vertices" in {
    val dbPath = "target/shortestpath"
    FileUtils.removeAll(dbPath)
    println("opening new empty graph - this takes a moment with neo4j")
    val graph = Neo4jGraph.open(dbPath).asScala
    println("opened empty graph, setting it up now")

    // format: OFF
    val auckland   = graph + (Location, Name → "Auckland")
    val whangarei  = graph + (Location, Name → "Whangarei")
    val dargaville = graph + (Location, Name → "Dargaville")
    val kaikohe    = graph + (Location, Name → "Kaikohe")
    val kerikeri   = graph + (Location, Name → "Kerikeri")
    val kaitaia    = graph + (Location, Name → "Kaitaia")
    val capeReinga = graph + (Location, Name → "Cape Reinga")

    auckland   <-- (Road, Distance → 158) --> whangarei
    whangarei  <-- (Road, Distance →  85) --> kaikohe
    kaikohe    <-- (Road, Distance →  82) --> kaitaia
    kaitaia    <-- (Road, Distance → 111) --> capeReinga
    whangarei  <-- (Road, Distance →  85) --> kerikeri
    kerikeri   <-- (Road, Distance →  88) --> kaitaia
    auckland   <-- (Road, Distance → 175) --> dargaville
    dargaville <-- (Road, Distance →  77) --> kaikohe
    kaikohe    <-- (Road, Distance →  36) --> kerikeri
    // format: ON

    println(s"finding shortest routes from auckland ($auckland) to cape reinga ($capeReinga)")
    val startTime = System.currentTimeMillis

    val paths = auckland.asScala
      .repeat(_.outE.inV.simplePath)
      .until(_.is(capeReinga.vertex))
      .path
      .toList

    println("time elapsed: " + (System.currentTimeMillis - startTime) + "ms")

    case class DescriptionAndDistance(description: String, distance: Int)
    val descriptionAndDistances: List[DescriptionAndDistance] = paths map { p: Path ⇒
      val pathDescription = p.objects collect {
        case v: Vertex ⇒ v.value[String]("name")
      } mkString (" -> ")

      val pathTotalKm = p.objects collect {
        case e: Edge ⇒ e.value[Int]("distance")
      } sum

      DescriptionAndDistance(pathDescription, pathTotalKm)
    }

    println(s"Paths from Auckland to Cape Reinga:")
    descriptionAndDistances foreach println

    val shortestPath = descriptionAndDistances.sortBy(_.distance).head
    shortestPath.distance shouldBe 436
    shortestPath.description shouldBe "Auckland -> Whangarei -> Kaikohe -> Kaitaia -> Cape Reinga"

    println("done - closing graph")
    graph.close
  }
}

// old version, more explicit on what's happening:
// val paths = auckland.asScala
//   .repeat(_.outE.inV)
//   .untilWithTraverser { t: Traverser[Vertex] ⇒
//     val city = t.get.value[String]("name")
//     t.loops > 5 || city == "Cape Reinga" || city == "Auckland"
//   }
//   .emit()
//   .filter(_.value[String]("name") == "Cape Reinga")
//   .path
//   .dedup()
//   .toList
