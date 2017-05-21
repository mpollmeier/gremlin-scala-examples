#!/bin/bash
set -x #verbose

# neo4j-bolt depends on running neo4j locally
# dse-graph depends on running dse locally
for project in tinkergraph orientdb neo4j janusgraph; do
    cd $project
    sbt clean test
    cd ..
done
