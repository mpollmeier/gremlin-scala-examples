import org.janusgraph.core.JanusGraphFactory
import org.apache.tinkerpop.gremlin.structure.T
import java.util.UUID

object LoadTest extends App {
  val nodeCount = 500 * 1000

  val graph = JanusGraphFactory.build
    // .set("storage.backend", "inmemory")
    .set("storage.backend", "berkeleyje").set("storage.directory", "bdb")
    .set("storage.berkeleyje.cache-percentage", 50)
    .set("storage.transactions", false)
    // .set("storage.batch-loading", true)
        .open

  println("starting")

  var lastStart = System.currentTimeMillis
  (1 to nodeCount) foreach { counter => 
    if (counter % 1000 == 0) {
      val millisSinceLastBatch = System.currentTimeMillis - lastStart
      lastStart = System.currentTimeMillis
      println("importing node " + counter + "; millis since last batch: " + millisSinceLastBatch)
    }
    if (counter % 10000 == 0) {
      println("committing tx")
      graph.tx.commit
    }
    val node = graph.addVertex(T.label, "testLabel")

    (1 to 10).foreach { i =>
      node.property(s"testProperty$i", UUID.randomUUID.toString)
    }
  }

  println("all done")

  graph.close
}

