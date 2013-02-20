package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CharacterSetRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ChoiceRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ConcatenationRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.EmptyStringRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.KleeneStarRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.MacroHoleRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;

public class RegexSimplifier implements	RegexBeanVisitor<Regex, RuntimeException>
{
	@Override
	public Regex visitChoiceRegex(ChoiceRegex bean)
	throws RuntimeException
	{
		if(bean.getSubexps().size() == 1) return bean.getSubexps().get(0);
		
		ChoiceRegex rv = new ChoiceRegex();
		CharacterSetRegex chars = null;
		
		for(Regex subexp : bean.getSubexps())
		{
			Regex subexpS = subexp.acceptVisitor(this);
			if(subexpS instanceof CharacterSetRegex)
			{
				if(chars == null) chars = (CharacterSetRegex) subexpS;
				else chars = CharacterSetRegex.union(chars,(CharacterSetRegex) subexpS);
			}
			else if(subexpS instanceof ChoiceRegex)
			{
				rv.addSubexps(((ChoiceRegex) subexpS).getSubexps());
			}
			else rv.addSubexp(subexpS);
		}
		if(chars != null) rv.addSubexp(chars);

		if(rv.getSubexps().size() == 1) return rv.getSubexps().get(0);
		else return rv;
	}

	@Override
	public Regex visitConcatenationRegex(ConcatenationRegex bean)
	throws RuntimeException
	{
		if(bean.getSubexps().size() == 1) return bean.getSubexps().get(0);
		
		ConcatenationRegex rv = new ConcatenationRegex();
		
		for(Regex subexp : bean.getSubexps())
		{
			Regex subexpS = subexp.acceptVisitor(this);
			if(subexpS instanceof ConcatenationRegex)
			{
				rv.addSubexps(((ConcatenationRegex) subexpS).getSubexps());
			}
			else if(!(subexpS instanceof EmptyStringRegex)) rv.addSubexp(subexpS);
		}
		
		if(rv.getSubexps().size() == 1) return rv.getSubexps().get(0);
		else return rv;
	}

	@Override
	public Regex visitKleeneStarRegex(KleeneStarRegex bean)
	throws RuntimeException
	{
		return new KleeneStarRegex(bean.getSubexp().acceptVisitor(this));
	}

	@Override
	public Regex visitEmptyStringRegex(EmptyStringRegex bean)
	throws RuntimeException
	{
		return bean;
	}

	@Override
	public Regex visitCharacterSetRegex(CharacterSetRegex bean,	SetOfCharsSyntax chars)
	throws RuntimeException
	{
		return bean;
	}

	@Override
	public Regex visitMacroHoleRegex(MacroHoleRegex bean)
	throws RuntimeException
	{
		return bean;
	}

}
