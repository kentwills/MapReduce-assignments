#!/bin/bash
ant
./etc/hadoop-cluster.sh WordCount -input bible+shakes.nopunc.gz -output wc -numReducers 5

