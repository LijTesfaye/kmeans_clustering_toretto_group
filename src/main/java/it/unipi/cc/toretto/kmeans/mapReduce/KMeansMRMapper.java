package it.unipi.cc.toretto.kmeans.mapReduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class KMeansMRMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    public void setup(Context context){
        //TODO any initialization tasks
    }
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        // TODO the mapper logic goes here
    }
    @Override
    protected void cleanup(Context context){
        //TODO any cleanup tasks
    }
}