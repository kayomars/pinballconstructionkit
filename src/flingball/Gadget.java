package flingball;
import java.awt.Graphics2D;

import physics.*;

/**
 * An immutable data type representing a gadget. 
 * Every gadget has an (x,y) location, where x and y are integers in [0,19].
 * Every gadget has a width and height, also integers in [0,19]. 
 * Some gadgets have a fixed width and height (like bumpers), 
 * and others have a configurable width and height (like absorbers).
 * Some gadgets have a coefficient of reflection, which is a multiplier applied 
 * to the magnitude of the ball’s velocity after it bounces off the gadget. 
 * Some gadgets have an orientation (clockwise), 
 * which determines how the gadget is rotated from its default orientation.
 * Each gadget may have a trigger and an action.
 * A trigger is an event that happens at the gadget, such as a ball colliding with it. 
 * An action is a response that a gadget can make to a trigger happening somewhere on the board.
 * Actions and triggers are mediated by the trigger handler.
 */
public interface Gadget {   
        
    /**
     * Get time remaining till ball collides with the gadget
     * 
     * @param ball the time remaining is in reference to
     * @param elapsedTime TODO
     * @return time in ms till collision
     */
    public double timeToCollide(Ball ball, Double elapsedTime);
    
    /**
     * Perform trigger events and update ball appropriately
     * 
     * @param ball that the gadget collides with
     * @return TODO
     */
    public String collide(Ball ball);
    
    /**
     * Renders the ball onto the given graphics object
     * @param graphic the drawing buffer to render on
     */
    public void render(Graphics2D graphic);
        
    /**
     * Gets the gadget name
     * 
     * @return Gadget name
     */
    public String name();
    
    /**
     * Get the position of a Gadget
     * @return the coordinates of the top-left of the gadget on the board
     */
    public Vect position();

    
    /**
     * Adds an absorber that will be activated every time a ball collides
     * with this object 
     * @param activator the absorbed that will be activated
     */
    public void link(Gadget activator);
    
    /**
     * Triggers this gadget's action, and trigger other gadgets linked to this trigger
     */
    public void action();
    
    /**
     * @param that any object
     * @return true if and only if this and that are structurally-equal
     */
    @Override
    public boolean equals(Object that);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Gadget,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
    
    /**
     * @return human-readable representation of the gadget
     */
    @Override
    public String toString();

}
