package flingball;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import flingball.TriangleBumper.Orientation;
import physics.Vect;

public class BoardTest {
    /**
     * Testing strategy
     *  - board with ... 
     *      . balls = 0, 1, > 1
     *      . gadgets = 0, 1, > 1 (for each kind)
     *      . gravity = 0, > 0
     *      . mu1, mu2, = 0 > 0
     *  - equals(that)
     *      . equals / not equals
     *  - hashcode()
     *      . no further partitions
     *  - toString()
     *      . no further partitions
     *  - timeStep()
     *      . no further partitions
     * 
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    // equals
    public void testEquals() {
        List<Ball> ballList = new ArrayList<Ball>();
        Ball ball1 = new Ball("ball1", 10, 10, 5, 5);
        Ball ball2 = new Ball("ball2", 1, 1, 5, 5);
        ballList.add(ball1);
        ballList.add(ball2);
        List<Gadget> gadgetList = new ArrayList<Gadget>();
        Gadget squareBumper = new SquareBumper("square1", 3, 3);
        Gadget circleBumper = new CircleBumper("circle1", 4, 3);
        Gadget triangleBumper = new TriangleBumper("triangle1", 5, 3, Orientation.DEG_270);
        Gadget absorber = new Absorber("absorber", 2, 16, 5, 2);
        gadgetList.add(squareBumper);
        gadgetList.add(circleBumper);
        gadgetList.add(triangleBumper);
        gadgetList.add(absorber);
        Board a = new Board("A", gadgetList, ballList, 20, 0.05, 0.05, new HashMap<String, String>(), new HashMap<String, Vect>());
        Board b = new Board("A", gadgetList, ballList, 20, 0.05, 0.05, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertTrue("should be equal", a.equals(b));
        assertTrue("should be equal", b.equals(a));
    }
    
    @Test
    // hashCode
    public void testHashCode() {
        Board a = new Board("A", new ArrayList<Gadget>(), new ArrayList<Ball>(), 50, 0.7, 0.7, new HashMap<String, String>(), new HashMap<String, Vect>());
        Board b = new Board("B", new ArrayList<Gadget>(), new ArrayList<Ball>(), 50, 0.7, 0.7, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals("hashcode should be correct", a.hashCode(), b.hashCode());
    }
        
    @Test
    // time step
    public void testTimeStep() {
        List<Ball> ballList = new ArrayList<Ball>();
        Ball ball1 = new Ball("ball1", 10, 10, 5, 5);
        ballList.add(ball1);
        Board a = new Board("A", new ArrayList<Gadget>(), ballList, 0, 0, 0, new HashMap<String, String>(), new HashMap<String, Vect>());
        a.timeStep(5);
        assertEquals(new Vect(35, 35), ball1.getPosition());
        
    }
    
    
    
}
