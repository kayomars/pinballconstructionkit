package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import physics.*;

/**
 * An implementation of Gadget that represents a square bumper
 *
 */
public class SquareBumper implements Gadget{
    
    private final LineSegment side1;
    private final LineSegment side2;
    private final LineSegment side3;
    private final LineSegment side4;
    private final String name;
    
    private List<Gadget> triggers;
    private List<LineSegment> sides;
    private List<Circle> corners;
    
    //Abstraction Function
    // AF(side1, side2, side3, side4, name, triggers) = a square bumper with name name that is represented in simulation by the line segments
    //   side1, side2, side3, side4, which are the top, right, bottom, left sides of the square. When this bumper is triggered, the gadgets in 
    //   triggers are activated. 
    
    //Rep Invariant
    // for side1, side2, side3, and side4, p1 and p2 >= 0

    //Rep Exposure Argument
    // all fields private final and immutable except triggers
    // triggers not returned to the client and contains no aliases as it is completely internal to the class
    // the gadgets in triggers may contain aliases outside the class, however we want the fields to update with any changes to these objects
    
    private void checkRep() {
        assert side1.p1().x() >= 0;
        assert side1.p2().x() >= 0;
        assert side2.p1().x() >= 0;
        assert side2.p2().x() >= 0;
        assert side3.p1().x() >= 0;
        assert side3.p2().x() >= 0;
        assert side4.p1().x() >= 0;
        assert side4.p2().x() >= 0;
        
        assert side1.p1().y() >= 0;
        assert side1.p2().y() >= 0;
        assert side2.p1().y() >= 0;
        assert side2.p2().y() >= 0;
        assert side3.p1().y() >= 0;
        assert side3.p2().y() >= 0;
        assert side4.p1().y() >= 0;
        assert side4.p2().y() >= 0;
    }
   
    /**
     * Creates a new Square Bumper gadget with top-left corner about the (xPos, yPos)*L coordinate
     * 
     * @param name Gadget identifier
     * @param xPos x coordinate
     * @param yPos y coordinate
     */
    public SquareBumper(String name, double xPos, double yPos) {
        this.name = name;  
        
        Vect corner1 = new Vect(xPos, yPos);
        Vect corner2 = new Vect(xPos+Flingball.L, yPos);
        Vect corner3 = new Vect(xPos+Flingball.L, yPos+Flingball.L);
        Vect corner4 = new Vect(xPos, yPos+Flingball.L);
        this.corners = new ArrayList<>();
        this.corners.addAll(Arrays.asList(new Circle(corner1,.05*Flingball.L), new Circle(corner2,.05*Flingball.L), 
                new Circle(corner3,.05*Flingball.L), new Circle(corner4, .05*Flingball.L)));
        
        this.side1 = new LineSegment(corner1, corner2);
        this.side2 = new LineSegment(corner2, corner3);
        this.side3 = new LineSegment(corner3, corner4);
        this.side4 = new LineSegment(corner4, corner1);
        this.sides = new ArrayList<LineSegment>();
        this.sides.addAll(Arrays.asList(side1, side2, side3, side4));
        
        this.triggers = new ArrayList<Gadget>();
    }

    @Override
    public double timeToCollide(Ball ball, Double elapsedTime) {
        //returns min time that the ball collides with either of the four walls
        double minTime = Double.POSITIVE_INFINITY;
        for (LineSegment side: sides) {
            double sideTime = Physics.timeUntilWallCollision(side, ball.getCircle(), ball.getVelocity());
            if (sideTime < minTime) {
                minTime = sideTime;
            }
        }
        for (Circle corner: corners) {
            double cornerTime = Physics.timeUntilCircleCollision(corner, ball.getCircle(), ball.getVelocity());
            if (cornerTime < minTime) {
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
        graphic.setColor(Color.red);
        graphic.fill(new Rectangle2D.Double(this.side1.p1().x(), 
                this.side1.p1().y(),
                Flingball.L,  
                Flingball.L));
    }
    
    @Override
    public String name() {
        return this.name;        
    }


    @Override public String toString() {
        return "SquareBumper [name=" + name + "]";
    }

    @Override
    public Vect position() {
        return this.side1.p1();
    }
    
    @Override
    public void link(Gadget activator) {
        checkRep();
        triggers.add(activator);               
    }
    
    @Override
    public void action() {
        //by the spec, nothing should happen here
    }


    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((side1 == null) ? 0 : side1.hashCode());
        result = prime * result + ((side2 == null) ? 0 : side2.hashCode());
        result = prime * result + ((side3 == null) ? 0 : side3.hashCode());
        result = prime * result + ((side4 == null) ? 0 : side4.hashCode());
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
        SquareBumper other = (SquareBumper) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (side1 == null) {
            if (other.side1 != null)
                return false;
        } else if (!side1.equals(other.side1))
            return false;
        if (side2 == null) {
            if (other.side2 != null)
                return false;
        } else if (!side2.equals(other.side2))
            return false;
        if (side3 == null) {
            if (other.side3 != null)
                return false;
        } else if (!side3.equals(other.side3))
            return false;
        if (side4 == null) {
            if (other.side4 != null)
                return false;
        } else if (!side4.equals(other.side4))
            return false;
        if (triggers == null) {
            if (other.triggers != null)
                return false;
        } else if (!triggers.equals(other.triggers))
            return false;
        return true;
    }
    

}
