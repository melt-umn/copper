package edu.umn.cs.melt.copper.compiletime.skins.cup;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.PrintCompilerLogHandler;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericLocatedMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GrammarSyntaxError;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CharacterSetRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ChoiceRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ConcatenationRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.DisambiguationFunction;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.EmptyStringRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.KleeneStarRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.MacroHoleRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorAssociativity;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.TerminalClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.ParserSpecProcessor;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.io.Location;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperSyntaxError;

public class CupSkinParser extends edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine<ParserBean,edu.umn.cs.melt.copper.runtime.logging.CopperParserException>
{
    protected String formatError(String error)
    {
    	   String location = "";
        location += "line " + virtualLocation.getLine() + ", column " + virtualLocation.getColumn();
        if(currentState.pos.getFileName().length() > 40) location += "\n         ";
        location += " in file " + virtualLocation.getFileName();
        location += "\n         (parser state: " + currentState.statenum + "; real character index: " + currentState.pos.getPos() + ")";
        return "Error at " + location + ":\n  " + error;
    }
    protected void reportError(String message)
    throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
        throw new edu.umn.cs.melt.copper.runtime.logging.CopperParserException(message);
    }
    protected void reportSyntaxError()
    throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
    java.util.ArrayList<String> expectedTerminalsReal = bitVecToRealStringList(getShiftableSets()[currentState.statenum]);
    java.util.ArrayList<String> expectedTerminalsDisplay = bitVecToDisplayStringList(getShiftableSets()[currentState.statenum]);
    java.util.ArrayList<String> matchedTerminalsReal = bitVecToRealStringList(disjointMatch.terms);
    java.util.ArrayList<String> matchedTerminalsDisplay = bitVecToDisplayStringList(disjointMatch.terms);
    throw new edu.umn.cs.melt.copper.runtime.logging.CopperSyntaxError(virtualLocation,currentState.pos,currentState.statenum,expectedTerminalsReal,expectedTerminalsDisplay,matchedTerminalsReal,matchedTerminalsDisplay);
    }
    public void setupEngine()
    {
    }
    public int transition(int state,char ch)
    {
         return delta[state][cmap[ch]];
    }
    public class Semantics extends edu.umn.cs.melt.copper.runtime.engines.single.semantics.SingleDFASemanticActionContainer<edu.umn.cs.melt.copper.runtime.logging.CopperParserException>
    {

        public Semantics()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            runInit();
        }

        public void error(edu.umn.cs.melt.copper.runtime.io.InputPosition pos,java.lang.String message)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            reportError("Error at " + pos.toString() + ":\n  " + message);
        }

        public void runDefaultTermAction()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
        }
        public void runDefaultProdAction()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
        }
        public void runInit()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
             
			currentParser = new ParserBean();
			currentGrammar = new Grammar();
			ignoreTerminals = new TreeSet<CopperElementReference>();
			currentGrammar.setGrammarLayout(ignoreTerminals); 
			         }
        public java.lang.Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,java.lang.Object[] _children,int _prod)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            this._pos = _pos;
            this._children = _children;
            this._prod = _prod;
            this._specialAttributes = new edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes(virtualLocation);
            java.lang.Object RESULT = null;
            switch(_prod)
            {
            case 93:
                RESULT = runSemanticAction_93();
                break;
            case 94:
                RESULT = runSemanticAction_94();
                break;
            case 95:
                RESULT = runSemanticAction_95();
                break;
            case 96:
                RESULT = runSemanticAction_96();
                break;
            case 97:
                RESULT = runSemanticAction_97();
                break;
            case 100:
                RESULT = runSemanticAction_100();
                break;
            case 101:
                RESULT = runSemanticAction_101();
                break;
            case 102:
                RESULT = runSemanticAction_102();
                break;
            case 103:
                RESULT = runSemanticAction_103();
                break;
            case 104:
                RESULT = runSemanticAction_104();
                break;
            case 105:
                RESULT = runSemanticAction_105();
                break;
            case 106:
                RESULT = runSemanticAction_106();
                break;
            case 107:
                RESULT = runSemanticAction_107();
                break;
            case 108:
                RESULT = runSemanticAction_108();
                break;
            case 109:
                RESULT = runSemanticAction_109();
                break;
            case 111:
                RESULT = runSemanticAction_111();
                break;
            case 115:
                RESULT = runSemanticAction_115();
                break;
            case 116:
                RESULT = runSemanticAction_116();
                break;
            case 117:
                RESULT = runSemanticAction_117();
                break;
            case 118:
                RESULT = runSemanticAction_118();
                break;
            case 119:
                RESULT = runSemanticAction_119();
                break;
            case 120:
                RESULT = runSemanticAction_120();
                break;
            case 121:
                RESULT = runSemanticAction_121();
                break;
            case 122:
                RESULT = runSemanticAction_122();
                break;
            case 123:
                RESULT = runSemanticAction_123();
                break;
            case 124:
                RESULT = runSemanticAction_124();
                break;
            case 125:
                RESULT = runSemanticAction_125();
                break;
            case 128:
                RESULT = runSemanticAction_128();
                break;
            case 129:
                RESULT = runSemanticAction_129();
                break;
            case 130:
                RESULT = runSemanticAction_130();
                break;
            case 131:
                RESULT = runSemanticAction_131();
                break;
            case 132:
                RESULT = runSemanticAction_132();
                break;
            case 133:
                RESULT = runSemanticAction_133();
                break;
            case 134:
                RESULT = runSemanticAction_134();
                break;
            case 135:
                RESULT = runSemanticAction_135();
                break;
            case 136:
                RESULT = runSemanticAction_136();
                break;
            case 137:
                RESULT = runSemanticAction_137();
                break;
            case 138:
                RESULT = runSemanticAction_138();
                break;
            case 139:
                RESULT = runSemanticAction_139();
                break;
            case 140:
                RESULT = runSemanticAction_140();
                break;
            case 141:
                RESULT = runSemanticAction_141();
                break;
            case 142:
                RESULT = runSemanticAction_142();
                break;
            case 143:
                RESULT = runSemanticAction_143();
                break;
            case 144:
                RESULT = runSemanticAction_144();
                break;
            case 145:
                RESULT = runSemanticAction_145();
                break;
            case 146:
                RESULT = runSemanticAction_146();
                break;
            case 147:
                RESULT = runSemanticAction_147();
                break;
            case 148:
                RESULT = runSemanticAction_148();
                break;
            case 149:
                RESULT = runSemanticAction_149();
                break;
            case 150:
                RESULT = runSemanticAction_150();
                break;
            case 151:
                RESULT = runSemanticAction_151();
                break;
            case 152:
                RESULT = runSemanticAction_152();
                break;
            case 153:
                RESULT = runSemanticAction_153();
                break;
            case 154:
                RESULT = runSemanticAction_154();
                break;
            case 155:
                RESULT = runSemanticAction_155();
                break;
            case 156:
                RESULT = runSemanticAction_156();
                break;
            case 157:
                RESULT = runSemanticAction_157();
                break;
            case 158:
                RESULT = runSemanticAction_158();
                break;
            case 159:
                RESULT = runSemanticAction_159();
                break;
            case 160:
                RESULT = runSemanticAction_160();
                break;
            case 161:
                RESULT = runSemanticAction_161();
                break;
            case 162:
                RESULT = runSemanticAction_162();
                break;
            case 163:
                RESULT = runSemanticAction_163();
                break;
            case 164:
                RESULT = runSemanticAction_164();
                break;
            case 165:
                RESULT = runSemanticAction_165();
                break;
            case 166:
                RESULT = runSemanticAction_166();
                break;
            case 167:
                RESULT = runSemanticAction_167();
                break;
            case 168:
                RESULT = runSemanticAction_168();
                break;
            case 169:
                RESULT = runSemanticAction_169();
                break;
            case 170:
                RESULT = runSemanticAction_170();
                break;
            case 171:
                RESULT = runSemanticAction_171();
                break;
            case 172:
                RESULT = runSemanticAction_172();
                break;
            case 173:
                RESULT = runSemanticAction_173();
                break;
            case 174:
                RESULT = runSemanticAction_174();
                break;
            case 175:
                RESULT = runSemanticAction_175();
                break;
            default:
        runDefaultProdAction();
                 break;
            }
            return RESULT;
        }
        public java.lang.Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData _terminal)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            this._pos = _pos;
            this._terminal = _terminal;
            this._specialAttributes = new edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes(virtualLocation);
            String lexeme = _terminal.lexeme;
            java.lang.Object RESULT = null;
            switch(_terminal.firstTerm)
            {
            case 1:
                RESULT = runSemanticAction_1(lexeme);
                break;
            case 9:
                RESULT = runSemanticAction_9(lexeme);
                break;
            case 13:
                RESULT = runSemanticAction_13(lexeme);
                break;
            case 19:
                RESULT = runSemanticAction_19(lexeme);
                break;
            case 33:
                RESULT = runSemanticAction_33(lexeme);
                break;
            case 39:
                RESULT = runSemanticAction_39(lexeme);
                break;
            case 51:
                RESULT = runSemanticAction_51(lexeme);
                break;
            default:
        runDefaultTermAction();
                 break;
            }
            return RESULT;
        }
        public void runPostParseCode(java.lang.Object __root)
        {
            ParserBean root = (ParserBean) __root;
             CompilerLogger thisLogger = new CompilerLogger(new PrintCompilerLogHandler(System.err));
			try { ParserSpecProcessor.normalizeParser(root,thisLogger); thisLogger.flush(); } catch(CopperException ex) { ex.printStackTrace(); }
			System.out.println(edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.ParserSpecPlaintextPrinter.specToString(root)); 
        }
        public java.lang.Object runSemanticAction_93()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            OperatorAssociativity associativity = (OperatorAssociativity) _children[1];
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> terminals = (LinkedList<CopperElementReference>) _children[2];
            java.lang.Object RESULT = null;
            
                    for(CopperElementReference name : terminals)
                    {
						Terminal terminal = null;
	                	CopperElementName newTerminalName = name.getName();
	                	if(currentGrammar.getGrammarElements().contains(newTerminalName))
	                	{
	                		if(currentGrammar.getGrammarElement(newTerminalName).getType() != CopperElementType.TERMINAL)
	                		{
	                			logError("Attempt to declare a precedence on '" + newTerminalName + "', which is not a terminal",name.getLocation()); 
	                		}
	                		else
	                		{
	                			terminal = (Terminal) currentGrammar.getGrammarElement(newTerminalName);
	                			if(terminal.getOperatorPrecedence() != null || terminal.getOperatorAssociativity() != null)
	                			{
	                				logError("Precedence declared twice on terminal '" + newTerminalName + "'",name.getLocation());
	                			}
	                		}
	                	}
	                	else
	                	{
        	             	terminal = new Terminal();
		                    terminal.setName(newTerminalName);
	                     	try { currentGrammar.addGrammarElement(terminal); }
	                     	catch(CopperException ex) { logError(ex.getMessage(),getVirtualLocation()); }
	                    }
	                    
	                    if(terminal != null)
	                    {
	                    	terminal.setOperatorPrecedence(nextPrecedence);
	                    	terminal.setOperatorAssociativity(associativity);
	                    }
	                } 
	                nextPrecedence++;
                 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_94()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token lhs = (Token) _children[0];
            @SuppressWarnings("unchecked") LinkedList<Production> rhss = (LinkedList<Production>) _children[2];
            java.lang.Object RESULT = null;
             for(Production rhs : rhss)
                {
                	rhs.setLhs(newReference(lhs));
                   	try { currentGrammar.addGrammarElement(rhs); }
                   	catch(CopperException ex) { logError(ex.getMessage(),rhs.getLocation()); }
                }  
            return RESULT;
        }
        public java.lang.Object runSemanticAction_95()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[2];
            java.lang.Object RESULT = null;
             if(currentParser.getStartSymbol() != null)
                                {
                                	logError("Only one start symbol allowed in a parser specification; start symbol previously specified at " + currentParser.getStartSymbol().getLocation(),name.location);
                                }
                                else
                                {
                                    currentParser.setStartSymbol(newReference(name));
                                }
                                 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_96()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token type = (Token) _children[2];
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> names = (LinkedList<CopperElementReference>) _children[3];
            java.lang.Object RESULT = null;
            
                            for(CopperElementReference ref : names)
                            {
	        	             	NonTerminal nonterminal = new NonTerminal();
			                    nonterminal.setName(ref.getName());
			                    nonterminal.setLocation(ref.getLocation());
			                    nonterminal.setReturnType(type.lexeme);
		                     	try { currentGrammar.addGrammarElement(nonterminal); }
		                     	catch(CopperException ex) { logError(ex.getMessage(),ref.getLocation()); }
		                     }
                 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_97()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> names = (LinkedList<CopperElementReference>) _children[2];
            java.lang.Object RESULT = null;
            
                            for(CopperElementReference ref : names)
                            {
	        	             	NonTerminal nonterminal = new NonTerminal();
			                    nonterminal.setName(ref.getName());
			                    nonterminal.setLocation(ref.getLocation());
		                     	try { currentGrammar.addGrammarElement(nonterminal); }
		                     	catch(CopperException ex) { logError(ex.getMessage(),ref.getLocation()); }
		                     }
                 
            return RESULT;
        }
        public Token runSemanticAction_100()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public Token runSemanticAction_101()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token code = (Token) _children[1];
            Token RESULT = null;
             RESULT = code; 
            return RESULT;
        }
        public LinkedList<CopperElementReference> runSemanticAction_102()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> tail = (LinkedList<CopperElementReference>) _children[1];
            LinkedList<CopperElementReference> RESULT = null;
             tail.addFirst(newReference(name));
                                RESULT = tail; 
            return RESULT;
        }
        public LinkedList<CopperElementReference> runSemanticAction_103()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> tail = (LinkedList<CopperElementReference>) _children[2];
            LinkedList<CopperElementReference> RESULT = null;
             tail.addFirst(newReference(name));
                                RESULT = tail; 
            return RESULT;
        }
        public LinkedList<CopperElementReference> runSemanticAction_104()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token head = (Token) _children[0];
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> tail = (LinkedList<CopperElementReference>) _children[2];
            LinkedList<CopperElementReference> RESULT = null;
             tail.addFirst(newReference(head));
                                RESULT = tail;
                               
            return RESULT;
        }
        public LinkedList<CopperElementReference> runSemanticAction_105()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            LinkedList<CopperElementReference> RESULT = null;
             LinkedList<CopperElementReference> newList = new LinkedList<CopperElementReference>();
                                newList.add(newReference(name));
                                RESULT = newList; 
            return RESULT;
        }
        public LinkedList<CopperElementReference> runSemanticAction_106()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            LinkedList<CopperElementReference> RESULT = null;
             RESULT = new LinkedList<CopperElementReference>(); 
            return RESULT;
        }
        public LinkedList<CopperElementReference> runSemanticAction_107()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> seq = (LinkedList<CopperElementReference>) _children[0];
            LinkedList<CopperElementReference> RESULT = null;
             RESULT = seq; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_108()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token type = (Token) _children[1];
            Token name = (Token) _children[2];
            java.lang.Object RESULT = null;
             ParserAttribute attr = new ParserAttribute();
                attr.setName(newName(name));
                attr.setLocation(name.location);
                attr.setAttributeType(type.lexeme);
				try { currentGrammar.addGrammarElement(attr); }
	    		catch(CopperException ex) { logError(ex.getMessage(),name.location); }
                 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_109()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token code = (Token) _children[1];
            java.lang.Object RESULT = null;
             currentParser.setParserClassAuxCode(code.lexeme); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_111()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token code = (Token) _children[1];
            java.lang.Object RESULT = null;
             currentParser.setParserInitCode(code.lexeme); 
            return RESULT;
        }
        public ParserBean runSemanticAction_115()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token code = (Token) _children[0];
            ParserBean RESULT = null;
             
                     currentParser.setPreambleCode(code.lexeme);
                     currentParser.setLocation(code.location);
                     currentGrammar.setLocation(code.location);
                     try { currentParser.addGrammar(currentGrammar); }
	    			catch(CopperException ex) { logError(ex.getMessage(),getVirtualLocation()); }
                     RESULT = currentParser;
                  
            return RESULT;
        }
        public Boolean runSemanticAction_116()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Boolean RESULT = null;
             RESULT = true; 
            return RESULT;
        }
        public Boolean runSemanticAction_117()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Boolean RESULT = null;
             RESULT = false; 
            return RESULT;
        }
        public LinkedList<LabeledName> runSemanticAction_118()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            LinkedList<LabeledName> RESULT = null;
             RESULT = new LinkedList<LabeledName>(); 
            return RESULT;
        }
        public LinkedList<LabeledName> runSemanticAction_119()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            Token label = (Token) _children[2];
            @SuppressWarnings("unchecked") LinkedList<LabeledName> tail = (LinkedList<LabeledName>) _children[3];
            LinkedList<LabeledName> RESULT = null;
             tail.addFirst(new LabeledName(label,name));
                                RESULT = tail; 
            return RESULT;
        }
        public LinkedList<LabeledName> runSemanticAction_120()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            @SuppressWarnings("unchecked") LinkedList<LabeledName> tail = (LinkedList<LabeledName>) _children[1];
            LinkedList<LabeledName> RESULT = null;
             tail.addFirst(new LabeledName(name));
                                RESULT = tail; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_121()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[1];
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> members = (LinkedList<CopperElementReference>) _children[4];
            Token code = (Token) _children[7];
            java.lang.Object RESULT = null;
             DisambiguationFunction df = new DisambiguationFunction();
                df.setName(newName(name));
                df.setLocation(name.location);
                TreeSet<CopperElementReference> newMembers = new TreeSet<CopperElementReference>(members);
                df.setMembers(newMembers);
                df.setCode(code.lexeme);
                
                try { currentGrammar.addGrammarElement(df); }
                catch(CopperException ex) { logError(ex.getMessage(),name.location); }
                 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_122()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[1];
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> members = (LinkedList<CopperElementReference>) _children[4];
            Token disambiguateTo = (Token) _children[7];
            java.lang.Object RESULT = null;
             DisambiguationFunction df = new DisambiguationFunction();
                df.setName(newName(name));
                df.setLocation(name.location);
                TreeSet<CopperElementReference> newMembers = new TreeSet<CopperElementReference>(members);
                df.setMembers(newMembers);
                df.setDisambiguateTo(newReference(disambiguateTo));
                
                try { currentGrammar.addGrammarElement(df); }
                catch(CopperException ex) { logError(ex.getMessage(),name.location); }
                
            return RESULT;
        }
        public java.lang.Object runSemanticAction_123()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> classes = (LinkedList<CopperElementReference>) _children[1];
            java.lang.Object RESULT = null;
             for(CopperElementReference termClass : classes)
{
	if(currentGrammar.getGrammarElements().contains(termClass.getName()))
	{
		logError("Duplicate declaration of '" + termClass.getName() + "' first declared at " + currentGrammar.getGrammarElement(termClass.getName()).getLocation(),termClass.getLocation());
	}
	else
	{
		TerminalClass bean = new TerminalClass();
		bean.setName(termClass.getName());
		bean.setLocation(termClass.getLocation());
		try { currentGrammar.addGrammarElement(bean); }
	    catch(CopperException ex) { logError(ex.getMessage(),getVirtualLocation()); }
	}
} 

            return RESULT;
        }
        public java.lang.Object runSemanticAction_124()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Boolean ignore = (Boolean) _children[0];
            Token name = (Token) _children[2];
            Regex regex = (Regex) _children[3];
            PrecLists precLists = (PrecLists) _children[4];
            Token code = (Token) _children[5];
            TerminalFlags terminalFlags = (TerminalFlags) _children[6];
            java.lang.Object RESULT = null;
             Terminal terminal = null;
                CopperElementName newTerminalName = newName(name);
                if(currentGrammar.getGrammarElements().contains(newTerminalName))
                {
                	if(currentGrammar.getGrammarElement(newTerminalName).getType() != CopperElementType.TERMINAL ||
                	   currentGrammar.getGrammarElement(newTerminalName).getLocation() != null)
                	{
                		logError("Duplicate declaration of '" + newTerminalName + "'" + (currentGrammar.getGrammarElement(newTerminalName).getLocation() != null ? " first declared at " + currentGrammar.getGrammarElement(newTerminalName).getLocation() : ""),name.location);
                	}
                	else
                    {
                        terminal = (Terminal) currentGrammar.getGrammarElement(newTerminalName);
                        terminal.setLocation(name.location);
                    }
                }
                else
                {
                     terminal = new Terminal();
                     terminal.setName(newTerminalName);
                     terminal.setLocation(name.location);
                     try { currentGrammar.addGrammarElement(terminal); }
                     catch(CopperException ex) { logError(ex.getMessage(),name.location); }
                } 
                
                if(terminal != null)
                {
	                terminal.setRegex(regex);
	                if(code != null) terminal.setCode(code.lexeme);
	                if(precLists != null)
	                {
	                	if(precLists.termClasses != null)
	                	{
	                		for(CopperElementReference termClass : precLists.termClasses)
	                		{
	                			terminal.addTerminalClass(termClass);
	                		}
	                	}
	                	if(precLists.submitList != null)
	                	{
	                		for(CopperElementReference t : precLists.submitList)
	                		{
	                			terminal.addSubmitsTo(t);
	                		}
	                	}
	                	if(precLists.dominateList != null)
	                	{
	                		for(CopperElementReference t : precLists.dominateList)
	                		{
	                			terminal.addDominates(t);
	                		}
	                	}
	                }
	                if(terminalFlags.prefix != null) terminal.setPrefix(terminalFlags.prefix);
	                
	                if(ignore) ignoreTerminals.add(CopperElementReference.ref(newTerminalName,name.location));
	            }
                
            return RESULT;
        }
        public java.lang.Object runSemanticAction_125()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Boolean ignore = (Boolean) _children[0];
            Token type = (Token) _children[2];
            Token name = (Token) _children[3];
            Regex regex = (Regex) _children[5];
            PrecLists precLists = (PrecLists) _children[6];
            Token code = (Token) _children[7];
            TerminalFlags terminalFlags = (TerminalFlags) _children[8];
            java.lang.Object RESULT = null;
             Terminal terminal = null;
                CopperElementName newTerminalName = newName(name);
                if(currentGrammar.getGrammarElements().contains(newTerminalName))
                {
                	if(currentGrammar.getGrammarElement(newTerminalName).getType() != CopperElementType.TERMINAL ||
                	   currentGrammar.getGrammarElement(newTerminalName).getLocation() != null)
                	{
                		logError("Duplicate declaration of '" + newTerminalName + "'" + (currentGrammar.getGrammarElement(newTerminalName).getLocation() != null ? " first declared at " + currentGrammar.getGrammarElement(newTerminalName).getLocation() : ""),name.location);
                	}
                	else
                    {
                        terminal = (Terminal) currentGrammar.getGrammarElement(newTerminalName);
                        terminal.setLocation(name.location);
                    }
                }
                else
                {
                     terminal = new Terminal();
                     terminal.setName(newTerminalName);
                     terminal.setLocation(name.location);
                     try { currentGrammar.addGrammarElement(terminal); }
                     catch(CopperException ex) { logError(ex.getMessage(),name.location); }
                } 
                
                if(terminal != null)
                {
	                terminal.setRegex(regex);
                    terminal.setReturnType(type.lexeme);
	                if(code != null) terminal.setCode(code.lexeme);
	                if(precLists != null)
	                {
	                	if(precLists.termClasses != null)
	                	{
	                		for(CopperElementReference termClass : precLists.termClasses)
	                		{
	                			terminal.addTerminalClass(termClass);
	                		}
	                	}
	                	if(precLists.submitList != null)
	                	{
	                		for(CopperElementReference t : precLists.submitList)
	                		{
	                			terminal.addSubmitsTo(t);
	                		}
	                	}
	                	if(precLists.dominateList != null)
	                	{
	                		for(CopperElementReference t : precLists.dominateList)
	                		{
	                			terminal.addDominates(t);
	                		}
	                	}
	                }
	                if(terminalFlags.prefix != null) terminal.setPrefix(terminalFlags.prefix);
	                
	                if(ignore) ignoreTerminals.add(CopperElementReference.ref(newTerminalName,name.location));
	            } 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_128()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token parserName = (Token) _children[1];
            java.lang.Object RESULT = null;
             currentParser.setName(newName(parserName));
                				currentParser.setUnitary(true);
                                currentGrammar.setName(newName(new Token("_" + parserName.lexeme,parserName.location)));
                                currentParser.setClassName(parserName.lexeme); 
            return RESULT;
        }
        public PrecLists runSemanticAction_129()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> terms = (LinkedList<CopperElementReference>) _children[2];
            PrecLists RESULT = null;
             PrecLists precLists = new PrecLists();
                precLists.dominateList = terms;
                RESULT = precLists;
                
            return RESULT;
        }
        public PrecLists runSemanticAction_130()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> terms = (LinkedList<CopperElementReference>) _children[2];
            PrecLists RESULT = null;
            PrecLists precLists = new PrecLists();
                precLists.termClasses = terms;
                RESULT = precLists; 
            return RESULT;
        }
        public PrecLists runSemanticAction_131()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> terms = (LinkedList<CopperElementReference>) _children[2];
            PrecLists RESULT = null;
             PrecLists precLists = new PrecLists();
                precLists.submitList = terms;
                RESULT = precLists; 
            return RESULT;
        }
        public PrecLists runSemanticAction_132()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            PrecLists head = (PrecLists) _children[0];
            PrecLists tail = (PrecLists) _children[2];
            PrecLists RESULT = null;
            
                    if(head.termClasses != null && tail.termClasses != null)
                    {
                    	logError("More than one 'in' list specified on a terminal",getVirtualLocation());
                    }
                    else if(head.termClasses != null && tail.termClasses == null) tail.termClasses = head.termClasses;
                    if(head.submitList != null && tail.submitList != null)
                    {
                    	logError("More than one '<' list specified on a terminal",getVirtualLocation());
                    }
                    else if(head.submitList != null && tail.submitList == null) tail.submitList = head.submitList;
                    if(head.dominateList != null && tail.dominateList != null)
                    {
                    	logError("More than one '>' list specified on a terminal",getVirtualLocation());
                    }
                    else if(head.dominateList != null && tail.dominateList == null) tail.dominateList = head.dominateList;
                    RESULT = tail;
                 
            return RESULT;
        }
        public PrecLists runSemanticAction_133()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            PrecLists tail = (PrecLists) _children[0];
            PrecLists RESULT = null;
             RESULT = tail; 
            return RESULT;
        }
        public PrecLists runSemanticAction_134()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            PrecLists RESULT = null;
             RESULT = new PrecLists(); 
            return RESULT;
        }
        public PrecLists runSemanticAction_135()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            PrecLists precLists = (PrecLists) _children[0];
            PrecLists RESULT = null;
             RESULT = precLists; 
            return RESULT;
        }
        public Token runSemanticAction_136()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            Token qname = (Token) _children[2];
            Token RESULT = null;
             RESULT = new Token(name.lexeme + "." + qname.lexeme,name.location); 
            return RESULT;
        }
        public Token runSemanticAction_137()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            Token RESULT = null;
             RESULT = name; 
            return RESULT;
        }
        public Production runSemanticAction_138()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") LinkedList<LabeledName> rhsElements = (LinkedList<LabeledName>) _children[0];
            Token code = (Token) _children[1];
            ProductionRHSFlags flags = (ProductionRHSFlags) _children[2];
            Production RESULT = null;
            
                Production bean = new Production();
                bean.setName(newName(new Token("_p" + (nextProduction++),getVirtualLocation())));
                bean.setLocation(getVirtualLocation());
                ArrayList<CopperElementReference> rhsSyms = new ArrayList<CopperElementReference>();
                ArrayList<String> rhsVarNames = new ArrayList<String>();
                for(LabeledName rhsSym : rhsElements)
                {
                	rhsSyms.add(newReference(rhsSym.name));
                	if(rhsSym.label != null) rhsVarNames.add(rhsSym.label.lexeme);
                	else rhsVarNames.add(null);
                }
                bean.setRhs(rhsSyms);
                bean.setRhsVarNames(rhsVarNames);
                if(code != null) bean.setCode(code.lexeme);
                if(flags.layout != null) bean.setLayout(new TreeSet<CopperElementReference>(flags.layout));
                if(flags.operator != null) bean.setOperator(flags.operator);
                RESULT = bean;
                
            return RESULT;
        }
        public ProductionRHSFlags runSemanticAction_139()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> layout = (LinkedList<CopperElementReference>) _children[2];
            ProductionRHSFlags tail = (ProductionRHSFlags) _children[4];
            ProductionRHSFlags RESULT = null;
             if(tail.layout != null) logError("Only one set of layout allowed per production",getVirtualLocation());
                                else tail.layout = layout;
                                RESULT = tail; 
            return RESULT;
        }
        public ProductionRHSFlags runSemanticAction_140()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[1];
            ProductionRHSFlags tail = (ProductionRHSFlags) _children[2];
            ProductionRHSFlags RESULT = null;
             if(tail.operator != null) logError("Only one precedence operator allowed per production",name.location);
                                else tail.operator = newReference(name);
                                RESULT = tail; 
            return RESULT;
        }
        public ProductionRHSFlags runSemanticAction_141()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            ProductionRHSFlags RESULT = null;
             RESULT = new ProductionRHSFlags(); 
            return RESULT;
        }
        public LinkedList<Production> runSemanticAction_142()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Production head = (Production) _children[0];
            @SuppressWarnings("unchecked") LinkedList<Production> tail = (LinkedList<Production>) _children[2];
            LinkedList<Production> RESULT = null;
             tail.add(head);
                                RESULT = tail; 
            return RESULT;
        }
        public LinkedList<Production> runSemanticAction_143()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Production rhs = (Production) _children[0];
            LinkedList<Production> RESULT = null;
             LinkedList<Production> rv = new LinkedList<Production>();
                                rv.add(rhs);
                                RESULT = rv; 
            return RESULT;
        }
        public Regex runSemanticAction_144()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex regex = (Regex) _children[1];
            Regex RESULT = null;
             RESULT = regex; 
            return RESULT;
        }
        public Regex runSemanticAction_145()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex RESULT = null;
             RESULT = new EmptyStringRegex(); 
            return RESULT;
        }
        public Character runSemanticAction_146()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Character character = (Character) _children[0];
            Character RESULT = null;
             RESULT = character; 
            return RESULT;
        }
        public Character runSemanticAction_147()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Character character = (Character) _children[0];
            Character RESULT = null;
             RESULT = character; 
            return RESULT;
        }
        public Regex runSemanticAction_148()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex lhs = (Regex) _children[0];
            Regex rhs = (Regex) _children[1];
            Regex RESULT = null;
             RESULT = new ConcatenationRegex().addSubexps(lhs,rhs); 
            return RESULT;
        }
        public Regex runSemanticAction_149()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex plussed = (Regex) _children[0];
            Regex rest = (Regex) _children[2];
            Regex RESULT = null;
             RESULT = new ConcatenationRegex().addSubexps(plussed,new KleeneStarRegex(plussed),rest); 
            return RESULT;
        }
        public Regex runSemanticAction_150()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex opt = (Regex) _children[0];
            Regex rest = (Regex) _children[2];
            Regex RESULT = null;
             RESULT = new ConcatenationRegex().addSubexps(new ChoiceRegex().addSubexps(opt,new EmptyStringRegex()),rest); 
            return RESULT;
        }
        public Regex runSemanticAction_151()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex starred = (Regex) _children[0];
            Regex rest = (Regex) _children[2];
            Regex RESULT = null;
             RESULT = new ConcatenationRegex().addSubexps(new KleeneStarRegex(starred),rest); 
            return RESULT;
        }
        public CharacterSetRegex runSemanticAction_152()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            CharacterSetRegex lhs = (CharacterSetRegex) _children[0];
            CharacterSetRegex rhs = (CharacterSetRegex) _children[1];
            CharacterSetRegex RESULT = null;
             RESULT = CharacterSetRegex.union(lhs,rhs); 
            return RESULT;
        }
        public Regex runSemanticAction_153()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex regex = (Regex) _children[0];
            Regex RESULT = null;
             RESULT = regex; 
            return RESULT;
        }
        public Regex runSemanticAction_154()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex lhs = (Regex) _children[0];
            Regex rhs = (Regex) _children[2];
            Regex RESULT = null;
             RESULT = new ChoiceRegex().addSubexps(lhs,rhs); 
            return RESULT;
        }
        public CharacterSetRegex runSemanticAction_155()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            CharacterSetRegex charset = (CharacterSetRegex) _children[0];
            CharacterSetRegex RESULT = null;
             RESULT = charset; 
            return RESULT;
        }
        public CharacterSetRegex runSemanticAction_156()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            CharacterSetRegex RESULT = null;
             RESULT = new CharacterSetRegex(); 
            return RESULT;
        }
        public Regex runSemanticAction_157()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex regex = (Regex) _children[0];
            Regex RESULT = null;
             RESULT = regex; 
            return RESULT;
        }
        public Regex runSemanticAction_158()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex RESULT = null;
             RESULT = new EmptyStringRegex(); 
            return RESULT;
        }
        public CharacterSetRegex runSemanticAction_159()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Character character = (Character) _children[0];
            CharacterSetRegex RESULT = null;
             RESULT = new CharacterSetRegex().addLooseChar(character); 
            return RESULT;
        }
        public CharacterSetRegex runSemanticAction_160()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Character lower = (Character) _children[0];
            Character upper = (Character) _children[2];
            CharacterSetRegex RESULT = null;
             RESULT = new CharacterSetRegex().addRange(lower,upper); 
            return RESULT;
        }
        public Regex runSemanticAction_161()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Character character = (Character) _children[0];
            Regex RESULT = null;
             RESULT = new CharacterSetRegex().addLooseChar(character); 
            return RESULT;
        }
        public Regex runSemanticAction_162()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            CharacterSetRegex regex = (CharacterSetRegex) _children[1];
            Regex RESULT = null;
             RESULT = regex; 
            return RESULT;
        }
        public Regex runSemanticAction_163()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            CharacterSetRegex regex = (CharacterSetRegex) _children[2];
            Regex RESULT = null;
             RESULT = regex.invert(); 
            return RESULT;
        }
        public Regex runSemanticAction_164()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex regex = (Regex) _children[1];
            Regex RESULT = null;
             RESULT = regex; 
            return RESULT;
        }
        public Regex runSemanticAction_165()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token term = (Token) _children[2];
            Regex RESULT = null;
             RESULT = new MacroHoleRegex(newReference(term)); 
            return RESULT;
        }
        public Regex runSemanticAction_166()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex RESULT = null;
             RESULT = new CharacterSetRegex().addLooseChar('\n').invert(); 
            return RESULT;
        }
        public Regex runSemanticAction_167()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Regex regex = (Regex) _children[1];
            Regex RESULT = null;
             RESULT = regex; 
            return RESULT;
        }
        public LinkedList<CopperElementReference> runSemanticAction_168()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            LinkedList<CopperElementReference> RESULT = null;
             RESULT = new LinkedList<CopperElementReference>(); 
            return RESULT;
        }
        public LinkedList<CopperElementReference> runSemanticAction_169()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            @SuppressWarnings("unchecked") LinkedList<CopperElementReference> tail = (LinkedList<CopperElementReference>) _children[1];
            LinkedList<CopperElementReference> RESULT = null;
             tail.addFirst(newReference(name));
                                 RESULT = tail; 
            return RESULT;
        }
        public TerminalFlags runSemanticAction_170()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[1];
            TerminalFlags tail = (TerminalFlags) _children[2];
            TerminalFlags RESULT = null;
             if(tail.prefix != null) logError("More than one prefix specified on a terminal",name.location);
                tail.prefix = newReference(name);
                RESULT = tail; 
            return RESULT;
        }
        public TerminalFlags runSemanticAction_171()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            TerminalFlags RESULT = null;
             RESULT = new TerminalFlags(); 
            return RESULT;
        }
        public Token runSemanticAction_172()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token qname = (Token) _children[0];
            Token RESULT = null;
             RESULT = qname; 
            return RESULT;
        }
        public Token runSemanticAction_173()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token qname = (Token) _children[0];
            @SuppressWarnings("unchecked") LinkedList<Token> names = (LinkedList<Token>) _children[2];
            Token RESULT = null;
             String rv = qname.lexeme + "< ";
                boolean first = true;
                for(Token name : names)
                {
                	if(!first) rv += " , ";
                	else first = false;
                	rv += name.lexeme;
                } 
                rv += " >";
                RESULT = new Token(rv,qname.location); 
            return RESULT;
        }
        public LinkedList<Token> runSemanticAction_174()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token head = (Token) _children[0];
            @SuppressWarnings("unchecked") LinkedList<Token> tail = (LinkedList<Token>) _children[2];
            LinkedList<Token> RESULT = null;
             tail.addFirst(head);
                                RESULT = tail; 
            return RESULT;
        }
        public LinkedList<Token> runSemanticAction_175()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token name = (Token) _children[0];
            LinkedList<Token> RESULT = null;
             LinkedList<Token> rv = new LinkedList<Token>();
                                rv.add(name);
                                RESULT = rv; 
            return RESULT;
        }
        public OperatorAssociativity runSemanticAction_1(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            OperatorAssociativity RESULT = null;
            
                if(lexeme.equals("left")) RESULT = OperatorAssociativity.LEFT;
                else if(lexeme.equals("right")) RESULT = OperatorAssociativity.RIGHT;
                else if(lexeme.equals("nonassoc")) RESULT = OperatorAssociativity.NONASSOC;
                else RESULT = OperatorAssociativity.NONE;
            return RESULT;
        }
        public Character runSemanticAction_9(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Character RESULT = null;
             RESULT = lexeme.charAt(0); 
            return RESULT;
        }
        public Token runSemanticAction_13(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = new Token(lexeme,getStartRealLocation()); 
            return RESULT;
        }
        public Character runSemanticAction_19(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Character RESULT = null;
                 char escapedChar = QuotedStringFormatter.getRepresentedCharacter(lexeme);
                                    if(escapedChar == ScannerBuffer.EOFIndicator) error(_pos,"Illegal escaped character");
    								RESULT = escapedChar; 
            return RESULT;
        }
        public Token runSemanticAction_33(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = new Token(lexeme,getStartRealLocation()); 
            return RESULT;
        }
        public Integer runSemanticAction_39(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Integer RESULT = null;
             RESULT = Integer.parseInt(lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_51(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = new Token(lexeme,getStartRealLocation()); 
            return RESULT;
        }
        public int runDisambiguationAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData match)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            String lexeme = match.lexeme;
            if(match.terms.equals(disambiguationGroups[0])) return disambiguate_0(lexeme);
            else if(match.terms.equals(disambiguationGroups[1])) return disambiguate_1(lexeme);
            else if(match.terms.equals(disambiguationGroups[2])) return disambiguate_2(lexeme);
            else if(match.terms.equals(disambiguationGroups[3])) return disambiguate_3(lexeme);
            else if(match.terms.equals(disambiguationGroups[4])) return disambiguate_4(lexeme);
            else if(match.terms.equals(disambiguationGroups[5])) return disambiguate_5(lexeme);
            else if(match.terms.equals(disambiguationGroups[6])) return disambiguate_6(lexeme);
            else if(match.terms.equals(disambiguationGroups[7])) return disambiguate_7(lexeme);
            else if(match.terms.equals(disambiguationGroups[8])) return disambiguate_8(lexeme);
            else if(match.terms.equals(disambiguationGroups[9])) return disambiguate_9(lexeme);
            else if(match.terms.equals(disambiguationGroups[10])) return disambiguate_10(lexeme);
            else if(match.terms.equals(disambiguationGroups[11])) return disambiguate_11(lexeme);
            else if(match.terms.equals(disambiguationGroups[12])) return disambiguate_12(lexeme);
            else if(match.terms.equals(disambiguationGroups[13])) return disambiguate_13(lexeme);
            else if(match.terms.equals(disambiguationGroups[14])) return disambiguate_14(lexeme);
            else if(match.terms.equals(disambiguationGroups[15])) return disambiguate_15(lexeme);
            else if(match.terms.equals(disambiguationGroups[16])) return disambiguate_16(lexeme);
            else if(match.terms.equals(disambiguationGroups[17])) return disambiguate_17(lexeme);
            else return -1;
        }
        public int disambiguate_0(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* lparen */ 31;
        }
        public int disambiguate_1(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* not */ 35;
        }
        public int disambiguate_2(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* colon */ 14;
        }
        public int disambiguate_3(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* plus */ 37;
        }
        public int disambiguate_4(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* slash_kwd */ 47;
        }
        public int disambiguate_5(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* rparen */ 45;
        }
        public int disambiguate_6(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* wildcard */ 52;
        }
        public int disambiguate_7(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* lbrack */ 28;
        }
        public int disambiguate_8(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* rbrack */ 44;
        }
        public int disambiguate_9(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* bar */ 5;
        }
        public int disambiguate_10(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* question */ 42;
        }
        public int disambiguate_11(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* star */ 48;
        }
        public int disambiguate_12(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* dash */ 17;
        }
        public int disambiguate_13(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* non_kwd */ 34;
        }
        public int disambiguate_14(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* start_kwd */ 49;
        }
        public int disambiguate_15(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* precedence_kwd */ 40;
        }
        public int disambiguate_16(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* code_t */ 13;
        }
        public int disambiguate_17(String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return /* ws */ 54;
        }
    }
    public Semantics semantics;
    public java.lang.Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,java.lang.Object[] _children,int _prod)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
        return semantics.runSemanticAction(_pos,_children,_prod);
    }
    public java.lang.Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData _terminal)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
        return semantics.runSemanticAction(_pos,_terminal);
    }
    public void runPostParseCode(java.lang.Object __root)
    {
        semantics.runPostParseCode(__root);
    }
    public int runDisambiguationAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData matches)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
        return semantics.runDisambiguationAction(_pos,matches);
    }
    public edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes getSpecialAttributes()
    {
        return semantics.getSpecialAttributes();
    }
    public void startEngine(edu.umn.cs.melt.copper.runtime.io.InputPosition initialPos)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
         super.startEngine(initialPos);
         semantics = new Semantics();
    }

