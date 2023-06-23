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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class KMeansMRMain {
    public static void main(String[] args) throws Exception {
        long start = 0;
        long end = 0;
        long startInitialCentroids = 0;
        long endInitialCentroids = 0;
        start = System.currentTimeMillis();
        //store hadoop  configuration settings
        Configuration conf = new Configuration();
        // Here is the <<config.xml>> file for the input parameters, it adds the resource to the configuration
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
        final int DISTANCE = conf.getInt("distance", 2);
        final int K = conf.getInt("k", 3);
        final float THRESHOLD = conf.getFloat("threshold", 0.0001f);
        final int MAX_ITERATIONS = conf.getInt("max.iteration", 30);
        //
        DataPoints[] oldCentroids = new DataPoints[K];
        DataPoints[] newCentroids = new DataPoints[K];
        //Initial centroids
        startInitialCentroids = System.currentTimeMillis();
        newCentroids = randomInitialCentroids(conf, INPUT, K, DATASET_SIZE);
        endInitialCentroids = System.currentTimeMillis();
        for(int i = 0; i < K; i++) {
            conf.set("centroid." + i, newCentroids[i].toString());
        }
        //MapReduce workflow
        boolean stop = false;
        boolean succeded = true;
        int i = 0;
        while(!stop) {
            i++;
            //Job configuration
            Job iteration = Job.getInstance(conf, "iter_" + i);
            //
            iteration.setJarByClass(KMeansMRMain.class);
            iteration.setMapperClass(KMeansMRMapper.class);
            iteration.setCombinerClass(KMeansMRCombiner.class);
            iteration.setReducerClass(KMeansMRReducer.class);
            iteration.setNumReduceTasks(K); //one task each centroid
            iteration.setOutputKeyClass(IntWritable.class);
            iteration.setOutputValueClass(DataPoints.class);
            //
            FileInputFormat.addInputPath(iteration, new Path(INPUT));
            FileOutputFormat.setOutputPath(iteration, new Path(OUTPUT));
            iteration.setInputFormatClass(TextInputFormat.class);
            iteration.setOutputFormatClass(TextOutputFormat.class);
            //
            succeded = iteration.waitForCompletion(true);
            //If the job fails the application will be closed.
            if(!succeded) {
                System.err.println("Iteration" + i + "failed.");
                System.exit(1);
            }
            //Save old centroids and read new centroids
            for(int id = 0; id < K; id++) {
                oldCentroids[id] = DataPoints.copy(newCentroids[id]);
            }
            newCentroids = readCentroids(conf, K, OUTPUT);
            //Let's check
            stop = hasConverged(oldCentroids, newCentroids, DISTANCE, THRESHOLD);
            if(stop || i == (MAX_ITERATIONS -1)) {
                writeCentroids(conf, newCentroids, otherArgs[1]);
            } else {
                //Set the new centroids in the configuration
                for(int d = 0; d < K; d++) {
                    conf.unset("centroid." + d);
                    conf.set("centroid." + d, newCentroids[d].toString());
                }
            }
        }
        end = System.currentTimeMillis();
        end -= start;
        endInitialCentroids -= startInitialCentroids;
        System.out.println("execution time: " + end + " ms");
        System.out.println("init centroid execution: " + endInitialCentroids + " ms");
        System.out.println("n_iter: " + i);
        System.exit(0);
    }

    private static DataPoints[] randomInitialCentroids(Configuration conf, String pathString, int k, int dataSetSize)
            throws IOException {
        System.out.println("Generating random initial centroids!");
        DataPoints[] coordinatePoints = new DataPoints[k];
        List<Integer> positions = new ArrayList<Integer>();
        Random random = new Random();
        int pos;
        while(positions.size() < k) {
            pos = random.nextInt(dataSetSize);
            if(!positions.contains(pos)) {
                positions.add(pos);
            }
        }
        Collections.sort(positions);

        //File reading utils
        Path path = new Path(pathString);
        FileSystem hdfs = FileSystem.get(conf);
        FSDataInputStream in = hdfs.open(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        //Get centroids from the file
        int row = 0;
        int i = 0;
        int position;
        while(i < positions.size()) {
            position = positions.get(i);
            String point = br.readLine();
            if(row == position) {
                coordinatePoints[i] = new DataPoints(point.split(","));
                i++;
            }
            row++;
        }
        br.close();

        return coordinatePoints;
    }
    private static boolean hasConverged(DataPoints[] oldCentroids, DataPoints[] newCentroids, int distance, float threshold) {
        boolean convergencyCondition;
        for(int i = 0; i < oldCentroids.length; i++) {
            convergencyCondition = oldCentroids[i].distanceCalculator(newCentroids[i], distance) <= threshold;
            if(!convergencyCondition) {
                return false;
            }
        }
        return true;
    }

    private static void writeCentroids(Configuration conf, DataPoints[] centroids, String output) throws IOException {
        FileSystem hdfsFileSystem = FileSystem.get(conf);
        FSDataOutputStream outputStream = hdfsFileSystem.create(new Path(output + "/MapReducecentroids.txt"), true);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        //Write the result in a unique file
        for(int i = 0; i < centroids.length; i++) {
            bufferedWriter.write(centroids[i].toString());
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
