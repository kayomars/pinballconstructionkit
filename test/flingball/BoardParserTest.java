package flingball;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.mit.eecs.parserlib.UnableToParseException;
import physics.Vect;

/**
 * Tests for BoardParser.
 */
public class BoardParserTest {

    /**
     * Testing Strategy
     * 
     * Parse
     * Input partitions:
     * - board w/O & w acceleration modifiers (mu1, mu2, gravity) 
     * - 0 balls
     * - 1 ball
     * - >1 balls
     * - 0 gadgets
     * - unique gadgets (1 of each individual gadget): 
     *   SquareBumper, CircleBumper, TriangleBumper (orientation 0, 90, 180, 270), Absorber,
     *   leftFlipper(orientation 0, 90, 180, 270), rightFlipper(orientation 0, 90, 180, 270),
     *   portal(with other board, without other board)
     * - >1 gadgets 
     * - 0 trigger/actions between gadgets
     * - 1 trigger/actions between gadgets
     * - >1 trigger/actions between gadgets
     * - 1 keydown press with link to gadget
     * - >1 keydown press with link to gadget
     * - 1 keyup release with link to gadget
     * - >1 keyup release with link to gadget
     * 
     * 
     * Conditions:
     * Extra whitespace, comments
     * 
     * NOTE: Overall testing schematics of the graphics for collisions will be tested manually
     * via the graphics 
     * 
     */
    
