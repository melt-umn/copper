package edu.umn.cs.melt.copper.legacy.compiletime.logging;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class CompilerLogHandler extends Handler
{
	public static final int SORT = 0;
	public static final int LOCATION = 1;
	public static final int FAIL_REQUIRED = 2;
	public static final int MIN_SIZE = 3;
	
	private Queue<CompilerLogMessage> messageQueue;
	private Queue<CompilerLogMessage> tickQueue;
	
	public CompilerLogHandler()
	{
		messageQueue = new LinkedList<CompilerLogMessage>();
		tickQueue = new LinkedList<CompilerLogMessage>();
	}

	public Queue<CompilerLogMessage> getMessageQueue()
	{
		return messageQueue;
	}
	
	public Queue<CompilerLogMessage> flushMessageQueue()
	{
		Queue<CompilerLogMessage> rv = getMessageQueue();
		messageQueue = new LinkedList<CompilerLogMessage>();
		return rv;
	}
	
	public Queue<CompilerLogMessage> getTickQueue()
	{
		return tickQueue;
	}
	
	public Queue<CompilerLogMessage> flushTickQueue()
	{
		Queue<CompilerLogMessage> rv = getTickQueue();
		tickQueue = new LinkedList<CompilerLogMessage>();
		return rv;
	}	
	
	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {}

	@Override
	public void publish(LogRecord arg0)
	{
		String newMessage = arg0.getMessage();
		Object[] newParams = arg0.getParameters();
		Queue<CompilerLogMessage> destQueue;
		if(newParams.length < MIN_SIZE ||
		   !(newParams[SORT] instanceof CompilerLogMessageSort) ||
		   !(newParams[FAIL_REQUIRED] instanceof Boolean))
		{
			messageQueue.offer(new CompilerLogMessage(
					            arg0.getSequenceNumber(),
					            arg0.getMillis(),
					            "Malformed log message\n",
					            /* SORT */ CompilerLogMessageSort.ERROR,
					            /* LOCATION */ null,
					            /* FAIL_REQUIRED */ true));
		}
		else
		{
			switch((CompilerLogMessageSort) newParams[SORT])
			{
			case TICK:
				destQueue = tickQueue;
				break;
			case DEBUG:
			case STATUS:
			case TAGGED_STATUS:
			case WARNING:
			case PARSE_TABLE_CONFLICT:
			case UNRESOLVED_CONFLICT:
			case ERROR:
			case FATAL_ERROR:
			default:
				destQueue = messageQueue;
				break;
			}
			
			destQueue.offer(new CompilerLogMessage(
				            arg0.getSequenceNumber(),
				            arg0.getMillis(),
				            newMessage,
				            newParams));
		}
	}

}
