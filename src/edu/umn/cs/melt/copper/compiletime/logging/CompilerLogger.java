package edu.umn.cs.melt.copper.compiletime.logging;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public abstract class CompilerLogger
{
	protected Logger logger;
	protected CompilerLogHandler handler;
	protected PrintStream out;
	private int tickCounter;
	private int parseTableConflictCounter,resolvedParseTableConflictCounter;
	private int lexicalConflictCounter,resolvedLexicalConflictByContextCounter,resolvedLexicalConflictByGroupCounter;
	private boolean hasErrors;
	
	protected CompilerLogger()
	{
		tickCounter = -1;
		parseTableConflictCounter = 0;
		resolvedParseTableConflictCounter = 0;
		lexicalConflictCounter = 0;
		resolvedLexicalConflictByContextCounter = 0;
		resolvedLexicalConflictByGroupCounter = 0;
		hasErrors = false;
	}
	
	public void logMessage(CompilerLogMessageSort sort,InputPosition location,String message)
	{
		Object[] parameters = new Object[3];
		parameters[0] = sort;
		parameters[1] = location;
		parameters[2] = false;
		hasErrors |= sort.isErrorLevel();
		logger.log(sort.getLevel(),message,parameters);
	}
	
	public void logTick(int modulo,String message)
	{
		tickCounter = (tickCounter + 1) % modulo;
		if(tickCounter != 0) return;
		Object[] parameters = new Object[3];
		parameters[0] = CompilerLogMessageSort.TICK;
		parameters[1] = null;
		parameters[2] = false;
		logger.log(CompilerLogMessageSort.TICK.getLevel(),message,parameters);
		flushTicks();
	}
	
	public void logErrorMessage(CompilerLogMessageSort sort,InputPosition location,String message)
	throws CopperException
	{
		Object[] parameters = new Object[3];
		parameters[0] = sort;
		parameters[1] = location;
		parameters[2] = true;
		hasErrors |= sort.isErrorLevel();
		logger.log(sort.getLevel(),message,parameters);
		flushMessages();
	}
	
	public void logParseTableConflict(CompilerLogMessageSort sort,boolean resolved,int statenum,String cell,String message)
	{
		parseTableConflictCounter++;
		if(resolved) resolvedParseTableConflictCounter++;
		Object[] parameters = new Object[4];
		parameters[0] = sort;
		parameters[1] = statenum;
		parameters[2] = false;
		parameters[3] = cell;
		hasErrors |= sort.isErrorLevel();
		logger.log(sort.getLevel(),message,parameters);
	}
	
	public void logLexicalConflict(CompilerLogMessageSort sort,LexicalConflictResolution howResolved,TreeSet<Integer> states,HashSet<Terminal> ambiguity,String message)
	{
		lexicalConflictCounter++;
		if(howResolved != null)
		{
			switch(howResolved)
			{
			case CONTEXT:
				resolvedLexicalConflictByContextCounter++;
				break;
			case DISAMBIGUATION_FUNCTION:
				resolvedLexicalConflictByGroupCounter++;
				break;
			default:
			}
		}

		Object[] parameters = new Object[4];
		parameters[0] = sort;
		parameters[1] = states;
		parameters[2] = (howResolved == null);
		parameters[3] = ambiguity;
		hasErrors |= sort.isErrorLevel();
		logger.log(sort.getLevel(),message,parameters);
	}

	public void logParsingErrorMessage(VirtualLocation virtualLocation,
			                           int statenum,
			                           long realCharIndex,
			                           String message)
	throws CopperException
	{
		Object[] parameters = new Object[5];
		parameters[0] = CompilerLogMessageSort.PARSING_ERROR;
		parameters[1] = virtualLocation;
		parameters[2] = true;
		parameters[3] = statenum;
		parameters[4] = realCharIndex;
		logger.log(CompilerLogMessageSort.PARSING_ERROR.getLevel(),message,parameters);
		flushMessages();
	}
	
	public abstract void flushMessages()
	throws CopperException;
	
	public boolean hasErrors() { return hasErrors; }
	
	public abstract void flushTicks();

	public Logger getLogger()
	{
		return logger;
	}

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	public CompilerLogHandler getHandler()
	{
		return handler;
	}

	public void setHandler(CompilerLogHandler handler)
	{
		this.handler = handler;
	}

	public PrintStream getOut()
	{
		return out;
	}

	public void setOut(PrintStream out)
	{
		this.out = out;
	}
	
	public int getParseTableConflictCounter()
	{
		return parseTableConflictCounter;
	}

	public void setParseTableConflictCounter(int conflictCounter)
	{
		this.parseTableConflictCounter = conflictCounter;
	}

	public int getResolvedParseTableConflictCounter() {
		return resolvedParseTableConflictCounter;
	}

	public void setResolvedParseTableConflictCounter(int resolvedConflictCounter) {
		this.resolvedParseTableConflictCounter = resolvedConflictCounter;
	}

	public int getLexicalConflictCounter() {
		return lexicalConflictCounter;
	}

	public void setLexicalConflictCounter(int lexicalConflictCounter) {
		this.lexicalConflictCounter = lexicalConflictCounter;
	}

	public int getResolvedLexicalConflictByContextCounter() {
		return resolvedLexicalConflictByContextCounter;
	}

	public void setResolvedLexicalConflictByContextCounter(
			int resolvedLexicalConflictByContextCounter) {
		this.resolvedLexicalConflictByContextCounter = resolvedLexicalConflictByContextCounter;
	}

	public int getResolvedLexicalConflictByGroupCounter() {
		return resolvedLexicalConflictByGroupCounter;
	}

	public void setResolvedLexicalConflictByGroupCounter(
			int resolvedLexicalConflictByGroupCounter) {
		this.resolvedLexicalConflictByGroupCounter = resolvedLexicalConflictByGroupCounter;
	}

	public Level getLevel()
	{
		return logger.getLevel();
	}
	
	public void setLevel(Level level)
	{
		logger.setLevel(level);
	}
	
	public boolean isLoggable(CompilerLogMessageSort sort)
	{
		return logger.isLoggable(sort.getLevel());
	}
}
