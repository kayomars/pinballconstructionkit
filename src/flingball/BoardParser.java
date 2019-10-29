package flingball;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;
import physics.Vect;


/**
 * A class that provides methods for parsing contents of .fb files
 */

public class BoardParser {
    /**
     * Main method. Parses and then reprints an example board. 
     * 
     * @param args command line arguments, not used
     * @throws UnableToParseException if example expression can't be parsed
     */
    public static void main(final String[] args) throws UnableToParseException {
        String file = "boards/absorber.fb";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder board = new StringBuilder();            
            reader.lines().forEachOrdered((line) -> {board.append(line).append('\n');});
            Board parsed = parse(board.toString());
            System.out.println(parsed.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }        
    }
    
    // the nonterminals of the grammar
    private enum BoardGrammar {
        BOARD, BOARDLINE, BALLLINE, GADGETLINE, SQUAREBUMPERLINE, CIRCLEBUMPERLINE,
        TRIANGLEBUMPERLINE, ABSORBERLINE, FIRELINE, COMMENT, GRAVITY, FRICTION1, FRICTION2,
        NEWLINE, NAMEATTR, POSITIONATTR, ORIENTATIONATTR, INTEGER, FLOAT, NAME, WHITESPACE,
        ORIENTATION, WHITESPACEANDCOMMENTS, KEYLINE, KEYUPLINE, KEYDOWNLINE, KEYS, LEFTFLIPPERLINE, RIGHTFLIPPERLINE,
        PORTALLINE, OTHERBOARDATTR, OTHERPORTALATTR, KEYATTR, ACTIONATTR
        
    }
    
    private static Parser<BoardGrammar> parser = makeParser();
    
    /**
     * Compile the grammar into a parser using ParserLib.
     * 
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<BoardGrammar> makeParser() throws RuntimeException {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/flingball/BoardGrammar.g");
            return Parser.compile(grammarFile, BoardGrammar.BOARD);
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }    
    }
    
    
    /**
     * Parses file's contents and returns an instantiated Board with the attributes specified by the parsing of the file
     * @param file is the file that needs to be parsed
     * @return a board that has been instantiated according to the parsed input file
     * @throws UnableToParseException if the file fails to be parsed
     * @throws IOException if the file cannot be located
     */
    public static Board parse(File file) throws UnableToParseException, IOException {
       BufferedReader reader = new BufferedReader(new FileReader(file));
       StringBuilder board = new StringBuilder();            
       reader.lines().forEachOrdered((line) -> {board.append(line).append('\n');});
       reader.close();
       return parse(board.toString());
    }
    
