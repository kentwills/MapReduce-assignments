package test;

//import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import org.apache.hadoop.io.*;
import main.BuildInvertedIndex;

//import org.apache.log4j.Logger;
import org.junit.Test;

import edu.umd.cloud9.io.pair.PairOfInts;


public class BuildInvertedIndexTest {
	//private static final Logger LOG = Logger.getLogger(BuildInvertedIndexTest.class);
	@Test
	public void test() throws IOException, InterruptedException{
		//Create Mapper
		BuildInvertedIndex.MyMapper mapper = new BuildInvertedIndex.MyMapper();
		//Input
		Text value = new Text("test");
		//Create context
		BuildInvertedIndex.MyMapper.Context context =	mock(BuildInvertedIndex.MyMapper.Context.class);
		mapper.map(null, value, context);
		//Check output
		verify(context).write(new Text("test"), new PairOfInts(1,1));		
	}

}
