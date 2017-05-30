import scala.concurrent.duration.FiniteDuration

import gremlin.scala._
import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.scalatest._
import org.umlg.sqlg.structure.SqlgGraph

import scala.util.Random

// to start postgres with first run:
// docker run -e POSTGRESQL_PASSWORD=pass -p 9096:5432 postgres`
class PostgresSpec extends WordSpec with Matchers {

  "Gremlin-Scala with Sqlg over PostgreSQL" should {
    "create some vertices with properties" in new Fixture {
      val Number = Key[Int]("number")
      graph + ("thing", Number → 1)
      graph + ("thing", Number → 2)
      graph.graph.tx.commit()

      val vertices = graph.V.has(label = "thing", key = Number, predicate = P.eq(2)).toList
      vertices.size shouldBe 1
      vertices.head.property(Number).value shouldBe 2

      graph.close()
    }

    "demonstrate bulk loading and speed" in new Fixture {
      val Data = Key[String]("data")

      val ElemCount = 50000
      val longString = Random.nextString(length = 1024)
      // Turn on batch mode
      graph.graph.asInstanceOf[SqlgGraph].tx.normalBatchModeOn()
      timed { () ⇒
        (1 to ElemCount) foreach { i ⇒
          graph + ("elem", Data → longString)

          if (i % 10000 == 0) {
            println(s"committing tx after $i elements")
            graph.graph.tx().commit()
            graph.graph.asInstanceOf[SqlgGraph].tx.normalBatchModeOn()
          }
        }
        println("final commit after adding all elements")
        graph.graph.tx().commit()
      } match {
        case (_, duration) ⇒ println(s"time for adding $ElemCount elements: $duration")
      }

      timed { () ⇒
        graph.V.count().head
      } match {
        case (count, duration) ⇒ println(s"graph.V.count=$count, time elapsed: $duration")
      }

      timed { () ⇒
        graph.V.toList
      } match {
        case (_, duration) ⇒ println(s"time elapsed to fetch all vertices: $duration")
      }

      graph.close
    }
  }

  trait Fixture {
    val configuration = new BaseConfiguration()
    configuration.setProperty("jdbc.url", "jdbc:postgresql://localhost:9096/postgres")
    configuration.setProperty("jdbc.username", "postgres")
    configuration.setProperty("jdbc.password", "pass")
    val graph: ScalaGraph = SqlgGraph.open[SqlgGraph](configuration).asScala

    def timed[A](fun: () ⇒ A): (A, FiniteDuration) = {
      val start = System.currentTimeMillis
      val ret = fun()
      val duration = FiniteDuration(System.currentTimeMillis - start, scala.concurrent.duration.MILLISECONDS)
      (ret, duration)
    }
  }
}
