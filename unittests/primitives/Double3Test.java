package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Double3Test {

    @Test
    void add() {
        Double3 d1 = new Double3(1,1,1);
        Double3 d2 = new Double3(2,2,2);
        Double3 d3 = new Double3(3,3,3);
        assertEquals(d1.add(d2), d3, "Adding double3 to double3 Failed");
    }

    @Test
    void subtract() {
        Double3 d1 = new Double3(1,1,1);
        Double3 d2 = new Double3(2,2,2);
        Double3 d3 = new Double3(3,3,3);
        assertEquals(d3.subtract(d2), d1, "Subtracting double3 to double3 Failed");
    }

    @Test
    void scale() {
        Double3 d1 = new Double3(1,1,1);
        Double3 d2 = new Double3(2,2,2);
        double num = 2;
        assertEquals(d1.scale(num), d2, "Scaling double3 by a double Failed");
    }

    @Test
    void reduce() {
        Double3 d1 = new Double3(1,1,1);
        Double3 d2 = new Double3(2,2,2);
        double num = 2;
        assertEquals(d2.scale(num), d1, "Reducing double3 by a double Failed");
    }

    @Test
    void product() {
        Double3 d1 = new Double3(4,4,4);
        Double3 d2 = new Double3(2,2,2);
        Double3 d3 = new Double3(8,8,8);
        assertEquals(d1.product(d2), d3, "Product double3 by double3 Failed");
    }
}