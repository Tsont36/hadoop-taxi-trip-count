import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TripCount {
    public static class TripMapper
	    extends Mapper<LongWritable, Text, Text, IntWritable> {

        private final Text date = new Text();
	private static final IntWritable ONE = new IntWritable(1);

	@Override
	protected void map(LongWritable key, Text value, Context context)
		throws IOException, InterruptedException {


	    String line = value.toString();

	    // Bo qua dong tieu de cua moi file csv
	    if (line.startsWith("VendorID,")) {
		return;
	    }

	    // Lay cot dau tien: thoi gian don khach
	    String[] fields = line.split(",", -1);
            // Cot thu 2 la tpep_pickup_datetime
	    if (fields.length > 1) {
	       String pickupTime = fields[1].trim();

               if (pickupTime.length() >= 10) {
                  String date = pickupTime.substring(0, 10);

	          if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
		     context.write(new Text(date), new IntWritable(1));
                  }
	      }
	  }
       }
    }

    public static class TripReducer
	    extends Reducer<Text, IntWritable, Text, IntWritable> {

	private final IntWritable result = new IntWritable();

	@Override 
	protected void reduce(Text key, Iterable<IntWritable> values, Context context)
		throws IOException, InterruptedException {
	    int total = 0;

	    for (IntWritable value : values) {
		total += value.get();
	    }

	    result.set(total);
	    context.write(key, result);
	}
    }

    public static void main(String[] args) throws Exception {

	if (args.length != 2) {
	    System.err.println(
		"Usage: TripCount <input_path> <output_path>");
	    System.exit(2);
	}


	Configuration conf = new Configuration();
	Job job = Job.getInstance(conf, "Taxi trips per day");

	job.setJarByClass(TripCount.class);
	job.setMapperClass(TripMapper.class);
	job.setCombinerClass(TripReducer.class);
	job.setReducerClass(TripReducer.class);

	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(IntWritable.class);

	FileInputFormat.addInputPath(job, new Path(args[0]));
	FileOutputFormat.setOutputPath(job, new Path(args[1]));

	System.exit(job.waitForCompletion(true) ? 0 :1);
    }
}
