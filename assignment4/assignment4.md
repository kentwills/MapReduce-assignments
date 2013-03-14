I was not able to finish the project.  The grading says that it will give points to a single source and multiple source implementation, but there is only one naming that will be executed.  Because of this I did not separate my files into a single and multiple source class.  I have broken out the important details so that you can see what was implemented per phase.

Single source
--------------------------------

I added the read in for the source node for all files.

BuildPersonalizedPageRankRecords.java
-------

properly sets prob to zero for source and Float.NegInf for rest (line110)

RunPersonalizedPageRankBasic.java
-----
Set new prob in mapper of the Mass distribution class, where I eliminated the divide by node count

float jump = (float) (Math.log(ALPHA));
float link = (float) Math.log(1.0f - ALPHA) + sumLogProbs(p, (float) (missingMass));

Multiple Source
-----
Executed above for all sources
Changed Mapper and Combiner to support multiple sources
Changed PageRankNode to support multiple sources
Changed Extract to support multiple sources, and then retracted so that it will only print the first source due to time limitations.


Final Notes:  I feel that I have completed much of this assignment, with little to show for it.  I feel that if I tweeked a few things here and there I would have a fully working implementation.  Unfortunately I have no good full data because of a bug not giving me a print out currently.