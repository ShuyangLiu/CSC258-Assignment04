import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class Histogram extends Configured implements Tool 
{

    

	public int run(String[] args) throws Exception 
	{
		String uri = "hdfs:///users/sliu44/output/part-r-00000"; 

        int max = 0; 
        int min = 0; 
        int width = 8;
		
		if (args.length == 3) {
			max = Integer.parseInt(args[0]);
            min = Integer.parseInt(args[1]);
            width = Integer.parseInt(args[2]);
		} else {
            return 1;
        }

        width = 3;

		Configuration conf = this.getConf();
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		InputStream in = null;

		try {
			in = fs.open(new Path(uri));
            //BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            BufferedReader reader_2 = new BufferedReader(new InputStreamReader(in));
            String line_2;
            while((line_2 = reader_2.readLine()) != null){
                String [] l = line_2.split("\\s+");
                int n = Integer.parseInt(l[1]);
                if(max < n){
                    max = n;
                }
            }

            //System.out.println("max: "+max);

            String line;
            in = null;
            in = fs.open(new Path(uri));
            BufferedReader reader = new BufferedReader(new  InputStreamReader(in));
            while((line = reader.readLine()) != null) {
                //System.out.println(line);
                String new_line = "";
                String[] ary = line.split("\\s+");
                int word_length = Integer.parseInt(ary[0]);
                int num = Integer.parseInt(ary[1]);

                new_line += ary[0];

                if(word_length>=20){
                    new_line += "+";
                    int sum = num;
                    while((line = reader.readLine()) != null) {
                        String[] y = line.split("\\s+");
                        sum += Integer.parseInt(y[1]);
                    }
                    num = sum;
                }

                int star_num = (int) ((num*1.0) / (max*1.0) * 50.0);

                //System.out.println("star_num = "+star_num);

                new_line += ": ";

                for(int i=0; i<star_num; i++){
                    new_line += "*";
                }

                for(int i=0; i<(55-star_num); i++){
                    new_line += " ";
                }
                new_line += ary[1];

                System.out.println(new_line);
            }
		} finally {
		}
		return 0;
	}
}
