package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import java.util.Comparator;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;

class CopperASTBeanComparator implements Comparator<CopperASTBean>
{
	public static CopperASTBeanComparator C = new CopperASTBeanComparator();
	
	@Override
	public int compare(CopperASTBean o1, CopperASTBean o2)
	{
		int typeCompare = o1.getType().compareTo(o2.getType()); 
		if(typeCompare != 0) return typeCompare;
		switch(o1.getType())
		{
		case PRODUCTION:
			int lhsCompare = ((Production) o1).getLhs().getName().compareTo(((Production) o2).getLhs().getName());
			if(lhsCompare != 0) return lhsCompare;
		default:
			return o1.getName().compareTo(o2.getName());
		}
	}
}
