package it.unipi.cc.toretto.kmeans;

import it.unipi.cc.toretto.kmeans.DTO.DataPoint;
import it.unipi.cc.toretto.kmeans.KMCentroid.KMeansCentroid;
import it.unipi.cc.toretto.kmeans.mapReduce.KMeansMRCombiner;
import it.unipi.cc.toretto.kmeans.mapReduce.KMeansMRMapper;
import it.unipi.cc.toretto.kmeans.mapReduce.KMeansMRReducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Help to avoid code cluttering in the Main class
 * Contains important methods that read and write centroids to files.
 * Set initial centroids to Hadoop's Configuration
 *
 *
 */
public class KMeansHelper {
    private static final Log LOG = LogFactory.getLog(KMeansHelper.class);
    /**
     * Write initial centroids to the Hadoop configuration
     * @param key holds the key to the centroids
     * @param centroids arraylist of centroids
     * @param conf  holds the Hadoop configration
     */
    public static void setCentroidsToConf(String key, ArrayList<KMeansCentroid> centroids, Configuration conf) {
        // Create an array of strings to store the centroid points
        String[] value = new String[centroids.size()];
        // Iterate over the centroids Arraylist
        int i = 0;
        while (i < centroids.size()) {
            KMeansCentroid centroid = centroids.get(i);
            value[i] = centroid.getPoint().toString();
            i++;
        }
        // Set the value of the configuration property specified by `key` to the centroid strings array
        conf.setStrings(key, value);
    }

