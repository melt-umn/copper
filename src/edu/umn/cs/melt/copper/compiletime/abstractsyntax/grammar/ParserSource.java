package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;

public class ParserSource
{
	private String semanticActionAuxCode;
	private String parserAttrInitCode;
	private String parserClassAuxCode;
	private String classFilePreambleCode;
	private String parserName;
	private String packageName;
	private String postParseCode;
	
	public ParserSource()
	{
		semanticActionAuxCode = "";
		parserAttrInitCode = "";
		parserClassAuxCode = "";
		classFilePreambleCode = "";
		postParseCode = "";
		parserName = "";
		packageName = "";
	}

	public String getClassFilePreambleCode()
	{
		return classFilePreambleCode;
	}
	
	public String getParserAttrInitCode()
	{
		return parserAttrInitCode;
	}

	public String getParserClassAuxCode()
	{
		return parserClassAuxCode;
	}

	public String getSemanticActionAuxCode()
	{
		return semanticActionAuxCode;
	}

	public String getPostParseCode()
	{
		return postParseCode;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public String getParserName()
	{
		return parserName;
	}

	public void setClassFilePreambleCode(String classFilePreambleCode)
	{
		this.classFilePreambleCode = classFilePreambleCode;
	}
	
	public void addClassFilePreambleCode(String classFilePreambleCode)
	{
		setClassFilePreambleCode(getClassFilePreambleCode() + classFilePreambleCode);
	}

	public void setParserAttrInitCode(String parserAttrInitCode)
	{
		this.parserAttrInitCode = parserAttrInitCode;
	}
	
	public void addParserAttrInitCode(String parserAttrInitCode)
	{
		setParserAttrInitCode(getParserAttrInitCode() + parserAttrInitCode);
	}

	public void setParserClassAuxCode(String parserClassAuxCode)
	{
		this.parserClassAuxCode = parserClassAuxCode;
	}
	
	public void addParserClassAuxCode(String parserClassAuxCode)
	{
		setParserClassAuxCode(getParserClassAuxCode() + parserClassAuxCode);
	}

	public void setSemanticActionAuxCode(String semanticActionAuxCode)
	{
		this.semanticActionAuxCode = semanticActionAuxCode;
	}
	
	public void addSemanticActionAuxCode(String semanticActionAuxCode)
	{
		setSemanticActionAuxCode(getSemanticActionAuxCode() + semanticActionAuxCode);
	}

	public void setPostParseCode(String postParseCode)
	{
		this.postParseCode = postParseCode;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public void setParserName(String parserName)
	{
		this.parserName = parserName;
	}
	
	
}
