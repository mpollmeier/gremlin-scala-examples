import org.apache.tinkerpop.gremlin.structure.T
import gremlin.scala._
import org.scalatest._

class SimpleSpec extends FlatSpec with Matchers with InMemoryConnect {

  "Gremlin-Scala" should "connect to Titan database, pull out Saturn's keys and shutdown cleanly" in {
      val g = connect()
      val gs = g.asScala

      (1 to 5) foreach { i ⇒
        gs.addVertex().setProperty("name", s"vertex $i")
      }
      gs.addVertex("saturn", Map("name" -> "saturn"))

      gs.V.count().head shouldBe 6

      val traversal = gs.V.value[String]("name")
      traversal.toList.size shouldBe 6

      gs.V.hasLabel("saturn").count().head shouldBe 1

      val saturnQ = gs.V.hasLabel("saturn").head
      saturnQ.value[String]("name") shouldBe "saturn"

      g.close
  }
}
