import com.thinkaurelius.titan.core.{TitanVertex, Cardinality, PropertyKey, TitanGraph}
import com.tinkerpop.gremlin.process.T
import com.tinkerpop.gremlin.scala.GremlinScala
import com.tinkerpop.gremlin.structure.Vertex
import org.specs2.mutable._

class HelloSpec extends Specification with InMemoryConnect {

  "The test" should {
    "connect to Titan database, pull out Saturn's keys and shutdown cleanly" in {
      val g = connect()
      val isOpen = g.isOpen
      isOpen must_=== true

      val gs = GremlinScala(g)

      (1 to 5) foreach { i ⇒
        gs.addVertex().setProperty("name", s"vertex $i")
      }
      gs.addVertex("saturn", Map("name" -> "saturn"))

      gs.V.count().head must_=== 6

      val traversal = gs.V.value[String]("name")
      traversal.toList.size must_=== 6

      gs.V.has(T.label, "saturn").count().head must_=== 1

      val saturnQ = gs.V.has(T.label, "saturn").head

      saturnQ.property[String]("name").value must_=== "saturn"

      g.close
      g.isClosed must beTrue
    }
  }
}
