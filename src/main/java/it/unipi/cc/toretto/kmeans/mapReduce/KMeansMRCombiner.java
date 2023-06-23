package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoints;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;


public class KMeansMRCombiner extends Reducer<IntWritable, DataPoints, IntWritable, DataPoints> {
    public void reduce(IntWritable centroid, Iterable<DataPoints> points, Context context)
            throws IOException, InterruptedException {

        //Sum the points
        DataPoints sum = DataPoints.copy(points.iterator().next());
        while (points.iterator().hasNext()) {
            sum.total_sum(points.iterator().next());
        }
        context.write(centroid, sum);
    }
}