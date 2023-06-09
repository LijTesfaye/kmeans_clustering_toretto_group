package it.unipi.cc.toretto.kmeans.DTO;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataPoints implements Writable {
    //TODO this class handles some arithmetic calculation on the DataPoints we generate
    // using the python scripts
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        //TODO write logic
    }
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        //TODO read logic goes here
    }
    public void average(){
        //TODO calculate the average of the datapoints
    }
    public void total_sum(){
        //TODO calculate the sum of the datapoints
    }
}