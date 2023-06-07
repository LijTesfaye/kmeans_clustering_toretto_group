package it.unipi.cc.toretto.kmeans;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class KmeansClustering implements Writable, Comparable<KmeansClustering> {
    int x,y;
    /* Default constructor*/
    public KmeansClustering(){}
    public KmeansClustering(int x, int y){
        this.x=x;
        this.y=y;
    }

    @Override
    public int compareTo(KmeansClustering o) {
        return 0;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }
}
