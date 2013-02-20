package parsers;
import java.util.LinkedList;

// Grammar 3.23 from Appel's "Modern Compiler Interpretation in Java,"
// Second Edition.

%%
%parser Grammar3p23Parser

%attr LinkedList<String> xList;

%aux{
	LinkedList<String> finalXList;

	public void runPostParseCode(Object __root)
	{
		System.out.println(finalXList);
	}
%aux}

%init{
     xList = new LinkedList<String>();
%init}

%lex{

    class main,punc,var;

    ignore terminal ws ::= /[ ]*/ in (main,punc)
    {:
         System.err.println("[space] ");
    :};
    
    terminal plus ::= /\+/ in (main,punc)
    {:
         System.err.println("[+]");
    :};
    
    terminal minus ::= /-/ in (main,punc)
    {:
         System.err.println("[-]");
    :};
    
    terminal x ::= /x/ in (main,var)
    {:
         System.err.println("[x]");
         xList.add("x");
    :};
    
    terminal x2 ::= /x/ in (main,var)
    {:
         System.err.println("[x2]");
         xList.poll();
    :} %prefix minus;
    
    disambiguate xs:(x,x2) ::= x;

%lex}


%cf{

    non terminal E;
    non terminal T;
    start with E;
    
    E ::=
      T plus E
      {:
           finalXList = xList;
      :}
    | T
     {:
           finalXList = xList;
     :}
    ;
    
    T ::=
      x 
    | x2
    ;

%cf}