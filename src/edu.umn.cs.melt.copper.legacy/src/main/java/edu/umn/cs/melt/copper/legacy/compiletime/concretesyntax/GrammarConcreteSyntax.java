package edu.umn.cs.melt.copper.legacy.compiletime.concretesyntax;

import java.util.HashSet;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminalAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.ParserAttribute;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.ProductionAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.CharacterSet;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.Choice;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.Concatenation;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.KleeneStar;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class GrammarConcreteSyntax
{
	// Install in compiletime.regex.CharacterSet and the NFAs a more efficient way of
	// storing character sets so as to expand Copper's lexical range beyond this.
	/** All typable characters, to be used in inverted character-set regexes. */
	public static final String UNIVERSAL_CHARACTER_SET = "`1234567890-=~!@#$%^&*()_+\tqwertyuiop[]\\QWERTYUIOP{}|asdfghjkl;'\b\f\r\nASDFGHJKL:\"zxcvbnm,./ZXCVBNM<>? ";
	
	public static GrammarSource grammar;

	public static HashSet<Terminal> epsLayout = new HashSet<Terminal>();
	public static HashSet<Terminal> wsLayout = new HashSet<Terminal>();
	
	public static final Terminal nonterm_tok = new Terminal("nonterm_tok");
	public static final Terminal terminal_tok = new Terminal("terminal_tok");
	public static final Terminal symbol_tok = new Terminal("symbol_tok");
	public static final Terminal prodname_tok = new Terminal("prodname_tok");
	public static final Terminal precclass_tok = new Terminal("precclass_tok");
	public static final Terminal attrname_tok = new Terminal("attrname_tok");
	public static final Terminal groupname_tok = new Terminal("groupname_tok");
	public static final Terminal prec_number = new Terminal("prec_number");
	public static final Terminal comment_line = new Terminal("comment_line");
	public static final Terminal t_decl = new Terminal("t_decl");
	public static final Terminal tok_decl = new Terminal("tok_decl");
	public static final Terminal nt_decl = new Terminal("nt_decl");
	public static final Terminal prec_decl = new Terminal("prec_decl");
	public static final Terminal submit_decl = new Terminal("submit_decl");
	public static final Terminal dominates_decl = new Terminal("dominates_decl"); 
	public static final Terminal prefix_decl = new Terminal("prefix_decl");
	public static final Terminal default_decl = new Terminal("default_decl");
	public static final Terminal operator_decl = new Terminal("operator_decl");
	public static final Terminal assoc_decl = new Terminal("assoc_decl");
	public static final Terminal assoctypes = new Terminal("assoctypes");
	public static final Terminal prod_decl = new Terminal("prod_decl");
	public static final Terminal precclass_decl = new Terminal("precclass_decl");
	public static final Terminal layout_decl = new Terminal("layout_decl");
	public static final Terminal bnf_decl = new Terminal("bnf_decl");
	public static final Terminal goesto = new Terminal("goesto");
	public static final Terminal attr_decl = new Terminal("attr_decl");
	public static final Terminal attr_type_decl = new Terminal("attr_type_decl");
	public static final Terminal code_decl = new Terminal("code_decl");
	public static final Terminal attr_type_base = new Terminal("attr_type_base");
	public static final Terminal embedded_code = new Terminal("embedded_code");
	public static final Terminal ambiguous_decl = new Terminal("ambiguous_decl");
	public static final Terminal group_decl = new Terminal("group_decl");
	public static final Terminal members_decl = new Terminal("members_decl");
	public static final Terminal start_decl = new Terminal("start_decl");
	public static final Terminal plus = new Terminal("plus");
	public static final Terminal star = new Terminal("star");
	public static final Terminal question = new Terminal("question");
	public static final Terminal bar = new Terminal("bar");
	public static final Terminal dash = new Terminal("dash");
	public static final Terminal colon = new Terminal("colon");
	public static final Terminal not = new Terminal("not");
	public static final Terminal lbrack = new Terminal("lbrack");
	public static final Terminal rbrack = new Terminal("rbrack");
	public static final Terminal lparen = new Terminal("lparen");
	public static final Terminal rparen = new Terminal("rparen");
	public static final Terminal lbrace = new Terminal("lbrace");
	public static final Terminal rbrace = new Terminal("rbrace");
	public static final Terminal wildcard = new Terminal("wildcard");
	public static final Terminal character = new Terminal("character");
	public static final Terminal termname = new Terminal("termname");
	public static final Terminal escaped = new Terminal("escaped");
	public static final Terminal ws = new Terminal("ws");
	public static final Terminal newline = new Terminal("newline");
	public static final Terminal grammar_decl = new Terminal("grammar_decl");
	public static final Terminal grammarname_tok = new Terminal("grammarname_tok");
	public static final Terminal grammar_name_decl = new Terminal("grammar_name_decl");
	public static final Terminal spectype_decl = new Terminal("spectype_decl");
	public static final Terminal spectypes = new Terminal("spectypes");
	public static final Terminal grammar_version = new Terminal("grammar_version");
	
	public static final NonTerminal GrammarFile = new NonTerminal("GrammarFile");
	public static final NonTerminal Grammar = new NonTerminal("Grammar");
	public static final NonTerminal NTLine = new NonTerminal("NTLine");
	public static final NonTerminal NTSeq = new NonTerminal("NTSeq");
	public static final NonTerminal TSeq = new NonTerminal("TSeq");
	public static final NonTerminal TLine = new NonTerminal("TLine");
	public static final NonTerminal TokLine = new NonTerminal("TokLine");
	public static final NonTerminal OpLine = new NonTerminal("OpLine");
	public static final NonTerminal ProdLine = new NonTerminal("ProdLine");
	public static final NonTerminal StartLine = new NonTerminal("StartLine");
	public static final NonTerminal AttrLine = new NonTerminal("AttrLine");
	public static final NonTerminal GroupLine = new NonTerminal("GroupLine");
	public static final NonTerminal DefaultTCodeLine = new NonTerminal("DefaultTCodeLine");
	public static final NonTerminal DefaultProdCodeLine = new NonTerminal("DefaultProdCodeLine");
	public static final NonTerminal PrecClassSeq = new NonTerminal("PrecClassSeq");
	public static final NonTerminal SymSeq = new NonTerminal("SymSeq");
	public static final NonTerminal Regex_Root = new NonTerminal("Regex_Root");
	public static final NonTerminal AttrTypeRoot = new NonTerminal("AttrTypeRoot");

	public static final NonTerminal Regex_R = new NonTerminal("Regex_R");
	public static final NonTerminal Regex_DR = new NonTerminal("Regex_DR");
	public static final NonTerminal Regex_UR = new NonTerminal("Regex_UR");
	public static final NonTerminal Regex_RR = new NonTerminal("Regex_RR");
	public static final NonTerminal Regex_G = new NonTerminal("Regex_G");
	public static final NonTerminal Regex_RG = new NonTerminal("Regex_RG");
	public static final NonTerminal Regex_UG = new NonTerminal("Regex_UG");
	public static final NonTerminal Regex_CHAR = new NonTerminal("Regex_CHAR");

	public static final Production GrammarFiletoGrammar = Production.production(Symbol.symbol("GrammarFiletoGrammar"),GrammarFile,grammar_decl,grammar_name_decl,grammarname_tok,spectype_decl,spectypes,grammar_version,newline,Grammar);

	public static final Production GrammartoNTLine = Production.production(Symbol.symbol("GrammartoNTLine"),Grammar,NTLine,Grammar);
	public static final Production GrammartoTLine = Production.production(Symbol.symbol("GrammartoTLine"),Grammar,TLine,Grammar);
	public static final Production GrammartoTokLine = Production.production(Symbol.symbol("GrammartoTokLine"),Grammar,TokLine,Grammar);
	public static final Production GrammartoOpLine = Production.production(Symbol.symbol("GrammartoOpLine"),Grammar,OpLine,Grammar);
	public static final Production GrammartoProdLine = Production.production(Symbol.symbol("GrammartoProdLine"),Grammar,ProdLine,Grammar);
	public static final Production GrammartoStartLine = Production.production(Symbol.symbol("GrammartoStartLine"),Grammar,StartLine,Grammar);
	public static final Production GrammartoAttrLine = Production.production(Symbol.symbol("GrammartoAttrLine"),Grammar,AttrLine,Grammar);
	public static final Production GrammartoGroupLine = Production.production(Symbol.symbol("GrammartoGroupLine"),Grammar,GroupLine,Grammar);
	public static final Production GrammartoDefaultTCodeLine = Production.production(Symbol.symbol("GrammartoDefaultTCodeLine"),Grammar,DefaultTCodeLine,Grammar);
	public static final Production GrammartoDefaultProdCodeLine = Production.production(Symbol.symbol("GrammartoDefaultProdCodeLine"),Grammar,DefaultProdCodeLine,Grammar);
	public static final Production GrammartoCommentLine = Production.production(Symbol.symbol("GrammartoCommentLine"),Grammar,comment_line,newline,Grammar);
	public static final Production GrammartoEps = Production.production(Symbol.symbol("GrammartoEps"),Grammar);

	public static final Production NTLineMain = Production.production(Symbol.symbol("NTLineMain"),NTLine,nt_decl,NTSeq,newline);
	public static final Production NTSeqMain = Production.production(Symbol.symbol("NTSeqMain"),NTSeq,nonterm_tok,NTSeq);
	public static final Production NTSeqEps = Production.production(Symbol.symbol("NTSeqEps"),NTSeq);
	public static final Production TSeqMain = Production.production(Symbol.symbol("TSeqMain"),TSeq,terminal_tok,TSeq);
	public static final Production TSeqEps = Production.production(Symbol.symbol("TSeqEps"),TSeq);
	public static final Production TLineMain = Production.production(Symbol.symbol("TLineMain"),TLine,t_decl,terminal_tok,Regex_Root);
	public static final Production TokLineMain = Production.production(Symbol.symbol("TokLineMain"),TokLine,tok_decl,terminal_tok,precclass_decl,lbrace,PrecClassSeq,rbrace,prec_decl,submit_decl,lbrace,SymSeq,rbrace,dominates_decl,lbrace,SymSeq,rbrace,prefix_decl,lbrace,TSeq,rbrace,code_decl,embedded_code,newline);
	public static final Production OpLineMain = Production.production(Symbol.symbol("OpLineMain"),OpLine,operator_decl,terminal_tok,precclass_decl,precclass_tok,prec_decl,prec_number,assoc_decl,assoctypes,newline);
	public static final Production ProdLineMain = Production.production(Symbol.symbol("ProdLineMain"),ProdLine,prod_decl,prodname_tok,precclass_decl,precclass_tok,prec_decl,prec_number,operator_decl,lbrace,TSeq,rbrace,layout_decl,lbrace,TSeq,rbrace,code_decl,embedded_code,bnf_decl,nonterm_tok,goesto,SymSeq,newline);
	public static final Production StartLineMain = Production.production(Symbol.symbol("StartLineMain"),StartLine,start_decl,nonterm_tok,layout_decl,lbrace,TSeq,rbrace,newline);

	public static final Production AttrLineMain = Production.production(Symbol.symbol("AttrLineMain"),AttrLine,attr_decl,attrname_tok,attr_type_decl,AttrTypeRoot,code_decl,embedded_code,newline);
	public static final Production AttrTypeList = Production.production(Symbol.symbol("AttrTypeList"),AttrTypeRoot,lbrack,AttrTypeRoot,rbrack);
	public static final Production AttrTypeBase = Production.production(Symbol.symbol("AttrTypeBase"),AttrTypeRoot,attr_type_base);
	
	public static final Production GroupLineMain = Production.production(Symbol.symbol("GroupLineMain"),GroupLine,ambiguous_decl,t_decl,group_decl,groupname_tok,code_decl,embedded_code,members_decl,TSeq,newline);
	
	public static final Production DefaultTCodeLineMain = Production.production(Symbol.symbol("DefaultTLineMain"),DefaultTCodeLine,default_decl,t_decl,code_decl,embedded_code,newline);

	public static final Production DefaultProdCodeLineMain = Production.production(Symbol.symbol("DefaultProdLineMain"),DefaultProdCodeLine,default_decl,prod_decl,code_decl,embedded_code,newline);

	public static final Production SymSeqMain = Production.production(Symbol.symbol("SymSeqMain"),SymSeq,symbol_tok,SymSeq);
	public static final Production SymSeqEps = Production.production(Symbol.symbol("SymSeqEps"),SymSeq);

	public static final Production PrecClassSeqMain = Production.production(Symbol.symbol("PrecClassSeqMain"),PrecClassSeq,precclass_tok,PrecClassSeq);
	public static final Production PrecClassSeqEps = Production.production(Symbol.symbol("PrecClassSeqEps"),PrecClassSeq);

	public static final Production RoottoR = Production.production(Symbol.symbol("RoottoR"),Regex_Root,colon,Regex_R,newline);
	public static final Production Roottoeps = Production.production(Symbol.symbol("Roottoeps"),Regex_Root,colon,newline);
	
	public static final Production RtoDR = Production.production(Symbol.symbol("RtoDR"),Regex_R,Regex_DR);
	public static final Production RtoDR_bar_R = Production.production(Symbol.symbol("RtoDR_bar_R"),Regex_R,Regex_DR,bar,Regex_R);
	public static final Production DRtoUR_RR = Production.production(Symbol.symbol("DRtoUR_RR"),Regex_DR,Regex_UR,Regex_RR);
	public static final Production DRtoUR_star_RR = Production.production(Symbol.symbol("DRtoUR_star_RR"),Regex_DR,Regex_UR,star,Regex_RR);
	public static final Production DRtoUR_plus_RR = Production.production(Symbol.symbol("DRtoUR_plus_RR"),Regex_DR,Regex_UR,plus,Regex_RR);
	public static final Production DRtoUR_question_RR = Production.production(Symbol.symbol("DRtoUR_question_RR"),Regex_DR,Regex_UR,question,Regex_RR);
	public static final Production RRtoDR = Production.production(Symbol.symbol("RRtoDR"),Regex_RR,Regex_DR);
	public static final Production RRtoeps = Production.production(Symbol.symbol("RRtoeps"),Regex_RR);
	public static final Production URtoCHAR = Production.production(Symbol.symbol("URtoCHAR"),Regex_UR,Regex_CHAR);
	public static final Production URtowildcard = Production.production(Symbol.symbol("URtowildcard"),Regex_UR,wildcard);
	public static final Production URtolb_G_rb = Production.production(Symbol.symbol("URtolb_G_rb"),Regex_UR,lbrack,Regex_G,rbrack);
	public static final Production URtolb_not_G_rb = Production.production(Symbol.symbol("URtolb_not_G_rb"),Regex_UR,lbrack,not,Regex_G,rbrack);
	public static final Production URtomacro = Production.production(Symbol.symbol("URtomacro"),Regex_UR,lbrack,colon,termname,colon,rbrack);
	public static final Production URtolp_R_rp = Production.production(Symbol.symbol("URtolp_R_rp"),Regex_UR,lparen,Regex_R,rparen);
	public static final Production GtoUG_RG = Production.production(Symbol.symbol("GtoUG_RG"),Regex_G,Regex_UG,Regex_RG);
	public static final Production UGtoCHAR = Production.production(Symbol.symbol("UGtoCHAR"),Regex_UG,Regex_CHAR);
	public static final Production UGtoCHAR_dash_CHAR = Production.production(Symbol.symbol("UGtoCHAR_dash_CHAR"),Regex_UG,Regex_CHAR,dash,Regex_CHAR);
	public static final Production RGtoG = Production.production(Symbol.symbol("RGtoG"),Regex_RG,Regex_G);
	public static final Production RGtoeps = Production.production(Symbol.symbol("RGtoeps"),Regex_RG);
	public static final Production CHARtochar = Production.production(Symbol.symbol("CHARtochar"),Regex_CHAR,character);
	public static final Production CHARtoescaped = Production.production(Symbol.symbol("CHARtoescaped"),Regex_CHAR,escaped);

	static
	{
		String GrammarFiletoGrammarCode = 
		    "edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition pos = (edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition) _children[0];\n//	    String spectype = (String) _children[4];\n	    String postParseCode = \"\";\n//	    if(spectype.equals(\"LALR1-silver.haskell\")) postParseCode = \"printParseTree(System.out,false,(edu.umn.cs.melt.copper.runtime.parsetree.stripped.StrippedParseTreeNode) root,\\\"\\\");\";\n//	    else if(spectype.equals(\"LALR1-pretty\")) postParseCode = \"printParseTree(System.out,true,(edu.umn.cs.melt.copper.runtime.parsetree.stripped.StrippedParseTreeNode) root,\\\"\\\");\";\n	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(\n	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	             edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.GRAMMAR_NAME,\n	             edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[2]),\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	              \"location\",\n	              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null))),\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	              \"spectype\",\n	              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[4])),\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(\n	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	             edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,\n	             edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(\" postParseCode \"),\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	              \"location\",\n	              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	              \"code\",\n	              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) postParseCode))),\n	             _children[7]));\n";
		String GrammartoNTLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoTLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoTokLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoOpLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoProdLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoStartLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoAttrLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoGroupLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoDefaultTCodeLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoDefaultProdCodeLineCode = 
		    "RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);";
		String GrammartoCommentLineCode = 
		    "RESULT = _children[2];";
		String GrammartoEpsCode = 
		    "RESULT = null;";
		String NTLineMainCode = 
		    "RESULT = _children[1];";
		String NTSeqMainCode = 
		    "edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition pos = ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,String>) _children[0]).first();\n	    Object child0 = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	                     edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.NON_TERMINAL,	                     edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,String>) _children[0]).second()),\n	                     edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	                      \"location\",\n	                      edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null)));\n	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(child0,_children[1]);";
		String NTSeqEpsCode = 
		    "RESULT = null;";
		String TSeqMainCode = 
		    "((java.util.LinkedList<String>) _children[1]).addFirst((String) _children[0]);\n	    RESULT = _children[1];";
		String TSeqEpsCode = 
		    "RESULT = new java.util.LinkedList<String>();";
		String TLineMainCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"location\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"regex\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[2])));";
		String TokLineMainCode = 
		    "boolean noPrefix = false;\n	    if(((java.util.LinkedList<String>) _children[17]).size() > 1) error(_pos,\"Terminals cannot have more than one prefix\");\n	    else if(((java.util.LinkedList<String>) _children[17]).isEmpty())\n	    {\n	        noPrefix = true;\n	    }\n	    java.util.LinkedList<String> classes = (java.util.LinkedList<String>) _children[4];\n	    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode node = null;\n	    for(String tClass : classes)\n	    {\n	        edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode newNode =\n	         new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	          edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,\n	          edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(tClass));\n	        node = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(newNode,node);\n	    }\n	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(\n	           node,\n	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"classes\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[4])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"submits\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[9])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"dominates\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[13])),\n	            noPrefix ? null : edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"prefix\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) ((java.util.LinkedList<String>) _children[17]).getFirst())),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"code\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[20]))));";	            
		String PrecClassSeqMainCode = 
		    "((java.util.LinkedList<String>) _children[1]).addFirst((String) _children[0]);\n	    RESULT = _children[1];";
		String PrecClassSeqEpsCode = 
		    "RESULT = new java.util.LinkedList<String>();";
		String OpLineMainCode = 
		    "edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition pos = (edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition) _children[0];\n	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(\n	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[3]),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"location\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null))),\n	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"operatorClass\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"operatorPrecedence\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[5])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"operatorAssociativity\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[7]))));";
		String ProdLineMainCode = 
		    "edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition pos = (edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition) _children[0];\n	    boolean noOperator = false;\n	    if(((java.util.LinkedList<String>) _children[8]).size() > 1) error(_pos,\"Productions cannot have more than one custom operator\");\n	    else if(((java.util.LinkedList<String>) _children[8]).isEmpty()) noOperator = true;\n	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(\n	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[3]),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"location\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null))),\n	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.PRODUCTION,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"location\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null)),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"class\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[3])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"precedence\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[5])),\n	            noOperator ? null : edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"operator\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) ((java.util.LinkedList<String>) _children[8]).getFirst())),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"layout\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[12])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"code\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[15])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"LHS\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,String>) _children[17]).second())),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"RHS\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[19]))));";
		String StartLineMainCode = 
			"edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition pos = (edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition) _children[0];\n	    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.NON_TERMINAL,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,String>) _children[1]).second()),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"startLayout\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[4])),edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n		             \"isStart\",\n		             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) true)));";
		String AttrLineMainCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.PARSER_ATTRIBUTE,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"location\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"type\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"code\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[5])));\n";
		String AttrTypeListCode = 
		     "RESULT = \"java.util.LinkedList< \" + _children[1] + \" >\";";
		String AttrTypeBaseCode = 
		    "RESULT = _children[0];";
		String GroupLineMainCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DISAMBIGUATION_GROUP,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[3]),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"location\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"code\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[5])),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"members\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[7])));";
		String DefaultTCodeLineMainCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(\" defaultTermCode \"),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"location\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"code\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])));";
		String DefaultProdCodeLineMainCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(\" defaultProdCode \"),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"location\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),\n	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(\n	             \"code\",\n	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])));";
		String SymSeqMainCode = 
		    "((java.util.LinkedList<String>) _children[1]).addFirst((String) _children[0]);\n	    RESULT = _children[1];";
		String SymSeqEpsCode = 
		    "RESULT = new java.util.LinkedList<String>();";
		String RoottoRCode = 
		    "RESULT = _children[1];";
		String RoottoepsCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();";
		String RtoDRCode = 
		    "RESULT = _children[0];";
		String RtoDR_bar_RCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice(\n	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0],\n	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);";
		String DRtoUR_RRCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(\n	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0],\n	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[1]);";
		String DRtoUR_star_RRCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(\n	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar(\n	             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),\n	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);";
		String DRtoUR_plus_RRCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(\n	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(\n	             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),\n	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar(\n	             ((edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]).clone()),\n	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);\n";
		String DRtoUR_question_RRCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(\n	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice(\n	             new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString(),\n	             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),\n	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);";
		String RRtoDRCode = 
		    "RESULT = _children[0];";
		String RRtoepsCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();";
		String URtoCHARCode = 
		    "RESULT = _children[0];";
		String URtowildcardCode = 
			"edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet Newline =\n		       new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet(\n		        edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,'\\n');\n	    RESULT = Newline.invertSet(edu.umn.cs.melt.copper.compiletime.concretesyntax.);";
		String URtolb_G_rbCode = 
		    "RESULT = _children[1];";
		String URtolb_not_G_rbCode = 
		    "if(_children[2] instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet)\n		{\n			edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet CGNode =\n			   (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) _children[2];\n			RESULT = CGNode.invertSet(edu.umn.cs.melt.copper.compiletime.concretesyntax.);\n		}\n		else\n		{\n			error(_pos,\"Type error in regex\");\n			RESULT = null;\n		}";
		String URtomacroCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.MacroHole(\n	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal((String) _children[2]));";
		String URtolp_R_rpCode = 
		    "RESULT = _children[1];";
		String GtoUG_RGCode = 
			"edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex UGNode =\n			(edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0];\n		edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex RGNode =\n			(edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[1];\n		\n		if(UGNode instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet &&\n		   (RGNode == null || RGNode instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString))\n		{\n			RESULT = UGNode;\n		}\n		else if(UGNode instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet &&\n		        RGNode instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet)\n		{\n			RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.union(\n					 (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) UGNode,\n					 (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) RGNode);\n		}\n		else\n		{\n			error(_pos,\"Type error in regex\");\n			RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();\n		}";
		String UGtoCHARCode = 
		    "RESULT = _children[0];";
		String UGtoCHAR_dash_CHARCode = 
			"edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex characterNode1 =\n			(edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0];\n		edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex characterNode2 =\n			(edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2];\n		\n		if(characterNode1 instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet &&\n		   characterNode2 instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet)\n		{\n			char lowerLimit = Character.MAX_VALUE,upperLimit = Character.MIN_VALUE;\n			for(char c : ((edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) characterNode1).getChars())\n			{\n				lowerLimit = c;\n			}\n			for(char c : ((edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) characterNode2).getChars())\n			{\n				upperLimit = c;\n			}\n			RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet(\n			        edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.RANGES,\n			        '+',lowerLimit,upperLimit);\n		}\n		else\n		{\n			error(_pos,\"Type error in regex\");\n			RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet(\n			        edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS);\n		}";
		String RGtoGCode = 
		    "RESULT = _children[0];";
		String RGtoepsCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();";
		String CHARtocharCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,\n			     (((String) _children[0]).toCharArray()));";
		String CHARtoescapedCode = 
		    "RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet(\n	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,\n			     (((String) _children[0]).toCharArray()));";
	

		epsLayout.add(FringeSymbols.EMPTY);
		wsLayout.add(ws);
	
		grammar = new GrammarSource();
		
		grammar.addToT(GrammarConcreteSyntax.plus); grammar.addLexicalAttributes(GrammarConcreteSyntax.plus,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));

		grammar.addToT(GrammarConcreteSyntax.star); grammar.addLexicalAttributes(GrammarConcreteSyntax.star,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.question); grammar.addLexicalAttributes(GrammarConcreteSyntax.question,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.bar); grammar.addLexicalAttributes(GrammarConcreteSyntax.bar,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.dash); grammar.addLexicalAttributes(GrammarConcreteSyntax.dash,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.colon); grammar.addLexicalAttributes(GrammarConcreteSyntax.colon,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.not); grammar.addLexicalAttributes(GrammarConcreteSyntax.not,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.lbrack); grammar.addLexicalAttributes(GrammarConcreteSyntax.lbrack,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.rbrack); grammar.addLexicalAttributes(GrammarConcreteSyntax.rbrack,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.lparen); grammar.addLexicalAttributes(GrammarConcreteSyntax.lparen,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.rparen); grammar.addLexicalAttributes(GrammarConcreteSyntax.rparen,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.lbrace); grammar.addLexicalAttributes(GrammarConcreteSyntax.lbrace,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.rbrace); grammar.addLexicalAttributes(GrammarConcreteSyntax.rbrace,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.wildcard); grammar.addLexicalAttributes(GrammarConcreteSyntax.wildcard,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.character); grammar.addLexicalAttributes(GrammarConcreteSyntax.character,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.termname); grammar.addLexicalAttributes(GrammarConcreteSyntax.termname,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.escaped); grammar.addLexicalAttributes(GrammarConcreteSyntax.escaped,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"    char escapedChar = edu.umn.cs.melt.copper.runtime.auxiliary.QuotedStringFormatter.getRepresentedCharacter(lexeme);\n    if(escapedChar == edu.umn.cs.melt.copper.runtime.auxiliary.QScannerBuffer.EOFIndicator) error(_pos,\"Illegal escaped character\");\n    RESULT = String.valueOf(escapedChar);"));

		grammar.addToT(GrammarConcreteSyntax.ambiguous_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.ambiguous_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = _pos;"));
		grammar.addToT(GrammarConcreteSyntax.assoc_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.assoc_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.assoctypes); grammar.addLexicalAttributes(GrammarConcreteSyntax.assoctypes,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"   if(lexeme.equals(\"nonassoc\")) RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_NONASSOC);\n   else if(lexeme.equals(\"left\")) RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_LEFT);\n   else /* if(lexeme.equals(\"right\")) */ RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_RIGHT);"));
		grammar.addToT(GrammarConcreteSyntax.attr_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.attr_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.attr_type_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.attr_type_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.attr_type_base); grammar.addLexicalAttributes(GrammarConcreteSyntax.attr_type_base,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.attrname_tok); grammar.addLexicalAttributes(GrammarConcreteSyntax.attrname_tok,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(" + MasterController.class.getName() + ".AST_DOT_WINDOW,\".\");\n  RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.bnf_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.bnf_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		//grammar.addToT(GrammarConcreteSyntax.bridging_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.bridging_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.code_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.code_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.comment_line); grammar.addLexicalAttributes(GrammarConcreteSyntax.comment_line,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.default_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.default_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.dominates_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.dominates_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		//grammar.addToT(GrammarConcreteSyntax.dynamic_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.dynamic_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.embedded_code); grammar.addLexicalAttributes(GrammarConcreteSyntax.embedded_code,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme.substring(1,lexeme.length() - 1);"));
		grammar.addToT(GrammarConcreteSyntax.goesto); grammar.addLexicalAttributes(GrammarConcreteSyntax.goesto,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.grammar_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.grammar_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = _pos;"));
		grammar.addToT(GrammarConcreteSyntax.grammar_name_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.grammar_name_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.grammar_version); grammar.addLexicalAttributes(GrammarConcreteSyntax.grammar_version,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.grammarname_tok); grammar.addLexicalAttributes(GrammarConcreteSyntax.grammarname_tok,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(" + MasterController.class.getName() + ".AST_DOT_WINDOW,\".\");\n  RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.group_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.group_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.groupname_tok); grammar.addLexicalAttributes(GrammarConcreteSyntax.groupname_tok,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(" + MasterController.class.getName() + ".AST_DOT_WINDOW,\".\");\n  RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.layout_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.layout_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		//grammar.addToT(GrammarConcreteSyntax.matchtype_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.matchtype_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		//grammar.addToT(GrammarConcreteSyntax.matchtypes); grammar.addLexicalAttributes(GrammarConcreteSyntax.matchtypes,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.members_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.members_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.newline); grammar.addLexicalAttributes(GrammarConcreteSyntax.newline,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.nonterm_tok); grammar.addLexicalAttributes(GrammarConcreteSyntax.nonterm_tok,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(" + MasterController.class.getName() + ".AST_DOT_WINDOW,\".\");\n  RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);"));
		grammar.addToT(GrammarConcreteSyntax.nt_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.nt_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = _pos;"));
		grammar.addToT(GrammarConcreteSyntax.operator_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.operator_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = _pos;"));
		grammar.addToT(GrammarConcreteSyntax.prec_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.prec_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.prefix_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.prefix_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.prec_number); grammar.addLexicalAttributes(GrammarConcreteSyntax.prec_number,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = Integer.parseInt(lexeme);"));
		grammar.addToT(GrammarConcreteSyntax.precclass_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.precclass_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.precclass_tok); grammar.addLexicalAttributes(GrammarConcreteSyntax.precclass_tok,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(" + MasterController.class.getName() + ".AST_DOT_WINDOW,\".\");\n  RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.prod_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.prod_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = _pos;"));
		grammar.addToT(GrammarConcreteSyntax.prodname_tok); grammar.addLexicalAttributes(GrammarConcreteSyntax.prodname_tok,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(" + MasterController.class.getName() + ".AST_DOT_WINDOW,\".\");\n  RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.spectype_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.spectype_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.spectypes); grammar.addLexicalAttributes(GrammarConcreteSyntax.spectypes,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.start_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.start_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = _pos;"));
		//grammar.addToT(GrammarConcreteSyntax.static_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.static_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.submit_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.submit_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.symbol_tok); grammar.addLexicalAttributes(GrammarConcreteSyntax.symbol_tok,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(" + MasterController.class.getName() + ".AST_DOT_WINDOW,\".\");\n  RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.t_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.t_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = _pos;"));
		grammar.addToT(GrammarConcreteSyntax.terminal_tok); grammar.addLexicalAttributes(GrammarConcreteSyntax.terminal_tok,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(" + MasterController.class.getName() + ".AST_DOT_WINDOW,\".\");\n  RESULT = lexeme;"));
		grammar.addToT(GrammarConcreteSyntax.tok_decl); grammar.addLexicalAttributes(GrammarConcreteSyntax.tok_decl,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = _pos;"));
		grammar.addToT(GrammarConcreteSyntax.ws); grammar.addLexicalAttributes(GrammarConcreteSyntax.ws,new LexicalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"RESULT = lexeme;"));

		grammar.addToNT(GrammarConcreteSyntax.GrammarFile); grammar.addNTAttributes(GrammarConcreteSyntax.GrammarFile,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),true));
		grammar.addToNT(GrammarConcreteSyntax.Grammar); grammar.addNTAttributes(GrammarConcreteSyntax.Grammar,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.NTLine); grammar.addNTAttributes(GrammarConcreteSyntax.NTLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.NTSeq); grammar.addNTAttributes(GrammarConcreteSyntax.NTSeq,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.TSeq); grammar.addNTAttributes(GrammarConcreteSyntax.TSeq,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.TLine); grammar.addNTAttributes(GrammarConcreteSyntax.TLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.TokLine); grammar.addNTAttributes(GrammarConcreteSyntax.TokLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.OpLine); grammar.addNTAttributes(GrammarConcreteSyntax.OpLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.ProdLine); grammar.addNTAttributes(GrammarConcreteSyntax.ProdLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.StartLine); grammar.addNTAttributes(GrammarConcreteSyntax.StartLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.AttrLine); grammar.addNTAttributes(GrammarConcreteSyntax.AttrLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.GroupLine); grammar.addNTAttributes(GrammarConcreteSyntax.GroupLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.DefaultTCodeLine); grammar.addNTAttributes(GrammarConcreteSyntax.DefaultTCodeLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.DefaultProdCodeLine); grammar.addNTAttributes(GrammarConcreteSyntax.DefaultProdCodeLine,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.SymSeq); grammar.addNTAttributes(GrammarConcreteSyntax.SymSeq,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.PrecClassSeq); grammar.addNTAttributes(GrammarConcreteSyntax.PrecClassSeq,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.AttrTypeRoot); grammar.addNTAttributes(GrammarConcreteSyntax.AttrTypeRoot,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_Root); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_Root,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_R); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_R,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_DR); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_DR,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_UR); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_UR,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_RR); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_RR,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_G); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_G,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_RG); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_RG,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_UG); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_UG,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		grammar.addToNT(GrammarConcreteSyntax.Regex_CHAR); grammar.addNTAttributes(GrammarConcreteSyntax.Regex_CHAR,new NonTerminalAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),false));
		
		grammar.addToP(GrammarConcreteSyntax.AttrLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.AttrLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),AttrLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.AttrTypeList); grammar.addProductionAttributes(GrammarConcreteSyntax.AttrTypeList,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),AttrTypeListCode));
		grammar.addToP(GrammarConcreteSyntax.AttrTypeBase); grammar.addProductionAttributes(GrammarConcreteSyntax.AttrTypeBase,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),AttrTypeBaseCode));
		grammar.addToP(GrammarConcreteSyntax.CHARtochar); grammar.addProductionAttributes(GrammarConcreteSyntax.CHARtochar,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),CHARtocharCode));
		grammar.addToP(GrammarConcreteSyntax.CHARtoescaped); grammar.addProductionAttributes(GrammarConcreteSyntax.CHARtoescaped,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),CHARtoescapedCode));
		grammar.addToP(GrammarConcreteSyntax.GrammarFiletoGrammar); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammarFiletoGrammar,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammarFiletoGrammarCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoCommentLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoCommentLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoCommentLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoEps); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoEps,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoEpsCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoNTLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoNTLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoNTLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoOpLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoOpLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoOpLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoProdLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoProdLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoProdLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoAttrLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoAttrLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoAttrLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoGroupLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoGroupLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoGroupLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoDefaultTCodeLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoDefaultTCodeLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoDefaultTCodeLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoDefaultProdCodeLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoDefaultProdCodeLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoDefaultProdCodeLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoStartLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoStartLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoStartLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoTLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoTLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoTLineCode));
		grammar.addToP(GrammarConcreteSyntax.GrammartoTokLine); grammar.addProductionAttributes(GrammarConcreteSyntax.GrammartoTokLine,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GrammartoTokLineCode));
		grammar.addToP(GrammarConcreteSyntax.GroupLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.GroupLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),GroupLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.GtoUG_RG); grammar.addProductionAttributes(GrammarConcreteSyntax.GtoUG_RG,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),GtoUG_RGCode));
		grammar.addToP(GrammarConcreteSyntax.NTLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.NTLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),NTLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.NTSeqEps); grammar.addProductionAttributes(GrammarConcreteSyntax.NTSeqEps,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),NTSeqEpsCode));
		grammar.addToP(GrammarConcreteSyntax.NTSeqMain); grammar.addProductionAttributes(GrammarConcreteSyntax.NTSeqMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),NTSeqMainCode));
		grammar.addToP(GrammarConcreteSyntax.OpLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.OpLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),OpLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.ProdLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.ProdLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),ProdLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.DefaultTCodeLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.DefaultTCodeLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),DefaultTCodeLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.DefaultProdCodeLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.DefaultProdCodeLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),DefaultProdCodeLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.PrecClassSeqEps); grammar.addProductionAttributes(GrammarConcreteSyntax.PrecClassSeqEps,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),PrecClassSeqEpsCode));
		grammar.addToP(GrammarConcreteSyntax.PrecClassSeqMain); grammar.addProductionAttributes(GrammarConcreteSyntax.PrecClassSeqMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),PrecClassSeqMainCode));
		grammar.addToP(GrammarConcreteSyntax.RGtoG); grammar.addProductionAttributes(GrammarConcreteSyntax.RGtoG,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),RGtoGCode));
		grammar.addToP(GrammarConcreteSyntax.RGtoeps); grammar.addProductionAttributes(GrammarConcreteSyntax.RGtoeps,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),RGtoepsCode));
		grammar.addToP(GrammarConcreteSyntax.RRtoDR); grammar.addProductionAttributes(GrammarConcreteSyntax.RRtoDR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),RRtoDRCode));
		grammar.addToP(GrammarConcreteSyntax.RRtoeps); grammar.addProductionAttributes(GrammarConcreteSyntax.RRtoeps,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),RRtoepsCode));
		grammar.addToP(GrammarConcreteSyntax.RoottoR); grammar.addProductionAttributes(GrammarConcreteSyntax.RoottoR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("main"),RoottoRCode));
		grammar.addToP(GrammarConcreteSyntax.Roottoeps); grammar.addProductionAttributes(GrammarConcreteSyntax.Roottoeps,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("main"),RoottoepsCode));
		grammar.addToP(GrammarConcreteSyntax.RtoDR); grammar.addProductionAttributes(GrammarConcreteSyntax.RtoDR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),RtoDRCode));
		grammar.addToP(GrammarConcreteSyntax.RtoDR_bar_R); grammar.addProductionAttributes(GrammarConcreteSyntax.RtoDR_bar_R,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),RtoDR_bar_RCode));
		grammar.addToP(GrammarConcreteSyntax.DRtoUR_RR); grammar.addProductionAttributes(GrammarConcreteSyntax.DRtoUR_RR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),DRtoUR_RRCode));
		grammar.addToP(GrammarConcreteSyntax.DRtoUR_plus_RR); grammar.addProductionAttributes(GrammarConcreteSyntax.DRtoUR_plus_RR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),DRtoUR_plus_RRCode));
		grammar.addToP(GrammarConcreteSyntax.DRtoUR_question_RR); grammar.addProductionAttributes(GrammarConcreteSyntax.DRtoUR_question_RR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),DRtoUR_question_RRCode));
		grammar.addToP(GrammarConcreteSyntax.DRtoUR_star_RR); grammar.addProductionAttributes(GrammarConcreteSyntax.DRtoUR_star_RR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),DRtoUR_star_RRCode));
		grammar.addToP(GrammarConcreteSyntax.StartLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.StartLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),StartLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.SymSeqEps); grammar.addProductionAttributes(GrammarConcreteSyntax.SymSeqEps,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),SymSeqEpsCode));
		grammar.addToP(GrammarConcreteSyntax.SymSeqMain); grammar.addProductionAttributes(GrammarConcreteSyntax.SymSeqMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),SymSeqMainCode));
		grammar.addToP(GrammarConcreteSyntax.TLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.TLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),TLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.TSeqEps); grammar.addProductionAttributes(GrammarConcreteSyntax.TSeqEps,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),TSeqEpsCode));
		grammar.addToP(GrammarConcreteSyntax.TSeqMain); grammar.addProductionAttributes(GrammarConcreteSyntax.TSeqMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),TSeqMainCode));
		grammar.addToP(GrammarConcreteSyntax.TokLineMain); grammar.addProductionAttributes(GrammarConcreteSyntax.TokLineMain,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.wsLayout,new TerminalClass("main"),TokLineMainCode));
		grammar.addToP(GrammarConcreteSyntax.UGtoCHAR); grammar.addProductionAttributes(GrammarConcreteSyntax.UGtoCHAR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),UGtoCHARCode));
		grammar.addToP(GrammarConcreteSyntax.UGtoCHAR_dash_CHAR); grammar.addProductionAttributes(GrammarConcreteSyntax.UGtoCHAR_dash_CHAR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),UGtoCHAR_dash_CHARCode));
		grammar.addToP(GrammarConcreteSyntax.URtoCHAR); grammar.addProductionAttributes(GrammarConcreteSyntax.URtoCHAR,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),URtoCHARCode));
		grammar.addToP(GrammarConcreteSyntax.URtolb_G_rb); grammar.addProductionAttributes(GrammarConcreteSyntax.URtolb_G_rb,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),URtolb_G_rbCode));
		grammar.addToP(GrammarConcreteSyntax.URtolb_not_G_rb); grammar.addProductionAttributes(GrammarConcreteSyntax.URtolb_not_G_rb,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),URtolb_not_G_rbCode));
		grammar.addToP(GrammarConcreteSyntax.URtolp_R_rp); grammar.addProductionAttributes(GrammarConcreteSyntax.URtolp_R_rp,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),URtolp_R_rpCode));
		grammar.addToP(GrammarConcreteSyntax.URtomacro); grammar.addProductionAttributes(GrammarConcreteSyntax.URtomacro,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),URtomacroCode));
		grammar.addToP(GrammarConcreteSyntax.URtowildcard); grammar.addProductionAttributes(GrammarConcreteSyntax.URtowildcard,new ProductionAttributes(new GrammarName("GrammarGrammar"),InputPosition.initialPos(),1,null,GrammarConcreteSyntax.epsLayout,new TerminalClass("regex"),URtowildcardCode));
		
		grammar.setStartSym(GrammarConcreteSyntax.GrammarFile);
		grammar.addStartLayout(GrammarConcreteSyntax.ws);
		
		grammar.addRegex(GrammarConcreteSyntax.plus,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'+'));
		grammar.addRegex(GrammarConcreteSyntax.star,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'*'));
		grammar.addRegex(GrammarConcreteSyntax.question,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'?'));
		grammar.addRegex(GrammarConcreteSyntax.bar,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'|'));
		grammar.addRegex(GrammarConcreteSyntax.dash,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'-'));
		grammar.addRegex(GrammarConcreteSyntax.colon,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,':'));
		grammar.addRegex(GrammarConcreteSyntax.not,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'^'));
		grammar.addRegex(GrammarConcreteSyntax.lbrack,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'['));
		grammar.addRegex(GrammarConcreteSyntax.rbrack,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,']'));
		grammar.addRegex(GrammarConcreteSyntax.lparen,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'('));
		grammar.addRegex(GrammarConcreteSyntax.rparen,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,')'));
		grammar.addRegex(GrammarConcreteSyntax.lbrace,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'{'));
		grammar.addRegex(GrammarConcreteSyntax.rbrace,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'}'));
		grammar.addRegex(GrammarConcreteSyntax.wildcard,CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'.'));
		grammar.addRegex(GrammarConcreteSyntax.character,(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\n').invertSet()));
		grammar.addRegex(GrammarConcreteSyntax.termname,new Concatenation(
				                   CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,':').invertSet(),
				                   new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,':').invertSet())));
		//grammar.addRegex(GrammarConcreteSyntax.escaped,new Concatenation(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\\'),CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,"+*?|-^[]().:\\ntrs".toCharArray())));
		grammar.addRegex(GrammarConcreteSyntax.escaped,new Concatenation(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\\'),CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\n').invertSet()));
		grammar.addRegex(GrammarConcreteSyntax.ambiguous_decl,ParsedRegex.simpleStringRegex("ambiguous"));
		grammar.addRegex(GrammarConcreteSyntax.assoc_decl,ParsedRegex.simpleStringRegex("associativity"));
		grammar.addRegex(GrammarConcreteSyntax.assoctypes,new Choice(
				                                             ParsedRegex.simpleStringRegex("left"),
				                                             ParsedRegex.simpleStringRegex("right"),
				                                             ParsedRegex.simpleStringRegex("none"),
				                                             ParsedRegex.simpleStringRegex("nonassoc")));
		grammar.addRegex(GrammarConcreteSyntax.attr_decl,ParsedRegex.simpleStringRegex("attribute"));
		grammar.addRegex(GrammarConcreteSyntax.attr_type_decl,ParsedRegex.simpleStringRegex("type"));
		grammar.addRegex(GrammarConcreteSyntax.attr_type_base,new Choice(
				                                                 ParsedRegex.simpleStringRegex("Integer"),
				                                                 ParsedRegex.simpleStringRegex("Float"),
				                                                 ParsedRegex.simpleStringRegex("String")));
		grammar.addRegex(GrammarConcreteSyntax.attrname_tok,new Concatenation(
			                                                   CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet(),
				                                               new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.bnf_decl,ParsedRegex.simpleStringRegex("bnf"));
		//grammar.addRegex(GrammarConcreteSyntax.bridging_decl,ParsedRegex.simpleStringRegex("bridging"));
		grammar.addRegex(GrammarConcreteSyntax.code_decl,ParsedRegex.simpleStringRegex("code"));
		grammar.addRegex(GrammarConcreteSyntax.comment_line,new Choice(
				                                               new Concatenation(
				                                                CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t'),
				                                                new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t'))),
				                                               new Concatenation(
				                                                CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'#'),
				                                                new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\n').invertSet()))));
		grammar.addRegex(GrammarConcreteSyntax.default_decl,ParsedRegex.simpleStringRegex("default"));
		grammar.addRegex(GrammarConcreteSyntax.dominates_decl,ParsedRegex.simpleStringRegex("dominates"));
		//grammar.addRegex(GrammarConcreteSyntax.dynamic_decl,ParsedRegex.simpleStringRegex("dynamic"));
		grammar.addRegex(GrammarConcreteSyntax.embedded_code,new Concatenation(
                                                                ParsedRegex.simpleStringRegex("@"),
                                                                new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'@').invertSet()),
                                                                ParsedRegex.simpleStringRegex("@")));
		grammar.addRegex(GrammarConcreteSyntax.goesto,ParsedRegex.simpleStringRegex("->"));
		grammar.addRegex(GrammarConcreteSyntax.grammar_decl,ParsedRegex.simpleStringRegex("grammar"));
		grammar.addRegex(GrammarConcreteSyntax.grammarname_tok,new Concatenation(
                CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet(),
                new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.grammar_name_decl,ParsedRegex.simpleStringRegex("name"));
		grammar.addRegex(GrammarConcreteSyntax.grammar_version,new Concatenation(
 			                                                      CharacterSet.instantiate(CharacterSet.RANGES,'+','0','9'),
				                                                  new KleeneStar(CharacterSet.instantiate(CharacterSet.RANGES,'+','0','9')),
				                                                  CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'.'),
				                                                  CharacterSet.instantiate(CharacterSet.RANGES,'+','0','9'),
				                                                  new KleeneStar(CharacterSet.instantiate(CharacterSet.RANGES,'+','0','9'))));
		grammar.addRegex(GrammarConcreteSyntax.group_decl,ParsedRegex.simpleStringRegex("group"));
		grammar.addRegex(GrammarConcreteSyntax.groupname_tok,new Concatenation(
                											   CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet(),
                											   new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.layout_decl,ParsedRegex.simpleStringRegex("layout"));
//		grammar.addRegex(GrammarConcreteSyntax.matchtype_decl,ParsedRegex.simpleStringRegex("matchtype"));
//		grammar.addRegex(GrammarConcreteSyntax.matchtypes,new Choice(
//				                                             ParsedRegex.simpleStringRegex("longest"),
//				                                             ParsedRegex.simpleStringRegex("shortest"),
//				                                             ParsedRegex.simpleStringRegex("all")));
		grammar.addRegex(GrammarConcreteSyntax.members_decl,ParsedRegex.simpleStringRegex("members"));
		grammar.addRegex(GrammarConcreteSyntax.newline,new Concatenation(
				                                          CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\n'),
				                                          new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\n'))));
		grammar.addRegex(GrammarConcreteSyntax.nonterm_tok,new Concatenation(
				                                             CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet(),
				                                             new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.nt_decl,ParsedRegex.simpleStringRegex("nonterm"));
		grammar.addRegex(GrammarConcreteSyntax.operator_decl,ParsedRegex.simpleStringRegex("operator"));
		grammar.addRegex(GrammarConcreteSyntax.prec_decl,ParsedRegex.simpleStringRegex("precedence"));
		grammar.addRegex(GrammarConcreteSyntax.prefix_decl,ParsedRegex.simpleStringRegex("prefix"));
		grammar.addRegex(GrammarConcreteSyntax.prec_number,new Choice(
				                                              CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'0'),
				                                              new Concatenation(
				                                               CharacterSet.instantiate(CharacterSet.RANGES,'+','1','9'),
				                                               new KleeneStar(CharacterSet.instantiate(CharacterSet.RANGES,'+','0','9')))));
		grammar.addRegex(GrammarConcreteSyntax.precclass_decl,ParsedRegex.simpleStringRegex("class"));
		grammar.addRegex(GrammarConcreteSyntax.precclass_tok,new Concatenation(
                											    CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet(),
                											    new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.prod_decl,ParsedRegex.simpleStringRegex("prod"));
		grammar.addRegex(GrammarConcreteSyntax.prodname_tok,new Concatenation(
														      CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet(),
															  new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.spectype_decl,ParsedRegex.simpleStringRegex("spectype"));
		grammar.addRegex(GrammarConcreteSyntax.spectypes,new Choice(ParsedRegex.simpleStringRegex("LALR1"),ParsedRegex.simpleStringRegex("LALR1-silver.haskell"),ParsedRegex.simpleStringRegex("LALR1-pretty")));
		grammar.addRegex(GrammarConcreteSyntax.symbol_tok,new Concatenation(
			    										     CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet(),
			    										     new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.start_decl,ParsedRegex.simpleStringRegex("start"));
		//grammar.addRegex(GrammarConcreteSyntax.static_decl,ParsedRegex.simpleStringRegex("static"));
		grammar.addRegex(GrammarConcreteSyntax.submit_decl,ParsedRegex.simpleStringRegex("submits to"));
		grammar.addRegex(GrammarConcreteSyntax.t_decl,ParsedRegex.simpleStringRegex("term"));
		grammar.addRegex(GrammarConcreteSyntax.terminal_tok,new Concatenation(
			    											  CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet(),
			    											  new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t','\n','^','$').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.termname,new Concatenation(
				                                          CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,':').invertSet(),
				                                          new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,':').invertSet())));
		grammar.addRegex(GrammarConcreteSyntax.tok_decl,ParsedRegex.simpleStringRegex("token"));
		grammar.addRegex(GrammarConcreteSyntax.ws,new KleeneStar(CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,' ','\t')));

		grammar.addContainedGrammar(new GrammarName("GrammarGrammar"));
		grammar.addContainedGrammar(new GrammarName(FringeSymbols.STARTPRIME.getId()));
		
		grammar.constructPrecedenceRelationsGraph();
		grammar.addStaticPrecedenceRelation(precclass_tok,rbrace);
		grammar.addStaticPrecedenceRelation(symbol_tok,rbrace);
		grammar.addStaticPrecedenceRelation(terminal_tok,rbrace);

		HashSet<Terminal> char1 = new HashSet<Terminal>();
		char1.add(GrammarConcreteSyntax.character);
		char1.add(GrammarConcreteSyntax.lparen);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char1"),char1,"return lparen;"));

		HashSet<Terminal> char2 = new HashSet<Terminal>();
		char2.add(GrammarConcreteSyntax.character);
		char2.add(GrammarConcreteSyntax.rparen);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char2"),char2,"return rparen;"));

		HashSet<Terminal> char3 = new HashSet<Terminal>();
		char3.add(GrammarConcreteSyntax.character);
		char3.add(GrammarConcreteSyntax.wildcard);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char3"),char3,"return wildcard;"));

		HashSet<Terminal> char4 = new HashSet<Terminal>();
		char4.add(GrammarConcreteSyntax.character);
		char4.add(GrammarConcreteSyntax.lbrack);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char4"),char4,"return lbrack;"));

		HashSet<Terminal> char5 = new HashSet<Terminal>();
		char5.add(GrammarConcreteSyntax.character);
		char5.add(GrammarConcreteSyntax.rbrack);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char5"),char5,"return rbrack;"));
		
		HashSet<Terminal> char6 = new HashSet<Terminal>();
		char6.add(GrammarConcreteSyntax.character);
		char6.add(GrammarConcreteSyntax.bar);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char6"),char6,"return bar;"));
		
		HashSet<Terminal> char7 = new HashSet<Terminal>();
		char7.add(GrammarConcreteSyntax.character);
		char7.add(GrammarConcreteSyntax.question);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char7"),char7,"return question;"));
		
		HashSet<Terminal> char8 = new HashSet<Terminal>();
		char8.add(GrammarConcreteSyntax.character);
		char8.add(GrammarConcreteSyntax.star);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char8"),char8,"return star;"));
		
		HashSet<Terminal> char9 = new HashSet<Terminal>();
		char9.add(GrammarConcreteSyntax.character);
		char9.add(GrammarConcreteSyntax.dash);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char9"),char9,"return dash;"));
		
		HashSet<Terminal> char10 = new HashSet<Terminal>();
		char10.add(GrammarConcreteSyntax.character);
		char10.add(GrammarConcreteSyntax.not);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char10"),char10,"return not;"));
		
		HashSet<Terminal> char11 = new HashSet<Terminal>();
		char11.add(GrammarConcreteSyntax.character);
		char11.add(GrammarConcreteSyntax.colon);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char11"),char11,"return colon;"));
		
		HashSet<Terminal> char12 = new HashSet<Terminal>();
		char12.add(GrammarConcreteSyntax.character);
		char12.add(GrammarConcreteSyntax.plus);
		grammar.addDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("char12"),char12,"return plus;"));
		
		grammar.addParserAttribute(new ParserAttribute(Symbol.symbol("dotCounter"),"Integer","dotCounter = 0;"));
	}
}
