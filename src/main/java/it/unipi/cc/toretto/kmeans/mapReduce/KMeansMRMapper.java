package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoint;
import it.unipi.cc.toretto.kmeans.KMCentroid.KMeansCentroid;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.ArrayList;

import static it.unipi.cc.toretto.kmeans.KMeansHelper.readCentroidsConf;
/**
 * KMeansMRMapper: a mapper class for the kmeans in the hadoop framework
 * it maps every datapoint to its nearest centroid.
 */
public class KMeansMRMapper extends Mapper<Object, Text, IntWritable, DataPoint> {
    // Array list of centroids
    private static ArrayList<KMeansCentroid> centroids;
    /**
     * Reads the centroids from the configuration during the setup phase.
     * @param context The context object for accessing the configuration.
     * @throws IOException          Thrown if there is I/O error.
     * @throws InterruptedException if the thread is interrupted during the execution of the method.
     */
    public void setup(Context context) throws IOException, InterruptedException {
        // Call the superclass setup method
        super.setup(context);
        // Load the centroids from the Hadoop configuration and store them in the class variable
        centroids = readCentroidsConf(context.getConfiguration());
    }
    /**
     * Maps/Associates every data point to its nearest centroid.
     *
     * @param key     The input key.
     * @param value   Represents the coordinates of a data point.
     * @param context The context object for writing the centroid-point association.
     * @throws InterruptedException Thrown when a thread is interrupted while waiting/sleeping/otherwise occupied.
     * @throws IOException          Thrown when an I/O error occurs.
     */
    protected void map(final Object key, final Text value, final Context context) throws InterruptedException, IOException {
        // Create a Point object from the input text
        DataPoint point = new DataPoint(value.toString());
        // Initialize variables to store the ID of the nearest centroid and the distance to it
        IntWritable centroidID = null;
        double pointDistanceFromCentroid = Double.MAX_VALUE;
        // Iterate over all centroids to find the nearest centroid
        for (KMeansCentroid centroid : centroids) {
            // Calculate the distance between the current centroid and the point
            double distance = centroid.getPoint().getDistance(point);
            // If this is the first centroid OR is closer than the previous nearest centroid,
            // update the nearest centroid ID and distance
            if (centroidID == null || distance < pointDistanceFromCentroid) {
                centroidID = centroid.getCentroidID();
                pointDistanceFromCentroid = distance;
            }
        }
        // Emit the centroid ID of the nearest centroid and the point
        context.write(centroidID, point);
    }
}
