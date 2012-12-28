package parsers;

%%
%parser MathGrammarParser

%aux{
    public void runPostParseCode(Object root)
    {
    	System.out.println(root);
    }
%aux}

%lex{
    ignore terminal spaces ::= /[ ]*/ in (), < (), > (tabs,newlines)
    {:
        System.err.println("Matched " + lexeme.length() + " space(s) at " + _pos);
    :};
    
    ignore terminal tabs ::= /[\t]*/ in (), < (), > ()
    {:
        System.err.println("Matched " + lexeme.length() + " tab(s) at " + _pos);
    :};
    
    ignore terminal newlines ::= /[\n]*/ in (), < (), > ()
    {:
        System.err.println("Matched " + lexeme.length() + " newline(s) at " + _pos);
    :};
    
    terminal plus ::= /\+/ in (), < (), > ();
    terminal minus ::= /-/ in (), < (), > ();
    terminal times ::= /\*/ in (), < (), > ();
    terminal slash ::= /\// in (), < (), > ();
    terminal lp ::= /\(/ in (), < (), > ();
    terminal rp ::= /\)/ in (), < (), > ();
    terminal lb ::= /\[/ in (), < (), > ();
    terminal rb ::= /\]/ in (), < (), > ();
    terminal min ::= /m/ in (), < (), > (/*id*/);
    terminal max ::= /M/ in (), < (), > (/*id*/);
    terminal Float digit ::= /0|([1-9][0-9]*)/ in (), < (), > ()
    {:
        RESULT = Float.parseFloat(lexeme);
    :};
    terminal Float ndigit ::= /0|([1-9][0-9]*)/ in (), < (), > ()
    {:
        RESULT = new Float(-1.0 * Float.parseFloat(lexeme));
    :} %prefix minus;
    terminal Float id ::= /[A-Za-z_][A-Za-z0-9_]*/ in (), < (), > ()
    {:
        RESULT = new Float(0.0);
    :};
    
    disambiguate digits:(digit,ndigit) ::= digit;

%lex}

%cf{
    non terminal Float E;
    non terminal Float M;
    
    start with E;
    
    precedence left plus minus;
    precedence left times slash;
    precedence left min max;
    
    E ::=
       E:left plus E:right  {: RESULT = left + right; :} /* %prec times */
     | E:left minus E:right {: RESULT = left - right; :} /* %prec slash */
     | E:left times E:right {: RESULT = left * right; :} /* %prec plus */
     | E:left slash E:right {: RESULT = left / right; :} /* %prec minus */
     | lb M:left rb         {: RESULT = left; :} %layout ()
     | lp E:inner rp        {: RESULT = inner; :}
     | digit:dig            {: RESULT = dig; :}
     | ndigit:dig           {: RESULT = dig; :}
     | id:id                {: RESULT = id; :}
     ;
     
    M ::=
       M:left min M:right {: RESULT = Math.min(left,right); :} %layout ()
     | M:left max M:right {: RESULT = Math.max(left,right); :} %layout ()
     | digit:dig {: RESULT = dig; :}
     ;
%cf}