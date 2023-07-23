package it.unipi.cc.toretto.kmeans.mapReduce;
import it.unipi.cc.toretto.kmeans.DTO.DataPoint;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.Iterator;

/**
 * The KMeansMRReducer class calculates the new centroid points based on the partialSums of the points
 * that are associated with the
 */
public class KMeansMRReducer extends Reducer<IntWritable, DataPoint, IntWritable, Text> {
    /**
     * Reduce method of the KMeansMRReducer class.
     * Calculates the new centroid points by averaging the partialSums of the assigned points.
     * @param centroidID  is the ID of the centroid.
     * @param partialSums  holds the partial sums of the data points associated to the corresponding centroids.
     * @param context     The context object for accessing Hadoop services.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the execution is interrupted.
     */
    @Override
    protected void reduce(IntWritable centroidID, Iterable<DataPoint> partialSums, Context context) throws IOException, InterruptedException {
        // Get an iterator for the partialSums
        final Iterator<DataPoint> iterator = partialSums.iterator();
        DataPoint nextCentroidPoint = iterator.next();
        // Iterate over the partial sums and add every coordinate of the points
        while (iterator.hasNext()) {
            nextCentroidPoint.sum(iterator.next());
        }
        // Calculate the average of the coordinates to obtain the new centroid point
        nextCentroidPoint.average();
        // Emit the centroid ID and the string representation of the new centroid point
        context.write(centroidID, new Text(nextCentroidPoint.toString()));
    }
}
