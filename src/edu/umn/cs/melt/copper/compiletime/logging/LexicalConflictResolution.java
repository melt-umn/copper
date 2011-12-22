package edu.umn.cs.melt.copper.compiletime.logging;

public enum LexicalConflictResolution
{
	CONTEXT { public String toString() { return "context"; } },
	DISAMBIGUATION_FUNCTION { public String toString() { return "disambiguation function"; } };
}
