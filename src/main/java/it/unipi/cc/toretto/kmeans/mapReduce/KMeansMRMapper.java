package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoint;
import it.unipi.cc.toretto.kmeans.KMCentroid.KMeansCentroid;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.ArrayList;

import static it.unipi.cc.toretto.kmeans.KMeansHelper.readCentroidsConf;

public class KMeansMRMapper extends Mapper<Object, Text, IntWritable, DataPoint> {

    private static ArrayList<KMeansCentroid> centroids;

    public void setup(Context context) throws IOException, InterruptedException {
        // Call the superclass setup method
        super.setup(context);
        // Load the centroids from the Hadoop configuration and store them in the class variable
        centroids = readCentroidsConf(context.getConfiguration());
    }

    protected void map(final Object key, final Text value, final Context context) throws InterruptedException, IOException {
        // Create a Point object from the input text
        DataPoint point = new DataPoint(value.toString());
        // Initialize variables to store the ID of the nearest centroid and the distance to it
        IntWritable centroid_id = null;
        double pointDistanceFromCentroid = Double.MAX_VALUE;

        // Iterate over all centroids to find the nearest centroid
        for (KMeansCentroid centroid : centroids) {
            // Calculate the distance between the current centroid and the point
            double distance = centroid.getPoint().getDistance(point);
            // If this is the first centroid or if it is closer than the previous nearest centroid,
            // update the nearest centroid ID and distance
            if (centroid_id == null || distance < pointDistanceFromCentroid) {
                centroid_id = centroid.getCentroidID();
                pointDistanceFromCentroid = distance;
            }
        }
        // Emit the ID of the nearest centroid and the point
        context.write(centroid_id, point);
    }
}