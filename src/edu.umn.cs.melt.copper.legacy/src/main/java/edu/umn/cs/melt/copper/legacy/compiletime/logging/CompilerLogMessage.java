package edu.umn.cs.melt.copper.legacy.compiletime.logging;

import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class CompilerLogMessage
{
	long sequenceNumber,timestamp;
	CompilerLogMessageSort sort;
	private String message;
	private boolean requiresImmediateFailure;
	private Object[] parameters;
	
	
	public CompilerLogMessage(long sequenceNumber, long timestamp, String message, Object... parameters)
	{
		this.sequenceNumber = sequenceNumber;
		this.timestamp = timestamp;
		this.requiresImmediateFailure = (Boolean) parameters[CompilerLogHandler.FAIL_REQUIRED];
		this.sort = (CompilerLogMessageSort) parameters[CompilerLogHandler.SORT];
		this.message = message;
		this.parameters = parameters;
	}
	
	public long getSequenceNumber()
	{
		return sequenceNumber;
	}
	public void setSequenceNumber(long sequenceNumber)
	{
		this.sequenceNumber = sequenceNumber;
	}
	public long getTimestamp()
	{
		return timestamp;
	}
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public InputPosition getLocation()
	{
		if(parameters.length > CompilerLogHandler.LOCATION && parameters[CompilerLogHandler.LOCATION] instanceof InputPosition) return (InputPosition) parameters[CompilerLogHandler.LOCATION];
		else return null;
	}
	public void setLocation(InputPosition location)
	{
		if(parameters.length > CompilerLogHandler.LOCATION && parameters[CompilerLogHandler.LOCATION] instanceof InputPosition) parameters[CompilerLogHandler.LOCATION] = location;
		else return;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}



	public CompilerLogMessageSort getSort() {
		return sort;
	}

	public void setSort(CompilerLogMessageSort sort) {
		this.sort = sort;
	}

	public boolean isRequiresImmediateFailure()
	{
		return requiresImmediateFailure;
	}

	public void setRequiresImmediateFailure(boolean requiresImmediateFailure)
	{
		this.requiresImmediateFailure = requiresImmediateFailure;
	}

	public VirtualLocation getVirtualLocation()
	{
		if(parameters.length > CompilerLogHandler.LOCATION && parameters[CompilerLogHandler.LOCATION] instanceof VirtualLocation) return (VirtualLocation) parameters[CompilerLogHandler.LOCATION];
		else return null;
	}

	public void setVirtualLocation(VirtualLocation virtualLocation)
	{
		if(parameters.length > CompilerLogHandler.LOCATION && parameters[CompilerLogHandler.LOCATION] instanceof VirtualLocation) parameters[CompilerLogHandler.LOCATION] = virtualLocation;
		else return;
	}
}
