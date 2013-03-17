I was not able to finish the project.  The grading says that it will give points to a single source and multiple source implementation, but there is only one naming that will be executed.  Because of this I did not separate my files into a single and multiple source class.  I have broken out the important details so that you can see what was implemented per phase.

Single source
====

For my understanding of this part, there were only two real sections that we had to change, other than adding the source input for all files.(Tested and correct, prints out proper numbers.)

        options.addOption(OptionBuilder.withArgName("path").hasArg()        
        .withDescription("sources").create(SOURCES));

BuildPersonalizedPageRankRecords.java
-------

<p>Properly sets probability to zero for source and Float.NegInf for rest, this was verified through a print statement. (line110)(This is code for multi, but was tested in without the i, in single as well)</p>

        for (int i = 0; i < sources.length; i++) {
    			if (Integer.toString(node.getNodeId()).equals(sources[i])) {					
					node.setPageRank((float) 0, i);// Log(1)
					LOG.info(nid + " " + node.getPageRank(i)+" "+i);
				} else
					node.setPageRank((float) Float.NEGATIVE_INFINITY, i);// (Log(0)   

RunPersonalizedPageRankBasic.java
-----
<p>Set new prob in mapper of the Mass distribution class, where I eliminated the divide by node count.  The key to the code below is that we are not distributing the mass anymore so we don't divide by the number of nodes anymore.  Also, we need to make sure that the mass goes back to only the source, so only the source gets updated with the if statement.(This is also a multi source implementation but the single source would be pretty easy to see.)</p> 
        
    for (int i = 0; i < sources.length; i++) {				 
		if (Integer.toString(nid.get()).equals(sources[i])) {
			float jump = (float) (Math.log(ALPHA));					
			float link = (float) Math.log(1.0f - ALPHA)+ sumLogProbs(p[i], (float) Math.log(missingMass[i]));						
			node.setPageRank(sumLogProbs(jump, link), i);			
		}
    }

Multiple Source
====

<p>I used the InMapperCombiner for the implementation.  Because of this I wanted to use HMAP, so I switched it to HMAPIVW so I could have a int,ArrayOfFloatsW.  ArrayOfFloatsW is a class that I implemented because I needed a comparablewritable to be used in HMAP and I could write an entire array.  With this method I was able to create two new sumLogProbs to reduce the for loops in the code and make the code more readable.  Below are a list of tasks completed:</p>
<ol>
<li>Executed above for multi-source (I hope that was apparent)</li>
<li>Changed Mapper and Combiner to support multiple sources</li>
    <ol>
    <li>New map created with support for multiple floats.   Custom class built</li>
        <code>private static final HMapIVW<ArrayOfFloatsW> map = new HMapIVW<ArrayOfFloatsW>();
        </code>
    <li>In cleanup, entry set changed to handle new map.</li>    
        <code>MapIV.Entry<ArrayOfFloatsW> e : map.entrySet()</code>
    <li>New method sumLogProbs to handle arrays so no for loop needed in main of code.</li>
        <code>
        map.put(neighbor,sumLogProbs(map.get(neighbor), data));
        </code>
    </ol>
<li>Changed PageRankNode to support multiple sources</li>
<li>Changed Extract to support multiple sources, and then retracted so that it will only print the first source due to time limitations.</li>
</ol>
<p>


<p>
Issues
-----
<li>My probabilities do not add up correctly</li>
<li>I lose structure nodes somehow.</li>

</p>

<li>Final Notes:  I feel that I have completed much of this assignment, with little to show for it.  I feel that if I tweeked a few things here and there I would have a fully working implementation.  The problem is that I am now officially stuck.  I have done what I thought would be sufficient for tweaking, but to no avail.  I am rather embarrassed to be turning in this quality of an assignment.</li>
</p>

Grading
=======

I looked at your code and it's definitely on the right track... Let me
know if you'd like to go over anything once I distribute the solutions.

+8 point for the single source implementation, +10 points for
the multi-source implementation, but -25% for late penalty.

Score: 14/35

-Jimmy
