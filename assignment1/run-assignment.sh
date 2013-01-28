#!/bin/bash
mkdir -p src/wordcount_classes
javac -classpath src/hadoop-core-2.0.0-mr1-cdh4.1.1.jar:src/hadoop-common-2.0.0-cdh4.1.1.jar:src/log4j-1.2.17.jar -d src/wordcount_classes src/DemoWordCount.java
jar -cvf ~/wordcount.jar -C src/wordcount_classes/ .
hadoop jar src/wordcount.jar \ -input bible+shakes.nopunc.gz -output wc -numReducers 5
