package edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.lalr;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.TerminalClass;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet;
import edu.umn.cs.melt.copper.compiletime.engines.lalr.LALREngine;
import edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo;
import edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData;
import edu.umn.cs.melt.copper.compiletime.engines.lalr.semantics.SemanticActionContainer;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFA;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.AcceptAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.FullReduceAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.GLRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.ShiftAction;
import edu.umn.cs.melt.copper.compiletime.semantics.lalr1.GenericLexicalAmbiguityChecker;
import edu.umn.cs.melt.copper.compiletime.semantics.lalr1.LexicalAmbiguityChecker;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.EngineBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.parsetablebuilders.LazyGLRParseTableBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.parsetablebuilders.ParseTableBuilder;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;



public class LALREngineBuilder implements EngineBuilder
{
	private GrammarSource grammar;
	private LALR1DFA dfa;
	private GLRParseTable parseTable;
	private CompilerLogger logger;

	private Hashtable<GrammarSymbol,Integer> symbolTransTable;
	private Hashtable<Production,Integer> productionTransTable;
	private Hashtable<TerminalClass,Integer> lexGroupTransTable;
	private QScannerStateInfo[] scannerInfo;
	
	public LALREngineBuilder(GrammarSource grammar,LALR1DFA dfa,GLRParseTable parseTable,CompilerLogger logger)
	{
		this.grammar = grammar;
		this.dfa = dfa;
		this.parseTable = parseTable;
		this.logger = logger;
	}

	public void buildLALREngine(PrintStream out,
								  String packageDecl,
			                      String importDecls,
			                      String parserName,
			                      String scannerName,
			                      String parserAncillaries,
			                      String scannerAncillaries)
	throws CopperException
	{
		symbolTransTable = new Hashtable<GrammarSymbol,Integer>();
		productionTransTable = new Hashtable<Production,Integer>();
		lexGroupTransTable = new Hashtable<TerminalClass,Integer>();
		out.print(packageDecl + "\n");
		out.print(importDecls + "\n");
		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"  Parser code...\n");
		// DEBUG-END
		out.print("\n");
		out.print("public class " + parserName + " extends " + LALREngine.class.getName() + "\n");
		out.print("{\n");
		out.print("    public " + parserName + "(" + Reader.class.getName() + " reader," + CompilerLogger.class.getName() + " logger)\n");
		out.print("    {\n");
		out.print("        scanner = new " + scannerName + "(reader,logger);\n");
		out.print("        this.logger = logger;\n");
		out.print("        setupEngine();\n");
		out.print("    }\n");
		out.print("\n");
		
		// DYNCODE-BEGIN
		out.print("    /** Create an empty symbol. */\n");
		out.print("    protected static " + Terminal.class.getName() + " eps()\n");
		out.print("    {\n");
		out.print("        return " + FringeSymbols.class.getName() + ".EMPTY;\n");
		out.print("    }\n");
		
		out.print("    /** Create a terminal from a symbol. */\n");
		out.print("    protected static " + Terminal.class.getName() + " t(String sym)\n");
		out.print("    {\n");
		out.print("        return new " + Terminal.class.getName() + "(sym);\n");
		out.print("    }\n");
		
		out.print("    /** Create a terminal from a symbol. */\n");
		out.print("    protected static " + Terminal.class.getName() + " t(" + Symbol.class.getName() + " sym,String lexeme)\n");
		out.print("    {\n");
		out.print("        return new " + Terminal.class.getName() + "(sym,lexeme);\n");
		out.print("    }\n");

		out.print("    /** Create a scanner match from a symbol, lexeme, and position-following. */\n");
		out.print("    protected static " + QScannerMatchData.class.getName() + " qsm(" + Symbol.class.getName() + " sym,String lexeme," + InputPosition.class.getName() + " positionPreceding," + InputPosition.class.getName() + " positionFollowing," + ArrayList.class.getName() + "<" + QScannerMatchData.class.getName() + "> layouts)\n");
		out.print("    {\n");
		out.print("        return new " + QScannerMatchData.class.getName() + "(t(sym,lexeme),positionPreceding,positionFollowing,layouts);\n");
		out.print("    }\n");

		out.print("    /** Create a nonterminal from a symbol. */\n");
		out.print("    protected static " + NonTerminal.class.getName() + " nt(String sym)\n");
		out.print("    {\n");
		out.print("        return new " + NonTerminal.class.getName() + "(sym);\n");
		out.print("    }\n");
		
		out.print("    /** Create a production. */\n");
		out.print("    protected static " + Production.class.getName() + " p(String name," + NonTerminal.class.getName() + " lhs," + GrammarSymbol.class.getName() + "... rhs)\n");
		out.print("    {\n");
		out.print("        return " + Production.class.getName() + ".production(" + Symbol.class.getName() + ".symbol(name),lhs,rhs);\n");
		out.print("    }\n");
		
