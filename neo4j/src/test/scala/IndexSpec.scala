// import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
// import gremlin.scala._
// import org.neo4j.graphdb.DynamicLabel
// import scala.util.Random
// import org.apache.tinkerpop.gremlin.process.T
// import org.scalatest._

// // inspired by https://github.com/tinkerpop/tinkerpop3/issues/359#event-197211058
// class IndexSpec extends FlatSpec with Matchers {
//   "Gremlin-Scala" should "create some vertices with properties" in {
//     val dbPath = "target/indexspec"
//     FileUtils.removeAll(dbPath)
//     val graph: Neo4jGraph = Neo4jGraph.open(dbPath)
//     val gs = GremlinScala(graph)

//     graph.tx.open
//     val l = DynamicLabel.label("Person")
//     val s = graph.getBaseGraph.schema
//     s.indexFor(l).on("name").create
//     graph.tx.close

//     val vertexCount = 10000
//     (1 to vertexCount) foreach { i ⇒
//       val name = i.toString
//       val nonIndexedString = i.toString
//       val vertex = gs.addVertex(label = "Person")
//       vertex.setProperty("name", name)
//       vertex.setProperty("nonIndexedString", nonIndexedString)
//     }

//     def timeLookups(propertyName: String): Long = {
//       val t0 = System.currentTimeMillis
//       (1 to 100).foreach { _ ⇒
//         val i = Random.nextInt(vertexCount)
//         val v = gs.V.has(T.label, "Person").has(propertyName, i.toString).headOption
//         assert(v.isDefined)
//       }
//       val t1 = System.currentTimeMillis
//       t1 - t0
//     }

//     val timeNonIndexed = timeLookups("nonIndexedString")
//     val timeIndexed = timeLookups("name")
//     println(s"time for lookups of non-indexed vertices: ${timeNonIndexed}ms")
//     println(s"time for lookups of indexed vertices: ${timeIndexed}ms")
//     timeIndexed should be < timeNonIndexed

//     graph.close
//   }
// }
