package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

/**
 * This is a container class for all the data returned by {@link StandardSpecCompiler}.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class StandardSpecCompilerReturnData
{
	public boolean succeeded;
	public int errorlevel;
	public PSSymbolTable symbolTable;
	public ParserSpec fullSpec;
	public String packageDecl;
	public String parserName;
	public LRLookaheadAndLayoutSets lookaheadSets;
	public LRParseTable parseTable;
	public GrammarStatistics stats;
	public TransparentPrefixes prefixes;
	public GeneralizedDFA scannerDFA;
	public SingleScannerDFAAnnotations scannerDFAAnnotations;

}
