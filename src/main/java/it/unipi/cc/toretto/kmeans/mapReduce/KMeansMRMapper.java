package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoints;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KMeansMRMapper extends Mapper<LongWritable, Text, IntWritable, DataPoints> {
    private DataPoints[] centroids;
    private int p;
    private final DataPoints point = new DataPoints();
    private final IntWritable centroid = new IntWritable();

    public void setup(Context context) {
        int k = Integer.parseInt(context.getConfiguration().get("k"));
        this.p = Integer.parseInt(context.getConfiguration().get("distance"));
        this.centroids = new DataPoints[k];
        for(int i = 0; i < k; i++) {
            String[] centroid = context.getConfiguration().getStrings("centroid." + i);
            this.centroids[i] = new DataPoints(centroid);
        }
    }

    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        // Contruct the point
        String[] pointString = value.toString().split(",");
        point.initData(pointString);

        // Initialize variables
        float minDist = Float.POSITIVE_INFINITY;
        float distance = 0.0f;
        int nearest = -1;

        // Find the closest centroid
        for (int i = 0; i < centroids.length; i++) {
            distance = point.distanceCalculator(centroids[i], p);
            if(distance < minDist) {
                nearest = i;
                minDist = distance;
            }
        }
        centroid.set(nearest);
        context.write(centroid, point);
    }
}