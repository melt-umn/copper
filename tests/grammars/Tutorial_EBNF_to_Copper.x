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

%%
%parser Tutorial_EBNF_to_CopperParser

/*
Scope
=====

This tutorial shows the translation from a grammar specification in a
EBNF-like format, to the equivalent Copper grammar. This is the grammar
to translate:

````
stmts ::= stmt*

stmt ::=
   expr
 | var ":=" expr
 | "{" stmts "}"
 | "if" cond "then" stmt ("else" stmt)?

expr ::=
   expr "+" expr
 | expr "-" expr
 | expr "*" expr
 | expr "/" expr
 | "(" expr ")"
 | var
 | number

cond ::=
   "true"
 | "false"
 | expr ">" expr
 | expr "<" expr
 | expr "=" expr

var ::= "_"? "_"? identifier

identifier = [a-z][a-zA-Z0-9]*

number = [0-9]+
````

This EBNF grammar is readable and compact, but it contains various ambiguities.
The resulting Copper grammar will be longer, but it will have no ambiguities, and
importantly the Copper compiler will advise if there are some of them left. So
the Copper specification is both an implementation, and a validation of the
grammar.

The intended audience is someone who worked on top-down parsers, but is confused
on how solving ambiguities errors signaled from Copper, and bottom-up LALR
parser compilers.

Testing
=======

For compiling the grammar, execute: 

````
cd tests
ant Tutorial01
````

For running the resulting parser on some input file:

````
cd tests
java -classpath ../jar/CopperRuntime.jar:. edu.umn.cs.melt.copper.runtime.RunParser parsers.Tutorial_EBNF_to_CopperParser -f SOME-INPUT-FILE
````

TODO evaluate to use annotations like `%prec left_to_right` in case the tests on associativity and precedence are correct.

*/

%aux{

     public void runPostParseCode(Object root)
     {
         System.out.println(root);
     }
%aux}

%lex{
    /*
    Here we are defining the lexical scanner (lexer).

    The lexer is a procedure that scans a sequence of characters, and generates
    a sequence of tokens, that are then sent to the parser. Tokens are specified
    using regular-expressions.

    Many top-down parsers have no distinct lexer and parser phases.

    Copper is a bottom-up parser, but it uses a context-aware scanning. So also
    in Copper, like in case of top-down parsers, the type of recognized tokens
    depends from the parsing state, and from the production rules, and there are
    less conflicts respect a traditional bottom-up parser.
    */

    class keyword;

    ignore terminal WS ::= /[ \t\n]*/;

    terminal plus ::= /\+/ in (), < (), > ();
    terminal minus ::= /-/ in (), < (), > ();
    terminal times ::= /\*/ in (), < (), > ();
    terminal slash ::= /\// in (), < (), > ();

    terminal lp ::= /\(/ in (), < (), > ();
    terminal rp ::= /\)/ in (), < (), > ();

    terminal lc ::= /\{/ in (), < (), > ();
    terminal rc ::= /\}/ in (), < (), > ();

    terminal lt ::= /</ in (), < (), > ();
    terminal gt ::= />/ in (), < (), > ();
    terminal eq ::= /=/ in (), < (), > ();

    terminal t_true ::= /true/ in (keyword), < (), > ();
    terminal t_false ::= /false/ in (keyword), < (), > ();
    /*
    We put `true` and `false` tokens in the `keyword` class, because they are
    reserved words of the language. So when the lexer find `true` it does not
    consider it a variable, but the `t_true` token, e.g. a reserved keyword.
    */

    terminal t_if ::= /if/ in (keyword), < (), > ();
    terminal t_then ::= /then/ in (keyword), < (), > ();
    terminal t_else ::= /else/ in (keyword), < (), > ();

    terminal t_protected ::= /_/ in (), < (), > ();
    terminal t_assign ::= /:=/ in (), < (), > ();

    terminal String t_id ::= /[a-z][a-zA-Z0-9_]*/ in (), < (keyword), > ()
    {: RESULT = lexeme; :};
    /*
    We have converted to regular-expression syntax, the corresponding EBNF rule.

    The annotation `< (keyword)` says that `t_id` tokens have less
    priority respects tokens in class `keyword`. So a `t_id` is every variable
    name that is not in conflict with a reserved keyword of the language.
    */

    terminal String number ::= /0|([1-9][0-9]*)/ in (), < (), > ()
    {: RESULT = lexeme; :};

%lex}

