// utility to create kryo file from movielens sample graph
// http://www.slideshare.net/slidarko/the-gremlin-traversal-language
// https://gist.github.com/dkuppitz/64a9f1ba30ca652d067a
// http://files.grouplens.org/datasets/movielens/ml-1m.zip

/* usage:
gremlin> :load /tmp/ml.groovy
==>true
gremlin> graph = TinkerGraph.open()
==>tinkergraph[vertices:0 edges:0]
gremlin> MovieLensParser.load(graph, '/tmp/ml-1m')
Processing movies.dat...
Processing users.dat...
Processing ratings.dat...
Loading took (ms): 20185
==>null
gremlin> graph.io(IoCore.gryo()).writeGraph("/tmp/movie-lens.kryo")
*/

class MovieLensParser {

    static Map occupations

    static {
        occupations = [0: "other", 1: "academic/educator", 2: "artist",
                3: "clerical/admin", 4: "college/grad student", 5: "customer service",
                6: "doctor/health care", 7: "executive/managerial", 8: "farmer",
                9: "homemaker", 10: "K-12 student", 11: "lawyer", 12: "programmer",
                13: "retired", 14: "sales/marketing", 15: "scientist", 16: "self-employed",
                17: "technician/engineer", 18: "tradesman/craftsman", 19: "unemployed", 20: "writer"]
    }

    public static void parse(final Graph graph, final String dataDirectory) {

        def g = graph.traversal()

        println 'Processing movies.dat...'
        // MovieID::Title::Genres
        new File(dataDirectory + '/movies.dat').eachLine { final String line ->

            def components = line.split("::")
            def movieId = components[0].toInteger()
            def movieTitleYear = components[1] =~ /(.*\b)\s*\((\d+)\)/

            if (!movieTitleYear.find()) return

            def movieTitle = movieTitleYear.group(1)
            def movieYear = movieTitleYear.group(2).toInteger()
            def genres = components[2]
            def movieVertex = graph.addVertex(label, 'movie', 'uid', 'm' + movieId, 'movieId', movieId, 'name', movieTitle, 'year', movieYear)

            genres.split('\\|').each { def genre ->
                def genreVertex = g.V().has('uid', 'g' + genre).tryNext().orElseGet {graph.addVertex(label, 'genre', 'uid', 'g' + genre, 'name', genre)}
                movieVertex.addEdge('hasGenre', genreVertex)
            }
        }

        println 'Processing users.dat...'
        // UserID::Gender::Age::Occupation::Zip-code
        new File(dataDirectory + '/users.dat').eachLine { final String line ->

            def components = line.split("::")
            def userId = components[0].toInteger()
            def userGender = components[1]
            def userAge = components[2].toInteger()
            def occupationId = components[3].toInteger()
            def userZipcode = components[4]
            def userVertex = graph.addVertex(label, 'person', 'uid', 'u' + userId, 'userId', userId, 'gender', userGender, 'age', userAge, 'zipcode', userZipcode)

            def occupationVertex = g.V().has('uid', 'o' + occupationId).tryNext().orElseGet {
                graph.addVertex(label, 'occupation', 'uid', 'o' + occupationId, 'jobId', occupationId, 'name', occupations.get(occupationId))
            }

            userVertex.addEdge('hasOccupation', occupationVertex)
        }

        println 'Processing ratings.dat...'
        // UserID::MovieID::Rating::Timestamp
        new File(dataDirectory + '/ratings.dat').eachLine { final String line ->
            def components = line.split("::")
            def userId = components[0].toInteger()
            def movieId = components[1].toInteger()
            def stars = components[2].toInteger()
            def time = components[3].toLong()
            def userTraversal = g.V().has('uid', 'u' + userId)
            def movieTraversal = g.V().has('uid', 'm' + movieId)
            if (userTraversal.hasNext() && movieTraversal.hasNext()) {
                userTraversal.next().addEdge('rated', movieTraversal.next(), 'stars', stars, 'time', time)
            }
        }
    }

    public static void load(final Graph graph, final String dataDirectory) {
        graph.createIndex('uid', Vertex.class)
        def start = System.currentTimeMillis()
        parse(graph, dataDirectory)
        println "Loading took (ms): " + (System.currentTimeMillis() - start)
    }
}
