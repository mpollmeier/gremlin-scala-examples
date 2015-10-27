import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.sql.query.OResultSet
import gremlin.scala._
import java.util.{ArrayList => JArrayList}
import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.orientdb._
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal
import org.scalatest.{Matchers, WordSpec}
import scala.collection.JavaConversions._

class SimpleSpec extends WordSpec with Matchers {

  "vertices" should {
    "be found if they exist" in new Fixture {
      val v1 = sg.addVertex()
      val v2 = sg.addVertex()
      val v3 = sg.addVertex()

      gs.V(v1.id, v3.id).toList should have length 2
      gs.V().toList should have length 3
    }

    "not be found if they don't exist" in new Fixture {
      val list = gs.V("#3:999").toList
      list should have length 0
    }

    "set property after creation" in new Fixture {
      val v = sg.addVertex()
      val key = Key[String]("testProperty")
      v.setProperty(key, "testValue1")

      v.property(key).value shouldBe "testValue1"
      gs.V(v.id).value(key).toList shouldBe List("testValue1")
    }

    "set property during creation" in new Fixture {
      val property1 = "key1" → "value1"
      val property2 = "key2" → "value2"
      val v = sg.addVertex(Map(property1, property2))
      gs.V(v.id).values[String]("key1", "key2").toList shouldBe List("value1", "value2")
    }

    "using labels" in new Fixture {
      val v1 = sg.addVertex("label1")
      val v2 = sg.addVertex("label2")
      val v3 = sg.addVertex()

      val labels = gs.V.label.toSet
      // labels should have size 3
      labels should contain("V")
      labels should contain("label1")
      labels should contain("label2")
    }

    "supports vertex indexes" in new Fixture {
      // this is just an example usage, not really a test with a verification
      // you can see that it worked in the log output of OrientGraphStep
      // if you see something like an info of 'index will be queried with' then all is good
      // if you see 'scanning through all vertices without using an index' then something's wrong
      val label = "somelabel"
      val indexedProperty = "indexedProperty"
      val indexedValue = "indexedValue"
      val config = new BaseConfiguration()
      config.setProperty("type", "UNIQUE")
      config.setProperty("keytype", OType.STRING)
      sg.asJava.createVertexIndex(indexedProperty, label, config)
      sg.addVertex(label, indexedProperty -> indexedValue)

      graph.V
        .hasLabel(label)
        .has(indexedProperty, indexedValue)
        .toList should have size 1
    }
  }

  "edges" should {
    "be found if they exist" in new Fixture {
      val v1 = sg.addVertex()
      val v2 = sg.addVertex()
      val e1 = v1.addEdge("label1", v2)
      val e2 = v2.addEdge("label2", v1)

      gs.E(e2.id).toList should have length 1
      gs.E().toList should have length 2
    }

    "not be found if they don't exist" in new Fixture {
      val list = gs.E("#3:999").toList
      list should have length 0
    }

    "set property after creation" in new Fixture {
      val v1 = sg.addVertex()
      val v2 = sg.addVertex()
      val e = v1.addEdge("label1", v2)

      val key = Key[String]("testProperty")
      e.setProperty(key, "testValue1")

      e.property(key).value shouldBe "testValue1"
      gs.E(e.id).value(key).toList shouldBe List("testValue1")
    }

    "set property during creation" taggedAs(org.scalatest.Tag("foo")) ignore new Fixture {
      val v1 = sg.addVertex()
      val v2 = sg.addVertex()
      val property1 = "key1" → "value1"
      val property2 = "key2" → "value2"
      val e = v1.addEdge("label1", v2, Map(property1, property2))
      gs.E(e.id).values[String]("key1", "key2").toList shouldBe List("value1", "value2")
    }
  }

