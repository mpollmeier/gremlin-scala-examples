import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.io.IoCore.gryo
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.process.traversal.{Order, P, Scope}
import java.lang.{Long ⇒ JLong, Double ⇒ JDouble}
import java.util.{Map ⇒ JMap}
import scala.collection.JavaConversions._
import org.scalatest.{Matchers, WordSpec}
import shapeless.HNil

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
    val ratedCounts: GremlinScala[Long, HNil] =
      for {
        person ← g.V.hasLabel(Person)
        count ← person.outE(Rated).count
      } yield count

    ratedCounts.max.head shouldBe 2161
  }

  "What year was the oldest movie made?" in {
    val min: Integer =
      g.V.hasLabel(Movie)
        .value(Year).min
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
    val traversal = for {
      genre ← g.V.hasLabel(Genre)
      count ← genre.start.inE(HasGenre).count
    } yield (genre.value2(Name), count)

    val genreMovieCounts = traversal.toMap

    genreMovieCounts should have size 18
    genreMovieCounts should contain("Animation" → 99)
    genreMovieCounts should contain("Drama" → 1405)
  }

  "For each movie, get its name and mean rating (or 0 if no ratings). Order by average rating and emit top 10." in {
    val traversal = for {
      movie ← g.V.hasLabel(Movie)
      stars ← movie.start.inE(Rated).value(Stars).mean
    } yield (movie.value2(Name), stars)

    val avgRatings = traversal.toMap

    avgRatings should contain("Lured" → 5)
    avgRatings should contain("Lamerica" → 4.75)
  }

  "For each movie with at least 11 ratings, emit a map of its name and average rating. " +
    "Sort the maps in decreasing order by their average rating. Emit the first 10 maps (i.e. top 10)." in {
      val movieAndRatings = for {
        movie ← g.V.hasLabel(Movie)
                  .where(_.inE(Rated).count().is(P.gt(10)))
        stars ← movie.start.inE(Rated).value(Stars).mean
      } yield (movie.value2(Name), stars)

      val top10 = movieAndRatings
        .orderBy(_._2, Order.decr)
        .limit(10)
        .toMap

      top10("Sanjuro").toDouble shouldBe 4.61 +- 0.1d
      top10("Rear Window").toDouble shouldBe 4.48 +- 0.1d
    }

  "Which programmers like Die Hard and what other movies do they like?" +
    "Group and count the movies by their name. Sort the group count map in decreasing order by the count." in {
      val dieHard: Vertex = g.V.has(Movie, Name, "Die Hard").head

      val popularMovies = for {
        programmer5Stars <- dieHard.start.inE(Rated).has(Stars, 5).outV
                            .where(_.out(HasOccupation).has(Name, "programmer"))
        otherMovie <-  programmer5Stars.start.outE(Rated).has(Stars, 5).inV
                          .filterNot(_ == dieHard)
      } yield otherMovie.value2(Name)

      val counts: Map[String, JLong] = popularMovies.groupCount.head.toMap
      val top10 = counts.toList.sortBy(_._2).reverse.take(10).toMap

      counts("Braveheart") shouldBe 24
      counts("Star Wars: Episode V - The Empire Strikes Back") shouldBe 36
    }

  "What 80's action movies do 30-something programmers like?" +
    "Group count the movies by their name and sort the group count map in decreasing order by value." in {
      val counts: Map[String, JLong] =
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
          .groupCount().head.toMap

      val top10 = counts.toList.sortBy(_._2).reverse.take(10).toMap

      top10("Raiders of the Lost Ark") shouldBe 26
      top10("Star Wars: Episode V - The Empire Strikes Back") shouldBe 26
      top10("Terminator, The") shouldBe 23
      top10("Star Wars: Episode VI - Return of the Jedi") shouldBe 22
      top10("Princess Bride, The") shouldBe 19
      top10("Aliens") shouldBe 18
      top10("Indiana Jones and the Last Crusade") shouldBe 11
      top10("Star Trek: The Wrath of Khan") shouldBe 10
      top10("Abyss, The") shouldBe 9
    }

  "What is the most liked movie in each decade?" in {
    val counts: JMap[Integer, String] = g.V()
      .hasLabel(Movie)
      .where(_.inE(Rated).count().is(P.gt(10)))
      .groupBy { movie =>
        val year = movie.value2(Year)
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
