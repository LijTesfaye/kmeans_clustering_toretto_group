package it.unipi.cc.toretto.kmeans;
import it.unipi.cc.toretto.kmeans.DTO.DataPoints;
import it.unipi.cc.toretto.kmeans.mapReduce.KMeansMRCombiner;
import it.unipi.cc.toretto.kmeans.mapReduce.KMeansMRMapper;
import it.unipi.cc.toretto.kmeans.mapReduce.KMeansMRReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class KMeansMRMain {
    public static void main(String[] args) throws Exception {
        long start;
        long end ;
        start = System.currentTimeMillis();
        Configuration conf = new Configuration();
        // Config XML file location.
        //home/tess/IdeaProjects/kmeans_clustering_hadoop/src/main/resources/config.xml
        conf.addResource(new Path("/home/tess/IdeaProjects/kmeans_clustering_hadoop/src/main/resources/config.xml"));
        //non-hadoop commandline args
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println(" For the configuration to work it needs two arguments <Input><Output>");
            System.exit(1);
        }
        //Parameters setting
        final String INPUT = otherArgs[0];
        final String OUTPUT = otherArgs[1] + "/temp";
        final int DATASET_SIZE = conf.getInt("dataset", 10);
        final int h = conf.getInt("distance", 2);
        final int K = conf.getInt("k", 4);
        final float THRESHOLD = conf.getFloat("threshold", 0.0001f);
        final int MAX_ITERATIONS = conf.getInt("max.iteration", 100);
        //
        DataPoints[] oldCentroids = new DataPoints[K];
        // Read initial centroids from the hdfs file.
        List<DataPoints> newCentroids = new ArrayList<>();
        try {
            Path filePath = new Path("/user/input/ic4D4K150N.txt");
            FileSystem fs = filePath.getFileSystem(conf);
            try (FSDataInputStream in = fs.open(filePath);
                 BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] centroidCoords = line.split(",");
                    float[] coords = new float[centroidCoords.length];
                    for (int i = 0; i < centroidCoords.length; i++) {
                        coords[i] = Float.parseFloat(centroidCoords[i]);
                    }
                    DataPoints centroid = new DataPoints(coords);
                    newCentroids.add(centroid);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Write the initial centroids to the configuration.
        for (int i = 0; i < newCentroids.size(); i++) {
            conf.set("centroid." + i, newCentroids.get(i).toString());
        }
        //MapReduce workflow
        boolean stop = false;
        boolean succeded = true;
        int i = 0;
        while(!stop) {
            i++;
            //Job configuration
            Job job = Job.getInstance(conf, "iter_" + i);
            job.setJarByClass(KMeansMRMain.class);
            job.setMapperClass(KMeansMRMapper.class);
            job.setCombinerClass(KMeansMRCombiner.class);
            job.setReducerClass(KMeansMRReducer.class);
            job.setNumReduceTasks(K); //Taking each centroid task is done by a single reducer.
            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(DataPoints.class);
            //
            FileInputFormat.addInputPath(job, new Path(INPUT));
            FileOutputFormat.setOutputPath(job, new Path(OUTPUT));
            //
            job.setInputFormatClass(TextInputFormat.class);
            job.setOutputFormatClass(TextOutputFormat.class);
            //
            succeded = job.waitForCompletion(true);
            //If the job fails the application will be closed.
            if(!succeded) {
                System.err.println("Iteration" + i + "failed.");
                System.exit(1);
            }
            //Save old centroids and read new centroids
            for(int id = 0; id < K; id++) {
                //oldCentroids.set(id, DataPoints.copy(newCentroids.get(id)));
                oldCentroids[id] = DataPoints.copy(newCentroids.get(id));
            }
            //
            newCentroids = new ArrayList<>(Arrays.asList(readCentroids(conf, K, OUTPUT)));
            stop = hasConverged(Arrays.asList(oldCentroids), newCentroids, h, THRESHOLD);
            //If the centroids change then write them to the output folder.
            if(stop || i == (MAX_ITERATIONS -1)) {
                writeFinalCentroids(conf, newCentroids, otherArgs[1]);
            } else {
                //Otherwise: Set the new centroids in the configuration
                for(int d = 0; d < K; d++) {
                    conf.unset("centroid." + d);
                    conf.set("centroid." + d, newCentroids.get(d).toString());
                }
            }
        }
        end = System.currentTimeMillis();
        end -= start;
        System.out.println("execution time: " + end + " ms");
        System.out.println("n_iter: " + i);
        System.exit(0);
    }

    private static boolean hasConverged(List<DataPoints> oldCentroids, List<DataPoints> newCentroids, int h, float threshold) {
        boolean convergencyCondition;
        for(int i = 0; i < oldCentroids.size(); i++) {
            convergencyCondition = oldCentroids.get(i).distanceCalculator(newCentroids.get(i), h) <= threshold;
            if(!convergencyCondition) {
                return false;
            }
        }
        return true;
    }
    private static void writeFinalCentroids(Configuration conf, List<DataPoints> centroids, String output) throws IOException {
        FileSystem hdfsFileSystem = FileSystem.get(conf);
        FSDataOutputStream outputStream = hdfsFileSystem.create(new Path(output + "/fc4D4K150N.txt"), true);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        //Write the result in a unique file
        for(int i = 0; i < centroids.size(); i++) {
            bufferedWriter.write(centroids.get(i).toString());
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        hdfsFileSystem.close();
    }
    private static DataPoints[] readCentroids(Configuration conf, int k, String pathString)
            throws IOException, FileNotFoundException {
        DataPoints[] points = new DataPoints[k];
        FileSystem hdfsFileSystem = FileSystem.get(conf);
        FileStatus[] status = hdfsFileSystem.listStatus(new Path(pathString));

        for (int i = 0; i < status.length; i++) {
            //get the centroids from the hadoop file system
            if(!status[i].getPath().toString().endsWith("_SUCCESS")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(hdfsFileSystem.open(status[i].getPath())));
                String[] keyValueSplit = br.readLine().split("\t"); //Split line in K,V
                int centroidId = Integer.parseInt(keyValueSplit[0]);
                String[] point = keyValueSplit[1].split(",");
                points[centroidId] = new DataPoints(point);
                br.close();
            }
        }
        //Delete temp directory
        hdfsFileSystem.delete(new Path(pathString), true);
        return points;
    }
}
