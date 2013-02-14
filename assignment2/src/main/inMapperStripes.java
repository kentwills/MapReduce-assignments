/*
 * Cloud9: A Hadoop toolkit for working with big data
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

//package edu.umd.cloud9.example.cooccur;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import cern.colt.Arrays;

import edu.umd.cloud9.io.map.HMapSIW;

/**
 * <p>
 * Implementation of the "pairs" algorithm for computing co-occurrence matrices
 * from a large text collection. This algorithm is described in Chapter 3 of
 * "Data-Intensive Text Processing with MapReduce" by Lin &amp; Dyer, as well as
 * the following paper:
 * </p>
 * 
 * <blockquote>Jimmy Lin. <b>Scalable Language Processing Algorithms for the
 * Masses: A Case Study in Computing Word Co-occurrence Matrices with
 * MapReduce.</b> <i>Proceedings of the 2008 Conference on Empirical Methods in
 * Natural Language Processing (EMNLP 2008)</i>, pages 419-428.</blockquote>
 * 
 * @author Jimmy Lin
 */
public class inMapperStripes extends Configured implements Tool {
	private static final Logger LOG = Logger.getLogger(StripesPMI.class);

	private static class MyMapper extends
			Mapper<LongWritable, Text, Text, HMapSIW> {

		private static final int FLUSH_SIZE = 1000;
		private static final Text KEY = new Text();
		private static HMapSIW map;
		private int window = 2;

		@Override
		public void setup(Context context) {
			window = context.getConfiguration().getInt("window", 2);
		}

		public void map(LongWritable key, Text line, Context context)
				throws IOException, InterruptedException {
			HMapSIW map = getMap();
			
			String text = line.toString();

			// Tokenize terms in document
			String[] terms = text.split("\\s+");

			// Iterate through each term in the document
			for (int i = 0; i < terms.length; i++) {
				String term = terms[i];

				// skip empty tokens
				if (term.length() == 0)
					continue;				
				// For each term in neighbors(w)
				for (int j = i - window; j < i + window + 1; j++) {
					if (j == i || j < 0)
						continue;

					if (j >= terms.length)
						break;

					// skip empty tokens
					if (terms[j].length() == 0)
						continue;

					// Check to see if we already hashed and add to the hashed
					// value
					if (map.containsKey(terms[j])) {
						map.increment(terms[j]);
					}
					// Create new hash
					else {
						map.put(terms[j], 1);
					}

					// do this for (term,*) calculation
					if (map.containsKey("*")) {
						map.increment("*");
					}
					// Create new hash
					else {
						map.put("*", 1);
					}
				}
			}
			flush(context, false);
		}
		
		private void doEmit(Context context)
				throws IOException, InterruptedException {
			
			edu.umd.cloud9.util.map.MapKI.Entry<String>[] data = map
					.getEntriesSortedByKey();

				for (int i = 0; i < data.length; i++) {
					KEY.set(data[i].getKey());
					context.write(KEY, map);
				}
			
		}

		private void flush(Context context, boolean force) throws IOException,
				InterruptedException {
			HMapSIW map = getMap();
			if (!force) {
				int size = map.size();
				if (size < FLUSH_SIZE)
					return;
			}

			doEmit(context);

			map.clear(); // make sure to empty map
		}

		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			flush(context, true); // force flush no matter what at the end
		}

