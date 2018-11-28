package parsers;

%%
%parser MiniJavaGrammarParser

%lex{
    class keywords;
    
    ignore terminal WS ::= /([ \t\n]+)|(\/\/.*)|(/\*([^*]|\*[^/])*\*\/)/;
    
    terminal CLASS ::= /class/ 			in (keywords), < (), > ();
    terminal PUBLIC ::= /public/ 		in (keywords), < (), > ();
    terminal STATIC ::= /static/ 		in (keywords), < (), > ();
    terminal VOID ::= /void/ 			in (keywords), < (), > ();
    terminal MAIN ::= /main/ 			in (keywords), < (), > ();
    terminal STRING ::= /String/ 	    in (keywords), < (), > ();
    terminal EXTENDS ::= /extends/ 		in (keywords), < (), > ();
    terminal RETURN ::= /return/ 		in (keywords), < (), > ();
    terminal INT ::= /int/  			in (keywords), < (), > ();
    terminal BOOLEAN ::= /boolean/ 		in (keywords), < (), > ();
    terminal IF ::= /if/  				in (keywords), < (), > ();
    terminal ELSE ::= /else/  			in (keywords), < (), > ();
    terminal WHILE ::= /while/ 			in (keywords), < (), > ();
    terminal SYSTEM_OUT_PRINTLN
           ::= /System\.out\.println/ 	in (keywords), < (), > ();
    terminal LENGTH ::= /length/ 		in (keywords), < (), > ();
    terminal TRUE ::= /true/ 			in (keywords), < (), > ();
    terminal FALSE ::= /false/  		in (keywords), < (), > ();
    terminal NEW ::= /new/ 				in (keywords), < (), > ();
    terminal THIS ::= /this/			in (keywords), < (), > ();
    
    terminal COMMA ::= /,/;
    terminal DOT ::= /\./;
    terminal SEMICOLON ::= /;/;
    terminal LPAREN ::= /\(/;
    terminal RPAREN ::= /\)/;
    terminal LBRACK ::= /\[/;
    terminal RBRACK ::= /\]/;
    terminal LBRACE ::= /\{/;
    terminal RBRACE ::= /\}/;
    terminal ASSIGN ::= /=/;

    terminal NOT ::= /\!/;
    terminal PLUS ::= /\+/;
    terminal MINUS ::= /-/;
    terminal TIMES ::= /\*/;
    terminal AND ::= /&&/;
    terminal LT ::= /</;
    
    
    terminal INTEGER_LITERAL ::= /[1-9][0-9]*/;
    terminal ID ::= /[A-Za-z_][A-Za-z0-9_]*/ in (), < (keywords), > ();
    
%lex}

%cf{
    non terminal program;
    non terminal mainClass;
    non terminal classDecl, classDeclList;
    non terminal varDecl, varDeclList;
    non terminal methodDecl, methodDeclList;
    non terminal formalList;
    non terminal formalRest, formalRestList;
    non terminal type;
    non terminal statement, statementList;
    non terminal exp;
    non terminal expList;
    non terminal expRest, expRestList;
    non terminal nonIDType;
    
	precedence nonassoc LT;
    precedence left AND;
    precedence left PLUS, MINUS;
    precedence left TIMES;
    precedence left NOT;
	precedence nonassoc LBRACK;
    precedence nonassoc DOT;
    precedence right ID;
    
    start with program;
    
    program ::= mainClass classDeclList;
    
    mainClass ::=
       CLASS ID LBRACE PUBLIC STATIC VOID MAIN LPAREN STRING LBRACK RBRACK ID RPAREN LBRACE statement RBRACE RBRACE
       ;
    
    classDecl ::=
            CLASS ID LBRACE varDeclList methodDeclList RBRACE
          | CLASS ID EXTENDS ID LBRACE varDeclList methodDeclList RBRACE
          ;
          
    classDeclList ::=
            classDecl classDeclList
          |
          ;
    
    varDecl ::= type ID SEMICOLON;
    
    varDeclList ::=
            varDecl varDeclList
          | %prec ID
          ;
    
    methodDecl ::= PUBLIC type ID LPAREN formalList RPAREN LBRACE varDeclList statementList RETURN exp SEMICOLON RBRACE;
    
    methodDeclList ::=
            methodDecl methodDeclList
          |
          ;
    
    formalList ::= 
          type ID formalRestList
        | 
        ;
    
    formalRest ::= COMMA type ID;
    
    formalRestList ::=
          formalRest formalRestList
        |
        ; 
    
    nonIDType ::=
        INT LBRACK RBRACK
      | BOOLEAN
      | INT
      ;
      
    type ::=
        nonIDType
      | ID
      ;
      
    statement ::=
         LBRACE statementList RBRACE
       | IF LPAREN exp RPAREN statement ELSE statement
       | WHILE LPAREN exp RPAREN statement
       | SYSTEM_OUT_PRINTLN LPAREN exp RPAREN SEMICOLON
       | ID ASSIGN exp
       | ID LBRACK exp RBRACK ASSIGN exp SEMICOLON
       ;
       
    statementList ::=
          statement statementList
        |
        ; 

    exp ::=
       exp AND exp
     | exp LT exp
     | exp PLUS exp
     | exp MINUS exp
     | exp TIMES exp
     | exp LBRACK exp RBRACK
     | exp DOT LENGTH
     | exp DOT ID LPAREN expList RPAREN
     | INTEGER_LITERAL
     | TRUE
     | FALSE
     | ID
     | THIS
     | NEW INT LBRACK exp RBRACK
     | NEW ID LPAREN RPAREN
     | NOT exp
     | LPAREN exp RPAREN
     ;
     
    expList ::=
       exp expRestList
     |
     ;
     
    expRest ::= COMMA exp;
    
    expRestList ::=
          expRest expRestList
        |
        ; 
    
%cf}