<p><b>Question 1.</b> What is the size of your compressed index? (In
need this value just in case I can't get your code to compile and
run)</p>

<p>
9.67M
</p>

<p>
This is because I chose to write the document frequency as an int writable with the byte writable postings data, otherwise, my size was 6.5M
</p>

<p>
Looking back I should have just specified an ending delimiter. to save overall compression space.  However, I did meet the intention of the exercise by storing vints in a stream and implementing gap compression for docids.
</p>

Grading
=======

Everything looks fine---good job!

As a side note, though, using a Pair object to represent the postings
list makes the index larger than it needs to be: you can actually fit
the *df* in the `BytesWritable` also. (So yes, you had the right intuition on how to save more space.)

Score: 35/35

-Jimmy