public static final byte[] symbolNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\155\125\113\162\343\066" +
"\020\145\215\307\222\250\277\145\127\166\271\302\134\040\053\217" +
"\074\346\270\112\261\034\322\116\026\131\260\100\022\226\021\223" +
"\000\007\004\307\162\345\112\111\125\316\222\113\244\262\310\035" +
"\322\215\017\011\226\147\043\275\176\154\364\037\215\077\376\013" +
"\116\133\031\234\377\272\373\215\174\045\037\112\302\017\037\022" +
"\045\031\077\374\360\347\337\077\377\363\357\367\277\107\357\202" +
"\340\130\007\101\360\227\012\116\076\355\257\125\260\044\115\043" +
"\162\365\132\323\046\175\176\051\124\260\041\012\216\144\255\242" +
"\151\101\363\322\220\347\244\075\246\131\051\362\347\064\057\105" +
"\103\235\152\307\212\232\162\103\236\144\104\252\140\006\277\222" +
"\121\151\025\363\307\267\247\317\072\262\077\034\346\117\104\222" +
"\134\121\211\270\204\330\014\177\221\213\202\276\065\161\356\321" +
"\275\221\221\146\225\012\116\163\121\012\216\226\360\337\171\020" +
"\125\105\014\176\137\220\346\111\005\353\202\065\244\312\330\241" +
"\045\312\032\036\323\046\047\065\005\064\075\010\332\050\141\055" +
"\037\224\001\123\166\340\102\132\345\021\263\266\057\030\147\352" +
"\033\121\172\164\037\345\252\044\257\242\125\136\221\107\145\006" +
"\271\123\007\236\341\144\111\277\125\366\236\365\222\056\153\042" +
"\051\107\140\143\234\160\122\101\031\004\330\031\163\227\376\011" +
"\027\120\230\025\350\066\320\234\336\367\373\272\154\033\025\054" +
"\152\111\163\217\236\151\231\267\125\206\035\131\242\104\013\312" +
"\163\033\311\012\210\107\166\364\016\114\276\264\120\056\206\125" +
"\037\111\227\216\264\351\214\244\215\161\322\320\212\331\176\064" +
"\320\345\047\033\104\243\160\166\102\375\147\250\071\214\102\305" +
"\070\161\346\121\304\274\000\276\260\262\310\211\054\064\124\326" +
"\304\273\027\310\142\372\322\244\134\244\045\343\240\167\232\334" +
"\137\306\367\340\174\173\175\005\141\102\061\014\000\275\371\026" +
"\006\345\043\026\162\137\103\125\026\133\034\215\275\114\136\253" +
"\204\176\201\344\265\354\244\245\047\151\365\020\255\350\323\340" +
"\261\303\140\166\026\111\002\252\362\232\225\340\077\274\321\243" +
"\142\074\354\110\106\113\132\070\233\343\035\075\232\240\046\026" +
"\141\364\167\272\071\226\277\203\222\357\130\203\376\034\304\310" +
"\073\154\354\376\324\222\222\075\062\132\334\352\332\234\304\237" +
"\023\070\013\277\327\045\071\200\376\010\240\366\030\306\364\100" +
"\217\261\300\051\230\152\234\156\077\137\306\250\254\205\053\200" +
"\143\003\243\016\365\237\343\250\207\075\373\320\263\017\300\056" +
"\223\266\246\322\163\064\162\371\056\356\155\067\155\130\223\173" +
"\330\073\046\344\231\203\132\161\244\233\166\007\266\356\272\221" +
"\353\012\042\012\003\303\004\347\304\340\065\036\057\156\005\107" +
"\017\206\332\074\160\365\206\234\331\346\157\005\307\122\133\151" +
"\217\203\262\362\247\341\123\015\237\327\076\363\043\141\060\271" +
"\027\203\031\271\025\132\124\301\167\003\372\027\030\107\373\141" +
"\345\115\215\261\060\230\043\364\173\066\034\054\355\171\063\344" +
"\314\311\305\045\154\146\157\350\346\227\355\321\023\155\152\126" +
"\132\334\300\322\361\225\355\200\131\161\331\017\254\051\305\242" +
"\047\114\065\274\041\066\356\247\146\220\261\226\150\016\036\227" +
"\226\034\254\270\036\014\266\111\141\100\151\001\312\067\040\157" +
"\205\245\303\110\212\266\066\035\132\165\070\141\125\215\067\110" +
"\117\315\026\037\003\073\002\175\073\027\272\355\275\354\262\264" +
"\111\315\234\250\123\132\366\027\313\066\023\207\013\305\053\201" +
"\123\251\250\271\150\113\107\337\160\103\154\034\221\264\131\305" +
"\224\041\027\216\264\276\346\235\254\235\365\242\256\106\257\155" +
"\134\237\015\156\254\261\260\036\160\332\312\030\356\255\071\260" +
"\161\227\031\165\167\372\351\200\004\174\162\017\227\216\050\201" +
"\217\257\243\265\353\251\271\373\306\107\150\004\153\034\157\247" +
"\210\221\325\210\152\165\275\017\004\076\304\270\022\265\320\275" +
"\206\041\354\007\361\020\353\313\277\264\030\137\016\115\154\054" +
"\341\236\200\201\026\356\164\263\063\042\220\043\275\111\116\341" +
"\023\056\234\231\376\117\063\324\100\026\064\364\362\211\154\110" +
"\243\330\052\216\021\150\152\002\073\107\230\315\265\161\060\305" +
"\347\334\255\063\130\104\366\373\014\141\231\245\121\052\063\030" +
"\057\053\301\073\150\031\363\275\116\343\124\326\220\041\112\025" +
"\311\245\200\036\042\356\237\231\251\331\153\146\247\205\336\250" +
"\117\375\373\175\066\330\161\266\263\003\116\237\231\273\175\367" +
"\221\064\170\335\234\030\121\116\045\313\075\246\353\335\322\143" +
"\240\201\377\003\236\333\331\161\356\011\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\155\125\315\156\343\066" +
"\020\326\046\033\333\362\217\354\070\101\157\275\365\274\057\320" +
"\123\326\151\264\001\334\044\225\067\355\241\300\012\224\304\070" +
"\154\044\122\113\121\033\007\175\245\026\350\263\364\045\212\036" +
"\372\016\235\341\217\104\041\173\261\277\371\064\234\177\016\377" +
"\370\057\070\151\145\160\366\353\366\067\362\205\274\053\011\337" +
"\277\333\051\311\370\376\373\077\377\376\371\237\177\277\375\075" +
"\076\012\202\103\035\004\301\137\052\170\363\235\012\042\322\064" +
"\042\127\057\065\155\322\247\347\102\005\153\242\340\100\326\052" +
"\232\026\064\057\015\171\106\332\103\232\225\042\177\112\363\122" +
"\064\324\251\166\254\250\051\067\344\161\106\244\012\146\360\053" +
"\031\225\126\061\177\170\175\372\264\043\373\303\141\376\110\044" +
"\311\025\225\210\113\210\315\360\347\271\050\350\153\023\147\036" +
"\335\033\031\151\126\251\340\044\027\245\340\150\011\377\235\007" +
"\121\125\304\340\267\005\151\036\125\260\052\130\103\252\214\355" +
"\133\242\254\341\061\155\162\122\123\100\323\275\240\215\022\326" +
"\362\136\031\060\145\173\056\244\125\036\061\153\373\234\161\246" +
"\276\022\245\107\367\121\056\113\362\042\132\345\025\171\124\146" +
"\220\073\165\340\011\116\226\364\153\145\357\131\057\351\262\046" +
"\222\162\004\066\306\011\047\025\224\101\200\235\061\167\351\037" +
"\163\001\205\131\202\156\003\315\351\175\277\255\313\266\121\301" +
"\242\226\064\367\350\231\226\171\133\145\330\221\010\045\132\120" +
"\236\333\110\226\100\074\260\203\167\140\362\271\205\162\061\254" +
"\372\110\272\164\244\115\147\044\155\214\223\206\126\314\366\243" +
"\201\056\077\332\040\032\205\263\023\352\077\103\315\141\024\052" +
"\306\211\063\217\042\346\005\360\231\225\105\116\144\241\241\262" +
"\046\216\236\041\213\351\163\223\162\221\226\214\203\336\233\117" +
"\340\170\163\165\011\041\102\041\014\000\235\371\006\206\344\075" +
"\026\361\266\206\212\054\066\070\026\267\162\367\122\355\350\147" +
"\110\134\313\116\212\074\111\253\207\150\105\237\006\157\035\006" +
"\263\263\130\022\120\225\127\254\004\337\341\265\036\023\343\141" +
"\113\062\132\322\302\331\034\157\351\301\004\065\261\010\043\277" +
"\323\215\261\374\035\224\173\313\032\364\347\040\106\336\141\143" +
"\367\247\226\224\354\201\321\342\106\327\345\070\371\260\203\263" +
"\360\173\125\222\075\350\217\000\152\217\141\102\367\364\220\010" +
"\234\200\251\306\351\346\303\105\202\312\132\270\004\070\066\060" +
"\356\120\377\071\211\173\330\263\367\075\173\017\154\264\153\153" +
"\052\075\107\043\227\357\342\243\355\244\015\153\362\021\166\216" +
"\011\171\346\240\126\074\372\004\035\213\356\272\121\353\212\041" +
"\012\003\303\035\316\207\301\053\074\132\334\010\216\326\015\265" +
"\276\347\352\025\071\263\215\337\010\216\145\266\322\055\016\310" +
"\322\237\204\037\152\370\274\362\231\037\011\203\211\075\037\314" +
"\307\215\320\242\012\276\031\320\277\300\030\332\017\113\157\142" +
"\214\205\301\014\241\337\323\341\120\151\317\353\041\147\116\056" +
"\056\140\043\173\003\067\277\150\017\236\150\123\263\322\342\032" +
"\226\215\257\154\207\313\212\121\077\254\246\024\213\236\060\325" +
"\360\006\330\270\237\232\041\306\132\242\071\170\122\132\262\267" +
"\342\152\060\324\046\205\001\245\005\050\337\200\274\021\226\016" +
"\143\051\332\332\164\150\331\341\035\253\152\274\075\172\142\066" +
"\370\010\330\021\350\333\271\320\155\357\145\227\245\115\152\346" +
"\104\235\122\324\137\052\333\114\034\056\024\057\005\116\244\242" +
"\346\222\105\216\276\346\206\130\073\142\327\146\025\123\206\134" +
"\070\322\372\232\167\262\166\326\213\272\032\275\266\161\175\072" +
"\270\255\306\302\152\300\151\053\143\270\263\346\300\332\135\144" +
"\324\335\352\047\003\022\360\311\133\270\160\104\011\174\164\035" +
"\255\135\117\315\275\067\076\102\043\130\343\170\063\105\202\254" +
"\106\124\253\353\135\040\360\001\306\165\250\205\356\025\014\141" +
"\067\210\373\104\137\374\310\142\174\061\064\261\266\204\133\375" +
"\003\055\334\345\146\137\304\040\307\172\213\234\300\047\134\066" +
"\063\375\237\146\250\201\054\150\350\305\023\333\220\106\211\125" +
"\034\043\320\324\004\366\215\060\133\153\355\140\212\317\270\133" +
"\145\260\204\354\367\031\302\062\113\343\124\146\060\136\126\202" +
"\367\317\062\346\173\235\046\251\254\041\103\224\052\222\113\001" +
"\075\104\334\077\057\123\263\323\314\076\013\275\121\237\372\367" +
"\373\164\260\337\154\147\007\234\076\063\167\273\356\075\151\360" +
"\272\071\061\246\234\112\226\173\114\327\273\310\143\240\201\377" +
"\003\135\362\354\010\344\011\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\066\060\014\163\240\060\310\260\003\003\003" +
"\023\020\263\040\141\126\050\015\022\147\164\200\250\141\206\362" +
"\231\241\030\046\316\010\125\313\214\206\231\220\344\140\152\031" +
"\220\314\345\002\142\116\250\132\016\050\037\246\007\335\075\350" +
"\366\061\043\321\254\120\232\001\115\034\146\026\314\074\154\156" +
"\103\067\027\233\371\060\373\131\221\314\142\100\062\023\075\014" +
"\030\001\172\251\027\057\333\002\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\165\303\311\011\302\120" +
"\030\205\321\113\300\076\134\131\203\363\074\105\215\103\214\143" +
"\066\226\040\202\142\103\331\145\345\322\222\004\173\360\027\276" +
"\305\103\236\007\116\376\126\341\172\121\220\206\321\363\124\272" +
"\275\036\131\040\335\317\222\222\242\124\266\025\317\052\153\254" +
"\263\301\046\133\236\155\166\154\227\075\147\337\163\300\241\035" +
"\071\307\014\071\341\324\316\234\021\347\134\160\371\163\145\143" +
"\256\231\160\303\355\237\073\273\347\201\107\246\337\037\363\232" +
"\343\147\153\001\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\235\347\327\045\105" +
"\021\306\237\003\142\316\011\061\226\011\163\316\212\332\146\127" +
"\105\314\151\015\153\026\214\054\301\005\023\101\101\024\005\301" +
"\034\021\130\100\135\105\061\363\367\370\311\163\374\354\127\147" +
"\160\356\171\347\355\355\236\356\256\016\125\175\347\376\316\231" +
"\323\167\172\252\253\036\152\233\176\373\116\117\317\075\366\037" +
"\234\164\336\141\234\170\360\340\201\223\377\373\257\063\116\271" +
"\352\212\323\116\000\216\174\001\300\355\103\375\011\007\017\234" +
"\176\373\241\123\317\377\367\155\107\067\325\357\307\216\246\234" +
"\167\066\276\212\041\373\274\314\023\356\150\273\065\020\160\142" +
"\253\130\341\314\017\152\356\324\112\315\232\330\313\374\041\151" +
"\051\053\043\157\264\251\011\001\047\111\153\030\041\340\316\065" +
"\374\352\312\074\001\167\221\326\320\212\343\063\117\300\135\247" +
"\362\156\163\313\341\374\356\271\321\006\037\367\160\324\335\063" +
"\327\157\056\203\206\173\015\307\275\133\306\074\076\363\006\270" +
"\150\136\332\365\071\270\174\224\360\333\043\352\106\233\373\110" +
"\153\010\101\300\175\063\333\337\157\054\263\347\363\367\317\121" +
"\261\146\324\365\371\007\014\307\003\075\327\036\024\321\376\301" +
"\201\353\047\017\307\103\322\225\225\047\273\317\237\122\126\317" +
"\172\110\317\074\001\017\015\134\177\230\165\376\160\216\062\056" +
"\146\370\217\312\151\117\300\043\246\362\221\303\361\250\174\105" +
"\156\366\062\157\200\303\123\304\265\316\052\251\145\314\175\231" +
"\077\247\145\344\265\123\356\057\254\001\256\210\251\133\152\237" +
"\142\037\202\200\107\227\362\125\203\242\231\077\026\123\347\203" +
"\200\307\244\330\367\216\272\131\345\143\245\065\264\302\235\171" +
"\002\036\047\243\147\075\324\353\363\004\074\276\264\317\155\102" +
"\327\150\123\002\002\116\225\326\020\303\376\314\023\360\004\131" +
"\075\353\141\373\372\374\034\002\236\050\255\301\107\134\346\015" +
"\360\321\026\152\250\315\035\263\047\245\053\053\117\134\346\011" +
"\170\162\033\075\353\241\335\150\103\300\123\152\307\350\011\135" +
"\343\074\001\117\155\034\357\151\055\343\315\121\227\371\247\113" +
"\153\150\205\256\314\227\300\000\347\112\153\210\141\373\062\117" +
"\300\063\244\065\304\040\273\046\145\200\257\305\306\215\305\224" +
"\135\223\172\146\276\042\067\262\175\236\200\147\111\304\325\300" +
"\276\065\251\263\245\325\254\011\135\343\074\365\361\244\323\263" +
"\113\370\331\145\076\025\312\177\306\354\071\303\361\134\135\231" +
"\347\100\300\363\244\065\160\330\067\316\177\166\254\261\313\015" +
"\366\071\007\227\217\022\176\173\144\271\317\033\340\174\137\313" +
"\245\153\113\054\265\043\340\371\251\161\270\072\226\030\164\274" +
"\040\160\375\205\123\371\042\156\014\135\243\015\001\057\156\034" +
"\357\045\214\066\271\343\374\151\143\251\056\363\057\155\030\353" +
"\145\255\142\271\120\227\371\227\113\153\150\105\364\232\324\107" +
"\332\350\131\017\373\346\066\237\033\153\354\162\203\175\316\301" +
"\345\243\204\337\036\331\227\371\317\217\065\166\271\301\076\347" +
"\340\362\121\302\157\217\224\033\347\351\216\044\266\145\010\170" +
"\346\170\264\216\133\002\135\177\141\071\020\360\012\151\015\034" +
"\312\144\236\200\127\226\321\263\036\164\365\171\002\136\045\255" +
"\041\004\345\177\223\172\365\130\312\146\336\140\210\276\122\366" +
"\315\155\306\067\014\301\056\067\330\347\034\134\076\112\370\355" +
"\221\242\173\106\276\031\123\267\324\076\305\276\167\212\316\052" +
"\137\143\327\031\340\326\134\277\333\112\335\161\236\200\327\326" +
"\360\273\015\354\033\347\077\063\326\330\345\006\373\234\203\313" +
"\107\011\277\075\222\277\062\102\300\353\122\042\056\371\044\075" +
"\053\043\257\057\355\323\046\230\371\217\371\132\022\160\200\023" +
"\161\311\047\307\076\325\337\010\001\157\110\155\123\232\345\314" +
"\023\360\306\266\172\322\061\300\125\322\032\070\350\372\016\313" +
"\201\200\067\111\153\340\240\347\136\245\141\356\276\067\236\173" +
"\225\264\232\335\367\122\020\160\272\264\006\016\272\062\117\355" +
"\367\214\274\271\145\274\071\105\107\233\063\012\370\170\113\152" +
"\033\003\334\222\033\267\005\004\274\165\176\256\253\317\163\040" +
"\340\155\322\032\070\154\105\346\337\056\255\201\203\256\314\123" +
"\373\161\376\035\055\343\315\111\317\274\001\276\224\162\175\311" +
"\076\344\213\103\015\237\065\320\321\347\011\170\247\144\174\011" +
"\252\276\337\346\135\051\366\146\213\337\143\106\300\273\355\072" +
"\035\175\176\003\001\357\221\326\320\012\165\231\137\313\156\235" +
"\367\026\135\207\375\175\114\135\252\217\155\105\135\237\177\237" +
"\264\206\126\004\127\106\216\370\132\056\135\133\042\265\135\310" +
"\236\253\103\232\354\167\161\037\054\253\147\075\004\373\374\167" +
"\112\107\244\304\177\345\301\376\003\113\327\115\005\215\061\020" +
"\360\301\234\366\273\275\201\036\035\261\173\003\077\304\215\021" +
"\275\133\047\353\337\067\226\061\216\057\126\214\206\030\033\055" +
"\104\147\376\303\055\324\214\161\174\261\142\064\304\330\150\101" +
"\327\254\222\003\001\207\244\065\160\320\225\171\152\177\227\130" +
"\354\377\021\165\231\337\346\325\300\175\073\133\165\145\236\203" +
"\001\076\041\255\201\103\364\137\330\305\071\165\051\306\070\276" +
"\130\061\032\142\154\264\020\235\171\366\274\065\205\061\216\057" +
"\126\214\206\030\033\055\364\075\332\030\340\123\343\041\255\203" +
"\203\256\314\123\373\271\115\223\167\214\273\320\225\171\056\304" +
"\170\222\273\140\354\217\163\332\361\063\157\200\313\102\066\064" +
"\315\073\206\362\223\021\266\301\121\303\025\063\106\107\056\004" +
"\234\071\034\147\015\307\247\113\371\254\272\002\276\312\135\070" +
"\261\324\035\155\014\360\207\032\176\267\201\335\135\142\217\216" +
"\352\357\023\344\365\171\132\351\333\200\112\242\143\156\103\053" +
"\174\273\220\216\314\157\240\025\275\003\041\070\316\177\253\255" +
"\236\164\114\007\032\135\350\352\363\034\014\360\135\151\015\034" +
"\242\357\230\035\152\241\146\214\343\213\025\243\041\306\106\013" +
"\133\321\347\203\337\217\065\262\025\231\377\215\264\006\016\175" +
"\147\236\072\376\205\216\264\314\023\160\070\322\356\234\010\233" +
"\163\147\237\163\177\017\347\270\357\261\103\335\027\163\174\046" +
"\306\077\062\034\027\114\237\057\264\256\071\167\017\145\335\253" +
"\274\071\326\046\305\066\325\046\246\235\106\164\214\066\004\174" +
"\131\062\276\004\152\062\377\025\311\370\022\250\311\374\352\336" +
"\133\231\065\316\177\143\176\116\025\176\211\056\024\323\127\327" +
"\003\131\231\277\274\274\236\364\230\022\072\112\300\317\074\061" +
"\337\143\226\203\161\254\164\273\352\102\020\160\121\276\232\074" +
"\164\214\363\271\220\354\263\007\027\163\332\005\357\022\173\277" +
"\215\054\135\133\042\265\135\310\236\253\103\232\140\346\131\317" +
"\222\054\221\352\163\156\357\152\233\352\117\013\272\106\033\112" +
"\174\306\314\000\147\145\306\273\144\070\056\315\361\301\145\267" +
"\067\220\013\001\137\317\151\237\065\267\131\234\107\123\203\147" +
"\277\172\046\330\347\257\131\072\367\331\207\354\154\033\237\275" +
"\253\176\036\303\365\071\126\253\064\301\314\137\273\164\356\263" +
"\017\331\331\066\076\173\127\375\074\206\353\163\254\126\151\362" +
"\357\317\223\343\073\044\105\334\237\017\304\351\352\267\027\150" +
"\172\043\022\131\317\101\014\347\337\366\265\331\216\225\021\217" +
"\177\251\225\221\053\255\153\345\127\106\276\027\153\223\142\233" +
"\152\023\323\116\043\301\161\376\246\245\163\037\061\166\163\033" +
"\237\275\253\336\325\156\054\155\333\030\015\222\004\063\377\353" +
"\245\163\037\061\166\163\033\237\275\253\336\325\156\054\155\333" +
"\030\015\222\004\337\374\057\362\055\045\026\003\374\144\136\366" +
"\204\256\273\007\134\250\303\047\374\202\243\315\057\347\347\324" +
"\170\156\103\216\137\262\030\352\256\266\365\215\045\131\177\151" +
"\251\375\334\346\032\253\356\332\251\054\074\267\251\001\311\334" +
"\061\373\176\216\017\056\273\314\017\307\017\162\174\160\331\145" +
"\176\070\176\230\343\203\113\365\137\257\053\272\037\126\012\372" +
"\377\176\330\037\225\364\251\253\317\207\040\340\307\166\235\001" +
"\176\327\136\011\017\232\315\176\167\173\300\143\240\275\075\340" +
"\077\055\345\263\257\076\157\143\012\356\206\157\115\337\231\047" +
"\340\147\322\032\270\004\277\111\135\351\252\327\204\351\100\243" +
"\213\354\367\022\377\274\254\236\365\240\153\264\041\340\027\322" +
"\032\132\241\053\363\245\040\353\176\223\106\344\126\003\267\011" +
"\332\133\207\375\225\125\137\150\035\326\130\236\035\012\256\213" +
"\261\053\101\213\030\065\351\167\264\041\345\153\116\041\372\035" +
"\155\214\242\171\025\355\215\066\327\117\345\015\123\131\150\264" +
"\321\006\001\067\112\153\340\262\173\336\246\120\254\371\363\066" +
"\107\255\153\345\237\267\271\072\326\046\305\066\325\046\246\235" +
"\106\164\257\303\206\060\263\165\130\207\177\351\165\330\233\246" +
"\222\321\347\315\056\363\121\220\073\363\067\117\345\056\363\325" +
"\040\167\346\157\231\112\136\346\125\337\107\061\223\076\243\134" +
"\247\213\140\346\105\236\210\210\305\114\372\214\162\235\056\272" +
"\237\317\167\371\076\247\221\356\063\377\133\151\015\134\272\317" +
"\174\067\317\035\330\144\175\223\312\332\225\310\301\025\123\102" +
"\107\011\164\365\171\002\216\111\153\150\205\256\314\163\240\116" +
"\177\301\172\265\157\131\021\377\105\202\335\356\173\056\004\334" +
"\232\323\236\375\346\377\077\346\104\335\261\045\343\374\237\244" +
"\065\160\310\317\074\001\267\225\323\263\036\372\355\363\004\374" +
"\131\132\103\016\375\256\200\153\202\200\277\244\266\011\316\155" +
"\256\137\072\367\021\143\067\267\361\331\273\352\135\355\306\322" +
"\266\215\321\040\111\277\243\215\351\370\011\356\221\256\063\177" +
"\334\316\235\236\010\216\066\067\056\235\373\210\261\233\333\370" +
"\354\135\365\256\166\143\151\333\306\150\220\244\337\076\077\142" +
"\246\175\113\246\340\376\245\126\004\373\274\310\056\335\130\314" +
"\244\317\050\327\351\042\230\171\221\135\272\261\230\111\237\121" +
"\256\323\105\060\363\105\167\337\226\306\114\372\214\162\235\056" +
"\262\326\244\056\136\072\257\201\053\106\213\270\065\310\312\374" +
"\045\113\347\065\160\305\150\021\267\006\131\231\277\164\351\274" +
"\006\256\030\055\342\326\240\257\131\045\365\377\336\203\277\156" +
"\076\247\147\336\004\176\201\310\276\276\144\037\362\305\241\206" +
"\317\032\354\372\174\113\010\370\333\346\163\160\126\351\335\347" +
"\243\005\323\201\106\027\175\365\171\027\004\374\135\132\003\007" +
"\326\070\177\141\312\365\045\373\220\057\016\065\174\326\240\337" +
"\076\117\300\077\244\065\344\020\034\347\157\130\072\367\021\143" +
"\067\267\361\331\273\352\135\355\306\322\266\215\321\040\111\362" +
"\356\373\353\112\332\345\320\042\106\115\372\035\155\066\230\116" +
"\237\241\357\077\363\004\374\123\132\003\007\326\334\346\202\224" +
"\353\113\366\041\137\034\152\370\254\101\360\057\354\321\245\163" +
"\037\061\166\163\033\237\275\253\336\325\156\054\155\333\030\015" +
"\222\260\372\374\342\357\027\333\327\227\354\103\276\070\324\360" +
"\131\203\377\001\172\041\065\310\315\022\001\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\255\226\275\116\343\120" +
"\020\205\307\216\101\026\225\331\135\012\272\113\107\105\105\203" +
"\150\256\256\122\255\266\210\104\231\046\040\121\004\001\012\304" +
"\101\251\220\313\255\126\110\373\002\051\266\133\032\244\245\241" +
"\342\015\170\022\036\001\011\333\100\263\314\167\321\130\130\212" +
"\043\345\144\176\356\234\063\307\276\176\224\245\331\271\174\035" +
"\376\070\332\277\330\337\232\225\343\343\255\060\056\367\016\313" +
"\335\223\365\315\354\341\127\177\222\212\314\047\042\162\067\075" +
"\227\342\377\177\235\076\375\274\334\331\376\273\321\223\144\050" +
"\331\301\270\234\226\222\016\277\317\047\165\322\346\333\145\267" +
"\137\156\146\277\337\162\044\362\172\315\247\147\162\051\351\254" +
"\271\057\265\200\157\176\166\004\210\047\040\321\200\202\152\244" +
"\130\074\110\362\315\004\160\052\375\034\041\131\066\106\364\353" +
"\173\077\303\166\327\014\043\031\110\117\066\364\342\010\304\016" +
"\050\271\016\024\132\104\077\312\271\244\357\201\021\105\274\014" +
"\261\242\124\072\220\130\305\000\000\236\043\006\070\142\320\105" +
"\106\242\212\332\332\256\367\074\166\220\217\017\231\024\356\123" +
"\212\307\345\243\023\205\242\006\140\324\162\153\073\171\135\333" +
"\330\056\022\105\355\342\106\011\003\316\116\324\100\043\312\173" +
"\140\260\075\072\002\112\361\201\357\231\247\213\112\114\255\251" +
"\314\065\142\253\126\351\007\254\211\122\375\252\233\332\215\056" +
"\212\104\265\074\121\204\316\040\154\055\232\045\273\150\047\152" +
"\155\014\166\350\152\144\355\252\252\050\042\044\262\050\140\007" +
"\101\014\264\234\154\226\365\225\133\224\370\201\205\133\266\266" +
"\003\265\010\070\000\212\112\256\362\324\070\135\072\271\031\010" +
"\365\135\255\021\132\012\055\000\252\004\227\063\320\016\272\330" +
"\236\253\021\115\123\132\127\253\377\334\325\112\252\104\040\320" +
"\250\055\017\032\121\002\104\335\023\121\253\177\134\035\241\025" +
"\047\100\026\040\121\241\351\142\052\036\242\035\250\077\013\222" +
"\217\321\031\150\015\030\300\255\155\177\267\030\100\056\120\243" +
"\271\124\152\033\322\127\010\320\123\025\224\012\015\300\254\253" +
"\142\041\072\347\166\200\005\107\100\007\006\021\100\057\261\277" +
"\205\143\252\230\145\350\017\026\252\321\136\336\044\006\322\025" +
"\033\100\114\127\052\360\201\227\150\251\314\234\263\030\142\317" +
"\017\073\240\266\313\357\242\350\076\236\332\045\152\261\106\207" +
"\261\223\001\330\337\031\260\053\054\376\026\361\014\334\371\113" +
"\125\212\022\000\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\256\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\025\054\341\060\052\061\052\061\052\061\052\061\052\061\052" +
"\061\052\061\052\101\211\104\303\240\164\325\160\221\140\030\224" +
"\256\032\056\022\070\103\227\212\022\007\006\322\162\234\022\203" +
"\063\076\006\124\142\100\343\203\164\211\003\203\322\125\203\063" +
"\152\351\041\061\240\301\076\070\045\006\147\104\121\121\142\160" +
"\206\025\101\073\000\360\047\072\334\212\022\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\256\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\165\124\142\124\142\124\142\124\142\124\142\124\142\124\142" +
"\124\142\124\142\124\142\124\142\124\142\124\142\124\142\124\142" +
"\124\142\124\142\004\110\000\000\235\170\015\335\212\022\000\000" +
""
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\314\241\016\001\001" +
"\034\300\341\377\156\074\001\345\336\100\272\240\260\151\046\152" +
"\342\245\013\302\031\333\261\073\223\074\202\047\121\124\057\141" +
"\223\145\215\147\240\150\222\211\337\057\377\366\035\037\321\156" +
"\066\321\315\363\351\242\330\026\131\123\227\313\154\134\326\263" +
"\171\075\112\257\317\323\340\176\353\047\021\273\052\042\316\357" +
"\261\363\345\133\245\275\326\345\060\251\076\337\260\372\261\146" +
"\035\373\110\010\004\002\201\100\040\020\010\004\002\201\100\040" +
"\020\010\004\002\201\100\040\020\010\004\002\201\100\040\020\010" +
"\004\002\201\100\040\020\010\004\002\201\100\040\374\127\170\001" +
"\175\276\210\333\077\060\000\000"
});

