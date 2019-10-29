package flingball;

import static org.junit.Assert.assertEquals;
import java.awt.event.KeyEvent;


import org.junit.Test;

public class KeysTest {

    /**
     * Testing strategy
     *  
     *  - Number of keyup events = 0, 1, >1
     *  - Number of keydown events = 0, 1, >1
     *  
     *  - Magic key testing with number of keyup events > 5
     *      
     *  - getBoardVersion()
     *      - With a legal input that meets the board representation requirements
     *      - With an input that does not meet the board representation requirements
     *      
     *  - In addition, these test were also carried out visually when simulating instances of the game
     * 
     */    

    @Test(expected = AssertionError.class) public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Testing getBoardVersion with legal input
    @Test
    public void testStringFromValidKeyPress() {
        
        KeyNames possibleKeys = new KeyNames();
        
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_A), "a");
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_9), "9");
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_SHIFT), "shift");
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_LEFT), "left");
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_UP), "up");
    }

    
    // Testing getBoardVersion with illegal input
    @Test
    public void testStringFromInValidKeyPress() {
        
        KeyNames possibleKeys = new KeyNames();
        
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_CUT), "NotValidRequest");  
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_ACCEPT), "NotValidRequest"); 
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_DEAD_ACUTE), "NotValidRequest"); 
        assertEquals(possibleKeys.getBoardVersion(KeyEvent.VK_CLEAR), "NotValidRequest"); 
    }
}