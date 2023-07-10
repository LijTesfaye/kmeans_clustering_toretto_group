package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoints;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class KMeansMRMapper extends Mapper<LongWritable, Text, IntWritable, DataPoints> {
    private List<DataPoints> initialCentroids; // initial centroids
    private int h;
    private final DataPoints dataPoint = new DataPoints();
    private final IntWritable nearestCentroid = new IntWritable();
    public void setup(Context context) {
        int k = Integer.parseInt(context.getConfiguration().get("k"));
        this.h = Integer.parseInt(context.getConfiguration().get("distance"));
        //this.initialCentroids = List.of(new DataPoints[k]);
        this.initialCentroids = Arrays.asList(new DataPoints[k]);
        // read the initial centroids as follows.
        for(int i = 0; i < k; i++) {
            String[] configCentroid = context.getConfiguration().getStrings("centroid." + i);
            this.initialCentroids.set(i, new DataPoints(configCentroid));
        }
        System.out.println("[MAPPER] Initial centroids are :");
        for(int ic=0; ic<k; ic++){
            System.out.println(initialCentroids.get(ic));
        }
    }
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        // Construct the point
        String[] pointString = value.toString().split(",");
        dataPoint.initData(pointString);
        // Initialize variables
        float minDist = Float.POSITIVE_INFINITY;
        float distance = 0.0f;
        int nearest = -1;
        // Find the closest centroid
        for (int i = 0; i < initialCentroids.size(); i++) {
            distance = dataPoint.distanceCalculator(initialCentroids.get(i), h);
            if(distance < minDist) {
                nearest = i;
                minDist = distance;
            }
        }
        nearestCentroid.set(nearest);
        context.write(nearestCentroid, dataPoint);
    }
}