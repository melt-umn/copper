package parsers;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CharacterSetRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ConcatenationRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.KleeneStarRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;
//import edu.umn.cs.melt.copper.compiletime.concretesyntax.oldxml.CustomRegexParser;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.main.CopperDumpType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.Location;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class Grammar3p23API
{
	public static void main(String[] args)
	throws CopperException,ParseException
	{
		CompilerLogger logger = new StringBasedCompilerLogger();
		logger.setOut(System.err);

		Location l = new VirtualLocation(Grammar3p23API.class.getName() + ".main()",0,0);
		
		
		
		
		/* ==================== TERMINAL CLASSES ==================== */
		
		TerminalClassBean main = new TerminalClassBean();
		main.setName("main");
		main.setLocation(l);
		TerminalClassBean punc = new TerminalClassBean();
		punc.setName("punc");
		punc.setLocation(l);
		TerminalClassBean var = new TerminalClassBean();
		var.setName("var");
		var.setLocation(l);
		
		
		
		
		/* ==================== TERMINALS ==================== */

		TerminalBean ws = new TerminalBean();
		ws.setName("ws");
		ws.setDisplayName("ws");
		ws.setLocation(l);
		ws.setCode("System.out.print(\"[space]\" + Grammar3p23$lineBreak);");
		ws.setRegex(
		 new ConcatenationRegexBean().addSubexps(
		  new CharacterSetRegexBean().addLooseChar(' '),
		  new KleeneStarRegexBean(
		   new CharacterSetRegexBean().addLooseChar(' '))));
		HashSet<CopperElementReference> wsClasses = new HashSet<CopperElementReference>();
		wsClasses.add(CopperElementReference.ref(main.getName(),l));
		wsClasses.add(CopperElementReference.ref(punc.getName(),l));
		ws.setTerminalClasses(wsClasses);
		
		TerminalBean plus = new TerminalBean();
		plus.setName("plus");
		plus.setDisplayName("'+'");
		plus.setLocation(l);
		plus.setCode("System.out.print(\"[+]\" + Grammar3p23$lineBreak);");
		plus.setRegex(new CharacterSetRegexBean().addLooseChar('+'));
		HashSet<CopperElementReference> plusClasses = new HashSet<CopperElementReference>();
		plusClasses.add(CopperElementReference.ref(main.getName(),l));
		plusClasses.add(CopperElementReference.ref(punc.getName(),l));
		plus.setTerminalClasses(plusClasses);

		TerminalBean minus = new TerminalBean();
		minus.setName("minus");
		minus.setDisplayName("'-'");
		minus.setLocation(l);
		minus.setCode("System.out.print(\"[-]\" + Grammar3p23$lineBreak);");
		minus.setRegex(new CharacterSetRegexBean().addLooseChar('-'));
		HashSet<CopperElementReference> minusClasses = new HashSet<CopperElementReference>();
		minusClasses.add(CopperElementReference.ref(main.getName(),l));
		minusClasses.add(CopperElementReference.ref(punc.getName(),l));
		minus.setTerminalClasses(minusClasses);

		TerminalBean x = new TerminalBean();
		x.setName("x");
		x.setDisplayName("x");
		x.setLocation(l);
		x.setCode("System.out.print(\"[x]\" + Grammar3p23$lineBreak);");
		x.setRegex(new CharacterSetRegexBean().addLooseChar('x'));
		HashSet<CopperElementReference> xClasses = new HashSet<CopperElementReference>();
		xClasses.add(CopperElementReference.ref(main.getName(),l));
		xClasses.add(CopperElementReference.ref(var.getName(),l));
		x.setTerminalClasses(xClasses);

		TerminalBean x2 = new TerminalBean();
		x2.setName("x2");
		x2.setDisplayName("x2");
		x2.setLocation(l);
		x2.setPrefix(CopperElementReference.ref(minus.getName(),l));
		x2.setCode("System.out.print(\"[x2]\" + Grammar3p23$lineBreak);");
		x2.setRegex(new CharacterSetRegexBean().addLooseChar('x'));
		HashSet<CopperElementReference> x2Classes = new HashSet<CopperElementReference>();
		x2Classes.add(CopperElementReference.ref(main.getName(),l));
		x2Classes.add(CopperElementReference.ref(var.getName(),l));
		x2.setTerminalClasses(x2Classes);

		TerminalBean x3 = new TerminalBean();
		x3.setName("x3");
		x3.setDisplayName("x3");
		x3.setLocation(l);
		x3.setCode("System.out.print(\"[x3]\" + Grammar3p23$lineBreak);");
		x3.setRegex(new CharacterSetRegexBean().addLooseChar('x'));
		HashSet<CopperElementReference> x3Classes = new HashSet<CopperElementReference>();
		x3Classes.add(CopperElementReference.ref(main.getName(),l));
		x3Classes.add(CopperElementReference.ref(var.getName(),l));
		x3.setTerminalClasses(x3Classes);
		HashSet<CopperElementReference> x3SubmitList = new HashSet<CopperElementReference>();
		x3SubmitList.add(CopperElementReference.ref(x.getName(),l));
		x3.setSubmitList(x3SubmitList);
		

		
		/* ==================== GRAMMAR LAYOUT ==================== */

		HashSet<CopperElementReference> grammarLayout = new HashSet<CopperElementReference>();
		grammarLayout.add(CopperElementReference.ref(ws.getName(),l));
		
		
		/* ==================== NONTERMINALS ==================== */
		
		NonTerminalBean E = new NonTerminalBean();
		E.setName("E");
		E.setDisplayName("E");
		E.setLocation(l);
		
		NonTerminalBean T = new NonTerminalBean();
		T.setName("T");
		T.setDisplayName("T");
		T.setLocation(l);
		
		
		
		
		/* ==================== DISAMBIGUATION FUNCTIONS ==================== */
		
		DisambiguationFunctionBean xs = new DisambiguationFunctionBean();
		xs.setName("xs");
		xs.setLocation(l);
		xs.setCode("return Grammar3p23$x;");
		HashSet<CopperElementReference> xsMembers = new HashSet<CopperElementReference>();
		xsMembers.add(CopperElementReference.ref(x.getName(),l));
		xsMembers.add(CopperElementReference.ref(x2.getName(),l));
		xs.setMembers(xsMembers);
		
		
		
		
		/* ==================== PRODUCTIONS ==================== */		
		
		ProductionBean TplusE = new ProductionBean();
		TplusE.setName("TplusE");
		TplusE.setLocation(l);
		TplusE.setLhs(CopperElementReference.ref(E.getName(),l));
		ArrayList<CopperElementReference> TplusERHS = new ArrayList<CopperElementReference>();
		TplusERHS.add(CopperElementReference.ref(T.getName(),l));
		TplusERHS.add(CopperElementReference.ref(plus.getName(),l));
		TplusERHS.add(CopperElementReference.ref(E.getName(),l));
		TplusE.setRhs(TplusERHS);
		
		ProductionBean EtoT = new ProductionBean();
		EtoT.setName("EtoT");
		EtoT.setLocation(l);
		EtoT.setLhs(CopperElementReference.ref(E.getName(),l));
		ArrayList<CopperElementReference> EtoTRHS = new ArrayList<CopperElementReference>();
		EtoTRHS.add(CopperElementReference.ref(T.getName(),l));
		EtoT.setRhs(EtoTRHS);

		ProductionBean TtoX = new ProductionBean();
		TtoX.setName("TtoX");
		TtoX.setLocation(l);
		TtoX.setLhs(CopperElementReference.ref(T.getName(),l));
		ArrayList<CopperElementReference> TtoXRHS = new ArrayList<CopperElementReference>();
		TtoXRHS.add(CopperElementReference.ref(x.getName(),l));
		TtoX.setRhs(TtoXRHS);

		ProductionBean TtoX2 = new ProductionBean();
		TtoX2.setName("TtoX2");
		TtoX2.setLocation(l);
		TtoX2.setLhs(CopperElementReference.ref(T.getName(),l));
		ArrayList<CopperElementReference> TtoX2RHS = new ArrayList<CopperElementReference>();
		TtoX2RHS.add(CopperElementReference.ref(x2.getName(),l));
		TtoX2.setRhs(TtoX2RHS);

		ProductionBean TtoX3 = new ProductionBean();
		TtoX3.setName("TtoX3");
		TtoX3.setLocation(l);
		TtoX3.setLhs(CopperElementReference.ref(T.getName(),l));
		ArrayList<CopperElementReference> TtoX3RHS = new ArrayList<CopperElementReference>();
		TtoX3RHS.add(CopperElementReference.ref(x3.getName(),l));
		TtoX3.setRhs(TtoX3RHS);
		
		
		
		
		
		/* ==================== PARSER ATTRIBUTES ==================== */
		
		ParserAttributeBean lineBreak = new ParserAttributeBean();
		lineBreak.setName("lineBreak");
		lineBreak.setLocation(l);
		lineBreak.setAttributeType(String.class.getName());
		lineBreak.setCode("Grammar3p23$lineBreak = \"\\n\";");
		
		
		
		/* ==================== GRAMMARS ==================== */

		GrammarBean grammar = new GrammarBean();
		grammar.setName("Grammar3p23");
		grammar.setLocation(l);
		//grammar.setStartLayout(grammarLayout);
		grammar.setGrammarLayout(grammarLayout);
		
		grammar.addGrammarElement(main);
		grammar.addGrammarElement(punc);
		grammar.addGrammarElement(var);
		
		grammar.addGrammarElement(ws);
		grammar.addGrammarElement(plus);
		grammar.addGrammarElement(minus);
		grammar.addGrammarElement(x);
		grammar.addGrammarElement(x2);
		grammar.addGrammarElement(x3);

		grammar.addGrammarElement(E);
		grammar.addGrammarElement(T);

		grammar.addGrammarElement(xs);

		grammar.addGrammarElement(TplusE);
		grammar.addGrammarElement(EtoT);
		grammar.addGrammarElement(TtoX);
		grammar.addGrammarElement(TtoX2);
		grammar.addGrammarElement(TtoX3);
		
		grammar.addGrammarElement(lineBreak);
		
		

		/* ==================== PARSERS ==================== */

		ParserBean mainParser = new ParserBean();
		mainParser.setName("Grammar3p23Parser");
		mainParser.setPackageDecl("parsers");
		mainParser.setLocation(l);
		mainParser.addGrammar(grammar);
		mainParser.setStartSymbol(CopperElementReference.ref(grammar.getName(),E.getName(),l));
		
		
		
		
		/* Check consistency. */
		
		ParserCompilerParameters compilerArgs = new ParserCompilerParameters();
		compilerArgs.setLogger(logger);
		compilerArgs.setDumpReport(true);
		compilerArgs.setDumpType(CopperDumpType.HTML);
		compilerArgs.setDumpFile("Grammar3p23Dump.html");
		try
		{
			compilerArgs.setOutputStream(new PrintStream("parsers/Grammar3p23Parser.java"));
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
		
		try
		{
			ParserCompiler.compile(mainParser,compilerArgs);
		}
		catch(CopperException ex)
		{
			System.err.println(ex.getMessage());
		}
	}
}