		 public HMapSIW getMap() {
			  if(null == map) //lazy loading
			   map = new HMapSIW();
			  return map;
			 }
	}

	private static class MyReducer extends
			Reducer<Text, HMapSIW, Text, FloatWritable> {
		private final static FloatWritable PMI = new FloatWritable();
		private final static Text BIGRAM = new Text();

		@Override
		public void reduce(Text key, Iterable<HMapSIW> values, Context context)
				throws IOException, InterruptedException {

			Iterator<HMapSIW> iter = values.iterator();
			HMapSIW map = new HMapSIW();
			float frequency = 0;
			float sum = 0;
			float totalWords = 156215;
			String prev = key.toString(), cur = "";

			while (iter.hasNext()) {
				map.plus(iter.next());
			}

			if (map.size() != 0) {
				edu.umd.cloud9.util.map.MapKI.Entry<String>[] data = map
						.getEntriesSortedByKey();

				if (map.get("*") > 9) {
					for (int i = 0; i < data.length; i++) {
						sum = map.get("*");
						cur = data[i].getKey();
						frequency = (float) map.get(cur) / sum;

						BIGRAM.set(prev + "," + cur);
						PMI.set((float) Math
								.log(frequency / (sum / totalWords)));

						context.write(BIGRAM, PMI);
					}
				}
			}

		}
	}

	/**
	 * Creates an instance of this tool.
	 */
	public inMapperStripes() {
	}

	private static final String INPUT = "input";
	private static final String OUTPUT = "output";
	private static final String WINDOW = "window";
	private static final String NUM_REDUCERS = "numReducers";

	/**
	 * Runs this tool.
	 */
	@SuppressWarnings({ "static-access" })
	public int run(String[] args) throws Exception {
		Options options = new Options();

		options.addOption(OptionBuilder.withArgName("path").hasArg()
				.withDescription("input path").create(INPUT));
		options.addOption(OptionBuilder.withArgName("path").hasArg()
				.withDescription("output path").create(OUTPUT));
		options.addOption(OptionBuilder.withArgName("num").hasArg()
				.withDescription("window size").create(WINDOW));
		options.addOption(OptionBuilder.withArgName("num").hasArg()
				.withDescription("number of reducers").create(NUM_REDUCERS));

		CommandLine cmdline;
		CommandLineParser parser = new GnuParser();

		try {
			cmdline = parser.parse(options, args);
		} catch (ParseException exp) {
			System.err.println("Error parsing command line: "
					+ exp.getMessage());
			return -1;
		}

		if (!cmdline.hasOption(INPUT) || !cmdline.hasOption(OUTPUT)) {
			System.out.println("args: " + Arrays.toString(args));
			HelpFormatter formatter = new HelpFormatter();
			formatter.setWidth(120);
			formatter.printHelp(this.getClass().getName(), options);
			ToolRunner.printGenericCommandUsage(System.out);
			return -1;
		}

		String inputPath = cmdline.getOptionValue(INPUT);
		String outputPath = cmdline.getOptionValue(OUTPUT);
		int reduceTasks = cmdline.hasOption(NUM_REDUCERS) ? Integer
				.parseInt(cmdline.getOptionValue(NUM_REDUCERS)) : 1;
		int window = cmdline.hasOption(WINDOW) ? Integer.parseInt(cmdline
				.getOptionValue(WINDOW)) : 2;

		LOG.info("Tool: " + inMapperStripes.class.getSimpleName());
		LOG.info(" - input path: " + inputPath);
		LOG.info(" - output path: " + outputPath);
		LOG.info(" - window: " + window);
		LOG.info(" - number of reducers: " + reduceTasks);

		Job job = Job.getInstance(getConf());
		job.setJobName(inMapperStripes.class.getSimpleName());
		job.setJarByClass(inMapperStripes.class);

		// Delete the output directory if it exists already.
		Path outputDir = new Path(outputPath);
		FileSystem.get(getConf()).delete(outputDir, true);

		job.getConfiguration().setInt("window", window);

		job.setNumReduceTasks(reduceTasks);

		FileInputFormat.setInputPaths(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(HMapSIW.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FloatWritable.class);

		job.setMapperClass(MyMapper.class);		
		job.setReducerClass(MyReducer.class);

		long startTime = System.currentTimeMillis();
		job.waitForCompletion(true);
		System.out.println("Job Finished in "
				+ (System.currentTimeMillis() - startTime) / 1000.0
				+ " seconds");

		return 0;
	}

	/**
	 * Dispatches command-line arguments to the tool via the {@code ToolRunner}.
	 */
	public static void main(String[] args) throws Exception {
		ToolRunner.run(new inMapperStripes(), args);
	}
}