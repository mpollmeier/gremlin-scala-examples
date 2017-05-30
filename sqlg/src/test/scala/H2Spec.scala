import java.io.{File, FilenameFilter}

import scala.concurrent.duration.FiniteDuration
import gremlin.scala._
import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.scalatest._
import org.umlg.sqlg.structure.SqlgGraph

import scala.util.Random

class H2Spec extends WordSpec with Matchers {

  "Gremlin-Scala with Sqlg over H2" should {
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
    val dbName = "h2spec"
    val dbPath = "target"
    // Delete any pre-existing H2 db
    new File(dbPath).listFiles(new FilenameFilter {
      override def accept(dir: File, name: Label) = name.startsWith(s"$dbName.")
    }).foreach(_.delete)

    configuration.setProperty("jdbc.url", s"jdbc:hsqldb:file:$dbPath/$dbName")
    configuration.setProperty("jdbc.username", "SA")
    configuration.setProperty("jdbc.password", "")
    val graph: ScalaGraph = SqlgGraph.open[SqlgGraph](configuration).asScala

    def timed[A](fun: () ⇒ A): (A, FiniteDuration) = {
      val start = System.currentTimeMillis
      val ret = fun()
      val duration = FiniteDuration(System.currentTimeMillis - start, scala.concurrent.duration.MILLISECONDS)
      (ret, duration)
    }
  }
}
