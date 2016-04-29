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
%parser TestPrecedences01Parser

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

    tests.add("lr1");
    tests.add("~~lr");

    // TODO: fail because it is associated `left`, into "~_lr", instead of `right` as expected.
    tests.add("_lr1");
    tests.add("_~lr");

    // TODO: fail because it is not able to parse "__lr", but only "_lr".
    // This is a recurring problem, also in other tests involving `left`.
    // associations. Maybe inspect on other simpler tests involving only this particular behavior.
    // TODO: I should study better LALR theory, maybe this is the correct behavior.
    tests.add("__lr1");
    tests.add("__lr");

    tests.add("lr2");
    tests.add("~~lr");

    tests.add("#lr2");
    tests.add("#~lr");

    tests.add("##lr2");
    tests.add("##lr");

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
     TestPrecedences01Parser parser = new TestPrecedences01Parser();
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

    terminal t_prefix1 ::= /_/ in (), < (), > ();
    terminal t_lr1 ::= /lr1/ in (), < (), > ();
    terminal left_to_right ::= // in (), < (), > ();

    terminal t_prefix2 ::= /#/ in (), < (), > ();
    terminal t_lr2 ::= /lr2/ in (), < (), > ();

%lex}

%cf{

    non terminal String EXPR;
    non terminal String LR1;
    non terminal String LR2;
    non terminal String PREFIX_LR1;
    non terminal String PREFIX_LR2;

    start with EXPR;

    precedence left t_prefix1;
    precedence right left_to_right;

    precedence right t_prefix2;

    EXPR ::=
      LR1:id {: RESULT = id; :}
    | LR2:id {: RESULT = id; :}
    ;

    LR1 ::= PREFIX_LR1:x PREFIX_LR1:y t_lr1:v
    {: RESULT = x + y + "lr"; :}
    ;

    PREFIX_LR1 ::=
      t_prefix1
      {: RESULT = "_"; :}
      %prec left_to_right
    |
      {: RESULT = "~"; :}
      %prec left_to_right
    ;
    /*
    TODO: we are using the `%prec left_to_right` instead
    of the default `t_prefix` associativity.
    But the compiler will use in this case the left associativity of `t_prefix`,
    instead of the right associativity of `left_to_right`.
    */

    LR2 ::= PREFIX_LR2:x PREFIX_LR2:y t_lr2:v
    {: RESULT = x + y + "lr"; :}
    ;

    PREFIX_LR2 ::=
      t_prefix2
      {: RESULT = "#"; :}
      %prec left_to_right
    |
      {: RESULT = "~"; :}
      %prec t_prefix2
    ;
    /* NOTE: in this case the behavior is correct. */
%cf}
