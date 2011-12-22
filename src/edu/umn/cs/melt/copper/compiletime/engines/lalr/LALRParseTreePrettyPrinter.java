package edu.umn.cs.melt.copper.compiletime.engines.lalr;

import java.io.PrintStream;

import edu.umn.cs.melt.copper.compiletime.parsetree.plain.ParseTree;
import edu.umn.cs.melt.copper.compiletime.parsetree.plain.ParseTreeNode;
import edu.umn.cs.melt.copper.compiletime.parsetree.plain.ParseTreeProdNode;
import edu.umn.cs.melt.copper.compiletime.parsetree.plain.ParseTreeTermNode;
import edu.umn.cs.melt.copper.compiletime.parsetree.plain.ParseTreeVisitor;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;




/**
 * "Pretty prints" a deterministic parse tree, with child nodes indented one space
 * ahead of their parents -- or, if the "non-pretty" mode is selected, a non-pretty-
 * printed format palatable to the Haskell read() function.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LALRParseTreePrettyPrinter implements ParseTreeVisitor<String,Object,CopperParserException>
{
	private int depthPad;
	private PrintStream out;
	private boolean isPretty;
	
	public static String run(boolean isPretty,ParseTree parseTree,PrintStream out)
	throws CopperParserException
	{
		return /*"Root headsymbol: " + parseTree.getRoot().getHeadSymbol() + "\n" +*/ parseTree.getRoot().acceptVisitor(new LALRParseTreePrettyPrinter(isPretty,out),null);
	}
	
	public LALRParseTreePrettyPrinter(boolean isPretty,PrintStream out)
	{
		depthPad = 0;
		this.isPretty = isPretty;
		this.out = out;
	}

	public String visitTermNode(ParseTreeTermNode node,Object inheritance)
	throws CopperParserException
	{
		if(isPretty)
		{
			for(int i = 0;i < depthPad;i++) out.print(' ');
			out.print("( ");
			out.print(node.getToken().getId());
			out.print(" \"");
			out.print(QuotedStringFormatter.formatOutputLexeme(node.getToken().getLexeme()));
			out.print("\" \"");
			out.print(QuotedStringFormatter.formatOutputLexeme(node.getVirtualLocation().toString()));
			out.print("\" )");
		}
		else
		{
			out.print("(Bridge_TERMINAL1 \"");
			out.print(QuotedStringFormatter.formatOutputLexeme(node.getToken().getLexeme()));
			out.print("\" ");
			out.print(QuotedStringFormatter.formatOutputLexeme(String.valueOf(node.getVirtualLocation().getLine())));
			out.print(" ");
			out.print(QuotedStringFormatter.formatOutputLexeme(String.valueOf(node.getVirtualLocation().getColumn())));
			// Had to break virtual-location compatibility to match Silver form; fix the latter and restore this.
			//out.print("\" \"");
			//out.print(QuotedStringFormatter.formatOutputLexeme(node.getVirtualLocation()));
			//out.print("\")");
			out.print(")");
		}
		return null;
	}

	public String visitProdNode(ParseTreeProdNode node,Object inheritance)
	throws CopperParserException
	{
		if(isPretty)
		{
			for(int i = 0;i < depthPad;i++) out.print(' ');
			out.print("( ");
			out.print(node.getProd().getName());
			out.print("\n");
			ParseTreeNode[] children = node.getChildren();
			depthPad++;
			for(int i = 0; i < children.length;i++)
			{
				children[i].acceptVisitor(this,null);
			    out.print('\n');
			}
			depthPad--;
			for(int i = 0;i < depthPad;i++) out.print(' ');
			out.print(")");
		}
		else
		{
			out.print("(");
			out.print(node.getProd().getName());
			ParseTreeNode[] children = node.getChildren();
			if(children.length > 0) out.print(' ');
			for(int i = 0; i < children.length;i++)
			{
				children[i].acceptVisitor(this,null);
			    if(i < children.length - 1) out.print(' ');
			}
			out.print(")");
		}
		return null;
	}
}
