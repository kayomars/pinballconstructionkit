package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import physics.Vect;

/**
 * The Flingball board.
 * 
 * Represents the Flingball board and contains all of the gadgets and balls.
 * Computes frame updates.  
 * 
 * The board size is 20L * 20L and contains 4 walls surrounding this area
 * 
 */
public class Board {
    private final List<Gadget> gadgets;
    private final CopyOnWriteArrayList<Ball> balls;
    private final double gravity;
    private final double mu1;
    private final double mu2;
    private final Map<String, String> keyMapping;
    private final String[] boardJoins;
    private final Set<String> activeBoards;
    private final String boardName;
    private final Map<String, Vect> portalMap;

    private StateChangeListener listener;

    // Abstraction Function
    // AF(gadgets, balls, gravity, mu1, mu2, keyMapping, boardJoins, activeBoards, boardName, portalMap) = a board with a name, containing all its gadgets
    //          in a list of gadgets, all its balls in a list of balls, with one gravity constant two friction constants. It also holds a mapping of keys 
    //          to the name of the gadget it should invoke action in, and an array of name of boards that it might be joined with. It keeps track of all
    //          activeBoards in a server run game of fling ball in a set. It holds a mapping of its portal name to its position.

    // Rep Invariant
    //  N/A

    // Rep Exposure argument
    // All fields private and final
    // boardName, gravity, mu1, mu2 are immutable
    // Gadgets and balls are mutable but are not returned to the client
    // Gadget and balls may contain aliases but we want to update based on changes in these arrays
    // keyMapping is mutable but only copies of it are returned
    // boardJoins and activeBoards are mutable but this is required and is done in a safe manner
    // portalMap is mutable, but a reference to it is never returned
    
    // Thread Safety Argument
    // Confinement:
    //  - gadgets, keyMapping, portalMap are confined to a single thread and are safe from race conditions
    //  - no global variables have been used
    // Immutability:
    //  - boardName, gravity, mu1, mu2 are immutable
    //  - all fields here have immutable references
    //  ThreadSafe Data Types:
    //  - balls is using a thread safe data type
    //  Other:
    //  - boardJoins and activeBoards may be mutated by different threads. However, all actions are atomic so they dont permit
    //        race conditions. Additionally, none of these mutations threaten the internal rep of these data types.
    
    

    private void checkRep() {
        assert (true);
    }

    /**
     * Creates a new board containing gadgets in gadgetList and 
     * balls in ballList
     * @param gadgetList list of gadgets
     * @param ballList list of balls
     * @param gravity the downward acceleration of gravity given in units of L/sec^2
     * @param mu1 the first friction constant as defined in the Ball spec
     * @param mu2 the second friction constant as defined in the Ball spec
     */
    public Board(String boardName, List<Gadget> gadgetList, List<Ball> ballList, double gravity, 
            double mu1, double mu2, Map<String, String> keyMappings, Map<String, Vect> portalMap) {
        this.gadgets = new ArrayList<>(gadgetList);
        gadgets.add(new Wall("0", 0, 0, 20*Flingball.L, 0)); //North
        gadgets.add(new Wall("1", 20*Flingball.L, 0, 20*Flingball.L, 20*Flingball.L)); //East
        gadgets.add(new Wall("2", 0, 20*Flingball.L, 20*Flingball.L, 20*Flingball.L)); //South
        gadgets.add(new Wall("3", 0, 0, 0, 20*Flingball.L)); // West
        this.balls = new CopyOnWriteArrayList<>(ballList);
        this.gravity = gravity;
        this.mu1 = mu1;
        this.mu2 = mu2;
        this.keyMapping = new HashMap<>(keyMappings);
        this.boardJoins = new String[]{"","","", ""}; // [N, E, S, W]
        this.activeBoards = new HashSet<String>();
        this.boardName = boardName;
        this.portalMap = portalMap;
    }

