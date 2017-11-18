package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CharacterSetRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ChoiceRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ConcatenationRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.DisambiguationFunction;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.EmptyStringRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtensionGrammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.KleeneStarRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.MacroHoleRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.TerminalClass;

/**
 * Prints out a parser spec in plaintext.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ParserSpecPlaintextPrinter implements CopperASTBeanVisitor<Boolean,RuntimeException>,RegexBeanVisitor<Boolean,RuntimeException>
{
	private ParserBean currentParser;
	private Grammar currentGrammar;
	private StringBuffer out;
	
	public static String specToString(ParserBean spec)
	{
		ParserSpecPlaintextPrinter printer = new ParserSpecPlaintextPrinter();
		spec.acceptVisitor(printer);
		return printer.out.toString();
	}
	
	private ParserSpecPlaintextPrinter()
	{
		this.out = new StringBuffer();
		currentParser = null;
		currentGrammar = null;
	}

	@Override
	public Boolean visitDisambiguationFunction(DisambiguationFunction bean)
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
	public Boolean visitGrammar(Grammar bean)
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
	public Boolean visitExtensionGrammar(ExtensionGrammar bean)
	throws RuntimeException
	{
		currentGrammar = bean;
		out.append("  Extension grammar " + bean.getName() + "(" + bean.getDisplayName() + ")\n");
		boolean hasError = false;
		out.append("    Marking terminals:\n");
		for(CopperElementName n : bean.getMarkingTerminals())
		{
			hasError |= bean.getMarkingTerminal(n).acceptVisitor(this);
		}
		out.append("\n    Bridge productions:\n");
		for(CopperElementName n : bean.getBridgeProductions())
		{
			hasError |= bean.getBridgeProduction(n).acceptVisitor(this);
		}
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
	public Boolean visitNonTerminal(NonTerminal bean)
	throws RuntimeException
	{
		out.append("      Nonterminal " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		return false;
	}

	@Override
	public Boolean visitParserAttribute(ParserAttribute bean)
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
	public Boolean visitProduction(Production bean)
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
	public Boolean visitTerminal(Terminal bean)
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
	public Boolean visitTerminalClass(TerminalClass bean)
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
	public Boolean visitOperatorClass(OperatorClass bean)
	throws RuntimeException
	{
		out.append("      Operator class: " + bean.getName() + " (" + bean.getDisplayName() + ")\n");
		return false;
	}
	
	@Override
	public Boolean visitChoiceRegex(ChoiceRegex bean)
	throws RuntimeException
	{
		boolean first = true;
		if(bean.getSubexps().size() > 1) out.append("(");
		for(Regex subexp : bean.getSubexps())
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
	public Boolean visitConcatenationRegex(ConcatenationRegex bean)
	throws RuntimeException
	{
		for(Regex subexp : bean.getSubexps())
		{
			subexp.acceptVisitor(this);
		}
		return false;
	}

	@Override
	public Boolean visitKleeneStarRegex(KleeneStarRegex bean)
	throws RuntimeException
	{
		if(!(bean.getSubexp() instanceof CharacterSetRegex)) out.append("(");
		bean.getSubexp().acceptVisitor(this);
		if(!(bean.getSubexp() instanceof CharacterSetRegex)) out.append(")");
		out.append("*");
		return false;
	}

	@Override
	public Boolean visitEmptyStringRegex(EmptyStringRegex bean)
	throws RuntimeException
	{
		// Intentionally blank.
		return false;
	}

	@Override
	public Boolean visitCharacterSetRegex(CharacterSetRegex bean,SetOfCharsSyntax chars)
	throws RuntimeException
	{
		out.append("[" + chars.toString() + "]");
		return false;
	}

	@Override
	public Boolean visitMacroHoleRegex(MacroHoleRegex bean)
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
