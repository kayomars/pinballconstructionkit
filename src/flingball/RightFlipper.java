package flingball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Angle;
import physics.LineSegment;
import physics.Physics;
import physics.Vect;

/**
 * Mutable Gadget representing flipper turning clockwise
 *
 */
public class RightFlipper implements Gadget {
    
    private final Orientation orientation;
    private final String name;
    private final List<Gadget> triggers;
    private LineSegment flipper;
    private Angle curAngle;
    private SWEEP state;
    
    //Abstraction Function
    // AF(name, orientation, flipper, curAngle, state, triggers) = a right flipper with name name, 
    // orientation orientation (0/90/180/270), and a line segment representing the flipper. 
    // In simulation state is held by curAngle and is rotated until terminal 0/90 degree states are reached, 
    // activates the gadgets in triggers. 
    
    //Rep Invariant
    // Angle is between zero and ninety degrees
   
    private void checkRep() {
        assert curAngle.compareTo(Angle.ZERO) >= 0 && curAngle.compareTo(Angle.DEG_90) <= 0;
    }
    
    /**
     * Creates a new right Flipper gadget with top-right at (xPos, yPos)
     * with given orientation
     * 
     * @param name Gadget identifier
     * @param xPos x coordinate
     * @param yPos y coordinate
     * @param orientation angle which is one of 0, 90, 180, 270
     */
    public RightFlipper(String name, double xPos, double yPos, Orientation orientation) {
        this.name = name;
        this.orientation = orientation;      
        this.triggers = new ArrayList<Gadget>();   
        Vect pivot; Vect tail;
        
        //make the sides based on orientation 
        switch(orientation) {
        case DEG_0: 
            pivot = new Vect(xPos, yPos);
            tail = new Vect(xPos, yPos+2*Flingball.L);
            break;
        case DEG_90:
            pivot = new Vect(xPos+2*Flingball.L, yPos);
            tail = new Vect(xPos, yPos);
            break;
        case DEG_180:
            pivot = new Vect(xPos+2*Flingball.L, yPos+2*Flingball.L);
            tail = new Vect(xPos+2*Flingball.L, yPos);
            break;
        case DEG_270:
            pivot = new Vect(xPos, yPos+2*Flingball.L);
            tail = new Vect(xPos+2*Flingball.L, yPos+2*Flingball.L);
            break;
        default: 
            //should never get here
            pivot = new Vect(0, 0);
            tail = new Vect(0, 0);
        }
        flipper = new LineSegment(pivot, tail);
        curAngle = new Angle(0.);
        state = SWEEP.BDONE;
    }
    
    public enum Orientation {
        DEG_0, DEG_90, DEG_180, DEG_270
    }
    
    private enum SWEEP {
        FORWARD, BACK, FDONE, BDONE
    }
    
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public double timeToCollide(Ball ball, Double elapsedTime) {
        Angle diff;
        switch(state) {
        case FDONE:
        case BDONE:
            break;
        case FORWARD:
            diff = new Angle(6*Math.PI*elapsedTime);
            if (curAngle.plus(diff).compareTo(Angle.DEG_90) >= 0) {
                diff = Angle.DEG_90.minus(curAngle);
                state = SWEEP.FDONE;
                curAngle = Angle.DEG_90;
            } else {
                curAngle = curAngle.plus(diff);
            }
            flipper = Physics.rotateAround(flipper, flipper.p1(), diff);
            break;
        case BACK:
            diff = new Angle(6*Math.PI*elapsedTime);
            if (diff.compareTo(curAngle) >= 0) {
                diff = curAngle;
                state = SWEEP.BDONE;
                curAngle = Angle.ZERO;
            } else {
                curAngle = curAngle.minus(diff);
            }
            flipper = Physics.rotateAround(flipper, flipper.p1(), Angle.ZERO.minus(diff));
            break;
        }
        checkRep();
        if (state == SWEEP.FDONE || state == SWEEP.BDONE) {
            return Physics.timeUntilWallCollision(flipper, ball.getCircle(), ball.getVelocity());
        }
        else {
            return Physics.timeUntilRotatingWallCollision(flipper, flipper.p1(), 6*Math.PI, ball.getCircle(), ball.getVelocity());
        }
    }
      
    @Override
    public String collide(Ball ball) {
        if (state == SWEEP.FDONE || state == SWEEP.BDONE) {
            ball.setVelocity(Physics.reflectWall(flipper, ball.getVelocity(), 0.95));
        }
        else {
            ball.setVelocity(Physics.reflectRotatingWall(flipper, flipper.p1(), 6*Math.PI, ball.getCircle(), ball.getVelocity(), 0.95));
        }
        for (Gadget trigger: triggers) {
            trigger.action();
        }
        checkRep();
        return "";
    }
    
    @Override
    public void render(Graphics2D graphic) {
        graphic.setColor(Color.orange);
        graphic.setStroke(new BasicStroke((float) (.25*Flingball.L), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphic.draw(flipper.toLine2D()); 
    }
    
    @Override
    public String name() {
        return this.name;     
    }
    
    @Override
    public Vect position() {
        return flipper.p1();
    }
    
    @Override
    public void link(Gadget activator) {
        triggers.add(activator);
        checkRep();
    }
    
    @Override
    public void action() {
        switch(state) {
            case FDONE: 
                state = SWEEP.BACK;
            break;
            case BDONE: 
                state = SWEEP.FORWARD;
            break;
        default:
            break;
        }
    }
    
    /**
     * @return rightFlipper [orientation=orientation, name=name, flipper=flipper]
     */
    @Override public String toString() {
        return "rightFlipper [orientation=" + orientation + ", name=" + name + ", flipper=" + flipper + "]";
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((curAngle == null) ? 0 : curAngle.hashCode());
        result = prime * result + ((flipper == null) ? 0 : flipper.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((orientation == null) ? 0 : orientation.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
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
        RightFlipper other = (RightFlipper) obj;
        if (curAngle == null) {
            if (other.curAngle != null)
                return false;
        } else if (!curAngle.equals(other.curAngle))
            return false;
        if (flipper == null) {
            if (other.flipper != null)
                return false;
        } else if (!flipper.equals(other.flipper))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (orientation != other.orientation)
            return false;
        if (state != other.state)
            return false;
        if (triggers == null) {
            if (other.triggers != null)
                return false;
        } else if (!triggers.equals(other.triggers))
            return false;
        return true;
    }

}