  "traversals" should {
    "follow outE" in new TinkerpopFixture {
      def traversal = gs.V(marko.id).outE
      traversal.label.toSet shouldBe Set("knows", "created")
      traversal.label.toList should have size 3
    }

    "follow outE for a label" in new TinkerpopFixture {
      def traversal = gs.V(marko.id).outE("knows")
      traversal.label.toSet shouldBe Set("knows")
      traversal.label.toList should have size 2
    }

    "follow inV" in new TinkerpopFixture {
      def traversal = gs.V(marko.id).outE.inV
      traversal.value[String]("name").toSet shouldBe Set("vadas", "josh", "lop")
    }

    "follow out" in new TinkerpopFixture {
      def traversal = gs.V(marko.id).out
      traversal.value[String]("name").toSet shouldBe Set("vadas", "josh", "lop")
    }

    "follow out for a label" in new TinkerpopFixture {
      def traversal = gs.V(marko.id).out("knows")
      traversal.value[String]("name").toSet shouldBe Set("vadas", "josh")
    }

    "follow in" in new TinkerpopFixture {
      def traversal = gs.V(josh.id).in
      traversal.value[String]("name").toSet shouldBe Set("marko")
    }

    "follow inE" in new TinkerpopFixture {
      def traversal = gs.V(josh.id).inE
      traversal.label.toSet shouldBe Set("knows")
    }

    "value" in new TinkerpopFixture {
      def traversal = gs.V(marko.id).out.value[Int]("age")
      traversal.toSet shouldBe Set(27, 32)
    }

    "properties" in new TinkerpopFixture {
      def traversal = gs.V(marko.id).out.properties("age")
      traversal.toSet.map(_.value) shouldBe Set(27, 32)
    }

    "filter" in new TinkerpopFixture {
      def traversal = gs.V(marko.id).out.filter(_.property[Int]("age").orElse(0) > 30)
      traversal.value[String]("name").toSet shouldBe Set("josh")
    }
  }

  "execute arbitrary OrientSQL" in new Fixture {
    (1 to 20) foreach { _ ⇒
      sg.addVertex()
    }

    val results: Seq[_] = graph.executeSql("select from V limit 10") match {
      case lst: JArrayList[_] ⇒ lst.toSeq
      case r: OResultSet[_]   ⇒ r.iterator().toSeq
      case other              ⇒ println(other.getClass()); println(other); ???
    }
    results should have length 10
  }

  trait Fixture {
    val graph = new OrientGraphFactory(s"memory:test-${math.random}").getNoTx()
    val gs = graph.asScala
    val sg = ScalaGraph(graph)
  }

  trait TinkerpopFixture {
    // val graph = new OrientGraphFactory("remote:localhost/graphtest", "root", "root").getNoTx()
    // val graph = new OrientGraphFactory("plocal:/home/mp/tmp/orientdb-community-2.1-rc5/databases/testgraph", "root", "root").getNoTx()
    val graph = new OrientGraphFactory(s"memory:test-${math.random}").getNoTx()
    val gs = graph.asScala
    val sg = ScalaGraph(graph)

    val marko = sg.addVertex("person", Map("name" -> "marko", "age" -> 29))
    val vadas = sg.addVertex("person", Map("name" -> "vadas", "age" -> 27))
    val lop = sg.addVertex("software", Map("name" -> "lop", "lang" -> "java"))
    val josh = sg.addVertex("person", Map("name" -> "josh", "age" -> 32))
    val ripple = sg.addVertex("software", Map("name" -> "ripple", "lang" -> "java"))
    val peter = sg.addVertex("person", Map("name" -> "peter", "age" -> 35))
    marko.addEdge("knows", vadas, Map("weight" -> 0.5d))
    marko.addEdge("knows", josh, Map("weight" -> 1.0d))
    marko.addEdge("created", lop, Map("weight" -> 0.4d))
    josh.addEdge("created", ripple, Map("weight" -> 1.0d))
    josh.addEdge("created", lop, Map("weight" -> 0.4d))
    peter.addEdge("created", lop, Map("weight" -> 0.2d))
  }
}
