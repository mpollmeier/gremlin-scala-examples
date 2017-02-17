#!/bin/bash
set -x #verbose

for project in `ls -d */`; do
# for project in neo4j neo4j-bolt orientdb tinkergraph titan; do
    cd $project
    sbt clean test
    cd ..
done
