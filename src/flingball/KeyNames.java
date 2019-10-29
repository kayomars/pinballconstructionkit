package flingball;

import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents a mapping of all the keyEvent's integer values to their names in the board file format.
 */
public class KeyNames {

    // More to include here
    // Abstraction Function
    // AF (keyName) = Contains a mapping of all the key events to their string representation according to the board file
    //               format
    // Rep Invariant 
    //  - None
    // Rep Exposure Argument
    //  - All fields are private and final
    //  - keyNames holds immutable data, and direct reference to the map itself is never returned
    // Thread Safety Argument
    //  - All fields are private and final
    //  - Even though the map here itself is mutable, it is never shared amongst threads, so race conditions are not possible
    //  - In addition, direct references to the map itself is never returned, so it is safe from mutation
    //  - Avoids using any global variables
    //  - Does not make use of threadsade data structures as it is not required
    
    private final Map<Integer,String> keyName;
     
    /**
     * Constructor for mapping of KeyNames
     */
    public KeyNames() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(KeyEvent.VK_A, "a");
        map.put(KeyEvent.VK_B, "b");
        map.put(KeyEvent.VK_C, "c");
        map.put(KeyEvent.VK_D, "d");
        map.put(KeyEvent.VK_E, "e");
        map.put(KeyEvent.VK_F, "f");
        map.put(KeyEvent.VK_G, "g");
        map.put(KeyEvent.VK_H, "h");
        map.put(KeyEvent.VK_I, "i");
        map.put(KeyEvent.VK_J, "j");
        map.put(KeyEvent.VK_K, "k");
        map.put(KeyEvent.VK_L, "l");
        map.put(KeyEvent.VK_M, "m");
        map.put(KeyEvent.VK_N, "n");
        map.put(KeyEvent.VK_O, "o");
        map.put(KeyEvent.VK_P, "p");
        map.put(KeyEvent.VK_Q, "q");
        map.put(KeyEvent.VK_R, "r");
        map.put(KeyEvent.VK_S, "s");
        map.put(KeyEvent.VK_T, "t");
        map.put(KeyEvent.VK_U, "u");
        map.put(KeyEvent.VK_V, "v");
        map.put(KeyEvent.VK_W, "w");
        map.put(KeyEvent.VK_X, "x");
        map.put(KeyEvent.VK_Y, "y");
        map.put(KeyEvent.VK_Z, "z");
        map.put(KeyEvent.VK_0, "0");
        map.put(KeyEvent.VK_1, "1");
        map.put(KeyEvent.VK_2, "2");
        map.put(KeyEvent.VK_3, "3");
        map.put(KeyEvent.VK_4, "4");
        map.put(KeyEvent.VK_5, "5");
        map.put(KeyEvent.VK_6, "6");
        map.put(KeyEvent.VK_7, "7");
        map.put(KeyEvent.VK_8, "8");
        map.put(KeyEvent.VK_9, "9");
        map.put(KeyEvent.VK_SHIFT, "shift");
        map.put(KeyEvent.VK_CONTROL, "ctrl");
        map.put(KeyEvent.VK_ALT, "alt");
        map.put(KeyEvent.VK_META, "meta");
        map.put(KeyEvent.VK_SPACE, "space");
        map.put(KeyEvent.VK_LEFT, "left");
        map.put(KeyEvent.VK_RIGHT, "right");
        map.put(KeyEvent.VK_UP, "up");
        map.put(KeyEvent.VK_DOWN, "down");
        map.put(KeyEvent.VK_MINUS, "minus");
        map.put(KeyEvent.VK_EQUALS, "equals");
        map.put(KeyEvent.VK_BACK_SPACE, "backspace");
        map.put(KeyEvent.VK_OPEN_BRACKET, "openbracket");
        map.put(KeyEvent.VK_CLOSE_BRACKET, "closebracket");
        map.put(KeyEvent.VK_BACK_SLASH, "backslash");
        map.put(KeyEvent.VK_SEMICOLON, "semicolon");
        map.put(KeyEvent.VK_QUOTE, "quote");
        map.put(KeyEvent.VK_ENTER, "enter");
        map.put(KeyEvent.VK_COMMA, "comma");
        map.put(KeyEvent.VK_PERIOD, "period");
        map.put(KeyEvent.VK_SLASH, "slash");
        keyName = Collections.unmodifiableMap(map);
    }
    
    /**
     * Gets the element that is mapped to by the map. If no such element exists, return "NotValidRequest"
     * @param keyEvent is the integer representing the key press
     * @return element mapped to my map, or "NotValidRequest"
     */
    public String getBoardVersion(int keyEvent) {
        
        if (keyName.containsKey(keyEvent)) {
            return keyName.get(keyEvent);
        }
        
        return "NotValidRequest";
    }
}