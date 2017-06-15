import gremlin.scala._
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
import org.apache.tinkerpop.gremlin.process.traversal.Path
import org.neo4j.graphdb.DynamicLabel
import org.neo4j.tinkerpop.api.impl.Neo4jGraphAPIImpl
import scala.util.Random
import collection.JavaConversions._
import org.scalatest._

// http://tinkerpop.apache.org/docs/current/reference/#neo4j-gremlin
class IndexSpec extends FlatSpec with Matchers {
  "Gremlin-Scala" should "create some vertices with indexed properties" in {
    val dbPath = "target/indexspec"
    FileUtils.removeAll(dbPath)
    val graph = Neo4jGraph.open(dbPath)
    val scalaGraph = graph.asScala
    val db = graph.getBaseGraph.asInstanceOf[Neo4jGraphAPIImpl].getGraphDatabase

    graph.tx.open
    val label = DynamicLabel.label("Person")
    db.schema.indexFor(label).on("name").create
    graph.tx.commit
    graph.tx.close

    val vertexCount = 10000
    graph.tx.open
    (1 to vertexCount) foreach { i ⇒
      scalaGraph.addVertex(label = "Person", Map("name" -> i.toString, "nonIndexedString" -> i.toString))
    }
    graph.tx.commit
    graph.tx.close

    def timeLookups(propertyName: String): Long = {
      val t0 = System.currentTimeMillis
      (1 to 100).foreach { _ ⇒
        val i = Random.nextInt(vertexCount)
        val v = scalaGraph.V.hasLabel("Person").has(Key[String](propertyName), i.toString).headOption
        assert(v.isDefined)
      }
      val t1 = System.currentTimeMillis
      t1 - t0
    }

    val timeNonIndexed = timeLookups("nonIndexedString")
    val timeIndexed = timeLookups("name")
    println(s"time for lookups of non-indexed vertices: ${timeNonIndexed}ms")
    println(s"time for lookups of indexed vertices: ${timeIndexed}ms")
    timeIndexed should be < timeNonIndexed

    graph.close
  }
}
