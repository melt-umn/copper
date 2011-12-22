package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;

import java.util.HashSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;


/**
 * Class to represent the "source" of a context free grammar: nonterminals,
 * terminals, productions, and the start symbol, as well as terminal regexes, nullable,
 * first and follow sets.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class GrammarSource
{
	private HashSet<NonTerminal> nt;
	private HashSet<Terminal> t;
	private Hashtable< NonTerminal,HashSet<Production> > p;
	private Hashtable< Terminal,ParsedRegex > regexes;
	private Hashtable<NonTerminal,NonTerminalAttributes> ntAttributes;
	private Hashtable<Terminal,LexicalAttributes> lexicalAttributes;
	private Hashtable<TerminalClass,TerminalClassDirectory> terminalClasses;
	private PrecedenceRelationGraph staticPrecedences;
	private Hashtable<Terminal,OperatorAttributes> operatorAttributes;
	private Hashtable<Production,ProductionAttributes> productionAttributes;
	private HashSet<ParserAttribute> parserAttributes;
	private HashSet<LexicalDisambiguationGroup> disambiguationGroups;
	private NonTerminal startSym;
	private HashSet<Terminal> startLayout;
	private Hashtable< GrammarName,HashSet<Terminal> > grammarLayout;
	private HashSet<GrammarName> containedGrammars;
	private String defaultTCode;
	private String defaultProdCode;
	private ParserSource parserSources;
	private int uselessNonterminalCount;
	private Hashtable<Symbol,String> displayNames;
	
	private ContextSets contextSets;
	
	public GrammarSource()
	{
		nt = new HashSet<NonTerminal>();
		t = new HashSet<Terminal>();
		p = new Hashtable< NonTerminal,HashSet<Production> >();
		regexes = new Hashtable< Terminal,ParsedRegex >();
		ntAttributes = new Hashtable<NonTerminal,NonTerminalAttributes>();
		lexicalAttributes = new Hashtable<Terminal,LexicalAttributes>();
		terminalClasses = new Hashtable<TerminalClass,TerminalClassDirectory>();
		operatorAttributes = new Hashtable<Terminal,OperatorAttributes>();
		productionAttributes = new Hashtable<Production,ProductionAttributes>();
		parserAttributes = new HashSet<ParserAttribute>();
		disambiguationGroups = new HashSet<LexicalDisambiguationGroup>();
		defaultTCode = "";
		defaultProdCode = "";
		startSym = null;
		startLayout = new HashSet<Terminal>();
		grammarLayout = new Hashtable< GrammarName,HashSet<Terminal> >();
		containedGrammars = new HashSet<GrammarName>();
		parserSources = new ParserSource();
		uselessNonterminalCount = 0;
		contextSets = new ContextSets();
		displayNames = new Hashtable<Symbol,String>();
	}
	
	public Iterable<NonTerminal> getNT()
	{
		return nt;
	}
	
	public boolean ntContains(NonTerminal sym)
	{
		return nt.contains(sym);
	}
	
	public int ntSize()
	{
		return nt.size();
	}
	
	public boolean addToNT(NonTerminal sym)
	{
		return nt.add(sym);
	}

	public Iterable<Production> getP(GrammarSymbol spec)
	{
		return p.get(spec);
	}
	
	public boolean pContains(Production prod)
	{
		if(!pContains(prod.getLeft())) return false;
		else return p.get(prod.getLeft()).contains(prod);
	}
	
	public boolean pContains(GrammarSymbol sym)
	{
		return p.containsKey(sym);
	}
	
	public boolean addToP(Production prod)
	{
		if(!pContains(prod.getLeft())) p.put(prod.getLeft(),new HashSet<Production>());
		return p.get(prod.getLeft()).add(prod);
	}
	
	public boolean hasRegex(Terminal spec)
	{
		return regexes.containsKey(spec);
	}

	public ParsedRegex getRegex(Terminal spec)
	{
		return regexes.get(spec);
	}

	public void addRegex(Terminal spec,ParsedRegex regex)
	{
		regexes.put(spec,regex);
	}

	public boolean hasNTAttributes(NonTerminal spec)
	{
		return ntAttributes.containsKey(spec);
	}

	public NonTerminalAttributes getNTAttributes(NonTerminal spec)
	{
		if(hasNTAttributes(spec)) return ntAttributes.get(spec);
		else return new NonTerminalAttributes(new GrammarName(""),InputPosition.initialPos(),"Object",false);
	}

	public void addNTAttributes(NonTerminal spec,NonTerminalAttributes attributes)
	{
		ntAttributes.put(spec,attributes);
	}

	public boolean hasLexicalAttributes(Terminal spec)
	{
		return lexicalAttributes.containsKey(spec);
	}

	public LexicalAttributes getLexicalAttributes(Terminal spec)
	{
		if(hasLexicalAttributes(spec)) return lexicalAttributes.get(spec);
		else return new LexicalAttributes(new GrammarName(""),InputPosition.initialPos(),"Object",FringeSymbols.EMPTY,"");
	}

	public void addLexicalAttributes(Terminal spec,LexicalAttributes attributes)
	{
		lexicalAttributes.put(spec,attributes);
	}
	
	public boolean hasTClass(TerminalClass name)
	{
		return terminalClasses.containsKey(name);
	}
	
	public boolean tClassContains(TerminalClass name,Terminal t)
	{
		if(!hasTClass(name)) return false;
		else return terminalClasses.get(name).getMembers().contains(t);
	}
	
	public HashSet<Terminal> getTClassMembers(TerminalClass name)
	{
		if(!hasTClass(name)) return new HashSet<Terminal>();
		else return terminalClasses.get(name).getMembers();
	}
	
	public boolean addTClass(TerminalClass name)
	{
		if(!hasTClass(name))
		{
			terminalClasses.put(name,new TerminalClassDirectory(name,new HashSet<Terminal>()));
			return true;
		}
		else return false;
	}
	
	public boolean addToTClass(TerminalClass name,Terminal t)
	{
		if(!hasTClass(name)) terminalClasses.put(name,new TerminalClassDirectory(name,new HashSet<Terminal>()));
		return terminalClasses.get(name).addMember(t);
	}
	
	public void constructPrecedenceRelationsGraph()
	{
		staticPrecedences = new PrecedenceRelationGraph(t);
	}
	public PrecedenceRelationGraph getPrecedenceRelationsGraph()
	{
		return staticPrecedences;
	}
	
	public void addStaticPrecedenceRelation(Terminal bottom,Terminal top)
	{
		staticPrecedences.addEdge(bottom,top);
	}

	public boolean hasOperatorAttributes(Terminal spec)
	{
		return operatorAttributes.containsKey(spec);
	}

	public OperatorAttributes getOperatorAttributes(Terminal spec)
	{
		return operatorAttributes.get(spec);
	}

	public void addOperatorAttributes(Terminal spec,OperatorAttributes attributes)
	{
		operatorAttributes.put(spec,attributes);
	}

	public boolean hasProductionAttributes(Production prod)
	{
		return productionAttributes.containsKey(prod);
	}
	
	public boolean addParserAttribute(ParserAttribute toAdd)
	{
		return parserAttributes.add(toAdd);
	}
	
	public Iterable<ParserAttribute> getParserAttributes()
	{
		return parserAttributes;
	}
	
	public boolean hasParserAttribute(ParserAttribute attr)
	{
		return parserAttributes.contains(attr);
	}
	
	public Iterable<LexicalDisambiguationGroup> getDisambiguationGroups()
	{
		return disambiguationGroups;
	}
	
	public boolean hasDisambiguationGroup(LexicalDisambiguationGroup group)
	{
		return disambiguationGroups.contains(group);
	}
	
	public boolean addDisambiguationGroup(LexicalDisambiguationGroup group)
	{
		return disambiguationGroups.add(group);
	}
	
	public int disambiguationGroupSize()
	{
		return disambiguationGroups.size();
	}
	
	public boolean hasLayout(Production prod)
	{
		return hasProductionAttributes(prod) && getProductionAttributes(prod).getLayout() != null;
	}
	
	public boolean hasTerminalLayout(Production prod)
	{
		return hasLayout(prod);
	}

	public ProductionAttributes getProductionAttributes(Production prod)
	{
		return productionAttributes.get(prod);
	}
	
	public HashSet<Terminal> getTerminalLayout(Production prod)
	{
		if(hasTerminalLayout(prod)) return getProductionAttributes(prod).getLayout();
		else return new HashSet<Terminal>();
	}

	public void addProductionAttributes(Production prod,ProductionAttributes attributes)
	{
		productionAttributes.put(prod,attributes);
	}

	public NonTerminal getStartSym()
	{
		return startSym;
	}
	
	public Production getStartProd()
	{
		return Production.production(FringeSymbols.STARTPROD_SYMBOL,FringeSymbols.STARTPRIME,startSym,FringeSymbols.EOF);
	}
	
	public HashSet<Terminal> getStartLayout()
	{
		return startLayout;
	}
	
	public boolean hasGrammarLayout(GrammarName grammar)
	{
		return grammarLayout.containsKey(grammar);
	}
	
	public HashSet<Terminal> getGrammarLayout(GrammarName grammar)
	{
		return grammarLayout.get(grammar);
	}
	
	public void setStartSym(NonTerminal startSym)
	{
		this.startSym = startSym;
	}
	
	public void addStartLayout(Terminal newStartLayout)
	{
		this.startLayout.add(newStartLayout);
	}
	
	public void addGrammarLayout(GrammarName grammar,Terminal newLayout)
	{
		if(!hasGrammarLayout(grammar)) grammarLayout.put(grammar,new HashSet<Terminal>());
		grammarLayout.get(grammar).add(newLayout);
	}
	
	public GrammarName getHostGrammarName()
	{
		return getOwner(getStartSym());
	}
	
	public boolean containsGrammar(GrammarName gn)
	{
		return containedGrammars.contains(gn);
	}
	
	public boolean addContainedGrammar(GrammarName gn)
	{
		return containedGrammars.add(gn);
	}
	
	public HashSet<GrammarName> getContainedGrammars()
	{
		return containedGrammars;
	}
	
	public Iterable<Terminal> getT()
	{
		return t;
	}
	
	public boolean tContains(Terminal sym)
	{
		return t.contains(sym);
	}
	
	public int tSize()
	{
		return t.size();
	}

	public boolean addToT(Terminal sym)
	{
		return t.add(sym);
	}
	
	public String getDefaultProdCode()
	{
		return defaultProdCode;
	}

	public void setDefaultProdCode(String defaultProdCode)
	{
		this.defaultProdCode = defaultProdCode;
	}

	public String getDefaultTCode()
	{
		return defaultTCode;
	}

	public void setDefaultTCode(String defaultTCode)
	{
		this.defaultTCode = defaultTCode;
	}
	
	public GrammarName getOwner(Terminal t)
	{
		if(!hasLexicalAttributes(t)) return null;
		else return getLexicalAttributes(t).getBelongsTo();
	}

	public GrammarName getOwner(NonTerminal nt)
	{
		if(!hasNTAttributes(nt)) return null;
		else return getNTAttributes(nt).getBelongsTo();
	}
	
	public GrammarName getOwner(Production p)
	{
		if(!hasProductionAttributes(p)) return null;
		else return getProductionAttributes(p).getBelongsTo();
	}

	public ParserSource getParserSources()
	{
		return parserSources;
	}
	
	public void setParserSources(ParserSource parserSources)
	{
		this.parserSources = parserSources;
	}

	public int getUselessNonterminalCount()
	{
		return uselessNonterminalCount;
	}

	public void setUselessNonterminalCount(int uselessNonterminalCount)
	{
		this.uselessNonterminalCount = uselessNonterminalCount;
	}

	public boolean hasDisplayName(Symbol symbol)
	{
		return displayNames.containsKey(symbol);
	}
	
	public String getDisplayName(Symbol symbol)
	{
		if(hasDisplayName(symbol)) return displayNames.get(symbol);
		else return symbol.toString();
	}
	
	public void setDisplayName(Symbol symbol,String name)
	{
		displayNames.put(symbol,name);
	}

	public ContextSets getContextSets()
	{
		return contextSets;
	}
	
	public String toString()
	{
		String rv = "[\n";
		rv += " NT = " + nt + "\n";
		rv += " T  = " + t + "\n";
		rv += " P  = [";
		for(NonTerminal sym : p.keySet()) for(Production prod : p.get(sym))
		{
			rv += "\n       " + prod;
		}
		rv += " ]\n";
		rv += " REGEXES = [";
		for(Terminal sym : regexes.keySet())
		{
			rv += "\n            " + sym + ": " + regexes.get(sym); 
		}
		rv += " ]\n";
		rv += " LEXICAL ATTRIBUTES = [";
		for(Terminal sym : lexicalAttributes.keySet())
		{
			rv += "\n            " + sym + ": " + lexicalAttributes.get(sym); 
		}
		rv += " ]\n";
		rv += " TERMINAL CLASSES = [";
		for(TerminalClass sym : terminalClasses.keySet())
		{
			rv += "\n            " + sym + ": " + terminalClasses.get(sym); 
		}
		rv += " ]\n";
		rv += " STATIC PRECEDENCES = \n";
		rv += "  " + staticPrecedences.toString();
		rv += " NON TERMINAL ATTRIBUTES = [";
		for(NonTerminal sym : ntAttributes.keySet())
		{
			rv += "\n            " + sym + ": " + ntAttributes.get(sym);
		}
		rv += " ]\n";
		rv += " OPERATOR ATTRIBUTES = [";
		for(Terminal sym : operatorAttributes.keySet())
		{
			rv += "\n            " + sym + ": " + operatorAttributes.get(sym); 
		}
		rv += " ]\n";
		rv += " PARSER ATTRIBUTES = [";
		for(ParserAttribute attr : parserAttributes)
		{
			rv += "\n            " + attr; 
		}
		rv += " ]\n";
		rv += " PRODUCTION ATTRIBUTES = [";
		for(Production prod : productionAttributes.keySet())
		{
			rv += "\n          " + prod + ": " + productionAttributes.get(prod); 
		}
		rv += "]\n";
		rv += " LEXICAL DISAMBIGUATION GROUPS = [";
		for(LexicalDisambiguationGroup group : disambiguationGroups){
			rv += "\n          " + group;
		}
		rv += "]\n";
		rv += " START = " + startSym + "\n";
		rv += " START LAYOUT = " + startLayout + "\n";
		rv += contextSets.toString();
		return rv;
	}
}
