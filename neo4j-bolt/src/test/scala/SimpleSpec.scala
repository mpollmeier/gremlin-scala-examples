import gremlin.scala._
import com.steelbridgelabs.oss.neo4j.structure.providers.DatabaseSequenceElementIdProvider
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraphConfigurationBuilder
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraphFactory
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph
import org.apache.commons.configuration.{ AbstractConfiguration, Configuration, PropertiesConfiguration }
import org.neo4j.driver.v1.{ AuthTokens, Driver, GraphDatabase }
import org.scalatest._

// to start neo4j with bolt protocol first run:
// docker run --publish=7474:7474 --publish=7687:7687 --volume=/tmp/neo4j/data:/data --volume=/tmp/neo4j/logs:/logs neo4j:3.0
// then go to http://localhost:7474/ and change the password from `neo4j` to `admin`
class SimpleSpec extends FlatSpec with Matchers {

  "Gremlin-Scala" should "create some vertices with properties" in {
    val driver: Driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "neo4j"))

    val configuration = Neo4JGraphConfigurationBuilder.connect("localhost", "neo4j", "admin")
      .withName("neo4j-bolt-test")
      .withElementIdProvider(classOf[ElementIdProvider])
      .build();

    val graph: ScalaGraph = Neo4JGraphFactory.open(configuration).asScala

    val Number = Key[Int]("number")
    val v1 = graph + ("elem", Number -> 1)

    println(graph.V.toList)

    // (1 to 5) foreach { i ⇒
    //   graph + ("some_label", Name -> s"vertex $i")
    // }

    // val names = graph.V.value(Name).toList
    // names.size shouldBe 5
    // names foreach (_.startsWith("vertex") shouldBe true)

    graph.close
  }
}
