package flingball;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import physics.*;
import physics.Physics.VectPair;

/**
 * The mutable Flingball ball. 
 */
public class Ball {
    private final String name;
    private double xPos; 
    private double yPos;
    private double xVel;
    private double yVel;
    private final double radius;
    
    /**
     * Creates new Flingball ball centered about (xPos, yPos) with
     * given velocity vector (xVel, yVel) and of specified radius
     * 
     * @param xPos x coordinate
     * @param yPos y coordinate
     * @param xVel x velocity
     * @param yVel y velocity
     */
    public Ball (String name, double xPos, double yPos, double xVel, double yVel) {
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVel = xVel;
        this.yVel = yVel;
        this.radius = .25*(double)Flingball.L;
    }
    
    //Abstraction Function
    // AF(name, xPos, yPos, xVel, yVel, radius) = ball with name name, at position (xPos, yPos), with velocity (xVel, yVel) and with radius radius
    
    //Rep Invariant
    // xPos >=0 
    // ypox >= 0
    // radius >= 0
    
    //Rep Exposure argument
    // all fields are private and immutable
    // xPos, yPos, xVel and yVel are not final but are doubles so cannot have aliases
    
    
    private void checkRep() {
        assert xPos >= 0;
        assert yPos >= 0;
        assert radius >= 0;
    }
    /**
     * @return the unique name for this ball
     */
    public String name() {
        return this.name;
    }
    
    /**
     * @return circle physics object associated with ball
     */
    public Circle getCircle() {
        return new Circle(this.xPos, this.yPos, this.radius); 
    }
    
    /**
     * @return the current position vector of the ball
     */
    public Vect getPosition() {
        return new Vect(this.xPos, this.yPos);      
    }
    
    /**
     * @return the current velocity vector of the ball
     */
    public Vect getVelocity() {
        return new Vect(this.xVel, this.yVel);       
    }
    
    /**
     * Sets the position of the ball
     * @param position of the ball
     */
    public void setPosition(Vect position) {
        this.xPos = position.x();
        this.yPos = position.y();
    }
    
    /**
     * Sets the velocity of the ball
     * @param velocity of the ball
     */
    public void setVelocity(Vect velocity) {
        this.xVel = velocity.x();
        this.yVel = velocity.y();
    }
    
    /**
     * Updates the position of the ball over the given time frame
     * @param elapsedTime 
     */
    public void updatePosition(double elapsedTime) {
        //new coordinate = current + velocity*time
        this.xPos = Math.min(Math.max(xPos + xVel*elapsedTime, radius), 20*(double)Flingball.L-radius);
        this.yPos = Math.min(Math.max(yPos + yVel*elapsedTime, radius), 20*(double)Flingball.L-radius);        
    }
    
    /**
     * Applies the deceleration of friction and gravity 
     * to the ball's velocity according to the formula
     * V_new = V_old × ( 1 - mu1 × delta_t - mu2 × |V_old| × delta_t) + gravity
     * @param gravity the downward acceleration of gravity given in units of L/sec^2
     * @param mu1 the first friction constant as defined in the spec
     * @param mu2 the second friction constant as defined in the spec
     * @param elapsedTime the amount of time to simulate. Equivalent to delta_t
     */
    public void applyMechanics(double gravity, double mu1, double mu2, double elapsedTime) { 
        //V_new = V_old × ( 1 - mu1 × delta_t - mu2 × |V_old| × delta_t) + gravity   
        double magnitude = Math.sqrt(xVel*xVel + yVel*yVel) / Flingball.L;
        this.xVel = xVel * (1 - mu1 * elapsedTime - mu2 * magnitude * elapsedTime);
        this.yVel = yVel * (1 - mu1 * elapsedTime - mu2 * magnitude * elapsedTime) + gravity*elapsedTime;
    }
    
    /**
     * Renders the ball onto the given graphics object
     * @param graphic the drawing buffer to render on
     */
    public void render(Graphics2D graphic) {
        graphic.setColor(Color.blue);
        graphic.fill(new Ellipse2D.Double(Math.max(xPos-radius, 0), 
                Math.max(yPos-radius, 0), 
                2*radius,  
                2*radius));
    }
    
    public double timeToCollide(Ball ball) {
        return Physics.timeUntilBallBallCollision(this.getCircle(), this.getVelocity(), ball.getCircle(), ball.getVelocity());        
    }

    public void collide(Ball ball) {
        VectPair reflectVels = Physics.reflectBalls(this.getCircle().getCenter(), 1., this.getVelocity(), ball.getCircle().getCenter(), 1., ball.getVelocity());
        this.setVelocity(reflectVels.v1);
        ball.setVelocity(reflectVels.v2);
    }   

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        long temp;
        temp = Double.doubleToLongBits(radius);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(xPos);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(xVel);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yPos);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yVel);
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
        Ball other = (Ball) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (Double.doubleToLongBits(radius) != Double.doubleToLongBits(other.radius))
            return false;
        if (Double.doubleToLongBits(xPos) != Double.doubleToLongBits(other.xPos))
            return false;
        if (Double.doubleToLongBits(xVel) != Double.doubleToLongBits(other.xVel))
            return false;
        if (Double.doubleToLongBits(yPos) != Double.doubleToLongBits(other.yPos))
            return false;
        if (Double.doubleToLongBits(yVel) != Double.doubleToLongBits(other.yVel))
            return false;
        return true;
    }

    @Override public String toString() {
        return "Ball [name=" + name + ", xPos=" + xPos + ", yPos=" + yPos + ", xVel=" + xVel + ", yVel=" + yVel
                + ", radius=" + radius + "]";
    }

}