		out.print("    /** Create various parse actions. */\n");
		out.print("    protected static " + AcceptAction.class.getName() + " a()\n");
		out.print("    {\n");
		out.print("        return new " + AcceptAction.class.getName() + "();\n");
		out.print("    }\n");
		out.print("    protected static " + FullReduceAction.class.getName() + " fr(" + Production.class.getName() + " p)\n");
		out.print("    {\n");
		out.print("        return new " + FullReduceAction.class.getName() + "(p);\n");
		out.print("    }\n");
		out.print("    protected static " + ShiftAction.class.getName() + " sh(int dest)\n");
		out.print("    {\n");
		out.print("        return new " + ShiftAction.class.getName() + "(dest);\n");
		out.print("    }\n");
		
		out.print("\n");

		out.print("    private Semantics semantics;\n");
		out.print("    public Object runSemanticAction(" + InputPosition.class.getName() + " _pos,Object[] _children," + Production.class.getName() + " _prod)\n");
	    out.print("    throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runSemanticAction(_pos,_children,_prod);\n");
	    out.print("    }\n");
	    out.print("    public Object runSemanticAction(" + InputPosition.class.getName() + " _pos," + QScannerMatchData.class.getName() + " _terminal)\n");
	    out.print("    throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runSemanticAction(_pos,_terminal);\n");
	    out.print("    }\n");
		out.print("    public " + QScannerMatchData.class.getName() + " runDisambiguationAction(" + InputPosition.class.getName() + " _pos," + HashSet.class.getName() + "<" + QScannerMatchData.class.getName() + "> matches)\n");
	    out.print("    throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runDisambiguationAction(_pos,matches);\n");
	    out.print("    }\n");
	    out.print("    public " + SpecialParserAttributes.class.getName() + " getSpecialAttributes()\n");
	    out.print("    {\n");
	    out.print("        return semantics.getSpecialAttributes();\n");
	    out.print("    }\n");
	    out.print("    public void startEngine(" + InputPosition.class.getName() + " initialPos)\n");
	    out.print("    throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
	    out.print("    {\n");
	    out.print("         super.startEngine(initialPos);\n");
	    out.print("         semantics = new Semantics();\n");
	    out.print("    }\n");
		out.print("\n");
		boolean first = true;
		int i = 0;
		out.print("    private static " + Terminal.class.getName() + " sym_0");
		for(Terminal t : grammar.getT())
		{
			symbolTransTable.put(t,i);
			if(first)
			{
				first = false;
				i++;
			}
			else out.print(",sym_" + (i++)); 
		}
		out.print(";\n");
		first = true;
		out.print("    private static " + NonTerminal.class.getName() + " sym_" + i + "");
		for(NonTerminal nt : grammar.getNT())
		{
			symbolTransTable.put(nt,i);
			if(first)
			{
				first = false;
				i++;
			}
			else out.print(",sym_" + (i++)); 
		}
		out.print(";\n");
		i = 0;
		first = true;
		out.print("    @SuppressWarnings(\"unused\")\n");
		out.print("    private static " + Production.class.getName() + " p_0");
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(Production p : grammar.getP(nt))
			{
				productionTransTable.put(p,i);
				if(first)
				{
					first = false;
					i++;
				}
				else out.print(",p_" + (i++)); 

			}
		}
		out.print(";\n");

		if(grammar.getDisambiguationGroups().iterator().hasNext()) out.print("    private static " + HashSet.class.getName() + "<" + Terminal.class.getName() + "> group_0");
		i = 0;
		first = true;
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			lexGroupTransTable.put(group.getName(),i);
			if(first)
			{
				first = false;
				i++;
			}
			else out.print(",group_" + (i++)); 
		}
		out.print(";\n\n");
		
		
		
		
//		ParseTableBuilder builder = new GLRParseTableBuilder(grammar,dfa,parseTable,reporter,symbolTransTable,productionTransTable);
//		builder.outputInitFunctions(out);
		ParseTableBuilder lazyBuilder = new LazyGLRParseTableBuilder(grammar,dfa,parseTable,logger,symbolTransTable,productionTransTable);
		lazyBuilder.outputInitFunctions(out);
//		ParseTableBuilder newBuilder = new HardCodedParseTableBuilder(grammar,dfa,parseTable,reporter,symbolTransTable,productionTransTable);
//		newBuilder.outputInitFunctions(out);

		
		
		
		out.print("    static\n");
		out.print("    {\n");

		
		
