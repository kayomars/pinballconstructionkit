package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import physics.*;

/**
 * Implementation of gadget representing a portal. 
 * Used to teleport ball to other portals, possibly on different boards
 *
 */
public class Portal implements Gadget{
    
    private final Circle portal;
    private final Vect center;
    private final String name;
    private final Vect position;
    private final String otherBoard;
    private final String otherPortal;
    
    private final List<Gadget> triggers;
    
    //Abstraction Function
    // AF(portal, center, name, position, triggers, otherBoard, otherPortal) = 
    //      a portal centered at center with name name and activating gadgets in triggers when triggered.
    //      The ball appears at position position when this bumper is triggered.
    //      In the simulation this bumper appears as a portal.
    //      Collisions return destination board otherBoard and destination portal otherPortal
    
    // Rep Invariant
    // center >=0
    // position >= 0
    // portal.center = center

    private void checkRep() {
        assert center.x() >= 0;
        assert center.y() >= 0;
        assert position.x() >= 0;
        assert position.y() >= 0;
        assert portal.getCenter() == center;
    }
    
    /**
     * Creates a new Circle Bumper gadget with top-left corner at (xPos, yPos)*L
     * 
     * @param name Gadget identifier
     * @param xPos x coordinate
     * @param yPos y coordinate
     */
    public Portal(String name, double xPos, double yPos, String otherBoard, String otherPortal) {
        this.name = name;
        this.position = new Vect(xPos, yPos);
        this.center = new Vect(xPos+(double)Flingball.L/2.0, yPos+(double)Flingball.L/2.0);
        this.portal = new Circle(this.center, (double)Flingball.L/2.0);
        this.triggers = new ArrayList<Gadget>();
        this.otherBoard = otherBoard;
        this.otherPortal = otherPortal;
    }

    @Override
    public double timeToCollide(Ball ball, Double elapsedTime) {
        return Math.max(Physics.timeUntilCircleCollision(portal, ball.getCircle(), ball.getVelocity()), .005);        
    }
      
    @Override
    public String collide(Ball ball) {
        //activate triggers
        for (Gadget trigger: triggers) {
            trigger.action();
        }
        return otherBoard + " " + otherPortal;
    }
    
    @Override
    public void render(Graphics2D graphic) {
        graphic.setColor(Color.cyan);
        graphic.fill(new Ellipse2D.Double(center.x()-portal.getRadius(), 
                center.y()-portal.getRadius(),
                2*portal.getRadius(),  
                2*portal.getRadius()));
    }
    
    @Override
    public String name() {
        return this.name;       
    }
    
    @Override
    public Vect position() {
        return position;
    }
    
    @Override
    public void link(Gadget activator) {
        checkRep();
        triggers.add(activator);               
    }
    
    @Override
    public void action() {
        //by the spec, nothing happens
    }

    @Override public String toString() {
        return "Portal [bumper=" + portal + ", center=" + center + ", name=" + name + ", position=" + position
                + ", otherBoard=" + otherBoard + ", otherPortal=" + otherPortal + ", triggers=" + triggers + "]";
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((portal == null) ? 0 : portal.hashCode());
        result = prime * result + ((center == null) ? 0 : center.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((otherBoard == null) ? 0 : otherBoard.hashCode());
        result = prime * result + ((otherPortal == null) ? 0 : otherPortal.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
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
        Portal other = (Portal) obj;
        if (portal == null) {
            if (other.portal != null)
                return false;
        } else if (!portal.equals(other.portal))
            return false;
        if (center == null) {
            if (other.center != null)
                return false;
        } else if (!center.equals(other.center))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (otherBoard == null) {
            if (other.otherBoard != null)
                return false;
        } else if (!otherBoard.equals(other.otherBoard))
            return false;
        if (otherPortal == null) {
            if (other.otherPortal != null)
                return false;
        } else if (!otherPortal.equals(other.otherPortal))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (triggers == null) {
            if (other.triggers != null)
                return false;
        } else if (!triggers.equals(other.triggers))
            return false;
        return true;
    }
        
}
