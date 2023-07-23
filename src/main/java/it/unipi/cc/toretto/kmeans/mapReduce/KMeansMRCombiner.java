package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoint;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.Iterator;

public class KMeansMRCombiner extends Reducer<IntWritable, DataPoint, IntWritable, DataPoint> {
    /**
     *  For all the datapoints that are associated to a given centroid, this method sum the coordinates of the points,
     *  to calculate their partial sum.
     *
     * @param centroidID is the ID of the centroid to which the points are associated.
     * @param points is an Iterable of the points associated with the specified centroid.
     * @param context is the Context object  in the Hadoop framework
     * @throws IOException if an I/O error occurs during the execution of the method.
     * @throws InterruptedException if the thread is interrupted during the execution of the method.
     */
    @Override
    protected void reduce(IntWritable centroidID, Iterable<DataPoint> points, Context context) throws IOException, InterruptedException {
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
        context.write(centroidID, partialSum);
    }
}

