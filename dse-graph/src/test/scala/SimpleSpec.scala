import com.datastax.driver.dse.DseCluster
import com.datastax.driver.dse.graph.{GraphOptions, SimpleGraphStatement}
import com.datastax.dse.graph.internal.DseRemoteConnection
import com.google.common.collect.ImmutableMap
import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph
import org.scalatest.{Matchers, WordSpec}
import org.slf4j.LoggerFactory
import shapeless.HNil


// to start dse, first run:
// docker run --name dseg51 -p 9042:9042 -d luketillman/datastax-enterprise:5.1.0 -g
// TODO: Automate creation/teardown of container
class SimpleSpec extends WordSpec with Matchers {
  lazy val logger = LoggerFactory.getLogger(SimpleSpec.getClass)

  import SimpleSpec._

  "Gremlin-Scala with DSE Graph" can {
    "create two vertices and connect them" in fixture { g =>
      logger.info("Creating vertices for marko and vadas")
      // create two vertices
      val marko = g.addV(Person).property(Name, "marko").property(Age, 29).head()
      val vadas = g.addV(Person).property(Name, "vadas").property(Age, 27).head()

      logger.info("Connecting vertices via 'knows' edge")
      val a = StepLabel[Vertex]()
      val b = StepLabel[Vertex]()
      // Find vertices by id and then connect them by 'knows' edge
      g.V(marko.id()).as(a).V(vadas.id()).as(b).addE(Knows).from(a).to(b).property(StartTime, 2010).iterate()

      logger.info("Retrieving vertices by edge")

      // Retrieve vertices connected by edge we just created by label and start time.
      val vertices = g.E().hasLabel(Knows).has(StartTime, 2010).bothV().toList().sortBy(_.property(Name).value)

      vertices shouldBe List(marko, vadas)
    }

    "bulk load many vertices in one traversal" in fixture { g =>
      logger.info("Loading 50,000 vertices across 100 traversals")

      // convenience type for representing a graph with vertex being current end point.
      type VTraversal = GremlinScala[Vertex, HNil]

      // loads a range of numbers, each number representing a person vertex with it's value as the name and age.
      def load(range: Range): Unit = {
        logger.info("Loading vertices {} to {}", range.start, range.end)
        // Build a traversal with addV for each range value.  This is the most efficient way to load
        // a lot of data using a traversal based API, but we realize this is not most efficient as it takes
        // 50ms per batch (after initial batch), creates a giant call back and sends over a lot of data.
        // https://datastax-oss.atlassian.net/browse/JAVA-1311 for future work.
        val t = range.aggregate[Option[VTraversal]](None)((t, i) => t match {
          case Some(traversal) => Some(traversal.addV(Person).property(Name, s"$i").property(Age, i))
          case None => Some(g.addV(Person).property(Name, s"$i").property(Age, i)) // initial case
        }, (_, t) => t)
        t.get.iterate()
      }

      // create 50k vertices, 500 per traversal with 100 total traversals
      (0 until 100).map(i => i * 500 until i * 500 + 500).foreach(load)

      // 50k vertices should have been created
      val count = g.V().count().head()
      count shouldEqual 50000
    }
  }

  object SimpleSpec {

    object Name extends Key[String]("name")

    object Age extends Key[Int]("age")

    object StartTime extends Key[Int]("startTime")

    val Person = "person"
    val Knows = "knows"

    def fixture[A](test: (ScalaGraph) => Unit) = {
      val dseCluster = DseCluster.builder()
        .addContactPoint("127.0.0.1")
        .build()

      val graphName = "simplespec"
      val graphOptions = new GraphOptions()
        .setGraphName(graphName)

      try {
        val session = dseCluster.connect()

        // The following uses the DSE graph schema API, which is currently only supported by the string-based
        // execution interface.  Eventually there will be a programmatic API for making schema changes, but until
        // then this needs to be used.

        // Create graph
        logger.info("Creating graph {}", graphName)
        session.executeGraph("system.graph(name).ifNotExists().create()", ImmutableMap.of("name", graphName))


        // Clear the schema to drop any existing data and schema
        logger.info("Dropping any existing schema and data if present")
        session.executeGraph(new SimpleGraphStatement("schema.clear()").setGraphName(graphName))

        // Note: typically you would not want to use development mode and allow scans, but it is good for convenience
        // and experimentation during development.

        // Enable development mode and allow scans
        logger.info("Enabling development mode")
        session.executeGraph(new SimpleGraphStatement("schema.config().option('graph.schema_mode').set('development')")
          .setGraphName(graphName))
        logger.info("Allowing scans")
        session.executeGraph(new SimpleGraphStatement("schema.config().option('graph.allow_scan').set('true')")
          .setGraphName(graphName))

        // Create a ScalaGraph from a remote Traversal Source using withRemote
        // See: http://tinkerpop.apache.org/docs/current/reference/#connecting-via-remotegraph for more details
        val connection = DseRemoteConnection.builder(session)
          .withGraphOptions(graphOptions)
          .build()
        val graph = EmptyGraph.instance().asScala
          .configure(_.withRemote(connection))

        test(graph)
      } finally {
        dseCluster.close()
      }
    }
  }

}
