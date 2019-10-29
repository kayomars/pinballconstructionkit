package flingball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Text-protocol game server 
 *
 */
public class FlingballServer{
    private final ServerSocket serverSocket;
    private final Set<PrintWriter> printerSet = new HashSet<PrintWriter>();
    private final Set<String> boardNames = new HashSet<String>();

    /**
     * How to access on command line: 
     *      - to run the server run the following : java -cp bin:lib/parserlib.jar:lib/physics.jar flingball.FlingballServer (optional port number here)
     *      - to run a client run the following: java -cp bin:lib/parserlib.jar:lib/physics.jar flingball.Flingball
     *          . once you've run the above line ^ you can then type in: Flingball (optional host) (optional port) (optional pathfile to .fb boards)

     */

    // Abstraction function:
    //  . AF(port, serverSocket, printerSet, boardNames) = on a particular port, our server hosts a game of multiplayer interactive flingball
    //    Our server acts as a message broadcaster, sending messages to all printerSets and keeping track of boardNames as new sockets connect 
    //    to the host + port. 

    // Rep Invariant:
    //  . true

    // Safety from rep exposure:  
    //  . all fields are private and final

    // Thread safety argument:
    //  . Each client is in its own thread, and no methods or variables are shared between threads, ensuring confinement. 
    //    printerSet and boardNames are shared, and they are not immutable, but all actions involving these data structures
    //    are atomic so there are no race conditions. We did not utilize any threadsafe data types or synchronization.

    /**
     * Make a new text game sever using flingball that listens for connections on port.
     * @param port at which to host
     * @throws IOException if there is a problem with I/O
     */
    public FlingballServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }


    /**
     * Gets the port where server is listening
     * @return the port on which this server is listening for connections
     */
    public int port() {
        return serverSocket.getLocalPort();
    }

    /**
     * Provides a thread where it reads in user input to console, and broadcasts the input
     * if recognized to all connected clients.
     * The server then blocks and waits for a connection.
     * Each client connection is then handled in a new thread.
     * @throws IOException if there is an error communicating with the client
     */
    public void serve() throws IOException {
        
        // Create new thread for System.in from the server's controller
        Thread handler = new Thread(new Runnable() {
            public void run() {
                try {
                    BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
                    String controllerInput;
                    while ((controllerInput = sysIn.readLine()) != null) {
                        String concatBoards = handleRequest(controllerInput); // returns joined boards string

                        // loop through all PrintWriters and broadcast
                        for (PrintWriter printer : printerSet) {
                            printer.println(concatBoards);
                        }
                        
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("you messed up");
                    System.out.println(e.getMessage());
                }
            }
        }); handler.start();


        // Handle client connections
        while(true) {
            // block until a client connects
            Socket socket = serverSocket.accept();

            // create new thread to listen to clients and echo messages
            Thread echo = new Thread(new Runnable() {
                public void run() {
                    try {
                        String myBoard = "";

                        try {
                            // store the boardName
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            // broadcast that that board has been added
                            // getting all the clients output streams and adding them to printerSet
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            printerSet.add(out);
                            for (String input = in.readLine(); input != null; input = in.readLine()) {
                                String[] args = input.split("\\s+");
                                myBoard = args[1];
                                boardNames.add(args[1]);

                                // loop through all PrintWriters and broadcast
                                for (PrintWriter printer : printerSet) {
                                    printer.println(input);
                                }
                            }

                        } finally {
                            for (PrintWriter printer : printerSet) {
                                printer.println("disconnected " + myBoard);
                            }
                            socket.close();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace(); // but do not stop serving
                    }
                }
            }); echo.start();
        }
    }


    /**
     * Handles the request that may be inputed into the console. Supports horizontal and
     * vertical connection of clients' boards only.
     * @param input the concatenation message 
     * @return a string formatted to fit the defined wire format
     * @throws IOException if an error with input/output exists
     * @throws InterruptedException if an unexpected interruption is encountered
     * @throws UnsupportedOperationException if the operation is not supported
     */
    private String handleRequest(String input) throws IOException, InterruptedException, UnsupportedOperationException {
        String []tokens = input.split(" ");

        assert tokens.length == 3;
        // handle side-by-side joining "h"
        if (tokens[0].equals("h")) {
            String leftBoard;
            String rightBoard;
            if (boardNames.contains(tokens[1]) && boardNames.contains(tokens[2])) {
                leftBoard = tokens[1];
                rightBoard = tokens[2];
            } else {
                throw new IllegalArgumentException();
            }

            // left board, west wall disappears
            // right board, east wall disappears
            return ((String) tokens[0] + " " + leftBoard + " " + rightBoard);
        }

        // handle top-and-bottom joining "v"
        if (tokens[0].equals("v")) {
            String topBoard;
            String bottomBoard;
            if (boardNames.contains(tokens[1]) && boardNames.contains(tokens[2])) {
                topBoard = tokens[1];
                bottomBoard = tokens[2];
            } else {
                throw new IllegalArgumentException();
            }

            // top board, south wall disappears
            // bottom board, north wall disappears 
            return ((String) tokens[0] + " " + topBoard + " " + bottomBoard);
        }

        throw new UnsupportedOperationException(input);
    }

    
    /**
     * Flushes out a message to all the clients who are currently connected
     * @param message is the message to send to clients
     */
    public void dispatch(String message) {
        for (PrintWriter o : printerSet) {
            if (o.checkError()) {
                printerSet.remove(o);
            } else {
                o.println(message);
            }
        }
    }

    /**
     * Creates an instance of flingball server. If an arg is provided, the server will listen on that port.
     * If none is provided, then it will host on the default port 10987
     * @param args an array of String
     * @throws IOException if there is an error with input/output
     */
    public static void main(String[] args) throws IOException  {
        if (args.length == 0) {
            int portDefault = 10987;
            new FlingballServer(portDefault).serve();
        } 
        else {
            new FlingballServer(Integer.parseInt(args[0])).serve();
        }

    }
}