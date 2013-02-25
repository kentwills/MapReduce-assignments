package test;

//import static org.junit.Assert.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mrunit.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;

import main.BuildInvertedIndex.*;

//import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.Before;

import edu.umd.cloud9.io.array.ArrayListWritable;
import edu.umd.cloud9.io.pair.PairOfInts;
import edu.umd.cloud9.io.pair.PairOfWritables;



public class BuildInvertedIndexTest {
	//private static final Logger LOG = Logger.getLogger(BuildInvertedIndexTest.class);
	  public  MapDriver<LongWritable, Text, Text, PairOfInts> mapDriver;
	  public  ReduceDriver<Text, PairOfInts, Text, PairOfWritables<IntWritable, ArrayListWritable<PairOfInts>>> reduceDriver;
	  public  MapReduceDriver<LongWritable, Text, 
	  									Text, PairOfInts, 
	  									Text,PairOfWritables<IntWritable, ArrayListWritable<PairOfInts>>> mapReduceDriver;
	
	@Before
	public void setUp(){
		//Create Mapper and Reducer
		MyMapper mapper = new MyMapper();
		MyReducer reducer = new MyReducer();
		
	    mapDriver = MapDriver.newMapDriver(mapper);
	    reduceDriver = ReduceDriver.newReduceDriver(reducer);
	    //mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}
	
	  @Test
	  public void testMapper() {
		  //Setup Input
		  mapDriver.withInput(new LongWritable(1), new Text("this"));
		  
		  //Declare Output
		  mapDriver.withOutput(new Text("this"),new PairOfInts(1,1));
		  
		  //Run the test.
		  mapDriver.runTest();
	  }
	
	  @Test
	  public void testReducer(){			
		  
		  //Setup input
		  List<PairOfInts>index =new ArrayList<PairOfInts>();
		  index.add(new PairOfInts(1,1));
		  reduceDriver.withInput(new Text("this"), index);
		  
		  //Construct Proper Ouput
		  ArrayListWritable<PairOfInts>a =new ArrayListWritable<PairOfInts>();
		  a.add( new PairOfInts(1,1));
		  Text t = new Text("this");
			PairOfWritables<IntWritable, ArrayListWritable<PairOfInts>> PW
			=new PairOfWritables<IntWritable, ArrayListWritable<PairOfInts>>(
					new IntWritable(2),
					a
					);
		  
		  //Declare Proper Output
		  reduceDriver.withOutput(t,PW);
		  
		  //Run the test.
		  reduceDriver.runTest();
	  }


}
