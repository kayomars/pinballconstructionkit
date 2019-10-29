package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import physics.*;

/**
 * A mutable implementation of Gadget representing an Absorber
 *
 */
public class Absorber implements Gadget{
    
    private final String name;
    private final Vect pos;
    private final double width;
    private final double height;
    private final List<Gadget> triggers;
    private final ArrayList<Ball> heldBalls;  
    private final List<LineSegment> sides;
    
    //Abstraction Function
    //AF(name, pos, width, height, triggers, heldBalls, sides) =
    //      an Absorber with name name, centered at position pos, with width width and height height,
    //      which is currently holding the balls in heldBalls, and when triggered, will activate the gadgets in triggers. 
    //      In the simulation, it is made up of the 4 sides in sides
    
    //Rep invariant
    // pos >= 0
    // pos + width >= 0, pos + height >= 0
    // sides.size() = 4
    
    //Rep exposure argument: 
    // all fields private and final
    // triggers, heldBalls, and sides contain no aliases and are not returned to the client
    // the items in triggers and heldBalls are mutable but we want to keep track of any changes to those objects
    
    /**
     * Creates a new absorber gadget simulating the ball return mechanism
     * with the top-left corner at (xPos, yPos) with given width and height.
     * 
     * @param name Gadget identifier
     * @param xPos x coordinate
     * @param yPos y coordinate
     * @param width of the gadget 
     * @param height of the gadget
     */
    public Absorber(String name, double xPos, double yPos, double width, double height) {
        this.name = name;
        this.pos = new Vect(xPos, yPos);
        this.width = width;
        this.height = height;
        this.triggers = new ArrayList<>();
        this.heldBalls = new ArrayList<>();
        
        this.sides = new ArrayList<>();
        this.sides.add(new LineSegment(xPos, yPos, xPos + width, yPos));
        this.sides.add(new LineSegment(xPos + width, yPos, xPos + width, yPos + height));
        this.sides.add(new LineSegment(xPos + width, yPos + height, xPos, yPos + height));
        this.sides.add(new LineSegment(xPos, yPos + height, xPos, yPos));
    }
    
    private void checkRep() {
        assert pos.x() >= 0;
        assert pos.y() >= 0;
        assert sides.size() == 4;
        assert pos.x() + width >= 0;
        assert pos.y() + height >= 0;
    }
    
    public List<Ball> getHeldBalls() {
        return heldBalls;
    }

    @Override
    public double timeToCollide(Ball ball, Double elapsedTime) {
        double minTime = Physics.timeUntilWallCollision(sides.get(0), ball.getCircle(), ball.getVelocity());
        for (LineSegment side: sides) {
            double sideTime = Physics.timeUntilWallCollision(side, ball.getCircle(), ball.getVelocity());
            if (sideTime < minTime) {
                minTime = sideTime;
            }
        }
        checkRep();
        return minTime;
    }
      
    @Override
    public String collide(Ball ball) {
        // hold the ball
        ball.setPosition(new Vect(pos.x()+width-ball.getCircle().getRadius(), pos.y()+height-ball.getCircle().getRadius()));
        ball.setVelocity(new Vect(0,0));
        if (!heldBalls.contains(ball))  {
            heldBalls.add(ball);
        }
        for (Gadget trigger: triggers) {
            trigger.action();
        }
        return "";
    }
    
    @Override
    public void render(Graphics2D graphic) {
        graphic.setColor(Color.green);
        graphic.fill(new Rectangle2D.Double(pos.x(), pos.y(), width, height));        
    }
    
    @Override
    public String name() {
        checkRep();
        return this.name;     
    }

    @Override
    public Vect position() {
        //returns top left corner
        checkRep();
        return sides.get(0).p1();
    }
    
    @Override
    public void link(Gadget activator) {
        checkRep();
        triggers.add(activator);               
    }
    
    @Override
    public void action() {
        if (!heldBalls.isEmpty()) {
            Ball otherBall = heldBalls.get(0);
            otherBall.setPosition(new Vect(pos.x() + width - otherBall.getCircle().getRadius(), pos.y() - otherBall.getCircle().getRadius()));
            otherBall.setVelocity(new Vect(0, -50.*Flingball.L));
            checkRep();
            heldBalls.remove(0);
        }
    }

    
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(height);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
        temp = Double.doubleToLongBits(width);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
        
    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Absorber other = (Absorber) obj;
        if (Double.doubleToLongBits(height) != Double.doubleToLongBits(other.height))
            return false;
        if (pos == null) {
            if (other.pos != null)
                return false;
        } else if (!pos.equals(other.pos))
            return false;
        if (Double.doubleToLongBits(width) != Double.doubleToLongBits(other.width))
            return false;
        return true;
    }

    /**
     * @return Absorber [name=name, pos=pos, width=width, height=height, triggers=triggers, heldBalls=heldBalls, sides=sides]
     */
    @Override public String toString() {
        return "Absorber [name=" + name + ", pos=" + pos + ", width=" + width + ", height=" + height + ", triggers="
                + triggers + ", heldBalls=" + heldBalls + ", sides=" + sides + "]";
    }
    
}
