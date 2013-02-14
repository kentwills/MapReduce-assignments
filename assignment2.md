<p>Answer the following questions:</p>

<p><b>Question 0.</b> <i>Briefly</i> describe in prose your solution,
both the pairs and stripes implementation. For example: how many
MapReduce jobs? What are the input records? What are the intermediate
key-value pairs? What are the final output records? A paragraph for
each implementation is about the expected length.</p>

Stripes: I implemented this with HMapSIW because we are able to obtain a listing of the keys in the hashmap.  The example given on Cloud9 uses a hashmap that does not allow for this.  I either increment or add a new key in the mapper when a new word for the key is added and complement it with initializing or incrementing the * entry. My combiner is a simple receive Text, HMapSIW and send Text, HMapSIW, which are in-turn the intermediate key,value pairs. Final output records are "Text Value" such as "the,apple .6", where value is the PMI.

Pairs: I implemented this with Pairs as the main key.  My intermediate values are PairOfStrings and FloatWritable where I emit the string pair and an extra string with the * token.  My combiner just compresses the pairs.  The Partitioner sorts by the first word. The final output values are "Pair Value"such as "(the,apple) .2", where value is the PMI.

<p><b>Question 1.</b> What is the running time of the complete pairs
implementation (in your VM)? What is the running time of the complete
stripes implementation (in your VM)?</p>

Pairs: 95.649 seconds
Stripes: 61.184 seconds

<p><b>Question 2.</b> Now disable all combiners. What is the running
time of the complete pairs implementation now? What is the running
time of the complete stripes implementation?</p>
<p>
Pairs: 96.501
Stripes: 61.704 seconds
</p>
<p>
It expected to see a huge performance gain in pairs because the amount of data sent across the network.  I am thinking there wasn't enough data for the combiners to get evoked?
</p>
<p><b>Question 3.</b> How many distinct PMI pairs did you extract?</p>

1439667

<p><b>Question 4.</b> What's the pair (x, y) with the highest PMI?
Write a sentence or two to explain what it is and why it has such a
high PMI.</p>

<p>
(deserves',he) "he" has a high occurence in the documents and the pair itself has a high occurence, leading to a high PMI.
</p>

<p><b>Question 5.</b> What are the three words that have the highest
PMI with "cloud" and "love"? And what are the PMI values?</p>

cloud = (the,a,and)
cloud, the = 3.9173
cloud, a =   3.5404
cloud, and = 3.0464

love = (i,of,my)
love, i =  -0.1761
love, of = -0.3329
love, my = -0.3393


<p>Note, there is no question 6</p>