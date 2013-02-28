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