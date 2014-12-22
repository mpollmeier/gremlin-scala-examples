About this project
==================

# Clone with caution - Titan 0.9.0-M1 aboard!

[Use this only for exploration, testing, and feedback and DO NOT use in production.](http://s3.thinkaurelius.com/docs/titan/0.9.0-M1/changelog.html)

The project uses the latest *experimental* release of [Titan 0.9.0-M1](https://groups.google.com/d/msg/aureliusgraphs/_onvDrvBEwk/lHCNMqefzacJ). According to the authors:

> 0.9.0-M1 is an experimental release intended for development use.

Worth to point out:

* This release uses TinkerPop 3.0.0.M6 = entails high-level API changes for Titan and Source written against Titan 0.5.2
and earlier will generally require modification to compile against Titan 0.9.0-M1
* The main feature of the 0.9.0 release is the implementation of the Tinkerpop 3 APIs in Titan = radical changes to the query language and query execution engine.
* Furthermore, large parts of the hadoop processing engine are now covered by Tinkerpop and Titan has been refactored accordingly.
* As TinkerPop 3 requires a Java 8 runtime, so too does Titan 0.9.0-M1.

**Clone with caution.**

# Introduction

Playground for my own self-learning in the area of [graph databases](http://en.wikipedia.org/wiki/Graph_database) with
[Titan graph database](http://thinkaurelius.github.io/titan/) and [Scala](http://www.scala-lang.org/).

There's a single integration test `HelloSpec` that requires a running instance of Titan db.

Download the latest version of Titan (the latest tested one is `0.9.0-M1`) and run `./bin/titan.sh -c cassandra-es start`
in the directory where you unzipped Titan.

    ➜  titan  ./bin/titan.sh -c cassandra-es start
    Forking Cassandra...
    Running `nodetool statusthrift`.. OK (returned exit status 0 and printed string "running").
    Forking Elasticsearch...
    Connecting to Elasticsearch (127.0.0.1:9300).. OK (connected to 127.0.0.1:9300).
    Forking Titan + Rexster...
    Connecting to Titan + Rexster (127.0.0.1:8184).... OK (connected to 127.0.0.1:8184).
    Run rexster-console.sh to connect.

With the infrastucture running, switch to [IntelliJ IDEA](https://www.jetbrains.com/idea/) and execute the test.

You should see the following output.

    0    [specs2.DefaultExecutionStrategy-1] INFO  com.thinkaurelius.titan.diskstorage.cassandra.thrift.CassandraThriftStoreManager  - Closed Thrift connection pooler.
    10   [specs2.DefaultExecutionStrategy-1] INFO  com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration  - Generated unique-instance-id=c0a80b3051261-japila-local1
    27   [specs2.DefaultExecutionStrategy-1] INFO  com.thinkaurelius.titan.diskstorage.Backend  - Configuring index [search]
    153  [specs2.DefaultExecutionStrategy-1] INFO  org.elasticsearch.plugins  - [Paul Bailey] loaded [], sites []
    770  [specs2.DefaultExecutionStrategy-1] INFO  com.thinkaurelius.titan.diskstorage.es.ElasticSearchIndex  - Configured remote host: 127.0.0.1 : 9300
    932  [specs2.DefaultExecutionStrategy-1] INFO  com.thinkaurelius.titan.diskstorage.Backend  - Initiated backend operations thread pool of size 16
    1034 [specs2.DefaultExecutionStrategy-1] INFO  com.thinkaurelius.titan.diskstorage.log.kcvs.KCVSLog  - Loaded unidentified ReadMarker start time Timepoint[1417527850370000 μs] into com.thinkaurelius.titan.diskstorage.log.kcvs.KCVSLog$MessagePuller@65664f27
    1270 [specs2.DefaultExecutionStrategy-1] INFO  com.thinkaurelius.titan.diskstorage.cassandra.thrift.CassandraThriftStoreManager  - Closed Thrift connection pooler.The test should

Use [http://localhost:8182/doghouse](http://localhost:8182/doghouse) to view the graph (nodes and edges) of the Gods family.

The project uses [Gremlin-Scala](https://github.com/mpollmeier/gremlin-scala) - *a thin wrapper for Gremlin to make it easily usable for Scala Developers.*
While you're at it, *Gremlin is a graph DSL for traversing graph databases*.

To stop the infrastructure (Titan, Elasticsearch, Cassandra, Rexster), execute `./bin/titan.sh -c cassandra-es stop`.

    ➜  titan  ./bin/titan.sh -c cassandra-es stop
    Killing Titan + Rexster (pid 915)...
    Killing Elasticsearch (pid 849)...
    Killing Cassandra (pid 745)...

## Debugging tips

### Cassandra

1. Cassandra and Elasticsearch store data/logs under `db/cassandra` and `db/es` directories, respectively.
2. Cassandra uses `conf/cassandra.yaml` for configuration.