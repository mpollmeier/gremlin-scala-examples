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
      val v1 = graph + MyLabel
      val v2 = graph + MyLabel
      val v3 = graph + MyLabel

      graph.V(v1.id, v3.id).toList should have length 2
      graph.V.toList should have length 3
    }

    "not be found if they don't exist" in new Fixture {
      val list = graph.V("#3:999").toList
      list should have length 0
    }

    "set property after creation" in new Fixture {
      val v = graph + MyLabel
      val key = Key[String]("testProperty")
      v.setProperty(key, "testValue1")

      v.property(key).value shouldBe "testValue1"
      graph.V(v.id).value(key).toList shouldBe List("testValue1")
    }

    "set property during creation" in new Fixture {
      val Key1 = Key[String]("key1")
      val Key2 = Key[String]("key2")
      val v = graph + (MyLabel, Key1 -> "value1", Key2 -> "value2")

      val vertex = graph.V(v.id).head
      vertex.value2(Key1) shouldBe "value1"
      vertex.value2(Key2) shouldBe "value2"
    }

    "using labels" in new Fixture {
      val v1 = graph + "label1"
      val v2 = graph + "label2"
      val v3 = graph.addVertex()

      val labels = graph.V.label.toSet
      labels should have size 3

      labels should contain("label1")
      labels should contain("label2")
    }

    "supports vertex indexes" in new Fixture {
      // this is just an example usage, not really a test with a verification
      // you can see that it worked in the log output of OrientGraphStep
      // if you see something like an info of 'index will be queried with' then all is good
      // if you see 'scanning through all vertices without using an index' then something's wrong
      val key  = Key[String]("indexedProperty")
      val indexedValue = "indexedValue"
      val config = new BaseConfiguration()
      config.setProperty("type", "UNIQUE")
      config.setProperty("keytype", OType.STRING)
      graphAsJava.createVertexIndex(key.name, MyLabel, config)
      graph + (MyLabel, key -> indexedValue)

      graph.V
        .hasLabel(MyLabel)
        .has(key, indexedValue)
        .toList should have size 1
    }
  }

  "edges" should {
    "be found if they exist" in new Fixture {
      val v1 = graph + MyLabel
      val v2 = graph + MyLabel
      val e1 = v1 --- "label1" --> v2
      val e2 = v1 <-- "label2" --- v2

      graph.E(e2.id).toList should have length 1
      graph.E().toList should have length 2
    }

    "not be found if they don't exist" in new Fixture {
      val list = graph.E("#3:999").toList
      list should have length 0
    }

    "set property after creation" in new Fixture {
      val v1 = graph + MyLabel
      val v2 = graph + MyLabel
      val e = v1 --- "label1" --> v2

      val key = Key[String]("testProperty")
      e.setProperty(key, "testValue1")

      e.property(key).value shouldBe "testValue1"
      graph.E(e.id).value(key).head shouldBe "testValue1"
    }

    "set property during creation" in new Fixture {
      val Key1 = Key[String]("key1")
      val Key2 = Key[String]("key2")

      val v1 = graph + MyLabel
      val v2 = graph + MyLabel
      v1 --- (MyLabel, Key1 -> "value1", Key2 -> "value2") --> v2

      val e = graph.E.head
      e.value2(Key1) shouldBe "value1"
      e.value2(Key2) shouldBe "value2"
    }
  }

  "traversals" should {
    "follow outE" in new TinkerpopFixture {
      def traversal = graph.V(marko.id).outE
      traversal.label.toSet shouldBe Set("knows", "created")
      traversal.label.toList should have size 3
    }

    "follow outE for a label" in new TinkerpopFixture {
      def traversal = graph.V(marko.id).outE("knows")
      traversal.label.toSet shouldBe Set("knows")
      traversal.label.toList should have size 2
    }

    "follow inV" in new TinkerpopFixture {
      def traversal = graph.V(marko.id).outE.inV
      traversal.value(Name).toSet shouldBe Set("vadas", "josh", "lop")
    }

    "follow out" in new TinkerpopFixture {
      def traversal = graph.V(marko.id).out
      traversal.value(Name).toSet shouldBe Set("vadas", "josh", "lop")
    }

    "follow out for a label" in new TinkerpopFixture {
      def traversal = graph.V(marko.id).out("knows")
      traversal.value(Name).toSet shouldBe Set("vadas", "josh")
    }

    "follow in" in new TinkerpopFixture {
      def traversal = graph.V(josh.id).in
      traversal.value(Name).toSet shouldBe Set("marko")
    }

    "follow inE" in new TinkerpopFixture {
      def traversal = graph.V(josh.id).inE
      traversal.label.toSet shouldBe Set("knows")
    }

    "value" in new TinkerpopFixture {
      def traversal = graph.V(marko.id).out.value(Age)
      traversal.toSet shouldBe Set(27, 32)
    }

    "properties" in new TinkerpopFixture {
      def traversal = graph.V(marko.id).out.properties("age")
      traversal.toSet.map(_.value) shouldBe Set(27, 32)
    }

    "filter" in new TinkerpopFixture {
      def traversal = graph.V(marko.id).out.filter(_.property(Age).orElse(0) > 30)
      traversal.value[String]("name").toSet shouldBe Set("josh")
    }
  }

  "execute arbitrary OrientSQL" in new Fixture {
    (1 to 20) foreach { _ ⇒
      graph.addVertex()
    }

    val results: Seq[_] = graphAsJava.executeSql("select from V limit 10") match {
      case lst: JArrayList[_] ⇒ lst.toSeq
      case r: OResultSet[_]   ⇒ r.iterator().toSeq
      case other              ⇒ println(other.getClass()); println(other); ???
    }
    results should have length 10
  }

  trait Fixture {
    val MyLabel = "mylabel"
    val graphAsJava: OrientGraph = new OrientGraphFactory(s"memory:test-${math.random}").getNoTx()
    val graph = graphAsJava.asScala
  }

  trait TinkerpopFixture {
    // val graph = new OrientGraphFactory("remote:localhost/graphtest", "root", "root").getNoTx()
    // val graph = new OrientGraphFactory("plocal:/home/mp/tmp/orientdb-community-2.1-rc5/databases/testgraph", "root", "root").getNoTx()
    val graph = new OrientGraphFactory(s"memory:test-${math.random}").getNoTx().asScala

    val Person = "Person"
    val Software = "Person"
    val Knows = "knows"
    val Created = "created"
    val Name = Key[String]("name")
    val Age = Key[Int]("age")
    val Lang = Key[String]("lang")
    val Weight = Key[Double]("weight")

    val marko = graph + (Person, Name -> "marko", Age -> 29)
    val vadas = graph + (Person, Name -> "vadas", Age -> 27)
    val lop = graph + (Software, Name -> "lop", Lang -> "java")
    val josh = graph + (Person, Name -> "josh", Age -> 32)
    val ripple = graph + (Software, Name -> "ripple", Lang -> "java")
    val peter = graph + (Person, Name -> "peter", Age -> 35)
    marko --- (Knows, Weight -> 0.5d) --> vadas
    marko --- (Knows, Weight -> 1.0d) --> josh
    marko --- (Created, Weight -> 0.4d) --> lop
    josh --- (Created, Weight -> 1.0d) --> ripple
    josh --- (Created, Weight -> 0.4d) --> lop
    peter --- (Created, Weight -> 0.2d) --> lop
  }
}
