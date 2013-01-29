#!/bin/bash
ant
./etc/hadoop-cluster.sh WordCount -input bible+shakes.nopunc.gz -output kentwills -numReducers 5

