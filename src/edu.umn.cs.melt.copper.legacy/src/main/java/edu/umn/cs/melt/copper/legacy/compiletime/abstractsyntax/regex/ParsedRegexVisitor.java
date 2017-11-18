package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex;

public interface ParsedRegexVisitor<SYNTYPE,INHTYPE,E extends Exception>
{
	public SYNTYPE visitCharacterSet(CharacterSet regex,INHTYPE inheritance) throws E;
	public SYNTYPE visitChoice(Choice regex,INHTYPE inheritance) throws E;
	public SYNTYPE visitConcatenation(Concatenation regex,INHTYPE inheritance) throws E;
	public SYNTYPE visitEmptyString(EmptyString regex,INHTYPE inheritance) throws E;
	public SYNTYPE visitKleeneStar(KleeneStar regex,INHTYPE inheritance) throws E;
	public SYNTYPE visitMacroHole(MacroHole regex,INHTYPE inheritance) throws E;
}
