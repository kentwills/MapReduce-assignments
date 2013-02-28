

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
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import edu.umd.cloud9.io.pair.PairOfStringInt;
import edu.umd.cloud9.io.pair.PairOfStrings;
import edu.umd.cloud9.util.fd.Object2IntFrequencyDistribution;
import edu.umd.cloud9.util.fd.Object2IntFrequencyDistributionEntry;
import edu.umd.cloud9.util.pair.PairOfObjectInt;

public class BuildInvertedIndexCompressed extends Configured implements Tool {
	private static final Logger LOG = Logger
			.getLogger(BuildInvertedIndexCompressed.class);

	public static class MyMapper extends
			Mapper<LongWritable, Text, PairOfStringInt, IntWritable> {
		private static final PairOfStringInt WORD_DOCN = new PairOfStringInt();
		private static final IntWritable F = new IntWritable();
		private static final Object2IntFrequencyDistribution<String> COUNTS = new Object2IntFrequencyDistributionEntry<String>();

		@Override
		public void map(LongWritable docno, Text doc, Context context)
				throws IOException, InterruptedException {

			// Get text in document
			String text = doc.toString();
			COUNTS.clear();

			String[] terms = text.split("\\s+");

			// First build a histogram of the terms.
			for (String term : terms) {
				if (term == null || term.length() == 0) {
					continue;
				}
				COUNTS.increment(term);
			}

			// Emit postings.
			for (PairOfObjectInt<String> e : COUNTS) {
				// set word,doc number pair
				WORD_DOCN.set(e.getLeftElement(), (int) docno.get());
				F.set(e.getRightElement());
				context.write(WORD_DOCN, F);
			}
		}
	}

	protected static class MyPartitioner extends
			Partitioner<PairOfStringInt, IntWritable> {
		@Override
		public int getPartition(PairOfStringInt key, IntWritable value,
				int numReduceTasks) {
			return (key.getLeftElement().hashCode() & Integer.MAX_VALUE)
					% numReduceTasks;
		}
	}

	private static class MyReducer extends
			Reducer<PairOfStringInt, IntWritable, Text, BytesWritable> {
		private static BytesWritable POSTINGS = new BytesWritable();
		public static int DOCPREV = 0;
		private static String TPREV = null;
		private final static Text TERM = new Text();
		private static ByteArrayOutputStream out = new ByteArrayOutputStream();
		private static DataOutputStream dataOut = new DataOutputStream(out);		

		@Override
		public void setup(Context context) throws IOException {
			TPREV = null;
			DOCPREV = 0;
			POSTINGS = new BytesWritable();
		}

		@Override
		public void reduce(PairOfStringInt key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			Iterator<IntWritable> iter = values.iterator();

			while (iter.hasNext()) {
				if (TPREV != null && !TPREV.equals(key.getLeftElement())) {
					POSTINGS.setSize(out.size());					
					POSTINGS.set(out.toByteArray(), 0, out.size());
					TERM.set(TPREV);
					context.write(TERM, new BytesWritable(out.toByteArray()));					
					reset();
				}

				// Listing of documents and their individual frequencies
				WritableUtils.writeVInt((DataOutput) dataOut,
						key.getRightElement() - DOCPREV);
				WritableUtils
						.writeVInt((DataOutput) dataOut, iter.next().get());
				TPREV = key.getLeftElement().toString();
				DOCPREV = (int) key.getRightElement();				
			}
		}

		@Override
		public void cleanup(Context context) throws IOException,
				InterruptedException {
			POSTINGS.setSize(out.size());
			POSTINGS.set(out.toByteArray(), 0, out.size());
			TERM.set(TPREV);
			context.write(TERM, new BytesWritable(out.toByteArray()));			
			dataOut.close();
		}

		public void reset() throws IOException {
			dataOut.close();
			POSTINGS = new BytesWritable();
			out = new ByteArrayOutputStream();
			dataOut = new DataOutputStream(out);
			DOCPREV = 0;
		}

	}

	private BuildInvertedIndexCompressed() {
	}

	private static final String INPUT = "input";
	private static final String OUTPUT = "output";
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

		LOG.info("Tool name: "
				+ BuildInvertedIndexCompressed.class.getSimpleName());
		LOG.info(" - input path: " + inputPath);
		LOG.info(" - output path: " + outputPath);
		LOG.info(" - num reducers: " + reduceTasks);

		Job job = Job.getInstance(getConf());
		job.setJobName(BuildInvertedIndexCompressed.class.getSimpleName());
		job.setJarByClass(BuildInvertedIndexCompressed.class);

		job.setNumReduceTasks(reduceTasks);

		FileInputFormat.setInputPaths(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.setMapOutputKeyClass(PairOfStringInt.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(BytesWritable.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		//job.setPartitionerClass(MyPartitioner.class);

		// Delete the output directory if it exists already.
		Path outputDir = new Path(outputPath);
		FileSystem.get(getConf()).delete(outputDir, true);

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
		ToolRunner.run(new BuildInvertedIndexCompressed(), args);
	}
}