public static final byte[] terminalUsesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\054\030\206\071\000\000\246\020\136\373\373" +
"\000\000\000"
});

public static final byte[] shiftableUnionHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\270" +
"\210\101\040\053\261\054\121\257\264\044\063\107\317\051\263\044" +
"\070\265\044\357\157\107\235\245\311\152\105\146\006\306\150\006" +
"\226\244\314\222\342\022\006\246\150\257\212\202\322\042\060\255" +
"\300\262\125\150\143\351\144\046\006\206\212\002\006\006\006\106" +
"\206\377\337\353\277\377\377\377\277\002\000\061\006\173\362\121" +
"\000\000\000"
});

public static final byte[] acceptSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\345\225\261\112\304\100" +
"\020\206\147\327\075\211\142\261\150\243\335\172\130\134\165\225" +
"\215\330\054\142\041\142\167\345\125\047\130\234\250\304\113\042" +
"\251\216\224\126\066\076\206\066\026\366\207\215\351\174\022\037" +
"\101\060\011\332\310\376\201\211\233\073\301\055\022\302\227\371" +
"\147\166\366\037\366\361\235\072\311\204\066\206\047\347\243\233" +
"\121\077\211\307\027\375\203\161\074\070\213\367\057\267\172\352" +
"\355\356\060\224\104\151\110\104\257\321\204\364\317\277\256\076" +
"\156\247\173\273\017\333\113\044\206\244\116\307\161\024\223\034" +
"\036\247\141\041\132\276\215\172\136\177\112\356\277\065\050\215" +
"\256\151\112\062\051\237\235\342\133\120\265\214\003\004\101\361" +
"\352\272\000\101\220\341\010\003\245\244\033\150\040\045\121\016" +
"\010\212\262\120\162\311\336\007\165\215\003\050\330\022\156\271" +
"\233\270\273\120\312\300\162\003\047\310\141\016\315\117\136\343" +
"\022\301\224\012\334\140\245\101\113\004\000\031\133\312\202\210" +
"\152\275\160\244\162\070\152\246\121\333\235\122\156\300\226\200" +
"\021\144\331\021\155\003\211\366\001\101\306\216\220\224\261\043" +
"\234\240\132\232\343\016\154\033\234\234\112\117\061\100\271\002" +
"\137\335\005\345\036\261\367\361\125\026\343\322\342\203\206\033" +
"\134\234\333\075\202\145\177\055\231\007\200\047\310\003\136\074" +
"\013\201\305\355\256\163\071\033\350\177\154\346\014\000\361\367" +
"\074\133\002\351\355\074\130\366\007\367\306\257\006\245\231\127" +
"\361\004\271\162\300\011\262\265\063\307\277\131\130\021\275\372" +
"\023\123\010\004\010\350\226\007\305\362\245\014\002\026\000\217" +
"\266\347\202\125\346\004\265\153\146\000\034\177\316\330\332\174" +
"\167\127\140\307\335\033\201\316\130\172\163\036\004\065\345\332" +
"\226\257\263\034\201\031\002\174\023\064\071\050\015\172\045\161" +
"\167\125\223\141\134\320\165\266\066\217\252\204\327\034\252\000" +
"\237\127\367\303\067\227\023\000\000"
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\361\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\120\121\134\310\120\307\300\124\012\042\131\107" +
"\005\106\005\106\005\106\005\106\005\106\005\106\005\106\005\106" +
"\005\106\005\106\005\106\005\106\005\000\144\317\162\054\227\015" +
"\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\225\277\112\303\120" +
"\024\306\117\156\123\271\226\016\101\027\005\207\153\021\351\124" +
"\104\134\304\045\210\103\021\207\202\143\027\053\212\124\124\152" +
"\233\110\247\222\321\311\305\307\320\305\301\275\233\335\174\001" +
"\237\100\360\021\374\163\323\342\242\347\273\172\103\322\072\230" +
"\241\241\375\365\073\347\334\323\357\113\157\137\050\037\266\151" +
"\266\276\163\334\270\150\124\302\240\171\122\331\154\006\273\207" +
"\301\306\351\174\331\175\274\332\152\011\242\156\213\210\036\072" +
"\155\362\276\176\353\354\365\262\267\276\166\263\230\043\247\116" +
"\356\176\063\350\004\044\352\333\335\226\056\032\337\225\173\077" +
"\163\027\136\177\326\240\156\347\234\172\044\302\370\065\257\337" +
"\073\364\076\274\336\276\003\051\365\255\304\050\342\317\001\210" +
"\260\102\261\100\324\216\050\367\304\052\074\120\112\240\036\123" +
"\032\010\266\124\004\247\022\326\347\240\222\142\200\013\127\042" +
"\320\124\202\372\234\142\016\156\327\201\047\127\160\134\111\253" +
"\014\030\300\036\036\000\105\274\166\335\004\001\307\162\045\222" +
"\357\061\215\306\055\340\251\034\036\014\042\303\017\345\262\300" +
"\007\212\370\362\236\321\001\227\321\332\025\247\120\240\107\331" +
"\264\166\276\024\002\144\255\220\043\015\002\076\002\013\054\310" +
"\361\245\104\015\366\070\300\315\235\010\254\235\077\207\176\062" +
"\050\326\160\021\122\010\244\020\024\041\205\317\002\007\114\065" +
"\274\126\154\134\002\101\021\237\234\142\157\131\000\302\012\260" +
"\222\002\152\016\307\255\142\303\351\014\302\135\111\033\123\033" +
"\201\007\017\270\204\062\210\363\341\042\060\147\023\003\362\100" +
"\014\204\017\363\341\042\260\147\033\034\230\017\103\014\354\334" +
"\156\260\150\152\276\262\007\175\153\005\364\056\004\176\062\267" +
"\377\233\372\367\246\046\326\324\303\233\245\251\377\250\333\271" +
"\223\117\064\006\175\064\025\054\145\017\252\250\207\157\004\031" +
"\147\060\365\104\111\004\274\164\242\006\166\045\051\101\006\025" +
"\002\076\000\023\214\232\175\242\306\141\152\063\110\247\271\275" +
"\333\241\251\345\050\013\054\020\251\071\021\032\056\173\227\230" +
"\037\257\031\273\044\311\017\065\206\355\046\372\127\303\276\312" +
"\272\007\052\225\246\342\047\360\001\121\023\022\136\307\023\000" +
"\000"
});

