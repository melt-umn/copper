/*
 Copyright 2016 Massimo Zaniboni <massimo.zaniboni@gmail.com>

 This file is part of Copper.

 Copper is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Copper is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with Copper.  If not, see <http://www.gnu.org/licenses/>.
*/

package parsers;

import java.io.IOException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import java.util.LinkedList; 

%%
%parser TestPrecedences02Parser

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
    tests.add("_~lr");

    tests.add("__lr");
    tests.add("__lr");

    tests.add("rl");
    tests.add("~~rl");

    tests.add("#rl");
    tests.add("~#rl");

    tests.add("##rl");
    tests.add(null);
    // NOTE: this fails (correctly) because `#` terminal associate to left.

    boolean allOk = testAllInputStrings(tests);

    if (!allOk) {
      System.exit(1);
    }

  }

  /**
   * Execute tests, and report on stderr the problems.
   *
   * @param inputStrings followed by expected result. Use null for no expected result (parsing error).
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
          if (expectedResult == null) {
             System.err.println("Input string \"" + inputString + "\" returned parsing result \"" + result + "\", but it should no parse.\n");
          } else {
            System.err.println("Input string \"" + inputString + "\" returned parsing result \"" + result + "\" instead of expected result \"" + expectedResult + "\"\n");
          }
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
     TestPrecedences02Parser parser = new TestPrecedences02Parser();
     try {
       String result = parser.parse(inputString);
       if (expectedResult != null && result.equals(expectedResult)) {
         return null;
       } else {
         return result;
       }
     } catch(IOException e) {
       return "IO error: " + e.getMessage();
     } catch(CopperParserException e) {
       if (expectedResult == null) {
         return null;
       } else {
         return "Error during parsing: " + e.getMessage();
       }
     }
  }

%aux}

%lex{
    ignore terminal WS ::= /[ \t\n]*/;

    terminal t_prefix_lr ::= /_/ in (), < (), > ();
    terminal t_prefix_rl ::= /#/ in (), < (), > ();

    terminal t_rl ::= /rl/ in (), < (), > ();
    terminal t_lr ::= /lr/ in (), < (), > ();

%lex}

%cf{

    non terminal String EXPR;
    non terminal String RL;
    non terminal String LR;
    non terminal String PREFIX_LR;
    non terminal String PREFIX_RL;

    start with EXPR;

    precedence right t_prefix_lr;
    precedence left  t_prefix_rl;

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

    PREFIX_LR ::=
      t_prefix_lr
      {: RESULT = "_"; :}
    |
      {: RESULT = "~"; :}
      %prec t_prefix_lr
    ;

    PREFIX_RL ::=
      t_prefix_rl
      {: RESULT = "#"; :}
    |
      {: RESULT = "~"; :}
      %prec t_prefix_rl
    ;

%cf}
