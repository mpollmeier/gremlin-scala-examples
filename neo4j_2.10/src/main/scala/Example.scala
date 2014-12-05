import com.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
import com.tinkerpop.gremlin.scala._
import org.neo4j.graphdb.DynamicLabel
import scala.util.Random
import com.tinkerpop.gremlin.process.T

object Neo4jSimpleExample extends App {
  FileUtils.removeAll("neo4j")
  val graph: Neo4jGraph = Neo4jGraph.open("neo4j")
  val gs = GremlinScala(graph)

  (1 to 5) foreach { i ⇒
    gs.addVertex().setProperty("name", s"vertex $i")
  }

  val traversal = gs.V.value[String]("name")
  println(traversal.toList)

  graph.close
}

// inspired by https://github.com/tinkerpop/tinkerpop3/issues/359#event-197211058
object Neo4jIndexExample extends App {
  FileUtils.removeAll("neo4j")
  val graph: Neo4jGraph = Neo4jGraph.open("neo4j")
  val gs = GremlinScala(graph)

  graph.tx.open
  val l = DynamicLabel.label("Person")
  val s = graph.getBaseGraph.schema
  s.indexFor(l).on("name").create
  graph.tx.close

  val vertexCount = 10000
  (1 to vertexCount) foreach { i ⇒
    val name = i.toString
    val nonIndexedString = i.toString
    val vertex = gs.addVertex(label = "Person")
    vertex.setProperty("name", name)
    vertex.setProperty("nonIndexedString", nonIndexedString)
  }

  println(s"time for lookups of non-indexed vertices: ${timeLookups("nonIndexedString")}ms")
  println(s"time for lookups of indexed vertices: ${timeLookups("name")}ms")

  def timeLookups(propertyName: String): Long = {
    val t0 = System.currentTimeMillis
    (1 to 100).foreach { _ ⇒
      val i = Random.nextInt(vertexCount)
      val v = gs.V.has(T.label, "Person").has(propertyName, i.toString).headOption
      assert(v.isDefined)
    }
    val t1 = System.currentTimeMillis
    t1 - t0
  }

  graph.close
}

object FileUtils {
  import java.io.File

  def removeAll(path: String) =
    listRecursively(new File(path)) foreach { f ⇒
      if (!f.delete) throw new RuntimeException("Failed to delete " + f.getAbsolutePath)
    }

  def listRecursively(f: File): Seq[File] =
    f.listFiles.filter(_.isDirectory).flatMap(listRecursively) ++ f.listFiles
}
