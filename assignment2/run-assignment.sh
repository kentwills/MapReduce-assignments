#!/bin/bash
./etc/hadoop-cluster.sh StripesPMI -input bible+shakes.nopunc.gz -output kentwills-Stripes -numReducers 5
hadoop fs -get kentwills-Stripes
hadoop fs -rmr kentwills-Stripes
