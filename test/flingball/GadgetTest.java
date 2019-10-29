package flingball;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import flingball.TriangleBumper.Orientation;
import physics.Vect;

public class GadgetTest {

    /**
     * Testing strategy
     *  Width: w = 0, 0 < w < 20, w = 20
     *  Height: h = 0, 0 < h < 20, h = 20
     *  Orientation (for triangle bumper): 0, 90, 180, 270
     * 
     *  - timeToCollide()
     *      . POSITIVE INFINITY, < POSITIVE INFINITY
     *    
     *  - collide()
     *      . hits absorber
     *      . hits bumpers
     *  
     *  - render()
     *      . different bumpers and absorber
     *      
     *  - name()
     *      . no further partitions
     *      
     *  - position()
     *      . top left, top right, bottom left, bottom right corner
     *      
     *  - link()
     *      . all the bumper types and the absorber itself
     *      
     *  - action()
     *      . none for the bumpers
     *      . absorber
     *      
     *  - equals(), hashCode(), toString()
     *      . no further partitions
     *      
     *  - drawing implementation will be tested visually
     *  
     *  ToDo:
     *  test Flipper rotation
     *  test collision write-out for Portal
     *  test collision write-out for wall
     * 
     */

    @Test(expected = AssertionError.class) public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    // getOrientation() 0, 90, 180, 270
    public void testOrientation() {
        TriangleBumper t0 = new TriangleBumper("t0", 0, 0, Orientation.DEG_0);
        TriangleBumper t90 = new TriangleBumper("t90", 8, 16, Orientation.DEG_90);
        TriangleBumper t180 = new TriangleBumper("t180", 4, 9, Orientation.DEG_180);
        TriangleBumper t270 = new TriangleBumper("t270", 2, 17, Orientation.DEG_270);
        assertEquals("orientation should be 0", t0.getOrientation(), Orientation.DEG_0);
        assertEquals("orientation should be 90", t90.getOrientation(), Orientation.DEG_90);
        assertEquals("orientation should be 180", t180.getOrientation(), Orientation.DEG_180);
        assertEquals("orientation should be 270", t270.getOrientation(), Orientation.DEG_270);
    }


    @Test
    // collisionTime for ball hitting gadget (<inf) 
    public void testCollisionTime() {
        CircleBumper a = new CircleBumper("circle", 10, 19);
        Ball ball = new Ball("downball", 10, 10, 0, 5);
        assertTrue("collision time with circle should be 1.563...", a.timeToCollide(ball, 0.) == 1.5639320225002102);
    }

    @Test
    // collide with bumper
    public void testCollideBumper() {
        SquareBumper a = new SquareBumper("square", 10, 19);
        Ball ball = new Ball("downball", 10, 10, 0, 5);
        a.collide(ball);
        assertEquals("new center for ball", ball.getPosition(), new Vect(10, 10));
    }

    @Test
    // collide with absorber
    public void testCollideAbsorber() {
        Absorber a = new Absorber("absorber", 0, 17, 20, 3);
        Ball ball = new Ball("downball", 10, 10, 0, 5);
        a.collide(ball);
        assertEquals("new velocity for ball", ball.getVelocity(), Vect.ZERO);
        assertEquals("new center for ball", ball.getPosition(), new Vect(15, 15));
    }

    @Test
    // no trigger for absorber
    public void testNoTriggerAction() {        
        Absorber a = new Absorber("absorber", 0, 17, 20, 3);
        Ball ball1 = new Ball("downball1", 10, 10, 0, 5);
        
        // no trigger for the absorber
        a.collide(ball1); // store first ball in absorber
        ArrayList<Ball> balls = new ArrayList<Ball>();
        balls.add(ball1);
        
        assertEquals("ball1 should not be moving", ball1.getVelocity(), Vect.ZERO);
        assertEquals("ball 1 should be in the absorber", balls, a.getHeldBalls());
        assertEquals(ball1.getPosition(), new Vect(15, 15));
    }
    
    @Test
    // trigger for absorber
    public void testTriggerAction() {
        Absorber a = new Absorber("absorber", 0, 17, 20, 3);
        Ball ball1 = new Ball("downball1", 10, 10, 0, 5);
        Ball ball2 = new Ball("downball2", 10, 20, 0, 5);
        TriangleBumper triangle = new TriangleBumper("triangle", 10, 15, Orientation.DEG_180);
        
        a.link(triangle); // link trigger to the Square Bumper
        a.collide(ball1); // store first ball in absorber
        ArrayList<Ball> balls = new ArrayList<Ball>();
        balls.add(ball1); 
        a.collide(ball2);
        balls.add(ball2);
        assertEquals(ball1.getVelocity(), Vect.ZERO);
        assertEquals(ball2.getVelocity(), Vect.ZERO);
        assertEquals(balls, a.getHeldBalls());
    }

    @SuppressWarnings("unlikely-arg-type") @Test
    // testing equals
    public void testEquals() {
        CircleBumper a = new CircleBumper("name", 0, 0);
        CircleBumper b = new CircleBumper("name", 1, 1);
        SquareBumper c = new SquareBumper("name", 0, 0);
        CircleBumper d = new CircleBumper("name", 0, 0);
        assertTrue(a.equals(d));
        assertFalse(a.equals(b));
        assertFalse(a.equals(c));
    }

    @Test
    // testing hash code
    public void testHashCode() {
        TriangleBumper a = new TriangleBumper("triangle", 3, 3, Orientation.DEG_0);
        TriangleBumper b = new TriangleBumper("triangle", 3, 3, Orientation.DEG_0);
        assertEquals("should have same hash code", a.hashCode(), b.hashCode());
    }

    @Test
    // testing toString
    public void testToString() {
        TriangleBumper a = new TriangleBumper("triangle", 3, 3, Orientation.DEG_0);
        SquareBumper b = new SquareBumper("square", 5, 3);
        CircleBumper c = new CircleBumper("circle", 8, 3);
        Absorber d = new Absorber("absorber", 0, 17, 20, 3);
        assertEquals(a.toString(), "TriangleBumper [orientation=DEG_0, name=triangle, topLeft=<3.0,3.0>]");
        assertEquals(b.toString(), "SquareBumper [name=square]");
        assertEquals(c.toString(), "CircleBumper [center=<18.0,13.0>, name=circle]");
        assertEquals(d.toString(), "Absorber [name=absorber, pos=<0.0,17.0>, width=20.0, height=3.0, triggers=[], heldBalls=[], "
                + "sides=[LineSegment(<0.0,17.0>-<20.0,17.0>), LineSegment(<20.0,17.0>-<20.0,20.0>), "
                + "LineSegment(<20.0,20.0>-<0.0,20.0>), LineSegment(<0.0,20.0>-<0.0,17.0>)]]");
    }


}
