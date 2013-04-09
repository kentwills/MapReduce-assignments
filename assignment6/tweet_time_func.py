@outputSchema('hour_block:chararray')
def time_normalize_block(tweet_time):
    """
    Return the local time of a tweet from the utc time of the tweet
    and the utc offset of the user.
    """
    tweet_time = tweet_time.split(' ')        
    return tweet_time[5]+""+tweet_time[1]+""+tweet_time[2]+"_"+tweet_time[3][0:2]
    