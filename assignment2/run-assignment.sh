#!/bin/bash
./etc/hadoop-cluster.sh StripesPMI -input bible+shakes.nopunc.gz -output kentwills-Stripes -numReducers 5
./etc/hadoop-cluster.sh PairsPMI -input bible+shakes.nopunc.gz -output kentwills-Pairs -numReducers 5
hadoop fs -get kentwills-Stripes
hadoop fs -rm -r kentwills-Stripes
hadoop fs -get kentwills-Pairs
hadoop fs -rm -r kentwills-Pairs
