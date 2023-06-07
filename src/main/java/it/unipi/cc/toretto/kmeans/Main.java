package it.unipi.cc.toretto.kmeans;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapred.*;

import javax.lang.model.SourceVersion;
import javax.tools.Tool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

public class Main extends Configured implements Tool {
    /*
    Map class for the first job
    */
    public static class MapClass extends MapReduceBase
            implements Mapper<LongWritable, Text, IntWritable, IntWritable> {

        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<IntWritable, IntWritable> outputCollector, Reporter reporter) throws IOException {

        }
    }
    /*
    Reduce class for the first job
    */
    public static class Reduce extends MapReduceBase implements Reducer <IntWritable,IntWritable,IntWritable,IntWritable >{
        @Override
        public void reduce(IntWritable intWritable, Iterator<IntWritable> iterator, OutputCollector<IntWritable, IntWritable> outputCollector, Reporter reporter) throws IOException {

        }
    }



    @Override
    public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
        return 0;
    }

    @Override
    public Set<SourceVersion> getSourceVersions() {
        return null;
    }

    public static void main(String[] args) throws  Exception{
        System.out.println("Hello Kmeans");
    }
}