    /**
     * Updates the configuration of the board after one time step.
     * 
     *   * Calculates new positions of balls based on their current position and current speed,
     *     and taking into account any collisions that can happen during the timestep.
     *   * Calculates new speed of balls based on friction and gravity.
     * 
     * @param frameTime length of a frame in milliseconds (1/framerate)
     */
    public void timeStep(final double frameTime) {
        double elapsedTime = 0;
        double timeDiff = frameTime;
        while (elapsedTime < frameTime) {
            Map<Gadget, Ball>  collidingGadgetsBalls = new HashMap<>();
            Map<Ball, Ball> collidingBallsBalls = new HashMap<>();
            double minimumCollisionTime = Double.POSITIVE_INFINITY;
            // Survey ball -> gadget collisions
            for (Ball ball : balls) {
                for (Gadget gadget : gadgets) {
                    double collisionTime = gadget.timeToCollide(ball, timeDiff);
                    if (collisionTime < minimumCollisionTime) {
                        minimumCollisionTime = collisionTime;
                        collidingGadgetsBalls.clear();
                        collidingGadgetsBalls.put(gadget, ball);
                    }
                    if (collisionTime == minimumCollisionTime) {
                        collidingGadgetsBalls.put(gadget, ball);
                    }
                }
            }
            // Survey ball -> ball collisions
            for (Ball ball : balls) {
                for (Ball ball2 : balls) {
                    double collisionTime = ball2.timeToCollide(ball);
                    if (collisionTime < minimumCollisionTime) {
                        minimumCollisionTime = collisionTime;
                        collidingGadgetsBalls.clear();
                        collidingBallsBalls.clear();
                        collidingBallsBalls.put(ball, ball2);
                    }
                    if (collisionTime == minimumCollisionTime) {
                        if (!collidingBallsBalls.containsKey(ball2)) {
                            collidingBallsBalls.put(ball, ball2);
                        }
                    }
                }
            }
            // Check if collisions can be processed within this frame
            double minTime = frameTime - elapsedTime;
            boolean collision = false;
            if (minimumCollisionTime < minTime) {
                minTime = minimumCollisionTime;
                collision = true;
            }
            for (Ball ball : balls) {
                ball.updatePosition(minTime);                
            }
            // Process minimum time collisions and teleport if necessary
            if (collision) {
                if (!collidingGadgetsBalls.isEmpty()) {
                    for (Map.Entry<Gadget, Ball> gb : collidingGadgetsBalls.entrySet()) {
                        Ball ball = gb.getValue();
                        Vect priorVelocity = ball.getVelocity();
                        String teleport = gb.getKey().collide(gb.getValue());
                        if (!teleport.isEmpty() && !activeBoards.isEmpty()) {
                            String teleArray[] = teleport.split(" ", 2);
                            if (teleArray.length == 2 && teleArray[0].isEmpty()) {
                                ball.setPosition(portalMap.get(teleArray[1]));
                            } else if (teleArray.length == 2 && activeBoards.contains(teleArray[0])) {
                                this.broadcastEvent("portalBall" + " " + this.boardName + 
                                        " " + teleArray[0]  + 
                                        " " + teleArray[1] + 
                                        " " + priorVelocity.x() + 
                                        " " + priorVelocity.y());
                                balls.remove(ball);
                            } else if (teleArray.length == 1) {
                                int dir = Integer.parseInt(teleArray[0]);
                                if (!boardJoins[dir].isEmpty()) {
                                    this.broadcastEvent("passBall" + " " + this.boardName + 
                                            " " + boardJoins[dir]  + 
                                            " " + ball.getPosition().x() + 
                                            " " + ball.getPosition().y() + 
                                            " " + priorVelocity.x() + 
                                            " " + priorVelocity.y());
                                    balls.remove(ball);
                                }
                            }
                        }
                    }
                }
                if (!collidingBallsBalls.isEmpty()) {
                    for (Map.Entry<Ball,Ball> bb : collidingBallsBalls.entrySet()) {
                        bb.getKey().collide(bb.getValue());                
                    }
                }
            }
            elapsedTime += minTime;
            timeDiff = minTime;
        }
        for (Ball ball : balls) {           
            ball.applyMechanics(gravity, mu1, mu2, frameTime);
        }
        checkRep();
    }


    /**
     * Renders the board for the animator
     * @param graphic the drawing buffer to render on
     */
    public void render(Graphics2D graphic) {
        for (Ball ball: balls) {
            ball.render(graphic);
        }
        for (Gadget gadget : gadgets) {
            gadget.render(graphic);
        }
        graphic.setColor(Color.white);
        if (!boardJoins[0].isEmpty()) { //North
            AffineTransform at = AffineTransform.getQuadrantRotateInstance(0);
            graphic.setTransform(at);
            graphic.drawString(boardJoins[0], 8*Flingball.L, 1*Flingball.L);
        }
        if (!boardJoins[1].isEmpty()) { //East
            AffineTransform at = AffineTransform.getQuadrantRotateInstance(1);
            graphic.setTransform(at);
            graphic.drawString(boardJoins[1], 8*Flingball.L, -19*Flingball.L);
        }
        if (!boardJoins[2].isEmpty()) { //South
            AffineTransform at = AffineTransform.getQuadrantRotateInstance(0);
            graphic.setTransform(at);
            graphic.drawString(boardJoins[2], 8*Flingball.L, 19*Flingball.L);
        }
        if (!boardJoins[3].isEmpty()) { //West
            AffineTransform at = AffineTransform.getQuadrantRotateInstance(3);
            graphic.setTransform(at);
            graphic.drawString(boardJoins[3], -12*Flingball.L, 1*Flingball.L);
        }
    }

