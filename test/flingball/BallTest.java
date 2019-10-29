package flingball;

import static org.junit.Assert.*;

import org.junit.Test;

import physics.Circle;
import physics.Vect;

public class BallTest {
    /**
     * Testing strategy
     * - setPosition(), getPosition(), getCircle()
     *  . x = 0, 0 < x < 19, x = 19
     *  
     * - setVelocity(), getVelocity()
     *  . positive directional x/y velocity
     *  . negative directional x/y velocity
     *  
     * - updatePosition
     *  . elapsedTime > 0
     *  
     * - applyMechanics
     *  . gravity > 0
     *  . mu1, mu2 >= 0
     *  . elapsedTime > 0
     *  
     * - toString(), equals(), hashCode()
     *   . no further partitions

     */
    
    private final Ball ball = new Ball("ball1", 10, 10, 1, 2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument : -ea
    }
    
    // setPosition(), getPosition(), getCircle() x = 0, y = 0
    @Test
    public void testPositionTopLeft() {
        ball.setPosition(new Vect(0, 0));
        assertEquals(new Vect(0, 0), ball.getPosition());
        assertEquals(new Circle(0, 0, 5.0), ball.getCircle());
    }
    
    // setPosition(), getPosition(), getCircle() 0 < x < 19, 0 < y < 19
    @Test
    public void testPositionMiddle() {
        ball.setPosition(new Vect(13, 14));
        assertEquals(new Vect(13, 14), ball.getPosition());
        assertEquals(new Circle(13, 14, 5.0), ball.getCircle());
    }
    
    // setPosition(), getPosition(), getCircle() x = 19, y = 19
    @Test
    public void testPositionBottomRight() {
        ball.setPosition(new Vect(19, 19));
        assertEquals(new Vect(19, 19), ball.getPosition());
        assertEquals(new Circle(19, 19, 5.0), ball.getCircle());
    }
    
    // setVelocity(), getVelocity() positive directions
    @Test
    public void testPositiveVelocity() {
        ball.setVelocity(new Vect(9, 12));
        assertEquals(new Vect(9, 12), ball.getVelocity());
    }
    
    // setVector(), getVector() negative directions
    @Test
    public void testNegativeVelocity() {
        ball.setVelocity(new Vect(-3, -4));
        assertEquals(new Vect(-3, -4), ball.getVelocity());
    }
    
    // updatePosition, elapsedTime > 0
    @Test
    public void testUpdatePositionSmallTime() {
        ball.updatePosition(0.42);
        assertEquals(new Vect(10.42, 10.84), ball.getPosition());
    }
    
    // updatePosition, elapsedTime > 0
    @Test
    public void testUpdatePositionLargeTime() {
        ball.updatePosition(4.0);
        assertEquals(new Vect(14.0, 18.0), ball.getPosition());
    }
    
    // gravity > 0, mu1 > 0, mu2 > 0, elapsedTime > 0
    @Test
    public void testAppliedMechanics() {
        ball.applyMechanics(30, 0.045, 0.045, 2.0);
        assertEquals(new Vect(10, 10), ball.getPosition());
    }
    
    // toString
    @Test
    public void testToString() {
        assertEquals("Ball [name=ball1, xPos=10.0, yPos=10.0, xVel=1.0, yVel=2.0, radius=5.0]", ball.toString());
    }
    
    // equals
    @Test
    public void testEquals() {
        Ball newBall = new Ball("ball1", 10, 10, 1, 2);
        assertTrue(ball.equals(newBall));
    }
    
    // hashCode
    @Test
    public void testHashCode() {
        Ball newBall = new Ball("ball1", 10, 10, 1, 2);
        assertEquals(newBall.hashCode(), ball.hashCode());
    }
}








