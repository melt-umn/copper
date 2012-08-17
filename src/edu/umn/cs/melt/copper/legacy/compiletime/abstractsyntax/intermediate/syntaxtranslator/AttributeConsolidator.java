package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateConsNode;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateNodeVisitor;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class AttributeConsolidator implements IntermediateNodeVisitor<GrammarName,GrammarName,CopperException>
{
	public CompilerLogger logger;
	public Hashtable<Symbol,IntermediateSymbolNode> consolidatedNodes;
	
	public AttributeConsolidator(CompilerLogger logger)
	{
		this.logger = logger;
		consolidatedNodes = new Hashtable<Symbol,IntermediateSymbolNode>();
	}

	public GrammarName visitIntermediateSymbolNode(IntermediateSymbolNode node, GrammarName inheritance)
	throws CopperException
	{
		if(!consolidatedNodes.containsKey(node.name))
		{
			// DEBUG-X-BEGIN
			//System.err.println("Node '" + node.name + "'; inheritance " + inheritance);
			// DEBUG-X-END
			consolidatedNodes.put(node.name,node);
			node.owner = inheritance;
			if(node.sort == IntermediateSymbolSort.GRAMMAR_NAME)
			{
				node.owner = new GrammarName(node.name);
				return node.owner;
			}
		}
		else
		{
			if(consolidatedNodes.get(node.name).sort != node.sort)
			{
				// DEBUG-X-BEGIN
				//System.err.println(node.name + " " + node.attributes.keySet());
				// DEBUG-X-END
				if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,node.attributes.get("location").first(),consolidatedNodes.get(node.name).sort + " '" + node.name + "' referenced here as a " + node.sort);
				return null;
			}
			HashSet<String> attributes = new HashSet<String>(node.attributes.keySet());
			attributes.retainAll(consolidatedNodes.get(node.name).attributes.keySet());
			attributes.remove("location");
			if(!attributes.isEmpty())
			{
				boolean isConflicting = false;
				for(String attr : attributes)
				{
					if(mergeAttr(node,attr) == null)
					{
						isConflicting = true;
						break;
					}
				}
				if(isConflicting)
				{
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,node.attributes.get("location").first(),"Conflicting definition involving " + node.sort + " '" + node.name + "': multiple specifications of attributes " + attributes);
					return null;
				}
			}
			for(String attr : node.attributes.keySet())
			{
				consolidatedNodes.get(node.name).attributes.put(attr,mergeAttr(node,attr));
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Pair<InputPosition,Object> mergeAttr(IntermediateSymbolNode node,String attr)
	{
		if(!consolidatedNodes.get(node.name).attributes.containsKey(attr) ||
		   consolidatedNodes.get(node.name).attributes.get(attr).second() == null ||
		   consolidatedNodes.get(node.name).attributes.get(attr).second().equals(node.attributes.get(attr).second()))
		{
			return node.attributes.get(attr);
		}
		if(node.attributes.get(attr).second() == null)
		{
			return consolidatedNodes.get(node.name).attributes.get(attr);			
		}
		Object newAttrObj = node.attributes.get(attr).second(); 
		Object oldAttrObj = consolidatedNodes.get(node.name).attributes.get(attr).second();
		if(!oldAttrObj.getClass().equals(newAttrObj.getClass()))
		{
			return null;
		}
		if(attr.equals("location")) return consolidatedNodes.get(node.name).attributes.get("location");
		else if(node.name.equals(Symbol.symbol(" startCode ")) && attr.equals("code"))
		{
			if(!(oldAttrObj instanceof String)) return null;
			else if(((String) newAttrObj).matches(".*package.*")) return Pair.cons(consolidatedNodes.get(node.name).attributes.get(attr).first(),(Object)((String) newAttrObj + (String) oldAttrObj));
			/*else if(((String) oldAttrObj).contains("package"))*/ return Pair.cons(consolidatedNodes.get(node.name).attributes.get(attr).first(),(Object)((String) oldAttrObj + (String) newAttrObj));
			
		}
		else if(attr.equals("classes"))
		{
			if(!(oldAttrObj instanceof LinkedList) || !(newAttrObj instanceof LinkedList)) return null;
			HashSet<String> newAttrs = new HashSet<String>();
			for(String id : (LinkedList<String>) oldAttrObj)
			{
				newAttrs.add(id);
			}
			for(String id : (LinkedList<String>) newAttrObj)
			{
				newAttrs.add(id);
			}
			LinkedList<String> newAttrList = new LinkedList<String>(newAttrs);
			return Pair.cons(consolidatedNodes.get(node.name).attributes.get(attr).first(),(Object) newAttrList);
		}
		else return null;
	}

	public GrammarName visitIntermediateConsNode(IntermediateConsNode node, GrammarName inheritance)
	throws CopperException
	{
		GrammarName carName = node.car.acceptVisitor(this,inheritance);
		GrammarName cdrName = node.cdr.acceptVisitor(this,(carName != null) ? carName : inheritance);
		if(inheritance != null && (carName != null || cdrName != null)) return inheritance; // Throw an error.
		else if(inheritance != null) return inheritance;
		else if(carName != null) return carName;
		else if(cdrName != null) return cdrName;
		return inheritance;
	}
	
	
}