public static final byte[] cMapHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\321\071\112\104\101" +
"\024\005\320\352\326\166\236\147\333\241\035\333\171\334\205\201" +
"\053\060\161\011\042\050\156\310\314\310\320\045\011\356\301\053" +
"\374\240\003\203\157\240\201\234\202\003\365\056\217\172\024\357" +
"\345\243\264\036\356\113\363\346\352\372\355\266\373\370\376\372" +
"\334\054\345\351\256\064\312\327\351\253\241\035\347\065\173\177" +
"\252\375\115\066\320\163\037\211\213\330\215\305\350\304\150\314" +
"\305\141\364\307\130\015\247\161\034\007\221\377\227\126\114\126" +
"\063\166\376\310\140\254\305\112\154\307\164\065\177\042\226\343" +
"\054\216\242\033\173\061\025\363\061\333\363\306\146\014\305\160" +
"\234\304\102\225\317\304\176\254\306\106\225\135\306\172\154\125" +
"\365\122\214\107\343\227\166\011\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\300\077\361\011\065\175\127\264\033\000\004\000" +
""
});

public static final byte[] deltaHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\233\351\163\024\105" +
"\030\306\247\100\020\021\024\020\304\170\042\102\040\211\347\046" +
"\136\020\017\024\320\050\021\020\105\044\162\203\342\015\110\040" +
"\152\134\221\250\334\240\002\312\245\106\016\101\100\344\126\221" +
"\033\321\177\304\117\126\361\231\257\364\126\165\247\246\146\272" +
"\267\347\350\356\171\373\235\236\252\247\172\347\331\171\167\347" +
"\227\267\247\317\315\376\113\136\227\346\005\136\347\246\246\206" +
"\376\227\377\033\133\261\156\105\175\047\317\153\231\347\171\336" +
"\277\304\357\324\324\320\170\142\172\345\242\377\017\357\142\166" +
"\255\207\350\150\236\357\025\075\102\326\101\125\172\335\231\352" +
"\052\242\056\104\135\211\256\046\352\106\164\015\121\167\242\153" +
"\211\172\020\365\244\376\165\264\054\305\135\117\324\213\250\067" +
"\365\372\020\335\100\324\227\236\367\043\272\221\250\077\321\115" +
"\324\253\240\345\315\264\274\205\226\267\322\262\244\333\210\156" +
"\047\272\203\150\000\321\235\324\037\110\164\027\321\040\242\301" +
"\104\225\041\252\041\034\015\025\370\120\124\105\313\152\346\071" +
"\052\260\312\053\125\015\107\167\013\174\246\173\070\336\275\276" +
"\327\367\111\342\375\272\077\306\265\114\245\343\001\277\227\223" +
"\134\371\257\056\004\250\012\076\317\377\176\041\360\176\201\343" +
"\005\143\144\361\345\374\340\173\125\264\144\124\005\353\251\152" +
"\143\123\341\254\201\070\251\352\044\124\165\061\276\031\016\025" +
"\316\134\341\244\112\323\006\076\050\211\221\305\227\363\203\357" +
"\245\157\331\037\052\163\127\017\013\356\014\026\225\253\201\376" +
"\367\037\161\271\062\236\053\133\250\036\005\101\065\114\123\256" +
"\206\243\312\225\235\063\221\172\311\167\313\133\213\307\070\172" +
"\134\340\103\321\023\264\254\146\236\253\201\231\325\300\047\045" +
"\337\355\372\053\254\124\272\153\340\210\062\361\345\174\127\003" +
"\141\122\075\305\361\236\116\105\225\246\006\216\224\304\310\342" +
"\135\015\224\347\152\024\321\350\200\236\341\170\220\364\154\320" +
"\003\327\013\067\110\076\127\135\015\254\021\370\111\345\146\042" +
"\152\236\053\125\065\360\071\111\214\054\136\177\015\304\225\053" +
"\046\325\317\225\171\252\322\141\133\313\036\122\116\162\025\167" +
"\247\031\202\102\367\355\250\224\353\371\300\371\230\314\250\032" +
"\001\344\306\114\256\136\010\234\217\105\101\065\116\363\135\217" +
"\317\204\312\204\136\114\105\345\357\007\240\254\007\252\035\007" +
"\362\250\046\130\117\205\363\367\026\161\153\340\113\126\344\052" +
"\077\317\325\313\050\251\262\310\325\304\324\124\245\043\037\363" +
"\053\234\273\335\065\250\250\222\076\127\257\160\274\064\317\225" +
"\332\326\102\127\015\124\055\267\306\204\265\015\204\322\137\225" +
"\312\111\145\142\365\364\302\257\032\240\112\327\132\114\346\250" +
"\111\340\233\320\153\021\256\231\102\313\251\314\013\121\115\343" +
"\150\272\300\207\242\031\264\254\146\236\065\317\125\111\063\005" +
"\261\321\306\201\326\037\041\252\131\034\211\174\050\252\242\345" +
"\154\346\345\210\152\016\072\252\044\255\305\353\236\231\326\302" +
"\154\057\234\264\015\174\303\030\225\311\365\330\271\212\076\047" +
"\164\337\040\126\244\337\264\206\352\055\203\177\025\063\124\157" +
"\033\044\062\233\053\225\172\007\005\325\273\050\162\365\136\312" +
"\170\071\325\373\032\357\176\236\246\317\225\123\045\351\205\347" +
"\173\311\172\141\173\307\026\013\120\122\351\316\325\007\050\251" +
"\130\256\026\046\246\152\006\114\005\047\127\213\254\242\132\014" +
"\056\127\070\327\003\171\121\055\326\123\175\310\321\107\002\077" +
"\053\175\034\070\157\245\345\047\314\013\121\025\211\076\015\150" +
"\011\307\063\245\317\222\304\345\244\006\342\134\147\347\105\055" +
"\005\160\347\351\250\332\210\076\017\350\013\216\007\121\243\331" +
"\153\045\275\360\227\136\270\267\204\325\013\363\126\174\227\011" +
"\174\050\312\363\356\001\276\134\101\236\137\055\027\304\232\237" +
"\211\254\120\110\225\274\265\120\261\346\263\122\321\347\250\133" +
"\143\132\245\361\333\127\147\106\145\362\157\274\006\045\125\134" +
"\255\105\107\265\116\222\253\257\254\244\022\351\353\110\271\372" +
"\106\343\035\254\327\364\271\162\252\254\377\366\216\052\072\025" +
"\344\021\323\006\101\054\254\265\333\215\061\251\222\217\230\124" +
"\120\175\253\051\127\331\122\251\256\201\337\201\241\332\344\162" +
"\205\212\152\263\061\252\026\140\332\022\341\232\142\320\163\073" +
"\075\026\123\015\341\310\376\377\351\161\073\075\366\324\300\042" +
"\060\155\115\022\027\242\332\306\121\121\340\233\320\367\021\256" +
"\231\102\313\037\230\227\023\252\037\071\152\027\370\131\351\247" +
"\300\171\053\055\267\063\057\104\265\324\102\265\321\262\206\171" +
"\334\275\106\133\325\161\377\041\252\035\034\265\011\174\050\232" +
"\101\313\331\314\313\011\125\271\231\310\116\117\355\114\304\334" +
"\374\152\026\107\042\037\212\354\336\153\334\225\070\127\220\251" +
"\222\327\300\254\327\141\335\212\164\336\251\176\326\370\355\273" +
"\135\256\174\332\003\206\352\027\203\324\166\346\312\156\252\275" +
"\050\251\160\346\112\037\025\344\021\323\076\101\154\136\307\201" +
"\020\251\366\243\244\342\345\352\127\245\124\007\200\120\341\314" +
"\125\074\252\337\054\320\301\300\171\173\360\232\020\125\273\005" +
"\072\044\273\046\322\072\373\141\201\157\367\072\373\021\201\157" +
"\317\072\073\316\075\021\234\065\060\353\275\105\075\173\215\274" +
"\265\321\145\002\037\212\344\153\267\070\237\053\336\236\303\121" +
"\201\017\105\351\366\104\124\214\230\216\105\214\167\343\100\267" +
"\316\156\217\314\120\035\107\111\025\127\277\243\242\372\303\130" +
"\256\154\153\003\377\364\362\333\262\103\244\072\221\011\325\137" +
"\340\163\225\365\057\275\365\374\362\073\213\226\135\177\033\210" +
"\342\310\311\374\012\047\025\157\126\326\046\360\241\310\334\254" +
"\361\244\044\306\315\257\322\267\201\131\337\241\243\212\116\005" +
"\161\034\030\345\271\072\345\245\031\007\236\006\112\005\173\314" +
"\176\006\034\325\131\227\053\107\025\201\352\034\112\052\270\271" +
"\072\217\222\112\224\253\013\231\120\375\255\231\012\147\256\354" +
"\242\272\210\222\052\152\256\376\101\111\025\073\127\127\000\265" +
"\356\171\206\203\246\000\000"
});

