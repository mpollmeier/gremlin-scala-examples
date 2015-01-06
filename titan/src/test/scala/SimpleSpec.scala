import com.tinkerpop.gremlin.process.T
import com.tinkerpop.gremlin.scala.GremlinScala
import org.scalatest._

//many thanks to Jacek Laskowski (https://twitter.com/jaceklaskowski) for this contribution
class SimpleSpec extends FlatSpec with Matchers with InMemoryConnect {

  "Gremlin-Scala" should "connect to Titan database, pull out Saturn's keys and shutdown cleanly" in {
      val g = connect()
      val gs = GremlinScala(g)

      (1 to 5) foreach { i ⇒
        gs.addVertex().setProperty("name", s"vertex $i")
      }
      gs.addVertex("saturn", Map("name" -> "saturn"))

      gs.V.count().head shouldBe 6

      val traversal = gs.V.value[String]("name")
      traversal.toList.size shouldBe 6

      gs.V.has(T.label, "saturn").count().head shouldBe 1

      val saturnQ = gs.V.has(T.label, "saturn").head
      saturnQ.property[String]("name").value shouldBe "saturn"

      g.close
  }
}