%cf{

    non terminal String STMT;
    non terminal String STMTS;
    non terminal String EXPR;
    non terminal String COND;
    non terminal String ELSE_PART;
    non terminal String VAR;
    non terminal String IS_PROTECTED;

    start with STMTS;

    precedence left plus minus;
    precedence left times slash;
    /*
    NOTE: we are declaring that `minus` is `left` associative, but we are also
    saying that `time` and `slash` have more parsing priority respect `plus` and
    `minus`.
    */

    precedence right t_protected;
    precedence right t_else;

    STMTS ::=
      STMTS:ss STMT:s
      {: RESULT = ss + "\n" + s + "\n"; :}
    |
      {: RESULT = ""; :}
    ;
    /*
    This is the conversion of the EBNF rule `STMTS ::= STMT*`.

    We used a left recursion, instead of a more natural right recursion like
    `STMT STMTS`, because in LALR grammars, the left recursion is more
    efficient, and should always be preferred.

    The second alternative of the production, written as `| ` is an empty rule.
    It generates/recognizes the empty string. It is needed for managing the
    terminal case of the recursion, otherwise the `STMT` sequence will be infinite.
    */

    STMT ::=
       EXPR:e
       {: RESULT = e; :}
    |  VAR:var t_assign EXPR:e
       {: RESULT = var + " := " + e; :}
    |  lc STMTS:ss rc
       {: RESULT = "(" + ss + ")"; :}
    |  t_if COND:c t_then STMT:s1 ELSE_PART:s2
       {: RESULT = "(if " + c + " then " + s1 + s2 + ")"; :}
       /*
       We split the EBNF production

       ````
       "if" cond "then" stmt ("else" stmt)?
       ````

       in two Copper productions.
       */

   ;

   ELSE_PART ::=
      t_else STMT:s
      {: RESULT = " else " + s; :}
   |
      {: RESULT = ""; :}
      %prec t_else
      /*
      This is an empty rule, so an `ELSE_PART` can be empty/optional.

      We used `%prec t_else` for forcing the `right` associativity of the `t_else` token.
      Without doing this, we will encounter the dangling-else ambiguity. As usual the
      Copper compiler will inform us of the ambiguity, and it will refuse to
      generate the parser. So the grammar is validated/checked from
      the parser compiler at compile-time. In many top-down parsers these errors
      are not signaled, and so they are discovered only during testing or in
      production. In case of input strings like:

      ````
      if x > 10 then if x < 20 then s := 10 else s := 20 
      ````

      it is not clear for the parser if the last `else` is part of the first
      `if` or of the last `if`. So if it a code like this:

      ````
      if x > 10 then {
        if x < 20 then s := 10 else s := 20
      }
      ````

      or 

      ````
      if x > 10 then {
        if x < 20 then s := 10
      } else s := 20
      ````

      With the `%prec t_else` annotation we are saying that the
      `ELSE_PART` rules are left to right associative, that is the
      intended behavior on the majority of grammars.
      */
   ;

    EXPR ::=
      EXPR:left plus EXPR:right
      {: RESULT = "+(" + left + "," + right + ")"; :}
    | EXPR:left minus EXPR:right
      {: RESULT = "-(" + left + "," + right + ")"; :}
    | EXPR:left times EXPR:right
      {: RESULT = "*(" + left + "," + right + ")"; :}
    | EXPR:left slash EXPR:right
      {: RESULT = "/(" + left + "," + right + ")"; :}
    | lp EXPR:inner rp
      {: RESULT = "(" + inner + ")"; :}
    | number:n {: RESULT = n; :}
    | VAR:var {: RESULT = var; :}
    ;
    /*
    The specification for mathematical expressions is very similar to our
    original EBNF notation, and it is very compact and elegant.

    We have both left and right recursion on `EXPR` productions, but this does
    not pose problems to the LALR parser, on the contrary of many top-down
    parsers.

    The EBNF is ambiguous because in case of input strings like

    ````
    3 + 2 * 5
    ````

    it is not clear if they must be parsed like `+(3,*(2,5))` or `*(+(3,2),5)`.

    A top-down parser does not signal the problem, while Copper will signal the
    ambiguity.

    In this case we declared the priority of tokens `+` and `*` and so first
    it is matched the production rule with the token with higher priority (`*` in
    our case). So it is parsed like `+(3,*(2,5))`.

    Another source of ambiguity is in input strings like this

    ````
    1 + 2 + 3
    ````

    The parse tree can be both `+(1,+(2,3))` or `+(+(1,2),3)`. Copper will
    generate the TODO tree because we declarated `+` has left associative.
    */

    COND ::=
      t_true
      {: RESULT = "true"; :}
    | t_false
      {: RESULT = "false"; :}
    | EXPR:e1 gt EXPR:e2
      {: RESULT = ">(" + e1 + ", " + e2 + ")"; :}
    | EXPR:e1 lt EXPR:e2
      {: RESULT = "<(" + e1 + ", " + e2 + ")"; :}
    | EXPR:e1 eq EXPR:e2
      {: RESULT = "=(" + e1 + ", " + e2 + ")"; :}
    ;

    VAR ::= IS_PROTECTED:is_protected IS_PROTECTED:is_private t_id:v
    {: RESULT = is_protected + is_private + v; :}
    ;

    IS_PROTECTED ::=
      t_protected
      {: RESULT = "_"; :}
    |
      {: RESULT = "~"; :}
      %prec t_protected
    ;
    /*
    We split the EBNF rule 

    ````
    var ::= "_"? "_"? identifier
    ````

    in two Copper rules.

    We had to specify `%prec t_protected` because otherwise there is an
    ambiguity in the grammar, because the input string "_var" can be "_~var" or
    "~_var".

    A more verbose way for expanding the rule, for removing the ambiguity, can be

    ````
    VAR ::=
       t_protected t_protected t_id
     | t_protected t_id
     | t_id
    ````

    but doing so, we have a combinatorial explosion of the variants,
    and the semantic actions should be repeated for every variant.

   */

%cf}


