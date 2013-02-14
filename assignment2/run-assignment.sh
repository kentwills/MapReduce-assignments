#!/bin/bash
./etc/hadoop-cluster.sh PairsPMI -input bible+shakes.nopunc.gz -output kentwills-Stripes -numReducers 5
hadoop fs -get kentwills-Pairs
hadoop fs -rmr kentwills-Pairs
