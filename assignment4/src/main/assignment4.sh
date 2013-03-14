etc/hadoop-local.sh BuildPersonalizedPageRankRecords -input sample-large.txt -output PageRankRecords -numNodes 1458 -sources 9627181,9370233,10207721
hadoop fs -mkdir PageRank
etc/hadoop-local.sh PartitionGraph -input PageRankRecords -output PageRank/iter0000 -numPartitions 5 -numNodes 1458
etc/hadoop-local.sh RunPersonalizedPageRankBasic -base PageRank -numNodes 1458 -start 0 -end 20 -sources 9627181,9370233,10207721
etc/hadoop-local.sh ExtractTopPersonalizedPageRankNodes -input PageRank/iter0019 -output Rank -top 10 %-sources 9627181,9370233,10207721
