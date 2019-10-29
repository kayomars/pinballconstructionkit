package flingball;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import org.junit.Test;

import edu.mit.eecs.parserlib.UnableToParseException;

public class ServerTest {
    
    /*
     * Testing Strategy
     *  - Incoming messages are being read to the server
     *  - server is broadcasting said incoming message
     *  
     *  
     * *** IMPORTANT ***
     * The below testing partitions were tested manually/visually on our local system
     *  - set to particular port
     *  - set to default port
     *  
     *  - horizontal adding
     *      . transfer of balls from left board to right board
     *      . transfer of balls from right board to left board
     *      
     *  - vertical adding
     *      . transfer of balls from top board to bottom board
     *      . transfer of balls from bottom board to top board
     *  
     *  - overriding previous addings
     *      . board1 horizontally joined to board2, board1 tries to horizontally join board3
     *          o board1's West wall joins board3's East wall, board2's East wall becomes solid
     *      . board1 vertically joined to board2, boars1 tries to vertically join board3
     *          o board1's South wall joins board3's North wall, board2's North wall becomes solid
     *      . board 1 horizontally joins board 2, board1 vertically joins board3, board3 horizontally joins board4
     *          o maximum 2x2 square is built 
     *          o also build variations of this
     *  
     *  - disconnecting
     *      . board1 horizontally/vertically joined to board2, board2 "disconnects", board1's West/South wall becomes solid 
     *        any balls/portals that were in board2 are no longer active / in board1 and vice versa for East/North wall disconnections
     *        
     *  - active portals
     *      . board1 has portals connected to board2. While board2 is not connected to the same host server, portals on board1 are ignored. 
     *        When board2 joins, portals are activated and balls can be transported. Disconnect rules are applied here as well.
     *        
     *  - Distinguishing key presses
     *      . When two boards with flippers are connected together, we ensure that the key strokes control them independently depending on 
     *        which Java Application you are focused in on .
     */
    
    
    private static final String LOCALHOST = "127.0.0.1";
    
    private static final int MAX_CONNECTION_ATTEMPTS = 5;
    
    /* Start server on its own thread. */
    private static Thread startServer(final FlingballServer server) {
        Thread thread = new Thread(() ->  {
            try {
                server.serve();
            } catch (IOException ioe) {
                throw new RuntimeException("serve() threw IOException", ioe);
            }
        });
        thread.start();
        return thread;
    }
    
    /* Connect to server with retries on failure. */
    private static Socket connectToServer(final Thread serverThread, final FlingballServer server) throws IOException {
        final int port = server.port();
        assertTrue("server.port() returned " + port, port > 0);
        for (int attempt = 0; attempt < MAX_CONNECTION_ATTEMPTS; attempt++) {
            try { Thread.sleep(attempt * 10); } catch (InterruptedException ie) { }
            if ( ! serverThread.isAlive()) {
                throw new IOException("server thread no longer running");
            }
            try {
                final Socket socket = new Socket(LOCALHOST, port);
                socket.setSoTimeout(1000 * 3);
                return socket;
            } catch (ConnectException ce) {
                // may try again
            }
        }
        throw new IOException("unable to connect after " + MAX_CONNECTION_ATTEMPTS + " attempts");
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // incoming messages & broadcasting on connected host
    @Test
    public void testBroadcasting() throws IOException, UnableToParseException {
        
         // NEEDS TO BE UNCOMMENTED AND TESTED LOCALLY AS IT CAN'T BE RUN ON DIDIT
        
//        int port = 10987;
//        final FlingballServer server = new FlingballServer(port);
//        final Thread thread = startServer(server);
//        final Socket socket = connectToServer(thread, server);
//        
//        final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//        
//        String filename = "boards/default.fb";
//        Board board = BoardParser.parse(new File(filename));
//        
//        out.println("joined " + board.getBoardName());
//        
//        // check that the broadcasted message is equal to the one that was sent
//        assertEquals("joined " + board.getBoardName(), in.readLine());
//        
//        int port2 = 8080;
//        FlingballServer server2 = new FlingballServer(port2);
//        final Thread thread2 = startServer(server2);
//        final Socket socket2 = connectToServer(thread2, server2);
//        
//        final BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
//        final PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);
//        
//        String filename2 = "boards/simple.fb";
//        
//        Board board2 = BoardParser.parse(new File(filename2));
//        
//        out2.println("joined " + board2.getBoardName());
//        assertEquals("joined " + board2.getBoardName(), in2.readLine());
//        // shows that the broadcasted message is only sent to its particular host
//        assertFalse(in.ready());
    
    }
    

    
    
    
}