package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoints;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;


public class KMeansMRCombiner extends Reducer<IntWritable, DataPoints, IntWritable, DataPoints> {
    public void reduce(IntWritable centroid, Iterable<DataPoints> dataPoints, Context context)
            throws InterruptedException {
        //TODO  calculate the total sum of the data points here
    }
}