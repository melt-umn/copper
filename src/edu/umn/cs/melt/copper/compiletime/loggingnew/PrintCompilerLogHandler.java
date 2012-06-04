package edu.umn.cs.melt.copper.compiletime.loggingnew;

import java.io.PrintStream;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

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
			out.println(messageQueue.get(marker) + "\n");
			if(messageQueue.get(marker).isFatalError()) fail = true;
		}
		if(fail) throw new CopperException("Error(s) raised in compilation");
	}

}
