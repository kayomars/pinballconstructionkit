@skip whitespaceAndComments {
    board ::= boardLine (ballLine | gadgetLine | fireLine | comment | newline | keyLine)*;
    boardLine ::= 'board' nameAttr gravity? friction1? friction2? '\n';
    ballLine ::= 'ball' nameAttr 'x' '=' FLOAT 'y' '=' FLOAT 'xVelocity' '=' FLOAT 'yVelocity' '=' FLOAT '\n';
    gadgetLine ::= squareBumperLine | circleBumperLine | triangleBumperLine | absorberLine | rightFlipperLine | leftFlipperLine | portalLine;
    squareBumperLine ::= 'squareBumper'  nameAttr positionAttr '\n';
    circleBumperLine ::= 'circleBumper'  nameAttr positionAttr '\n';
    triangleBumperLine ::= 'triangleBumper'  nameAttr positionAttr orientationAttr? '\n';
    rightFlipperLine ::= 'rightFlipper'  nameAttr positionAttr orientationAttr? '\n';
    leftFlipperLine ::= 'leftFlipper'  nameAttr positionAttr orientationAttr? '\n';
    portalLine ::= 'portal' nameAttr positionAttr otherBoardAttr? otherPortalAttr '\n';
    absorberLine ::= 'absorber' nameAttr positionAttr 'width' '=' INTEGER 'height' '=' INTEGER '\n';
    fireLine ::= 'fire' 'trigger' '=' NAME 'action' '=' NAME '\n';
    keyLine ::= keyUpLine | keyDownLine;
    keyUpLine ::= 'keyup' keyAttr actionAttr '\n';
    keyDownLine ::= 'keydown' keyAttr actionAttr '\n';
    newline ::= '\n';
    actionAttr ::= 'action' "=" NAME;
    keyAttr ::= 'key' "=" KEYS;
    nameAttr ::= 'name' "=" NAME;
    positionAttr ::= 'x' '=' INTEGER 'y' '=' INTEGER;
    orientationAttr ::= 'orientation' '=' orientation;
    otherBoardAttr ::= 'otherBoard' '=' NAME;
    otherPortalAttr ::= 'otherPortal' '=' NAME;
    gravity ::= 'gravity' '=' FLOAT;
    friction1 ::=  'friction1' '=' FLOAT;
    friction2 ::=  'friction2' '=' FLOAT;
    orientation ::= '0'|'90'|'180'|'270';
}
 
INTEGER ::= [0-9]+;
FLOAT ::= '-'?([0-9]+'.'[0-9]*|'.'?[0-9]+);
NAME ::= [A-Za-z_][A-Za-z_0-9]*;
KEYS ::= [a-z] | [0-9] | 'shift' | 'ctrl' | 'alt' | 'meta' | 'space' | 'left' | 'right' | 'up' | 'down' | 'minus' | 'equals' | 'backspace' | 'openbracket' | 'closebracket' | 'backslash' | 'semicolon' | 'quote' | 'enter' | 'comma' | 'period' | 'slash';
whitespace ::= [ \t\r]+;
comment ::='#'[^\n]*'\n';
whitespaceAndComments ::= (whitespace | comment)*;
