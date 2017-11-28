import scala.concurrent.duration.FiniteDuration
import java.util.UUID

import com.steelbridgelabs.oss.neo4j.structure.{Neo4JGraphFactory, Neo4JGraphConfigurationBuilder}
import gremlin.scala._
import org.neo4j.driver.v1.{Driver, GraphDatabase}
import org.scalatest._
import scala.util.Random

// to start neo4j with bolt protocol first run:
// `docker run --env=NEO4J_AUTH=none --publish=7474:7474 --publish=7687:7687 --volume=/tmp/neo4j/data:/data --volume=/tmp/neo4j/logs:/logs neo4j:3.0`
// then go to http://localhost:7474/ and change the password from `neo4j` to `admin`
// TODO: add to travis run - would have to make the password change part of the test setup...
class SimpleSpec extends WordSpec with Matchers {

  "Gremlin-Scala with neo4j-bolt" should {
    "create some vertices with properties" in new Fixture {
      val Name = Key[String]("name")
      graph + ("thing", Name → "name 1")
      graph + ("thing", Name → "name 2")
      graph.graph.tx.commit()

      val vertices = graph.V.has(label = "thing", key = Name, predicate = P.eq[String]("name 2")).toList
      vertices.size shouldBe 1
      vertices.head.property(Name).value shouldBe "name 2"

      graph.close()
    }

    "demonstrate bulk loading and speed" in new Fixture {
      val Data = Key[String]("data")

      val ElemCount = 50000
      val longString = Random.nextString(length = 1024)
      timed { () ⇒
        (1 to ElemCount) foreach { i ⇒
          graph + ("elem", Data → longString)

          if (i % 10000 == 0) {
            println(s"committing tx after $i elements")
            graph.graph.tx().commit()
          }
        }
        println("final commit after adding all elements")
        graph.graph.tx().commit()
      } match {
        case (_, duration) ⇒ println(s"time for adding $ElemCount elements: $duration")
      }

      // unfortunately even a simple `count` operation fetches all vertices over the wire and is therefor quite slow and not scalable
      timed { () ⇒
        graph.V.count().head
      } match {
        case (count, duration) ⇒ println(s"graph.V.count=$count, time elapsed: $duration")
      }

      // any follow up queries are fast then, since it's caching the results locally
      timed { () ⇒
        graph.V.toList
        // val allData = graph.V.value(Data).toList
      } match {
        case (_, duration) ⇒ println(s"time elapsed to fetch all vertices: $duration")
      }

      graph.close
    }
  }

  trait Fixture {
    val configuration = Neo4JGraphConfigurationBuilder.connect("localhost", "neo4j", "admin")
      // .withName(s"neo4j-bolt")
      .withName(s"neo4j-bolt-${UUID.randomUUID.toString}")
      .withElementIdProvider(classOf[ElementIdProvider])
      .build();
    val graph: ScalaGraph = Neo4JGraphFactory.open(configuration).asScala

    def timed[A](fun: () ⇒ A): (A, FiniteDuration) = {
      val start = System.currentTimeMillis
      val ret = fun()
      val duration = FiniteDuration(System.currentTimeMillis - start, scala.concurrent.duration.MILLISECONDS)
      (ret, duration)
    }
  }
}
