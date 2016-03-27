package edu.umn.cs.melt.copper.compiletime.pipeline;


import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin: extracted compileParser into new superclass ParserBeanCompiler
 */
public class StandardSpecCompiler extends ParserBeanCompiler<StandardSpecCompilerReturnData>
{
	@Override
	public StandardSpecCompilerReturnData compileParser(ParserBean spec, SpecCompilerParameters args) throws CopperException {
		boolean beanCompilationSucceeded = compileParserBean(spec, args);

		if (beanCompilationSucceeded) {
			StandardSpecCompilerReturnData rv = new StandardSpecCompilerReturnData();

			rv.succeeded = this.succeeded;
			rv.errorlevel = this.succeeded ? 0 : 1;
			rv.symbolTable = this.symbolTable;
			rv.fullSpec = this.fullSpec;
			rv.packageDecl = this.packageDecl;
			rv.parserName = this.parserName;
			rv.lookaheadSets = this.lookaheadSets;
			rv.parseTable = this.parseTable;
			rv.stats = this.stats;
			rv.prefixes = this.prefixes;
			rv.scannerDFA = this.scannerDFA;
			rv.scannerDFAAnnotations = this.scannerDFAAnnotations;

			return rv;
		} else {
			return null;
		}
	}
}
