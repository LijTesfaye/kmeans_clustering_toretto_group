package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoints;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class KMeansMRReducer extends Reducer<IntWritable, DataPoints, Text, Text> {
    public void reduce(IntWritable centroid, Iterable<DataPoints> partialSums, Context context)
            throws IOException, InterruptedException {
        //TODO Job Configuration
    }
}
