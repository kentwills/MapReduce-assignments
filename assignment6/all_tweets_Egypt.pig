-- Use UDF
REGISTER 'tweet_time_func.py' USING jython AS t;

-- Load Data
tweets = load '/user/shared/tweets2011/tweets2011.txt' as (id, time, username, text);

-- Filter with respect to Egypt
tweets_with_egypt = FILTER tweets BY text matches '.*([Ee][Gg][Yy][Pp][Tt]|[Cc][Aa][Ii][Rr][Oo]).*';

-- Normalize and setup for bucketing for each block
tweet_buckets = FOREACH tweets_with_egypt GENERATE t.time_normalize_block(time) AS hour_block;

-- Count by group
grouped = GROUP tweet_buckets BY hour_block;
counts =  FOREACH grouped GENERATE group, COUNT(tweet_buckets.hour_block) as num_tweets;

top_results = ORDER counts BY group ASC;

store top_results into 'hourly-counts-egypt.txt';