public static void initArrays()
throws java.io.IOException,java.lang.ClassNotFoundException
{
    symbolNames = (String[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(symbolNamesHash);
    symbolDisplayNames = (String[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(symbolDisplayNamesHash);
    symbolNumbers = (int[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(symbolNumbersHash);
    productionLHSs = (int[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(productionLHSsHash);
    parseTable = (int[][]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(parseTableHash);
    shiftableSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(shiftableSetsHash);
    layoutSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(layoutSetsHash);
    prefixSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(prefixSetsHash);
    prefixMaps = (java.util.BitSet[][]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(prefixMapsHash);
    terminalUses = (int[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(terminalUsesHash);
    shiftableUnion = (java.util.BitSet) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(shiftableUnionHash);
    acceptSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(acceptSetsHash);
    rejectSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(rejectSetsHash);
    possibleSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(possibleSetsHash);
    cmap = (int[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(cMapHash);
    delta = (int[][]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(deltaHash);
    }
    public CupSkinParser() {}
    
    public static void main(String[] args)
    {
        boolean useFile = false;
        String filename = "<stdin>";
        java.io.Reader reader = null;
        try
        {
	        int i;
    	    for(i = 0;i < args.length;i++)
        	{
        		if(args[i].charAt(0) != '-') break;
	        	else if(args[i].equals("-f"))
    	    	{
        			i++;
            	    if(i >= args.length) throw new edu.umn.cs.melt.copper.runtime.logging.CopperParserException("A filename must be provided with switch '-f'");
                    useFile = true;
                	filename = args[i];
 	               continue;
    	        }
        	}
            if(!useFile) reader = new java.io.InputStreamReader(System.in);
            else
    		{
    	        try
        	    {
                	reader = new java.io.FileReader(filename);
            	}
            	catch(java.io.FileNotFoundException ex)
            	{
              	  throw new edu.umn.cs.melt.copper.runtime.logging.CopperParserException("File not found: '" + filename + "'");
            	}
        	}
            edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine<ParserBean,edu.umn.cs.melt.copper.runtime.logging.CopperParserException> engine = new edu.umn.cs.melt.copper.compiletime.skins.cup.CupSkinParser();
            Object parseTree = engine.parse(reader,filename);
            engine.runPostParseCode(parseTree);
        }
        catch(java.lang.Exception ex)
        {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
		private static int TERMINAL_COUNT;
		private static int GRAMMAR_SYMBOL_COUNT;
		private static int SYMBOL_COUNT;
		private static int PARSER_STATE_COUNT;
		private static int SCANNER_STATE_COUNT;
		private static int DISAMBIG_GROUP_COUNT;
		
		private static int SCANNER_START_STATENUM;
		private static int PARSER_START_STATENUM;
		private static int EOF_SYMNUM;
		private static int EPS_SYMNUM;
		
		private static String[] symbolNames;
		private static String[] symbolDisplayNames;
		private static int[] symbolNumbers;
		private static int[] productionLHSs;
		
		private static int[][] parseTable;
		private static java.util.BitSet[] shiftableSets;
		private static java.util.BitSet[] layoutSets;
		private static java.util.BitSet[] prefixSets;
		private static java.util.BitSet[][] prefixMaps;
		private static int[] terminalUses;
		
		private static java.util.BitSet[] disambiguationGroups;
		
		private static java.util.BitSet shiftableUnion;
		
		private static java.util.BitSet[] acceptSets,rejectSets,possibleSets;
		
		private static int[][] delta;
		private static int[] cmap;
		
		public int getTERMINAL_COUNT() {
			return TERMINAL_COUNT;
		}
		public int getGRAMMAR_SYMBOL_COUNT() {
			return GRAMMAR_SYMBOL_COUNT;
		}
		public int getSYMBOL_COUNT() {
			return SYMBOL_COUNT;
		}
		public int getPARSER_STATE_COUNT() {
			return PARSER_STATE_COUNT;
		}
		public int getSCANNER_STATE_COUNT() {
			return SCANNER_STATE_COUNT;
		}
		public int getDISAMBIG_GROUP_COUNT() {
			return DISAMBIG_GROUP_COUNT;
		}
		public int getSCANNER_START_STATENUM() {
			return SCANNER_START_STATENUM;
		}
		public int getPARSER_START_STATENUM() {
			return PARSER_START_STATENUM;
		}
		public int getEOF_SYMNUM() {
			return EOF_SYMNUM;
		}
		public int getEPS_SYMNUM() {
			return EPS_SYMNUM;
		}
		public String[] getSymbolNames() {
			return symbolNames;
		}
		public String[] getSymbolDisplayNames() {
			return symbolDisplayNames;
		}
		public int[] getSymbolNumbers() {
			return symbolNumbers;
		}
		public int[] getProductionLHSs() {
			return productionLHSs;
		}
		public int[][] getParseTable() {
			return parseTable;
		}
		public java.util.BitSet[] getShiftableSets() {
			return shiftableSets;
		}
		public java.util.BitSet[] getLayoutSets() {
			return layoutSets;
		}
		public java.util.BitSet[] getPrefixSets() {
			return prefixSets;
		}
		public java.util.BitSet[][] getLayoutMaps() {
			return null;
		}
		public java.util.BitSet[][] getPrefixMaps() {
			return prefixMaps;
		}
		public int[] getTerminalUses() {
			return terminalUses;
		}
		public java.util.BitSet[] getDisambiguationGroups() {
			return disambiguationGroups;
		}
		public java.util.BitSet getShiftableUnion() {
			return shiftableUnion;
		}
		public java.util.BitSet[] getAcceptSets() {
			return acceptSets;
		}
		public java.util.BitSet[] getRejectSets() {
			return rejectSets;
		}
		public java.util.BitSet[] getPossibleSets() {
			return possibleSets;
		}
		public int[][] getDelta() {
			return delta;
		}
		public int[] getCmap() {
			return cmap;
		}	
    public ParserBean parse(java.io.Reader input,String inputName)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
    this.buffer = edu.umn.cs.melt.copper.runtime.io.ScannerBuffer.instantiate(input);
    setupEngine();
    startEngine(edu.umn.cs.melt.copper.runtime.io.InputPosition.initialPos(inputName));
    ParserBean parseTree = (ParserBean) runEngine();
    return parseTree;
    }

 public CompilerLogger logger; 
			private ParserBean currentParser;
			private Grammar currentGrammar;
			private TreeSet<CopperElementReference> ignoreTerminals;
			
			private int nextPrecedence = 0;
			private int nextProduction = 0;
			
			private CopperElementName newName(Token token)
			{
			   try { return CopperElementName.newName(token.lexeme); }
			   catch(ParseException ex)
			   {
			       logger.log(new GenericLocatedMessage(CompilerLevel.QUIET,token.location,"Unexpected element name -- bug in CUP skin parser",true,true));
			       return null;
			   }
			}
			
			private CopperElementReference newReference(Token token)
			{
			   try { return CopperElementReference.ref(currentGrammar.getName(),token.lexeme,token.location); }
			   catch(ParseException ex)
			   {
			       logger.log(new GenericLocatedMessage(CompilerLevel.QUIET,token.location,"Unexpected element name -- bug in CUP skin parser",true,true));
			       return null;
			   }
			}
			
			private static class Token
			{
				public String lexeme;
				public Location location;
				
				public Token(String lexeme,Location location)
				{
					this.lexeme = lexeme;
					this.location = location;
				}
			}
			
			private static class PrecLists
			{
				public LinkedList<CopperElementReference> termClasses;
				public LinkedList<CopperElementReference> submitList;
				public LinkedList<CopperElementReference> dominateList;
				
				public PrecLists()
				{
				    this.termClasses = null;
				    this.submitList = null;
				    this.dominateList = null;
				}
			}
			
			private static class TerminalFlags
			{
				public CopperElementReference prefix;
			}
			
			private static class ProductionRHSFlags
			{
				public LinkedList<CopperElementReference> layout;
				public CopperElementReference operator;
			}
			
			private static class LabeledName
			{
				public Token label;
				public Token name;
				
				public LabeledName(Token label,Token name)
				{
					this.label = label;
					this.name = name;
				}
				
				public LabeledName(Token name)
				{
				    this(null,name);
				}
			}
			
			private void logError(String message,Location location)
			{
			    logger.log(new GenericLocatedMessage(CompilerLevel.QUIET,location,message,true,false));
			}
			
			public CupSkinParser(CompilerLogger logger)
			{
				this.logger = logger;
			}
			
			public static ParserBean parseGrammar(ArrayList< Pair<String,Reader> > files,CompilerLogger logger)
    		throws IOException,CopperException
    		{
    			if(files.size() != 1)
    			{
    				logger.logError(new GenericMessage(CompilerLevel.QUIET,"CUP skin requires exactly one input file",true,true));
    			}
        		ParserBean spec;
				try
				{
					CupSkinParser engine = new CupSkinParser(logger);
					spec = engine.parse(files.get(0).second(),files.get(0).first());
					ParserSpecProcessor.normalizeParser(spec,logger);
				}
				catch(CopperSyntaxError ex)
				{
					logger.log(new GrammarSyntaxError(ex));
					spec = null;
				}
        		logger.flush();
        		return spec;
    		}
			

    static
    {
        TERMINAL_COUNT = 56;
        GRAMMAR_SYMBOL_COUNT = 92;
        SYMBOL_COUNT = 176;
        PARSER_STATE_COUNT = 186;
        SCANNER_STATE_COUNT = 199;
        DISAMBIG_GROUP_COUNT = 18;
        SCANNER_START_STATENUM = 1;
        PARSER_START_STATENUM = 1;
        EOF_SYMNUM = 0;
        EPS_SYMNUM = -1;
        try { initArrays(); }
        catch(java.io.IOException ex) { System.err.println("IO Exception"); }
        catch(java.lang.ClassNotFoundException ex) { System.err.println("Class Not Found Exception"); }
        disambiguationGroups = new java.util.BitSet[18];
        disambiguationGroups[0] = newBitVec(56,9,31);
        disambiguationGroups[1] = newBitVec(56,9,35);
        disambiguationGroups[2] = newBitVec(56,9,14);
        disambiguationGroups[3] = newBitVec(56,9,37);
        disambiguationGroups[4] = newBitVec(56,9,47);
        disambiguationGroups[5] = newBitVec(56,9,45);
        disambiguationGroups[6] = newBitVec(56,9,52);
        disambiguationGroups[7] = newBitVec(56,9,28);
        disambiguationGroups[8] = newBitVec(56,9,44);
        disambiguationGroups[9] = newBitVec(56,5,9);
        disambiguationGroups[10] = newBitVec(56,9,42);
        disambiguationGroups[11] = newBitVec(56,9,48);
        disambiguationGroups[12] = newBitVec(56,9,17);
        disambiguationGroups[13] = newBitVec(56,33,34);
        disambiguationGroups[14] = newBitVec(56,33,49);
        disambiguationGroups[15] = newBitVec(56,33,40);
        disambiguationGroups[16] = newBitVec(56,13,54);
        disambiguationGroups[17] = newBitVec(56,54,55);
    }

}
