package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CharacterSetRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ChoiceRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ConcatenationRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.EmptyStringRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.KleeneStarRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.MacroHoleRegex;

public interface RegexBeanVisitor<RT,E extends Exception>
{
	public RT visitChoiceRegex(ChoiceRegex bean) throws E;
	public RT visitConcatenationRegex(ConcatenationRegex bean) throws E;
	public RT visitKleeneStarRegex(KleeneStarRegex bean) throws E;
	public RT visitEmptyStringRegex(EmptyStringRegex bean) throws E;
	public RT visitCharacterSetRegex(CharacterSetRegex bean,SetOfCharsSyntax chars) throws E;
	public RT visitMacroHoleRegex(MacroHoleRegex bean) throws E;
}
