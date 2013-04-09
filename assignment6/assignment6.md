Assignment 6
=========


Pig Analysis Script #1 (Hourly basis)
-------------------

    -- Use UDF
    REGISTER 'tweet_time_func.py' USING jython AS t;

    -- Load Data
    tweets = load '/user/shared/tweets2011/tweets2011.txt' as (id:int, time:chararray, username:chararray, text:chararray);

    -- Get the bucketing for each block
    tweet_buckets = FOREACH tweets GENERATE t.time_normalize_block(time) AS hour_block;

    -- Count by group
    grouped = GROUP tweet_buckets BY hour_block;
    counts =  FOREACH grouped 
          GENERATE group,
                   COUNT(tweet_buckets.hour_block) as num_tweets;

    top_results = ORDER counts BY group ASC;
    store top_results into 'hourly-counts-all.txt';

Pig Analysis Script #2 (Hourly basis on Egypt/Cairo)
-------------------

    -- Use UDF
    REGISTER 'tweet_time_func.py' USING streaming_python AS t;

    -- Load Data
tweets = load '/user/shared/tweets2011/tweets2011.txt' as (id:int, time:chararray, username:chararray, text:chararray);

    -- Filter with respect to Egypt
    tweets_with_egypt = FILTER tweets BY text matches '.*([Ee][Gg][Yy][Pp][Tt]|[Cc][Aa][Ii][Rr][Oo]).*';

    -- Normalize and setup for bucketing for each block
tweet_buckets = FOREACH tweets_with_egypt GENERATE t.time_normalize_block(time) AS hour_block;

    -- Count by group
    grouped = GROUP tweet_buckets BY hour_block;
    counts =  FOREACH grouped 
          GENERATE group,
                   COUNT(tweet_buckets.hour_block) as num_tweets;

    top_results = ORDER counts BY group ASC;
    store top_results into 'hourly-counts-egypt.txt';

    
UDF tweet_time_func.py
---------------------
	@outputSchema('hour_block:chararray')
	def time_normalize_block(tweet_time):
    		"""
	    	Return the parsed time
    		"""
    		tweet_time = str(tweet_time).split(' ')        
		if(len(tweet_time)==6):
			print(len(tweet_time))       
		return 												tweet_time[5] + "" + tweet_time[1] + "" + 					tweet_time[2] + "_"+tweet_time[3][0:2]
		else:
	        return
    
