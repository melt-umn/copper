package edu.umn.cs.melt.copper.legacy.compiletime.statistics;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;

public class ScannerStatistics
{
	public Terminal lastMatched;
	public int timesScannedAtPos;
	public int repeatScans;
	public int shiftableChanged;
	public int shiftableWasSubsetOnRepeat;
	public int lastMatchedTokenInSubset;
	public int maxScansPerToken;
	public int tsapSummation;
	public int tokensSought;
	
	
	public ScannerStatistics()
	{
		timesScannedAtPos = 0;
		repeatScans = 0;
		shiftableChanged = 0;
		shiftableWasSubsetOnRepeat = 0;
		maxScansPerToken = 1;
		tsapSummation = 0;
		tokensSought = 0;
	}
	
	public String toString()
	{
		tsapSummation += timesScannedAtPos;
	    return "\n  Scans = " + tsapSummation +
	            "\n  Repeat_scans = " + repeatScans +
	            "\n  Tokens = " + tokensSought +
	            "\n  Average_scans_per_token = " + ((double) tsapSummation / tokensSought) +
	            "\n  Maximum_scans_per_token = " + maxScansPerToken +
	            "\n  Shiftable_changed_absolute = " + shiftableChanged +
	            "\n  Shiftable_changed_percentage_relative_to_Repeat_scans = " + 100 * ((double) shiftableChanged / (tsapSummation - tokensSought)) +
	            "\n  Shiftable_was_subset_on_repeat_absolute = " + shiftableWasSubsetOnRepeat + 
	            "\n  Shiftable_was_subset_on_repeat_percentage_relative_to_repeat_scans = " + 100 * ((double) shiftableWasSubsetOnRepeat / repeatScans) +
	            "\n  Subset_contained_last_matched_token_absolute = " + lastMatchedTokenInSubset +
	            "\n  Shiftable_contained_last_matched_token_percentage_relative_to_shiftable_was_subset = " + 100 * ((double) lastMatchedTokenInSubset / shiftableWasSubsetOnRepeat)
	            ;
	}
}
