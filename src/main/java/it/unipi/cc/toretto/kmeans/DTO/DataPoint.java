package it.unipi.cc.toretto.kmeans.DTO;

import com.sun.istack.NotNull;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DataPoint implements Writable {
    //datapoint coordinates
    private ArrayList<Double> coordinates;

    //datapoint
    private int data;


    public DataPoint(ArrayList<Double> coord) {
        this.coordinates = coord;
        this.data = 1;
    }

    public DataPoint() {
        this.coordinates = new ArrayList<>();
        this.data = 1;
    }

    public DataPoint(String text) throws NullPointerException {
        String[] c = text.split(",");
        this.coordinates = new ArrayList<>();
        for (String x : c) {
            this.coordinates.add(Double.parseDouble(x));
        }
        this.data = 1;
    }
 

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.coordinates.size());
        for (Double coordinate : this.coordinates) {
            dataOutput.writeDouble(coordinate);
        }
        dataOutput.writeInt(data);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.coordinates = new ArrayList<>();
        int size = dataInput.readInt();
        for (int i = 0; i < size; i++) {
            double value = dataInput.readDouble();
            this.coordinates.add(value);
        }
        this.data = dataInput.readInt();
    }
    /* Operations on points */

    public double getDistance(@NotNull DataPoint point) {
        double sum = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            double diff = this.coordinates.get(i) - point.coordinates.get(i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public void sum(DataPoint point) {
        for (int i = 0; i < this.coordinates.size(); i++) {
            this.coordinates.set(i, this.coordinates.get(i) + point.getCoordinates().get(i));
        }
        this.data += point.getInstances();
    }

    public void average() {
        this.coordinates.replaceAll(aDouble -> aDouble / this.data);
        this.data = 1;
    }

    public ArrayList<Double> getCoordinates() {

        return coordinates;
    }

    public int getInstances() {

        return data;
    }

    public String toString() {
        return this.coordinates.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}

