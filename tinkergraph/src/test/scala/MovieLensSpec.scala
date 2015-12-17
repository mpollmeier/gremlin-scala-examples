import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.io.IoCore.gryo
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.process.traversal.{Order, P, Scope}
import java.lang.{Long ⇒ JLong, Double ⇒ JDouble}
import java.util.{Map ⇒ JMap}
import scala.collection.JavaConversions._
import org.scalatest.{Matchers, WordSpec}

/**
  * Examples to traverse the MovieLens graph.
  * [Discussion in usergroup](https://groups.google.com/forum/#!msg/gremlin-users/jtWhwFpqnng/I-juFwyVCAAJ)
  * Based on [presentation](http://www.slideshare.net/slidarko/the-gremlin-traversal-language) by Marko Rodriguez and Daniel Kuppitz
  *
  * Differences fro traversals in their presentation:
  * 1) edge label 'category' is called 'hasGenre'
  * 2) edge label 'occupation' is called 'hasOccupation'
  */
class MovieLensSpec extends WordSpec with Matchers {
  val Name = Key[String]("name")
  val Stars = Key[Int]("stars")
  val Year = Key[Int]("year")


  object Label {
    val Person = "person"
    val Movie = "movie"
    val Occupation = "occupation"
    val HasOccupation = "hasOccupation"
    val Genre = "genre"
    val HasGenre = "hasGenre"
    val Rated = "rated"
  }
  import Label._

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
      g.V.has(Movie, Name, "Die Hard")
        .inE(Rated)
        .value(Stars)
        .mean
        .head

    "%.2f".format(avgRating) shouldBe "4.12"
  }

  "For each vertex, emit its label, then group and count each distinct label" in {
    val groupCount: JMap[String, JLong] =
      g.V.label.groupCount.head
    groupCount.get(Occupation) shouldBe 21
    groupCount.get(Movie) shouldBe 3546
    groupCount.get(Person) shouldBe 6040
    groupCount.get(Genre) shouldBe 18
  }

  "For each rated-edge, emit its stars property value and compute the average value" in {
    val meanStars: JDouble =
      g.E.hasLabel(Rated)
        .value(Stars).mean
        .head

    "%.2f".format(meanStars) shouldBe "3.57"
  }

  "Get the maximum number of movies a single user rated" in {
    val max: JLong =
      g.V.hasLabel(Person)
        .flatMap(_.outE(Rated).count)
        .max
        .head

    max shouldBe 2161
  }

  "What year was the oldest movie made?" in {
    val min: Integer =
      g.V.hasLabel(Movie)
        .value(Year)
        .min
        .head

    min shouldBe 1919
  }

  "For each vertex that is labeled 'genre', emit the name property value of that vertex" in {
    val categories: Set[String] =
      g.V.hasLabel(Genre)
        .value(Name)
        .toSet

    categories should contain("Animation")
    categories should contain("Thriller")
    categories should have size 18
  }

  "For each genre vertex, emit a map of its name and the number of movies it represents" in {
    val genreMovieCounts =
      g.V.hasLabel(Genre).as("a", "b")
        .select("a", "b")
        .by("name")
        .by(__.inE(HasGenre).count)
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
      g.V.hasLabel(Movie).as("a", "b")
        .select("a", "b")
        .by("name")
        .by(
          __.coalesce(
          __.inE(Rated).values("stars"),
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
        g.V.hasLabel(Movie).as("a", "b")
          .where(_.inE(Rated).count().is(P.gt(10)))
          .select("a", "b")
          .by("name")
          .by(__.inE(Rated).values("stars").mean())
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
        g.V.has(Movie, Name, "Die Hard").as("a")
          .inE(Rated).has(Stars, 5).outV
          .where(_.out(HasOccupation).has(Name, "programmer"))
          .outE(Rated).has(Stars, 5).inV
          .where(P.neq("a"))
          .map(_.value2(Name))
          .groupCount
          .order(Scope.local).by(Order.valueDecr)
          .limit(Scope.local, 10)
          .head

      counts.get("Braveheart") shouldBe 24
      counts.get("Star Wars: Episode V - The Empire Strikes Back") shouldBe 36
    }

  "What 80's action movies do 30-something programmers like?" +
    "Group count the movies by their name and sort the group count map in decreasing order by value." in {
      val counts: JMap[String, JLong] =
        g.V
          .`match`(
            __.as("a").hasLabel(Movie),
            __.as("a").out(HasGenre).has("name", "Action"),
            __.as("a").has("year", P.between(1980, 1990)),
            __.as("a").inE(Rated).as("b"),
            __.as("b").has("stars", 5),
            __.as("b").outV().as("c"),
            __.as("c").out(HasOccupation).has("name", "programmer"),
            __.as("c").has("age", P.between(30, 40))
          )
          .select[Vertex]("a")
          .map(_.value[String]("name"))
          .groupCount()
          .order(Scope.local).by(Order.valueDecr)
          .limit(Scope.local, 10)
          .head

        counts.get("Raiders of the Lost Ark") shouldBe 26
        counts.get("Star Wars: Episode V - The Empire Strikes Back") shouldBe 26
        counts.get("Terminator, The") shouldBe 23
        counts.get("Star Wars: Episode VI - Return of the Jedi") shouldBe 22
        counts.get("Princess Bride, The") shouldBe 19
        counts.get("Aliens") shouldBe 18
        counts.get("Indiana Jones and the Last Crusade") shouldBe 11
        counts.get("Star Trek: The Wrath of Khan") shouldBe 10
        counts.get("Abyss, The") shouldBe 9
    }

  // TODO: fix - group step behaviour changed
  "What is the most liked movie in each decade?" ignore {
    val counts: JMap[Integer, String] = g.V()
      .hasLabel(Movie)
      .where(_.inE(Rated).count().is(P.gt(10)))
      .group { v ⇒
        val year = v.value2(Year)
        val decade = (year / 10)
        (decade * 10): Integer
      }
      .map { moviesByDecade ⇒
        val highestRatedByDecade = moviesByDecade.mapValues { movies ⇒
          movies.toList
            .sortBy { _.inE(Rated).value(Stars).mean().head }
            .reverse.head //get the movie with the highest mean rating
        }
        highestRatedByDecade.mapValues(_.value2(Name))
      }
      .order(Scope.local).by(Order.keyIncr)
      .head

    counts.get(1910) shouldBe "Daddy Long Legs"
    counts.get(1920) shouldBe "General, The"
    counts.get(1930) shouldBe "City Lights"
    counts.get(1940) shouldBe "Third Man, The"
    counts.get(1960) shouldBe "Sanjuro"
    counts.get(1970) shouldBe "Godfather, The"
    counts.get(1980) shouldBe "Raiders of the Lost Ark"
    counts.get(1990) shouldBe "Shawshank Redemption, The"
    counts.get(2000) shouldBe "Almost Famous"
  }
}
