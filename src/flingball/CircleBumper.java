package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import physics.*;

/**
 * Implementation of gadget representing a circle bumper
 *
 */
public class CircleBumper implements Gadget{
    
    private final Circle bumper;
    private final Vect center;
    private final String name;
    private final Vect position;
    
    private final List<Gadget> triggers;
    
    //Abstraction Function
    // AF(bumper, center, name, position, triggers) = a circle bumper centered at center with name name and activating gadgets in triggers when triggered.
    //      The ball appears at position position when this bumper is triggered
    //      In the simulation this bumper appears as the circle bumper
    
    //Rep Invariant
    // center >=0
    // position >= 0
    // bumper.center = center

    private void checkRep() {
        assert center.x() >= 0;
        assert center.y() >= 0;
        assert position.x() >= 0;
        assert position.y() >= 0;
        assert bumper.getCenter() == center;
    }
    
    /**
     * Creates a new Circle Bumper gadget with top-left corner at (xPos, yPos)*L
     * 
     * @param name Gadget identifier
     * @param xPos x coordinate
     * @param yPos y coordinate
     */
    public CircleBumper(String name, double xPos, double yPos) {
        this.name = name;
        this.position = new Vect(xPos, yPos);
        this.center = new Vect(xPos+(double)Flingball.L/2.0, yPos+(double)Flingball.L/2.0);
        this.bumper = new Circle(this.center, (double)Flingball.L/2.0);
        this.triggers = new ArrayList<Gadget>();
    }

    @Override
    public double timeToCollide(Ball ball, Double elapsedTime) {
        return Physics.timeUntilCircleCollision(bumper, ball.getCircle(), ball.getVelocity());        
    }
      
    @Override
    public String collide(Ball ball) {
        //reflect ball
        ball.setVelocity(Physics.reflectCircle(this.center, ball.getCircle().getCenter(), ball.getVelocity())); 
        //activate triggers
        for (Gadget trigger: triggers) {
            trigger.action();
        }
        return "";
    }
    
    @Override
    public void render(Graphics2D graphic) {
        graphic.setColor(Color.red);
        graphic.fill(new Ellipse2D.Double(center.x()-bumper.getRadius(), 
                center.y()-bumper.getRadius(),
                2*bumper.getRadius(),  
                2*bumper.getRadius()));
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

    
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bumper == null) ? 0 : bumper.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CircleBumper other = (CircleBumper) obj;
        if (bumper == null) {
            if (other.bumper != null)
                return false;
        } else if (!bumper.equals(other.bumper))
            return false;
        return true;
    }
    
    @Override public String toString() {
        return "CircleBumper [center=" + center + ", name=" + name + "]";
    }

}
