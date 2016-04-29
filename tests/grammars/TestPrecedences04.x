package parsers;

import java.io.IOException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import java.util.LinkedList; 

%%
%parser TestPrecedences04Parser

/*

This grammar is only used for testing Copper implementation.

See "TODO" annotations, for discovered problems.
*/

%aux{

  /**
   * Execute tests.
   */
  static public void main(String [] args) {

    // a list of input string and then the expected result.
    LinkedList<String> tests = new LinkedList<String>();

    tests.add("lr");
    tests.add("~~lr");

    tests.add("_lr");
    tests.add("~_lr");

    // TODO: this test fails because the parser does not admit "__", but only "_".
    tests.add("__lr");
    tests.add("__lr");

    tests.add("rl");
    tests.add("~~rl");

    // TODO: this test fails because the parser does not accept "_rl" but only "_lr".
    tests.add("_rl");
    tests.add("~_rl");

    // TODO: fails because it does not accept two consecutive "__".
    tests.add("__rl");
    tests.add("__rl");

    boolean allOk = testAllInputStrings(tests);

    if (!allOk) {
      System.exit(1);
    }

  } 

  /**
   * Execute tests, and report on stderr the problems.
   *
   * @param inputString followed by expected result.
   * @return true if all tests are passed.
   */
  static public boolean testAllInputStrings(LinkedList<String> inputStrings) {
    boolean allOk = true;
    String inputString = null;

    for (String str : inputStrings) {

      if (inputString == null) {
         inputString = str;
      } else {
        boolean testIsOk = true;
        String expectedResult = str;
        String result = testInputString(inputString, expectedResult);

        if (result != null) {
          testIsOk = false;
          System.err.println("Input string \"" + inputString + "\" returned parsing result \"" + result + "\" instead of expected result \"" + expectedResult + "\"\n");
        }
        allOk = allOk && testIsOk;

        inputString = null;
      }
    }

    return allOk;
  }

  /**
   * @param inputString
   * @param expectedResult
   * @return null if the parsing is successful, the returned string otherwise.
   */
  static public String testInputString(String inputString, String expectedResult) {
     // *WARNING*: in case of copy and paste, update also this class!!!
     TestPrecedences04Parser parser = new TestPrecedences04Parser();
     try {
       String result = parser.parse(inputString);
       if (result.equals(expectedResult)) {
         return null;
       } else {
         return result;
       }
     } catch(IOException e) {
       return "IO error: " + e.getMessage();
     } catch(CopperParserException e) {
       return "Error during parsing: " + e.getMessage(); 
     }
  }

%aux}

%lex{
    ignore terminal WS ::= /[ \t\n]*/;

    terminal t_prefix ::= /_/ in (), < (), > ();

    terminal t_rl ::= /rl/ in (), < (), > ();
    terminal t_lr ::= /lr/ in (), < (), > ();

    terminal left_to_right ::= // in (), < (), > ();
    terminal right_to_left ::= // in (), < (), > ();
    // NOTE: this is a dummy token, used onl for annotating the associativity for some rules.
    // Probably it is better using only the associativity of real tokens, that is working correctly,
    // but in any case this way of working confound the parser compiler, that generate
    // a parser, with an incorrect behavior.

%lex}

%cf{

    non terminal String EXPR;
    non terminal String RL;
    non terminal String LR;
    non terminal String PREFIX_LR;
    non terminal String PREFIX_RL;

    start with EXPR;

    precedence right t_prefix;

    precedence right left_to_right;
    precedence left right_to_left;

    EXPR ::=
      LR:id {: RESULT = id; :}
    | RL:id {: RESULT = id; :}
    ;


    LR ::= PREFIX_LR:x PREFIX_LR:y t_lr:v
    {: RESULT = x + y + "lr"; :}
    ;

    RL ::= PREFIX_RL:x PREFIX_RL:y t_rl:v
    {: RESULT = x + y + "rl"; :}
    ;
    /*
    TODO this rule has a syntax very similar to `LR`,
    because the prefix token is always "_", and only the associativity
    annotation differs. The only hint about the used variant is in the
    lookahead token "lr" vs "rl".

    These are difficult rules, and also a little artificial.

    The parser should reject to compile rules like these,
    or compile them correctly.

    The (apparent) current behavior seems to compiling them,
    but with a bad behavior.
    */

    PREFIX_LR ::=
      t_prefix
      {: RESULT = "_"; :}
      %prec left_to_right
    |
      {: RESULT = "~"; :}
      %prec left_to_right
    ;
    /*
    NOTE: we are using the `%prec` annotation on `left_to_right` instead of `t_prefix`.
     */

    PREFIX_RL ::=
      t_prefix
      {: RESULT = "_"; :}
      %prec right_to_left
    |
      {: RESULT = "~"; :}
      %prec right_to_left
    ;

%cf}
