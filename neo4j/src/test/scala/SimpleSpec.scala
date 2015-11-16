import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
import gremlin.scala._
import org.scalatest._

class SimpleSpec extends FlatSpec with Matchers {

  "Gremlin-Scala" should "create some vertices with properties" in {
    val dbPath = "target/simplespec"
    FileUtils.removeAll(dbPath)
    val graph = Neo4jGraph.open(dbPath).asScala
    val Name = Key[String]("name")

    (1 to 5) foreach { i ⇒
      graph + ("some_label", Name -> s"vertex $i")
    }

    val names = graph.V.value(Name).toList
    names.size shouldBe 5
    names foreach (_.startsWith("vertex") shouldBe true)

    graph.close
  }
}
