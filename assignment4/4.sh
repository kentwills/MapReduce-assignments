hadoop fs -rm -r PageRank
hadoop fs -rm -r PageRankRecords
hadoop fs -rm -r Rank
etc/hadoop-cluster.sh BuildPersonalizedPageRankRecords -input sample-large.txt -output PageRankRecords -numNodes 1458 %-sources RECORDS
hadoop fs -mkdir PageRank
etc/hadoop-cluster.sh PartitionGraph -input PageRankRecords -output PageRank/iter0000 -numPartitions 5 -numNodes 1458
etc/hadoop-cluster.sh RunPersonalizedPageRankBasic -base PageRank -numNodes 1458 -start 0 -end 20 %-sources RECORDS
etc/hadoop-cluster.sh ExtractTopPersonalizedPageRankNodes -input YOURNAME-PageRank/iter0019 -output Rank -top 10 %-sources RECORDS
