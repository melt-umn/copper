package edu.umn.cs.melt.copper.compiletime.auxiliary;

import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.compiletime.parsetable.AcceptAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.FullReduceAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.ParseAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.ParseActionVisitor;
import edu.umn.cs.melt.copper.compiletime.parsetable.ShiftAction;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;

public class ParseActionPrettyPrinter implements ParseActionVisitor<String,RuntimeException>
{
	private GrammarSource grammar;
	
	public ParseActionPrettyPrinter(GrammarSource grammar)
	{
		this.grammar = grammar;
	}
	
	public String prettyPrintConflict(Iterable<ParseAction> conflict)
	{
		HashSet<String> prettyPrintedActions = new HashSet<String>();
		for(ParseAction a : conflict) prettyPrintedActions.add(a.acceptVisitor(this));
		return PrettyPrinter.iterablePrettyPrint(prettyPrintedActions, "    ", PrettyPrinter.getOptimalItemsPerLine(prettyPrintedActions,80));
	}
	
	@Override
	public String visitAcceptAction(AcceptAction action)
	throws RuntimeException
	{
		return action.toString();
	}

	@Override
	public String visitFullReduceAction(FullReduceAction action)
	throws RuntimeException
	{
		StringBuffer rv = new StringBuffer();
		Production p = action.getProd();
		rv.append("REDUCE(");
		rv.append(grammar.getDisplayName(p.getLeft().getId()));
		rv.append(" ::=");
		for(GrammarSymbol gs : p.getRight()) rv.append(" ").append(grammar.getDisplayName(gs.getId()));
		rv.append(")");
		return rv.toString();
	}

	@Override
	public String visitShiftAction(ShiftAction action)
	throws RuntimeException
	{
		return action.toString();
	}
	
}