    private static final String BOARD = "board name=Absorber\n";
    private static final String BOARD_G = "board name=Absorber gravity = 50.0\n";
    private static final String BOARD_1 = "board name=Absorber friction1 = .050\n";
    private static final String BOARD_2 = "board name=Absorber friction2 = .050 \n";
    private static final String BOARD_G1 = "board name=Absorber gravity = 50.0 friction1 = .050\n";
    private static final String BOARD_G2 = "board name=Absorber gravity = 50.0 friction2 = .050\n";
    private static final String BOARD_12 = "board name=Absorber friction1 = .050 friction2 = .050\n";
    private static final String BOARD_G12 = "board name=Absorber gravity = 50.0 friction1 = .050 friction2 = .050\n";
    private static final String BALL_A = "ball name=BallA x=10.25 y=15.25 xVelocity=0 yVelocity=0\n";
    private static final String BALL_B = "ball name=BallB x=19.25 y=3.25 xVelocity=0 yVelocity=0\n";
    private static final String SQUARE_BUMPER = "squareBumper name=SquareA x=0 y=14\n";
    private static final String CIRCLE_BUMPER = "circleBumper name=CircleA x=1 y=10 \n";
    private static final String TRIANGLE_BUMPER = "triangleBumper name=Tri x=19 y=0 orientation=90 \n";
    private static final String ABSORBER = "absorber name=Abs1 x=0 y=18 width=10 height=2\n";
    private static final String TRIGGER_ACTION_CIRCLE = "fire trigger=CircleA action=Abs1\n";
    private static final String TRIGGER_ACTION_ABSORBER = "fire trigger=Abs1 action=Abs1\n";
    private static final String COMMENT = "# this is a comment\n";
    private static final String WHITESPACE = "\t\r";
    private static final String LEFTFLIP = "leftFlipper name=Lef x=7 y=7 orientation=90\n";
    private static final String RIGHTFLIP = "rightFlipper name=Rig x=3 y=2 \n";
    private static final String KEYUP = "keyup key=space action=Abs1 \n";
    private static final String KEYUP_2 = "keyup key=shift action=Lef1 \n";
    private static final String KEYDOWN = "keydown key=space action=Abs1 \n";
    private static final String KEYDOWN_2 = "keydown key=left action=Lef1 \n";
    private static final String PORTAL_WITHOUT = "portal name=Alpha x=5 y=7 otherPortal=Beta \n";
    private static final String PORTAL_WITH = "portal name=Beta x=15 y=7 otherBoard=Mercury otherPortal=Gamma \n";
    private static final double L = 20.;
    private static final double GRAVITY = 25.0*L;
    private static final double MU = .025;
    private static final double GRAVITY_MOD = 50.0*L;
    private static final double MU_MOD = .05;

    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // board w/o acceleration, 0 balls, 0 gadgets, 0 trigger/actions
    @Test
    public void testEmptyBoard() throws UnableToParseException {
        String input = BOARD;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName", gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // Comments
    @Test
    public void testComments() throws UnableToParseException {
        String input = COMMENT+BOARD+COMMENT;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName", gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // Whitespace
    @Test
    public void testWhitespace() throws UnableToParseException {
        String input = WHITESPACE+BOARD+WHITESPACE;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName", gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
        
    // board w/ gravity
    @Test
    public void testGravityBoard() throws UnableToParseException {
        String input = BOARD_G;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName", gadgetList, ballList, GRAVITY_MOD, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }

    // board w/ mu1
    @Test
    public void testMu1Board() throws UnableToParseException {
        String input = BOARD_1;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName", gadgetList, ballList, GRAVITY, MU_MOD, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }

    // board w/ mu2
    @Test
    public void testMu2Board() throws UnableToParseException {
        String input = BOARD_2;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName", gadgetList, ballList, GRAVITY, MU, MU_MOD, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // board w/ gravity and mu1
    @Test
    public void testGravityMu1Board() throws UnableToParseException {
        String input = BOARD_G1;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName", gadgetList, ballList, GRAVITY_MOD, MU_MOD, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // board w/ gravity and mu2
    @Test
    public void testGravityMu2Board() throws UnableToParseException {
        String input = BOARD_G2;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName", gadgetList, ballList, GRAVITY_MOD, MU, MU_MOD, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);

    }
    
    // board w/ mu1 and mu2
    @Test
    public void testMu1Mu2Board() throws UnableToParseException {
        String input = BOARD_12;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU_MOD, MU_MOD, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // board w/ gravity, mu1, and mu2
    @Test
    public void testGravityMu1Mu2Board() throws UnableToParseException {
        String input = BOARD_G12;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY_MOD, MU_MOD, MU_MOD, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // 1 ball
    @Test
    public void testSingleBall() throws UnableToParseException {
        String input = BOARD+BALL_A;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        ballList.add(new Ball("BallA", 10.25*L, 15.25*L, 0., 0.));
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // >1 balls
    @Test
    public void testManyBalls() throws UnableToParseException {
        String input = BOARD+BALL_A+BALL_B;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        List<Ball> ballList = new ArrayList<>();
        ballList.add(new Ball("BallA", 10.25*L, 15.25*L, 0., 0.));
        ballList.add(new Ball("BallB", 19.25*L, 3.25*L, 0., 0.));
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // SquareBumper
    @Test
    public void testSquareBumper() throws UnableToParseException {
        String input = BOARD+SQUARE_BUMPER;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new SquareBumper("SquareA", 0.*L, 14.*L));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }

    // CircleBumper
    @Test
    public void testCircleBumper() throws UnableToParseException {
        String input = BOARD+CIRCLE_BUMPER;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new CircleBumper("CircleA", 1*L, 10*L));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);

    }
   
    // TriangleBumper
    @Test
    public void testTriangleBumper() throws UnableToParseException {
        String input = BOARD+TRIANGLE_BUMPER;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new TriangleBumper("Tri", 380., 0., TriangleBumper.Orientation.DEG_90));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
  
    // Absorber
    @Test
    public void testAbsorber() throws UnableToParseException {
        String input = BOARD+ABSORBER;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new Absorber("Abs1", 0*L, 18*L, 10*L, 2*L));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // LeftFlipper
    @Test
    public void testLeftFlipper() throws UnableToParseException {
        String input = BOARD+LEFTFLIP;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new LeftFlipper("Lef", 7*L, 7*L, LeftFlipper.Orientation.DEG_90));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
     //RightFlipper
    @Test
    public void testRightFlipper() throws UnableToParseException {
        String input = BOARD+RIGHTFLIP;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new RightFlipper("Rig", 3*L, 2*L, RightFlipper.Orientation.DEG_0));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // KeyUp
    @Test
    public void testKeyUp() throws UnableToParseException {
        String input = BOARD+ABSORBER+KEYUP;
        Board actual = BoardParser.parse(input);

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Releasespace", "Abs1");
        
        assertEquals(expectedMap, actual.getKeyMapping());
    }
    
    // KeyUp, where num > 1
    @Test
    public void testKeyUpMultiple() throws UnableToParseException {
        String input = BOARD+ABSORBER+LEFTFLIP+KEYUP+KEYUP_2;
        Board actual = BoardParser.parse(input);

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Releasespace", "Abs1");
        expectedMap.put("Releaseshift", "Lef1");
        
        assertEquals(expectedMap, actual.getKeyMapping());
    }
    
    // KeyDown
    @Test
    public void testKeyDown() throws UnableToParseException {
        String input = BOARD+ABSORBER+KEYDOWN;
        Board actual = BoardParser.parse(input);

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Pressspace", "Abs1");
        
        assertEquals(expectedMap, actual.getKeyMapping());
    }
    
    // KeyDown, where num > 1
    @Test
    public void testKeyDownMultiple() throws UnableToParseException {
        String input = BOARD+ABSORBER+LEFTFLIP+KEYDOWN+KEYDOWN_2;
        Board actual = BoardParser.parse(input);

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Pressspace", "Abs1");
        expectedMap.put("Pressleft", "Lef1");
        
        assertEquals(expectedMap, actual.getKeyMapping());
    }
    
    // Portal without
    @Test
    public void testPortalWithout() throws UnableToParseException {
        String input = BOARD+PORTAL_WITHOUT;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new Portal ("Alpha", 5*L, 7*L, "", "Beta"));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // Portal with
    @Test
    public void testPortalWith() throws UnableToParseException {
        String input = BOARD+PORTAL_WITH;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new Portal("Beta", 15*L, 7*L, "Mercury", "Gamma"));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // >1 gadgets
    @Test
    public void testManyGadgets() throws UnableToParseException {
        String input = BOARD+SQUARE_BUMPER+CIRCLE_BUMPER+TRIANGLE_BUMPER+ABSORBER;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new SquareBumper("SquareA", 0.*L, 14.*L));
        gadgetList.add(new CircleBumper("CircleA", 1*L, 10*L));
        gadgetList.add(new TriangleBumper("Tri", 380., 0., TriangleBumper.Orientation.DEG_90));
        gadgetList.add(new Absorber("Abs1", 0*L, 18*L, 10*L, 2*L));
        List<Ball> ballList = new ArrayList<>();
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // 1 trigger/actions between gadgets
    @Test
    public void testSingleTrigger() throws UnableToParseException {
        String input = BOARD+BALL_A+CIRCLE_BUMPER+ABSORBER+TRIGGER_ACTION_CIRCLE;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new CircleBumper("CircleA", 1*L, 10*L));
        gadgetList.add(new Absorber("Abs1", 0*L, 18*L, 10*L, 2*L));
        List<Ball> ballList = new ArrayList<>();
        ballList.add(new Ball("BallA", 10.25*L, 15.25*L, 0., 0.));
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }
    
    // >1 trigger/actions between gadgets
    @Test
    public void testManyTriggers() throws UnableToParseException {
        String input = BOARD+BALL_A+CIRCLE_BUMPER+ABSORBER+TRIGGER_ACTION_CIRCLE+TRIGGER_ACTION_ABSORBER;
        Board actual = BoardParser.parse(input);
        List<Gadget> gadgetList = new ArrayList<>();
        gadgetList.add(new CircleBumper("CircleA", 1*L, 10*L));
        gadgetList.add(new Absorber("Abs1", 0*L, 18*L, 10*L, 2*L));
        List<Ball> ballList = new ArrayList<>();
        ballList.add(new Ball("BallA", 10.25*L, 15.25*L, 0., 0.));
        Board expected = new Board("testName",gadgetList, ballList, GRAVITY, MU, MU, new HashMap<String, String>(), new HashMap<String, Vect>());
        assertEquals(expected, actual);
    }

}
