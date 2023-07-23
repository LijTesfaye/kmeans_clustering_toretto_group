package it.unipi.cc.toretto.kmeans.DTO;

import com.sun.istack.NotNull;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * In this Kmeans algorithm implementation the DataPoint Class represents a 'point' or a 'datapoint'
 * (we used these words interchangeably)
 * , so every datapoint Object is characterized by two fields
 * 1) coordinates : holds the geometric coordinates of a datapoint
 * 2) data: that holds the weight of a datapoint. The weight is important in the reducer to calculate the
 * average of the datapoints that are categorized into a centroid.
 *
 */
public class DataPoint implements Writable {
    //datapoint coordinates
    private ArrayList<Double> coordinates;
    /**
     * Holds a weight, there are two cases.
     * IF is a centroid 'data' variable will hold the weight of all the datapoints that are associated within that centroid.
     * IF NOT a centroid it will have a weight of 1.
     */
    private int data;
    /**
     * Default constructor
     * Initializes 'coordinates' field to an empty arrayList  AND
     * the 'data' field to 1
     */
    public DataPoint() {
        this.coordinates = new ArrayList<>();
        this.data = 1;
    }

    /**
     * Constructor
     * @param coord Holds the coordinates of the new point.
     */
    public DataPoint(ArrayList<Double> coord) {
        this.coordinates = coord;
        this.data = 1;
    }
    /**
     * This constructor helps for creating a DataPoint object from
     * a comma-separated string representation.
     * @param text The input text representing the coordinates.
     * @throws NullPointerException If the text is null.
     */
    public DataPoint(String text) throws NullPointerException {
        String[] c = text.split(",");
        this.coordinates = new ArrayList<>();
        for (String x : c) {
            this.coordinates.add(Double.parseDouble(x));
        }
        this.data = 1;
    }
    /**
     * write method writes the Datapoint object to the DataOutput stream.
     * @param dataOutput The output stream to write the data to.
     * @throws IOException If there is an Input output error.
     */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.coordinates.size());
        for (Double coordinate : this.coordinates) {
            dataOutput.writeDouble(coordinate);
        }
        dataOutput.writeInt(data);
    }

    /**
     * THis method reads the DataPoint object from DataInput
     * @param dataInput the input stream to read the data from.
     * @throws IOException If there is an Input output error.
     */
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

    /**
     * Calculate the Euclidean distance between two DataPoints
     * @param point the point from which the distance is going to be calculated.
     * @return the Euclidean distance
     */

    public double getDistance(@NotNull DataPoint point) {
        double sum = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            double diff = this.coordinates.get(i) - point.coordinates.get(i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    /**
     * Sums-up two  datapoints' coordinate wise.
     * @param point
     */
    public void sum(DataPoint point) {
        for (int i = 0; i < this.coordinates.size(); i++) {
            this.coordinates.set(i, this.coordinates.get(i) + point.getCoordinates().get(i));
        }
        this.data += point.getInstances();
    }

    /**
     * Calculate the average of the datapoints sum
     * Every coordinate will be divided by the weight variable aka, 'data'.
     */
    public void average() {
        this.coordinates.replaceAll(aDouble -> aDouble / this.data);
        this.data = 1;
    }

    /**
     *  A Getter method
     * @return returns the 'Double Arraylist of coordinates'
     */
    public ArrayList<Double> getCoordinates() {
        return coordinates;
    }

    /**
     * Getter
     * Retrieves the number of the corresponding elements of a partialSum
     * @return return number of elements/Weight of that partialSum
     */
    public int getInstances() {

        return data;
    }
    /**
     * toString method
     * @return returns the string representation of a DataPoint object
     */
    public String toString() {
        return this.coordinates.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}

