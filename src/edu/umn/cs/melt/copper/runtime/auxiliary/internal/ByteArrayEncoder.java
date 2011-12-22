package edu.umn.cs.melt.copper.runtime.auxiliary.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Compresses arrays of bytes and encodes them as Java string literals.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&rt;
 *
 */
public class ByteArrayEncoder
{
	public static final int MAX_STRING_CONST_LENGTH = 32768;
    public static String byteArrayToLiteral(int charsPerLine,byte[] array)
    {
    	try
    	{
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		GZIPOutputStream gos = new GZIPOutputStream(baos);
    		gos.write(array);
    		gos.close();
    		baos.close();
    		array = baos.toByteArray();
    	}
    	catch(IOException ex)
    	{
    		ex.printStackTrace(System.err);
    		return null;
    	}
    	return byteArrayToUncompressedLiteral(charsPerLine,array);
    }
    	
    public static String byteArrayToUncompressedLiteral(int charsPerLine,byte[] array)
    {
    	StringBuffer rv = new StringBuffer();
    	int stringNumber = 0;
    	for(int i = 0;i <= (array.length / charsPerLine);i++)
    	{
        	rv.append("\"");
    		for(int j = 0;j < charsPerLine && (charsPerLine*i)+j < array.length;j++)
    		{
    			rv.append(String.format("\\%03o",array[(charsPerLine*i)+j]));
    		}
    		rv.append("\"");
    		if(i < (array.length / charsPerLine))
    		{
    			if(((charsPerLine*(i+1)) / (MAX_STRING_CONST_LENGTH - charsPerLine)) != stringNumber)
    			{
    				stringNumber++;
    				rv.append(",");
    			}
    			else rv.append(" +");
    		}
    		rv.append("\n");
    	}
    	return rv.toString();
    }
    
    public static byte[] uncompressedLiteralToByteArray(String[] literal)
    {
    	int sigma_l = 0;
    	for(String l : literal) sigma_l += l.length();
    	byte[] rv = new byte[sigma_l];
    	int rv_index = 0;
    	for(int i = 0;i < literal.length;i++)
    	{
    		for(int j = 0;j < literal[i].length();j++)
    		{
    			rv[rv_index++] = (byte) literal[i].charAt(j);
    		}
    	}
    	return rv;
    }
    
    public static byte[] literalToByteArray(String[] literal)
    {
    	byte[] rv = uncompressedLiteralToByteArray(literal);
    	try
    	{
    		GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(rv));
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		int length = 0;
    		for(int i = 0;i < literal.length;i++) length += literal[i].length();
    		byte[] buf = new byte[length];
    		int bytesRead = -1;
    		while((bytesRead = gzis.read(buf)) != -1)
    		{
    			baos.write(buf,0,bytesRead);
    		}
    		gzis.close();
    		baos.close();
    		rv = baos.toByteArray();
    	}
    	catch(IOException ex)
    	{
    		ex.printStackTrace(System.err);
    		return null;
    	}
    	return rv;
    }
    
	public static Object readHash(byte[] hash)
	throws IOException,ClassNotFoundException
	{
		ByteArrayInputStream bain;
		ObjectInputStream oin;
		bain = new ByteArrayInputStream(hash);
		oin = new ObjectInputStream(bain);
		return oin.readObject();
	}

}
