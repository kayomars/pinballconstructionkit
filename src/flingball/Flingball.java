package flingball;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.mit.eecs.parserlib.UnableToParseException;
import physics.Vect;

/**
 * Main class that handles the command line interface
 * and starts the Flingball game
 */
public class Flingball {
    
    public static final double fps = 60;
    public static final int L = 20; 
    
    private static final String HOST = "[A-Za-z0-9.]*";
    private static final String PORT = "[0-9]*";
     

    /**
     * Starts a flingball game. If no filename is given, then creates the board
     * described in "default.fb". Otherwise, creates the board described in
     * the given file
     * 
     * @param args The command line arguments to the program. If args is empty
     *   then a default board will be created. If args is not empty, then it must
     *   have size 1 and its sole element must be a valid Flingball file name.
     * @throws IOException if the board file specified could not be found
     * @throws UnableToParseException if the board described by args could not be parsed  
     * @throws IllegalArgumentException if args is ill formed
     */
    public static void main(String[] args) throws UnableToParseException, IOException, IllegalArgumentException {
        BufferedReader userInputBR = new BufferedReader(new InputStreamReader(System.in));
        String userInput = userInputBR.readLine();
        String[] arguments = userInput.split("\\s+");
        
        boolean hostExists = false;
        String hostName = "";
        int portNumber = 10987; // default port number
        String filename = "boards/default.fb";
        
        assert (arguments[0].equals("Flingball"));
        
        if (arguments.length == 4) {
            hostExists = true;
            hostName = arguments[1];
            portNumber = Integer.parseInt(arguments[2]);
            filename = arguments[3];
        } 
        
        else if (arguments.length == 3) {
            // host + port OR host + file
            if (arguments[1].matches(HOST)) {
                hostExists = true;
                hostName = arguments[1];
                if (arguments[2].matches(PORT)) {
                    portNumber = Integer.parseInt(arguments[2]);
                } else {
                    filename = arguments[2];
                }
            } else {
                // port + file
                portNumber = Integer.parseInt(arguments[1]);
                filename = arguments[2];
            }
        }
            
        else if (arguments.length == 2) {
            if(arguments[1].matches(PORT)) {
                portNumber = Integer.parseInt(arguments[1]);
            } else if (arguments[1].matches(HOST)) {
                hostExists = true;
                hostName = arguments[1];
                
            } else {
                filename = arguments[1];
            }
        } 
        
        else if (arguments.length == 1) {
            // do nothing
        }
        
        else {
            throw new IllegalArgumentException("that is not a valid command.");
        }        
        
        Board board = BoardParser.parse(new File(filename));
        
        /*
         * WIRE PROTOCOL EXPLANATION:
         *  The structure of our client-server interaction is one that mimics echoClient and echoSocket (example can be found from Oracle). The client sends a message
         *  to the the server, and the server broadcasts it out to all the other active boards on a particular host. From there each board will parse that message
         *  and only act if it is applicable to them. The first argument of each message is the indicator for what type of action is happening. 
         * 
         *  - Sending message to Server to be broadcasted:
         *      . "passBall From To xPos yPos xVel yVel"
         *          o this message is for passing a ball from one board to another if the boards are conjoined. The first argument is the action type, the second is the board
         *            that is sending that particular message, the third is the board that the "From" board is sending the ball to, and the 4, 5, 6, 7th arguments are the 
         *            necessary arguments to create a Ball to the "To" board with such that the position and velocity are preserved in the passing. 
         *      . "portalBall From To portalName xVel yVel"
         *          o this message is for transporting a ball from one portal to its connected active board portal. The first argument is the action type, the second is the board
         *            that is sending that particular message, the third is the board that the "From" board is sending the ball to, the 4th argument is the portalName (we have a 
         *            map of portals with their names as keys, and their position on the board as values) so that we can create a ball at the correct positioning of the "To" board, 
         *            and the 5, 6th arguments are the velocity preserved for the creation of the ball to the "To" board to emulate the transportation of a portal.
         *  
         *  - Receiving broadcasted message from Server after an occurrence has occurred:
         *      . "joined boardName"
         *          o the first time a board connects to a host, all other active boards on that host will receive this message and keep track of all the active boards
         *      . "disconnected boardName"
         *          o when a player closes out of his/her board, all other active boards on that board's same host will receive this message and remove it from it's 
         *            active boards. Additionally, any boards that were conjoined to the disconnected board will no longer be connected and its walls will revert to solid
         *      . "h Board1 Board2"
         *          o joining Board1 to Board2 horizontally such that Board1's east wall and Board2's west wall become permeable
         *      . "v Board1 Board2"
         *          o join in Board1 to Board2 vertically such that Board1's south wall and Board2's north wall becomes permeable
         *      . "passBall From To xPos yPos xVel yVel"
         *          o see explanation above
         *      . "portalBall From To portnalName xVel yVel"
         *          o see explanation above
         */
        
        
        if (hostExists) {
            try {
                    @SuppressWarnings("resource")
                    Socket echoSocket = new Socket(hostName, portNumber);
                    PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                    out.println("joined " + board.getBoardName());
                    
                                       
                    Thread sendingMessage = new Thread(new Runnable() {
                        public void run() {
                            board.attachListener(new StateChangeListener() {
                                public void notifyStateChange(String str) {
                                    // str = from, to, action, position/portal, velocity
                                    out.println(str); 
                                }
                                
                            });
                        }
                    }); sendingMessage.start();
                    
                    
                     
                    Thread receivingMessage = new Thread(new Runnable() {
                        public void run() {
                            String receivingInput;
                            try {
                                while ((receivingInput = in.readLine()) != null) {
                                    String[] args = receivingInput.split("\\s+");


                                    // add active board
                                    if (args[0].equals("joined")) {
                                        board.addActiveBoard(args[1]);
                                    } 
                                    
                                    // remove disconnected board
                                    else if (args[0].equals("disconnected")) {
                                        for (int i = 0; i < board.getBoardJoins().length; i++) {
                                            if (board.getBoardJoins()[i].equals(args[1])) {
                                                board.removeConcatBoard(i);
                                            }
                                        }
                                        board.removeActiveBoard(args[1]);
                                    }
                                    
                                    // horizontal concatenation of boards
                                    else if (args[0].equals("h")) {
                                        // leftBoard --> set East wall index to args[2] = rightBoard
                                        if (args[1].equals(board.getBoardName())) {
                                            board.concatBoard(args[2], "E");
                                        }
                                        
                                        // rightBoard --> set West wall index to args[1] = leftBoard
                                        else if (args[2].equals(board.getBoardName())) {
                                            board.concatBoard(args[1], "W");
                                        }
                                        
                                        // check if joinedBoards of my Board are being joined
                                        else {
                                            String[] joinedBoards = board.getBoardJoins();
                                            for (int i=0; i < joinedBoards.length; i++) {
                                                if (joinedBoards[i].equals(args[1]) && board.getBoardJoins()[3].equals(joinedBoards[i])) {
                                                    board.removeConcatBoard(i);
                                                } else if (joinedBoards[i].equals(args[2]) && board.getBoardJoins()[1].equals(joinedBoards[i])) {
                                                    board.removeConcatBoard(i);
                                                }
                                            }
                                        }
                                    }
                                    
                                    // vertical concatenation of boards
                                    else if (args[0].equals("v")) {
                                        // topBoard --> set South wall index to args[2] = bottomBoard
                                        if (args[1].equals(board.getBoardName())) {
                                            board.concatBoard(args[2], "S");
                                        }
                                        
                                        // bottomBoard --> set North wall index to args[1] = topBoard
                                        else if (args[2].equals(board.getBoardName())) {
                                            board.concatBoard(args[1], "N");
                                        }
                                        
                                        // check if joinedBoards of my Board are being joined
                                        else {
                                            String[] joinedBoards = board.getBoardJoins();
                                            for (int i=0; i < joinedBoards.length; i++) {
                                                if (joinedBoards[i].equals(args[1]) && board.getBoardJoins()[0].equals(joinedBoards[i])) {
                                                    board.removeConcatBoard(i);
                                                } else if (joinedBoards[i].equals(args[2]) && board.getBoardJoins()[2].equals(joinedBoards[i])) {
                                                    board.removeConcatBoard(i);
                                                }
                                            }
                                        }
                                    }
                                    
                                    
                                    
                                    // passBall: passball, From, To, xpos, ypos, xvel, yvel
                                    else if (args[0].equals("passBall")) {
                                        // check if this board is the TO board
                                        if (board.getBoardName().equals(args[2])) {
                                            String[] joinedBoards = board.getBoardJoins();
                                            
                                            // check if partnered wall is truly a current joined wall
                                            if (joinedBoards[0].equals(args[1]) || joinedBoards[1].equals(args[1]) || joinedBoards[2].equals(args[1]) || joinedBoards[3].equals(args[1])) {
                                                int passWall = 0; // [N, E, S, W] -> [0, 1, 2, 3]
                                                for (int i=0; i < joinedBoards.length; i++) {
                                                    if (joinedBoards[i].equals(args[1])) {
                                                        passWall = i;
                                                    }
                                                }

                                                   
                                                if (passWall == 0) { // coming from the north
                                                    Ball passBall = new Ball("passBall", Double.parseDouble(args[3]), 0, Double.parseDouble(args[5]), Double.parseDouble(args[6]));
                                                    board.addBall(passBall);
                                                } else if (passWall == 1) { // coming from the east
                                                    Ball passBall = new Ball("passBall", 20*L, Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
                                                    board.addBall(passBall);
                                                } else if (passWall == 2) { // coming from the south
                                                    Ball passBall = new Ball("passBall", Double.parseDouble(args[3]), 20*L, Double.parseDouble(args[5]), Double.parseDouble(args[6]));
                                                    board.addBall(passBall);
                                                } else if (passWall == 3) { // coming from the west
                                                    Ball passBall = new Ball("passBall", 0, Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
                                                    board.addBall(passBall);
                                                }
                                            }
                                            
                                        }
                                            }
                                            
                                                                               
                                    // portalBall: portalball, From, To, portalName, xvel, yvel
                                    else if (args[0].equals("portalBall")) {
                                        // check if this board is the TO board
                                        if (board.getBoardName().equals(args[2])) {
                                            
                                            if (board.getPortalMap().containsKey(args[3])) {
                                                Vect neededLoc = board.getPortalMap().get(args[3]);
                                                double x_loc = neededLoc.x();
                                                double y_loc = neededLoc.y();
                                                
                                                Ball portalBall = new Ball("portalBall", x_loc, y_loc,  Double.valueOf(args[4]), Double.valueOf(args[5]));
                                                board.addBall(portalBall);
                                            }
                                            
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }); receivingMessage.start();

                } catch (UnknownHostException e) {
                    System.err.println("Don't know about host " + hostName);
                } catch (IOException e) {
                    System.err.println("Couldn't get I/O for the connection to " + hostName);
                    System.exit(1);

                }   
        } 
         
        Simulator sim = new Simulator(board, fps);
        sim.run();
    }
        
}
