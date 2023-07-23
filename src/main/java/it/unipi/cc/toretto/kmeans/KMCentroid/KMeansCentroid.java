package it.unipi.cc.toretto.kmeans.KMCentroid;

import it.unipi.cc.toretto.kmeans.DTO.DataPoint;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;


public class KMeansCentroid implements WritableComparable<KMeansCentroid> {
    /**
     * DataPoint Object that represents a KMeansCentroid Object
     */
    private DataPoint dataPoint;
    /**
     * Integer ID of a given centroid
     */
    private final IntWritable centroidID;

    /**
     * Default constructor for the  KMeansCentroid class
     */

    public KMeansCentroid() {
        this.centroidID = new IntWritable();
        this.dataPoint = new DataPoint();
    }

    /**
     * Create KMeansCentroid object given the Corresponding ID and coordinates of a centroid
     *
     * @param centroid_id ID of the new centroid
     * @param coords      coordinate values of the new centroids
     */
    public KMeansCentroid(int centroid_id, ArrayList<Double> coords) {
        this.centroidID = new IntWritable(centroid_id);
        this.dataPoint = new DataPoint(coords);
    }

    /**
     * Write the KMeansCentroid object to an output stream
     *
     * @param dataOutput output stream  to write the data to.
     * @throws IOException If there is an Input output error.
     */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.centroidID.write(dataOutput);
        this.dataPoint.write(dataOutput);
    }

    /**
     * Helps for sorting purpose by comparing two centroids objects based on their centroidID
     *
     * @param other the object to be compared.
     * @return A negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the other object.
     */
    @Override
    public int compareTo(KMeansCentroid other) {
        return Double.compare(this.getCentroidID().get(), other.getCentroidID().get());
    }

    /**
     * Read KMeansCentroid objects from DataInput Stream
     *
     * @param dataInput the input stream to read the data
     * @throws IOException If there is an Input output error.
     */
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.centroidID.readFields(dataInput);
        this.dataPoint.readFields(dataInput);
    }

    /**
     * retrives the Datapoint object that represents the KMeansCentroid object
     * @return the Datapoint
     */
    public DataPoint getPoint() {
        return dataPoint;
    }
    /**
     * Sets the DataPoint Object that represents the KmeansCentroid object
     * @param point the DataPoint object that represents the KmeansCentroid object
     */
    public void setPoint(DataPoint point) {
        this.dataPoint = point;
    }

    /**
     * Getter
     *
     * @return returns the centroidID
     */
    public IntWritable getCentroidID() {
        return centroidID;
    }
}
