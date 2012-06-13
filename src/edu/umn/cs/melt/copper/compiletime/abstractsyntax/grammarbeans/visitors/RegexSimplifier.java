package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CharacterSetRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ChoiceRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ConcatenationRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.EmptyStringRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.KleeneStarRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.MacroHoleRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.RegexBean;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;

public class RegexSimplifier implements	RegexBeanVisitor<RegexBean, RuntimeException>
{
	@Override
	public RegexBean visitChoiceRegex(ChoiceRegexBean bean)
	throws RuntimeException
	{
		if(bean.getSubexps().size() == 1) return bean.getSubexps().get(0);
		
		ChoiceRegexBean rv = new ChoiceRegexBean();
		CharacterSetRegexBean chars = null;
		
		for(RegexBean subexp : bean.getSubexps())
		{
			RegexBean subexpS = subexp.acceptVisitor(this);
			if(subexpS instanceof CharacterSetRegexBean)
			{
				if(chars == null) chars = (CharacterSetRegexBean) subexpS;
				else chars = CharacterSetRegexBean.union(chars,(CharacterSetRegexBean) subexpS);
			}
			else if(subexpS instanceof ChoiceRegexBean)
			{
				rv.addSubexps(((ChoiceRegexBean) subexpS).getSubexps());
			}
			else rv.addSubexp(subexpS);
		}
		if(chars != null) rv.addSubexp(chars);

		if(rv.getSubexps().size() == 1) return rv.getSubexps().get(0);
		else return rv;
	}

	@Override
	public RegexBean visitConcatenationRegex(ConcatenationRegexBean bean)
	throws RuntimeException
	{
		if(bean.getSubexps().size() == 1) return bean.getSubexps().get(0);
		
		ConcatenationRegexBean rv = new ConcatenationRegexBean();
		
		for(RegexBean subexp : bean.getSubexps())
		{
			RegexBean subexpS = subexp.acceptVisitor(this);
			if(subexpS instanceof ConcatenationRegexBean)
			{
				rv.addSubexps(((ConcatenationRegexBean) subexpS).getSubexps());
			}
			else if(!(subexpS instanceof EmptyStringRegexBean)) rv.addSubexp(subexpS);
		}
		
		if(rv.getSubexps().size() == 1) return rv.getSubexps().get(0);
		else return rv;
	}

	@Override
	public RegexBean visitKleeneStarRegex(KleeneStarRegexBean bean)
	throws RuntimeException
	{
		return new KleeneStarRegexBean(bean.getSubexp().acceptVisitor(this));
	}

	@Override
	public RegexBean visitEmptyStringRegex(EmptyStringRegexBean bean)
	throws RuntimeException
	{
		return bean;
	}

	@Override
	public RegexBean visitCharacterSetRegex(CharacterSetRegexBean bean,	SetOfCharsSyntax chars)
	throws RuntimeException
	{
		return bean;
	}

	@Override
	public RegexBean visitMacroHoleRegex(MacroHoleRegexBean bean)
	throws RuntimeException
	{
		return bean;
	}

}
