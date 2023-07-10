package it.unipi.cc.toretto.kmeans;
import it.unipi.cc.toretto.kmeans.DTO.DataPoints;
import junit.framework.TestCase;
public class DataModelTest  extends TestCase {
    // TODO case_1: the Euclidean distance test

    // TODO case_2: the Manhattan distance test

    // TODO case_3: if there are other distance test cases
    private DataPoints p1; // bi-dimensional point
    private DataPoints p2; // bi-dimensional point

    private DataPoints t1; // tri-dimensional point
    private DataPoints t2; // tri-dimensional point

    private DataPoints z1; // 7-dimensional point
    private DataPoints z2; // 7-dimensional point
    private DataPoints z3; // 7-dimensional point

    protected void setUp() throws Exception {
        super.setUp();
        p1 = new DataPoints(new float[] {1, 2});
        p2 = new DataPoints(new float[] {2, 1});

        t1 = new DataPoints(new float[] {1, 2, 3});
        t2 = new DataPoints(new float[] {2, 1, 0});

        z1 = new DataPoints(new float[] {1, 2, 3, -4, 5, 6, 7});
        z2 = new DataPoints(new float[] {2, 1, 0, 1, 2, 3, 4});
        z3 = new DataPoints(new float[] {2, 1, 0, 1, 2, 3, 4});

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void testMinkowsky() {
        float distance = z2.distanceCalculator(z3, 5);
        System.out.println("Minkowsky Test 4 Distance: " + distance);
        assertTrue(distance == 0.0f);
    }

    public void testInfinity() {
        float distance = p1.distanceCalculator(p2, 0);
        System.out.println("Infinity Test 1 Distance: " + distance);
        assertTrue(Math.abs(distance - 1.0) < 0.0001f);
    }

    public void testManhattan() {
        float distance = z2.distanceCalculator(z3, 1);
        System.out.println("Manhattan Test 4 Distance: " + distance);
        assertTrue(distance == 0.0f);
    }

    public void testEuclidean() {
        float distance = p1.distanceCalculator(p2, 2);
        System.out.println("Euclidean Test 1 Distance: " + distance);
        assertTrue(Math.abs(distance - 1.4142) < 0.0001f );
    }
}