    /**
     * Adds a ball to the board, used during wall and portal teleportation 
     * @param ball
     */
    public void addBall(Ball ball) {
        balls.add(ball);
        checkRep();
    }

    /**
     * Add an active board to our activeBoards list
     * @param bName name of board that has become active
     */
    public void addActiveBoard(String bName) {
        activeBoards.add(bName);
    }
    
    /**
     * Remove an active board from our activeBoards list
     * @param bName name of board that has should be removed
     */
    public void removeActiveBoard(String bName) {
        activeBoards.remove(bName);
    }

    /**
     * Add board name to joinBoard list at a certain index depending on the direction
     * @param bName name of board
     * @param dir N, E, S, W directionality
     */
    public void concatBoard(String bName, String dir) {
        if (dir.equals("N")) {
            boardJoins[0] = bName;
        } else if (dir.equals("E")) {
            boardJoins[1] = bName;

        } else if (dir.equals("S")) {
            boardJoins[2] = bName;
        } else {
            boardJoins[3] = bName;
        }
    }

    /**
     * Remove joined board at particular index 
     * @param index at which the board is stored
     */
    public void removeConcatBoard(int index) {
        boardJoins[index] = "";
    }
    
    /**
     * Return a copy of boardJoins list
     * @return a copy of boardJoins
     */
    public String[] getBoardJoins() {
        String[] copyBoardJoins = boardJoins.clone();
        return copyBoardJoins;
    }

    /**
     * Broadcasts teleportation event up to server using callback
     */
    public void broadcastEvent(String str) {
        listener.notifyStateChange(str);
    }

    /**
     * Attach listener for board events to board
     */
    public void attachListener(StateChangeListener callback) {
        listener = callback;
        checkRep();
    }

    /**
     * Offers an unmodifiable view of the key mapping map
     * @return a view of the keys map
     */
    public Map<String, String> getKeyMapping() {  
        Map<String, String> neededMap = Collections.unmodifiableMap(keyMapping);
        return neededMap;
    }


    /**
     * Returns a copy of portal map
     * @return a copy of portal map
     */
    public Map<String, Vect> getPortalMap() {
        return new HashMap<String, Vect>(portalMap);
    }

    /**
     * Returns a copy of the list of gadgets
     * @return copy of gadget list
     */
    public List<Gadget> getGadgets() {
        return new ArrayList<Gadget>(gadgets);
    }

    /**
     * Takes in a list of key presses in the format dictated in the parser. It will search through the mapping
     * of key presses to gadget, and if there is a match, will trigger the action method of that gadget. If a match 
     * is not found, it does nothing.
     * @param keyPresses the list of key presses 
     */
    public void callActionOnGadgets(List<String> keyPresses) {

        for (String keyPress: keyPresses) {

            if (keyMapping.containsKey(keyPress)) {

                String nameOfGadget = keyMapping.get(keyPress);

                for (Gadget curGadget: gadgets) {
                    if (curGadget.name().equals(nameOfGadget)) {
                        curGadget.action();
                        break;
                    }
                }
            }      
        }
        keyPresses.clear();

    }

    /**
     * Returns the board's name
     * @return board's name
     */
    public String getBoardName() {
        return boardName;
    }

    /**
     * @param that any object
     * @return true if and only if this and that are structurally-equal
     */
    @Override
    public boolean equals(Object that) {
        return that instanceof Board && this.sameValue((Board) that);

    }

    private boolean sameValue(Board that) {
        Set<Gadget> thisGadgets = new HashSet<>(gadgets);
        Set<Gadget> thatGadgets = new HashSet<>(that.gadgets);
        Set<Ball> thisBall = new HashSet<>(balls);
        Set<Ball> thatBall = new HashSet<>(that.balls);
        return thisGadgets.equals(thatGadgets) &&
                thisBall.equals(thatBall) &&
                this.mu1 == that.mu1 &&
                this.mu2 == that.mu2 &&
                this.gravity == that.gravity;          
    }

    /**
     * @return Board [gadgets= gadgets, balls=balls, gravity=gravity, mu1=mu1, mu2=mu2]
     */
    @Override public String toString() {
        return "Board [gadgets=" + gadgets + ", balls=" + balls + ", gravity=" + gravity + ", mu1=" + mu1 + ", mu2="
                + mu2 + "]";
    }

    @Override public int hashCode() {
        return (int) (gadgets.hashCode() + balls.hashCode() + gravity + mu1 +mu2);
    }




}
