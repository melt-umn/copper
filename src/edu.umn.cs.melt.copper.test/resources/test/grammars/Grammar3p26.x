package parsers;

// Grammar 3.26 from Appel's "Modern Compiler Interpretation in Java,"
// Second Edition.

%%
%parser Grammar3p26Parser

%lex{
    terminal eq         ::= /=/;
    terminal x          ::= /x/;
    terminal star       ::= /\*/;
    terminal ws         ::= /[ ]+/;
    terminal underscore ::= /_/; 
%lex}

%cf{
    non terminal S;
    non terminal V;
    non terminal E;
    
    start with S;
    
    S ::= E 
        | V eq E %layout (ws)
        ;
        
    E ::= V;
    
    V ::= x
        | star E %layout (underscore)
        ;

%cf}