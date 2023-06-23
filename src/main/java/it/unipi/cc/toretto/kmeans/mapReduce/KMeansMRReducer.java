package it.unipi.cc.toretto.kmeans.mapReduce;

import it.unipi.cc.toretto.kmeans.DTO.DataPoints;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Iterator;

public class KMeansMRReducer extends Reducer<IntWritable, DataPoints, Text, Text> {
    private final Text centroidId = new Text();
    private final Text centroidValue = new Text();

    public void reduce(IntWritable centroid, Iterable<DataPoints> partialSums, Context context)
            throws IOException, InterruptedException {

        //Sum the partial sums
        DataPoints sum = DataPoints.copy(partialSums.iterator().next());
        while (partialSums.iterator().hasNext()) {
            sum.total_sum(partialSums.iterator().next());
        }
        //Calculate the new centroid
        sum.average();
        centroidId.set(centroid.toString());
        centroidValue.set(sum.toString());
        context.write(centroidId, centroidValue);
    }
    /*
    // This the cleanup method that implements the ...
    @Override
    protected void cleanup(Context context) throws InterruptedException, IOException {
        Configuration conf = context.getConfiguration();
        Path centroidsPath = new Path(conf.get("centroidsPath"));
        FileSystem fs = FileSystem.get(conf);
        //
        TODO  ...
        }
     */

}