    /**
     * Read initial centroids from  the Hadoop configuration
     * @param conf The hadoop configuration
     * @param fileName THe name of the file that contains the init. centroids
     * @return return the arraylist of initial centroids
     * @throws IOException IF there is an Input Output error
     */
    public static ArrayList<KMeansCentroid> readInitalCentroidsFromHDFS(Configuration conf, String fileName) throws IOException {
        ArrayList<KMeansCentroid> initialCentroids = new ArrayList<>();
                String filePathString = "/user/tess/input/" + fileName+".txt";
                Path filePath = new Path(filePathString);
                FileSystem fs = filePath.getFileSystem(conf);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(filePath)))) {
                    String line;
                    int centroidId = 0;
                    while ((line = br.readLine()) != null) {
                        String[] SingleRecord = line.split(",");
                        ArrayList<Double> dataPoint = new ArrayList<>();
                        for (String sr : SingleRecord) {
                            if (line.isEmpty()) {
                                continue; // Skip empty lines
                            }
                            dataPoint.add(Double.parseDouble(sr));
                        }
                        initialCentroids.add(new KMeansCentroid(centroidId, dataPoint));
                        centroidId++;
                    }
            return initialCentroids;
        }
    }

    /**
     * Reads the centroids from the Configuration
     * @param conf Hadoop's configration that holds settings in the (Key,value) form
     * @return returns arraylist of  KMeansCentroid type centroids
     */
    public static ArrayList<KMeansCentroid> readCentroidsConf(Configuration conf) {
        // Create an empty list to store the centroids
        ArrayList<KMeansCentroid> centroids = new ArrayList<>();
        // Get the string representations of the centroids from the configuration object
        String[] centroidStrings = conf.getStrings("centroids");
        // Iterate over all centroid strings
        for (int i = 0; i < centroidStrings.length; i++) {
            // Convert the string representation of the centroid to an array of doubles
            String[] centroidCoords = centroidStrings[i].split(" ");
            ArrayList<Double>coordinates = new ArrayList<>();
          //  double[] coordinates = new double[centroidCoords.length];
            for (String coord : centroidCoords) {
                coordinates.add(Double.parseDouble(coord));
            }
            // Create a new Centroid object and add it to the list
            centroids.add(new KMeansCentroid(i, coordinates));
        }
        // Return the list of centroids
        return centroids;
    }

    /**
     * If num_reducers is 1 then the final centroids are read from a single file.
     * @param conf Hadoop's configuration file.
     * @param pathString the path to the output file
     * @param initialCentroidsFile  boolean that holds if this method is reading from an initial centroid file
     *                              or not.
     * @return
     * @throws IOException
     */
    public static ArrayList<KMeansCentroid> readCentroidsFromSingleReducerFile(Configuration conf,String pathString, boolean initialCentroidsFile) throws IOException {
        ArrayList<KMeansCentroid> centroids = new ArrayList<>();
        Path path = new Path(pathString);
        FileSystem hdfs = FileSystem.get(conf);
        FSDataInputStream in = hdfs.open(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split("\\s");
            if(initialCentroidsFile){
                fields=line.split(",");
            }
            KMeansCentroid centroid = buildCentroidObject(fields);
            centroids.add(centroid.getCentroidID().get(), centroid);
        }
        in.close();
        return centroids;
    }

    /**
     * If num_reducers >1 the final centroids are outputted in an hdfs dir that has multiple number of
     * files
     * @param conf Hadoops configuration file
     * @param dirPath the path to the dir that holds the centroids
     * @return
     * @throws IOException
     */
    public static ArrayList<KMeansCentroid> readCentroidMultipleReducersFile(Configuration conf, Path dirPath) throws IOException {
        ArrayList<KMeansCentroid> centroids = new ArrayList<>();
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] fileStatus = fs.listStatus(dirPath);
        for (FileStatus status : fileStatus) {
            Path filePath = status.getPath();
            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(filePath)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\s");
                centroids.add(buildCentroidObject(fields));
            }
        }
        Collections.sort(centroids);
        return centroids;
    }

    /**
     * Given a string representation of centroids this method helps us build centroids as set in the
     * KMeansCentroid class.
     * @param records takes a string array
     * @return returns the KMeansCentroid centroid objects.
     */

    //We need a centroid Object from String arrays.
    private static KMeansCentroid buildCentroidObject(String[] records) {
        KMeansCentroid centroid = new KMeansCentroid();
        // Set centroid ID
        int centroidId = (int) Double.parseDouble(records[0]);
        centroid.getCentroidID().set(centroidId);
        ArrayList<Double> coords = new ArrayList<>();
        for (int i = 1; i < records.length; i++) {
            double value = Double.parseDouble(records[i]);
            coords.add(value);
        }
        DataPoint point = new DataPoint(coords);
        centroid.setPoint(point);
        return centroid;
    }

    /**
     * Check if the stopping criterion is fulfilled or not
     * @param newCentroids gets the new centroids from the caller
     * @param threshold the constant value of the initial centroid set by the programmer
     * @param conf Hadoop's configuration
     * @return returns a true of the shift of the centroids is less than the threshold already set by the
     * programmer.
     */
    public static boolean hasConverged(ArrayList<KMeansCentroid> newCentroids, float threshold, Configuration conf) {
        double shift = getShiftOfCentroids(newCentroids, conf);
        if (!(shift < threshold)) {
            return false;
        }
        return true;
    }

    /**
     * THis method calculates the shift of centroids between each successive centroids.
     * @param newCentroids These are the centroids in the current iteration
     * @param conf Hadoop's configuration
     * @return returns the double value of the shift of the centroids
     */
    public static double getShiftOfCentroids(ArrayList<KMeansCentroid> newCentroids, Configuration conf) {
        // Initialize the variable to store the total shift of all centroids
        double shift = 0.0;
        // Read previous centroids from the configuration object
        ArrayList<KMeansCentroid> oldCentroids = readCentroidsConf(conf);
        // Initialize the variable to store the total shift of all centroids
        // Iterate over all centroids
        for (int i = 0; i < oldCentroids.size(); i++) {
            // Get the coordinates of the previous and current centroid
            ArrayList<Double> oldCentroidsCoord = oldCentroids.get(i).getPoint().getCoordinates();
            ArrayList<Double> currentCentroidCoord = newCentroids.get(i).getPoint().getCoordinates();
            // Calculate the shift for each dimension and add it to the total shift
            for (int j = 0; j < oldCentroidsCoord.size(); j++) {
                shift += Math.abs(currentCentroidCoord.get(j) - oldCentroidsCoord.get(j));
            }
        }
        return shift;
    }

    /**
     * Helps to configurate a mapreduce job to the mapreduce workflow.
     * @param conf Hadoop's configuration
     * @param inputPath the path to the input file
     * @param outputPath the path to the output file
     * @param numReducers holds the number of reducers
     * @param iteration the current iteration
     * @return returns the Job type of
     */
    public static Job configureJob(Configuration conf, String inputPath, String outputPath, int numReducers, int iteration) {
        Job job;
        try {
            System.out.println("job number "+(iteration+1));
            job = Job.getInstance(conf, "iter_" + iteration);
            job.setJarByClass(KMeansMRMain.class);
            job.setMapperClass(KMeansMRMapper.class);
            job.setCombinerClass(KMeansMRCombiner.class);
            job.setReducerClass(KMeansMRReducer.class);
            job.setNumReduceTasks(numReducers);
            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(DataPoint.class);
            //
            FileInputFormat.addInputPath(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            //
            job.setInputFormatClass(TextInputFormat.class);
            job.setOutputFormatClass(TextOutputFormat.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return job;
    }
    /**
     * Does the iteration of the Kmeans algorithm in the mapreduce implementation
     * @param conf Hadoop's configuration
     * @param INPUT the input file path taken from the terminal args
     * @param OUTPUT the output file path
     * @param num_reducers holds the number of reucers
     * @param max_iteration holds the constant value of the maximum iteration set by the programmer
     * @param threshold the threshold value to check for the stopping criterion
     * @throws IOException If there is an I/O error
     */
    public static void MapReduceWorkFlow(Configuration conf, String INPUT, String OUTPUT, int num_reducers, int max_iteration, float threshold) throws IOException {
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            System.err.println("[Error] fileSystem not found: " + e.getMessage());
            System.exit(1);
        }
        boolean stop = false;
        int iteration = 0;
        while (!stop && iteration < max_iteration) {
            // Delete output path if it already exists
            try {
                fs.delete(new Path(OUTPUT), true);
            } catch (IOException e) {
                System.err.println("[Error] can't delete output file: " + e.getMessage());
                System.exit(1);
            }
            //Job configuration
            try (Job job = KMeansHelper.configureJob(conf, INPUT, OUTPUT, num_reducers, iteration)) {
                //change the INPUT
                if (job == null) {
                    System.err.println("[Error]:null job");
                    System.exit(1);
                }
                if (!job.waitForCompletion(true)) {
                    System.err.println("[Error]: Job not completed");
                    System.exit(1);
                }
            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            // Read the current and previous centroids
            ArrayList<KMeansCentroid> currentCentroids = null;
            try {
                if (num_reducers > 1) {
                    currentCentroids = KMeansHelper.readCentroidMultipleReducersFile(conf, new Path(OUTPUT));
                } else {
                    String currentCentroidFile = new Path(OUTPUT) + "/part-r-00000";
                    currentCentroids = KMeansHelper.readCentroidsFromSingleReducerFile(conf,currentCentroidFile, false);
                }
            } catch (IOException e) {
                System.err.println("[Error] can't read current centroids because of : " + e.getMessage());
                System.exit(1);
            }
			double shift = KMeansHelper.getShiftOfCentroids(currentCentroids, conf);
			// Check if converged
			stop = (shift < threshold);
            if (!stop) {
                KMeansHelper.setCentroidsToConf("centroids", currentCentroids, conf);
            }
            iteration++;
        }
    }
}