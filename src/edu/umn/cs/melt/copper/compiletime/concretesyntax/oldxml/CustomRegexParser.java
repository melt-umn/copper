package edu.umn.cs.melt.copper.compiletime.concretesyntax.oldxml;

import java.util.Stack;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
//import edu.umn.cs.melt.copper.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class CustomRegexParser
{
	/*public static void main(String[] args)
	throws CopperException
	{
		CompilerLogger logger = new StringBasedCompilerLogger();
		logger.setOut(System.err);
		CustomRegexParser parser = new CustomRegexParser(logger);
		parser.setToParse(args[0]);
		ParsedRegex regex = parser.parse();
		System.out.println(regex);
	}*/
	
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
	private Stack< Pair<Integer,Pair<String,ParsedRegex> > > parseStack; 
	private CompilerLogger logger;
	
	public CustomRegexParser(CompilerLogger logger)
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
				logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"Syntax error in regex '" + "Regex '" + toParse + "'");
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
		parseStack.push(Pair.cons(state,Pair.cons(token.first().second(),(ParsedRegex) null)));
	}
	
	public void goTo(int state,Pair<RegexNonTerminals,ParsedRegex> toPush)
	{
		parseStack.push(Pair.cons(state,Pair.cons((String) null,toPush.second())));
	}
	
	private ParsedRegex getWildcardRegex()
	{
		CharacterSet Newline = CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\n');
	    return invertCharacterSet(Newline);
	}

	private ParsedRegex invertCharacterSet(CharacterSet toInvert)
	{
		return toInvert.invertSet();
	}
	private ParsedRegex getCharactersRegex(String lexeme)
	{
	    return CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,lexeme.toCharArray());
	}
	
	private void syntaxError()
	throws CopperException
	{
		logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"Syntax error parsing regex '" + toParse + "'");
	}
	
	public ParsedRegex parse()
	throws CopperException
	{
		boolean finished = false;
		ParsedRegex pr;
		Pair<RegexNonTerminals,ParsedRegex> toGoto;
		Pair< Pair<RegexTerminals,String>,Integer > token;
		parseStack = new Stack< Pair<Integer,Pair<String,ParsedRegex> > >();
		parseStack.push(Pair.cons(0,Pair.cons("",(ParsedRegex) null)));
		position = 0;
		
		if(toParse.length() == 0) return new EmptyString();
		
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
					toGoto = Pair.cons(RegexNonTerminals.RR,(ParsedRegex) new EmptyString());
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
					toGoto = Pair.cons(RegexNonTerminals.RG,(ParsedRegex) new EmptyString());
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
					ParsedRegex right = parseStack.pop().second().second();
					ParsedRegex left = parseStack.pop().second().second();
					toGoto = Pair.cons(RegexNonTerminals.DR,(ParsedRegex) new Concatenation(left,right));
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
					toGoto = Pair.cons(RegexNonTerminals.RR,(ParsedRegex) new EmptyString());
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
					toGoto = Pair.cons(RegexNonTerminals.RR,(ParsedRegex) new EmptyString());
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
					toGoto = Pair.cons(RegexNonTerminals.RR,(ParsedRegex) new EmptyString());
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
					ParsedRegex middle = parseStack.pop().second().second();
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
					ParsedRegex right = parseStack.pop().second().second();
					parseStack.pop();
					ParsedRegex left = parseStack.pop().second().second();
				    toGoto = Pair.cons(RegexNonTerminals.DR,(ParsedRegex) new Concatenation(new KleeneStar(left),right));
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
					ParsedRegex middle = parseStack.pop().second().second();
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
					ParsedRegex right = parseStack.pop().second().second();
					parseStack.pop();
					ParsedRegex left = parseStack.pop().second().second();
					toGoto = Pair.cons(RegexNonTerminals.R,(ParsedRegex) new Choice(left,right));
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
					ParsedRegex right = parseStack.pop().second().second();
					parseStack.pop();
					ParsedRegex left = parseStack.pop().second().second();
				    toGoto = Pair.cons(RegexNonTerminals.DR,(ParsedRegex) new Concatenation(new Concatenation(left),new KleeneStar(left.clone()),right));
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
					ParsedRegex RGNode = parseStack.pop().second().second();
					ParsedRegex UGNode = parseStack.pop().second().second();
					
					if(UGNode instanceof CharacterSet && (RGNode == null || RGNode instanceof EmptyString))
					{
						toGoto = Pair.cons(RegexNonTerminals.G,UGNode);
					}
					else if(UGNode instanceof CharacterSet && RGNode instanceof CharacterSet)
					{
						toGoto = Pair.cons(RegexNonTerminals.G,(ParsedRegex) CharacterSet.union((CharacterSet) UGNode,(CharacterSet) RGNode));
					}
					else
					{
						// Type error!
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
					ParsedRegex right = parseStack.pop().second().second();
					parseStack.pop();
					ParsedRegex left = parseStack.pop().second().second();
				    toGoto = Pair.cons(RegexNonTerminals.DR,(ParsedRegex) new Concatenation(new Choice(new EmptyString(),left),right));
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
					ParsedRegex characterNode2 = parseStack.pop().second().second();
					parseStack.pop();
					ParsedRegex characterNode1 = parseStack.pop().second().second();
					
					if(characterNode1 instanceof CharacterSet &&
					   characterNode2 instanceof CharacterSet)
					{
						char lowerLimit = ((CharacterSet) characterNode1).getFirstChar();
						char upperLimit = ((CharacterSet) characterNode2).getFirstChar();
						toGoto = Pair.cons(RegexNonTerminals.UG,(ParsedRegex) CharacterSet.instantiate(CharacterSet.RANGES,'+',lowerLimit,upperLimit));
					}
					else
					{
						// Type error!
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
					ParsedRegex middle = parseStack.pop().second().second();
					parseStack.pop();
					parseStack.pop();
				    if(middle instanceof CharacterSet)
					{
						toGoto = Pair.cons(RegexNonTerminals.UR,invertCharacterSet((CharacterSet) middle));
					}
					else
					{
						// Type error!
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
