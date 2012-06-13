package edu.umn.cs.melt.copper.compiletime.auxiliary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A functional analogue of the JCL class SetOfIntegerSyntax for Unicode characters,
 * also adding union, intersection, and subtract operations.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class SetOfCharsSyntax
{
	private String stringRep = null;
	private int size = -1;
	// TODO: This is used to sort ranges when constructing a SetOfCharsSyntax
	//       object. It could do with some optimization.
	private class CharRange implements Comparable<CharRange>
	{
		public char min,max;
		
		public CharRange(char min,char max)
		{
			this.min = min;
			this.max = max;
		}
		
		public int compareTo(CharRange r)
		{
			if(min < r.min) return -1;
			else if(min > r.min) return 1;
			else /* implicit: if(min == r.min) */
			{
				if(max < r.max) return -1;
				else if(max == r.max) return 0;
				else /* implicit: if(max > r.max) */ return 1;
			}
		}
		
		public boolean equals(Object rhs)
		{
			return rhs instanceof CharRange && (min == ((CharRange) rhs).min && max == ((CharRange) rhs).max);
		}
		
		public String toString()
		{
			return min + "-" + max;
		}
	}
		
	private char[][] canonicalRanges;
	
	private SetOfCharsSyntax(char[][] canonicalRanges,boolean isSorted)
	{
		if(canonicalRanges.length == 0 || isSorted)
		{
			this.canonicalRanges = canonicalRanges;
			return;
		}
		if(canonicalRanges[0].length != 2) throw new ArrayIndexOutOfBoundsException("Canonical range array's second dimension must be 2");
		
		TreeSet<CharRange> ranges = new TreeSet<CharRange>();
		for(int i = 0;i < canonicalRanges.length;i++)
		{
			if(canonicalRanges[i][0] <= canonicalRanges[i][1]) ranges.add(new CharRange(canonicalRanges[i][0],canonicalRanges[i][1]));
		}
		// DEBUG-X-BEGIN
		// System.err.println(ranges);
		// DEBUG-X-END
		ArrayList<CharRange> newRanges = new ArrayList<CharRange>(ranges.size());
		Iterator<CharRange> it = ranges.iterator();
		CharRange first = it.next();
		CharRange last = first;
		for(;it.hasNext();)
		{
			CharRange current = it.next();
			if(current.min - 1 > last.max)
			{
				newRanges.add(new CharRange(first.min,last.max));
				first = current;
			}
			if(current.max > last.max) last = current;
		}
		newRanges.add(new CharRange(first.min,last.max));
		// DEBUG-X-BEGIN
		// System.err.println(newRanges);
		// DEBUG-X-END
		
		this.canonicalRanges = new char[newRanges.size()][2];
		for(int i = 0;i < newRanges.size();i++)
		{
			this.canonicalRanges[i][0] = newRanges.get(i).min;
			this.canonicalRanges[i][1] = newRanges.get(i).max;
		}
	}
	
	public SetOfCharsSyntax(char[][] canonicalRanges)
	{
		this(canonicalRanges,false);
	}
	
	public SetOfCharsSyntax(char lowerBound,char upperBound)
	{
		if(lowerBound > upperBound)
		{
			this.canonicalRanges = new char[0][2];
		}
		else
		{
			this.canonicalRanges = new char[1][2];
			canonicalRanges[0][0] = lowerBound;
			canonicalRanges[0][1] = upperBound;
		}
	}
	
	public SetOfCharsSyntax()
	{
		this.canonicalRanges = new char[0][2];
	}
	
	private static String charToString(char c)
	{
		//return String.valueOf(c);
		return "U+" + Integer.toHexString(c);
	}
	
	public String toString()
	{
		if(stringRep == null)
		{
			stringRep = "";
			if(isEmpty()) stringRep += "EMPTY";
			for(int i = 0;i < canonicalRanges.length;i++)
			{
				if(canonicalRanges[i][0] == canonicalRanges[i][1]) stringRep += charToString(canonicalRanges[i][0]);
				else stringRep += charToString(canonicalRanges[i][0]) + "-" + charToString(canonicalRanges[i][1]);
				if(i < canonicalRanges.length - 1) stringRep += ","; 
			}
		}
		return stringRep;
	}
	
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	public boolean equals(Object o)
	{
		return (o instanceof SetOfCharsSyntax) && (toString().equals(((SetOfCharsSyntax) o).toString()));
	}

	public char[][] getMembers()
	{
		return canonicalRanges;
	}
	
	public boolean isEmpty()
	{
		return (canonicalRanges.length == 0);
	}
	
	public int size()
	{
		if(size != -1) return size;
		size = 0;
		for(int i = 0;i < canonicalRanges.length;i++) size += (canonicalRanges[i][1] - canonicalRanges[i][0] + 1);
		return size;
	}
	

	
	
	
	
	
	
	
	
	
	private class Masks
	{
		public static final byte LEFT_TURNS_ON  = 0x01;
		public static final byte LEFT_TURNS_OFF = 0x02;
		public static final byte RIGHT_TURNS_ON  = 0x04;
		public static final byte RIGHT_TURNS_OFF = 0x08;
	}
	
	private enum SetOperation
	{
		UNION
		{
			public boolean onLogic(boolean leftOn,boolean rightOn,boolean leftTurnsOn,boolean rightTurnsOn)
			{
				return (!leftOn && rightTurnsOn) ||
				       (!rightOn && leftTurnsOn);
			}
			public boolean offLogic(boolean leftOn,boolean rightOn,boolean leftTurnsOff,boolean rightTurnsOff)
			{
				return (!leftOn && rightTurnsOff) ||
				       (!rightOn && leftTurnsOff) ||
				       (leftTurnsOff && rightTurnsOff);
			}
		},
		INTERSECT
		{
			public boolean onLogic(boolean leftOn,boolean rightOn,boolean leftTurnsOn,boolean rightTurnsOn)
			{
				return (leftTurnsOn || leftOn) &&
				       (rightTurnsOn || rightOn) &&
				       (!leftOn || !rightOn);
			}
			public boolean offLogic(boolean leftOn,boolean rightOn,boolean leftTurnsOff,boolean rightTurnsOff)
			{
				return leftOn && rightOn && (leftTurnsOff || rightTurnsOff);
			}
		};
		
		public abstract boolean onLogic(boolean leftOn,boolean rightOn,boolean leftTurnsOn,boolean rightTurnsOn);
		public abstract boolean offLogic(boolean leftOn,boolean rightOn,boolean leftTurnsOff,boolean rightTurnsOff);
	}

	private static TreeMap<Character,Byte> mapExtrema(SetOfCharsSyntax left,SetOfCharsSyntax right)
	{
		TreeMap<Character,Byte> extrema = new TreeMap<Character,Byte>();
		for(int i = 0;i < left.canonicalRanges.length;i++)
		{
			if(!extrema.containsKey(left.canonicalRanges[i][0])) extrema.put(left.canonicalRanges[i][0],Masks.LEFT_TURNS_ON);
			else extrema.put(left.canonicalRanges[i][0],(byte)(extrema.get(left.canonicalRanges[i][0]) | Masks.LEFT_TURNS_ON));
			if(!extrema.containsKey(left.canonicalRanges[i][1])) extrema.put(left.canonicalRanges[i][1],Masks.LEFT_TURNS_OFF);
			else extrema.put(left.canonicalRanges[i][1],(byte)(extrema.get(left.canonicalRanges[i][1]) | Masks.LEFT_TURNS_OFF));
		}
		for(int i = 0;i < right.canonicalRanges.length;i++)
		{
			if(!extrema.containsKey(right.canonicalRanges[i][0])) extrema.put(right.canonicalRanges[i][0],Masks.RIGHT_TURNS_ON);
			else extrema.put(right.canonicalRanges[i][0],(byte)(extrema.get(right.canonicalRanges[i][0]) | Masks.RIGHT_TURNS_ON));
			if(!extrema.containsKey(right.canonicalRanges[i][1])) extrema.put(right.canonicalRanges[i][1],Masks.RIGHT_TURNS_OFF);
			else extrema.put(right.canonicalRanges[i][1],(byte)(extrema.get(right.canonicalRanges[i][1]) | Masks.RIGHT_TURNS_OFF));
		}
		// DEBUG-X-BEGIN
		// System.err.println(extrema);
		// DEBUG-X-END
		return extrema;
	}
	
	private static TreeMap<Character,Byte> mapExtremaSubtract(SetOfCharsSyntax left,SetOfCharsSyntax right)
	{
		TreeMap<Character,Byte> extrema = new TreeMap<Character,Byte>();
		for(int i = 0;i < left.canonicalRanges.length;i++)
		{
			if(!extrema.containsKey(left.canonicalRanges[i][0])) extrema.put(left.canonicalRanges[i][0],Masks.LEFT_TURNS_ON);
			else extrema.put(left.canonicalRanges[i][0],(byte)(extrema.get(left.canonicalRanges[i][0]) | Masks.LEFT_TURNS_ON));
			if(!extrema.containsKey(left.canonicalRanges[i][1])) extrema.put(left.canonicalRanges[i][1],Masks.LEFT_TURNS_OFF);
			else extrema.put(left.canonicalRanges[i][1],(byte)(extrema.get(left.canonicalRanges[i][1]) | Masks.LEFT_TURNS_OFF));
		}
		boolean rightHasMin = false;
		boolean rightHasMax = false;
		for(int i = 0;i < right.canonicalRanges.length;i++)
		{
			if(right.canonicalRanges[i][0] != Character.MIN_VALUE)
			{
				if(!extrema.containsKey((char)(right.canonicalRanges[i][0] - 1))) extrema.put((char)(right.canonicalRanges[i][0] - 1),Masks.RIGHT_TURNS_OFF);
				else extrema.put((char)(right.canonicalRanges[i][0] - 1),(byte)(extrema.get((char)(right.canonicalRanges[i][0] - 1)) | Masks.RIGHT_TURNS_OFF));
			}
			else rightHasMin = true;
			
			if(right.canonicalRanges[i][1] != Character.MAX_VALUE)
			{
				if(!extrema.containsKey((char)(right.canonicalRanges[i][1] + 1))) extrema.put((char)(right.canonicalRanges[i][1] + 1),Masks.RIGHT_TURNS_ON);
				else extrema.put((char)(right.canonicalRanges[i][1] + 1),(byte)(extrema.get((char)(right.canonicalRanges[i][1] + 1)) | Masks.RIGHT_TURNS_ON));
			}
			else rightHasMax = true;
		}
		if(!rightHasMin)
		{
			if(!extrema.containsKey(Character.MIN_VALUE)) extrema.put(Character.MIN_VALUE,Masks.RIGHT_TURNS_ON);
			else extrema.put(Character.MIN_VALUE,(byte)(extrema.get(Character.MIN_VALUE) | Masks.RIGHT_TURNS_ON));
		}
		if(!rightHasMax)
		{
			if(!extrema.containsKey(Character.MAX_VALUE)) extrema.put(Character.MAX_VALUE,Masks.RIGHT_TURNS_OFF);
			else extrema.put(Character.MAX_VALUE,(byte)(extrema.get(Character.MAX_VALUE) | Masks.RIGHT_TURNS_OFF));
		}
		// DEBUG-X-BEGIN
		//System.err.print("[");
		//for(char extremum : extrema.keySet())
		//{
		//	System.err.printf(" U+%x=%s",(int) extremum,Integer.toBinaryString(extrema.get(extremum))); 
		//}
		//System.err.println(" ]");
		// DEBUG-X-END
		return extrema;
	}
	
	private static char[][] clipRanges(char[][] ranges,int i)
	{
		if(i < ranges.length)
		{
			char[][] newRanges = new char[i][2];
			System.arraycopy(ranges,0,newRanges,0,i);
			return newRanges;
		}
		else return ranges;
	}
	
	
	private static SetOfCharsSyntax operate(SetOperation operation,TreeMap<Character,Byte> extrema)
	{
		boolean leftOn = false,leftTurnsOn = false,leftTurnsOff = false;
		boolean rightOn = false,rightTurnsOn = false,rightTurnsOff = false;
		char[][] newCanonicalRanges = new char[extrema.size()][2];
		int i = 0;
		for(char extremum : extrema.keySet())
		{
			leftTurnsOff = ((extrema.get(extremum) & Masks.LEFT_TURNS_OFF) != 0);
			leftTurnsOn = ((extrema.get(extremum) & Masks.LEFT_TURNS_ON) != 0);
			rightTurnsOff = ((extrema.get(extremum) & Masks.RIGHT_TURNS_OFF) != 0);
			rightTurnsOn = ((extrema.get(extremum) & Masks.RIGHT_TURNS_ON) != 0);
						
			if(operation.onLogic(leftOn,rightOn,leftTurnsOn,rightTurnsOn))
			{
				if(i > 0 && (extremum - newCanonicalRanges[i-1][1]) <= 1) i--;
				else newCanonicalRanges[i][0] = extremum;
			}

			// DEBUG-X-BEGIN
			//System.err.print(extremum + " " + leftOn + " " + rightOn);
			// DEBUG-X-END

			leftOn = leftOn || leftTurnsOn;
			rightOn = rightOn || rightTurnsOn;

			
			if(operation.offLogic(leftOn,rightOn,leftTurnsOff,rightTurnsOff))
			{
				newCanonicalRanges[i][1] = extremum;
				i++;
			}
			
			leftOn = leftOn && !leftTurnsOff;
			rightOn = rightOn && !rightTurnsOff;
			
			// DEBUG-X-BEGIN
			//System.err.println(" " + leftOn + " " + rightOn);
			// DEBUG-X-END
		}

		// DEBUG-X-BEGIN
		// for(int j = 0;j < i;j++) System.err.println(newCanonicalRanges[j][0] + " " + newCanonicalRanges[j][1]);
		// System.err.println(i + " " + newCanonicalRanges.length);
		// DEBUG-X-END

		return new SetOfCharsSyntax(clipRanges(newCanonicalRanges,i),true);
	}
		
	public static SetOfCharsSyntax union(SetOfCharsSyntax augend,SetOfCharsSyntax addend)
	{
		return operate(SetOperation.UNION,mapExtrema(augend,addend));
	}
	
	public static SetOfCharsSyntax intersect(SetOfCharsSyntax lfactor,SetOfCharsSyntax rfactor)
	{
		return operate(SetOperation.INTERSECT,mapExtrema(lfactor,rfactor));
	}
	
	public static SetOfCharsSyntax subtract(SetOfCharsSyntax minuend,SetOfCharsSyntax subtrahend)
	{
		return operate(SetOperation.INTERSECT,mapExtremaSubtract(minuend,subtrahend));
	}
	
	public SetOfCharsSyntax invert(SetOfCharsSyntax universal)
	{
		return subtract(universal,this);
	}

	public SetOfCharsSyntax invert()
	{
		return invert(new SetOfCharsSyntax(Character.MIN_VALUE,Character.MAX_VALUE));
	}

	/*public static void main(String[] args)
	{
		char[][] canonicalRanges = new char[args.length / 2][2];
		
		for(int i = 0;i < args.length / 2;i++)
		{
			canonicalRanges[i][0] = (char)Integer.parseInt(args[2*i]);
			canonicalRanges[i][1] = (char)Integer.parseInt(args[2*i+1]);
		}
		
		char[][] initCanonicalRanges = new char[args.length / 4][2];
		System.arraycopy(canonicalRanges,0,initCanonicalRanges,0,args.length / 4);
		SetOfCharsSyntax left = new SetOfCharsSyntax(initCanonicalRanges);
		System.arraycopy(canonicalRanges,args.length / 4,initCanonicalRanges,0,args.length / 4);
		SetOfCharsSyntax right = new SetOfCharsSyntax(initCanonicalRanges);
		
		System.out.println("========================");
		System.out.println("Left:\t\t" + left);
		System.out.println("Right:\t\t" + right);
		System.out.println();
		System.out.println("Union:\t\t" + union(left,right));
		System.out.println("Intersection:\t" + intersect(left,right));
		System.out.println("Difference:\t" + subtract(left,right));
		System.out.println("Inversion of left:\t" + left.invert());
		System.out.println("Inversion of right:\t" + right.invert());
		System.out.println("Tiling: " + GeneralizedNFA.tileCharSets(left,right));
	}*/
}
