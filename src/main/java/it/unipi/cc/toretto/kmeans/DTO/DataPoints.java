package it.unipi.cc.toretto.kmeans.DTO;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataPoints implements Writable {
    //TODO this class handles some arithmetic calculation on the DataPoints we generate
    // using the python scripts
    private float [] features; // Holds the features of the dataset
    private int numberOfPoints; // this will be used for the partial sums in the combiners/reducers
    private  int dataDimension; // It holds the dimension of the data
    public DataPoints() {
        this.dataDimension = 0;
    }
    public DataPoints(final float[] f) {
        this.initData(f);
    }
    public DataPoints(final String[] s) {
        this.initData(s);
    }
    public static DataPoints copy(final DataPoints cent) {
        DataPoints ret = new DataPoints(cent.features);
        ret.numberOfPoints = cent.numberOfPoints;
        return ret;
    }
    public void initData(final float[] f) {
        this.features = f;
        this.dataDimension = f.length;
        this.numberOfPoints= 1;
    }
    public void initData(String[] s) {
        this.features = new float[s.length];
        this.dataDimension = s.length;
        this.numberOfPoints = 1;
        for (int i = 0; i < s.length; i++) {
            this.features[i] = Float.parseFloat(s[i]);
        }
    }
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.dataDimension);
        out.writeInt(this.numberOfPoints);
        for(int i = 0; i < this.dataDimension; i++) {
            out.writeFloat(this.features[i]);
        }
    }
    @Override
    public void readFields(DataInput dataInput ) throws IOException {
        //TODO read logic goes here
        this.dataDimension = dataInput.readInt();
        this.numberOfPoints = dataInput.readInt();
        this.features = new float[this.dataDimension];
        for(int i = 0; i < this.dataDimension; i++) {
            this.features[i] = dataInput.readFloat();
        }
    }
    public void average(){
        //TODO calculate the average of the datapoints
        for (int i = 0; i < this.dataDimension; i++) {
            float temp = this.features[i] / this.numberOfPoints;
            this.features[i] = (float)Math.round(temp*100000)/100000.0f;
        }
        this.numberOfPoints = 1;
    }
    public void total_sum(DataPoints p){
        //TODO calculate the sum of the datapoints
        for (int i = 0; i < this.dataDimension; i++) {
            this.features[i] += p.features[i];
        }
        this.numberOfPoints += p.numberOfPoints;
    }
    public float distanceCalculator(DataPoints p, int h){
        if (h < 0) {
            h = 2;
        }

        if (h == 0) {
            // Chebyshev
            float max = -1f;
            float diff = 0.0f;
            for (int i = 0; i < this.dataDimension; i++) {
                diff = Math.abs(this.features[i] - p.features[i]);
                if (diff > max) {
                    max = diff;
                }
            }
            return max;

        } else {
            // Manhattan, Euclidean, Minkowsky
            float dist = 0.0f;
            for (int i = 0; i < this.dataDimension; i++) {
                dist += Math.pow(Math.abs(this.features[i] - p.features[i]), h);
            }
            dist = (float)Math.round(Math.pow(dist, 1f/h)*100000)/100000.0f;
            return dist;
        }
    }

    public boolean equals(Object o){
        return true;
    }
    public int hashCode(){
        return 0;
    }
    public String toString(){
        StringBuilder point = new StringBuilder();
        for (int i = 0; i < this.dataDimension; i++) {
            point.append(this.features[i]);
            if(i != dataDimension - 1) {
                point.append(",");
            }
        }
        return point.toString();
    }
}