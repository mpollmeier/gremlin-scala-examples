import gremlin.scala._
import org.scalatest.{Matchers, WordSpec}
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.core.JanusGraph
import org.apache.commons.configuration.BaseConfiguration

class SimpleSpec extends WordSpec with Matchers {

  "connect to janusgraph, pull out Saturn's keys and shutdown cleanly" in {
    val conf = new BaseConfiguration()
    conf.setProperty("storage.backend","inmemory")
    val graph = JanusGraphFactory.open(conf)
    val scalaGraph = graph.asScala

    val Name = Key[String]("name")
    val Planet = "planet"
    val Saturn = "saturn"

    (1 to 4) foreach { i ⇒
      graph + (Planet, Name -> s"vertex $i")
    }
    val saturnV = graph + (Saturn, Name -> Saturn)
    val sunV = graph + ("sun", Name -> "sun")
    saturnV --- "orbits" --> sunV

    graph.V.count.head shouldBe 6
    graph.E.count.head shouldBe 1

    val traversal = graph.V.value(Name)
    traversal.toList.size shouldBe 6

    graph.V.hasLabel(Saturn).count.head shouldBe 1

    val saturnQ = graph.V.hasLabel(Saturn).head
    saturnQ.value2(Name) shouldBe Saturn

    graph.close
  }
}
