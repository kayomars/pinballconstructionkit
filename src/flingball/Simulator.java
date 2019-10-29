package flingball;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Class that simulates the physics of the Flingball board
 */
public class Simulator implements Runnable {
    
    private static final int GAMEBOARD_SIZE = 20;
    private static final int DRAWING_AREA_SIZE_IN_PIXELS = GAMEBOARD_SIZE * Flingball.L;
    
    private Board board;
    private double fps;

    /**
     * Creates a new Simulator
     * 
     * @param board the board to display
     * @param framesPerSecond the frame rate to display at
     */    
    public Simulator(Board board, double framesPerSecond) {
        this.board = board;
        this.fps = framesPerSecond;        
    }
    
    /**
     * Runs the board's physics and animates at a rate of framesPerSecond
     * Creates a key listener that will trigger the gadget's action it is linked to when triggered
     * if such a gadget exists on the board.
     */
    public void run() {   
        final JFrame window = new JFrame("Flingball");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final JPanel drawingArea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawBoard(g);
            }
        };
        drawingArea.setPreferredSize(new Dimension(DRAWING_AREA_SIZE_IN_PIXELS, DRAWING_AREA_SIZE_IN_PIXELS));
        window.add(drawingArea);
        window.pack();
        window.setVisible(true);
        
        KeyNames possibleKeys = new KeyNames();
        BlockingQueue<String> allPresses = new LinkedBlockingQueue<String>(); 
        Map<String, String> allMatchedMappings = board.getKeyMapping();
        List<String> allPressesForBoard = new ArrayList<String>();
        
        
        // Only adds mappings matched from parser to allPresses
        KeyListener listener = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                
                final String currentAction = "Press" + possibleKeys.getBoardVersion(e.getKeyCode());
                
                if (allMatchedMappings.containsKey(currentAction)) {
                    try {
                        allPresses.put(currentAction);
                    } catch (InterruptedException e1) {
                        // Do nothing
                    }
                }
            }

            @Override public void keyReleased(KeyEvent e) {
                
                final String currentAction = "Release" + possibleKeys.getBoardVersion(e.getKeyCode());
                
                if (allMatchedMappings.containsKey(currentAction)) {
                    try {
                        allPresses.put(currentAction);
                    } catch (InterruptedException e1) {
                        // Do nothing
                    }
                }
            }
        };
        
        listener = new MagicKeyListener(listener);
        window.addKeyListener(listener);
        

        new Timer(1000/(int)fps, (ActionEvent e) -> {
            drawingArea.repaint();
        }).start();
        
        while (true) {
            allPressesForBoard = new ArrayList<String>();
            
            // Getting all registered key presses till now
            int numOfPresses = allPresses.size();
            
            for (int i = 0; i < numOfPresses; i++) {
                
                try {
                    String reqdStr = allPresses.take();
                    allPressesForBoard.add(reqdStr);
                } catch (InterruptedException e1) {
                    // Do nothing
                }
            }
            
            // Now pass this list into the board to a method that calls action of all gadgets
            board.callActionOnGadgets(allPressesForBoard);
            board.timeStep(1./fps);
            
            try {
                Thread.sleep((long) (1000./fps));
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    /**
     * Redraws the board within the JFrame
     * @param g graphics object used to paint the board each time step
     */
    private void drawBoard(final Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        g2.fill(new Rectangle2D.Double(0, 0, DRAWING_AREA_SIZE_IN_PIXELS, DRAWING_AREA_SIZE_IN_PIXELS));
        // Render the board, each gadget renders onto the canvas
        board.render(g2);      
    }


}

