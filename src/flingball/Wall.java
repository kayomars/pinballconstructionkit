package flingball;


import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.*;

//An implementation of gadget representing a wall
public class Wall implements Gadget{
    
    private final String name;
    private final LineSegment wall;
    private final Circle startCorner;
    private final Circle endCorner;
    private final Vect startPos;
    private final Vect endPos;
    
    private final List<Gadget> triggers;
    
    //Abstraction Function
    // AF(name, wall, startCorner, endCorner, startPos, endPos) = a wall with name name, represented in simulation by the line segment wall 
    //     starting at startCorner at position startPos and ending at endCorner at position endPos
    
    //Rep Invariant
    // startPos >= 0
    // endPos >= 0
    // startCorner.center = startPos
    // endCorner.center = endPos
    
    //Rep Exposure Argument
    // all fields private, final and immutable except for triggers
    // triggers is not returned to the client and contains no aliases as it is internal to the class
    // the gadgets in triggers contain aliases, but we want to update triggers as these gadgets are modified outside the class (they shouldn't be anyway)
    
    private void checkRep() {
        assert startPos.x() >= 0;
        assert endPos.x() >= 0;
        assert startPos.y() >= 0;
        assert endPos.y() >= 0;
        assert startCorner.getCenter().equals(startPos);
        assert startCorner.getCenter().equals(endPos);
    }

    /**
     * Creates a new wall gadget spanning (x1, y1) to (x2, y2)
     * 
     * @param name Gadget identifier
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     */
    public Wall(String name, int x1, int y1, int x2, int y2) {
        this.name = name;
        this.startPos = new Vect(x1, y1);
        this.endPos = new Vect(x2, y2);
        this.startCorner = new Circle(startPos, 0);
        this.endCorner = new Circle(endPos, 0);
        this.wall = new LineSegment(startPos, endPos);  
        this.triggers = new ArrayList<>();
    }

    @Override
    public double timeToCollide(Ball ball, Double elapsedTime) {
        //returns the min time that the ball collides with either of the corners or the wall
        return Math.min(
                Math.min(Physics.timeUntilWallCollision(wall, ball.getCircle(), ball.getVelocity()),
                        Physics.timeUntilCircleCollision(startCorner, ball.getCircle(), ball.getVelocity())),
                Physics.timeUntilCircleCollision(endCorner, ball.getCircle(), ball.getVelocity()));
    }
      
    @Override
    public String collide(Ball ball) {
        double timeToCollide = timeToCollide(ball, 0.);
        Vect newVelocity;
        if (timeToCollide == Physics.timeUntilWallCollision(wall, ball.getCircle(), ball.getVelocity())){
            newVelocity = Physics.reflectWall(wall, ball.getVelocity());  
        } else if (timeToCollide == Physics.timeUntilCircleCollision(startCorner, ball.getCircle(), ball.getVelocity())) {
            newVelocity = Physics.reflectCircle(startCorner.getCenter(), ball.getCircle().getCenter(), ball.getVelocity());
        } else {
            newVelocity = Physics.reflectCircle(endCorner.getCenter(), ball.getCircle().getCenter(), ball.getVelocity());
        }
        ball.setVelocity(newVelocity);
        return name;
    }
    
    @Override
    public void render(Graphics2D graphic) {
        return;        
    }
    
    @Override
    public String name() {
         return name;       
    }
    
    @Override
    public Vect position() {
        return new Vect(startPos.x() - endPos.x(), startPos.y() - endPos.y());
    }

    @Override
    public void link(Gadget activator) {
        checkRep();
        this.triggers.add(activator);         
    }

    @Override
    public void action() {
        //by spec, nothing should happen 
    }


    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Wall other = (Wall) obj;
        if (wall == null) {
            if (other.wall != null)
                return false;
        } else if (!wall.equals(other.wall))
            return false;
        return true;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((wall == null) ? 0 : wall.hashCode());
        return result;
    }
    
    @Override
    /**
     * @return WallName and two end positions 
     */
    public String toString() {
        return "Wall name=" + this.name + " start="+ startCorner.toString() + " end="+ endCorner.toString();
    }

}
