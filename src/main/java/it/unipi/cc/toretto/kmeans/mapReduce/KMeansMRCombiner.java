package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoint;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.Iterator;

public class KMeansMRCombiner extends Reducer<IntWritable, DataPoint, IntWritable, DataPoint> {
    @Override
    protected void reduce(IntWritable centroidId, Iterable<DataPoint> points, Context context) throws IOException, InterruptedException {
        // Create an iterator for the points
        final Iterator<DataPoint> it = points.iterator();
        // Get the first point and use it as the initial partial sum
        DataPoint partialSum = it.next();
        // Iterate over the remaining points
        while (it.hasNext()) {
            // call the method that sum the coordinates of the points
            partialSum.sum(it.next());
        }
        // Write the centroid ID and the partial sum to the context
        context.write(centroidId, partialSum);
    }
}

