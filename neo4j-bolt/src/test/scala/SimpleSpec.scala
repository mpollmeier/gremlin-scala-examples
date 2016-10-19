import gremlin.scala._
import com.steelbridgelabs.oss.neo4j.structure.providers.DatabaseSequenceElementIdProvider
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraphConfigurationBuilder
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraphFactory
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph
import org.apache.commons.configuration.{ AbstractConfiguration, Configuration, PropertiesConfiguration }
import org.neo4j.driver.v1.{ AuthTokens, Driver, GraphDatabase }
import org.scalatest._

class SimpleSpec extends FlatSpec with Matchers {

  "Gremlin-Scala" should "create some vertices with properties" in {
    val driver: Driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "neo4j"))
    // val graph: Graph = new Neo4JGraph(driver, vertexIdProvider, edgeIdProvider)

    val configuration = Neo4JGraphConfigurationBuilder.connect("localhost", "neo4j", "admin")
      .withName("neo4j-bolt-test")
      .withElementIdProvider(classOf[ElementIdProvider])
      // .withElementIdProvider(classOf[DatabaseSequenceElementIdProvider])
      .build();

    // val a: DatabaseSequenceElementIdProvider  = ???

    // val configuration: Configuration = new PropertiesConfiguration()

    val graph: ScalaGraph = Neo4JGraphFactory.open(configuration).asScala
    println(graph.V.toList)

    // val dbPath = "target/simplespec"
    // FileUtils.removeAll(dbPath)
    // val graph = Neo4jGraph.open(dbPath).asScala
    // val Name = Key[String]("name")

    // (1 to 5) foreach { i ⇒
    //   graph + ("some_label", Name -> s"vertex $i")
    // }

    // val names = graph.V.value(Name).toList
    // names.size shouldBe 5
    // names foreach (_.startsWith("vertex") shouldBe true)

    // graph.close
  }
}
