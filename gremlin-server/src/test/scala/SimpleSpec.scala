import gremlin.scala._
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoMapper 
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerIoRegistryV3d0
import org.scalatest.{Matchers, WordSpec}

class SimpleSpec extends WordSpec with Matchers {

  /** prerequisite: 
    * download gremlin-server 3.3.1 
    * start it with `bin/gremlin-server.sh conf/gremlin-server.yaml`
    * (you can stop it with C-c)
    */
  "remotely execute traversals in gremlin-server (which uses tinkergraph internally)" in {
    val serializer = new GryoMessageSerializerV3d0(GryoMapper.build.addRegistry(TinkerIoRegistryV3d0.instance))
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
