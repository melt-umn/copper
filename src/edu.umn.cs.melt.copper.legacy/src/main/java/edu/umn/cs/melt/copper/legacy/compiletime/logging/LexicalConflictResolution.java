package edu.umn.cs.melt.copper.legacy.compiletime.logging;

public enum LexicalConflictResolution
{
	CONTEXT { public String toString() { return "context"; } },
	DISAMBIGUATION_FUNCTION { public String toString() { return "disambiguation function"; } };
}
