import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.io.IoCore.gryo
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.scalatest.{ Matchers, WordSpec }
import scala.collection.JavaConversions._

class MovieLensSpec extends WordSpec with Matchers {

  val g: ScalaGraph[TinkerGraph] = {
    val graph = TinkerGraph.open
    graph.io(gryo()).readGraph("src/test/resources/movie-lens.kryo")
    graph.asScala
  }

  "loads the graph" in {
    g.V.count.head shouldBe 9625
    g.E.count.head shouldBe 969719
  }

  "for each vertex, emit its label, then group and count each distinct label" in {
    val groupCount = g.V.label().groupCount.head
    groupCount.get("occupation") shouldBe 21
    groupCount.get("movie") shouldBe 3546
    groupCount.get("person") shouldBe 6040
    groupCount.get("genre") shouldBe 18
  }

  "for each rated-edge, emit its stars property value and compute the average value" in {
    val meanStars = g.E.hasLabel("rated").values("stars").mean.head
    "%.2f".format(meanStars) shouldBe "3.57"
  }

  "maximum number of movies a single user rated" ignore {
    val max = g.V.hasLabel("user").map[GremlinScala[java.lang.Long, shapeless.HNil]](_.outE("rated").count).max.head
    // TODO: fix traversal - doesn't seem to work
    // val max = g.V.hasLabel("user").map[java.lang.Long](_.outE("rated").count).max.head
    // TODO: fix type of given traversal
    // TODO: infer type Long from given traversal
    // TODO: infer type of max
    // max shouldBe 2314
  }
}
