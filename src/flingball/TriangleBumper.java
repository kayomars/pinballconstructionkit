package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import physics.*;

/**
 * An implementation of Gadget that represents a triangle bumper
 *
 */
public class TriangleBumper implements Gadget{
    
    private final Orientation orientation;
    
    private final String name;
    private final Vect topLeft;
    private final Polygon triangle;
    
    private final List<Gadget> triggers;
    private final List<LineSegment> sides;
    private final List<Circle> corners;
    
    //Abstraction Function
    // AF(name, orientation, topLeft, triangle, triggers, sides) = a triangle bumper with name name, orientation orientation (0/90/180/270)
    //      and topLeft corner at position topLeft. In simulation it is made up of the line segments in sides and is shown by the polygon triangle and when triggered, activates 
    //      the gadgets in triggers. 
    
    //Rep Invariant
    // topLeft >= 0
   
    private void checkRep() {
        assert topLeft.x() >= 0;
        assert topLeft.y() >= 0;
    }
    
    /**
     * Creates a new Triangle Bumper gadget with top-left at (xPos, yPos)*L
     * with given orientation
     * 
     * @param name Gadget identifier
     * @param xPos x coordinate
     * @param yPos y coordinate
     * @param orientation angle which is one of 0, 90, 180, 270
     */
    public TriangleBumper(String name, double xPos, double yPos, Orientation orientation) {
        this.name = name;
        this.topLeft = new Vect(xPos, yPos);
        this.orientation = orientation;      
        this.triggers = new ArrayList<Gadget>();
        this.sides = new ArrayList<>();
        this.triangle = new Polygon();
        this.corners = new ArrayList<>();
        
        Vect pivot; Vect leftCorner; Vect rightCorner;
        
        //make the sides based on orientation 
        switch(orientation) {
        case DEG_0: 
            pivot = new Vect(xPos, yPos);
            leftCorner = new Vect(xPos, yPos+Flingball.L);
            rightCorner = new Vect(xPos+Flingball.L, yPos);
            break;
        case DEG_90:
            pivot = new Vect(xPos+Flingball.L, yPos);
            leftCorner = new Vect(xPos, yPos);
            rightCorner = new Vect(xPos+Flingball.L, yPos+Flingball.L);
            break;
        case DEG_180:
            pivot = new Vect(xPos+Flingball.L, yPos+Flingball.L);
            leftCorner = new Vect(xPos+Flingball.L, yPos);
            rightCorner = new Vect(xPos, yPos+Flingball.L);
            break;
        case DEG_270:
            pivot = new Vect(xPos, yPos+Flingball.L);
            leftCorner = new Vect(xPos+Flingball.L, yPos+Flingball.L);
            rightCorner = new Vect(xPos, yPos);
            break;
        default: 
            //should never get here
            pivot = new Vect(0, 0);
            leftCorner = new Vect(0, 0);
            rightCorner = new Vect(0, 0);
        }
        //make the triangle based on the sides 
        this.triangle.addPoint((int) pivot.x(), (int) pivot.y());
        this.triangle.addPoint((int) leftCorner.x(), (int) leftCorner.y());
        this.triangle.addPoint((int) rightCorner.x(), (int) rightCorner.y());
        this.sides.addAll(Arrays.asList(new LineSegment(pivot, leftCorner), new LineSegment(pivot, rightCorner), new LineSegment(leftCorner, rightCorner)));
        this.corners.addAll(Arrays.asList(new Circle(pivot,.05*Flingball.L), new Circle(leftCorner,.05*Flingball.L), new Circle(rightCorner,.05*Flingball.L)));
    }
    
    public enum Orientation {
        DEG_0, DEG_90, DEG_180, DEG_270
    }    
    
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public double timeToCollide(Ball ball, Double elapsedTime) {
        double minTime = Double.POSITIVE_INFINITY;
        for (LineSegment side: sides) {
            double sideTime = Physics.timeUntilWallCollision(side, ball.getCircle(), ball.getVelocity());
            if (sideTime < minTime){
                minTime = sideTime;
            }
        }
        for (Circle corner: corners) {
            double cornerTime = Physics.timeUntilCircleCollision(corner, ball.getCircle(), ball.getVelocity());
            if (cornerTime < minTime){
                minTime = cornerTime;
            }
        }
        return minTime;
    }
      
    @Override
    public String collide(Ball ball) {
        double timeToCollide = timeToCollide(ball, 0.);
        for (LineSegment side: sides) {
            double sideTime = Physics.timeUntilWallCollision(side, ball.getCircle(), ball.getVelocity());
            if (sideTime == timeToCollide) {
                ball.setVelocity(Physics.reflectWall(side, ball.getVelocity()));
                break;
            }
        }
        for (Circle corner: corners) {
            double cornerTime = Physics.timeUntilCircleCollision(corner, ball.getCircle(), ball.getVelocity());
            if (cornerTime == timeToCollide) {
                ball.setVelocity(Physics.reflectCircle(corner.getCenter(), ball.getCircle().getCenter(), ball.getVelocity()));
                break;
            }
        }
        
        for (Gadget trigger: triggers) {
            trigger.action();
        }
        return "";
    }
    
    @Override
    public void render(Graphics2D graphic) {
        graphic.setColor(Color.orange);
        graphic.fill(triangle); 
    }
    
    @Override
    public String name() {
        return this.name;     
    }
    
    @Override
    public Vect position() {
        return topLeft;
    }
    
    @Override
    public void link(Gadget activator) {
        checkRep();
        triggers.add(activator);               
    }
    
    @Override
    public void action() {
        //by spec, nothing should happen
    }
    
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((topLeft == null) ? 0 : topLeft.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((orientation == null) ? 0 : orientation.hashCode());
        result = prime * result + ((triggers == null) ? 0 : triggers.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TriangleBumper other = (TriangleBumper) obj;
        if (topLeft == null) {
            if (other.topLeft != null)
                return false;
        } else if (!topLeft.equals(other.topLeft))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (orientation != other.orientation)
            return false;
        if (triggers == null) {
            if (other.triggers != null)
                return false;
        } else if (!triggers.equals(other.triggers))
            return false;
        return true;
    }

    /**
     * @return TriangleBumper [orientation=orientation, name=name, topLeft=topLeftcorner]
     */
    @Override public String toString() {
        return "TriangleBumper [orientation=" + orientation + ", name=" + name + ", topLeft=" + topLeft + "]";
    }
    
}
