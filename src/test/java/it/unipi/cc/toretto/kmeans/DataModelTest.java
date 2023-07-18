package it.unipi.cc.toretto.kmeans;
import it.unipi.cc.toretto.kmeans.DTO.DataPoint;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
public class DataModelTest  extends TestCase {
    @Test
    public void testGetDistance() {
        // Create DataPoint instances for testing
        ArrayList<Double> point1Coords = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        ArrayList<Double> point2Coords = new ArrayList<>(Arrays.asList(4.0, 5.0, 6.0));
        ArrayList<Double> point3Coords = new ArrayList<>(Arrays.asList(7.0, 8.0, 9.0));

        DataPoint point1 = new DataPoint(point1Coords);
        DataPoint point2 = new DataPoint(point2Coords);
        DataPoint point3 = new DataPoint(point3Coords);

        // Test getDistance method
        double dist1to2 = point1.getDistance(point2);
        double dist1to3 = point1.getDistance(point3);

        // Assert expected distances
        assertEquals(5.196, dist1to2, 0.001);
        assertEquals(10.392, dist1to3, 0.001);
    }
}

