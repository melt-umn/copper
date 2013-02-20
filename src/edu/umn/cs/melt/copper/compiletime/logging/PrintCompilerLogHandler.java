package edu.umn.cs.melt.copper.compiletime.logging;

import java.io.PrintStream;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * The default handler for log messages: simply holds a buffer of them and prints the contents
 * of the buffer when it is "flushed."
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class PrintCompilerLogHandler implements CompilerLogHandler
{
	private PrintStream out;
	private ArrayList<CompilerLogMessage> messageQueue;
	private int marker;
	
	public PrintCompilerLogHandler(PrintStream out)
	{
		this.out = out;
		messageQueue = new ArrayList<CompilerLogMessage>();
		marker = 0;
	}
	
	// This method is only needed by the legacy pipeline.
	public PrintStream getOut()
	{
		return out;
	}
	
	@Override
	public void handleMessage(CompilerLogMessage message)
	{
		messageQueue.add(message);
	}

	@Override
	public void handleErrorMessage(CompilerLogMessage message)
	throws CopperException
	{
		handleMessage(message);
		flush();
	}

	@Override
	public void flush()
	throws CopperException
	{
		boolean fail = false;
		for(;marker < messageQueue.size();marker++)
		{
			CompilerLogMessage m = messageQueue.get(marker);
			if(m.getType() == CompilerLogMessageType.LEXICAL_AMBIGUITY ||
			   m.getType() == CompilerLogMessageType.PARSE_TABLE_CONFLICT ||
			   marker > 0 && (messageQueue.get(marker - 1).getType() == CompilerLogMessageType.LEXICAL_AMBIGUITY ||
			                  messageQueue.get(marker - 1).getType() == CompilerLogMessageType.PARSE_TABLE_CONFLICT)) out.println();
			out.println(m);
			if(messageQueue.get(marker).isFatalError()) fail = true;
		}
		if(fail) throw new CopperException("Error(s) raised in compilation");
	}

}
