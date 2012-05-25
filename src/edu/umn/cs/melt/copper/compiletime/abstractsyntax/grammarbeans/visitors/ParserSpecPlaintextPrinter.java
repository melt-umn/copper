package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CharacterSetRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ChoiceRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ConcatenationRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.EmptyStringRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtensionGrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.KleeneStarRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.MacroHoleRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.RegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;

/**
 * Prints out a parser spec in plaintext.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ParserSpecPlaintextPrinter implements CopperASTBeanVisitor<Boolean,RuntimeException>,RegexBeanVisitor<Boolean,RuntimeException>
{
	private ParserBean currentParser;
	private GrammarBean currentGrammar;
	private StringBuffer out;
	
	public static String specToString(ParserBean spec)
	{
		ParserSpecPlaintextPrinter printer = new ParserSpecPlaintextPrinter();
		spec.acceptVisitor(printer);
		return printer.out.toString();
	}
	
	public ParserSpecPlaintextPrinter()
	{
		this.out = new StringBuffer();
		currentParser = null;
		currentGrammar = null;
	}

	@Override
	public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
	throws RuntimeException
	{
		out.append("      Disambiguation " + (bean.getDisambiguateTo() == null ? "function" : "group") + ": " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		if(bean.getDisambiguateTo() != null) out.append("        Disambiguates to: " + getName(bean.getDisambiguateTo())).append("\n");
		out.append("        Members:");
		for(CopperElementReference member : bean.getMembers())
		{
			out.append("\n      	    " + getName(member));
		}
		out.append("\n");
		return false;
	}

	@Override
	public Boolean visitGrammarBean(GrammarBean bean)
	throws RuntimeException
	{
		currentGrammar = bean;
		out.append("  Grammar " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		out.append("    Grammar layout:");
		if(bean.getGrammarLayout() == null) out.append(" NONE\n");
		else for(CopperElementReference layout : bean.getGrammarLayout())
		{
			out.append("\n      " + getName(layout));
		}
		out.append("\n    Elements:\n");
		boolean hasError = false;
		for(CopperElementName n : bean.getGrammarElements())
		{
			hasError |= bean.getGrammarElement(n).acceptVisitor(this);
		}
		return hasError;			
	}

	@Override
	public Boolean visitExtensionGrammarBean(ExtensionGrammarBean bean)
	throws RuntimeException
	{
		currentGrammar = bean;
		out.append("  Extension grammar " + bean.getName() + "(" + bean.getDisplayName() + ")\n");
		boolean hasError = false;
		out.append("    Marking terminal:\n");
		hasError |= bean.getMarkingTerminal().acceptVisitor(this);
		out.append("\n    Start production:\n");
		hasError |= bean.getStartProduction().acceptVisitor(this);
		out.append("\n    Grammar layout:");
		if(bean.getGrammarLayout() == null) out.append(" NONE\n");
		else for(CopperElementReference layout : bean.getGrammarLayout())
		{
			out.append("\n      " + getName(layout));
		}
		out.append("\n    Elements:\n");
		for(CopperElementName n : bean.getGrammarElements())
		{
			hasError |= bean.getGrammarElement(n).acceptVisitor(this);
		}
		return hasError;			
	}

	@Override
	public Boolean visitNonTerminalBean(NonTerminalBean bean)
	throws RuntimeException
	{
		out.append("      Nonterminal " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		return false;
	}

	@Override
	public Boolean visitParserAttributeBean(ParserAttributeBean bean)
	throws RuntimeException
	{
		out.append("      Parser attribute " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		out.append("        Type: " + bean.getAttributeType()).append("\n");
		return false;
	}

	@Override
	public Boolean visitParserBean(ParserBean bean)
	throws RuntimeException
	{
		currentParser = bean;
		out.append("Parser " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		out.append("  Package declaration: " + bean.getPackageDecl()).append("\n");
		out.append("  Class name: " + bean.getClassName()).append("\n");
		out.append("  Start symbol: " + getName(bean.getStartSymbol())).append("\n");
		out.append("  Start layout:");
		if(bean.getStartLayout() == null) out.append(" NONE\n");
		else for(CopperElementReference layout : bean.getStartLayout())
		{
			out.append("\n    " + getName(layout));
		}
		out.append("\n  Grammars:");
		for(CopperElementName grammar : bean.getGrammars())
		{
			out.append("\n    " + grammar);
		}
		out.append("\n");
		boolean hasError = false;
		for(CopperElementName n : bean.getGrammars())
		{
			hasError |= bean.getGrammar(n).acceptVisitor(this);
		}
		currentParser = null;
		return hasError;
	}

	@Override
	public Boolean visitExtendedParserBean(ExtendedParserBean bean)
	throws RuntimeException
	{
		return visitParserBean(bean);
	}

	@Override
	public Boolean visitProductionBean(ProductionBean bean)
	throws RuntimeException
	{
		out.append("      Production: " + bean.getName() + " (" + bean.getDisplayName() + "), signature " + getName(bean.getLhs()) + " ::=");
		for(int i = 0;i < bean.getRhs().size();i++)
		{
			out.append(" ");
			if(bean.getRhsVarNames().get(i) != null) out.append(bean.getRhsVarNames().get(i) + "::");
			out.append(getName(bean.getRhs().get(i)));
		}
		out.append("\n");
		if(bean.getOperator() != null) out.append("        Operator: " + getName(bean.getOperator())).append("\n");
		if(bean.getPrecedenceClass() != null) out.append("        Precedence class: " + getName(bean.getPrecedenceClass())).append("\n");
		if(bean.getPrecedence() != null) out.append("        Precedence: " + bean.getPrecedence()).append("\n");
		return false;
	}

	@Override
	public Boolean visitTerminalBean(TerminalBean bean)
			throws RuntimeException
	{
		out.append("      Terminal: " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		out.append("        Regex: ");
		bean.getRegex().acceptVisitor(this);
		out.append("\n");
		out.append("        Operator precedence class:");
		if(bean.getOperatorClass() == null) out.append(" NONE\n");
		else out.append(" " + getName(bean.getOperatorClass())).append("\n");
		out.append("        Operator precedence:");
		if(bean.getOperatorPrecedence() == null) out.append(" NONE\n");
		else out.append(" " + bean.getOperatorPrecedence()).append("\n");
		out.append("        Operator associativity:");
		if(bean.getOperatorAssociativity() == null) out.append(" NONE\n");
		else out.append(" " + bean.getOperatorAssociativity()).append("\n");
		out.append("        Transparent prefix:");
		if(bean.getPrefix() == null) out.append(" NONE\n");
		else out.append(" " + getName(bean.getPrefix())).append("\n");
		out.append("        Terminal classes:");
		for(CopperElementReference member : bean.getTerminalClasses())
		{
			out.append("\n          " + getName(member));
		}
		out.append("\n        Submit list:");
		for(CopperElementReference member : bean.getSubmitList())
		{
			out.append("\n          " + getName(member));
		}
		out.append("\n        Dominate list:");
		for(CopperElementReference member : bean.getDominateList())
		{
			out.append("\n          " + getName(member));
		}
		out.append("\n");
		return false;
	}

	@Override
	public Boolean visitTerminalClassBean(TerminalClassBean bean)
	throws RuntimeException
	{
		out.append("      Terminal class: " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		out.append("        Members:");
		for(CopperElementReference member : bean.getMembers())
		{
			out.append("\n          " + getName(member));
		}
		out.append("\n");
		return false;
	}

	@Override
	public Boolean visitOperatorClassBean(OperatorClassBean bean)
	throws RuntimeException
	{
		out.append("      Operator class: " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		return false;
	}
	
	@Override
	public Boolean visitChoiceRegex(ChoiceRegexBean bean)
	throws RuntimeException
	{
		boolean first = true;
		if(bean.getSubexps().size() > 1) out.append("(");
		for(RegexBean subexp : bean.getSubexps())
		{
			if(first)
			{
				first = false;
			}
			else out.append("|");
			subexp.acceptVisitor(this);
		}
		if(bean.getSubexps().size() > 1) out.append(")");
		return false;
	}

	@Override
	public Boolean visitConcatenationRegex(ConcatenationRegexBean bean)
	throws RuntimeException
	{
		for(RegexBean subexp : bean.getSubexps())
		{
			subexp.acceptVisitor(this);
		}
		return false;
	}

	@Override
	public Boolean visitKleeneStarRegex(KleeneStarRegexBean bean)
	throws RuntimeException
	{
		if(!(bean.getSubexp() instanceof CharacterSetRegexBean)) out.append("(");
		bean.getSubexp().acceptVisitor(this);
		if(!(bean.getSubexp() instanceof CharacterSetRegexBean)) out.append(")");
		out.append("*");
		return false;
	}

	@Override
	public Boolean visitEmptyStringRegex(EmptyStringRegexBean bean)
	throws RuntimeException
	{
		// Intentionally blank.
		return false;
	}

	@Override
	public Boolean visitCharacterSetRegex(CharacterSetRegexBean bean,SetOfCharsSyntax chars)
	throws RuntimeException
	{
		out.append("[" + chars.toString() + "]");
		return false;
	}

	@Override
	public Boolean visitMacroHoleRegex(MacroHoleRegexBean bean)
	throws RuntimeException
	{
		out.append("[:" + getName(bean.getMacroName()) + ":]");
		return false;
	}

	private String getName(CopperElementReference ref)
	{
		if(currentParser.isUnitary())
		{
			if(!ref.isFQ()) return ref.getName().toString();
			else return ref.getName().toString();
		}
		else
		{
			if(!ref.isFQ()) return currentGrammar.getDisplayName() + "$" + ref.getName();
			else return currentParser.getGrammar(ref.getGrammarName()).getDisplayName() + "$" + ref.getName();
		}
	}
}
