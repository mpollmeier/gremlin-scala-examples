import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.io.IoCore.gryo
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.process.traversal.{Order, P, Scope}
import java.lang.{Long ⇒ JLong, Double ⇒ JDouble}
import java.util.{Map ⇒ JMap}
import scala.collection.JavaConversions._
import org.scalatest.{Matchers, WordSpec}

class MovieLensSpec extends WordSpec with Matchers {

  val g: ScalaGraph[TinkerGraph] = {
    val graph = TinkerGraph.open
    graph.io(gryo()).readGraph("src/test/resources/movie-lens.kryo")
    graph.asScala
  }

  "Get the vertex and edge counts for the graph" in {
    g.V.count.head shouldBe 9625
    g.E.count.head shouldBe 969719
  }

  "What is Die Hard's average rating?" in {
    val avgRating: JDouble =
      g.V.has("movie", "name", "Die Hard")
        .inE("rated")
        .values("stars")
        .mean
        .head

    "%.2f".format(avgRating) shouldBe "4.12"
  }

  "For each vertex, emit its label, then group and count each distinct label" in {
    val groupCount: JMap[String, JLong] =
      g.V.label.groupCount.head
    groupCount.get("occupation") shouldBe 21
    groupCount.get("movie") shouldBe 3546
    groupCount.get("person") shouldBe 6040
    groupCount.get("genre") shouldBe 18
  }

  "For each rated-edge, emit its stars property value and compute the average value" in {
    val meanStars: JDouble =
      g.E.hasLabel("rated")
        .values("stars").mean
        .head

    "%.2f".format(meanStars) shouldBe "3.57"
  }

  "Get the maximum number of movies a single user rated" in {
    val max: JLong =
      g.V.hasLabel("person")
        .flatMap(_.outE("rated").count)
        .max
        .head

    max shouldBe 2161
  }

  "What year was the oldest movie made?" in {
    val min: Integer =
      g.V.hasLabel("movie")
        .values[Integer]("year")
        .min
        .head

    min shouldBe 1919
  }

  "For each vertex that is labeled 'genre', emit the name property value of that vertex" in {
    val categories: Set[String] =
      g.V.hasLabel("genre")
        .values[String]("name")
        .toSet

    categories should contain("Animation")
    categories should contain("Thriller")
    categories should have size 18
  }

  "For each genre vertex, emit a map of its name and the number of movies it represents" in {
    val genreMovieCounts =
      g.V.hasLabel("genre").as("a", "b")
        .select("a", "b")
        .by("name")
        .by(__.inE("hasGenre").count)
        .toList

    genreMovieCounts should have size 18
    assertGenreMovieCount("Animation", 99)
    assertGenreMovieCount("Drama", 1405)

    def assertGenreMovieCount(genre: String, count: Int) = {
      val map = genreMovieCounts.find(_.get("a") == genre).get
      map.get("b") shouldBe count
    }
  }

  "For each movie, get its name and mean rating (or 0 if no ratings). Order by average rating and emit top 10." in {
    val avgRatings =
      g.V.hasLabel("movie").as("a", "b")
        .select("a", "b")
        .by("name")
        .by(
          __.coalesce(
          __.inE("rated").values("stars"),
          __.constant(0)
        ).mean
        )
        .order.by(__.select("b"), Order.decr)
        .limit(10)
        .toList

    assertMapEntry("Lured", 5)
    assertMapEntry("Lamerica", 4.75)

    def assertMapEntry(name: String, value: Number) = {
      val map = avgRatings.find(_.get("a") == name).get
      map.get("b") shouldBe value
    }
  }

  "For each movie with at least 11 ratings, emit a map of its name and average rating. " +
    "Sort the maps in decreasing order by their average rating. Emit the first 10 maps (i.e. top 10)." in {
      val avgRatings: List[JMap[String, Any]] =
        g.V.hasLabel("movie").as("a", "b")
          .where(_.inE("rated").count().is(P.gt(10)))
          .select("a", "b")
          .by("name")
          .by(__.inE("rated").values("stars").mean())
          .order.by(__.select("b"), Order.decr)
          .limit(10)
          .toList

      assertMapEntry("Sanjuro", 4.61)
      assertMapEntry("Rear Window", 4.48)

      def assertMapEntry(name: String, value: Double) = {
        val map = avgRatings.find(_.get("a") == name).get
        map.get("b").asInstanceOf[Double] shouldBe value +- 0.1
      }
    }

  "Which programmers like Die Hard and what other movies do they like?" +
    "Group and count the movies by their name. Sort the group count map in decreasing order by the count." in {
      val counts: JMap[String, JLong] =
        g.V.has("movie", "name", "Die Hard").as("a")
          .inE("rated").has("stars", 5).outV
          .where(_.out("hasOccupation").has("name", "programmer"))
          .outE("rated").has("stars", 5).inV
          .where(P.neq("a"))
          .groupCount
          .map(_.map{ case (key, value) => (key.value[String]("name"), value)})
          .order(Scope.local).by(Order.valueDecr)
          .limit(Scope.local, 10)
          .head

      counts.get("Braveheart") shouldBe 24
      counts.get("Star Wars: Episode V - The Empire Strikes Back") shouldBe 36
    }

  // "What 80's action movies do 30-something programmers like?" +
  //   "Group count the movies by their name and sort the group count map in decreasing order by value." in {
      // val counts: JMap[Verte]
  //   }
}
