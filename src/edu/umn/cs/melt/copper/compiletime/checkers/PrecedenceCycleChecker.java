package edu.umn.cs.melt.copper.compiletime.checkers;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.CyclicPrecedenceRelationMessage;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * Checks that there are no cyclic precedence relations (e.g., A and B are on each other's submit lists)
 * in a parser specification.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class PrecedenceCycleChecker
{
	private CompilerLogger logger;
	private SymbolTable<CopperASTBean> symbolTable;
	private SingleScannerDFAAnnotations scannerDFAAnnotations;

	private PrecedenceCycleChecker(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable,SingleScannerDFAAnnotations scannerDFAAnnotations)
	{
		this.logger = logger;
		this.symbolTable = symbolTable;
		this.scannerDFAAnnotations = scannerDFAAnnotations;
	}
	
	public static boolean check(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable,SingleScannerDFAAnnotations scannerDFAAnnotations)
	throws CopperException
	{
		return new PrecedenceCycleChecker(logger, symbolTable, scannerDFAAnnotations).checkCycles();
	}
	
	private boolean checkCycles()
	throws CopperException
	{
		if(logger.isLoggable(CompilerLevel.QUIET))
		{
			for(BitSet bs : scannerDFAAnnotations.circularDependencies)
			{
				logger.log(new CyclicPrecedenceRelationMessage(symbolTable,bs));
			}
		}
		return (scannerDFAAnnotations.circularDependencies.length == 0);
	}
}
