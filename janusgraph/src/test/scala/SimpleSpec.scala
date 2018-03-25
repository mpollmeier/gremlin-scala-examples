import gremlin.scala._
import org.scalatest.{Matchers, WordSpec}
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.core.JanusGraph
import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoMapper 
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph

class SimpleSpec extends WordSpec with Matchers {

  "connect to janusgraph, pull out Saturn's keys and shutdown cleanly" when {

    "using inmemory janusgraph instance" in {
      val conf = new BaseConfiguration()
      conf.setProperty("storage.backend","inmemory")
      val graph = JanusGraphFactory.open(conf)
      implicit val scalaGraph = graph.asScala

      val Name = Key[String]("name")
      val Planet = "planet"
      val Saturn = "saturn"

      (1 to 4) foreach { i ⇒
        graph + (Planet, Name -> s"vertex $i")
      }
      val saturnV = graph + (Saturn, Name -> Saturn)
      val sunV = graph + ("sun", Name -> "sun")
      saturnV --- "orbits" --> sunV

      graph.V.count.head shouldBe 6
      graph.E.count.head shouldBe 1

      val traversal = graph.V.value(Name)
      traversal.toList.size shouldBe 6

      graph.V.hasLabel(Saturn).count.head shouldBe 1

      val saturnQ = graph.V.hasLabel(Saturn).head
      saturnQ.value2(Name) shouldBe Saturn

      graph.close
    }

    /** prerequisite: 
      * download and extract janusgraph-0.2.0-hadoop2
      * start it with `bin/janusgraph.sh -v start`
      * note: you can stop it with `bin/janusgraph.sh -v stop`
      */
    "connecting to remote janusgraph" in {
      import org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerGremlinV2d0
      import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper
      import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry

      val mapper = GraphSONMapper.build.addRegistry(JanusGraphIoRegistry.getInstance).create
      val serializer = new GraphSONMessageSerializerGremlinV2d0(mapper)

      // val serializer = new GryoMessageSerializerV3d0(GryoMapper.build)

// val serializer = new org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV1d0(GryoMapper.build.addRegistry(org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry.getInstance))
// val serializer = new org.apache.tinkerpop.gremlin.driver.ser.GryoLiteMessageSerializerV1d0(GryoMapper.build.addRegistry(org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry.getInstance))
// val serializer = new org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerGremlinV1d0(GryoMapper.build.addRegistry(org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistryV1d0.getInstance))
// val serializer = new org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerGremlinV2d0(GryoMapper.build.addRegistry(org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry.getInstance))
// val serializer = new org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV1d0(GryoMapper.build.addRegistry(org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistryV1d0.getInstance))

      val cluster = Cluster.build.addContactPoint("localhost").port(8182).serializer(serializer).create
      implicit val graph = EmptyGraph.instance.asScala.configure(_.withRemote(DriverRemoteConnection.using(cluster)))
      graph.traversal.V.drop.iterate

      val Name = Key[String]("name")
      val Planet = "planet"

      val saturn: Vertex = graph + (Planet, Name -> "saturn")
      val sun   : Vertex = graph + (Planet, Name -> "sun")

      saturn --- "orbits" --> sun

      graph.V.count.head shouldBe 2
      graph.E.count.head shouldBe 1

      cluster.close()
    }
  }
}