//		builder.outputInitStatements(out);
		lazyBuilder.outputInitStatements(out);
//		newBuilder.outputInitStatements(out);
		
		
		
		i = 0;
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			out.print("        group_" + (i++) + " = tset(");
			first = true;
			for(Terminal t : group.getMembers())
			{
				if(!first) out.print(",");
				else first = false;
				out.print("sym_" + symbolTransTable.get(t));
			}
			out.print(");\n");
		}
		out.print("    }\n");
		// DYNCODE-END
		out.print("    public class Semantics extends " + SemanticActionContainer.class.getName() + "\n");
		out.print("    {\n");
		for(ParserAttribute attr : grammar.getParserAttributes())
		{
			out.print("        public " + attr.getType() + " " + attr.getName().toString() + ";\n");
		}
		out.print("\n");
		out.print("        public Semantics()\n");
		out.print("        throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
	    out.print("        {\n");
		out.print("            runInit();\n");
		out.print("        }\n");
		out.print("\n");
		out.print("        public void error(" + InputPosition.class.getName() + " pos," + String.class.getName() + " message)\n");
		out.print("        throws " + CopperException.class.getName() + "\n");
		out.print("        {\n");
		out.print("              if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".ERROR)) logger.logErrorMessage(" + CompilerLogMessageSort.class.getName() + ".ERROR,pos,message);\n");
		out.print("        }\n");
		out.print("\n");
		out.print("        public void runDefaultTermAction()\n");
		out.print("        throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
		out.print("        {\n");
		out.print("            " + grammar.getDefaultTCode() + "\n");
		out.print("        }\n");
	    out.print("        public void runDefaultProdAction()\n");
		out.print("        throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
		out.print("        {\n");
		out.print("            " + grammar.getDefaultProdCode() + "\n");
		out.print("        }\n");
		out.print("        public void runInit()\n");
		out.print("        throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
		out.print("        {\n");
		for(ParserAttribute attr : grammar.getParserAttributes())
		{
			if(attr.getType().equals("Integer")) out.print("            " + attr.getName().toString() + " = 0;\n");
			else if(attr.getType().equals("Float")) out.print("            " + attr.getName().toString() + " = 0.0;\n");
			else out.print("            " + attr.getName().toString() + " = new " + attr.getType() + "();\n");
		}
		for(ParserAttribute attr : grammar.getParserAttributes())
		{
			out.print("            " + attr.getInitCode() + "\n");
		}
		out.print("        }\n");
		out.print("        @SuppressWarnings(\"unchecked\")\n");
		out.print("        public Object runSemanticAction(" + InputPosition.class.getName() + " _pos,Object[] _children," + Production.class.getName() + " _prod)\n");
		out.print("        throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
		out.print("        {\n");
		out.print("            this._pos = _pos;\n");
		out.print("            this._children = _children;\n");
		out.print("            this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);\n");
		out.print("            Object RESULT = null;\n");
		out.print("\n");
		i = 0;
		first = true;
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(Production p : grammar.getP(nt))
			{
				if(QuotedStringFormatter.isJavaWhitespace(grammar.getProductionAttributes(p).getActionCode()))
				{
					i++;
					continue;
				}
				out.print("            ");
				if(!first) out.print("else ");
				else first = false;
				out.print("if(_prod.equals(p_" + (i++) + "))\n");
				out.print("            {\n");
				out.print("                " + grammar.getProductionAttributes(p).getActionCode() + "\n");
				out.print("            }\n");
			}
		}
		out.print("            ");
		if(!first) out.print("else\n             {");
		out.print("                runDefaultProdAction();\n");
        out.print("                return \"PARSETREE\";\n");
        if(!first) out.print("            }\n");
        out.print("            return RESULT;\n");
		out.print("        }\n");
		
		out.print("        public Object runSemanticAction(" + InputPosition.class.getName() + " _pos," + QScannerMatchData.class.getName() + " _terminal)\n");
		out.print("        throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
		out.print("        {\n");
		out.print("            this._pos = _pos;\n");
		out.print("            this._terminal = _terminal;\n");
		out.print("            this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);\n");
		out.print("            Object RESULT = null;\n");
		first = false;
		for(Terminal t : grammar.getT())
		{
			if(!QuotedStringFormatter.isJavaWhitespace(grammar.getLexicalAttributes(t).getParserSemanticActionCode())) first = true;
		}
		if(first) out.print("            String lexeme = _terminal.getToken().getLexeme();\n");
		out.print("\n");
		first = true;
		for(Terminal t : grammar.getT())
		{
			if(QuotedStringFormatter.isJavaWhitespace(grammar.getLexicalAttributes(t).getParserSemanticActionCode()))
			{
				continue;				
			}
			out.print("            ");
			if(!first) out.print("else ");
			else first = false;
			out.print("if(_terminal.getToken().equals(sym_" + symbolTransTable.get(t) + "))\n");
			out.print("            {\n");
			out.print("                " + grammar.getLexicalAttributes(t).getParserSemanticActionCode());
			out.print("            }\n");
		}
		out.print("            ");
		if(!first) out.print("else\n             {");
        out.print("                runDefaultTermAction();\n");
        out.print("                return \"PARSETREE\";\n");
        if(!first) out.print("            }\n");
        out.print("            return RESULT;\n");
		out.print("        }\n");
		
		out.print("        public " + QScannerMatchData.class.getName() + " runDisambiguationAction(" + InputPosition.class.getName() + " _pos," + HashSet.class.getName() + "<" + QScannerMatchData.class.getName() + "> matches)\n");
		out.print("        throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n");
		out.print("        {\n");
		out.print("            this._pos = _pos;\n");
		out.print("            String lexeme = null;\n");
		out.print("            " + InputPosition.class.getName() + " positionFollowing = null;\n");
		out.print("            " + HashSet.class.getName() + "<" + Terminal.class.getName() + "> matchesT = new " + HashSet.class.getName() + "<" + Terminal.class.getName() + ">();\n");
		out.print("            " + DynHashSet.class.getName() + "<" + QScannerMatchData.class.getName() + "> matchesD = new " + DynHashSet.class.getName() + "<" + QScannerMatchData.class.getName() + ">();\n");
		out.print("            for(" + QScannerMatchData.class.getName() + " qm : matches)\n");
		out.print("            {\n");
		out.print("                if(lexeme == null) lexeme = qm.getToken().getLexeme();\n");
		out.print("                if(positionFollowing == null) positionFollowing = qm.getPositionFollowing();\n");
		out.print("                if(!lexeme.equals(qm.getToken().getLexeme())) error(_pos,\"Attempt to run disambiguation on matches with unequal lexemes '\" + lexeme + \"' and '\" + qm.getToken().getLexeme() + \"'\");\n");
		out.print("                if(!positionFollowing.equals(qm.getPositionFollowing())) error(_pos,\"Attempt to run disambiguation on matches with unequal preceding whitespace\");\n");
		out.print("                matchesT.add(qm.getToken().bareSym());\n");
		out.print("                matchesD.put(qm);\n");
		out.print("            }\n");
		first = true;
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			out.print("            ");
			if(!first) out.print("else ");
			else first = false;
			out.print("if(matchesT.equals(group_" + lexGroupTransTable.get(group.getName()) + ")) return disambiguate_" + lexGroupTransTable.get(group.getName()) + "(lexeme,matchesD);\n");
		}
		out.print("            ");
		
		if(!first) out.print("else ");
		out.print("return null;\n");
		out.print("        }\n");
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			out.print("        public " + QScannerMatchData.class.getName() + " disambiguate_" + lexGroupTransTable.get(group.getName()) + "(String lexeme," + DynHashSet.class.getName() + "<" + QScannerMatchData.class.getName() + "> _layouts)\n");
			out.print("        throws " + CopperException.class.getName() + "\n");
			out.print("        {\n");
			for(Terminal t : group.getMembers())
			{
				out.print("            @SuppressWarnings(\"unused\") " + QScannerMatchData.class.getName() + " " + t.getId() + " = _layouts.get(qsm(sym_" + symbolTransTable.get(t) + ".getId(),null,null,null,null));\n");
			}
			out.print("            " + group.getDisambigCode() + "\n");
			out.print("        }\n");
		}
		out.print("    }\n");
		out.print("\n");
		out.print(parserAncillaries);
		out.print("\n");
		out.print("}\n");
		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"  Scanner code...\n");
		// DEBUG-END
		QScannerGenerator scannerGen = new QScannerGenerator(logger);
		for(Terminal t : grammar.getT())
		{
			if(t.equals(FringeSymbols.EOF)) continue;
			ParsedRegex pr = grammar.getRegex(t);
			if(pr == null)
			{
				if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"No regex provided for terminal " + t);
				return;
			}
			scannerGen.addRegex(t.getId(),pr,grammar.getLexicalAttributes(t),null);
		}
		scannerInfo = scannerGen.compile(grammar,out,"","","",scannerName,"","");
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"  Lexical ambiguity check...");
		LexicalAmbiguityChecker lexChecker = new GenericLexicalAmbiguityChecker(logger);
		lexChecker.checkLexicalAmbiguities(grammar,scannerInfo,parseTable);
	}
	
	public int getScannerStateCount()
	{
		return scannerInfo.length;
	}
}
