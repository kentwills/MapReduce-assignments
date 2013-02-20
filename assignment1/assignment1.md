<p>Answer the following questions:</p>

<p><b>Question 1.</b> What is the first term
in <code>part-r-00000</code> and how many times does it appear?</p>

but, 1 time

<p><b>Question 2.</b> What is the third to last term
in <code>part-r-00004</code> and how many times does it appear?</p>

zorah, 8

<p><b>Question 3.</b> How many unique terms are there? (Hint: read the
counter values)</p>

41,788, wc -l used to get the # of lines in all the output files.

<p>Let's do a little bit of cleanup of the words. Modify the word
count demo so that only words consisting entirely of letters are
counted. To be more specific, the word must match the following Java
regular expression:</p>

<pre>
word.matches("[A-Za-z]+")
</pre>

<p>Now run word count again, also with five reducers. Answer the
following questions:</p>

<p><b>Question 4.</b> What is the first term
in <code>part-r-00000</code> and how many times does it appear?</p>

aaron, 416

<p><b>Question 5.</b> What is the third to last term
in <code>part-r-00004</code> and how many times does it appear?</p>

zorah, 8

<p><b>Question 6.</b> How many unique terms are there?

31,940

Grading
=======

Your answers are correct. However, the run script was not named correctly and the assignment answers were not in the
expected location. Please be careful next time.

-Jimmy
