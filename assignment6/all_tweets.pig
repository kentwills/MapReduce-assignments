-- Use UDF
REGISTER 'tweet_time_func.py' USING jython AS t;

-- Load Data
tweets = load '/user/shared/tweets2011/tweets2011.txt' as (id:int, time:chararray, username:chararray, text:chararray);

-- Get the bucketing for each block
tweet_buckets = FOREACH tweets GENERATE t.time_normalize_block(time) AS hour_block;

-- Count by group
grouped = GROUP tweet_buckets BY hour_block;
counts =  FOREACH grouped GENERATE group, COUNT(tweet_buckets.hour_block) as num_tweets;

top_results = ORDER counts BY group ASC;

store top_results into 'hourly-counts-all.txt';
