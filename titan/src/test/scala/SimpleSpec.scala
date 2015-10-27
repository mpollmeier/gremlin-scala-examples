import org.apache.tinkerpop.gremlin.structure.T
import gremlin.scala._
import org.scalatest._

class SimpleSpec extends FlatSpec with Matchers with InMemoryConnect {

  "Gremlin-Scala" should "connect to Titan database, pull out Saturn's keys and shutdown cleanly" in {
    val graph = connect().asScala
    val Name = Key[String]("name")

    (1 to 5) foreach { i ⇒
      graph + ("planet", Name -> s"vertex $i")
    }
    graph + ("saturn", Name -> "saturn")

    graph.V.count.head shouldBe 6

    val traversal = graph.V.value(Name)
    traversal.toList.size shouldBe 6

    graph.V.hasLabel("saturn").count.head shouldBe 1

    val saturnQ = graph.V.hasLabel("saturn").head
    saturnQ.value2(Name) shouldBe "saturn"

    graph.close
  }
}
