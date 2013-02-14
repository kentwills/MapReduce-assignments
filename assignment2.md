<p>Answer the following questions:</p>

<p><b>Question 0.</b> <i>Briefly</i> describe in prose your solution,
both the pairs and stripes implementation. For example: how many
MapReduce jobs? What are the input records? What are the intermediate
key-value pairs? What are the final output records? A paragraph for
each implementation is about the expected length.</p>

<p><b>Question 1.</b> What is the running time of the complete pairs
implementation (in your VM)? What is the running time of the complete
stripes implementation (in your VM)?</p>

Pairs:
Stripes: 61.184 seconds

<p><b>Question 2.</b> Now disable all combiners. What is the running
time of the complete pairs implementation now? What is the running
time of the complete stripes implementation?</p>

Pairs:
Stripes:

<p><b>Question 3.</b> How many distinct PMI pairs did you extract?</p>

1439667

<p><b>Question 4.</b> What's the pair (x, y) with the highest PMI?
Write a sentence or two to explain what it is and why it has such a
high PMI.</p>

<p><b>Question 5.</b> What are the three words that have the highest
PMI with "cloud" and "love"? And what are the PMI values?</p>

<p>Note that you can compute the answer to questions 3&mdash;6 however
you wish: a helper Java program, a Python script, command-line
manipulation, etc.</p>