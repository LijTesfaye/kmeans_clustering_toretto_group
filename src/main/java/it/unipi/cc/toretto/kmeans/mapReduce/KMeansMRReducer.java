package it.unipi.cc.toretto.kmeans.mapReduce;
import it.unipi.cc.toretto.kmeans.DTO.DataPoint;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.Iterator;

public class KMeansMRReducer extends Reducer<IntWritable, DataPoint, IntWritable, Text> {
    @Override
    protected void reduce(IntWritable centroidId, Iterable<DataPoint> partialSums, Context context) throws IOException, InterruptedException {
        final Iterator<DataPoint> iterator = partialSums.iterator();
        DataPoint nextCentroidPoint = iterator.next();
        // Iterate over the partial sums and add every coordinate of the points
        while (iterator.hasNext()) {
            nextCentroidPoint.sum(iterator.next());
        }
        // Calculate the average of the coordinates to obtain the new centroid point
        nextCentroidPoint.average();
        // Emit the centroid ID and the string representation of the new centroid point
        context.write(centroidId, new Text(nextCentroidPoint.toString()));
    }
}
