package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CharacterSetRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ChoiceRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ConcatenationRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.EmptyStringRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.KleeneStarRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.MacroHoleRegexBean;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;

public interface RegexBeanVisitor<RT,E extends Exception>
{
	public RT visitChoiceRegex(ChoiceRegexBean bean) throws E;
	public RT visitConcatenationRegex(ConcatenationRegexBean bean) throws E;
	public RT visitKleeneStarRegex(KleeneStarRegexBean bean) throws E;
	public RT visitEmptyStringRegex(EmptyStringRegexBean bean) throws E;
	public RT visitCharacterSetRegex(CharacterSetRegexBean bean,SetOfCharsSyntax chars) throws E;
	public RT visitMacroHoleRegex(MacroHoleRegexBean bean) throws E;
}
