package edu.umn.cs.melt.copper.runtime.auxiliary.internal;

import java.util.BitSet;

/**
 * Various utility functions on BitSet that cannot be expressed in a single expression.
 * @author Lucas Kramer &lt;<a href="mailto:krame505@umn.edu">krame505@umn.edu</a>&gt;
 *
 */
public class BitSetUtils {
	public static boolean subset(final BitSet s1, final BitSet s2) {
		BitSet s1Copy = (BitSet)s1.clone();
		s1Copy.and(s2);
		return s1Copy.equals(s1);
	}
}
