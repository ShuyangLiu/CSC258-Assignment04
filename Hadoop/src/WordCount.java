import java.io.IOException;
import java.util.StringTokenizer;
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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.io.InputStream;
import java.net.URI;


public class WordCount {

    static int max = 0;
    static int min = 0;
    static int width = 10;

    public static boolean isNumeric (String str)
    {
        return str.matches("^[0-9-]+$");
    }

    public static void setMax(int num) {
        max = num;
    }

    public static int getMax() {
        return max;
    }

    public static class TokenizerMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> 
    {
        private final static IntWritable one = new IntWritable(1);
        private IntWritable wordLength = new IntWritable();

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
             String line = value.toString();
             StringTokenizer tokenizer = new StringTokenizer(line);
              while (tokenizer.hasMoreTokens()) {
                String word=tokenizer.nextToken();
                if(!isNumeric(word)){
                    wordLength.set(word.length());
                    context.write(wordLength, one);
                }
            }
        }
    }

  public static class IntSumReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> 
  {
    private IntWritable result = new IntWritable();

    public void reduce( IntWritable key, 
                        Iterable<IntWritable> values,
                        Context context) 
    throws IOException, InterruptedException 
    {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }

      if(sum > WordCount.getMax()){
        WordCount.setMax(sum);
      }

      if(sum < min){
          min = sum;
      }

      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception 
  {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    job.waitForCompletion(true);

    String arguments[] = new String[3];
    arguments[0] = Integer.toString(max);
    arguments[1] = Integer.toString(min);
    arguments[2] = Integer.toString(width);


    int exitCode = ToolRunner.run(new Histogram(), arguments);
    System.exit(exitCode);
  }
}

