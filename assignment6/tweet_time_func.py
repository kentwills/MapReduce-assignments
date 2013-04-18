@outputSchema('hour_block:chararray')
def time_normalize_block(tweet_time):
    """
    Return the parsed time
    """
    tweet_time = str(tweet_time).split(' ')        
    if(len(tweet_time)==6):            
        return tweet_time[5] + "" + tweet_time[1] + "" + tweet_time[2] + "_"+tweet_time[3][0:2]
    else:
        return