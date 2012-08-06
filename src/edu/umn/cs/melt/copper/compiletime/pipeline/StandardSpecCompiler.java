package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementType;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.NumericParserSpecBuilder;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.SymbolTableBuilder;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ContextSets;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.builders.ContextSetBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.LALRLookaheadAndLayoutSetBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.LR0DFABuilder;
import edu.umn.cs.melt.copper.compiletime.builders.LRParseTableBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.LexicalAmbiguitySetBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.ModularDeterminismAnalyzer;
import edu.umn.cs.melt.copper.compiletime.builders.SingleScannerDFAAnnotationBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.SingleScannerDFABuilder;
import edu.umn.cs.melt.copper.compiletime.builders.TransparentPrefixSetBuilder;
import edu.umn.cs.melt.copper.compiletime.checkers.GrammarWellFormednessChecker;
import edu.umn.cs.melt.copper.compiletime.checkers.LexicalAmbiguityChecker;
import edu.umn.cs.melt.copper.compiletime.checkers.MDAResultChecker;
import edu.umn.cs.melt.copper.compiletime.checkers.ParseTableConflictChecker;
import edu.umn.cs.melt.copper.compiletime.checkers.PrecedenceCycleChecker;
import edu.umn.cs.melt.copper.compiletime.dumpers.Dumper;
import edu.umn.cs.melt.copper.compiletime.dumpers.DumperFactory;
import edu.umn.cs.melt.copper.compiletime.dumpers.PlainTextParserDumper;
import edu.umn.cs.melt.copper.compiletime.dumpers.XHTMLParserDumper;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.LexicalAmbiguities;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRDFAPrinter;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.GenericLocatedMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.TimingMessage;
import edu.umn.cs.melt.copper.compiletime.mda.MDAResults;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTablePrinter;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class StandardSpecCompiler implements SpecCompiler<ParserBean, CompilerReturnData>
{
	@Override
	public CompilerReturnData compileParser(ParserBean spec, SpecCompilerParameters args)
	throws CopperException
	{
		if(spec == null) return null;
		boolean succeeded = true;
		CompilerLogger oldStyleLogger = AuxiliaryMethods.getOrMakeLogger(args);
		edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger logger = AuxiliaryMethods.getNewStyleLogger(oldStyleLogger,args);

		
		String packageDecl = 
				(args.getPackageDecl() != null && !args.getPackageDecl().equals("")) ?
						args.getPackageDecl() :
						(spec.getPackageDecl() != null && !spec.getPackageDecl().equals("") ?
								spec.getPackageDecl() : 
						        "");
		String parserName =
			(args.getParserName() != null && !args.getParserName().equals("")) ?
					args.getParserName() :
					(spec.getClassName() != null && !spec.getClassName().equals("") ?
							spec.getClassName() : 
					        "Parser");
					
		if(args.isComposition() && spec.getType() != CopperElementType.EXTENDED_PARSER)
		{
			logger.log(new GenericLocatedMessage(CompilerLevel.QUIET, spec.getLocation(), "Cannot run the modular determinism analysis on a non-extended parser",true,false));
			logger.flush();
			return null;
		}

		long timeBefore;
		
			timeBefore = System.currentTimeMillis();
		
		PSSymbolTable symbolTable = SymbolTableBuilder.build(spec);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing symbol table",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Symbol table:\n" + symbolTable));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
		
		ParserSpec hostSpec = null;
		
		if(args.isComposition() && spec.getType() == CopperElementType.EXTENDED_PARSER)
		{
			hostSpec = NumericParserSpecBuilder.buildExt((ExtendedParserBean) spec,symbolTable,true);
		}
		ParserSpec fullSpec = NumericParserSpecBuilder.build(spec,symbolTable);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing numeric grammar specification",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Numeric spec:\n" + fullSpec.toString(symbolTable)));
			logger.flush();
			
			GrammarStatistics stats = new GrammarStatistics(fullSpec);
			timeBefore = System.currentTimeMillis();
		
		succeeded &= GrammarWellFormednessChecker.check(logger, stats, symbolTable, fullSpec, true);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking grammar well-formedness",System.currentTimeMillis() - timeBefore));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
		
		ContextSets hostContextSets = (hostSpec != null) ? ContextSetBuilder.build(hostSpec) : null;
		ContextSets contextSets = ContextSetBuilder.build(fullSpec);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing context sets",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Context sets:\n" + contextSets.toString(symbolTable)));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
		
		LR0DFA hostDFA = (hostSpec != null) ? LR0DFABuilder.build(hostSpec) : null;
		LR0DFA dfa = LR0DFABuilder.build(fullSpec);
		stats.parseStateCount = dfa.size() - 1;
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing LR(0) DFA",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"LR(0) DFA:\n" + LRDFAPrinter.toString(symbolTable,fullSpec,dfa)));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
		
		LRLookaheadAndLayoutSets hostLookaheadSets = (hostSpec != null) ? LALRLookaheadAndLayoutSetBuilder.build(hostSpec,hostContextSets,hostDFA) : null;
		LRLookaheadAndLayoutSets lookaheadSets = LALRLookaheadAndLayoutSetBuilder.build(fullSpec,contextSets,dfa);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing LALR lookahead/layout sets",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"LALR(1) DFA:\n" + LRDFAPrinter.toString(symbolTable,fullSpec,dfa,lookaheadSets)));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
			
		if(hostSpec != null)
		{
			MDAResults mdaResults = ModularDeterminismAnalyzer.build(true,hostSpec,fullSpec,hostContextSets,contextSets,hostDFA,dfa,hostLookaheadSets,lookaheadSets);
			succeeded &= MDAResultChecker.check(logger, mdaResults, symbolTable, fullSpec, dfa, stats);
			
				if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Running modular determinism analysis",System.currentTimeMillis() - timeBefore));
				logger.flush();
				
				timeBefore = System.currentTimeMillis();
		}
		
		LRParseTable parseTable = LRParseTableBuilder.build(fullSpec,dfa,lookaheadSets);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing parse table",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Parse table:\n" + LRParseTablePrinter.toString(symbolTable,fullSpec,parseTable)));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
		
		succeeded &= ParseTableConflictChecker.check(logger, symbolTable, fullSpec, parseTable, stats);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking parse table conflicts",System.currentTimeMillis() - timeBefore));
			logger.flush();

			timeBefore = System.currentTimeMillis();
		
		TransparentPrefixes prefixes = TransparentPrefixSetBuilder.build(fullSpec,parseTable);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing transparent prefix sets",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Transparent prefix sets:\n" + prefixes.toString(symbolTable)));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
		
		GeneralizedDFA scannerDFA = SingleScannerDFABuilder.build(fullSpec);
		stats.scannerStateCount = scannerDFA.stateCount();
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing scanner DFA",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Scanner DFA:\n" + scannerDFA));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
		
		SingleScannerDFAAnnotations scannerDFAAnnotations = SingleScannerDFAAnnotationBuilder.build(fullSpec,scannerDFA);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing scanner DFA annotations (accept/reject/possible sets, character map)",System.currentTimeMillis() - timeBefore));
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Scanner DFA annotations:\n" + scannerDFAAnnotations));
			logger.flush();

			timeBefore = System.currentTimeMillis();
		
		succeeded &= PrecedenceCycleChecker.check(logger, symbolTable, scannerDFAAnnotations);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking for precedence dependency cycles",System.currentTimeMillis() - timeBefore));
			logger.flush();
			
			timeBefore = System.currentTimeMillis();
		
		LexicalAmbiguities lexicalAmbiguities = LexicalAmbiguitySetBuilder.build(fullSpec,lookaheadSets,parseTable,prefixes,scannerDFAAnnotations);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking for lexical ambiguities",System.currentTimeMillis() - timeBefore));
			logger.flush();

			timeBefore = System.currentTimeMillis();
		
		succeeded &= LexicalAmbiguityChecker.check(logger, symbolTable, lexicalAmbiguities, stats);
		
			if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Reporting lexical ambiguities",System.currentTimeMillis() - timeBefore));
			logger.flush();

		if(args.isDumpReport() &&
		   (!args.isDumpOnlyOnError() || !succeeded))
		{
			PrintStream dumpStream = null;
			Dumper dumper = null;
			
			switch(args.getDumpType())
			{
			case HTML:
			case XML:
				try { dumper = new XHTMLParserDumper(symbolTable, fullSpec, dfa, lookaheadSets, parseTable, prefixes); }
				catch(ParserConfigurationException ex) { ex.printStackTrace(); }
				break;
			case PLAIN:
				dumper = new PlainTextParserDumper(80, symbolTable, fullSpec, dfa, lookaheadSets, parseTable, prefixes);
				break;
			case XML_SPEC:
				break;
			}
			
			if(dumper != null)
			{
				try
				{
					dumpStream = DumperFactory.getDumpStream(args);
				}
				catch(FileNotFoundException ex)
				{
					ex.printStackTrace();
				}
				
				if(dumpStream != null)
				{
					try
					{
						dumper.dump(args.getDumpType(),dumpStream);
					}
					catch (UnsupportedOperationException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		
		
		
		
		CompilerReturnData rv = new CompilerReturnData();
		
		rv.succeeded = succeeded;
		rv.errorlevel = succeeded ? 0 : 1;
		rv.symbolTable = symbolTable;
		rv.fullSpec = fullSpec;
		rv.packageDecl = packageDecl;
		rv.parserName = parserName;
		rv.lookaheadSets = lookaheadSets;
		rv.parseTable = parseTable;
		rv.stats = stats;
		rv.prefixes = prefixes;
		rv.scannerDFA = scannerDFA;
		rv.scannerDFAAnnotations = scannerDFAAnnotations;		

		return rv;
	}

}