    /**
     * Parse a string into a Board object according to the grammar described in
     * flingball/BoardGrammar.g.
     * 
     * @param string String to parse
     * @return Board parsed from the string
     * @throws UnableToParseException if the string doesn't match the Board grammar
     */
    public static Board parse(final String string) throws UnableToParseException {
        final ParseTree<BoardGrammar> parseTree = parser.parse(string);
        final Board expression = makeAbstractSyntaxTree(parseTree);
        return expression;    
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in BoardGrammar.g
     * @return abstract syntax tree corresponding to parseTree
     * @throws IllegalArgumentException when the grammar requirements are not met
     */
    private static Board makeAbstractSyntaxTree(final ParseTree<BoardGrammar> parseTree) throws IllegalArgumentException{
        double L = (double) Flingball.L;
        double gravity = 25 * L;
        double mu1 = 0.025;
        double mu2 = 0.025;
        String boardName = "";
        List<Ball> ballList = new ArrayList<>();
        List<Gadget> gadgetList = new ArrayList<>();
        Map<String, String> neededKeyMap = new HashMap<>();
        Map<String, Vect> portalMap = new HashMap<String, Vect>();
        
        for (ParseTree<BoardGrammar> child : parseTree.children()) {
            
            switch(child.name()) {
            case BOARDLINE:
                for (ParseTree<BoardGrammar> param : child.children()) {
                    if (param.name() == BoardGrammar.NAMEATTR) {
                        boardName = param.children().get(0).text();
                    }
                    else if (param.name() == BoardGrammar.GRAVITY) {
                        gravity = Double.parseDouble(param.children().get(0).text())*L;
                    }
                    else if (param.name() == BoardGrammar.FRICTION1) {
                        mu1 = Double.parseDouble(param.children().get(0).text());                        
                    }
                    else if (param.name() == BoardGrammar.FRICTION2) {
                        mu2 = Double.parseDouble(param.children().get(0).text());                        
                    }
                    else {
                        throw new IllegalArgumentException();
                    }
                }
                break;
            
            case BALLLINE:
                String name = "";
                List<Double> params = new ArrayList<Double>();
                for (ParseTree<BoardGrammar> param : child.children()) {                    
                    if (param.name() == BoardGrammar.NAMEATTR) {
                        name = param.children().get(0).text();
                    }
                    else if (param.name() == BoardGrammar.FLOAT) {
                        params.add(Double.parseDouble(param.text()));
                    }
                    else {
                        throw new IllegalArgumentException();
                    }
                }
                ballList.add(new Ball(name, params.get(0)*L, params.get(1)*L, params.get(2)*L, params.get(3)*L));
                break;
            
            case GADGETLINE:
                child = child.children().get(0);
                ParseTree<BoardGrammar> nameAttribute = child.children().get(0);
                ParseTree<BoardGrammar> position = child.children().get(1);
                name = nameAttribute.children().get(0).text();
                double xpos = L*Integer.parseInt(position.children().get(0).text());
                double ypos = L*Integer.parseInt(position.children().get(1).text());
                if (child.name() == BoardGrammar.SQUAREBUMPERLINE) {
                    gadgetList.add(new SquareBumper(name, xpos, ypos));
                }
                else if (child.name() == BoardGrammar.CIRCLEBUMPERLINE) {
                    gadgetList.add(new CircleBumper(name, xpos, ypos));
                }
                else if (child.name() == BoardGrammar.TRIANGLEBUMPERLINE) {
                    TriangleBumper.Orientation orientation = TriangleBumper.Orientation.DEG_0;
                    if (child.children().size() > 2) {
                        ParseTree<BoardGrammar> orientationAttr = child.children().get(2);
                        String angle = orientationAttr.children().get(0).text();
                        angle = angle.trim();
                        if (angle.equals("0")) {
                            orientation = TriangleBumper.Orientation.DEG_0;
                        }
                        else if (angle.equals("90")) {
                            orientation = TriangleBumper.Orientation.DEG_90;
                        }
                        else if (angle.equals("180")) {
                            orientation = TriangleBumper.Orientation.DEG_180;
                        }
                        else if (angle.equals("270")) {
                            orientation = TriangleBumper.Orientation.DEG_270;                            
                        }
                        else {
                            throw new IllegalArgumentException();
                        }
                    }
                    gadgetList.add(new TriangleBumper(name, xpos, ypos, orientation));
                }
               
                else if (child.name() == BoardGrammar.LEFTFLIPPERLINE) {
                    LeftFlipper.Orientation orientation = LeftFlipper.Orientation.DEG_0;
                    if (child.children().size() > 2) {
                        ParseTree<BoardGrammar> orientationAttr = child.children().get(2);
                        String angle = orientationAttr.children().get(0).text();
                        angle = angle.trim();
                        if (angle.equals("0")) {
                            orientation = LeftFlipper.Orientation.DEG_0;
                        }
                        else if (angle.equals("90")) {
                            orientation = LeftFlipper.Orientation.DEG_90;
                        }
                        else if (angle.equals("180")) {
                            orientation = LeftFlipper.Orientation.DEG_180;
                        }
                        else if (angle.equals("270")) {
                            orientation = LeftFlipper.Orientation.DEG_270;                            
                        }
                        else {
                            throw new IllegalArgumentException();
                        }
                    }
                    gadgetList.add(new LeftFlipper(name, xpos, ypos, orientation));
                }
               
                else if (child.name() == BoardGrammar.RIGHTFLIPPERLINE) {
                    RightFlipper.Orientation orientation = RightFlipper.Orientation.DEG_0;
                    if (child.children().size() > 2) {
                        ParseTree<BoardGrammar> orientationAttr = child.children().get(2);
                        String angle = orientationAttr.children().get(0).text();
                        angle = angle.trim();
                        if (angle.equals("0")) {
                            orientation = RightFlipper.Orientation.DEG_0;
                        }
                        else if (angle.equals("90")) {
                            orientation = RightFlipper.Orientation.DEG_90;
                        }
                        else if (angle.equals("180")) {
                            orientation = RightFlipper.Orientation.DEG_180;
                        }
                        else if (angle.equals("270")) {
                            orientation = RightFlipper.Orientation.DEG_270;                            
                        }
                        else {
                            throw new IllegalArgumentException();
                        }
                    }
                    gadgetList.add(new RightFlipper(name, xpos, ypos, orientation));
                }
               
                else if (child.name() == BoardGrammar.ABSORBERLINE) {
                    double width = L*Integer.parseInt(child.children().get(2).text());
                    double height = L*Integer.parseInt(child.children().get(3).text());
                    gadgetList.add(new Absorber(name, xpos, ypos, width, height));
                }
                
                else if (child.name() == BoardGrammar.PORTALLINE) {
                    String otherBoardName = "";
                    String otherPortalName = "";
                    if (child.children().size() > 3) {
                        ParseTree<BoardGrammar> otherBoardAttr = child.children().get(2);
                        otherBoardName = otherBoardAttr.children().get(0).text();
                        ParseTree<BoardGrammar> otherPortalAttr = child.children().get(3);
                        otherPortalName = otherPortalAttr.children().get(0).text();
                    }
                    else {
                        ParseTree<BoardGrammar> otherPortalAttr = child.children().get(2);
                        otherPortalName = otherPortalAttr.children().get(0).text();
                    }
                    portalMap.put(name, new Vect(xpos, ypos));
                    gadgetList.add(new Portal(name, xpos, ypos, otherBoardName, otherPortalName));
                }
                else {
                    throw new IllegalArgumentException();
                }
                break;
                
            case KEYLINE:
                child = child.children().get(0);
                ParseTree<BoardGrammar> keyAttribute = child.children().get(0);
                ParseTree<BoardGrammar> nameNeeded = child.children().get(1);
                
                String keySelected = keyAttribute.children().get(0).text();
                name = nameNeeded.children().get(0).text();
                
                if (child.name() == BoardGrammar.KEYUPLINE) {
                    
                    keySelected = "Release" + keySelected;
                    neededKeyMap.put(keySelected, name);
                    
                }
                else if (child.name() == BoardGrammar.KEYDOWNLINE) {
                    
                    keySelected = "Press" + keySelected;
                    neededKeyMap.put(keySelected, name);
                }
                else {
                    throw new IllegalArgumentException();
                }
                break;
                           
            case FIRELINE:
                String triggerName = child.children().get(0).text();
                String actionName = child.children().get(1).text();
                Gadget trigger = null;
                Gadget action = null;
                for (Gadget gadget : gadgetList) {
                    if (gadget.name().equals(triggerName)) {
                        trigger = gadget;
                    }
                    if (gadget.name().equals(actionName)) {
                        action = gadget;
                    }
                }
                trigger.link(action);
                break;
            
            default:
                break;
            } 
        }

        return new Board(boardName, gadgetList, ballList, gravity, mu1, mu2, neededKeyMap, portalMap);
    }
}
