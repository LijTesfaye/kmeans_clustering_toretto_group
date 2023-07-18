package it.unipi.cc.toretto.kmeans.KMCentroid;

import it.unipi.cc.toretto.kmeans.DTO.DataPoint;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;


public class KMeansCentroid implements WritableComparable<KMeansCentroid> {

    private DataPoint dataPoint;

    private final IntWritable centroidID;

    public KMeansCentroid(int centroid_id, ArrayList<Double> coords) {
        this.centroidID = new IntWritable(centroid_id);
        this.dataPoint = new DataPoint(coords);
    }

    public KMeansCentroid() {
        this.centroidID = new IntWritable();
        this.dataPoint = new DataPoint();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.centroidID.write(dataOutput);
        this.dataPoint.write(dataOutput);
    }

    @Override
    public int compareTo(KMeansCentroid other) {
        return Double.compare(this.getCentroidID().get(), other.getCentroidID().get());
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.centroidID.readFields(dataInput);
        this.dataPoint.readFields(dataInput);
    }


    public DataPoint getPoint() {
        return dataPoint;
    }

    public void setPoint(DataPoint point) {
        this.dataPoint = point;
    }

    public IntWritable getCentroidID() {
        return centroidID;
    }
}
