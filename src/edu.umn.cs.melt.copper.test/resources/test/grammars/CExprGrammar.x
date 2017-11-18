package parsers;
import java.util.Hashtable;

%%
%parser CExprGrammarParser

%attr Hashtable<String,Double> env;
%attr Integer nextUnnamed;

%init{
    env = new Hashtable<String,Double>();
    nextUnnamed = 0;
%init}

%aux{
     public void runPostParseCode(Object root)
     {
         System.out.println(root);
     }
%aux}

/* Lexical syntax */
%lex{

    /* Whitespace */
    ignore terminal WS ::= /[ \t\n]*/;

    /* Expression terminals */
    terminal PLUS                 ::= /\+/;
    terminal UNARY_MINUS          ::= /-/;
    terminal BINARY_MINUS         ::= /-/;
    terminal TIMES                ::= /\*/;
    terminal DIVIDE               ::= /\//;
    terminal LPAREN               ::= /\(/;
    terminal RPAREN               ::= /\)/;
    terminal Double NUMBER        ::= /0|([1-9][0-9]*)(\.[0-9]+)?/ {: RESULT = Double.parseDouble(lexeme); :};
    terminal Double ASSIGNED_ID   ::= /[A-Za-z][A-Za-z0-9_]*/
    {:
        RESULT = env.get(lexeme);
    :};
    terminal String UNASSIGNED_ID ::= /[A-Za-z][A-Za-z0-9_]*/
    {:
       RESULT = lexeme;
    :};
    
    /* Statement terminals */
    terminal ASSIGN         ::= /=/;
    terminal SEMI           ::= /;/;

    /* Disambiguation group */
    disambiguate ids:(ASSIGNED_ID,UNASSIGNED_ID)
    {:
        if(env.containsKey(lexeme)) return ASSIGNED_ID;
        else return UNASSIGNED_ID;
    :};
    
%lex}

/* Context-free syntax */
%cf{

    /* Nonterminals */
    non terminal stmt;
    non terminal Double stmts,expr;

    /* Start symbol */
    start with stmts;

    /* Precedences */
    precedence left PLUS, BINARY_MINUS;
    precedence left TIMES, DIVIDE;
    precedence left UNARY_MINUS;

    stmts ::=
      stmts:hd stmt:tl   {: RESULT = (env.containsKey("RESULT") ? env.get("RESULT") : 0.0/0.0); :}
    | stmt:s             {: RESULT = (env.containsKey("RESULT") ? env.get("RESULT") : 0.0/0.0);  :}
    ;

    stmt ::=
      UNASSIGNED_ID:i ASSIGN expr:e SEMI      {: env.put(i,e); :}
    | expr:e SEMI                             {: env.put("_e" + (nextUnnamed++),e); :}
    ;

    /* Expressions */
    expr ::=
      expr:l PLUS expr:r           {: RESULT = l + r;    :}
    | expr:l BINARY_MINUS expr:r   {: RESULT = l - r;    :}
    | expr:l TIMES expr:r          {: RESULT = l * r;    :}
    | expr:l DIVIDE expr:r         {: RESULT = l / r;    :}
    | UNARY_MINUS expr:e           {: RESULT = -1.0 * e; :}
        %layout ()
    | LPAREN expr:e RPAREN         {: RESULT = e;        :}
    | NUMBER:n                     {: RESULT = n;        :}
    | ASSIGNED_ID:i                {: RESULT = i;        :}
    | UNASSIGNED_ID:u
      {:
          error(_pos,"Undefined symbol '" + u + "'");
          RESULT = 0.0/0.0;
      :}
    ;

%cf}