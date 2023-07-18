package it.unipi.cc.toretto.kmeans;




import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
public class KMeansMRMain {
    public static void main(String[] args) throws Exception {
        long start;
        long end;
        start = System.currentTimeMillis();
        Configuration conf = new Configuration();
        // Config XML file location.
        conf.addResource(new Path("/home/tess/IdeaProjects/kmeans_clustering_hadoop/src/main/resources/config.xml"));
        //non-hadoop commandline args
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println(" For the configuration to work it needs two arguments <Input><Output>");
            System.exit(1);
        }
        String otherArgs1 = otherArgs[1];
        //Parameters setting
        final String INPUT = otherArgs[0];
        final String OUTPUT = otherArgs[1] + "/temp";
        final int DATASET_SIZE = conf.getInt("dataset", 10);
        final int h = conf.getInt("distance", 2);
        final int K = conf.getInt("k", 4);
        final float THRESHOLD = conf.getFloat("threshold", 0.01f);
        final int num_reducers = conf.getInt("num_reducers", 1);
        final int MAX_ITERATIONS = conf.getInt("max_iteration", 100);
        // initial centroids file name  /user/tess/input/icKmeansPP_2D2K500N.txt
//icRandom_4D4K10000N.txt
        String initCentroidsFile = "icRandom_4D4K10000N";
        // icKmeansPP, icRandom  4D4K1500N
        // Set initial centroids in the config
        KMeansHelper.setCentroidsToConf("centroids", KMeansHelper.readInitalCentroidsFromHDFS(conf, initCentroidsFile), conf);
        // workflow
        KMeansHelper.MapReduceWorkFlow(conf, INPUT, OUTPUT, num_reducers, MAX_ITERATIONS, THRESHOLD, otherArgs1);
        end = System.currentTimeMillis();
        end -= start;
        System.out.println("execution time: " + end + " ms");
        // System.out.println("num_iteration: " + job_iteration);
        System.exit(0);
    }
}