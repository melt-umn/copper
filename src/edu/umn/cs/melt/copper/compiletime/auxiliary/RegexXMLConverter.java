package edu.umn.cs.melt.copper.compiletime.auxiliary;

import java.util.Stack;

import org.xml.sax.SAXException;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.PrintCompilerLogHandler;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.skins.xml.ParserSpecXMLPrinter;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CharacterSetRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ChoiceRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ConcatenationRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.EmptyStringRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.KleeneStarRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.RegexSimplifier;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class RegexXMLConverter
{
	public static void main(String[] args)
	throws CopperException
	{
		CompilerLogger logger = new CompilerLogger(new PrintCompilerLogHandler(System.err));
		RegexXMLConverter parser = new RegexXMLConverter(logger);
		parser.setToParse(args[0]);
		Regex regex = parser.parse();

		RegexSimplifier simplifier = new RegexSimplifier();
		regex = regex.acceptVisitor(simplifier);

		ParserSpecXMLPrinter printer = new ParserSpecXMLPrinter(System.out,"    ");
		try
		{
			regex.acceptVisitor(printer);
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
	}
	
	enum RegexTerminals
	{
		STAR,
		PLUS,
		COLON,
		QUESTION,
		ENDOFSTRING,
		DASH,
		LPAREN,
		RPAREN,
		NOT,
		BAR,
		LBRACK,
		RBRACK,
		WILDCARD,
		CHARACTER
	}
	
	enum RegexNonTerminals
	{
		R,
		DR,
		RG,
		UG,
		CHAR,
		G,
		RR,
		UR
	}
	
	private String toParse;
	private int position;
	private Stack< Pair<Integer,Pair<String,Regex> > > parseStack; 
	private CompilerLogger logger;
	
	public RegexXMLConverter(CompilerLogger logger)
	{
		this.position = 0;
		this.logger = logger;
	}

	public String getToParse()
	{
		return toParse;
	}

	public void setToParse(String toParse)
	{
		this.toParse = toParse;
	}

	public Pair< Pair<RegexTerminals,String>,Integer > scan(int position,String context)
	throws CopperException
	{
		RegexTerminals match = null;
		String lexeme = null;
		int newPosition = position;
		
		if(position >= toParse.length())
		{
			match = RegexTerminals.ENDOFSTRING;
			lexeme = "";
		}
		else switch(toParse.charAt(position))
		{
		case '*':
			match = (context.indexOf('*') != -1) ? RegexTerminals.STAR : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '+':
			match = (context.indexOf('+') != -1) ? RegexTerminals.PLUS : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case ':':
			match = (context.indexOf(':') != -1) ? RegexTerminals.COLON : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '?':
			match = (context.indexOf('?') != -1) ? RegexTerminals.QUESTION : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '-':
			match = (context.indexOf('-') != -1) ? RegexTerminals.DASH : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '(':
			match = (context.indexOf('(') != -1) ? RegexTerminals.LPAREN : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case ')':
			match = (context.indexOf(')') != -1) ? RegexTerminals.RPAREN : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '^':
			match = (context.indexOf('^') != -1) ? RegexTerminals.NOT : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '|':
			match = (context.indexOf('|') != -1) ? RegexTerminals.BAR : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '[':
			match = (context.indexOf('[') != -1) ? RegexTerminals.LBRACK : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case ']':
			match = (context.indexOf(']') != -1) ? RegexTerminals.RBRACK : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '.':
			match = (context.indexOf('.') != -1) ? RegexTerminals.WILDCARD : RegexTerminals.CHARACTER;
			newPosition++;
			break;
		case '\\':
			match = RegexTerminals.CHARACTER;
			newPosition++;
			if(newPosition >= toParse.length())
			{
				logger.logError(new GenericMessage(CompilerLevel.QUIET,"Syntax error in regex '" + "Regex '" + toParse + "'",true,true));
			}
			newPosition++;
		    char escapedChar = QuotedStringFormatter.getRepresentedCharacter(toParse.substring(position,newPosition));
		    lexeme = String.valueOf(escapedChar);
		    break;
		default:
			match = RegexTerminals.CHARACTER;
			newPosition++;
			break;		
		}
		if(lexeme == null) lexeme = toParse.substring(position,newPosition);
		return Pair.cons(Pair.cons(match,lexeme),newPosition);
	}
	
	public static String[] contexts = { /* 0 */ "(.[",
										/* 1 */ "*+?(|.)[",
										/* 2 */ "|)",
										/* 3 */ "^",
										/* 4 */ "*+?(|.)[",
										/* 5 */ "(.[",
										/* 6 */ "*+?(|.([",
										/* 7 */ "",
										/* 8 */ "*+?-(|].)[",
										/* 9 */ ")",
										/* 10 */ "]",
										/* 11 */ "]",
										/* 12 */ "-]",
										/* 13 */ "",
										/* 14 */ "(.[",
										/* 15 */ "|)",
										/* 16 */ "(|.)[",
										/* 17 */ "|)",
										/* 18 */ "(|.)[",
										/* 19 */ "(|.)[",
										/* 20 */ "*+?(|.)[",
										/* 21 */ "",
										/* 22 */ "|)",
										/* 23 */ "*+?(|.)[",
										/* 24 */ ")",
										/* 25 */ "|)",
										/* 26 */ "]",
										/* 27 */ "]",
										/* 28 */ "]",
										/* 29 */ "|)",
										/* 30 */ "]",
										/* 31 */ "*+?(|.)[" };
	
	public void consume(Pair< Pair<RegexTerminals,String>,Integer > token)
	{
		position = token.second();
	}
	
	public void shift(int state,Pair< Pair<RegexTerminals,String>,Integer > token)
	{
		consume(token);
		parseStack.push(Pair.cons(state,Pair.cons(token.first().second(),(Regex) null)));
	}
	
	public void goTo(int state,Pair<RegexNonTerminals,Regex> toPush)
	{
		parseStack.push(Pair.cons(state,Pair.cons((String) null,toPush.second())));
	}
	
	private Regex getWildcardRegex()
	{
		return new CharacterSetRegex().addLooseChar('\n').invert();
	}

	private Regex getCharactersRegex(String lexeme)
	{
		if(lexeme.length() == 1)
		{
			return new CharacterSetRegex().addLooseChar(lexeme.charAt(0));
		}
		else
		{
			ConcatenationRegex rv = new ConcatenationRegex();
			for(int i = 0;i < lexeme.length();i++) rv.addSubexp(new CharacterSetRegex().addLooseChar(lexeme.charAt(i)));
			return rv;
		}
	}
	
	private void syntaxError()
	throws CopperException
	{
		logger.logError(new GenericMessage(CompilerLevel.QUIET,"Syntax error parsing regex '" + toParse + "'",true,true));
	}
	
	public Regex parse()
	throws CopperException
	{
		boolean finished = false;
		Regex pr;
		Pair<RegexNonTerminals,Regex> toGoto;
		Pair< Pair<RegexTerminals,String>,Integer > token;
		parseStack = new Stack< Pair<Integer,Pair<String,Regex> > >();
		parseStack.push(Pair.cons(0,Pair.cons("",(Regex) null)));
		position = 0;
		
		if(toParse.length() == 0) return new EmptyStringRegex();
		
		while(!finished)
		{
			toGoto = null;
			token = scan(position,contexts[parseStack.peek().first()]);
			// DEBUG-X-BEGIN
			//System.err.println(position + " " + parseStack + " " + token.first().first());
			// DEBUG-X-END
			switch(parseStack.peek().first())
			{
			case 0:
				switch(token.first().first())
				{
				case LPAREN:
					shift(5,token);
					break;
				case WILDCARD:
					shift(4,token);
					break;
				case LBRACK:
					shift(3,token);
					break;
				case CHARACTER:
					shift(8,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 1:
				switch(token.first().first())
				{
				case STAR:
					shift(19,token);
					break;
				case PLUS:
					shift(18,token);
					break;
				case QUESTION:
					shift(16,token);
					break;
				case LPAREN:
					shift(5,token);
					break;
				case WILDCARD:
					shift(4,token);
					break;
				case LBRACK:
					shift(3,token);
					break;
				case CHARACTER:
					shift(8,token);
					break;
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					toGoto = Pair.cons(RegexNonTerminals.RR,(Regex) new EmptyStringRegex());
					break;
				default:
					syntaxError();
				}
				break;
			case 2:
				switch(token.first().first())
				{
				case ENDOFSTRING:
				case RPAREN:
					pr = parseStack.pop().second().second();
					toGoto = Pair.cons(RegexNonTerminals.R,pr);
					break;
				case BAR:
					shift(14,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 3:
				switch(token.first().first())
				{
				case NOT:
					shift(13,token);
					break;
				case CHARACTER:
					shift(8,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 4:
				switch(token.first().first())
				{
				case STAR:
				case PLUS:
				case QUESTION:
				case ENDOFSTRING:
				case LPAREN:
				case BAR:
				case WILDCARD:
				case RPAREN:
				case LBRACK:
				case CHARACTER:
					parseStack.pop();
					toGoto = Pair.cons(RegexNonTerminals.UR,getWildcardRegex());
					break;
				default:
					syntaxError();
				}
				break;
			case 5:
				switch(token.first().first())
				{
				case LPAREN:
					shift(5,token);
					break;
				case WILDCARD:
					shift(4,token);
					break;
				case LBRACK:
					shift(3,token);
					break;
				case CHARACTER:
					shift(8,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 6:
				switch(token.first().first())
				{
				case STAR:
				case PLUS:
				case QUESTION:
				case ENDOFSTRING:
				case LPAREN:
				case BAR:
				case WILDCARD:
				case RPAREN:
				case LBRACK:
				case CHARACTER:
					toGoto = Pair.cons(RegexNonTerminals.UR,parseStack.pop().second().second());
					break;
				default:
					syntaxError();
				}
				break;
			case 7:
				switch(token.first().first())
				{
				case ENDOFSTRING:
					return parseStack.peek().second().second();
				default:
					syntaxError();
				}
				break;
			case 8:
				switch(token.first().first())
				{
				case STAR:
				case PLUS:
				case QUESTION:
				case ENDOFSTRING:
				case DASH:
				case LPAREN:
				case BAR:
				case RBRACK:
				case WILDCARD:
				case RPAREN:
				case LBRACK:
				case CHARACTER:
					toGoto = Pair.cons(RegexNonTerminals.CHAR,getCharactersRegex(parseStack.pop().second().first()));
					break;
				default:
					syntaxError();
				}
				break;
			case 9:
				switch(token.first().first())
				{
				case RPAREN:
					shift(23,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 10:
				switch(token.first().first())
				{
				case RBRACK:
					toGoto = Pair.cons(RegexNonTerminals.RG,(Regex) new EmptyStringRegex());
					break;
				case CHARACTER:
					shift(8,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 11:
				switch(token.first().first())
				{
				case RBRACK:
					shift(20,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 12:
				switch(token.first().first())
				{
				case DASH:
					shift(21,token);
					break;
				case RBRACK:
				case CHARACTER:
					toGoto = Pair.cons(RegexNonTerminals.UG,parseStack.pop().second().second());
					break;
				default:
					syntaxError();
				}
				break;
			case 13:
				switch(token.first().first())
				{
				case CHARACTER:
					shift(8,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 14:
				switch(token.first().first())
				{
				case LPAREN:
					shift(5,token);
					break;
				case WILDCARD:
					shift(4,token);
					break;
				case LBRACK:
					shift(3,token);
					break;
				case CHARACTER:
					shift(8,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 15:
				switch(token.first().first())
				{
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					Regex right = parseStack.pop().second().second();
					Regex left = parseStack.pop().second().second();
					toGoto = Pair.cons(RegexNonTerminals.DR,(Regex) new ConcatenationRegex().addSubexps(left,right));
					break;
				default:
					syntaxError();
				}
				break;
			case 16:
				switch(token.first().first())
				{
				case LPAREN:
					shift(5,token);
					break;
				case WILDCARD:
					shift(4,token);
					break;
				case LBRACK:
					shift(3,token);
					break;
				case CHARACTER:
					shift(8,token);
					break;
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					toGoto = Pair.cons(RegexNonTerminals.RR,(Regex) new EmptyStringRegex());
					break;
				default:
					syntaxError();
				}
				break;
			case 17:
				switch(token.first().first())
				{
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					toGoto = Pair.cons(RegexNonTerminals.RR,parseStack.pop().second().second());
					break;
				default:
					syntaxError();
				}
				break;
			case 18:
				switch(token.first().first())
				{
				case LPAREN:
					shift(5,token);
					break;
				case WILDCARD:
					shift(4,token);
					break;
				case LBRACK:
					shift(3,token);
					break;
				case CHARACTER:
					shift(8,token);
					break;
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					toGoto = Pair.cons(RegexNonTerminals.RR,(Regex) new EmptyStringRegex());
					break;
				default:
					syntaxError();
				}
				break;
			case 19:
				switch(token.first().first())
				{
				case LPAREN:
					shift(5,token);
					break;
				case WILDCARD:
					shift(4,token);
					break;
				case LBRACK:
					shift(3,token);
					break;
				case CHARACTER:
					shift(8,token);
					break;
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					toGoto = Pair.cons(RegexNonTerminals.RR,(Regex) new EmptyStringRegex());
					break;
				default:
					syntaxError();
				}
				break;
			case 20:
				switch(token.first().first())
				{
				case STAR:
				case PLUS:
				case QUESTION:
				case ENDOFSTRING:
				case LPAREN:
				case BAR:
				case WILDCARD:
				case RPAREN:
				case LBRACK:
				case CHARACTER:
					parseStack.pop();
					Regex middle = parseStack.pop().second().second();
					parseStack.pop();
					toGoto = Pair.cons(RegexNonTerminals.UR,middle);
					break;
				default:
					syntaxError();
				}
				break;
			case 21:
				switch(token.first().first())
				{
				case CHARACTER:
					shift(8,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 22:
				switch(token.first().first())
				{
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					Regex right = parseStack.pop().second().second();
					parseStack.pop();
					Regex left = parseStack.pop().second().second();
				    toGoto = Pair.cons(RegexNonTerminals.DR,(Regex) new ConcatenationRegex().addSubexps(new KleeneStarRegex(left),right));
				    break;
				default:
					syntaxError();
				}
				break;
			case 23:
				switch(token.first().first())
				{
				case STAR:
				case PLUS:
				case QUESTION:
				case ENDOFSTRING:
				case LPAREN:
				case BAR:
				case WILDCARD:
				case RPAREN:
				case LBRACK:
				case CHARACTER:
					parseStack.pop();
					Regex middle = parseStack.pop().second().second();
					parseStack.pop();
					toGoto = Pair.cons(RegexNonTerminals.UR,middle);
					break;
				default:
					syntaxError();
				}
				break;
			case 24:
				switch(token.first().first())
				{
				case ENDOFSTRING:
				case RPAREN:
					Regex right = parseStack.pop().second().second();
					parseStack.pop();
					Regex left = parseStack.pop().second().second();
					toGoto = Pair.cons(RegexNonTerminals.R,(Regex) new ChoiceRegex().addSubexps(left,right));
					break;
				default:
					syntaxError();
				}
				break;
			case 25:
				switch(token.first().first())
				{
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					Regex right = parseStack.pop().second().second();
					parseStack.pop();
					Regex left = parseStack.pop().second().second();
				    toGoto = Pair.cons(RegexNonTerminals.DR,(Regex) new ConcatenationRegex().addSubexps(left,new KleeneStarRegex(left),right));
				    break;
				default:
					syntaxError();
				}
				break;
			case 26:
				switch(token.first().first())
				{
				case RBRACK:
					shift(31,token);
					break;
				default:
					syntaxError();
				}
				break;
			case 27:
				switch(token.first().first())
				{
				case RBRACK:
					toGoto = Pair.cons(RegexNonTerminals.RG,parseStack.pop().second().second());
					break;
				default:
					syntaxError();
				}
				break;
			case 28:
				switch(token.first().first())
				{
				case RBRACK:
					Regex RGNode = parseStack.pop().second().second();
					Regex UGNode = parseStack.pop().second().second();
					
					if(UGNode instanceof CharacterSetRegex && (RGNode == null || RGNode instanceof EmptyStringRegex))
					{
						toGoto = Pair.cons(RegexNonTerminals.G,UGNode);
					}
					else if(UGNode instanceof CharacterSetRegex && RGNode instanceof CharacterSetRegex)
					{
						toGoto = Pair.cons(RegexNonTerminals.G,(Regex) CharacterSetRegex.union((CharacterSetRegex) UGNode,(CharacterSetRegex) RGNode));
					}
					else
					{
						// Type error!
						System.err.println("Type error - state 28 - expected character sets or empty string, got (" + (UGNode == null ? "null" : UGNode.getClass().getSimpleName()) + "," + (RGNode == null ? "null" : RGNode.getClass().getSimpleName()) + ")");
						toGoto = Pair.cons(RegexNonTerminals.G,null);
					}
					break;
				default:
					syntaxError();
				}
				break;
			case 29:
				switch(token.first().first())
				{
				case ENDOFSTRING:
				case BAR:
				case RPAREN:
					Regex right = parseStack.pop().second().second();
					parseStack.pop();
					Regex left = parseStack.pop().second().second();
				    toGoto = Pair.cons(RegexNonTerminals.DR,(Regex) new ConcatenationRegex().addSubexps(new ChoiceRegex().addSubexps(new EmptyStringRegex(),left),right));
				    break;
				default:
					syntaxError();
				}
				break;
			case 30:
				switch(token.first().first())
				{
				case RBRACK:
				case CHARACTER:
					Regex characterNode2 = parseStack.pop().second().second();
					parseStack.pop();
					Regex characterNode1 = parseStack.pop().second().second();
					
					if(characterNode1 instanceof CharacterSetRegex &&
					   characterNode2 instanceof CharacterSetRegex)
					{
						char lowerBound = ((CharacterSetRegex) characterNode1).getOnlyChar();
						char upperBound = ((CharacterSetRegex) characterNode2).getOnlyChar();
						toGoto = Pair.cons(RegexNonTerminals.UG,(Regex) new CharacterSetRegex().addRange(lowerBound, upperBound));
					}
					else
					{
						// Type error!
						System.err.println("Type error - state 30 - expected character sets, got (" + (characterNode1 == null ? "null" : characterNode1.getClass().getSimpleName()) + "," + (characterNode2 == null ? "null" : characterNode2.getClass().getSimpleName()) + ")");
						toGoto = Pair.cons(RegexNonTerminals.UG,null);
					}
					break;
				default:
					syntaxError();
				}
				break;
			case 31:
				switch(token.first().first())
				{
				case STAR:
				case PLUS:
				case QUESTION:
				case ENDOFSTRING:
				case LPAREN:
				case BAR:
				case WILDCARD:
				case RPAREN:
				case LBRACK:
				case CHARACTER:
					parseStack.pop();
					Regex middle = parseStack.pop().second().second();
					parseStack.pop();
					parseStack.pop();
				    if(middle instanceof CharacterSetRegex)
					{
						toGoto = Pair.cons(RegexNonTerminals.UR,(Regex) ((CharacterSetRegex) middle).invert());
					}
					else
					{
						// Type error!
						System.err.println("Type error - state 31 - expected a character set, got " + (middle == null ? "null" : middle.getClass().getSimpleName()));
						toGoto = Pair.cons(RegexNonTerminals.UR,null);
					}
				    break;
				default:
					syntaxError();
				}
				break;
			// Should not be reached.
			default:
				syntaxError();
			}
			
			if(toGoto != null)
			{
				switch(parseStack.peek().first())
				{
				case 0:
					switch(toGoto.first())
					{
					case R:
						goTo(7,toGoto);
						break;
					case DR:
						goTo(2,toGoto);
						break;
					case CHAR:
						goTo(6,toGoto);
						break;
					case UR:
						goTo(1,toGoto);
						break;
					default:
						syntaxError();
					}
					break;
				case 1:
					switch(toGoto.first())
					{
					case DR:
						goTo(17,toGoto);
						break;
					case CHAR:
						goTo(6,toGoto);
						break;
					case RR:
						goTo(15,toGoto);
						break;
					case UR:
						goTo(1,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 3:
					switch(toGoto.first())
					{
					case UG:
						goTo(10,toGoto);
						break;
					case CHAR:
						goTo(12,toGoto);
						break;
					case G:
						goTo(11,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 5:
					switch(toGoto.first())
					{
					case R:
						goTo(9,toGoto);
						break;
					case DR:
						goTo(2,toGoto);
						break;
					case CHAR:
						goTo(6,toGoto);
						break;
					case UR:
						goTo(1,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 10:
					switch(toGoto.first())
					{
					case RG:
						goTo(28,toGoto);
						break;
					case UG:
						goTo(10,toGoto);
						break;
					case CHAR:
						goTo(12,toGoto);
						break;
					case G:
						goTo(27,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 13:
					switch(toGoto.first())
					{
					case UG:
						goTo(10,toGoto);
						break;
					case CHAR:
						goTo(12,toGoto);
						break;
					case G:
						goTo(26,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 14:
					switch(toGoto.first())
					{
					case R:
						goTo(24,toGoto);
						break;
					case DR:
						goTo(2,toGoto);
						break;
					case CHAR:
						goTo(6,toGoto);
						break;
					case UR:
						goTo(1,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 16:
					switch(toGoto.first())
					{
					case DR:
						goTo(17,toGoto);
						break;
					case CHAR:
						goTo(6,toGoto);
						break;
					case RR:
						goTo(29,toGoto);
						break;
					case UR:
						goTo(1,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 18:
					switch(toGoto.first())
					{
					case DR:
						goTo(17,toGoto);
						break;
					case CHAR:
						goTo(6,toGoto);
						break;
					case RR:
						goTo(25,toGoto);
						break;
					case UR:
						goTo(1,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 19:
					switch(toGoto.first())
					{
					case DR:
						goTo(17,toGoto);
						break;
					case CHAR:
						goTo(6,toGoto);
						break;
					case RR:
						goTo(22,toGoto);
						break;
					case UR:
						goTo(1,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 21:
					switch(toGoto.first())
					{
					case CHAR:
						goTo(30,toGoto);
						break;
					default:
					syntaxError();
					}
					break;
				case 2:
				case 4:
				case 6:
				case 7:
				case 8:
				case 9:
				case 11:
				case 12:
				case 15:
				case 17:
				case 20:
				case 22:
				case 23:
				case 24:
				case 25:
				case 26:
				case 27:
				case 28:
				case 29:
				case 30:
				case 31:
				// Should not be reached.
				default:
					syntaxError();

				}
			}
			// DEBUG-X-BEGIN
			//System.err.println("On top of stack is: " + parseStack.peek().first() + "," + parseStack.peek().second().first() + "," + parseStack.peek().second().second());
			// DEBUG-X-END
		}
		return parseStack.peek().second().second();
	}
	
}
