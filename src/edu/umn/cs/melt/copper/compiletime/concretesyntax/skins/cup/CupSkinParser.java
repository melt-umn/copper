package edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.cup;

import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;


public class CupSkinParser extends edu.umn.cs.melt.copper.compiletime.engines.lalr.LALREngine
{
    public CupSkinParser(java.io.Reader reader,edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger logger)
    {
        scanner = new CupSkinParserScanner(reader,logger);
        this.logger = logger;
        setupEngine();
    }

    /** Create an empty symbol. */
    protected static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal eps()
    {
        return edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.FringeSymbols.EMPTY;
    }
    /** Create a terminal from a symbol. */
    protected static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t(String sym)
    {
        return new edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal(sym);
    }
    /** Create a terminal from a symbol. */
    protected static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol sym,String lexeme)
    {
        return new edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal(sym,lexeme);
    }
    /** Create a scanner match from a symbol, lexeme, and position-following. */
    protected static edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData qsm(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol sym,String lexeme,edu.umn.cs.melt.copper.runtime.io.InputPosition positionPreceding,edu.umn.cs.melt.copper.runtime.io.InputPosition positionFollowing,java.util.ArrayList<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> layouts)
    {
        return new edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData(t(sym,lexeme),positionPreceding,positionFollowing,layouts);
    }
    /** Create a nonterminal from a symbol. */
    protected static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal nt(String sym)
    {
        return new edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal(sym);
    }
    /** Create a production. */
    protected static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production p(String name,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal lhs,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol... rhs)
    {
        return edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production.production(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(name),lhs,rhs);
    }
    /** Create various parse actions. */
    protected static edu.umn.cs.melt.copper.compiletime.parsetable.AcceptAction a()
    {
        return new edu.umn.cs.melt.copper.compiletime.parsetable.AcceptAction();
    }
    protected static edu.umn.cs.melt.copper.compiletime.parsetable.FullReduceAction fr(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production p)
    {
        return new edu.umn.cs.melt.copper.compiletime.parsetable.FullReduceAction(p);
    }
    protected static edu.umn.cs.melt.copper.compiletime.parsetable.ShiftAction sh(int dest)
    {
        return new edu.umn.cs.melt.copper.compiletime.parsetable.ShiftAction(dest);
    }

    private Semantics semantics;
    public Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,Object[] _children,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production _prod)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
    {
        return semantics.runSemanticAction(_pos,_children,_prod);
    }
    public Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData _terminal)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
    {
        return semantics.runSemanticAction(_pos,_terminal);
    }
    public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData runDisambiguationAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,java.util.HashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> matches)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
    {
        return semantics.runDisambiguationAction(_pos,matches);
    }
    public edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes getSpecialAttributes()
    {
        return semantics.getSpecialAttributes();
    }
    public void startEngine(edu.umn.cs.melt.copper.runtime.io.InputPosition initialPos)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
    {
         super.startEngine(initialPos);
         semantics = new Semantics();
    }

    private static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal sym_0,sym_1,sym_2,sym_3,sym_4,sym_5,sym_6,sym_7,sym_8,sym_9,sym_10,sym_11,sym_12,sym_13,sym_14,sym_15,sym_16,sym_17,sym_18,sym_19,sym_20,sym_21,sym_22,sym_23,sym_24,sym_25,sym_26,sym_27,sym_28,sym_29,sym_30,sym_31,sym_32,sym_33,sym_34,sym_35,sym_36,sym_37,sym_38,sym_39,sym_40,sym_41,sym_42,sym_43,sym_44,sym_45,sym_46,sym_47,sym_48,sym_49,sym_50,sym_51,sym_52,sym_53,sym_54,sym_55;
    private static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal sym_56,sym_57,sym_58,sym_59,sym_60,sym_61,sym_62,sym_63,sym_64,sym_65,sym_66,sym_67,sym_68,sym_69,sym_70,sym_71,sym_72,sym_73,sym_74,sym_75,sym_76,sym_77,sym_78,sym_79,sym_80,sym_81,sym_82,sym_83,sym_84,sym_85,sym_86,sym_87,sym_88,sym_89,sym_90,sym_91,sym_92;
    @SuppressWarnings("unused")
    private static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production p_0,p_1,p_2,p_3,p_4,p_5,p_6,p_7,p_8,p_9,p_10,p_11,p_12,p_13,p_14,p_15,p_16,p_17,p_18,p_19,p_20,p_21,p_22,p_23,p_24,p_25,p_26,p_27,p_28,p_29,p_30,p_31,p_32,p_33,p_34,p_35,p_36,p_37,p_38,p_39,p_40,p_41,p_42,p_43,p_44,p_45,p_46,p_47,p_48,p_49,p_50,p_51,p_52,p_53,p_54,p_55,p_56,p_57,p_58,p_59,p_60,p_61,p_62,p_63,p_64,p_65,p_66,p_67,p_68,p_69,p_70,p_71,p_72,p_73,p_74,p_75,p_76,p_77,p_78,p_79,p_80,p_81,p_82,p_83,p_84,p_85;
    private static java.util.HashSet<edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal> group_0,group_1,group_2,group_3,group_4,group_5,group_6,group_7,group_8,group_9,group_10,group_11,group_12,group_13,group_14,group_15;

    public static ThisParseTable parseTable;

    private static class ThisParseTable extends edu.umn.cs.melt.copper.compiletime.parsetable.LazyGLRParseTable
    {
        public ThisParseTable()
        {
            super(185);
        }
        public void initShiftableUnion()
        {
            shiftableUnion = tset(sym_0,sym_2,sym_3,sym_4,sym_5,sym_6,sym_7,sym_8,sym_9,sym_10,sym_11,sym_12,sym_13,sym_14,sym_15,sym_16,sym_18,sym_17,sym_19,sym_20,sym_22,sym_21,sym_24,sym_23,sym_25,sym_26,sym_27,sym_29,sym_28,sym_30,sym_31,sym_32,sym_33,sym_34,sym_35,sym_36,sym_37,sym_39,sym_38,sym_41,sym_40,sym_43,sym_42,sym_44,sym_46,sym_47,sym_50,sym_51,sym_52,sym_53,sym_54);
        }
        public void initState(int statenum)
        {
            switch(statenum)
            {
                case 0: init_0(); return;
                case 1: init_1(); return;
                case 2: init_2(); return;
                case 3: init_3(); return;
                case 4: init_4(); return;
                case 5: init_5(); return;
                case 6: init_6(); return;
                case 7: init_7(); return;
                case 8: init_8(); return;
                case 9: init_9(); return;
                case 10: init_10(); return;
                case 11: init_11(); return;
                case 12: init_12(); return;
                case 13: init_13(); return;
                case 14: init_14(); return;
                case 15: init_15(); return;
                case 16: init_16(); return;
                case 17: init_17(); return;
                case 18: init_18(); return;
                case 19: init_19(); return;
                case 20: init_20(); return;
                case 21: init_21(); return;
                case 22: init_22(); return;
                case 23: init_23(); return;
                case 24: init_24(); return;
                case 25: init_25(); return;
                case 26: init_26(); return;
                case 27: init_27(); return;
                case 28: init_28(); return;
                case 29: init_29(); return;
                case 30: init_30(); return;
                case 31: init_31(); return;
                case 32: init_32(); return;
                case 33: init_33(); return;
                case 34: init_34(); return;
                case 35: init_35(); return;
                case 36: init_36(); return;
                case 37: init_37(); return;
                case 38: init_38(); return;
                case 39: init_39(); return;
                case 40: init_40(); return;
                case 41: init_41(); return;
                case 42: init_42(); return;
                case 43: init_43(); return;
                case 44: init_44(); return;
                case 45: init_45(); return;
                case 46: init_46(); return;
                case 47: init_47(); return;
                case 48: init_48(); return;
                case 49: init_49(); return;
                case 50: init_50(); return;
                case 51: init_51(); return;
                case 52: init_52(); return;
                case 53: init_53(); return;
                case 54: init_54(); return;
                case 55: init_55(); return;
                case 56: init_56(); return;
                case 57: init_57(); return;
                case 58: init_58(); return;
                case 59: init_59(); return;
                case 60: init_60(); return;
                case 61: init_61(); return;
                case 62: init_62(); return;
                case 63: init_63(); return;
                case 64: init_64(); return;
                case 65: init_65(); return;
                case 66: init_66(); return;
                case 67: init_67(); return;
                case 68: init_68(); return;
                case 69: init_69(); return;
                case 70: init_70(); return;
                case 71: init_71(); return;
                case 72: init_72(); return;
                case 73: init_73(); return;
                case 74: init_74(); return;
                case 75: init_75(); return;
                case 76: init_76(); return;
                case 77: init_77(); return;
                case 78: init_78(); return;
                case 79: init_79(); return;
                case 80: init_80(); return;
                case 81: init_81(); return;
                case 82: init_82(); return;
                case 83: init_83(); return;
                case 84: init_84(); return;
                case 85: init_85(); return;
                case 86: init_86(); return;
                case 87: init_87(); return;
                case 88: init_88(); return;
                case 89: init_89(); return;
                case 90: init_90(); return;
                case 91: init_91(); return;
                case 92: init_92(); return;
                case 93: init_93(); return;
                case 94: init_94(); return;
                case 95: init_95(); return;
                case 96: init_96(); return;
                case 97: init_97(); return;
                case 98: init_98(); return;
                case 99: init_99(); return;
                case 100: init_100(); return;
                case 101: init_101(); return;
                case 102: init_102(); return;
                case 103: init_103(); return;
                case 104: init_104(); return;
                case 105: init_105(); return;
                case 106: init_106(); return;
                case 107: init_107(); return;
                case 108: init_108(); return;
                case 109: init_109(); return;
                case 110: init_110(); return;
                case 111: init_111(); return;
                case 112: init_112(); return;
                case 113: init_113(); return;
                case 114: init_114(); return;
                case 115: init_115(); return;
                case 116: init_116(); return;
                case 117: init_117(); return;
                case 118: init_118(); return;
                case 119: init_119(); return;
                case 120: init_120(); return;
                case 121: init_121(); return;
                case 122: init_122(); return;
                case 123: init_123(); return;
                case 124: init_124(); return;
                case 125: init_125(); return;
                case 126: init_126(); return;
                case 127: init_127(); return;
                case 128: init_128(); return;
                case 129: init_129(); return;
                case 130: init_130(); return;
                case 131: init_131(); return;
                case 132: init_132(); return;
                case 133: init_133(); return;
                case 134: init_134(); return;
                case 135: init_135(); return;
                case 136: init_136(); return;
                case 137: init_137(); return;
                case 138: init_138(); return;
                case 139: init_139(); return;
                case 140: init_140(); return;
                case 141: init_141(); return;
                case 142: init_142(); return;
                case 143: init_143(); return;
                case 144: init_144(); return;
                case 145: init_145(); return;
                case 146: init_146(); return;
                case 147: init_147(); return;
                case 148: init_148(); return;
                case 149: init_149(); return;
                case 150: init_150(); return;
                case 151: init_151(); return;
                case 152: init_152(); return;
                case 153: init_153(); return;
                case 154: init_154(); return;
                case 155: init_155(); return;
                case 156: init_156(); return;
                case 157: init_157(); return;
                case 158: init_158(); return;
                case 159: init_159(); return;
                case 160: init_160(); return;
                case 161: init_161(); return;
                case 162: init_162(); return;
                case 163: init_163(); return;
                case 164: init_164(); return;
                case 165: init_165(); return;
                case 166: init_166(); return;
                case 167: init_167(); return;
                case 168: init_168(); return;
                case 169: init_169(); return;
                case 170: init_170(); return;
                case 171: init_171(); return;
                case 172: init_172(); return;
                case 173: init_173(); return;
                case 174: init_174(); return;
                case 175: init_175(); return;
                case 176: init_176(); return;
                case 177: init_177(); return;
                case 178: init_178(); return;
                case 179: init_179(); return;
                case 180: init_180(); return;
                case 181: init_181(); return;
                case 182: init_182(); return;
                case 183: init_183(); return;
                case 184: init_184(); return;
                default: return;
            }
        }
    }

    public edu.umn.cs.melt.copper.compiletime.parsetable.ReadOnlyParseTable getParseTable()
    {
        return parseTable;
    }
    /** Create a set of terminals. */
    protected static java.util.HashSet<edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal> tset(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal... ts)
    {
        java.util.HashSet<edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal> rv = new java.util.HashSet<edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal>();
        for(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t : ts) rv.add(t);
        return rv;
    }
    /** Add a parse action. */
    protected static void addA(int statenum,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal sym,edu.umn.cs.melt.copper.compiletime.parsetable.ParseAction action)
    {
        parseTable.addAction(statenum,sym,action);
    }
    /** Add a goto action. */
    protected static void addG(int statenum,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal sym,edu.umn.cs.melt.copper.compiletime.parsetable.ShiftAction action)
    {
        parseTable.addGotoAction(statenum,sym,action);
    }
    /** Add layout. */
    protected static void addL(int statenum,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal layout,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal... tokensFollowing)
    {
        for(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t : tokensFollowing) parseTable.addLayout(statenum,layout,t);
    }
    /** Add transparent prefixes. */
    protected static void addTP(int statenum,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal prefix,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal... tokensFollowing)
    {
        for(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t : tokensFollowing) parseTable.addPrefix(statenum,prefix,t);
    }
    /*
        GrammarFile ::= (*) code_t barrier_kwd ParserDecl DeclBlocks
        ^ ::= (*) GrammarFile $

    */
    public static void init_0()
    {
        addA(0,sym_52,sh(1));
        addG(0,sym_78,sh(2));
        addL(0,sym_48,sym_52);
    }
    /*
        GrammarFile ::= code_t (*) barrier_kwd ParserDecl DeclBlocks

    */
    public static void init_1()
    {
        addA(1,sym_10,sh(3));
        addL(1,sym_48,sym_10);
    }
    /*
        ^ ::= GrammarFile (*) $

    */
    public static void init_2()
    {
        addA(2,sym_0,a());
        addL(2,sym_48,sym_0);
    }
    /*
        GrammarFile ::= code_t barrier_kwd (*) ParserDecl DeclBlocks
        ParserDecl ::= (*) parser_decl_kwd name_tok

    */
    public static void init_3()
    {
        addA(3,sym_5,sh(5));
        addG(3,sym_63,sh(4));
        addL(3,sym_48,sym_5);
    }
    /*
        DeclBlocks ::= (*) DeclBlock
        DeclBlock ::= (*) attribute_decl_kwd TypeName name_tok semi_kwd
        DeclBlock ::= (*) cf_block_open_kwd CFDecls cf_block_close_kwd
        DeclBlock ::= (*) init_block_open_kwd code_t init_block_close_kwd
        DeclBlock ::= (*) lex_block_open_kwd LexDecls lex_block_close_kwd
        DeclBlock ::= (*) aux_block_open_kwd code_t aux_block_close_kwd
        DeclBlocks ::= (*) DeclBlock DeclBlocks
        GrammarFile ::= code_t barrier_kwd ParserDecl (*) DeclBlocks

    */
    public static void init_4()
    {
        addA(4,sym_44,sh(8));
        addA(4,sym_3,sh(7));
        addA(4,sym_32,sh(10));
        addA(4,sym_36,sh(9));
        addA(4,sym_8,sh(11));
        addG(4,sym_56,sh(6));
        addG(4,sym_57,sh(12));
        addL(4,sym_48,sym_3,sym_8,sym_32,sym_36,sym_44);
    }
    /*
        ParserDecl ::= parser_decl_kwd (*) name_tok

    */
    public static void init_5()
    {
        addA(5,sym_51,sh(13));
        addL(5,sym_48,sym_51);
    }
    /*
        DeclBlocks ::= (*) DeclBlock
        DeclBlock ::= (*) attribute_decl_kwd TypeName name_tok semi_kwd
        DeclBlock ::= (*) cf_block_open_kwd CFDecls cf_block_close_kwd
        DeclBlocks ::= DeclBlock (*)	[$]
        DeclBlock ::= (*) init_block_open_kwd code_t init_block_close_kwd
        DeclBlock ::= (*) lex_block_open_kwd LexDecls lex_block_close_kwd
        DeclBlock ::= (*) aux_block_open_kwd code_t aux_block_close_kwd
        DeclBlocks ::= (*) DeclBlock DeclBlocks
        DeclBlocks ::= DeclBlock (*) DeclBlocks

    */
    public static void init_6()
    {
        addA(6,sym_44,sh(8));
        addA(6,sym_3,sh(7));
        addA(6,sym_32,sh(10));
        addA(6,sym_0,fr(p_6));
        addA(6,sym_36,sh(9));
        addA(6,sym_8,sh(11));
        addG(6,sym_56,sh(6));
        addG(6,sym_57,sh(21));
        addL(6,sym_48,sym_3,sym_8,sym_0,sym_32,sym_36,sym_44);
    }
    /*
        TypeName ::= (*) QualifiedName lt_kwd TypeNameSeq gt_kwd
        DeclBlock ::= attribute_decl_kwd (*) TypeName name_tok semi_kwd
        QualifiedName ::= (*) name_tok
        TypeName ::= (*) QualifiedName
        QualifiedName ::= (*) name_tok wildcard QualifiedName

    */
    public static void init_7()
    {
        addA(7,sym_51,sh(24));
        addG(7,sym_72,sh(22));
        addG(7,sym_71,sh(23));
        addL(7,sym_48,sym_51);
    }
    /*
        CFDecl ::= (*) name_tok goesto_kwd RHSSeq semi_kwd
        CFDecl ::= (*) start_kwd with_kwd name_tok semi_kwd
        DeclBlock ::= cf_block_open_kwd (*) CFDecls cf_block_close_kwd
        CFDecls ::= (*) CFDecl
        CFDecl ::= (*) non_kwd terminal_kwd CommaSymSeq semi_kwd
        CFDecls ::= (*) CFDecl CFDecls
        CFDecl ::= (*) precedence_kwd assoctypes_kwd CommaOrSymSeq semi_kwd
        CFDecl ::= (*) non_kwd terminal_kwd TypeName CommaSymSeq semi_kwd

    */
    public static void init_8()
    {
        addA(8,sym_26,sh(27));
        addA(8,sym_29,sh(31));
        addA(8,sym_22,sh(30));
        addA(8,sym_51,sh(26));
        addG(8,sym_59,sh(28));
        addG(8,sym_85,sh(29));
        addL(8,sym_48,sym_22,sym_26,sym_29,sym_51);
    }
    /*
        DeclBlock ::= init_block_open_kwd (*) code_t init_block_close_kwd

    */
    public static void init_9()
    {
        addA(9,sym_52,sh(20));
        addL(9,sym_48,sym_52);
    }
    /*
        LexDecl ::= (*) IgnoreOpt terminal_kwd TypeName name_tok goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        LexDecl ::= (*) class_kwd CommaSymSeq semi_kwd
        LexDecl ::= (*) disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen code_block_open_kwd code_t code_block_close_kwd semi_kwd
        IgnoreOpt ::= (*)	[terminal_kwd]
        DeclBlock ::= lex_block_open_kwd (*) LexDecls lex_block_close_kwd
        LexDecl ::= (*) IgnoreOpt terminal_kwd name_tok SuperRegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        LexDecls ::= (*) LexDecl LexDecls
        IgnoreOpt ::= (*) ignore_kwd
        LexDecl ::= (*) disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen goesto_kwd name_tok semi_kwd
        LexDecls ::= (*) LexDecl

    */
    public static void init_10()
    {
        addA(10,sym_27,sh(16));
        addA(10,sym_18,sh(15));
        addA(10,sym_19,fr(p_71));
        addA(10,sym_20,sh(19));
        addG(10,sym_84,sh(18));
        addG(10,sym_61,sh(17));
        addG(10,sym_86,sh(14));
        addL(10,sym_48,sym_18,sym_19,sym_20,sym_27);
    }
    /*
        DeclBlock ::= aux_block_open_kwd (*) code_t aux_block_close_kwd

    */
    public static void init_11()
    {
        addA(11,sym_52,sh(25));
        addL(11,sym_48,sym_52);
    }
    /*
        GrammarFile ::= code_t barrier_kwd ParserDecl DeclBlocks (*)	[$]

    */
    public static void init_12()
    {
        addA(12,sym_0,fr(p_50));
        addL(12,sym_48,sym_0);
    }
    /*
        ParserDecl ::= parser_decl_kwd name_tok (*)	[attribute_decl_kwd, aux_block_open_kwd, lex_block_open_kwd, init_block_open_kwd, cf_block_open_kwd]

    */
    public static void init_13()
    {
        addA(13,sym_44,fr(p_18));
        addA(13,sym_3,fr(p_18));
        addA(13,sym_32,fr(p_18));
        addA(13,sym_36,fr(p_18));
        addA(13,sym_8,fr(p_18));
        addL(13,sym_48,sym_3,sym_8,sym_32,sym_36,sym_44);
    }
    /*
        LexDecl ::= IgnoreOpt (*) terminal_kwd TypeName name_tok goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        LexDecl ::= IgnoreOpt (*) terminal_kwd name_tok SuperRegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd

    */
    public static void init_14()
    {
        addA(14,sym_19,sh(46));
        addL(14,sym_48,sym_19);
    }
    /*
        LexDecl ::= class_kwd (*) CommaSymSeq semi_kwd
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaSymSeq ::= (*) name_tok

    */
    public static void init_15()
    {
        addA(15,sym_51,sh(48));
        addG(15,sym_77,sh(47));
        addL(15,sym_48,sym_51);
    }
    /*
        LexDecl ::= disambiguate_kwd (*) name_tok colon_kwd lparen CommaSymSeq rparen code_block_open_kwd code_t code_block_close_kwd semi_kwd
        LexDecl ::= disambiguate_kwd (*) name_tok colon_kwd lparen CommaSymSeq rparen goesto_kwd name_tok semi_kwd

    */
    public static void init_16()
    {
        addA(16,sym_51,sh(35));
        addL(16,sym_48,sym_51);
    }
    /*
        DeclBlock ::= lex_block_open_kwd LexDecls (*) lex_block_close_kwd

    */
    public static void init_17()
    {
        addA(17,sym_30,sh(39));
        addL(17,sym_48,sym_30);
    }
    /*
        LexDecl ::= (*) IgnoreOpt terminal_kwd TypeName name_tok goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        LexDecls ::= LexDecl (*)	[lex_block_close_kwd]
        LexDecl ::= (*) class_kwd CommaSymSeq semi_kwd
        LexDecl ::= (*) disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen code_block_open_kwd code_t code_block_close_kwd semi_kwd
        IgnoreOpt ::= (*)	[terminal_kwd]
        LexDecl ::= (*) IgnoreOpt terminal_kwd name_tok SuperRegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        LexDecls ::= (*) LexDecl LexDecls
        IgnoreOpt ::= (*) ignore_kwd
        LexDecl ::= (*) disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen goesto_kwd name_tok semi_kwd
        LexDecls ::= LexDecl (*) LexDecls
        LexDecls ::= (*) LexDecl

    */
    public static void init_18()
    {
        addA(18,sym_30,fr(p_15));
        addA(18,sym_27,sh(16));
        addA(18,sym_18,sh(15));
        addA(18,sym_19,fr(p_71));
        addA(18,sym_20,sh(19));
        addG(18,sym_84,sh(18));
        addG(18,sym_61,sh(36));
        addG(18,sym_86,sh(14));
        addL(18,sym_48,sym_18,sym_19,sym_20,sym_27,sym_30);
    }
    /*
        IgnoreOpt ::= ignore_kwd (*)	[terminal_kwd]

    */
    public static void init_19()
    {
        addA(19,sym_19,fr(p_72));
        addL(19,sym_48,sym_19);
    }
    /*
        DeclBlock ::= init_block_open_kwd code_t (*) init_block_close_kwd

    */
    public static void init_20()
    {
        addA(20,sym_34,sh(44));
        addL(20,sym_48,sym_34);
    }
    /*
        DeclBlocks ::= DeclBlock DeclBlocks (*)	[$]

    */
    public static void init_21()
    {
        addA(21,sym_0,fr(p_5));
        addL(21,sym_48,sym_0);
    }
    /*
        TypeName ::= QualifiedName (*) lt_kwd TypeNameSeq gt_kwd
        TypeName ::= QualifiedName (*)	[name_tok, comma_kwd, gt_kwd]

    */
    public static void init_22()
    {
        addA(22,sym_16,fr(p_37));
        addA(22,sym_15,sh(37));
        addA(22,sym_12,fr(p_37));
        addA(22,sym_51,fr(p_37));
        addL(22,sym_48,sym_51,sym_12,sym_15,sym_16);
    }
    /*
        DeclBlock ::= attribute_decl_kwd TypeName (*) name_tok semi_kwd

    */
    public static void init_23()
    {
        addA(23,sym_51,sh(41));
        addL(23,sym_48,sym_51);
    }
    /*
        QualifiedName ::= name_tok (*)	[name_tok, comma_kwd, lt_kwd, gt_kwd]
        QualifiedName ::= name_tok (*) wildcard QualifiedName

    */
    public static void init_24()
    {
        addA(24,sym_16,fr(p_39));
        addA(24,sym_15,fr(p_39));
        addA(24,sym_12,fr(p_39));
        addA(24,sym_51,fr(p_39));
        addA(24,sym_39,sh(33));
        addL(24,sym_48,sym_51,sym_12,sym_39,sym_15,sym_16);
    }
    /*
        DeclBlock ::= aux_block_open_kwd code_t (*) aux_block_close_kwd

    */
    public static void init_25()
    {
        addA(25,sym_9,sh(45));
        addL(25,sym_48,sym_9);
    }
    /*
        CFDecl ::= name_tok (*) goesto_kwd RHSSeq semi_kwd

    */
    public static void init_26()
    {
        addA(26,sym_11,sh(40));
        addL(26,sym_48,sym_11);
    }
    /*
        CFDecl ::= start_kwd (*) with_kwd name_tok semi_kwd

    */
    public static void init_27()
    {
        addA(27,sym_24,sh(43));
        addL(27,sym_48,sym_24);
    }
    /*
        DeclBlock ::= cf_block_open_kwd CFDecls (*) cf_block_close_kwd

    */
    public static void init_28()
    {
        addA(28,sym_42,sh(42));
        addL(28,sym_48,sym_42);
    }
    /*
        CFDecl ::= (*) name_tok goesto_kwd RHSSeq semi_kwd
        CFDecl ::= (*) start_kwd with_kwd name_tok semi_kwd
        CFDecls ::= CFDecl (*)	[cf_block_close_kwd]
        CFDecls ::= (*) CFDecl
        CFDecl ::= (*) non_kwd terminal_kwd CommaSymSeq semi_kwd
        CFDecls ::= (*) CFDecl CFDecls
        CFDecls ::= CFDecl (*) CFDecls
        CFDecl ::= (*) precedence_kwd assoctypes_kwd CommaOrSymSeq semi_kwd
        CFDecl ::= (*) non_kwd terminal_kwd TypeName CommaSymSeq semi_kwd

    */
    public static void init_29()
    {
        addA(29,sym_26,sh(27));
        addA(29,sym_29,sh(31));
        addA(29,sym_22,sh(30));
        addA(29,sym_51,sh(26));
        addA(29,sym_42,fr(p_9));
        addG(29,sym_59,sh(34));
        addG(29,sym_85,sh(29));
        addL(29,sym_48,sym_22,sym_26,sym_29,sym_51,sym_42);
    }
    /*
        CFDecl ::= non_kwd (*) terminal_kwd CommaSymSeq semi_kwd
        CFDecl ::= non_kwd (*) terminal_kwd TypeName CommaSymSeq semi_kwd

    */
    public static void init_30()
    {
        addA(30,sym_19,sh(32));
        addL(30,sym_48,sym_19);
    }
    /*
        CFDecl ::= precedence_kwd (*) assoctypes_kwd CommaOrSymSeq semi_kwd

    */
    public static void init_31()
    {
        addA(31,sym_54,sh(38));
        addL(31,sym_48,sym_54);
    }
    /*
        TypeName ::= (*) QualifiedName lt_kwd TypeNameSeq gt_kwd
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        QualifiedName ::= (*) name_tok
        CommaSymSeq ::= (*) name_tok
        CFDecl ::= non_kwd terminal_kwd (*) CommaSymSeq semi_kwd
        TypeName ::= (*) QualifiedName
        CFDecl ::= non_kwd terminal_kwd (*) TypeName CommaSymSeq semi_kwd
        QualifiedName ::= (*) name_tok wildcard QualifiedName

    */
    public static void init_32()
    {
        addA(32,sym_51,sh(59));
        addG(32,sym_77,sh(60));
        addG(32,sym_72,sh(22));
        addG(32,sym_71,sh(61));
        addL(32,sym_48,sym_51);
    }
    /*
        QualifiedName ::= (*) name_tok
        QualifiedName ::= name_tok wildcard (*) QualifiedName
        QualifiedName ::= (*) name_tok wildcard QualifiedName

    */
    public static void init_33()
    {
        addA(33,sym_51,sh(24));
        addG(33,sym_72,sh(50));
        addL(33,sym_48,sym_51);
    }
    /*
        CFDecls ::= CFDecl CFDecls (*)	[cf_block_close_kwd]

    */
    public static void init_34()
    {
        addA(34,sym_42,fr(p_10));
        addL(34,sym_48,sym_42);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok (*) colon_kwd lparen CommaSymSeq rparen code_block_open_kwd code_t code_block_close_kwd semi_kwd
        LexDecl ::= disambiguate_kwd name_tok (*) colon_kwd lparen CommaSymSeq rparen goesto_kwd name_tok semi_kwd

    */
    public static void init_35()
    {
        addA(35,sym_2,sh(49));
        addL(35,sym_48,sym_2);
    }
    /*
        LexDecls ::= LexDecl LexDecls (*)	[lex_block_close_kwd]

    */
    public static void init_36()
    {
        addA(36,sym_30,fr(p_16));
        addL(36,sym_48,sym_30);
    }
    /*
        TypeNameSeq ::= (*) TypeName comma_kwd TypeNameSeq
        TypeName ::= (*) QualifiedName lt_kwd TypeNameSeq gt_kwd
        QualifiedName ::= (*) name_tok
        TypeName ::= QualifiedName lt_kwd (*) TypeNameSeq gt_kwd
        TypeName ::= (*) QualifiedName
        TypeNameSeq ::= (*) TypeName
        QualifiedName ::= (*) name_tok wildcard QualifiedName

    */
    public static void init_37()
    {
        addA(37,sym_51,sh(24));
        addG(37,sym_72,sh(22));
        addG(37,sym_71,sh(66));
        addG(37,sym_70,sh(67));
        addL(37,sym_48,sym_51);
    }
    /*
        CommaOrSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaOrSymSeq ::= (*) name_tok SymSeq
        CFDecl ::= precedence_kwd assoctypes_kwd (*) CommaOrSymSeq semi_kwd

    */
    public static void init_38()
    {
        addA(38,sym_51,sh(57));
        addG(38,sym_76,sh(58));
        addL(38,sym_48,sym_51);
    }
    /*
        DeclBlock ::= lex_block_open_kwd LexDecls lex_block_close_kwd (*)	[attribute_decl_kwd, aux_block_open_kwd, $, lex_block_open_kwd, init_block_open_kwd, cf_block_open_kwd]

    */
    public static void init_39()
    {
        addA(39,sym_44,fr(p_4));
        addA(39,sym_3,fr(p_4));
        addA(39,sym_32,fr(p_4));
        addA(39,sym_0,fr(p_4));
        addA(39,sym_36,fr(p_4));
        addA(39,sym_8,fr(p_4));
        addL(39,sym_48,sym_3,sym_8,sym_0,sym_32,sym_36,sym_44);
    }
    /*
        CFDecl ::= name_tok goesto_kwd (*) RHSSeq semi_kwd
        LabeledSymSeq ::= (*)	[layout_decl_kwd, prec_decl_kwd, bar, semi_kwd, code_block_open_kwd]
        LabeledSymSeq ::= (*) name_tok LabeledSymSeq
        RHS ::= (*) LabeledSymSeq CodeBlockOpt RHSFlags
        LabeledSymSeq ::= (*) name_tok colon name_tok LabeledSymSeq
        RHSSeq ::= (*) RHS
        RHSSeq ::= (*) RHS bar RHSSeq

    */
    public static void init_40()
    {
        addA(40,sym_35,fr(p_85));
        addA(40,sym_13,fr(p_85));
        addA(40,sym_6,fr(p_85));
        addA(40,sym_51,sh(63));
        addA(40,sym_40,fr(p_85));
        addA(40,sym_4,fr(p_85));
        addG(40,sym_90,sh(62));
        addG(40,sym_91,sh(65));
        addG(40,sym_92,sh(64));
        addL(40,sym_48,sym_4,sym_6,sym_51,sym_35,sym_13,sym_40);
    }
    /*
        DeclBlock ::= attribute_decl_kwd TypeName name_tok (*) semi_kwd

    */
    public static void init_41()
    {
        addA(41,sym_13,sh(53));
        addL(41,sym_48,sym_13);
    }
    /*
        DeclBlock ::= cf_block_open_kwd CFDecls cf_block_close_kwd (*)	[attribute_decl_kwd, aux_block_open_kwd, $, lex_block_open_kwd, init_block_open_kwd, cf_block_open_kwd]

    */
    public static void init_42()
    {
        addA(42,sym_44,fr(p_3));
        addA(42,sym_3,fr(p_3));
        addA(42,sym_32,fr(p_3));
        addA(42,sym_0,fr(p_3));
        addA(42,sym_36,fr(p_3));
        addA(42,sym_8,fr(p_3));
        addL(42,sym_48,sym_3,sym_8,sym_0,sym_32,sym_36,sym_44);
    }
    /*
        CFDecl ::= start_kwd with_kwd (*) name_tok semi_kwd

    */
    public static void init_43()
    {
        addA(43,sym_51,sh(54));
        addL(43,sym_48,sym_51);
    }
    /*
        DeclBlock ::= init_block_open_kwd code_t init_block_close_kwd (*)	[attribute_decl_kwd, aux_block_open_kwd, $, lex_block_open_kwd, init_block_open_kwd, cf_block_open_kwd]

    */
    public static void init_44()
    {
        addA(44,sym_44,fr(p_1));
        addA(44,sym_3,fr(p_1));
        addA(44,sym_32,fr(p_1));
        addA(44,sym_0,fr(p_1));
        addA(44,sym_36,fr(p_1));
        addA(44,sym_8,fr(p_1));
        addL(44,sym_48,sym_3,sym_8,sym_0,sym_32,sym_36,sym_44);
    }
    /*
        DeclBlock ::= aux_block_open_kwd code_t aux_block_close_kwd (*)	[attribute_decl_kwd, aux_block_open_kwd, $, lex_block_open_kwd, init_block_open_kwd, cf_block_open_kwd]

    */
    public static void init_45()
    {
        addA(45,sym_44,fr(p_2));
        addA(45,sym_3,fr(p_2));
        addA(45,sym_32,fr(p_2));
        addA(45,sym_0,fr(p_2));
        addA(45,sym_36,fr(p_2));
        addA(45,sym_8,fr(p_2));
        addL(45,sym_48,sym_3,sym_8,sym_0,sym_32,sym_36,sym_44);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd (*) TypeName name_tok goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        TypeName ::= (*) QualifiedName lt_kwd TypeNameSeq gt_kwd
        QualifiedName ::= (*) name_tok
        TypeName ::= (*) QualifiedName
        LexDecl ::= IgnoreOpt terminal_kwd (*) name_tok SuperRegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        QualifiedName ::= (*) name_tok wildcard QualifiedName

    */
    public static void init_46()
    {
        addA(46,sym_51,sh(52));
        addG(46,sym_72,sh(22));
        addG(46,sym_71,sh(51));
        addL(46,sym_48,sym_51);
    }
    /*
        LexDecl ::= class_kwd CommaSymSeq (*) semi_kwd

    */
    public static void init_47()
    {
        addA(47,sym_13,sh(55));
        addL(47,sym_48,sym_13);
    }
    /*
        CommaSymSeq ::= name_tok (*) comma_kwd CommaSymSeq
        CommaSymSeq ::= name_tok (*)	[semi_kwd, rparen]

    */
    public static void init_48()
    {
        addA(48,sym_13,fr(p_49));
        addA(48,sym_12,sh(56));
        addA(48,sym_41,fr(p_49));
        addL(48,sym_48,sym_12,sym_13,sym_41);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd (*) lparen CommaSymSeq rparen goesto_kwd name_tok semi_kwd
        LexDecl ::= disambiguate_kwd name_tok colon_kwd (*) lparen CommaSymSeq rparen code_block_open_kwd code_t code_block_close_kwd semi_kwd

    */
    public static void init_49()
    {
        addA(49,sym_31,sh(68));
        addL(49,sym_48,sym_31);
    }
    /*
        QualifiedName ::= name_tok wildcard QualifiedName (*)	[name_tok, comma_kwd, lt_kwd, gt_kwd]

    */
    public static void init_50()
    {
        addA(50,sym_16,fr(p_38));
        addA(50,sym_15,fr(p_38));
        addA(50,sym_12,fr(p_38));
        addA(50,sym_51,fr(p_38));
        addL(50,sym_48,sym_51,sym_12,sym_15,sym_16);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd TypeName (*) name_tok goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd

    */
    public static void init_51()
    {
        addA(51,sym_51,sh(70));
        addL(51,sym_48,sym_51);
    }
    /*
        QualifiedName ::= name_tok (*)	[name_tok, lt_kwd]
        QualifiedName ::= name_tok (*) wildcard QualifiedName
        SuperRegexRoot ::= (*) goesto_kwd RegexRoot
        LexDecl ::= IgnoreOpt terminal_kwd name_tok (*) SuperRegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd

    */
    public static void init_52()
    {
        addA(52,sym_15,fr(p_39));
        addA(52,sym_11,sh(71));
        addA(52,sym_51,fr(p_39));
        addA(52,sym_39,sh(33));
        addG(52,sym_83,sh(72));
        addL(52,sym_48,sym_51,sym_11,sym_39,sym_15);
    }
    /*
        DeclBlock ::= attribute_decl_kwd TypeName name_tok semi_kwd (*)	[attribute_decl_kwd, aux_block_open_kwd, $, lex_block_open_kwd, init_block_open_kwd, cf_block_open_kwd]

    */
    public static void init_53()
    {
        addA(53,sym_44,fr(p_0));
        addA(53,sym_3,fr(p_0));
        addA(53,sym_32,fr(p_0));
        addA(53,sym_0,fr(p_0));
        addA(53,sym_36,fr(p_0));
        addA(53,sym_8,fr(p_0));
        addL(53,sym_48,sym_3,sym_8,sym_0,sym_32,sym_36,sym_44);
    }
    /*
        CFDecl ::= start_kwd with_kwd name_tok (*) semi_kwd

    */
    public static void init_54()
    {
        addA(54,sym_13,sh(78));
        addL(54,sym_48,sym_13);
    }
    /*
        LexDecl ::= class_kwd CommaSymSeq semi_kwd (*)	[class_kwd, terminal_kwd, ignore_kwd, disambiguate_kwd, lex_block_close_kwd]

    */
    public static void init_55()
    {
        addA(55,sym_30,fr(p_62));
        addA(55,sym_27,fr(p_62));
        addA(55,sym_18,fr(p_62));
        addA(55,sym_19,fr(p_62));
        addA(55,sym_20,fr(p_62));
        addL(55,sym_48,sym_18,sym_19,sym_20,sym_27,sym_30);
    }
    /*
        CommaSymSeq ::= name_tok comma_kwd (*) CommaSymSeq
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaSymSeq ::= (*) name_tok

    */
    public static void init_56()
    {
        addA(56,sym_51,sh(48));
        addG(56,sym_77,sh(82));
        addL(56,sym_48,sym_51);
    }
    /*
        CommaOrSymSeq ::= name_tok (*) comma_kwd CommaSymSeq
        SymSeq ::= (*)	[semi_kwd]
        CommaOrSymSeq ::= name_tok (*) SymSeq
        SymSeq ::= (*) name_tok SymSeq

    */
    public static void init_57()
    {
        addA(57,sym_13,fr(p_31));
        addA(57,sym_12,sh(73));
        addA(57,sym_51,sh(75));
        addG(57,sym_68,sh(74));
        addL(57,sym_48,sym_51,sym_12,sym_13);
    }
    /*
        CFDecl ::= precedence_kwd assoctypes_kwd CommaOrSymSeq (*) semi_kwd

    */
    public static void init_58()
    {
        addA(58,sym_13,sh(76));
        addL(58,sym_48,sym_13);
    }
    /*
        CommaSymSeq ::= name_tok (*) comma_kwd CommaSymSeq
        QualifiedName ::= name_tok (*)	[name_tok, lt_kwd]
        CommaSymSeq ::= name_tok (*)	[semi_kwd]
        QualifiedName ::= name_tok (*) wildcard QualifiedName

    */
    public static void init_59()
    {
        addA(59,sym_15,fr(p_39));
        addA(59,sym_13,fr(p_49));
        addA(59,sym_12,sh(56));
        addA(59,sym_51,fr(p_39));
        addA(59,sym_39,sh(33));
        addL(59,sym_48,sym_51,sym_12,sym_39,sym_13,sym_15);
    }
    /*
        CFDecl ::= non_kwd terminal_kwd CommaSymSeq (*) semi_kwd

    */
    public static void init_60()
    {
        addA(60,sym_13,sh(86));
        addL(60,sym_48,sym_13);
    }
    /*
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaSymSeq ::= (*) name_tok
        CFDecl ::= non_kwd terminal_kwd TypeName (*) CommaSymSeq semi_kwd

    */
    public static void init_61()
    {
        addA(61,sym_51,sh(48));
        addG(61,sym_77,sh(69));
        addL(61,sym_48,sym_51);
    }
    /*
        CFDecl ::= name_tok goesto_kwd RHSSeq (*) semi_kwd

    */
    public static void init_62()
    {
        addA(62,sym_13,sh(81));
        addL(62,sym_48,sym_13);
    }
    /*
        LabeledSymSeq ::= (*)	[layout_decl_kwd, prec_decl_kwd, bar, semi_kwd, code_block_open_kwd]
        LabeledSymSeq ::= (*) name_tok LabeledSymSeq
        LabeledSymSeq ::= name_tok (*) LabeledSymSeq
        LabeledSymSeq ::= name_tok (*) colon name_tok LabeledSymSeq
        LabeledSymSeq ::= (*) name_tok colon name_tok LabeledSymSeq

    */
    public static void init_63()
    {
        addA(63,sym_35,fr(p_85));
        addA(63,sym_13,fr(p_85));
        addA(63,sym_23,sh(80));
        addA(63,sym_6,fr(p_85));
        addA(63,sym_51,sh(63));
        addA(63,sym_40,fr(p_85));
        addA(63,sym_4,fr(p_85));
        addG(63,sym_92,sh(79));
        addL(63,sym_48,sym_4,sym_23,sym_6,sym_51,sym_35,sym_13,sym_40);
    }
    /*
        RHS ::= LabeledSymSeq (*) CodeBlockOpt RHSFlags
        CodeBlockOpt ::= (*)	[layout_decl_kwd, prec_decl_kwd, bar, semi_kwd]
        CodeBlockOpt ::= (*) code_block_open_kwd code_t code_block_close_kwd

    */
    public static void init_64()
    {
        addA(64,sym_35,fr(p_75));
        addA(64,sym_13,fr(p_75));
        addA(64,sym_6,fr(p_75));
        addA(64,sym_40,sh(85));
        addA(64,sym_4,fr(p_75));
        addG(64,sym_88,sh(84));
        addL(64,sym_48,sym_4,sym_6,sym_35,sym_13,sym_40);
    }
    /*
        RHSSeq ::= RHS (*)	[semi_kwd]
        RHSSeq ::= RHS (*) bar RHSSeq

    */
    public static void init_65()
    {
        addA(65,sym_35,sh(77));
        addA(65,sym_13,fr(p_81));
        addL(65,sym_48,sym_35,sym_13);
    }
    /*
        TypeNameSeq ::= TypeName (*) comma_kwd TypeNameSeq
        TypeNameSeq ::= TypeName (*)	[gt_kwd]

    */
    public static void init_66()
    {
        addA(66,sym_16,fr(p_34));
        addA(66,sym_12,sh(87));
        addL(66,sym_48,sym_12,sym_16);
    }
    /*
        TypeName ::= QualifiedName lt_kwd TypeNameSeq (*) gt_kwd

    */
    public static void init_67()
    {
        addA(67,sym_16,sh(83));
        addL(67,sym_48,sym_16);
    }
    /*
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaSymSeq ::= (*) name_tok
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen (*) CommaSymSeq rparen goesto_kwd name_tok semi_kwd
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen (*) CommaSymSeq rparen code_block_open_kwd code_t code_block_close_kwd semi_kwd

    */
    public static void init_68()
    {
        addA(68,sym_51,sh(48));
        addG(68,sym_77,sh(106));
        addL(68,sym_48,sym_51);
    }
    /*
        CFDecl ::= non_kwd terminal_kwd TypeName CommaSymSeq (*) semi_kwd

    */
    public static void init_69()
    {
        addA(69,sym_13,sh(104));
        addL(69,sym_48,sym_13);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd TypeName name_tok (*) goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd

    */
    public static void init_70()
    {
        addA(70,sym_11,sh(89));
        addL(70,sym_48,sym_11);
    }
    /*
        RegexRoot ::= (*) slash_kwd slash_kwd
        RegexRoot ::= (*) slash_kwd Regex_R slash_kwd
        SuperRegexRoot ::= goesto_kwd (*) RegexRoot

    */
    public static void init_71()
    {
        addA(71,sym_14,sh(90));
        addG(71,sym_74,sh(91));
        addL(71,sym_55,sym_14);
    }
    /*
        PrecList ::= (*) in_kwd lparen CommaSymSeqOpt rparen
        PrecLists ::= (*) PrecList comma_kwd PrecLists
        PrecLists ::= (*) PrecList
        PrecList ::= (*) gt_kwd lparen CommaSymSeqOpt rparen
        PrecListsOpt ::= (*)	[prefix_decl_kwd, semi_kwd, code_block_open_kwd]
        PrecListsOpt ::= (*) PrecLists
        LexDecl ::= IgnoreOpt terminal_kwd name_tok SuperRegexRoot (*) PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        PrecList ::= (*) lt_kwd lparen CommaSymSeqOpt rparen

    */
    public static void init_72()
    {
        addA(72,sym_16,sh(95));
        addA(72,sym_15,sh(98));
        addA(72,sym_13,fr(p_73));
        addA(72,sym_7,fr(p_73));
        addA(72,sym_40,fr(p_73));
        addA(72,sym_53,sh(93));
        addG(72,sym_81,sh(94));
        addG(72,sym_80,sh(96));
        addG(72,sym_87,sh(97));
        addL(72,sym_48,sym_7,sym_53,sym_13,sym_40,sym_15,sym_16);
    }
    /*
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaOrSymSeq ::= name_tok comma_kwd (*) CommaSymSeq
        CommaSymSeq ::= (*) name_tok

    */
    public static void init_73()
    {
        addA(73,sym_51,sh(48));
        addG(73,sym_77,sh(88));
        addL(73,sym_48,sym_51);
    }
    /*
        CommaOrSymSeq ::= name_tok SymSeq (*)	[semi_kwd]

    */
    public static void init_74()
    {
        addA(74,sym_13,fr(p_46));
        addL(74,sym_48,sym_13);
    }
    /*
        SymSeq ::= (*)	[semi_kwd]
        SymSeq ::= name_tok (*) SymSeq
        SymSeq ::= (*) name_tok SymSeq

    */
    public static void init_75()
    {
        addA(75,sym_13,fr(p_31));
        addA(75,sym_51,sh(75));
        addG(75,sym_68,sh(99));
        addL(75,sym_48,sym_51,sym_13);
    }
    /*
        CFDecl ::= precedence_kwd assoctypes_kwd CommaOrSymSeq semi_kwd (*)	[non_kwd, start_kwd, precedence_kwd, name_tok, cf_block_close_kwd]

    */
    public static void init_76()
    {
        addA(76,sym_26,fr(p_66));
        addA(76,sym_29,fr(p_66));
        addA(76,sym_22,fr(p_66));
        addA(76,sym_51,fr(p_66));
        addA(76,sym_42,fr(p_66));
        addL(76,sym_48,sym_22,sym_26,sym_29,sym_51,sym_42);
    }
    /*
        LabeledSymSeq ::= (*)	[layout_decl_kwd, prec_decl_kwd, bar, semi_kwd, code_block_open_kwd]
        LabeledSymSeq ::= (*) name_tok LabeledSymSeq
        RHS ::= (*) LabeledSymSeq CodeBlockOpt RHSFlags
        RHSSeq ::= RHS bar (*) RHSSeq
        LabeledSymSeq ::= (*) name_tok colon name_tok LabeledSymSeq
        RHSSeq ::= (*) RHS
        RHSSeq ::= (*) RHS bar RHSSeq

    */
    public static void init_77()
    {
        addA(77,sym_35,fr(p_85));
        addA(77,sym_13,fr(p_85));
        addA(77,sym_6,fr(p_85));
        addA(77,sym_51,sh(63));
        addA(77,sym_40,fr(p_85));
        addA(77,sym_4,fr(p_85));
        addG(77,sym_90,sh(107));
        addG(77,sym_91,sh(65));
        addG(77,sym_92,sh(64));
        addL(77,sym_48,sym_4,sym_6,sym_51,sym_35,sym_13,sym_40);
    }
    /*
        CFDecl ::= start_kwd with_kwd name_tok semi_kwd (*)	[non_kwd, start_kwd, precedence_kwd, name_tok, cf_block_close_kwd]

    */
    public static void init_78()
    {
        addA(78,sym_26,fr(p_68));
        addA(78,sym_29,fr(p_68));
        addA(78,sym_22,fr(p_68));
        addA(78,sym_51,fr(p_68));
        addA(78,sym_42,fr(p_68));
        addL(78,sym_48,sym_22,sym_26,sym_29,sym_51,sym_42);
    }
    /*
        LabeledSymSeq ::= name_tok LabeledSymSeq (*)	[layout_decl_kwd, prec_decl_kwd, bar, semi_kwd, code_block_open_kwd]

    */
    public static void init_79()
    {
        addA(79,sym_35,fr(p_84));
        addA(79,sym_13,fr(p_84));
        addA(79,sym_6,fr(p_84));
        addA(79,sym_40,fr(p_84));
        addA(79,sym_4,fr(p_84));
        addL(79,sym_48,sym_4,sym_6,sym_35,sym_13,sym_40);
    }
    /*
        LabeledSymSeq ::= name_tok colon (*) name_tok LabeledSymSeq

    */
    public static void init_80()
    {
        addA(80,sym_51,sh(105));
        addL(80,sym_48,sym_51);
    }
    /*
        CFDecl ::= name_tok goesto_kwd RHSSeq semi_kwd (*)	[non_kwd, start_kwd, precedence_kwd, name_tok, cf_block_close_kwd]

    */
    public static void init_81()
    {
        addA(81,sym_26,fr(p_67));
        addA(81,sym_29,fr(p_67));
        addA(81,sym_22,fr(p_67));
        addA(81,sym_51,fr(p_67));
        addA(81,sym_42,fr(p_67));
        addL(81,sym_48,sym_22,sym_26,sym_29,sym_51,sym_42);
    }
    /*
        CommaSymSeq ::= name_tok comma_kwd CommaSymSeq (*)	[semi_kwd, rparen]

    */
    public static void init_82()
    {
        addA(82,sym_13,fr(p_48));
        addA(82,sym_41,fr(p_48));
        addL(82,sym_48,sym_13,sym_41);
    }
    /*
        TypeName ::= QualifiedName lt_kwd TypeNameSeq gt_kwd (*)	[name_tok, comma_kwd, gt_kwd]

    */
    public static void init_83()
    {
        addA(83,sym_16,fr(p_36));
        addA(83,sym_12,fr(p_36));
        addA(83,sym_51,fr(p_36));
        addL(83,sym_48,sym_51,sym_12,sym_16);
    }
    /*
        RHSFlags ::= (*)	[bar, semi_kwd]
        RHS ::= LabeledSymSeq CodeBlockOpt (*) RHSFlags
        RHSFlags ::= (*) prec_decl_kwd name_tok RHSFlags
        RHSFlags ::= (*) layout_decl_kwd lparen CommaSymSeqOpt rparen RHSFlags

    */
    public static void init_84()
    {
        addA(84,sym_35,fr(p_77));
        addA(84,sym_13,fr(p_77));
        addA(84,sym_6,sh(102));
        addA(84,sym_4,sh(103));
        addG(84,sym_89,sh(101));
        addL(84,sym_48,sym_4,sym_6,sym_35,sym_13);
    }
    /*
        CodeBlockOpt ::= code_block_open_kwd (*) code_t code_block_close_kwd

    */
    public static void init_85()
    {
        addA(85,sym_52,sh(92));
        addL(85,sym_48,sym_52);
    }
    /*
        CFDecl ::= non_kwd terminal_kwd CommaSymSeq semi_kwd (*)	[non_kwd, start_kwd, precedence_kwd, name_tok, cf_block_close_kwd]

    */
    public static void init_86()
    {
        addA(86,sym_26,fr(p_70));
        addA(86,sym_29,fr(p_70));
        addA(86,sym_22,fr(p_70));
        addA(86,sym_51,fr(p_70));
        addA(86,sym_42,fr(p_70));
        addL(86,sym_48,sym_22,sym_26,sym_29,sym_51,sym_42);
    }
    /*
        TypeNameSeq ::= TypeName comma_kwd (*) TypeNameSeq
        TypeNameSeq ::= (*) TypeName comma_kwd TypeNameSeq
        TypeName ::= (*) QualifiedName lt_kwd TypeNameSeq gt_kwd
        QualifiedName ::= (*) name_tok
        TypeName ::= (*) QualifiedName
        TypeNameSeq ::= (*) TypeName
        QualifiedName ::= (*) name_tok wildcard QualifiedName

    */
    public static void init_87()
    {
        addA(87,sym_51,sh(24));
        addG(87,sym_72,sh(22));
        addG(87,sym_71,sh(66));
        addG(87,sym_70,sh(100));
        addL(87,sym_48,sym_51);
    }
    /*
        CommaOrSymSeq ::= name_tok comma_kwd CommaSymSeq (*)	[semi_kwd]

    */
    public static void init_88()
    {
        addA(88,sym_13,fr(p_47));
        addL(88,sym_48,sym_13);
    }
    /*
        RegexRoot ::= (*) slash_kwd slash_kwd
        RegexRoot ::= (*) slash_kwd Regex_R slash_kwd
        LexDecl ::= IgnoreOpt terminal_kwd TypeName name_tok goesto_kwd (*) RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd

    */
    public static void init_89()
    {
        addA(89,sym_14,sh(90));
        addG(89,sym_74,sh(126));
        addL(89,sym_48,sym_14);
    }
    /*
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_R ::= (*) Regex_DR bar Regex_R
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_UR ::= (*) wildcard
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_UR ::= (*) Regex_CHAR
        Regex_R ::= (*) Regex_DR
        RegexRoot ::= slash_kwd (*) slash_kwd
        Regex_DR ::= (*) Regex_UR question Regex_RR
        RegexRoot ::= slash_kwd (*) Regex_R slash_kwd
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_CHAR ::= (*) character

    */
    public static void init_90()
    {
        addA(90,sym_31,sh(117));
        addA(90,sym_50,sh(122));
        addA(90,sym_14,sh(119));
        addA(90,sym_47,sh(121));
        addA(90,sym_39,sh(116));
        addA(90,sym_43,sh(115));
        addG(90,sym_67,sh(113));
        addG(90,sym_60,sh(114));
        addG(90,sym_58,sh(120));
        addG(90,sym_64,sh(118));
        addL(90,eps(),sym_47,sym_50,sym_31,sym_39,sym_14,sym_43);
    }
    /*
        SuperRegexRoot ::= goesto_kwd RegexRoot (*)	[prefix_decl_kwd, in_kwd, semi_kwd, code_block_open_kwd, lt_kwd, gt_kwd]

    */
    public static void init_91()
    {
        addA(91,sym_16,fr(p_60));
        addA(91,sym_15,fr(p_60));
        addA(91,sym_13,fr(p_60));
        addA(91,sym_7,fr(p_60));
        addA(91,sym_40,fr(p_60));
        addA(91,sym_53,fr(p_60));
        addL(91,sym_48,sym_7,sym_53,sym_13,sym_40,sym_15,sym_16);
    }
    /*
        CodeBlockOpt ::= code_block_open_kwd code_t (*) code_block_close_kwd

    */
    public static void init_92()
    {
        addA(92,sym_38,sh(111));
        addL(92,sym_48,sym_38);
    }
    /*
        PrecList ::= in_kwd (*) lparen CommaSymSeqOpt rparen

    */
    public static void init_93()
    {
        addA(93,sym_31,sh(125));
        addL(93,sym_48,sym_31);
    }
    /*
        PrecLists ::= PrecList (*) comma_kwd PrecLists
        PrecLists ::= PrecList (*)	[prefix_decl_kwd, semi_kwd, code_block_open_kwd]

    */
    public static void init_94()
    {
        addA(94,sym_13,fr(p_53));
        addA(94,sym_12,sh(109));
        addA(94,sym_7,fr(p_53));
        addA(94,sym_40,fr(p_53));
        addL(94,sym_48,sym_7,sym_12,sym_13,sym_40);
    }
    /*
        PrecList ::= gt_kwd (*) lparen CommaSymSeqOpt rparen

    */
    public static void init_95()
    {
        addA(95,sym_31,sh(127));
        addL(95,sym_48,sym_31);
    }
    /*
        PrecListsOpt ::= PrecLists (*)	[prefix_decl_kwd, semi_kwd, code_block_open_kwd]

    */
    public static void init_96()
    {
        addA(96,sym_13,fr(p_74));
        addA(96,sym_7,fr(p_74));
        addA(96,sym_40,fr(p_74));
        addL(96,sym_48,sym_7,sym_13,sym_40);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd name_tok SuperRegexRoot PrecListsOpt (*) CodeBlockOpt TerminalFlags semi_kwd
        CodeBlockOpt ::= (*)	[prefix_decl_kwd, semi_kwd]
        CodeBlockOpt ::= (*) code_block_open_kwd code_t code_block_close_kwd

    */
    public static void init_97()
    {
        addA(97,sym_13,fr(p_75));
        addA(97,sym_7,fr(p_75));
        addA(97,sym_40,sh(85));
        addG(97,sym_88,sh(124));
        addL(97,sym_48,sym_7,sym_13,sym_40);
    }
    /*
        PrecList ::= lt_kwd (*) lparen CommaSymSeqOpt rparen

    */
    public static void init_98()
    {
        addA(98,sym_31,sh(123));
        addL(98,sym_48,sym_31);
    }
    /*
        SymSeq ::= name_tok SymSeq (*)	[semi_kwd]

    */
    public static void init_99()
    {
        addA(99,sym_13,fr(p_30));
        addL(99,sym_48,sym_13);
    }
    /*
        TypeNameSeq ::= TypeName comma_kwd TypeNameSeq (*)	[gt_kwd]

    */
    public static void init_100()
    {
        addA(100,sym_16,fr(p_35));
        addL(100,sym_48,sym_16);
    }
    /*
        RHS ::= LabeledSymSeq CodeBlockOpt RHSFlags (*)	[bar, semi_kwd]

    */
    public static void init_101()
    {
        addA(101,sym_35,fr(p_82));
        addA(101,sym_13,fr(p_82));
        addL(101,sym_48,sym_35,sym_13);
    }
    /*
        RHSFlags ::= prec_decl_kwd (*) name_tok RHSFlags

    */
    public static void init_102()
    {
        addA(102,sym_51,sh(110));
        addL(102,sym_48,sym_51);
    }
    /*
        RHSFlags ::= layout_decl_kwd (*) lparen CommaSymSeqOpt rparen RHSFlags

    */
    public static void init_103()
    {
        addA(103,sym_31,sh(108));
        addL(103,sym_48,sym_31);
    }
    /*
        CFDecl ::= non_kwd terminal_kwd TypeName CommaSymSeq semi_kwd (*)	[non_kwd, start_kwd, precedence_kwd, name_tok, cf_block_close_kwd]

    */
    public static void init_104()
    {
        addA(104,sym_26,fr(p_69));
        addA(104,sym_29,fr(p_69));
        addA(104,sym_22,fr(p_69));
        addA(104,sym_51,fr(p_69));
        addA(104,sym_42,fr(p_69));
        addL(104,sym_48,sym_22,sym_26,sym_29,sym_51,sym_42);
    }
    /*
        LabeledSymSeq ::= (*)	[layout_decl_kwd, prec_decl_kwd, bar, semi_kwd, code_block_open_kwd]
        LabeledSymSeq ::= (*) name_tok LabeledSymSeq
        LabeledSymSeq ::= (*) name_tok colon name_tok LabeledSymSeq
        LabeledSymSeq ::= name_tok colon name_tok (*) LabeledSymSeq

    */
    public static void init_105()
    {
        addA(105,sym_35,fr(p_85));
        addA(105,sym_13,fr(p_85));
        addA(105,sym_6,fr(p_85));
        addA(105,sym_51,sh(63));
        addA(105,sym_40,fr(p_85));
        addA(105,sym_4,fr(p_85));
        addG(105,sym_92,sh(112));
        addL(105,sym_48,sym_4,sym_6,sym_51,sym_35,sym_13,sym_40);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq (*) rparen goesto_kwd name_tok semi_kwd
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq (*) rparen code_block_open_kwd code_t code_block_close_kwd semi_kwd

    */
    public static void init_106()
    {
        addA(106,sym_41,sh(128));
        addL(106,sym_48,sym_41);
    }
    /*
        RHSSeq ::= RHS bar RHSSeq (*)	[semi_kwd]

    */
    public static void init_107()
    {
        addA(107,sym_13,fr(p_80));
        addL(107,sym_48,sym_13);
    }
    /*
        RHSFlags ::= layout_decl_kwd lparen (*) CommaSymSeqOpt rparen RHSFlags
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaSymSeqOpt ::= (*) CommaSymSeq
        CommaSymSeqOpt ::= (*)	[rparen]
        CommaSymSeq ::= (*) name_tok

    */
    public static void init_108()
    {
        addA(108,sym_41,fr(p_52));
        addA(108,sym_51,sh(48));
        addG(108,sym_79,sh(146));
        addG(108,sym_77,sh(135));
        addL(108,sym_48,sym_51,sym_41);
    }
    /*
        PrecList ::= (*) in_kwd lparen CommaSymSeqOpt rparen
        PrecLists ::= (*) PrecList comma_kwd PrecLists
        PrecLists ::= PrecList comma_kwd (*) PrecLists
        PrecLists ::= (*) PrecList
        PrecList ::= (*) gt_kwd lparen CommaSymSeqOpt rparen
        PrecList ::= (*) lt_kwd lparen CommaSymSeqOpt rparen

    */
    public static void init_109()
    {
        addA(109,sym_16,sh(95));
        addA(109,sym_15,sh(98));
        addA(109,sym_53,sh(93));
        addG(109,sym_81,sh(94));
        addG(109,sym_80,sh(129));
        addL(109,sym_48,sym_53,sym_15,sym_16);
    }
    /*
        RHSFlags ::= (*)	[bar, semi_kwd]
        RHSFlags ::= prec_decl_kwd name_tok (*) RHSFlags
        RHSFlags ::= (*) prec_decl_kwd name_tok RHSFlags
        RHSFlags ::= (*) layout_decl_kwd lparen CommaSymSeqOpt rparen RHSFlags

    */
    public static void init_110()
    {
        addA(110,sym_35,fr(p_77));
        addA(110,sym_13,fr(p_77));
        addA(110,sym_6,sh(102));
        addA(110,sym_4,sh(103));
        addG(110,sym_89,sh(137));
        addL(110,sym_48,sym_4,sym_6,sym_35,sym_13);
    }
    /*
        CodeBlockOpt ::= code_block_open_kwd code_t code_block_close_kwd (*)	[layout_decl_kwd, prec_decl_kwd, prefix_decl_kwd, bar, semi_kwd]

    */
    public static void init_111()
    {
        addA(111,sym_35,fr(p_76));
        addA(111,sym_13,fr(p_76));
        addA(111,sym_7,fr(p_76));
        addA(111,sym_6,fr(p_76));
        addA(111,sym_4,fr(p_76));
        addL(111,sym_48,sym_4,sym_6,sym_7,sym_35,sym_13);
    }
    /*
        LabeledSymSeq ::= name_tok colon name_tok LabeledSymSeq (*)	[layout_decl_kwd, prec_decl_kwd, bar, semi_kwd, code_block_open_kwd]

    */
    public static void init_112()
    {
        addA(112,sym_35,fr(p_83));
        addA(112,sym_13,fr(p_83));
        addA(112,sym_6,fr(p_83));
        addA(112,sym_40,fr(p_83));
        addA(112,sym_4,fr(p_83));
        addL(112,sym_48,sym_4,sym_6,sym_35,sym_13,sym_40);
    }
    /*
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_DR ::= Regex_UR (*) Regex_RR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_UR ::= (*) wildcard
        Regex_UR ::= (*) Regex_CHAR
        Regex_DR ::= Regex_UR (*) question Regex_RR
        Regex_RR ::= (*) Regex_DR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_RR ::= (*)	[bar, rparen, slash_kwd]
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_DR ::= Regex_UR (*) plus Regex_RR
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_CHAR ::= (*) character
        Regex_DR ::= Regex_UR (*) star Regex_RR

    */
    public static void init_113()
    {
        addA(113,sym_43,sh(115));
        addA(113,sym_31,sh(117));
        addA(113,sym_35,fr(p_23));
        addA(113,sym_25,sh(139));
        addA(113,sym_14,fr(p_23));
        addA(113,sym_17,sh(142));
        addA(113,sym_21,sh(141));
        addA(113,sym_50,sh(122));
        addA(113,sym_47,sh(121));
        addA(113,sym_41,fr(p_23));
        addA(113,sym_39,sh(116));
        addG(113,sym_67,sh(113));
        addG(113,sym_60,sh(140));
        addG(113,sym_66,sh(138));
        addG(113,sym_64,sh(118));
        addL(113,eps(),sym_17,sym_47,sym_21,sym_25,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        Regex_R ::= Regex_DR (*) bar Regex_R
        Regex_R ::= Regex_DR (*)	[rparen, slash_kwd]

    */
    public static void init_114()
    {
        addA(114,sym_35,sh(147));
        addA(114,sym_14,fr(p_7));
        addA(114,sym_41,fr(p_7));
        addL(114,eps(),sym_35,sym_14,sym_41);
    }
    /*
        Regex_G ::= (*) Regex_UG Regex_RG
        Regex_UR ::= lbrack (*) Regex_G rbrack
        Regex_UG ::= (*) Regex_CHAR dash Regex_CHAR
        Regex_UR ::= lbrack (*) colon termname colon rbrack
        Regex_UG ::= (*) Regex_CHAR
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= lbrack (*) not Regex_G rbrack
        Regex_CHAR ::= (*) character

    */
    public static void init_115()
    {
        addA(115,sym_33,sh(134));
        addA(115,sym_50,sh(122));
        addA(115,sym_47,sh(121));
        addA(115,sym_23,sh(133));
        addG(115,sym_75,sh(130));
        addG(115,sym_65,sh(131));
        addG(115,sym_64,sh(132));
        addL(115,eps(),sym_47,sym_23,sym_50,sym_33);
    }
    /*
        Regex_UR ::= wildcard (*)	[star, escaped, plus, question, character, lparen, bar, wildcard, rparen, slash_kwd, lbrack]

    */
    public static void init_116()
    {
        addA(116,sym_43,fr(p_27));
        addA(116,sym_31,fr(p_27));
        addA(116,sym_35,fr(p_27));
        addA(116,sym_25,fr(p_27));
        addA(116,sym_14,fr(p_27));
        addA(116,sym_17,fr(p_27));
        addA(116,sym_21,fr(p_27));
        addA(116,sym_50,fr(p_27));
        addA(116,sym_47,fr(p_27));
        addA(116,sym_41,fr(p_27));
        addA(116,sym_39,fr(p_27));
        addL(116,eps(),sym_17,sym_47,sym_21,sym_25,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_R ::= (*) Regex_DR bar Regex_R
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_UR ::= lparen (*) Regex_R rparen
        Regex_UR ::= (*) wildcard
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_UR ::= (*) Regex_CHAR
        Regex_R ::= (*) Regex_DR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_CHAR ::= (*) character

    */
    public static void init_117()
    {
        addA(117,sym_31,sh(117));
        addA(117,sym_50,sh(122));
        addA(117,sym_47,sh(121));
        addA(117,sym_39,sh(116));
        addA(117,sym_43,sh(115));
        addG(117,sym_67,sh(113));
        addG(117,sym_60,sh(114));
        addG(117,sym_58,sh(149));
        addG(117,sym_64,sh(118));
        addL(117,eps(),sym_47,sym_50,sym_31,sym_39,sym_43);
    }
    /*
        Regex_UR ::= Regex_CHAR (*)	[star, escaped, plus, question, character, lparen, bar, wildcard, rparen, slash_kwd, lbrack]

    */
    public static void init_118()
    {
        addA(118,sym_43,fr(p_26));
        addA(118,sym_31,fr(p_26));
        addA(118,sym_35,fr(p_26));
        addA(118,sym_25,fr(p_26));
        addA(118,sym_14,fr(p_26));
        addA(118,sym_17,fr(p_26));
        addA(118,sym_21,fr(p_26));
        addA(118,sym_50,fr(p_26));
        addA(118,sym_47,fr(p_26));
        addA(118,sym_41,fr(p_26));
        addA(118,sym_39,fr(p_26));
        addL(118,eps(),sym_17,sym_47,sym_21,sym_25,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        RegexRoot ::= slash_kwd slash_kwd (*)	[prefix_decl_kwd, in_kwd, semi_kwd, code_block_open_kwd, lt_kwd, gt_kwd]

    */
    public static void init_119()
    {
        addA(119,sym_16,fr(p_42));
        addA(119,sym_15,fr(p_42));
        addA(119,sym_13,fr(p_42));
        addA(119,sym_7,fr(p_42));
        addA(119,sym_40,fr(p_42));
        addA(119,sym_53,fr(p_42));
        addL(119,sym_48,sym_7,sym_53,sym_13,sym_40,sym_15,sym_16);
    }
    /*
        RegexRoot ::= slash_kwd Regex_R (*) slash_kwd

    */
    public static void init_120()
    {
        addA(120,sym_14,sh(148));
        addL(120,eps(),sym_14);
    }
    /*
        Regex_CHAR ::= escaped (*)	[star, plus, question, dash, lparen, bar, rbrack, wildcard, rparen, lbrack, escaped, character, slash_kwd]

    */
    public static void init_121()
    {
        addA(121,sym_43,fr(p_20));
        addA(121,sym_31,fr(p_20));
        addA(121,sym_37,fr(p_20));
        addA(121,sym_35,fr(p_20));
        addA(121,sym_25,fr(p_20));
        addA(121,sym_28,fr(p_20));
        addA(121,sym_14,fr(p_20));
        addA(121,sym_17,fr(p_20));
        addA(121,sym_21,fr(p_20));
        addA(121,sym_50,fr(p_20));
        addA(121,sym_47,fr(p_20));
        addA(121,sym_41,fr(p_20));
        addA(121,sym_39,fr(p_20));
        addL(121,eps(),sym_17,sym_21,sym_25,sym_28,sym_31,sym_35,sym_37,sym_39,sym_41,sym_43,sym_47,sym_50,sym_14);
    }
    /*
        Regex_CHAR ::= character (*)	[star, plus, question, dash, lparen, bar, rbrack, wildcard, rparen, lbrack, escaped, character, slash_kwd]

    */
    public static void init_122()
    {
        addA(122,sym_43,fr(p_19));
        addA(122,sym_31,fr(p_19));
        addA(122,sym_37,fr(p_19));
        addA(122,sym_35,fr(p_19));
        addA(122,sym_25,fr(p_19));
        addA(122,sym_28,fr(p_19));
        addA(122,sym_14,fr(p_19));
        addA(122,sym_17,fr(p_19));
        addA(122,sym_21,fr(p_19));
        addA(122,sym_50,fr(p_19));
        addA(122,sym_47,fr(p_19));
        addA(122,sym_41,fr(p_19));
        addA(122,sym_39,fr(p_19));
        addL(122,eps(),sym_17,sym_21,sym_25,sym_28,sym_31,sym_35,sym_37,sym_39,sym_41,sym_43,sym_47,sym_50,sym_14);
    }
    /*
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaSymSeqOpt ::= (*) CommaSymSeq
        CommaSymSeqOpt ::= (*)	[rparen]
        CommaSymSeq ::= (*) name_tok
        PrecList ::= lt_kwd lparen (*) CommaSymSeqOpt rparen

    */
    public static void init_123()
    {
        addA(123,sym_41,fr(p_52));
        addA(123,sym_51,sh(48));
        addG(123,sym_79,sh(143));
        addG(123,sym_77,sh(135));
        addL(123,sym_48,sym_51,sym_41);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd name_tok SuperRegexRoot PrecListsOpt CodeBlockOpt (*) TerminalFlags semi_kwd
        TerminalFlags ::= (*)	[semi_kwd]
        TerminalFlags ::= (*) prefix_decl_kwd name_tok TerminalFlags

    */
    public static void init_124()
    {
        addA(124,sym_13,fr(p_59));
        addA(124,sym_7,sh(145));
        addG(124,sym_82,sh(144));
        addL(124,sym_48,sym_7,sym_13);
    }
    /*
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaSymSeqOpt ::= (*) CommaSymSeq
        CommaSymSeqOpt ::= (*)	[rparen]
        CommaSymSeq ::= (*) name_tok
        PrecList ::= in_kwd lparen (*) CommaSymSeqOpt rparen

    */
    public static void init_125()
    {
        addA(125,sym_41,fr(p_52));
        addA(125,sym_51,sh(48));
        addG(125,sym_79,sh(136));
        addG(125,sym_77,sh(135));
        addL(125,sym_48,sym_51,sym_41);
    }
    /*
        PrecList ::= (*) in_kwd lparen CommaSymSeqOpt rparen
        PrecLists ::= (*) PrecList comma_kwd PrecLists
        PrecLists ::= (*) PrecList
        PrecList ::= (*) gt_kwd lparen CommaSymSeqOpt rparen
        LexDecl ::= IgnoreOpt terminal_kwd TypeName name_tok goesto_kwd RegexRoot (*) PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd
        PrecListsOpt ::= (*)	[prefix_decl_kwd, semi_kwd, code_block_open_kwd]
        PrecListsOpt ::= (*) PrecLists
        PrecList ::= (*) lt_kwd lparen CommaSymSeqOpt rparen

    */
    public static void init_126()
    {
        addA(126,sym_16,sh(95));
        addA(126,sym_15,sh(98));
        addA(126,sym_13,fr(p_73));
        addA(126,sym_7,fr(p_73));
        addA(126,sym_40,fr(p_73));
        addA(126,sym_53,sh(93));
        addG(126,sym_81,sh(94));
        addG(126,sym_80,sh(96));
        addG(126,sym_87,sh(150));
        addL(126,sym_48,sym_7,sym_53,sym_13,sym_40,sym_15,sym_16);
    }
    /*
        CommaSymSeq ::= (*) name_tok comma_kwd CommaSymSeq
        CommaSymSeqOpt ::= (*) CommaSymSeq
        PrecList ::= gt_kwd lparen (*) CommaSymSeqOpt rparen
        CommaSymSeqOpt ::= (*)	[rparen]
        CommaSymSeq ::= (*) name_tok

    */
    public static void init_127()
    {
        addA(127,sym_41,fr(p_52));
        addA(127,sym_51,sh(48));
        addG(127,sym_79,sh(151));
        addG(127,sym_77,sh(135));
        addL(127,sym_48,sym_51,sym_41);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen (*) goesto_kwd name_tok semi_kwd
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen (*) code_block_open_kwd code_t code_block_close_kwd semi_kwd

    */
    public static void init_128()
    {
        addA(128,sym_11,sh(152));
        addA(128,sym_40,sh(153));
        addL(128,sym_48,sym_11,sym_40);
    }
    /*
        PrecLists ::= PrecList comma_kwd PrecLists (*)	[prefix_decl_kwd, semi_kwd, code_block_open_kwd]

    */
    public static void init_129()
    {
        addA(129,sym_13,fr(p_54));
        addA(129,sym_7,fr(p_54));
        addA(129,sym_40,fr(p_54));
        addL(129,sym_48,sym_7,sym_13,sym_40);
    }
    /*
        Regex_G ::= (*) Regex_UG Regex_RG
        Regex_UG ::= (*) Regex_CHAR dash Regex_CHAR
        Regex_RG ::= (*) Regex_G
        Regex_UG ::= (*) Regex_CHAR
        Regex_CHAR ::= (*) escaped
        Regex_RG ::= (*)	[rbrack]
        Regex_G ::= Regex_UG (*) Regex_RG
        Regex_CHAR ::= (*) character

    */
    public static void init_130()
    {
        addA(130,sym_37,fr(p_41));
        addA(130,sym_50,sh(122));
        addA(130,sym_47,sh(121));
        addG(130,sym_73,sh(165));
        addG(130,sym_75,sh(130));
        addG(130,sym_65,sh(164));
        addG(130,sym_64,sh(132));
        addL(130,eps(),sym_47,sym_50,sym_37);
    }
    /*
        Regex_UR ::= lbrack Regex_G (*) rbrack

    */
    public static void init_131()
    {
        addA(131,sym_37,sh(166));
        addL(131,eps(),sym_37);
    }
    /*
        Regex_UG ::= Regex_CHAR (*) dash Regex_CHAR
        Regex_UG ::= Regex_CHAR (*)	[escaped, character, rbrack]

    */
    public static void init_132()
    {
        addA(132,sym_37,fr(p_45));
        addA(132,sym_50,fr(p_45));
        addA(132,sym_47,fr(p_45));
        addA(132,sym_28,sh(168));
        addL(132,eps(),sym_47,sym_50,sym_28,sym_37);
    }
    /*
        Regex_UR ::= lbrack colon (*) termname colon rbrack

    */
    public static void init_133()
    {
        addA(133,sym_46,sh(173));
        addL(133,eps(),sym_46);
    }
    /*
        Regex_G ::= (*) Regex_UG Regex_RG
        Regex_UG ::= (*) Regex_CHAR dash Regex_CHAR
        Regex_UG ::= (*) Regex_CHAR
        Regex_CHAR ::= (*) escaped
        Regex_CHAR ::= (*) character
        Regex_UR ::= lbrack not (*) Regex_G rbrack

    */
    public static void init_134()
    {
        addA(134,sym_50,sh(122));
        addA(134,sym_47,sh(121));
        addG(134,sym_75,sh(130));
        addG(134,sym_65,sh(170));
        addG(134,sym_64,sh(132));
        addL(134,eps(),sym_47,sym_50);
    }
    /*
        CommaSymSeqOpt ::= CommaSymSeq (*)	[rparen]

    */
    public static void init_135()
    {
        addA(135,sym_41,fr(p_51));
        addL(135,sym_48,sym_41);
    }
    /*
        PrecList ::= in_kwd lparen CommaSymSeqOpt (*) rparen

    */
    public static void init_136()
    {
        addA(136,sym_41,sh(159));
        addL(136,sym_48,sym_41);
    }
    /*
        RHSFlags ::= prec_decl_kwd name_tok RHSFlags (*)	[bar, semi_kwd]

    */
    public static void init_137()
    {
        addA(137,sym_35,fr(p_79));
        addA(137,sym_13,fr(p_79));
        addL(137,sym_48,sym_35,sym_13);
    }
    /*
        Regex_DR ::= Regex_UR Regex_RR (*)	[bar, rparen, slash_kwd]

    */
    public static void init_138()
    {
        addA(138,sym_35,fr(p_14));
        addA(138,sym_14,fr(p_14));
        addA(138,sym_41,fr(p_14));
        addL(138,eps(),sym_35,sym_14,sym_41);
    }
    /*
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_UR ::= (*) wildcard
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_UR ::= (*) Regex_CHAR
        Regex_DR ::= Regex_UR question (*) Regex_RR
        Regex_RR ::= (*) Regex_DR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_RR ::= (*)	[bar, rparen, slash_kwd]
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_CHAR ::= (*) character

    */
    public static void init_139()
    {
        addA(139,sym_31,sh(117));
        addA(139,sym_50,sh(122));
        addA(139,sym_35,fr(p_23));
        addA(139,sym_14,fr(p_23));
        addA(139,sym_47,sh(121));
        addA(139,sym_41,fr(p_23));
        addA(139,sym_39,sh(116));
        addA(139,sym_43,sh(115));
        addG(139,sym_67,sh(113));
        addG(139,sym_60,sh(140));
        addG(139,sym_66,sh(171));
        addG(139,sym_64,sh(118));
        addL(139,eps(),sym_47,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        Regex_RR ::= Regex_DR (*)	[bar, rparen, slash_kwd]

    */
    public static void init_140()
    {
        addA(140,sym_35,fr(p_22));
        addA(140,sym_14,fr(p_22));
        addA(140,sym_41,fr(p_22));
        addL(140,eps(),sym_35,sym_14,sym_41);
    }
    /*
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_UR ::= (*) wildcard
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_UR ::= (*) Regex_CHAR
        Regex_RR ::= (*) Regex_DR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_RR ::= (*)	[bar, rparen, slash_kwd]
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_CHAR ::= (*) escaped
        Regex_DR ::= Regex_UR plus (*) Regex_RR
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_CHAR ::= (*) character

    */
    public static void init_141()
    {
        addA(141,sym_31,sh(117));
        addA(141,sym_50,sh(122));
        addA(141,sym_35,fr(p_23));
        addA(141,sym_14,fr(p_23));
        addA(141,sym_47,sh(121));
        addA(141,sym_41,fr(p_23));
        addA(141,sym_39,sh(116));
        addA(141,sym_43,sh(115));
        addG(141,sym_67,sh(113));
        addG(141,sym_60,sh(140));
        addG(141,sym_66,sh(160));
        addG(141,sym_64,sh(118));
        addL(141,eps(),sym_47,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_UR ::= (*) wildcard
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_UR ::= (*) Regex_CHAR
        Regex_RR ::= (*) Regex_DR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_DR ::= Regex_UR star (*) Regex_RR
        Regex_RR ::= (*)	[bar, rparen, slash_kwd]
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_CHAR ::= (*) character

    */
    public static void init_142()
    {
        addA(142,sym_31,sh(117));
        addA(142,sym_50,sh(122));
        addA(142,sym_35,fr(p_23));
        addA(142,sym_14,fr(p_23));
        addA(142,sym_47,sh(121));
        addA(142,sym_41,fr(p_23));
        addA(142,sym_39,sh(116));
        addA(142,sym_43,sh(115));
        addG(142,sym_67,sh(113));
        addG(142,sym_60,sh(140));
        addG(142,sym_66,sh(154));
        addG(142,sym_64,sh(118));
        addL(142,eps(),sym_47,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        PrecList ::= lt_kwd lparen CommaSymSeqOpt (*) rparen

    */
    public static void init_143()
    {
        addA(143,sym_41,sh(161));
        addL(143,sym_48,sym_41);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd name_tok SuperRegexRoot PrecListsOpt CodeBlockOpt TerminalFlags (*) semi_kwd

    */
    public static void init_144()
    {
        addA(144,sym_13,sh(162));
        addL(144,sym_48,sym_13);
    }
    /*
        TerminalFlags ::= prefix_decl_kwd (*) name_tok TerminalFlags

    */
    public static void init_145()
    {
        addA(145,sym_51,sh(156));
        addL(145,sym_48,sym_51);
    }
    /*
        RHSFlags ::= layout_decl_kwd lparen CommaSymSeqOpt (*) rparen RHSFlags

    */
    public static void init_146()
    {
        addA(146,sym_41,sh(155));
        addL(146,sym_48,sym_41);
    }
    /*
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_R ::= (*) Regex_DR bar Regex_R
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_R ::= Regex_DR bar (*) Regex_R
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_UR ::= (*) wildcard
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_UR ::= (*) Regex_CHAR
        Regex_R ::= (*) Regex_DR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_CHAR ::= (*) character

    */
    public static void init_147()
    {
        addA(147,sym_31,sh(117));
        addA(147,sym_50,sh(122));
        addA(147,sym_47,sh(121));
        addA(147,sym_39,sh(116));
        addA(147,sym_43,sh(115));
        addG(147,sym_67,sh(113));
        addG(147,sym_60,sh(114));
        addG(147,sym_58,sh(167));
        addG(147,sym_64,sh(118));
        addL(147,eps(),sym_47,sym_50,sym_31,sym_39,sym_43);
    }
    /*
        RegexRoot ::= slash_kwd Regex_R slash_kwd (*)	[prefix_decl_kwd, in_kwd, semi_kwd, code_block_open_kwd, lt_kwd, gt_kwd]

    */
    public static void init_148()
    {
        addA(148,sym_16,fr(p_43));
        addA(148,sym_15,fr(p_43));
        addA(148,sym_13,fr(p_43));
        addA(148,sym_7,fr(p_43));
        addA(148,sym_40,fr(p_43));
        addA(148,sym_53,fr(p_43));
        addL(148,sym_48,sym_7,sym_53,sym_13,sym_40,sym_15,sym_16);
    }
    /*
        Regex_UR ::= lparen Regex_R (*) rparen

    */
    public static void init_149()
    {
        addA(149,sym_41,sh(169));
        addL(149,eps(),sym_41);
    }
    /*
        CodeBlockOpt ::= (*)	[prefix_decl_kwd, semi_kwd]
        LexDecl ::= IgnoreOpt terminal_kwd TypeName name_tok goesto_kwd RegexRoot PrecListsOpt (*) CodeBlockOpt TerminalFlags semi_kwd
        CodeBlockOpt ::= (*) code_block_open_kwd code_t code_block_close_kwd

    */
    public static void init_150()
    {
        addA(150,sym_13,fr(p_75));
        addA(150,sym_7,fr(p_75));
        addA(150,sym_40,sh(85));
        addG(150,sym_88,sh(158));
        addL(150,sym_48,sym_7,sym_13,sym_40);
    }
    /*
        PrecList ::= gt_kwd lparen CommaSymSeqOpt (*) rparen

    */
    public static void init_151()
    {
        addA(151,sym_41,sh(172));
        addL(151,sym_48,sym_41);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen goesto_kwd (*) name_tok semi_kwd

    */
    public static void init_152()
    {
        addA(152,sym_51,sh(163));
        addL(152,sym_48,sym_51);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen code_block_open_kwd (*) code_t code_block_close_kwd semi_kwd

    */
    public static void init_153()
    {
        addA(153,sym_52,sh(157));
        addL(153,sym_48,sym_52);
    }
    /*
        Regex_DR ::= Regex_UR star Regex_RR (*)	[bar, rparen, slash_kwd]

    */
    public static void init_154()
    {
        addA(154,sym_35,fr(p_12));
        addA(154,sym_14,fr(p_12));
        addA(154,sym_41,fr(p_12));
        addL(154,eps(),sym_35,sym_14,sym_41);
    }
    /*
        RHSFlags ::= (*)	[bar, semi_kwd]
        RHSFlags ::= layout_decl_kwd lparen CommaSymSeqOpt rparen (*) RHSFlags
        RHSFlags ::= (*) prec_decl_kwd name_tok RHSFlags
        RHSFlags ::= (*) layout_decl_kwd lparen CommaSymSeqOpt rparen RHSFlags

    */
    public static void init_155()
    {
        addA(155,sym_35,fr(p_77));
        addA(155,sym_13,fr(p_77));
        addA(155,sym_6,sh(102));
        addA(155,sym_4,sh(103));
        addG(155,sym_89,sh(177));
        addL(155,sym_48,sym_4,sym_6,sym_35,sym_13);
    }
    /*
        TerminalFlags ::= (*)	[semi_kwd]
        TerminalFlags ::= (*) prefix_decl_kwd name_tok TerminalFlags
        TerminalFlags ::= prefix_decl_kwd name_tok (*) TerminalFlags

    */
    public static void init_156()
    {
        addA(156,sym_13,fr(p_59));
        addA(156,sym_7,sh(145));
        addG(156,sym_82,sh(176));
        addL(156,sym_48,sym_7,sym_13);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen code_block_open_kwd code_t (*) code_block_close_kwd semi_kwd

    */
    public static void init_157()
    {
        addA(157,sym_38,sh(178));
        addL(157,sym_48,sym_38);
    }
    /*
        TerminalFlags ::= (*)	[semi_kwd]
        TerminalFlags ::= (*) prefix_decl_kwd name_tok TerminalFlags
        LexDecl ::= IgnoreOpt terminal_kwd TypeName name_tok goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt (*) TerminalFlags semi_kwd

    */
    public static void init_158()
    {
        addA(158,sym_13,fr(p_59));
        addA(158,sym_7,sh(145));
        addG(158,sym_82,sh(175));
        addL(158,sym_48,sym_7,sym_13);
    }
    /*
        PrecList ::= in_kwd lparen CommaSymSeqOpt rparen (*)	[prefix_decl_kwd, comma_kwd, semi_kwd, code_block_open_kwd]

    */
    public static void init_159()
    {
        addA(159,sym_13,fr(p_56));
        addA(159,sym_12,fr(p_56));
        addA(159,sym_7,fr(p_56));
        addA(159,sym_40,fr(p_56));
        addL(159,sym_48,sym_7,sym_12,sym_13,sym_40);
    }
    /*
        Regex_DR ::= Regex_UR plus Regex_RR (*)	[bar, rparen, slash_kwd]

    */
    public static void init_160()
    {
        addA(160,sym_35,fr(p_13));
        addA(160,sym_14,fr(p_13));
        addA(160,sym_41,fr(p_13));
        addL(160,eps(),sym_35,sym_14,sym_41);
    }
    /*
        PrecList ::= lt_kwd lparen CommaSymSeqOpt rparen (*)	[prefix_decl_kwd, comma_kwd, semi_kwd, code_block_open_kwd]

    */
    public static void init_161()
    {
        addA(161,sym_13,fr(p_55));
        addA(161,sym_12,fr(p_55));
        addA(161,sym_7,fr(p_55));
        addA(161,sym_40,fr(p_55));
        addL(161,sym_48,sym_7,sym_12,sym_13,sym_40);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd name_tok SuperRegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd (*)	[class_kwd, terminal_kwd, ignore_kwd, disambiguate_kwd, lex_block_close_kwd]

    */
    public static void init_162()
    {
        addA(162,sym_30,fr(p_61));
        addA(162,sym_27,fr(p_61));
        addA(162,sym_18,fr(p_61));
        addA(162,sym_19,fr(p_61));
        addA(162,sym_20,fr(p_61));
        addL(162,sym_48,sym_18,sym_19,sym_20,sym_27,sym_30);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen goesto_kwd name_tok (*) semi_kwd

    */
    public static void init_163()
    {
        addA(163,sym_13,sh(179));
        addL(163,sym_48,sym_13);
    }
    /*
        Regex_RG ::= Regex_G (*)	[rbrack]

    */
    public static void init_164()
    {
        addA(164,sym_37,fr(p_40));
        addL(164,eps(),sym_37);
    }
    /*
        Regex_G ::= Regex_UG Regex_RG (*)	[rbrack]

    */
    public static void init_165()
    {
        addA(165,sym_37,fr(p_21));
        addL(165,eps(),sym_37);
    }
    /*
        Regex_UR ::= lbrack Regex_G rbrack (*)	[star, escaped, plus, question, character, lparen, bar, wildcard, rparen, slash_kwd, lbrack]

    */
    public static void init_166()
    {
        addA(166,sym_43,fr(p_29));
        addA(166,sym_31,fr(p_29));
        addA(166,sym_35,fr(p_29));
        addA(166,sym_25,fr(p_29));
        addA(166,sym_14,fr(p_29));
        addA(166,sym_17,fr(p_29));
        addA(166,sym_21,fr(p_29));
        addA(166,sym_50,fr(p_29));
        addA(166,sym_47,fr(p_29));
        addA(166,sym_41,fr(p_29));
        addA(166,sym_39,fr(p_29));
        addL(166,eps(),sym_17,sym_47,sym_21,sym_25,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        Regex_R ::= Regex_DR bar Regex_R (*)	[rparen, slash_kwd]

    */
    public static void init_167()
    {
        addA(167,sym_14,fr(p_8));
        addA(167,sym_41,fr(p_8));
        addL(167,eps(),sym_14,sym_41);
    }
    /*
        Regex_UG ::= Regex_CHAR dash (*) Regex_CHAR
        Regex_CHAR ::= (*) escaped
        Regex_CHAR ::= (*) character

    */
    public static void init_168()
    {
        addA(168,sym_50,sh(122));
        addA(168,sym_47,sh(121));
        addG(168,sym_64,sh(174));
        addL(168,eps(),sym_47,sym_50);
    }
    /*
        Regex_UR ::= lparen Regex_R rparen (*)	[star, escaped, plus, question, character, lparen, bar, wildcard, rparen, slash_kwd, lbrack]

    */
    public static void init_169()
    {
        addA(169,sym_43,fr(p_24));
        addA(169,sym_31,fr(p_24));
        addA(169,sym_35,fr(p_24));
        addA(169,sym_25,fr(p_24));
        addA(169,sym_14,fr(p_24));
        addA(169,sym_17,fr(p_24));
        addA(169,sym_21,fr(p_24));
        addA(169,sym_50,fr(p_24));
        addA(169,sym_47,fr(p_24));
        addA(169,sym_41,fr(p_24));
        addA(169,sym_39,fr(p_24));
        addL(169,eps(),sym_17,sym_47,sym_21,sym_25,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        Regex_UR ::= lbrack not Regex_G (*) rbrack

    */
    public static void init_170()
    {
        addA(170,sym_37,sh(181));
        addL(170,eps(),sym_37);
    }
    /*
        Regex_DR ::= Regex_UR question Regex_RR (*)	[bar, rparen, slash_kwd]

    */
    public static void init_171()
    {
        addA(171,sym_35,fr(p_11));
        addA(171,sym_14,fr(p_11));
        addA(171,sym_41,fr(p_11));
        addL(171,eps(),sym_35,sym_14,sym_41);
    }
    /*
        PrecList ::= gt_kwd lparen CommaSymSeqOpt rparen (*)	[prefix_decl_kwd, comma_kwd, semi_kwd, code_block_open_kwd]

    */
    public static void init_172()
    {
        addA(172,sym_13,fr(p_57));
        addA(172,sym_12,fr(p_57));
        addA(172,sym_7,fr(p_57));
        addA(172,sym_40,fr(p_57));
        addL(172,sym_48,sym_7,sym_12,sym_13,sym_40);
    }
    /*
        Regex_UR ::= lbrack colon termname (*) colon rbrack

    */
    public static void init_173()
    {
        addA(173,sym_23,sh(180));
        addL(173,eps(),sym_23);
    }
    /*
        Regex_UG ::= Regex_CHAR dash Regex_CHAR (*)	[escaped, character, rbrack]

    */
    public static void init_174()
    {
        addA(174,sym_37,fr(p_44));
        addA(174,sym_50,fr(p_44));
        addA(174,sym_47,fr(p_44));
        addL(174,eps(),sym_47,sym_50,sym_37);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd TypeName name_tok goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags (*) semi_kwd

    */
    public static void init_175()
    {
        addA(175,sym_13,sh(184));
        addL(175,sym_48,sym_13);
    }
    /*
        TerminalFlags ::= prefix_decl_kwd name_tok TerminalFlags (*)	[semi_kwd]

    */
    public static void init_176()
    {
        addA(176,sym_13,fr(p_58));
        addL(176,sym_48,sym_13);
    }
    /*
        RHSFlags ::= layout_decl_kwd lparen CommaSymSeqOpt rparen RHSFlags (*)	[bar, semi_kwd]

    */
    public static void init_177()
    {
        addA(177,sym_35,fr(p_78));
        addA(177,sym_13,fr(p_78));
        addL(177,sym_48,sym_35,sym_13);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen code_block_open_kwd code_t code_block_close_kwd (*) semi_kwd

    */
    public static void init_178()
    {
        addA(178,sym_13,sh(182));
        addL(178,sym_48,sym_13);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen goesto_kwd name_tok semi_kwd (*)	[class_kwd, terminal_kwd, ignore_kwd, disambiguate_kwd, lex_block_close_kwd]

    */
    public static void init_179()
    {
        addA(179,sym_30,fr(p_64));
        addA(179,sym_27,fr(p_64));
        addA(179,sym_18,fr(p_64));
        addA(179,sym_19,fr(p_64));
        addA(179,sym_20,fr(p_64));
        addL(179,sym_48,sym_18,sym_19,sym_20,sym_27,sym_30);
    }
    /*
        Regex_UR ::= lbrack colon termname colon (*) rbrack

    */
    public static void init_180()
    {
        addA(180,sym_37,sh(183));
        addL(180,eps(),sym_37);
    }
    /*
        Regex_UR ::= lbrack not Regex_G rbrack (*)	[star, escaped, plus, question, character, lparen, bar, wildcard, rparen, slash_kwd, lbrack]

    */
    public static void init_181()
    {
        addA(181,sym_43,fr(p_25));
        addA(181,sym_31,fr(p_25));
        addA(181,sym_35,fr(p_25));
        addA(181,sym_25,fr(p_25));
        addA(181,sym_14,fr(p_25));
        addA(181,sym_17,fr(p_25));
        addA(181,sym_21,fr(p_25));
        addA(181,sym_50,fr(p_25));
        addA(181,sym_47,fr(p_25));
        addA(181,sym_41,fr(p_25));
        addA(181,sym_39,fr(p_25));
        addL(181,eps(),sym_17,sym_47,sym_21,sym_25,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        LexDecl ::= disambiguate_kwd name_tok colon_kwd lparen CommaSymSeq rparen code_block_open_kwd code_t code_block_close_kwd semi_kwd (*)	[class_kwd, terminal_kwd, ignore_kwd, disambiguate_kwd, lex_block_close_kwd]

    */
    public static void init_182()
    {
        addA(182,sym_30,fr(p_65));
        addA(182,sym_27,fr(p_65));
        addA(182,sym_18,fr(p_65));
        addA(182,sym_19,fr(p_65));
        addA(182,sym_20,fr(p_65));
        addL(182,sym_48,sym_18,sym_19,sym_20,sym_27,sym_30);
    }
    /*
        Regex_UR ::= lbrack colon termname colon rbrack (*)	[star, escaped, plus, question, character, lparen, bar, wildcard, rparen, slash_kwd, lbrack]

    */
    public static void init_183()
    {
        addA(183,sym_43,fr(p_28));
        addA(183,sym_31,fr(p_28));
        addA(183,sym_35,fr(p_28));
        addA(183,sym_25,fr(p_28));
        addA(183,sym_14,fr(p_28));
        addA(183,sym_17,fr(p_28));
        addA(183,sym_21,fr(p_28));
        addA(183,sym_50,fr(p_28));
        addA(183,sym_47,fr(p_28));
        addA(183,sym_41,fr(p_28));
        addA(183,sym_39,fr(p_28));
        addL(183,eps(),sym_17,sym_47,sym_21,sym_25,sym_50,sym_31,sym_35,sym_39,sym_14,sym_41,sym_43);
    }
    /*
        LexDecl ::= IgnoreOpt terminal_kwd TypeName name_tok goesto_kwd RegexRoot PrecListsOpt CodeBlockOpt TerminalFlags semi_kwd (*)	[class_kwd, terminal_kwd, ignore_kwd, disambiguate_kwd, lex_block_close_kwd]

    */
    public static void init_184()
    {
        addA(184,sym_30,fr(p_63));
        addA(184,sym_27,fr(p_63));
        addA(184,sym_18,fr(p_63));
        addA(184,sym_19,fr(p_63));
        addA(184,sym_20,fr(p_63));
        addL(184,sym_48,sym_18,sym_19,sym_20,sym_27,sym_30);
    }
    static
    {
        sym_0 = t("$");
        sym_1 = t("prec_number");
        sym_2 = t("colon_kwd");
        sym_3 = t("attribute_decl_kwd");
        sym_4 = t("layout_decl_kwd");
        sym_5 = t("parser_decl_kwd");
        sym_6 = t("prec_decl_kwd");
        sym_7 = t("prefix_decl_kwd");
        sym_8 = t("aux_block_open_kwd");
        sym_9 = t("aux_block_close_kwd");
        sym_10 = t("barrier_kwd");
        sym_11 = t("goesto_kwd");
        sym_12 = t("comma_kwd");
        sym_13 = t("semi_kwd");
        sym_14 = t("slash_kwd");
        sym_15 = t("lt_kwd");
        sym_16 = t("gt_kwd");
        sym_17 = t("star");
        sym_18 = t("class_kwd");
        sym_19 = t("terminal_kwd");
        sym_20 = t("ignore_kwd");
        sym_21 = t("plus");
        sym_22 = t("non_kwd");
        sym_23 = t("colon");
        sym_24 = t("with_kwd");
        sym_25 = t("question");
        sym_26 = t("start_kwd");
        sym_27 = t("disambiguate_kwd");
        sym_28 = t("dash");
        sym_29 = t("precedence_kwd");
        sym_30 = t("lex_block_close_kwd");
        sym_31 = t("lparen");
        sym_32 = t("lex_block_open_kwd");
        sym_33 = t("not");
        sym_34 = t("init_block_close_kwd");
        sym_35 = t("bar");
        sym_36 = t("init_block_open_kwd");
        sym_37 = t("rbrack");
        sym_38 = t("code_block_close_kwd");
        sym_39 = t("wildcard");
        sym_40 = t("code_block_open_kwd");
        sym_41 = t("rparen");
        sym_42 = t("cf_block_close_kwd");
        sym_43 = t("lbrack");
        sym_44 = t("cf_block_open_kwd");
        sym_45 = t("rbrace");
        sym_46 = t("termname");
        sym_47 = t("escaped");
        sym_48 = t("ws");
        sym_49 = t("lbrace");
        sym_50 = t("character");
        sym_51 = t("name_tok");
        sym_52 = t("code_t");
        sym_53 = t("in_kwd");
        sym_54 = t("assoctypes_kwd");
        sym_55 = t("ws_no_line");
        sym_56 = nt("DeclBlock");
        sym_57 = nt("DeclBlocks");
        sym_58 = nt("Regex_R");
        sym_59 = nt("CFDecls");
        sym_60 = nt("Regex_DR");
        sym_61 = nt("LexDecls");
        sym_62 = nt("^");
        sym_63 = nt("ParserDecl");
        sym_64 = nt("Regex_CHAR");
        sym_65 = nt("Regex_G");
        sym_66 = nt("Regex_RR");
        sym_67 = nt("Regex_UR");
        sym_68 = nt("SymSeq");
        sym_69 = nt("TypeNameOpt");
        sym_70 = nt("TypeNameSeq");
        sym_71 = nt("TypeName");
        sym_72 = nt("QualifiedName");
        sym_73 = nt("Regex_RG");
        sym_74 = nt("RegexRoot");
        sym_75 = nt("Regex_UG");
        sym_76 = nt("CommaOrSymSeq");
        sym_77 = nt("CommaSymSeq");
        sym_78 = nt("GrammarFile");
        sym_79 = nt("CommaSymSeqOpt");
        sym_80 = nt("PrecLists");
        sym_81 = nt("PrecList");
        sym_82 = nt("TerminalFlags");
        sym_83 = nt("SuperRegexRoot");
        sym_84 = nt("LexDecl");
        sym_85 = nt("CFDecl");
        sym_86 = nt("IgnoreOpt");
        sym_87 = nt("PrecListsOpt");
        sym_88 = nt("CodeBlockOpt");
        sym_89 = nt("RHSFlags");
        sym_90 = nt("RHSSeq");
        sym_91 = nt("RHS");
        sym_92 = nt("LabeledSymSeq");
        p_0 = p("AttrDeclBlock",sym_56,sym_3,sym_71,sym_51,sym_13);
        p_1 = p("InitDeclBlock",sym_56,sym_36,sym_52,sym_34);
        p_2 = p("AuxDeclBlock",sym_56,sym_8,sym_52,sym_9);
        p_3 = p("CFDeclBlock",sym_56,sym_44,sym_59,sym_42);
        p_4 = p("LexDeclBlock",sym_56,sym_32,sym_61,sym_30);
        p_5 = p("DeclBlocksCons",sym_57,sym_56,sym_57);
        p_6 = p("DeclBlocksOne",sym_57,sym_56);
        p_7 = p("RtoDR",sym_58,sym_60);
        p_8 = p("RtoDR_bar_R",sym_58,sym_60,sym_35,sym_58);
        p_9 = p("CFDeclsOne",sym_59,sym_85);
        p_10 = p("CFDeclsCons",sym_59,sym_85,sym_59);
        p_11 = p("DRtoUR_question_RR",sym_60,sym_67,sym_25,sym_66);
        p_12 = p("DRtoUR_star_RR",sym_60,sym_67,sym_17,sym_66);
        p_13 = p("DRtoUR_plus_RR",sym_60,sym_67,sym_21,sym_66);
        p_14 = p("DRtoUR_RR",sym_60,sym_67,sym_66);
        p_15 = p("LexDeclsOne",sym_61,sym_84);
        p_16 = p("LexDeclsCons",sym_61,sym_84,sym_61);
        p_17 = p("Capsule",sym_62,sym_78,sym_0);
        p_18 = p("ParserDeclMain",sym_63,sym_5,sym_51);
        p_19 = p("CHARtochar",sym_64,sym_50);
        p_20 = p("CHARtoescaped",sym_64,sym_47);
        p_21 = p("GtoUG_RG",sym_65,sym_75,sym_73);
        p_22 = p("RRtoDR",sym_66,sym_60);
        p_23 = p("RRtoeps",sym_66);
        p_24 = p("URtolp_R_rp",sym_67,sym_31,sym_58,sym_41);
        p_25 = p("URtolb_not_G_rb",sym_67,sym_43,sym_33,sym_65,sym_37);
        p_26 = p("URtoCHAR",sym_67,sym_64);
        p_27 = p("URtowildcard",sym_67,sym_39);
        p_28 = p("URtomacro",sym_67,sym_43,sym_23,sym_46,sym_23,sym_37);
        p_29 = p("URtolb_G_rb",sym_67,sym_43,sym_65,sym_37);
        p_30 = p("SymSeqMain",sym_68,sym_51,sym_68);
        p_31 = p("SymSeqEps",sym_68);
        p_32 = p("TypeNameOptEps",sym_69);
        p_33 = p("TypeNameOptMain",sym_69,sym_71);
        p_34 = p("TypeNameSeqOne",sym_70,sym_71);
        p_35 = p("TypeNameSeqCons",sym_70,sym_71,sym_12,sym_70);
        p_36 = p("TypeNameGeneric",sym_71,sym_72,sym_15,sym_70,sym_16);
        p_37 = p("TypeNameBase",sym_71,sym_72);
        p_38 = p("QualifiedNameCons",sym_72,sym_51,sym_39,sym_72);
        p_39 = p("QualifiedNameOne",sym_72,sym_51);
        p_40 = p("RGtoG",sym_73,sym_65);
        p_41 = p("RGtoeps",sym_73);
        p_42 = p("Roottoeps",sym_74,sym_14,sym_14);
        p_43 = p("RoottoR",sym_74,sym_14,sym_58,sym_14);
        p_44 = p("UGtoCHAR_dash_CHAR",sym_75,sym_64,sym_28,sym_64);
        p_45 = p("UGtoCHAR",sym_75,sym_64);
        p_46 = p("CommaOrSymSeqNoComma",sym_76,sym_51,sym_68);
        p_47 = p("CommaOrSymSeqWithComma",sym_76,sym_51,sym_12,sym_77);
        p_48 = p("CommaSymSeqMain",sym_77,sym_51,sym_12,sym_77);
        p_49 = p("CommaSymSeqOne",sym_77,sym_51);
        p_50 = p("GrammarFileMain",sym_78,sym_52,sym_10,sym_63,sym_57);
        p_51 = p("CommaSymSeqOptMain",sym_79,sym_77);
        p_52 = p("CommaSymSeqOptEps",sym_79);
        p_53 = p("PrecDeclsOne",sym_80,sym_81);
        p_54 = p("PrecDeclsCons",sym_80,sym_81,sym_12,sym_80);
        p_55 = p("PrecDeclSubmitList",sym_81,sym_15,sym_31,sym_79,sym_41);
        p_56 = p("PrecDeclInList",sym_81,sym_53,sym_31,sym_79,sym_41);
        p_57 = p("PrecDeclDominateList",sym_81,sym_16,sym_31,sym_79,sym_41);
        p_58 = p("TerminalFlagsCons",sym_82,sym_7,sym_51,sym_82);
        p_59 = p("TerminalFlagsEps",sym_82);
        p_60 = p("SuperRRoot",sym_83,sym_11,sym_74);
        p_61 = p("TermDecl",sym_84,sym_86,sym_19,sym_51,sym_83,sym_87,sym_88,sym_82,sym_13);
        p_62 = p("TermClassDecl",sym_84,sym_18,sym_77,sym_13);
        p_63 = p("TypedTermDecl",sym_84,sym_86,sym_19,sym_71,sym_51,sym_11,sym_74,sym_87,sym_88,sym_82,sym_13);
        p_64 = p("GroupDeclSimple",sym_84,sym_27,sym_51,sym_2,sym_31,sym_77,sym_41,sym_11,sym_51,sym_13);
        p_65 = p("GroupDecl",sym_84,sym_27,sym_51,sym_2,sym_31,sym_77,sym_41,sym_40,sym_52,sym_38,sym_13);
        p_66 = p("PrecedenceDecl",sym_85,sym_29,sym_54,sym_76,sym_13);
        p_67 = p("ProdDecl",sym_85,sym_51,sym_11,sym_90,sym_13);
        p_68 = p("StartDecl",sym_85,sym_26,sym_24,sym_51,sym_13);
        p_69 = p("TypedNonTermDecl",sym_85,sym_22,sym_19,sym_71,sym_77,sym_13);
        p_70 = p("UntypedNonTermDecl",sym_85,sym_22,sym_19,sym_77,sym_13);
        p_71 = p("LanguageTerm",sym_86);
        p_72 = p("IgnoreTerm",sym_86,sym_20);
        p_73 = p("PrecDeclsEps",sym_87);
        p_74 = p("PrecDeclsMain",sym_87,sym_80);
        p_75 = p("CodeBlockOptEps",sym_88);
        p_76 = p("CodeBlockOptMain",sym_88,sym_40,sym_52,sym_38);
        p_77 = p("RHSFlagsEps",sym_89);
        p_78 = p("RHSFlagsConsLayout",sym_89,sym_4,sym_31,sym_79,sym_41,sym_89);
        p_79 = p("RHSFlagsConsOperator",sym_89,sym_6,sym_51,sym_89);
        p_80 = p("RHSSeqCons",sym_90,sym_91,sym_35,sym_90);
        p_81 = p("RHSSeqOne",sym_90,sym_91);
        p_82 = p("RHSMain",sym_91,sym_92,sym_88,sym_89);
        p_83 = p("LabeledSymSeqLabel",sym_92,sym_51,sym_23,sym_51,sym_92);
        p_84 = p("LabeledSymSeqNoLabel",sym_92,sym_51,sym_92);
        p_85 = p("LabeledSymSeqEps",sym_92);
        parseTable = new ThisParseTable();
        group_0 = tset(sym_50,sym_39);
        group_1 = tset(sym_50,sym_14);
        group_2 = tset(sym_50,sym_41);
        group_3 = tset(sym_50,sym_43);
        group_4 = tset(sym_29,sym_51);
        group_5 = tset(sym_22,sym_51);
        group_6 = tset(sym_21,sym_50);
        group_7 = tset(sym_17,sym_50);
        group_8 = tset(sym_26,sym_51);
        group_9 = tset(sym_50,sym_37);
        group_10 = tset(sym_50,sym_35);
        group_11 = tset(sym_50,sym_33);
        group_12 = tset(sym_50,sym_31);
        group_13 = tset(sym_50,sym_28);
        group_14 = tset(sym_25,sym_50);
        group_15 = tset(sym_23,sym_50);
    }
    public class Semantics extends edu.umn.cs.melt.copper.compiletime.engines.lalr.semantics.SemanticActionContainer
    {
        public String grammarNameGrabbed;
        public Integer prodNameCounter;
        public Integer dotCounter;
        public Integer nextOpPrecedence;
        public java.util.LinkedList< String > grammarLayout;
        public Integer nextProdPrecedence;

        public Semantics()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            runInit();
        }

        public void error(edu.umn.cs.melt.copper.runtime.io.InputPosition pos,java.lang.String message)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
              if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.ERROR)) logger.logErrorMessage(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.ERROR,pos,message);
        }

        public void runDefaultTermAction()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            
        }
        public void runDefaultProdAction()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            
        }
        public void runInit()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            grammarNameGrabbed = new String();
            prodNameCounter = 0;
            dotCounter = 0;
            nextOpPrecedence = 0;
            grammarLayout = new java.util.LinkedList< String >();
            nextProdPrecedence = 0;
            
             prodNameCounter = 0; 
             dotCounter = 0; 
             nextOpPrecedence = 0; 
            
             nextProdPrecedence = Integer.MAX_VALUE; 
        }
        @SuppressWarnings("unchecked")
        public Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,Object[] _children,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production _prod)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            this._pos = _pos;
            this._children = _children;
            this._specialAttributes = new edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes(virtualLocation);
            Object RESULT = null;

            if(_prod.equals(p_0))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.PARSER_ATTRIBUTE,
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[2]).second()),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "location",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "type",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[1])),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "code",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) "")));

            }
            else if(_prod.equals(p_1))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
              edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,
              edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(" initCode "),
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
              "location",
              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
              "code",
              (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[1]));

            }
            else if(_prod.equals(p_2))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
              edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,
              edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(" auxCode "),
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
              "location",
              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
              "code",
              (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[1]));

            }
            else if(_prod.equals(p_3))
            {
                
    RESULT = _children[1];

            }
            else if(_prod.equals(p_4))
            {
                
    RESULT = _children[1];

            }
            else if(_prod.equals(p_5))
            {
                
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);

            }
            else if(_prod.equals(p_6))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_7))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_8))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice(
            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0],
            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);

            }
            else if(_prod.equals(p_9))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_10))
            {
                
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);

            }
            else if(_prod.equals(p_11))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice(
             new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString(),
             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),
            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);

            }
            else if(_prod.equals(p_12))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar(
             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),
            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);

            }
            else if(_prod.equals(p_13))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),
            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar(
             ((edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]).clone()),
            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);

            }
            else if(_prod.equals(p_14))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0],
            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[1]);

            }
            else if(_prod.equals(p_15))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_16))
            {
                
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);

            }
            else if(_prod.equals(p_18))
            {
                
    grammarNameGrabbed = (String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[1]).second();
    RESULT = grammarNameGrabbed;

            }
            else if(_prod.equals(p_19))
            {
                
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.instantiate(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,
		     (((String) _children[0]).toCharArray()));

            }
            else if(_prod.equals(p_20))
            {
                
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.instantiate(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,
		     (((String) _children[0]).toCharArray()));

            }
            else if(_prod.equals(p_21))
            {
                
	edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex UGNode =
		(edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0];
	edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex RGNode =
		(edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[1];
	
	if(UGNode instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet &&
	   (RGNode == null || RGNode instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString))
	{
		RESULT = UGNode;
	}
	else if(UGNode instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet &&
	        RGNode instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet)
	{
		RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.union(
				 (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) UGNode,
				 (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) RGNode);
	}
	else
	{
		error(_pos,"Type error in regex");
		RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();
	}

            }
            else if(_prod.equals(p_22))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_23))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();

            }
            else if(_prod.equals(p_24))
            {
                
    RESULT = _children[1];

            }
            else if(_prod.equals(p_25))
            {
                
    if(_children[2] instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet)
	{
		edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet CGNode =
		   (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) _children[2];
		RESULT = CGNode.invertSet();
	}
	else
	{
		error(_pos,"Type error in regex");
		RESULT = null;
	}

            }
            else if(_prod.equals(p_26))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_27))
            {
                
	edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet Newline =
	       edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.instantiate(
	        edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,'\n');
    RESULT = Newline.invertSet();

            }
            else if(_prod.equals(p_28))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.MacroHole(
            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal((String) _children[2]));

            }
            else if(_prod.equals(p_29))
            {
                
    RESULT = _children[1];

            }
            else if(_prod.equals(p_30))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object> sym = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0];
    ((java.util.LinkedList<String>) _children[1]).addFirst((String) sym.second());
    RESULT = _children[1];

            }
            else if(_prod.equals(p_31))
            {
                
    RESULT = new java.util.LinkedList<String>();

            }
            else if(_prod.equals(p_32))
            {
                
    RESULT = "";

            }
            else if(_prod.equals(p_33))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_34))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_35))
            {
                
    RESULT = _children[0] + "," + _children[2];

            }
            else if(_prod.equals(p_36))
            {
                
    RESULT = _children[0] + "<" + _children[2] + ">";

            }
            else if(_prod.equals(p_37))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_38))
            {
                
    RESULT = ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0]).second() + "." + _children[2];

            }
            else if(_prod.equals(p_39))
            {
                
    RESULT = ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0]).second();

            }
            else if(_prod.equals(p_40))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_41))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();

            }
            else if(_prod.equals(p_42))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();

            }
            else if(_prod.equals(p_43))
            {
                
    RESULT = _children[1];

            }
            else if(_prod.equals(p_44))
            {
                
	edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex characterNode1 =
		(edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0];
	edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex characterNode2 =
		(edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2];
	
	if(characterNode1 instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet &&
	   characterNode2 instanceof edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet)
	{
		char lowerLimit = ((edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) characterNode1).getFirstChar();
		char upperLimit = ((edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet) characterNode2).getFirstChar();
		RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.instantiate(
		        edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.RANGES,
		        '+',lowerLimit,upperLimit);
	}
	else
	{
		error(_pos,"Type error in regex");
		RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.instantiate(
		        edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS);
	}

            }
            else if(_prod.equals(p_45))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_46))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object> sym = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0];
    ((java.util.LinkedList<String>) _children[1]).addFirst((String) sym.second());
    RESULT = _children[1];

            }
            else if(_prod.equals(p_47))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object> sym = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0];
    ((java.util.LinkedList<String>) _children[2]).addFirst((String) sym.second());
    RESULT = _children[2];

            }
            else if(_prod.equals(p_48))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object> sym = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0];
    ((java.util.LinkedList<String>) _children[2]).addFirst((String) sym.second());
    RESULT = _children[2];

            }
            else if(_prod.equals(p_49))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object> sym = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0];
    java.util.LinkedList<String> seq = new java.util.LinkedList<String>();
    seq.addFirst((String) sym.second()); 
    RESULT = seq;

            }
            else if(_prod.equals(p_50))
            {
                
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
              new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
               edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.GRAMMAR_NAME,
               edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[2]),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "location",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "layout",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) grammarLayout)),               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "spectype",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) "JavaCUP")),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "parserName",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[2]))),
              new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
               edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,
               edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(" startCode "),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "location",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "code",
                (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[0])),
              _children[3]);

            }
            else if(_prod.equals(p_51))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_52))
            {
                
    RESULT = new java.util.LinkedList<String>();

            }
            else if(_prod.equals(p_53))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_54))
            {
                
    java.util.ArrayList< java.util.LinkedList<String> > rv = (java.util.ArrayList< java.util.LinkedList<String> >) _children[2];
    java.util.ArrayList< java.util.LinkedList<String> > additions = (java.util.ArrayList< java.util.LinkedList<String> >) _children[0]; 
    rv.get(0).addAll(additions.get(0));
    rv.get(1).addAll(additions.get(1));
    rv.get(2).addAll(additions.get(2));
    RESULT = rv;

            }
            else if(_prod.equals(p_55))
            {
                
    java.util.ArrayList< java.util.LinkedList<String> > rv = new java.util.ArrayList< java.util.LinkedList<String> >();
    rv.add(new java.util.LinkedList<String>());
    rv.add((java.util.LinkedList<String>) _children[2]);
    rv.add(new java.util.LinkedList<String>());
    RESULT = rv;

            }
            else if(_prod.equals(p_56))
            {
                
    java.util.ArrayList< java.util.LinkedList<String> > rv = new java.util.ArrayList< java.util.LinkedList<String> >();
    rv.add((java.util.LinkedList<String>) _children[2]);
    rv.add(new java.util.LinkedList<String>());
    rv.add(new java.util.LinkedList<String>());
    RESULT = rv;

            }
            else if(_prod.equals(p_57))
            {
                
    java.util.ArrayList< java.util.LinkedList<String> > rv = new java.util.ArrayList< java.util.LinkedList<String> >();
    rv.add(new java.util.LinkedList<String>());
    rv.add(new java.util.LinkedList<String>());
    rv.add((java.util.LinkedList<String>) _children[2]);
    RESULT = rv;

            }
            else if(_prod.equals(p_58))
            {
                
    // FIXME Put in a check if the same flag is used twice.
    RESULT = _children[1];

            }
            else if(_prod.equals(p_59))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null);

            }
            else if(_prod.equals(p_60))
            {
                
    RESULT = _children[1];

            }
            else if(_prod.equals(p_61))
            {
                
    boolean isIgnore;
    isIgnore = (Boolean) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[0]).second();
    if(isIgnore) grammarLayout.add((String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[2]).second());
    boolean noPrefix = true;
    if(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[6]).second() != null) noPrefix = false;
    String type = "Object";
    java.util.ArrayList< java.util.LinkedList<String> > precLists = (java.util.ArrayList< java.util.LinkedList<String> >) _children[4];
    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode node = null;
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
           node,
           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[2]).second()),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "location",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "type",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) type)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "regex",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "classes",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) precLists.get(0))),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "submits",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) precLists.get(1))),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "dominates",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) precLists.get(2))),
            noPrefix ? null : edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "prefix",
             (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[6]),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "code",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[5]))));

            }
            else if(_prod.equals(p_62))
            {
                
    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode rv = null;
    java.util.LinkedList<String> classes = (java.util.LinkedList<String>) _children[1];
    for(String termClass : classes)
    {
        rv = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
                  new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
                   edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,
                   edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(termClass),
                   edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                    "location",
                    edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null))),
                  rv);
    }
    RESULT = rv;

            }
            else if(_prod.equals(p_63))
            {
                
    boolean isIgnore;
    isIgnore = (Boolean) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[0]).second();
    if(isIgnore) grammarLayout.add((String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[3]).second());
    boolean noPrefix = true;
    if(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[8]).second() != null) noPrefix = false;
    String type = (String) _children[2];
    java.util.ArrayList< java.util.LinkedList<String> > precLists = (java.util.ArrayList< java.util.LinkedList<String> >) _children[6];
    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode node = null;
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
           node,
           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[3]).second()),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "location",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "type",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) type)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "regex",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[5])),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "classes",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) precLists.get(0))),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "submits",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) precLists.get(1))),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "dominates",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) precLists.get(2))),
            noPrefix ? null : edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "prefix",
             (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[8]),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "code",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[7]))));

            }
            else if(_prod.equals(p_64))
            {
                
    // FIXME Put in a check if the regex disambiguated to is outside the group.
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object> code = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[7];
    code = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(code.first(),(Object) ("return " + ((String) code.second()) + ";\n"));
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DISAMBIGUATION_GROUP,
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[1]).second()),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "location",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "code",
             code),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "members",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[4])));

            }
            else if(_prod.equals(p_65))
            {
                
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DISAMBIGUATION_GROUP,
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[1]).second()),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "location",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "code",
             (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[7]),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "members",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[4])));

            }
            else if(_prod.equals(p_66))
            {
                
    edu.umn.cs.melt.copper.runtime.io.InputPosition pos = (edu.umn.cs.melt.copper.runtime.io.InputPosition) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[0]).first();
    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode rv = null;
    java.util.LinkedList<String> terminals = (java.util.LinkedList<String>) _children[2];
    String opClassName = " OpMain ";
    for(String terminal : terminals)
    {
        rv = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
              new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
               edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,
               edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(opClassName),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "location",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null))),
              new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
               edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,
               edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(terminal),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "location",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null)),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "operatorClass",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) opClassName)),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "operatorPrecedence",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) nextOpPrecedence)),
               edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                "operatorAssociativity",
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[1]))),
              rv);
    }
    nextOpPrecedence++;
    RESULT = rv;

            }
            else if(_prod.equals(p_67))
            {
                
    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode rhss = (edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode) _children[2];
    rhss.acceptVisitor(new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.LHSAssigner(),(edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,Object>) _children[0]);
    RESULT = rhss;

            }
            else if(_prod.equals(p_68))
            {
                
    edu.umn.cs.melt.copper.runtime.io.InputPosition pos = (edu.umn.cs.melt.copper.runtime.io.InputPosition) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,java.lang.Object>) _children[0]).first();
    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.NON_TERMINAL,
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,String>) _children[2]).second()),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "isStart",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) new Boolean(true))));

            }
            else if(_prod.equals(p_69))
            {
                
    edu.umn.cs.melt.copper.runtime.io.InputPosition pos = ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,String>) _children[0]).first();
    java.util.LinkedList<String> nonTerms = (java.util.LinkedList<String>) _children[3];
    String nontermType = (String) _children[2];//((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,java.lang.Object>) _children[2]).second();
    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode rv = null;
    for(String nonTerm : nonTerms)
    {
         rv = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
               new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
                edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.NON_TERMINAL,
                edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(nonTerm),
                /*edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,String>) _children[0]).second()),*/
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                 "location",
                 edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null)),
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                 "type",
                 edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) nontermType))),
               rv);        
    }
    RESULT = rv;

            }
            else if(_prod.equals(p_70))
            {
                
    edu.umn.cs.melt.copper.runtime.io.InputPosition pos = ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,String>) _children[0]).first();
    java.util.LinkedList<String> nonTerms = (java.util.LinkedList<String>) _children[2];
    String nontermType = "Object";
    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode rv = null;
    for(String nonTerm : nonTerms)
    {
         rv = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
               new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
                edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.NON_TERMINAL,
                edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(nonTerm),
                /*edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,String>) _children[0]).second()),*/
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                 "location",
                 edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null)),
                edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
                 "type",
                 edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) nontermType))),
               rv);        
    }
    RESULT = rv;

            }
            else if(_prod.equals(p_71))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,false);

            }
            else if(_prod.equals(p_72))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,true);

            }
            else if(_prod.equals(p_73))
            {
                
    java.util.ArrayList< java.util.LinkedList<String> > rv = new java.util.ArrayList< java.util.LinkedList<String> >();
    rv.add(new java.util.LinkedList<String>());
    rv.add(new java.util.LinkedList<String>());
    rv.add(new java.util.LinkedList<String>());
    RESULT = rv;

            }
            else if(_prod.equals(p_74))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_75))
            {
                
    RESULT = "";

            }
            else if(_prod.equals(p_76))
            {
                
    RESULT = ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[1]).second();

            }
            else if(_prod.equals(p_77))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons((java.util.LinkedList<String>) null,(String) null);

            }
            else if(_prod.equals(p_78))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,String > flags = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,String >) _children[4];
    // FIXME Put in a check if the same flag is used twice.
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons((java.util.LinkedList<String>) _children[2],flags.second());

            }
            else if(_prod.equals(p_79))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,String > flags = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,String >) _children[2];
    // FIXME Put in a check if the same flag is used twice.
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(flags.first(),((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,Object>) _children[1]).second());

            }
            else if(_prod.equals(p_80))
            {
                
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[2]);

            }
            else if(_prod.equals(p_81))
            {
                
    RESULT = _children[0];

            }
            else if(_prod.equals(p_82))
            {
                
    boolean noOperator = false;
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,String > flags = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,String >) _children[2];
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,java.util.LinkedList<String> > fullRHS = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,java.util.LinkedList<String> >) _children[0];
    //if(((java.util.LinkedList<String>) _children[8]).size() > 1) error(_pos,"Productions cannot have more than one custom operator");
    /*else*/ if(flags.second() == null) noOperator = true;
    String prodClassName = " ProdMain ";
    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(prodClassName),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "location",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null))),
           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.PRODUCTION,
            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol("p_" + grammarNameGrabbed + "_" + (prodNameCounter++)),
            /* edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,java.lang.Object>) _children[1]).second()), */
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "location",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "class",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) prodClassName)),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "precedence",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) nextProdPrecedence--)),
            noOperator ? null : edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "operator",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) flags.second())),
            (flags.first() == null ? null :
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
              "layout",
              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) flags.first()))),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "code",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[1])),
            /*edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "LHS",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition,String>) _children[17]).second())),*/
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "RHSVars",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) fullRHS.first())),
            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
             "RHS",
             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) fullRHS.second()))));

            }
            else if(_prod.equals(p_83))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object> sym = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0];
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object> name = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[2];
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,java.util.LinkedList<String> > lists = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,java.util.LinkedList<String> >) _children[3];
    lists.first().addFirst((String) name.second());
    lists.second().addFirst((String) sym.second());
    RESULT = _children[3];

            }
            else if(_prod.equals(p_84))
            {
                
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object> sym = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair<Object,Object>) _children[0];
    edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,java.util.LinkedList<String> > lists = (edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,java.util.LinkedList<String> >) _children[1];
    lists.first().addFirst(null);
    lists.second().addFirst((String) sym.second());
    RESULT = _children[1];

            }
            else if(_prod.equals(p_85))
            {
                
    RESULT = new edu.umn.cs.melt.copper.runtime.auxiliary.Pair< java.util.LinkedList<String>,java.util.LinkedList<String> >(new java.util.LinkedList<String>(),new java.util.LinkedList<String>());

            }
            else
             {                runDefaultProdAction();
                return "PARSETREE";
            }
            return RESULT;
        }
        public Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData _terminal)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            this._pos = _pos;
            this._terminal = _terminal;
            this._specialAttributes = new edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes(virtualLocation);
            Object RESULT = null;
            String lexeme = _terminal.getToken().getLexeme();

            if(_terminal.getToken().equals(sym_1))
            {
                 RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,Integer.parseInt(lexeme));             }
            else if(_terminal.getToken().equals(sym_2))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_3))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_4))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_5))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_6))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_7))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_8))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_9))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_10))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_11))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_12))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_13))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_14))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_15))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_16))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_17))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_18))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_19))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_20))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_21))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_22))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_23))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_24))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_25))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_26))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_27))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_28))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_29))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_30))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_31))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_32))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_33))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_34))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_35))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_36))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_37))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_38))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_39))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_40))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_41))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_42))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_43))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_44))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_45))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_46))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_47))
            {
                
    char escapedChar = edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter.getRepresentedCharacter(lexeme);
    if(escapedChar == edu.umn.cs.melt.copper.runtime.io.ScannerBuffer.EOFIndicator) error(_pos,"Illegal escaped character");
    RESULT = String.valueOf(escapedChar);
            }
            else if(_terminal.getToken().equals(sym_48))
            {
                 RESULT = null;             }
            else if(_terminal.getToken().equals(sym_49))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_50))
            {
                 RESULT = lexeme;             }
            else if(_terminal.getToken().equals(sym_51))
            {
                
     if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
     /*if(reporter.willHaveEffect(edu.umn.cs.melt.copper.runtime.auxiliary.ErrorDegree.DEGREE_NOTA_BENE) &&
      (dotCounter = (dotCounter + 1) % edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW) == 0) reporter.reportRaw(edu.umn.cs.melt.copper.runtime.auxiliary.ErrorDegree.DEGREE_NOTA_BENE,"",".");*/
     RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_52))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_53))
            {
                
    RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);
            }
            else if(_terminal.getToken().equals(sym_54))
            {
                
   if(lexeme.equals("nonassoc")) RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_NONASSOC);
   else if(lexeme.equals("left")) RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_LEFT);
   else /* if(lexeme.equals("right")) */ RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_RIGHT);
            }
            else
             {                runDefaultTermAction();
                return "PARSETREE";
            }
            return RESULT;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData runDisambiguationAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,java.util.HashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> matches)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            this._pos = _pos;
            String lexeme = null;
            edu.umn.cs.melt.copper.runtime.io.InputPosition positionFollowing = null;
            java.util.HashSet<edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal> matchesT = new java.util.HashSet<edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal>();
            edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> matchesD = new edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData>();
            for(edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData qm : matches)
            {
                if(lexeme == null) lexeme = qm.getToken().getLexeme();
                if(positionFollowing == null) positionFollowing = qm.getPositionFollowing();
                if(!lexeme.equals(qm.getToken().getLexeme())) error(_pos,"Attempt to run disambiguation on matches with unequal lexemes '" + lexeme + "' and '" + qm.getToken().getLexeme() + "'");
                if(!positionFollowing.equals(qm.getPositionFollowing())) error(_pos,"Attempt to run disambiguation on matches with unequal preceding whitespace");
                matchesT.add(qm.getToken().bareSym());
                matchesD.put(qm);
            }
            if(matchesT.equals(group_0)) return disambiguate_0(lexeme,matchesD);
            else if(matchesT.equals(group_1)) return disambiguate_1(lexeme,matchesD);
            else if(matchesT.equals(group_2)) return disambiguate_2(lexeme,matchesD);
            else if(matchesT.equals(group_3)) return disambiguate_3(lexeme,matchesD);
            else if(matchesT.equals(group_4)) return disambiguate_4(lexeme,matchesD);
            else if(matchesT.equals(group_5)) return disambiguate_5(lexeme,matchesD);
            else if(matchesT.equals(group_6)) return disambiguate_6(lexeme,matchesD);
            else if(matchesT.equals(group_7)) return disambiguate_7(lexeme,matchesD);
            else if(matchesT.equals(group_8)) return disambiguate_8(lexeme,matchesD);
            else if(matchesT.equals(group_9)) return disambiguate_9(lexeme,matchesD);
            else if(matchesT.equals(group_10)) return disambiguate_10(lexeme,matchesD);
            else if(matchesT.equals(group_11)) return disambiguate_11(lexeme,matchesD);
            else if(matchesT.equals(group_12)) return disambiguate_12(lexeme,matchesD);
            else if(matchesT.equals(group_13)) return disambiguate_13(lexeme,matchesD);
            else if(matchesT.equals(group_14)) return disambiguate_14(lexeme,matchesD);
            else if(matchesT.equals(group_15)) return disambiguate_15(lexeme,matchesD);
            else return null;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_0(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData wildcard = _layouts.get(qsm(sym_39.getId(),null,null,null,null));
             return wildcard; 
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_1(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData slash_kwd = _layouts.get(qsm(sym_14.getId(),null,null,null,null));
             return slash_kwd; 
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_2(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData rparen = _layouts.get(qsm(sym_41.getId(),null,null,null,null));
             return rparen;   
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_3(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData lbrack = _layouts.get(qsm(sym_43.getId(),null,null,null,null));
             return lbrack;   
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_4(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData precedence_kwd = _layouts.get(qsm(sym_29.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData name_tok = _layouts.get(qsm(sym_51.getId(),null,null,null,null));
             return precedence_kwd; 
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_5(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData non_kwd = _layouts.get(qsm(sym_22.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData name_tok = _layouts.get(qsm(sym_51.getId(),null,null,null,null));
             return non_kwd; 
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_6(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData plus = _layouts.get(qsm(sym_21.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
             return plus;     
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_7(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData star = _layouts.get(qsm(sym_17.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
             return star;     
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_8(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData start_kwd = _layouts.get(qsm(sym_26.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData name_tok = _layouts.get(qsm(sym_51.getId(),null,null,null,null));
             return start_kwd; 
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_9(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData rbrack = _layouts.get(qsm(sym_37.getId(),null,null,null,null));
             return rbrack;   
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_10(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData bar = _layouts.get(qsm(sym_35.getId(),null,null,null,null));
             return bar;      
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_11(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData not = _layouts.get(qsm(sym_33.getId(),null,null,null,null));
             return not;      
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_12(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData lparen = _layouts.get(qsm(sym_31.getId(),null,null,null,null));
             return lparen;   
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_13(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData dash = _layouts.get(qsm(sym_28.getId(),null,null,null,null));
             return dash;     
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_14(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData question = _layouts.get(qsm(sym_25.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
             return question; 
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_15(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData colon = _layouts.get(qsm(sym_23.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_50.getId(),null,null,null,null));
             return colon;    
        }
    }

    public static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource parseGrammar(java.util.ArrayList< edu.umn.cs.melt.copper.runtime.auxiliary.Pair<String,java.io.Reader> > files,edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger logger)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
    {
        edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode node = null;
        for(edu.umn.cs.melt.copper.runtime.auxiliary.Pair<String,java.io.Reader> file : files)
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.LALREngine engine = new edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.cup.CupSkinParser(file.second(),logger);
            engine.startEngine(edu.umn.cs.melt.copper.runtime.io.InputPosition.initialPos(file.first()));
            Object parseTree = null;
            try { parseTree = engine.runEngine(); }
            catch(edu.umn.cs.melt.copper.runtime.logging.CopperException ex)
            {
                throw ex;
            }
            if(parseTree != null)
            {
            	node = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(parseTree,node);
            }
        }
        if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(1,"\nBuilding grammar AST");
        edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.AttributeConsolidator consolidator = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.AttributeConsolidator(logger);
        node.acceptVisitor(consolidator,null);
        return edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.buildAST(logger,consolidator.consolidatedNodes);
    }

}


 class CupSkinParserScanner extends edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScanner
{
    public CupSkinParserScanner(java.io.Reader reader,edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger logger)
    {
        super(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK));
        this.buffer = ScannerBuffer.instantiate(reader);
        this.logger = logger;
        startState = 113;
    }

    /** Create a symbol. */
    protected static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol s(String sym)
    {
        return edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(sym);
    }
    /** Create a terminal from a symbol. */
    protected static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t(String sym)
    {
        return new edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal(sym);
    }
    /** Setup accepting symbols for a state. */
    protected static void sas(int index,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal... aSyms)
    {
        staticStateInfo[index].addAcceptingSyms(aSyms);
    }
    /** Setup possible symbols for a state. */
    protected static void sps(int index,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal... pSyms)
    {
        staticStateInfo[index].addPossibleSyms(pSyms);
    }
    /** Setup rejecting symbols for a state. */
    protected static void srs(int index,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal... rSyms)
    {
        staticStateInfo[index].addRejectingSyms(rSyms);
    }
    /** Return maximal-munch match objects. */
    protected static edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchLongest newlong(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t,edu.umn.cs.melt.copper.runtime.io.InputPosition positionPreceding,edu.umn.cs.melt.copper.runtime.io.InputPosition positionFollowing,java.util.ArrayList<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> layouts)
    {
        return new edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchLongest(t,positionPreceding,positionFollowing,layouts);
    }
    /** Functions for determining character ranges. */
    protected static boolean cheq(char input,char single)
    {
        return (input == single);
    }
    protected static boolean chin(char input,char min,char max)
    {
        return (input >= min && input <= max);
    }
    private static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t_1,t_2,t_3,t_4,t_5,t_6,t_7,t_8,t_9,t_10,t_11,t_12,t_13,t_14,t_15,t_16,t_17,t_18,t_19,t_20,t_21,t_22,t_23,t_24,t_25,t_26,t_27,t_28,t_29,t_30,t_31,t_32,t_33,t_34,t_35,t_36,t_37,t_38,t_39,t_40,t_41,t_42,t_43,t_44,t_45,t_46,t_47,t_48,t_49,t_50,t_51,t_52,t_53,t_54,t_55;
;
    private static void symAdd_0()
    {
        sas(1,t_36,t_23);
        sps(1,t_36,t_23);
        sas(2,t_36,t_34,t_55,t_2);
        sps(2,t_36,t_34,t_55,t_2);
        sas(3,t_2);
        sps(3,t_40,t_2,t_1);
        sas(4,t_2);
        sps(4,t_2);
        sas(5,t_36,t_3,t_2);
        sps(5,t_36,t_13,t_3,t_2,t_4);
        sas(6,t_36,t_34,t_2,t_35);
        sps(6,t_36,t_34,t_2,t_35);
        sas(7,t_2);
        sps(7,t_40,t_2,t_1);
        sas(8,t_36,t_2);
        sps(8,t_36,t_40,t_2,t_1);
        sas(9,t_36,t_13,t_3,t_2);
        sps(9,t_36,t_13,t_3,t_2,t_4);
        sas(10,t_38);
        sps(10,t_38,t_2);
        sas(11,t_36,t_22);
        sps(11,t_36,t_22);
        sas(12,t_36,t_24);
        sps(12,t_36,t_24);
        sas(13,t_36,t_34,t_3,t_2);
        sps(13,t_36,t_11,t_34,t_3,t_2);
        sas(14,t_36,t_2);
        sps(14,t_36,t_40,t_2,t_1);
        sas(15,t_36);
        sps(15,t_36,t_24);
        sas(16,t_2);
        sps(16,t_2,t_37);
        sas(17,t_36,t_34,t_2,t_30);
        sps(17,t_36,t_34,t_2,t_30);
        sas(18,t_36,t_34,t_2,t_31);
        sps(18,t_36,t_34,t_2,t_31);
        sas(19,t_36);
        sps(19,t_36,t_20,t_21);
        sas(20,t_36,t_3,t_2);
        sps(20,t_36,t_3,t_2,t_4);
        sas(21,t_36,t_3,t_2);
        sps(21,t_36,t_3,t_2,t_4);
        sas(22,t_36);
        sps(22,t_36,t_24);
        sas(23,t_36,t_34,t_3,t_2);
        sps(23,t_36,t_34,t_3,t_2);
        sas(24,t_36);
        sps(24,t_36,t_20,t_21);
        sas(25,t_36,t_34,t_2,t_33);
        sps(25,t_36,t_34,t_2,t_33);
        sas(26,t_36,t_3,t_2);
        sps(26,t_36,t_3,t_2,t_4);
        sps(27,t_40,t_2,t_1);
        sas(28,t_36,t_20);
        sps(28,t_36,t_20);
        sas(29,t_36,t_3,t_2);
        sps(29,t_36,t_3,t_2,t_4);
        sas(30,t_36,t_3,t_2);
        sps(30,t_36,t_3,t_2,t_4);
        sas(31,t_36);
        sps(31,t_36,t_23,t_22);
        sas(32,t_36);
        sps(32,t_36,t_23,t_22);
        sas(33,t_36,t_3,t_2);
        sps(33,t_36,t_3,t_2,t_4);
        sas(34,t_36,t_3,t_2);
        sps(34,t_36,t_3,t_2,t_4);
        sas(35,t_36,t_3,t_2);
        sps(35,t_36,t_3,t_2,t_4);
        sas(36,t_36,t_40);
        sps(36,t_36,t_40);
        sas(37,t_2);
        sps(37,t_40,t_2,t_1);
        sps(38,t_40,t_2,t_1);
        sas(39,t_36,t_40,t_2);
        sps(39,t_36,t_40,t_2);
        sas(40,t_36,t_21);
        sps(40,t_36,t_21);
        sas(41,t_36,t_3,t_2);
        sps(41,t_36,t_3,t_2,t_4);
        sas(42,t_36,t_34,t_2,t_42);
        sps(42,t_36,t_34,t_2,t_42);
        sas(43,t_29,t_51,t_34);
        sps(43,t_29,t_51,t_34,t_2,t_37,t_14);
        sas(44,t_2);
        sps(44,t_40,t_2,t_1);
        sas(45,t_36,t_34,t_2,t_45);
        sps(45,t_36,t_34,t_2,t_45);
        sas(46,t_36,t_3,t_2,t_4);
        sps(46,t_36,t_3,t_2,t_4);
        sas(47,t_36,t_17);
        sps(47,t_36,t_17);
        sas(48,t_36,t_38,t_2);
        sps(48,t_36,t_38,t_2);
        sas(49,t_36,t_34,t_2,t_43);
        sps(49,t_36,t_34,t_2,t_43);
        sas(50,t_36,t_34,t_2,t_44);
        sps(50,t_36,t_34,t_2,t_44);
        sas(51,t_36,t_19);
        sps(51,t_36,t_19);
        sas(52,t_36,t_40,t_2,t_1);
        sps(52,t_36,t_40,t_2,t_1);
        sas(53,t_36,t_2);
        sps(53,t_36,t_2);
        sas(54,t_36,t_3,t_2,t_4);
        sps(54,t_36,t_3,t_2,t_4);
        sas(55,t_40,t_1);
        sps(55,t_40,t_1);
        sas(56,t_2);
        sps(56,t_40,t_2,t_1);
        sas(57,t_36);
        sps(57,t_36);
        sas(58,t_36,t_2);
        sps(58,t_36,t_40,t_2,t_1);
        sas(59,t_36,t_34);
        sps(59,t_18,t_19,t_20,t_21,t_16,t_17,t_36,t_28,t_27,t_26,t_25,t_24,t_23,t_22,t_34,t_39);
        sas(60,t_36,t_2);
        sps(60,t_36,t_40,t_2,t_1);
        sas(61,t_36,t_18);
        sps(61,t_36,t_18);
        sas(62,t_36,t_41,t_34,t_2);
        sps(62,t_36,t_41,t_34,t_2,t_15);
        sas(63,t_36,t_3,t_2,t_4);
        sps(63,t_36,t_3,t_2,t_4);
        sas(64,t_2);
        sps(64,t_2);
        sas(65,t_40,t_2);
        sps(65,t_40,t_2);
        sas(66,t_36);
        sps(66,t_36,t_40,t_1);
        sas(67,t_2);
        sps(67,t_40,t_2,t_1);
        sas(68,t_36);
        sps(68,t_36,t_40,t_1);
        sas(69,t_36);
        sps(69,t_36,t_27,t_18,t_19);
        sas(70,t_2);
        sps(70,t_40,t_2,t_1);
        sas(71,t_36,t_16);
        sps(71,t_36,t_16);
        sas(72,t_40,t_2,t_1);
        sps(72,t_40,t_2,t_1);
        sas(73,t_36,t_34,t_2);
        sps(73,t_36,t_38,t_34,t_2);
        sas(74,t_36,t_34,t_2);
        sps(74,t_36,t_34,t_2);
        sas(75,t_36);
        sps(75,t_36,t_25,t_24);
        sas(76,t_2);
        sps(76,t_2);
        sas(77,t_36);
        sps(77,t_36,t_25,t_24);
        sas(78,t_36,t_34,t_3,t_2);
        sps(78,t_36,t_34,t_3,t_2,t_4);
        sas(79,t_36,t_3,t_2);
        sps(79,t_36,t_12,t_3,t_2);
        sas(80,t_2);
        sps(80,t_2);
        sas(81,t_36,t_3,t_2);
        sps(81,t_36,t_12,t_3,t_2);
        sas(82,t_36,t_40,t_2);
        sps(82,t_36,t_40,t_2);
        sas(83,t_36,t_12,t_3,t_2);
        sps(83,t_36,t_12,t_3,t_2);
        sas(84,t_2);
        sps(84,t_2);
        sas(85,t_36,t_40,t_2,t_1);
        sps(85,t_36,t_40,t_2,t_1);
        sas(86,t_36,t_55,t_2);
        sps(86,t_36,t_55,t_2);
        sas(87,t_36,t_34,t_3,t_2);
        sps(87,t_36,t_34,t_3,t_2,t_4);
        sas(88,t_36,t_34,t_55,t_2);
        sps(88,t_36,t_34,t_55,t_2);
        sas(89,t_36,t_3,t_2);
        sps(89,t_36,t_12,t_3,t_2);
        sas(90,t_36);
        sps(90,t_36,t_40,t_1);
        sas(91,t_36,t_3,t_2);
        sps(91,t_36,t_12,t_3,t_2);
        sas(92,t_36,t_3,t_2);
        sps(92,t_36,t_11,t_3,t_2);
        sas(93,t_36,t_3,t_2);
        sps(93,t_36,t_11,t_3,t_2);
        sas(94,t_36,t_54,t_34,t_2);
        sps(94,t_36,t_54,t_34,t_2);
        sas(95,t_36,t_3,t_2);
        sps(95,t_36,t_3,t_2);
        sps(96,t_40,t_1);
        sas(97,t_36,t_2);
        sps(97,t_36,t_40,t_2,t_1);
        sas(98,t_36,t_3,t_2);
        sps(98,t_36,t_11,t_3,t_2);
        sas(99,t_36,t_11,t_3,t_2);
        sps(99,t_36,t_11,t_3,t_2);
        sas(100,t_2,t_37);
        sps(100,t_2,t_37);
        sas(101,t_40);
        sps(101,t_40,t_2);
        sas(102,t_36,t_34,t_52,t_2);
        sps(102,t_36,t_52,t_34,t_2);
        sas(103,t_2);
        sps(103,t_40,t_2,t_1);
        sas(104,t_36,t_40,t_2,t_1);
        sps(104,t_36,t_40,t_2,t_1);
        sas(105,t_36,t_3,t_2);
        sps(105,t_36,t_11,t_3,t_2);
        sas(106,t_36,t_3,t_2);
        sps(106,t_36,t_11,t_3,t_2);
        sas(107,t_36,t_34,t_2,t_32);
        sps(107,t_36,t_40,t_34,t_2,t_32,t_1);
        sas(108,t_53,t_36,t_34,t_2);
        sps(108,t_53,t_36,t_34,t_2);
        sas(109,t_36,t_3,t_2);
        sps(109,t_36,t_11,t_3,t_2);
        sas(110,t_2);
        sps(110,t_40,t_2,t_1);
        sas(111,t_36,t_3,t_2);
        sps(111,t_10,t_36,t_3,t_2);
        sas(112,t_36,t_34,t_2,t_49);
        sps(112,t_36,t_34,t_2,t_49);
        sas(113,t_40,t_2,t_1);
        sps(113,t_55,t_29,t_28,t_27,t_26,t_25,t_24,t_23,t_22,t_39,t_37,t_35,t_33,t_32,t_31,t_30,t_53,t_10,t_11,t_12,t_13,t_54,t_6,t_51,t_7,t_50,t_8,t_52,t_9,t_18,t_47,t_19,t_46,t_20,t_49,t_21,t_48,t_14,t_43,t_42,t_15,t_45,t_16,t_44,t_17,t_36,t_38,t_40,t_41,t_34,t_3,t_2,t_5,t_4,t_1);
        sas(114,t_36,t_3,t_2);
        sps(114,t_10,t_36,t_3,t_2);
        sas(115,t_36,t_3,t_2);
        sps(115,t_10,t_36,t_3,t_2);
        sas(116,t_36);
        sps(116,t_36,t_16,t_17);
        sas(117,t_36,t_40,t_2,t_1);
        sps(117,t_36,t_40,t_2,t_1);
        sas(118,t_10,t_36,t_3,t_2);
        sps(118,t_10,t_36,t_3,t_2);
        sas(119,t_36,t_50,t_34,t_2);
        sps(119,t_36,t_50,t_34,t_2);
        sas(120,t_36,t_34,t_3,t_2);
        sps(120,t_36,t_6,t_34,t_3,t_2);
        sas(121,t_36,t_2);
        sps(121,t_36,t_2);
        sas(122,t_36);
        sps(122,t_36,t_16,t_17);
        sas(123,t_36,t_3,t_2);
        sps(123,t_36,t_9,t_3,t_2);
        sas(124,t_36,t_39);
        sps(124,t_36,t_39);
        sas(125,t_36,t_34,t_2,t_46);
        sps(125,t_36,t_34,t_46,t_2);
        sas(126,t_36,t_9,t_3,t_2);
        sps(126,t_36,t_9,t_3,t_2);
        sas(127,t_36,t_34,t_2,t_48);
        sps(127,t_36,t_34,t_2,t_48);
        sas(128,t_36,t_40,t_2);
        sps(128,t_36,t_40,t_2);
        sas(129,t_36,t_34,t_47,t_2);
        sps(129,t_36,t_34,t_47,t_2);
        sps(130,t_2);
        sas(131,t_36);
        sps(131,t_36,t_18,t_19);
        sas(132,t_36,t_3,t_2);
        sps(132,t_36,t_9,t_3,t_2);
        sas(133,t_36,t_3,t_2);
        sps(133,t_36,t_9,t_3,t_2);
        sas(134,t_2);
        sps(134,t_40,t_2,t_1);
        sas(135,t_36,t_34,t_3,t_2);
        sps(135,t_36,t_8,t_34,t_3,t_2);
        sas(136,t_36,t_27);
        sps(136,t_36,t_27);
        sas(137,t_36);
        sps(137,t_36,t_18,t_19);
        sas(138,t_36,t_3,t_2);
        sps(138,t_36,t_9,t_3,t_2);
        sas(139,t_36);
        sps(139,t_36,t_27);
        sas(140,t_36,t_3,t_2);
        sps(140,t_36,t_9,t_3,t_2);
        sas(141,t_36,t_3,t_2);
        sps(141,t_36,t_9,t_3,t_2);
        sas(142,t_40,t_2);
        sps(142,t_40,t_2);
        sas(143,t_36,t_2);
        sps(143,t_36,t_2);
        sas(144,t_36,t_34,t_3,t_2);
        sps(144,t_36,t_7,t_34,t_3,t_2);
        sas(145,t_36,t_28);
        sps(145,t_36,t_28);
        sas(146,t_36,t_3,t_2);
        sps(146,t_36,t_9,t_3,t_2);
        sas(147,t_36);
        sps(147,t_36,t_28);
        sas(148,t_36,t_3,t_2);
        sps(148,t_36,t_9,t_3,t_2);
        sas(149,t_36);
        sps(149,t_36,t_28);
        sas(150,t_36,t_8,t_3,t_2);
        sps(150,t_36,t_8,t_3,t_2);
        sas(151,t_40,t_2);
        sps(151,t_40,t_2);
        sas(152,t_36,t_3,t_2);
        sps(152,t_36,t_8,t_3,t_2);
        sas(153,t_40,t_2);
        sps(153,t_40,t_2);
        sas(154,t_15);
        sps(154,t_2,t_15);
        sas(155,t_36,t_3,t_2);
        sps(155,t_36,t_8,t_3,t_2);
        sas(156,t_36,t_3,t_2);
        sps(156,t_36,t_8,t_3,t_2);
        sas(157,t_36,t_3,t_2);
        sps(157,t_36,t_8,t_3,t_2);
        sas(158,t_36,t_3,t_2);
        sps(158,t_36,t_8,t_3,t_2);
        sas(159,t_36,t_3,t_2);
        sps(159,t_36,t_8,t_3,t_2);
        sas(160,t_36,t_3,t_2);
        sps(160,t_36,t_8,t_3,t_2);
        sas(161,t_36,t_25);
        sps(161,t_36,t_25);
        sas(162,t_14);
        sps(162,t_14);
        sas(163,t_36,t_3,t_2);
        sps(163,t_36,t_8,t_3,t_2);
        sps(164,t_40,t_1);
        sas(165,t_36,t_3,t_2);
        sps(165,t_36,t_8,t_3,t_2);
        sps(166,t_40,t_1);
        sas(167,t_36);
        sps(167,t_36,t_28,t_23,t_22);
        sas(168,t_36,t_3,t_2);
        sps(168,t_36,t_8,t_3,t_2);
        sas(169,t_36);
        sps(169,t_36,t_20,t_21);
        sas(170,t_36,t_7,t_3,t_2);
        sps(170,t_36,t_7,t_3,t_2);
        sas(171,t_36);
        sps(171,t_36,t_26);
        sas(172,t_36,t_40,t_34,t_2,t_1);
        sps(172,t_36,t_40,t_34,t_2,t_1);
        sas(173,t_36);
        sps(173,t_36,t_26);
        sas(174,t_36,t_3,t_2);
        sps(174,t_36,t_7,t_3,t_2);
        sas(175,t_36);
        sps(175,t_36,t_20,t_21);
        sas(176,t_36,t_3,t_2);
        sps(176,t_36,t_7,t_3,t_2);
        sas(177,t_36,t_34,t_3,t_2);
        sps(177,t_36,t_34,t_9,t_3,t_2);
        sas(178,t_36,t_26);
        sps(178,t_36,t_26);
        sas(179,t_36,t_34,t_3,t_2);
        sps(179,t_10,t_36,t_34,t_3,t_2);
        sas(180,t_40);
        sps(180,t_40);
        sas(181,t_36,t_3,t_2);
        sps(181,t_36,t_7,t_3,t_2);
        sas(182,t_36);
        sps(182,t_36,t_26);
        sas(183,t_36,t_3,t_2);
        sps(183,t_36,t_6,t_3,t_2);
        sas(184,t_36);
        sps(184,t_36,t_26,t_25,t_24);
        sas(185,t_36,t_34,t_3,t_2);
        sps(185,t_36,t_13,t_34,t_3,t_2,t_4);
        sas(186,t_36,t_2);
        sps(186,t_36,t_40,t_2,t_1);
        sas(187,t_36);
        sps(187,t_36,t_26);
        sas(188,t_36,t_2);
        sps(188,t_36,t_40,t_2,t_1);
        sas(189,t_36,t_6,t_3,t_2);
        sps(189,t_36,t_6,t_3,t_2);
        sas(190,t_36,t_38,t_2);
        sps(190,t_36,t_38,t_2);
        sas(191,t_36,t_34,t_3,t_2);
        sps(191,t_36,t_12,t_34,t_3,t_2,t_5);
        sas(192,t_36);
        sps(192,t_36,t_27);
        sas(193,t_36,t_3,t_2);
        sps(193,t_36,t_6,t_3,t_2);
        sas(194,t_36,t_40,t_1);
        sps(194,t_36,t_40,t_1);
        sas(195,t_36);
        sps(195,t_36,t_27);
        sas(196,t_36,t_3,t_2,t_5);
        sps(196,t_36,t_3,t_2,t_5);
        sas(197,t_36,t_2);
        sps(197,t_36,t_40,t_2,t_1);
        sas(198,t_36);
        sps(198,t_36,t_27);
    }

    private static edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo[] staticStateInfo;
    static
    {
        t_1 = t("ws_no_line");
        t_2 = t("code_t");
        t_3 = t("name_tok");
        t_4 = t("assoctypes_kwd");
        t_5 = t("in_kwd");
        t_6 = t("with_kwd");
        t_7 = t("start_kwd");
        t_8 = t("disambiguate_kwd");
        t_9 = t("precedence_kwd");
        t_10 = t("class_kwd");
        t_11 = t("terminal_kwd");
        t_12 = t("ignore_kwd");
        t_13 = t("non_kwd");
        t_14 = t("code_block_close_kwd");
        t_15 = t("code_block_open_kwd");
        t_16 = t("cf_block_close_kwd");
        t_17 = t("cf_block_open_kwd");
        t_18 = t("lex_block_close_kwd");
        t_19 = t("lex_block_open_kwd");
        t_20 = t("init_block_close_kwd");
        t_21 = t("init_block_open_kwd");
        t_22 = t("aux_block_close_kwd");
        t_23 = t("aux_block_open_kwd");
        t_24 = t("prefix_decl_kwd");
        t_25 = t("prec_decl_kwd");
        t_26 = t("parser_decl_kwd");
        t_27 = t("layout_decl_kwd");
        t_28 = t("attribute_decl_kwd");
        t_29 = t("colon_kwd");
        t_30 = t("gt_kwd");
        t_31 = t("lt_kwd");
        t_32 = t("slash_kwd");
        t_33 = t("semi_kwd");
        t_34 = t("character");
        t_35 = t("comma_kwd");
        t_36 = t("termname");
        t_37 = t("goesto_kwd");
        t_38 = t("escaped");
        t_39 = t("barrier_kwd");
        t_40 = t("ws");
        t_41 = t("lbrace");
        t_42 = t("rparen");
        t_43 = t("wildcard");
        t_44 = t("rbrace");
        t_45 = t("lbrack");
        t_46 = t("not");
        t_47 = t("lparen");
        t_48 = t("rbrack");
        t_49 = t("bar");
        t_50 = t("question");
        t_51 = t("colon");
        t_52 = t("dash");
        t_53 = t("star");
        t_54 = t("plus");
        t_55 = t("prec_number");
        staticStateInfo = new edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo[199];
        for(int i = 0;i < 199;i++) staticStateInfo[i] = new edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo();
        symAdd_0();
    }
    protected int transition(int state,char ch)
    {
        switch(state)
        {
            case 1:
                return tr_1(ch);
            case 2:
                return tr_2(ch);
            case 3:
                return tr_3(ch);
            case 4:
                return tr_4(ch);
            case 5:
                return tr_5(ch);
            case 6:
                return tr_6(ch);
            case 7:
                return tr_7(ch);
            case 8:
                return tr_8(ch);
            case 9:
                return tr_9(ch);
            case 10:
                return tr_10(ch);
            case 11:
                return tr_11(ch);
            case 12:
                return tr_12(ch);
            case 13:
                return tr_13(ch);
            case 14:
                return tr_14(ch);
            case 15:
                return tr_15(ch);
            case 16:
                return tr_16(ch);
            case 17:
                return tr_17(ch);
            case 18:
                return tr_18(ch);
            case 19:
                return tr_19(ch);
            case 20:
                return tr_20(ch);
            case 21:
                return tr_21(ch);
            case 22:
                return tr_22(ch);
            case 23:
                return tr_23(ch);
            case 24:
                return tr_24(ch);
            case 25:
                return tr_25(ch);
            case 26:
                return tr_26(ch);
            case 27:
                return tr_27(ch);
            case 28:
                return tr_28(ch);
            case 29:
                return tr_29(ch);
            case 30:
                return tr_30(ch);
            case 31:
                return tr_31(ch);
            case 32:
                return tr_32(ch);
            case 33:
                return tr_33(ch);
            case 34:
                return tr_34(ch);
            case 35:
                return tr_35(ch);
            case 36:
                return tr_36(ch);
            case 37:
                return tr_37(ch);
            case 38:
                return tr_38(ch);
            case 39:
                return tr_39(ch);
            case 40:
                return tr_40(ch);
            case 41:
                return tr_41(ch);
            case 42:
                return tr_42(ch);
            case 43:
                return tr_43(ch);
            case 44:
                return tr_44(ch);
            case 45:
                return tr_45(ch);
            case 46:
                return tr_46(ch);
            case 47:
                return tr_47(ch);
            case 48:
                return tr_48(ch);
            case 49:
                return tr_49(ch);
            case 50:
                return tr_50(ch);
            case 51:
                return tr_51(ch);
            case 52:
                return tr_52(ch);
            case 53:
                return tr_53(ch);
            case 54:
                return tr_54(ch);
            case 55:
                return tr_55(ch);
            case 56:
                return tr_56(ch);
            case 57:
                return tr_57(ch);
            case 58:
                return tr_58(ch);
            case 59:
                return tr_59(ch);
            case 60:
                return tr_60(ch);
            case 61:
                return tr_61(ch);
            case 62:
                return tr_62(ch);
            case 63:
                return tr_63(ch);
            case 64:
                return tr_64(ch);
            case 65:
                return tr_65(ch);
            case 66:
                return tr_66(ch);
            case 67:
                return tr_67(ch);
            case 68:
                return tr_68(ch);
            case 69:
                return tr_69(ch);
            case 70:
                return tr_70(ch);
            case 71:
                return tr_71(ch);
            case 72:
                return tr_72(ch);
            case 73:
                return tr_73(ch);
            case 74:
                return tr_74(ch);
            case 75:
                return tr_75(ch);
            case 76:
                return tr_76(ch);
            case 77:
                return tr_77(ch);
            case 78:
                return tr_78(ch);
            case 79:
                return tr_79(ch);
            case 80:
                return tr_80(ch);
            case 81:
                return tr_81(ch);
            case 82:
                return tr_82(ch);
            case 83:
                return tr_83(ch);
            case 84:
                return tr_84(ch);
            case 85:
                return tr_85(ch);
            case 86:
                return tr_86(ch);
            case 87:
                return tr_87(ch);
            case 88:
                return tr_88(ch);
            case 89:
                return tr_89(ch);
            case 90:
                return tr_90(ch);
            case 91:
                return tr_91(ch);
            case 92:
                return tr_92(ch);
            case 93:
                return tr_93(ch);
            case 94:
                return tr_94(ch);
            case 95:
                return tr_95(ch);
            case 96:
                return tr_96(ch);
            case 97:
                return tr_97(ch);
            case 98:
                return tr_98(ch);
            case 99:
                return tr_99(ch);
            case 100:
                return tr_100(ch);
            case 101:
                return tr_101(ch);
            case 102:
                return tr_102(ch);
            case 103:
                return tr_103(ch);
            case 104:
                return tr_104(ch);
            case 105:
                return tr_105(ch);
            case 106:
                return tr_106(ch);
            case 107:
                return tr_107(ch);
            case 108:
                return tr_108(ch);
            case 109:
                return tr_109(ch);
            case 110:
                return tr_110(ch);
            case 111:
                return tr_111(ch);
            case 112:
                return tr_112(ch);
            case 113:
                return tr_113(ch);
            case 114:
                return tr_114(ch);
            case 115:
                return tr_115(ch);
            case 116:
                return tr_116(ch);
            case 117:
                return tr_117(ch);
            case 118:
                return tr_118(ch);
            case 119:
                return tr_119(ch);
            case 120:
                return tr_120(ch);
            case 121:
                return tr_121(ch);
            case 122:
                return tr_122(ch);
            case 123:
                return tr_123(ch);
            case 124:
                return tr_124(ch);
            case 125:
                return tr_125(ch);
            case 126:
                return tr_126(ch);
            case 127:
                return tr_127(ch);
            case 128:
                return tr_128(ch);
            case 129:
                return tr_129(ch);
            case 130:
                return tr_130(ch);
            case 131:
                return tr_131(ch);
            case 132:
                return tr_132(ch);
            case 133:
                return tr_133(ch);
            case 134:
                return tr_134(ch);
            case 135:
                return tr_135(ch);
            case 136:
                return tr_136(ch);
            case 137:
                return tr_137(ch);
            case 138:
                return tr_138(ch);
            case 139:
                return tr_139(ch);
            case 140:
                return tr_140(ch);
            case 141:
                return tr_141(ch);
            case 142:
                return tr_142(ch);
            case 143:
                return tr_143(ch);
            case 144:
                return tr_144(ch);
            case 145:
                return tr_145(ch);
            case 146:
                return tr_146(ch);
            case 147:
                return tr_147(ch);
            case 148:
                return tr_148(ch);
            case 149:
                return tr_149(ch);
            case 150:
                return tr_150(ch);
            case 151:
                return tr_151(ch);
            case 152:
                return tr_152(ch);
            case 153:
                return tr_153(ch);
            case 154:
                return tr_154(ch);
            case 155:
                return tr_155(ch);
            case 156:
                return tr_156(ch);
            case 157:
                return tr_157(ch);
            case 158:
                return tr_158(ch);
            case 159:
                return tr_159(ch);
            case 160:
                return tr_160(ch);
            case 161:
                return tr_161(ch);
            case 162:
                return tr_162(ch);
            case 163:
                return tr_163(ch);
            case 164:
                return tr_164(ch);
            case 165:
                return tr_165(ch);
            case 166:
                return tr_166(ch);
            case 167:
                return tr_167(ch);
            case 168:
                return tr_168(ch);
            case 169:
                return tr_169(ch);
            case 170:
                return tr_170(ch);
            case 171:
                return tr_171(ch);
            case 172:
                return tr_172(ch);
            case 173:
                return tr_173(ch);
            case 174:
                return tr_174(ch);
            case 175:
                return tr_175(ch);
            case 176:
                return tr_176(ch);
            case 177:
                return tr_177(ch);
            case 178:
                return tr_178(ch);
            case 179:
                return tr_179(ch);
            case 180:
                return tr_180(ch);
            case 181:
                return tr_181(ch);
            case 182:
                return tr_182(ch);
            case 183:
                return tr_183(ch);
            case 184:
                return tr_184(ch);
            case 185:
                return tr_185(ch);
            case 186:
                return tr_186(ch);
            case 187:
                return tr_187(ch);
            case 188:
                return tr_188(ch);
            case 189:
                return tr_189(ch);
            case 190:
                return tr_190(ch);
            case 191:
                return tr_191(ch);
            case 192:
                return tr_192(ch);
            case 193:
                return tr_193(ch);
            case 194:
                return tr_194(ch);
            case 195:
                return tr_195(ch);
            case 196:
                return tr_196(ch);
            case 197:
                return tr_197(ch);
            case 198:
                return tr_198(ch);
        default: return 0;
        }
    }
    private int tr_1(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_2(char ch)
    {
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 86;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_3(char ch)
    {
        if(chin(ch,'&',')')) return 103;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 103;
        if(cheq(ch,'*')) return 70;
        if(cheq(ch,'%')) return 166;
        if(chin(ch,' ','$')) return 103;
        if(cheq(ch,'\n')) return 56;
        if(cheq(ch,'\t')) return 103;
        if(chin(ch,';','~')) return 103;
        return 0;
    }
    private int tr_4(char ch)
    {
        if(chin(ch,'&','9')) return 4;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 80;
        if(chin(ch,' ','$')) return 4;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 4;
        if(chin(ch,';','~')) return 4;
        return 0;
    }
    private int tr_5(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'n')) return 9;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','m')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'o','z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_6(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_7(char ch)
    {
        if(chin(ch,'&',')')) return 103;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 103;
        if(cheq(ch,'*')) return 70;
        if(cheq(ch,'%')) return 166;
        if(chin(ch,' ','$')) return 103;
        if(cheq(ch,'\n')) return 56;
        if(cheq(ch,'\t')) return 103;
        if(chin(ch,';','~')) return 103;
        return 0;
    }
    private int tr_8(char ch)
    {
        if(chin(ch,'&',')')) return 197;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 197;
        if(cheq(ch,'*')) return 97;
        if(cheq(ch,'%')) return 66;
        if(chin(ch,' ','$')) return 197;
        if(cheq(ch,'\n')) return 8;
        if(cheq(ch,'\t')) return 197;
        if(chin(ch,';','~')) return 197;
        return 0;
    }
    private int tr_9(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'a')) return 35;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'b','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_10(char ch)
    {
        if(cheq(ch,'~')) return 84;
        if(chin(ch,'\t','\n')) return 84;
        if(chin(ch,'&','|')) return 84;
        if(cheq(ch,'%')) return 64;
        if(chin(ch,' ','$')) return 84;
        return 0;
    }
    private int tr_11(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_12(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_13(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','d')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'e')) return 105;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'f','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_14(char ch)
    {
        if(chin(ch,'&',')')) return 197;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 197;
        if(cheq(ch,'*')) return 97;
        if(cheq(ch,'%')) return 66;
        if(chin(ch,' ','$')) return 197;
        if(cheq(ch,'\n')) return 8;
        if(cheq(ch,'\t')) return 197;
        if(chin(ch,';','~')) return 197;
        return 0;
    }
    private int tr_15(char ch)
    {
        if(chin(ch,';','w')) return 57;
        if(cheq(ch,'x')) return 12;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'y','~')) return 57;
        return 0;
    }
    private int tr_16(char ch)
    {
        if(chin(ch,'>','~')) return 4;
        if(chin(ch,'&','9')) return 4;
        if(chin(ch,';','<')) return 4;
        if(cheq(ch,'=')) return 100;
        if(cheq(ch,':')) return 130;
        if(chin(ch,' ','$')) return 4;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 4;
        return 0;
    }
    private int tr_17(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_18(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_19(char ch)
    {
        if(cheq(ch,'t')) return 24;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'u','~')) return 57;
        if(chin(ch,';','s')) return 57;
        return 0;
    }
    private int tr_20(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'u','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'t')) return 63;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','s')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_21(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'h')) return 20;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(chin(ch,'a','g')) return 95;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'i','z')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_22(char ch)
    {
        if(chin(ch,'j','~')) return 57;
        if(cheq(ch,'i')) return 15;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','h')) return 57;
        return 0;
    }
    private int tr_23(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_24(char ch)
    {
        if(cheq(ch,'~')) return 57;
        if(cheq(ch,'}')) return 28;
        if(cheq(ch,'|')) return 57;
        if(cheq(ch,'{')) return 40;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','z')) return 57;
        return 0;
    }
    private int tr_25(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_26(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'g')) return 21;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'h','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'a','f')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_27(char ch)
    {
        if(chin(ch,'&',')')) return 7;
        if(cheq(ch,'~')) return 7;
        if(cheq(ch,'}')) return 166;
        if(chin(ch,'\t','\n')) return 7;
        if(cheq(ch,'*')) return 37;
        if(cheq(ch,'%')) return 44;
        if(chin(ch,' ','$')) return 7;
        if(chin(ch,'+','|')) return 7;
        return 0;
    }
    private int tr_28(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_29(char ch)
    {
        if(chin(ch,'a','e')) return 95;
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'f')) return 30;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'g','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_30(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'u','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'t')) return 54;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','s')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_31(char ch)
    {
        if(chin(ch,';','w')) return 57;
        if(cheq(ch,'x')) return 32;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'y','~')) return 57;
        return 0;
    }
    private int tr_32(char ch)
    {
        if(cheq(ch,'~')) return 57;
        if(cheq(ch,'}')) return 11;
        if(cheq(ch,'|')) return 57;
        if(cheq(ch,'{')) return 1;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','z')) return 57;
        return 0;
    }
    private int tr_33(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'t','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'s')) return 34;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'a','r')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_34(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'o')) return 41;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','n')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        if(chin(ch,'p','z')) return 95;
        return 0;
    }
    private int tr_35(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'t','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'s')) return 33;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'a','r')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_36(char ch)
    {
        if(chin(ch,' ','9')) return 36;
        if(cheq(ch,':')) return 180;
        if(cheq(ch,'\n')) return 57;
        if(cheq(ch,'\t')) return 36;
        if(chin(ch,';','~')) return 36;
        return 0;
    }
    private int tr_37(char ch)
    {
        if(chin(ch,'&','.')) return 110;
        if(cheq(ch,':')) return 38;
        if(cheq(ch,'/')) return 72;
        if(cheq(ch,'%')) return 164;
        if(chin(ch,' ','$')) return 110;
        if(chin(ch,'0','9')) return 110;
        if(cheq(ch,'\n')) return 67;
        if(cheq(ch,'\t')) return 110;
        if(chin(ch,';','~')) return 110;
        return 0;
    }
    private int tr_38(char ch)
    {
        if(chin(ch,'&',')')) return 7;
        if(cheq(ch,'~')) return 7;
        if(cheq(ch,'}')) return 166;
        if(chin(ch,'\t','\n')) return 7;
        if(cheq(ch,'*')) return 37;
        if(cheq(ch,'%')) return 44;
        if(chin(ch,' ','$')) return 7;
        if(chin(ch,'+','|')) return 7;
        return 0;
    }
    private int tr_39(char ch)
    {
        if(chin(ch,'&','9')) return 39;
        if(cheq(ch,':')) return 101;
        if(cheq(ch,'%')) return 82;
        if(chin(ch,' ','$')) return 39;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 39;
        if(chin(ch,';','~')) return 39;
        return 0;
    }
    private int tr_40(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_41(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'c')) return 46;
        if(chin(ch,'a','b')) return 95;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(chin(ch,'d','z')) return 95;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_42(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_43(char ch)
    {
        if(chin(ch,';','|')) return 84;
        if(cheq(ch,'~')) return 84;
        if(cheq(ch,'}')) return 162;
        if(chin(ch,'\t','\n')) return 84;
        if(chin(ch,'&','9')) return 84;
        if(cheq(ch,':')) return 16;
        if(cheq(ch,'%')) return 64;
        if(chin(ch,' ','$')) return 84;
        return 0;
    }
    private int tr_44(char ch)
    {
        if(chin(ch,'&',')')) return 103;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 103;
        if(cheq(ch,'*')) return 70;
        if(cheq(ch,'%')) return 166;
        if(chin(ch,' ','$')) return 103;
        if(cheq(ch,'\n')) return 56;
        if(cheq(ch,'\t')) return 103;
        if(chin(ch,';','~')) return 103;
        return 0;
    }
    private int tr_45(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_46(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_47(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_48(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 57;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_49(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_50(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_51(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_52(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 57;
        if(cheq(ch,' ')) return 104;
        if(chin(ch,'!','$')) return 143;
        if(cheq(ch,'\n')) return 52;
        if(cheq(ch,'\t')) return 104;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_53(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 57;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_54(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_55(char ch)
    {
        return 0;
    }
    private int tr_56(char ch)
    {
        if(chin(ch,'&',')')) return 103;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 103;
        if(cheq(ch,'*')) return 70;
        if(cheq(ch,'%')) return 166;
        if(chin(ch,' ','$')) return 103;
        if(cheq(ch,'\n')) return 56;
        if(cheq(ch,'\t')) return 103;
        if(chin(ch,';','~')) return 103;
        return 0;
    }
    private int tr_57(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_58(char ch)
    {
        if(chin(ch,'&',')')) return 197;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 197;
        if(cheq(ch,'*')) return 97;
        if(cheq(ch,'%')) return 66;
        if(chin(ch,' ','$')) return 197;
        if(cheq(ch,'\n')) return 8;
        if(cheq(ch,'\t')) return 197;
        if(chin(ch,';','~')) return 197;
        return 0;
    }
    private int tr_59(char ch)
    {
        if(chin(ch,'j','k')) return 57;
        if(chin(ch,'q','~')) return 57;
        if(cheq(ch,'p')) return 184;
        if(cheq(ch,'l')) return 69;
        if(cheq(ch,'i')) return 175;
        if(chin(ch,'\t','\n')) return 57;
        if(cheq(ch,'c')) return 116;
        if(cheq(ch,'b')) return 57;
        if(cheq(ch,'a')) return 167;
        if(chin(ch,'d','h')) return 57;
        if(chin(ch,'&','9')) return 57;
        if(chin(ch,'m','o')) return 57;
        if(cheq(ch,'%')) return 124;
        if(chin(ch,';','`')) return 57;
        if(chin(ch,' ','$')) return 57;
        return 0;
    }
    private int tr_60(char ch)
    {
        if(chin(ch,'&',')')) return 197;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 197;
        if(cheq(ch,'*')) return 97;
        if(cheq(ch,'%')) return 66;
        if(chin(ch,' ','$')) return 197;
        if(cheq(ch,'\n')) return 8;
        if(cheq(ch,'\t')) return 197;
        if(chin(ch,';','~')) return 197;
        return 0;
    }
    private int tr_61(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_62(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 154;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_63(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_64(char ch)
    {
        if(chin(ch,'&','9')) return 4;
        if(cheq(ch,':')) return 130;
        if(chin(ch,' ','$')) return 4;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 4;
        if(chin(ch,';','~')) return 4;
        return 0;
    }
    private int tr_65(char ch)
    {
        if(chin(ch,'&','9')) return 153;
        if(cheq(ch,':')) return 101;
        if(cheq(ch,'%')) return 180;
        if(chin(ch,' ','$')) return 153;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 153;
        if(chin(ch,';','~')) return 153;
        return 0;
    }
    private int tr_66(char ch)
    {
        if(chin(ch,' ',')')) return 66;
        if(chin(ch,'\t','\n')) return 66;
        if(cheq(ch,':')) return 166;
        if(chin(ch,'+','9')) return 66;
        if(cheq(ch,'*')) return 90;
        if(chin(ch,';','~')) return 66;
        return 0;
    }
    private int tr_67(char ch)
    {
        if(chin(ch,'&',')')) return 103;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 103;
        if(cheq(ch,'*')) return 70;
        if(cheq(ch,'%')) return 166;
        if(chin(ch,' ','$')) return 103;
        if(cheq(ch,'\n')) return 56;
        if(cheq(ch,'\t')) return 103;
        if(chin(ch,';','~')) return 103;
        return 0;
    }
    private int tr_68(char ch)
    {
        if(chin(ch,' ',')')) return 66;
        if(chin(ch,'\t','\n')) return 66;
        if(cheq(ch,':')) return 166;
        if(chin(ch,'+','9')) return 66;
        if(cheq(ch,'*')) return 90;
        if(chin(ch,';','~')) return 66;
        return 0;
    }
    private int tr_69(char ch)
    {
        if(chin(ch,'f','~')) return 57;
        if(chin(ch,'b','d')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(cheq(ch,'e')) return 137;
        if(chin(ch,'\t','\n')) return 57;
        if(cheq(ch,'a')) return 198;
        if(chin(ch,';','`')) return 57;
        return 0;
    }
    private int tr_70(char ch)
    {
        if(chin(ch,'&','.')) return 110;
        if(cheq(ch,':')) return 38;
        if(cheq(ch,'/')) return 72;
        if(cheq(ch,'%')) return 134;
        if(chin(ch,' ','$')) return 110;
        if(chin(ch,'0','9')) return 110;
        if(cheq(ch,'\n')) return 67;
        if(cheq(ch,'\t')) return 110;
        if(chin(ch,';','~')) return 110;
        return 0;
    }
    private int tr_71(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_72(char ch)
    {
        if(chin(ch,'&','9')) return 4;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 80;
        if(chin(ch,' ','$')) return 4;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 4;
        if(chin(ch,';','~')) return 4;
        return 0;
    }
    private int tr_73(char ch)
    {
        if(chin(ch,'&','9')) return 190;
        if(cheq(ch,':')) return 10;
        if(cheq(ch,'%')) return 48;
        if(chin(ch,' ','$')) return 190;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 190;
        if(chin(ch,';','~')) return 190;
        return 0;
    }
    private int tr_74(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_75(char ch)
    {
        if(chin(ch,'d','e')) return 57;
        if(chin(ch,'g','~')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(cheq(ch,'f')) return 22;
        if(chin(ch,'\t','\n')) return 57;
        if(cheq(ch,'c')) return 161;
        if(chin(ch,';','b')) return 57;
        return 0;
    }
    private int tr_76(char ch)
    {
        if(chin(ch,'&','9')) return 4;
        if(cheq(ch,':')) return 130;
        if(chin(ch,' ','$')) return 4;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 4;
        if(chin(ch,';','~')) return 4;
        return 0;
    }
    private int tr_77(char ch)
    {
        if(chin(ch,'f','~')) return 57;
        if(chin(ch,';','d')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(cheq(ch,'e')) return 75;
        if(chin(ch,'\t','\n')) return 57;
        return 0;
    }
    private int tr_78(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'i')) return 26;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','h')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'j','z')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_79(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','d')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'e')) return 83;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'f','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_80(char ch)
    {
        if(chin(ch,'&','9')) return 4;
        if(cheq(ch,':')) return 130;
        if(chin(ch,' ','$')) return 4;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 4;
        if(chin(ch,';','~')) return 4;
        return 0;
    }
    private int tr_81(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','q')) return 95;
        if(chin(ch,'s','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'r')) return 79;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_82(char ch)
    {
        if(chin(ch,'&','9')) return 39;
        if(cheq(ch,':')) return 101;
        if(cheq(ch,'%')) return 36;
        if(chin(ch,' ','$')) return 39;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 39;
        if(chin(ch,';','~')) return 39;
        return 0;
    }
    private int tr_83(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_84(char ch)
    {
        if(chin(ch,'&','9')) return 4;
        if(cheq(ch,':')) return 130;
        if(chin(ch,' ','$')) return 4;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 4;
        if(chin(ch,';','~')) return 4;
        return 0;
    }
    private int tr_85(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_86(char ch)
    {
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 86;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_87(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','d')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'e')) return 29;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'f','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_88(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_89(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'o')) return 81;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','n')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        if(chin(ch,'p','z')) return 95;
        return 0;
    }
    private int tr_90(char ch)
    {
        if(chin(ch,' ','.')) return 68;
        if(chin(ch,'\t','\n')) return 68;
        if(cheq(ch,':')) return 164;
        if(cheq(ch,'/')) return 194;
        if(chin(ch,'0','9')) return 68;
        if(chin(ch,';','~')) return 68;
        return 0;
    }
    private int tr_91(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'n')) return 89;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','m')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'o','z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_92(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'n')) return 93;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','m')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'o','z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_93(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'a')) return 98;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'b','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_94(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_95(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_96(char ch)
    {
        if(chin(ch,' ','.')) return 164;
        if(chin(ch,'\t','\n')) return 164;
        if(chin(ch,'0','~')) return 164;
        if(cheq(ch,'/')) return 55;
        return 0;
    }
    private int tr_97(char ch)
    {
        if(chin(ch,'&','.')) return 186;
        if(cheq(ch,':')) return 38;
        if(cheq(ch,'/')) return 85;
        if(cheq(ch,'%')) return 60;
        if(chin(ch,' ','$')) return 186;
        if(chin(ch,'0','9')) return 186;
        if(cheq(ch,'\n')) return 14;
        if(cheq(ch,'\t')) return 186;
        if(chin(ch,';','~')) return 186;
        return 0;
    }
    private int tr_98(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','k')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'l')) return 99;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'m','z')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_99(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_100(char ch)
    {
        if(chin(ch,'&','9')) return 4;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 80;
        if(chin(ch,' ','$')) return 4;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 4;
        if(chin(ch,';','~')) return 4;
        return 0;
    }
    private int tr_101(char ch)
    {
        if(cheq(ch,'~')) return 151;
        if(cheq(ch,'}')) return 180;
        if(chin(ch,'&','|')) return 151;
        if(cheq(ch,'%')) return 65;
        if(chin(ch,' ','$')) return 151;
        if(cheq(ch,'\n')) return 84;
        if(cheq(ch,'\t')) return 151;
        return 0;
    }
    private int tr_102(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_103(char ch)
    {
        if(chin(ch,'&',')')) return 103;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 103;
        if(cheq(ch,'*')) return 70;
        if(cheq(ch,'%')) return 3;
        if(chin(ch,' ','$')) return 103;
        if(cheq(ch,'\n')) return 56;
        if(cheq(ch,'\t')) return 103;
        if(chin(ch,';','~')) return 103;
        return 0;
    }
    private int tr_104(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(cheq(ch,' ')) return 104;
        if(chin(ch,'!','$')) return 143;
        if(cheq(ch,'\n')) return 52;
        if(cheq(ch,'\t')) return 104;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_105(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','q')) return 95;
        if(chin(ch,'s','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'r')) return 106;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_106(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'m')) return 109;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'a','l')) return 95;
        if(chin(ch,'n','z')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_107(char ch)
    {
        if(chin(ch,'&',')')) return 143;
        if(chin(ch,'+','.')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'/')) return 128;
        if(cheq(ch,'*')) return 188;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_108(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_109(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'i')) return 92;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','h')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'j','z')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_110(char ch)
    {
        if(chin(ch,'&',')')) return 103;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 103;
        if(cheq(ch,'*')) return 70;
        if(cheq(ch,'%')) return 3;
        if(chin(ch,' ','$')) return 103;
        if(cheq(ch,'\n')) return 56;
        if(cheq(ch,'\t')) return 103;
        if(chin(ch,';','~')) return 103;
        return 0;
    }
    private int tr_111(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'a')) return 114;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'b','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_112(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_113(char ch)
    {
        if(chin(ch,'x','z')) return 23;
        if(chin(ch,'j','k')) return 23;
        if(cheq(ch,'~')) return 74;
        if(cheq(ch,'}')) return 50;
        if(cheq(ch,'|')) return 112;
        if(cheq(ch,'{')) return 62;
        if(cheq(ch,'w')) return 120;
        if(cheq(ch,'t')) return 13;
        if(cheq(ch,'s')) return 144;
        if(cheq(ch,'r')) return 78;
        if(cheq(ch,'q')) return 23;
        if(cheq(ch,'p')) return 177;
        if(cheq(ch,'o')) return 23;
        if(cheq(ch,'n')) return 185;
        if(cheq(ch,'m')) return 23;
        if(cheq(ch,'l')) return 87;
        if(cheq(ch,'i')) return 191;
        if(cheq(ch,'d')) return 135;
        if(cheq(ch,'c')) return 179;
        if(cheq(ch,'`')) return 74;
        if(chin(ch,'a','b')) return 23;
        if(cheq(ch,'_')) return 23;
        if(cheq(ch,'^')) return 125;
        if(cheq(ch,']')) return 127;
        if(cheq(ch,'\\')) return 73;
        if(cheq(ch,'[')) return 45;
        if(chin(ch,'&','\'')) return 74;
        if(chin(ch,'e','h')) return 23;
        if(cheq(ch,'@')) return 74;
        if(cheq(ch,'?')) return 119;
        if(cheq(ch,'>')) return 17;
        if(cheq(ch,'=')) return 74;
        if(cheq(ch,'<')) return 18;
        if(cheq(ch,';')) return 25;
        if(cheq(ch,':')) return 43;
        if(cheq(ch,'0')) return 88;
        if(cheq(ch,'/')) return 107;
        if(cheq(ch,'.')) return 49;
        if(cheq(ch,'-')) return 102;
        if(cheq(ch,',')) return 6;
        if(chin(ch,'u','v')) return 23;
        if(cheq(ch,'+')) return 94;
        if(cheq(ch,'*')) return 108;
        if(cheq(ch,')')) return 42;
        if(cheq(ch,'(')) return 129;
        if(cheq(ch,'%')) return 59;
        if(cheq(ch,' ')) return 172;
        if(chin(ch,'!','$')) return 74;
        if(chin(ch,'1','9')) return 2;
        if(cheq(ch,'\n')) return 117;
        if(cheq(ch,'\t')) return 172;
        if(chin(ch,'A','Z')) return 23;
        return 0;
    }
    private int tr_114(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'t','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'s')) return 115;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'a','r')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_115(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'t','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'s')) return 118;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'a','r')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_116(char ch)
    {
        if(chin(ch,';','e')) return 57;
        if(chin(ch,'g','~')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(cheq(ch,'f')) return 122;
        if(chin(ch,'\t','\n')) return 57;
        return 0;
    }
    private int tr_117(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 57;
        if(cheq(ch,' ')) return 104;
        if(chin(ch,'!','$')) return 143;
        if(cheq(ch,'\n')) return 52;
        if(cheq(ch,'\t')) return 104;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_118(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_119(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_120(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'i')) return 193;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','h')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'j','z')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_121(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 57;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_122(char ch)
    {
        if(cheq(ch,'~')) return 57;
        if(cheq(ch,'}')) return 71;
        if(cheq(ch,'|')) return 57;
        if(cheq(ch,'{')) return 47;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','z')) return 57;
        return 0;
    }
    private int tr_123(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','d')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'e')) return 126;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'f','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_124(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_125(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_126(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_127(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_128(char ch)
    {
        if(chin(ch,'&','9')) return 39;
        if(cheq(ch,':')) return 101;
        if(cheq(ch,'%')) return 82;
        if(chin(ch,' ','$')) return 39;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 39;
        if(chin(ch,';','~')) return 39;
        return 0;
    }
    private int tr_129(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_130(char ch)
    {
        if(cheq(ch,'~')) return 84;
        if(chin(ch,'\t','\n')) return 84;
        if(chin(ch,'&','|')) return 84;
        if(cheq(ch,'%')) return 64;
        if(chin(ch,' ','$')) return 84;
        return 0;
    }
    private int tr_131(char ch)
    {
        if(cheq(ch,'~')) return 57;
        if(cheq(ch,'}')) return 61;
        if(cheq(ch,'|')) return 57;
        if(cheq(ch,'{')) return 51;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','z')) return 57;
        return 0;
    }
    private int tr_132(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'c')) return 123;
        if(chin(ch,'a','b')) return 95;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(chin(ch,'d','z')) return 95;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_133(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'n')) return 132;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','m')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'o','z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_134(char ch)
    {
        if(chin(ch,'&',')')) return 103;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 103;
        if(cheq(ch,'*')) return 70;
        if(cheq(ch,'%')) return 166;
        if(chin(ch,' ','$')) return 103;
        if(cheq(ch,'\n')) return 56;
        if(cheq(ch,'\t')) return 103;
        if(chin(ch,';','~')) return 103;
        return 0;
    }
    private int tr_135(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'i')) return 168;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','h')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'j','z')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_136(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_137(char ch)
    {
        if(chin(ch,';','w')) return 57;
        if(cheq(ch,'x')) return 131;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'y','~')) return 57;
        return 0;
    }
    private int tr_138(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','d')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'e')) return 133;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'f','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_139(char ch)
    {
        if(cheq(ch,'t')) return 136;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'u','~')) return 57;
        if(chin(ch,';','s')) return 57;
        return 0;
    }
    private int tr_140(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(chin(ch,'a','c')) return 95;
        if(cheq(ch,'d')) return 138;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'e','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_141(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','d')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'e')) return 140;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'f','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_142(char ch)
    {
        if(chin(ch,'&','9')) return 153;
        if(cheq(ch,':')) return 101;
        if(cheq(ch,'%')) return 180;
        if(chin(ch,' ','$')) return 153;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 153;
        if(chin(ch,';','~')) return 153;
        return 0;
    }
    private int tr_143(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_144(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'u','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'t')) return 181;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','s')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_145(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_146(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'c')) return 141;
        if(chin(ch,'a','b')) return 95;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(chin(ch,'d','z')) return 95;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_147(char ch)
    {
        if(chin(ch,';','q')) return 57;
        if(cheq(ch,'r')) return 145;
        if(chin(ch,'s','~')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        return 0;
    }
    private int tr_148(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','d')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'e')) return 146;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'f','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_149(char ch)
    {
        if(cheq(ch,'t')) return 147;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'u','~')) return 57;
        if(chin(ch,';','s')) return 57;
        return 0;
    }
    private int tr_150(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_151(char ch)
    {
        if(chin(ch,'&','9')) return 153;
        if(cheq(ch,':')) return 101;
        if(cheq(ch,'%')) return 180;
        if(chin(ch,' ','$')) return 153;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 153;
        if(chin(ch,';','~')) return 153;
        return 0;
    }
    private int tr_152(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','d')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'e')) return 150;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'f','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_153(char ch)
    {
        if(chin(ch,'&','9')) return 153;
        if(cheq(ch,':')) return 101;
        if(cheq(ch,'%')) return 142;
        if(chin(ch,' ','$')) return 153;
        if(cheq(ch,'\n')) return 76;
        if(cheq(ch,'\t')) return 153;
        if(chin(ch,';','~')) return 153;
        return 0;
    }
    private int tr_154(char ch)
    {
        if(cheq(ch,'~')) return 84;
        if(chin(ch,'\t','\n')) return 84;
        if(chin(ch,'&','|')) return 84;
        if(cheq(ch,'%')) return 64;
        if(chin(ch,' ','$')) return 84;
        return 0;
    }
    private int tr_155(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'u','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'t')) return 152;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','s')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_156(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'a')) return 155;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'b','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_157(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'v','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'u')) return 156;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','t')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_158(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'g')) return 157;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'h','z')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'a','f')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_159(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'i')) return 158;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','h')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'j','z')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_160(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'b')) return 159;
        if(cheq(ch,'a')) return 95;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(chin(ch,'c','z')) return 95;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_161(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_162(char ch)
    {
        return 0;
    }
    private int tr_163(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'a')) return 165;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'b','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_164(char ch)
    {
        if(chin(ch,' ',')')) return 166;
        if(chin(ch,'\t','\n')) return 166;
        if(chin(ch,'+','~')) return 166;
        if(cheq(ch,'*')) return 96;
        return 0;
    }
    private int tr_165(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'m')) return 160;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'a','l')) return 95;
        if(chin(ch,'n','z')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_166(char ch)
    {
        if(chin(ch,' ',')')) return 166;
        if(chin(ch,'\t','\n')) return 166;
        if(chin(ch,'+','~')) return 166;
        if(cheq(ch,'*')) return 96;
        return 0;
    }
    private int tr_167(char ch)
    {
        if(cheq(ch,'u')) return 31;
        if(cheq(ch,'t')) return 149;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'v','~')) return 57;
        if(chin(ch,';','s')) return 57;
        return 0;
    }
    private int tr_168(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'t','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'s')) return 163;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'a','r')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_169(char ch)
    {
        if(chin(ch,'j','~')) return 57;
        if(cheq(ch,'i')) return 19;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','h')) return 57;
        return 0;
    }
    private int tr_170(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_171(char ch)
    {
        if(chin(ch,';','q')) return 57;
        if(cheq(ch,'r')) return 173;
        if(chin(ch,'s','~')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        return 0;
    }
    private int tr_172(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(cheq(ch,' ')) return 104;
        if(chin(ch,'!','$')) return 143;
        if(cheq(ch,'\n')) return 52;
        if(cheq(ch,'\t')) return 104;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_173(char ch)
    {
        if(cheq(ch,'s')) return 182;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'t','~')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','r')) return 57;
        return 0;
    }
    private int tr_174(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','q')) return 95;
        if(chin(ch,'s','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'r')) return 176;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_175(char ch)
    {
        if(chin(ch,'o','~')) return 57;
        if(cheq(ch,'n')) return 169;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','m')) return 57;
        return 0;
    }
    private int tr_176(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'u','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'t')) return 170;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','s')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_177(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','q')) return 95;
        if(chin(ch,'s','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'r')) return 148;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_178(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_179(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'a','k')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'l')) return 111;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'m','z')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_180(char ch)
    {
        if(chin(ch,' ','~')) return 180;
        if(cheq(ch,'\t')) return 180;
        return 0;
    }
    private int tr_181(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'a')) return 174;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'b','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_182(char ch)
    {
        if(chin(ch,'f','~')) return 57;
        if(chin(ch,';','d')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(cheq(ch,'e')) return 187;
        if(chin(ch,'\t','\n')) return 57;
        return 0;
    }
    private int tr_183(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'h')) return 189;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(chin(ch,'a','g')) return 95;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'i','z')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_184(char ch)
    {
        if(chin(ch,'b','q')) return 57;
        if(cheq(ch,'r')) return 77;
        if(chin(ch,'s','~')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(cheq(ch,'a')) return 171;
        if(chin(ch,';','`')) return 57;
        return 0;
    }
    private int tr_185(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'o')) return 5;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','n')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        if(chin(ch,'p','z')) return 95;
        return 0;
    }
    private int tr_186(char ch)
    {
        if(chin(ch,'&',')')) return 197;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 197;
        if(cheq(ch,'*')) return 97;
        if(cheq(ch,'%')) return 58;
        if(chin(ch,' ','$')) return 197;
        if(cheq(ch,'\n')) return 8;
        if(cheq(ch,'\t')) return 197;
        if(chin(ch,';','~')) return 197;
        return 0;
    }
    private int tr_187(char ch)
    {
        if(chin(ch,';','q')) return 57;
        if(cheq(ch,'r')) return 178;
        if(chin(ch,'s','~')) return 57;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        return 0;
    }
    private int tr_188(char ch)
    {
        if(chin(ch,'&',')')) return 197;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 197;
        if(cheq(ch,'*')) return 97;
        if(cheq(ch,'%')) return 58;
        if(chin(ch,' ','$')) return 197;
        if(cheq(ch,'\n')) return 8;
        if(cheq(ch,'\t')) return 197;
        if(chin(ch,';','~')) return 197;
        return 0;
    }
    private int tr_189(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_190(char ch)
    {
        if(chin(ch,'&','9')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,';','~')) return 143;
        return 0;
    }
    private int tr_191(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'n')) return 196;
        if(cheq(ch,'g')) return 91;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,'a','f')) return 95;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(chin(ch,'h','m')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'o','z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_192(char ch)
    {
        if(chin(ch,'p','~')) return 57;
        if(cheq(ch,'o')) return 195;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','n')) return 57;
        return 0;
    }
    private int tr_193(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'u','z')) return 95;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'t')) return 183;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(chin(ch,'a','s')) return 95;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_194(char ch)
    {
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,';','~')) return 57;
        return 0;
    }
    private int tr_195(char ch)
    {
        if(cheq(ch,'u')) return 139;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'v','~')) return 57;
        if(chin(ch,';','t')) return 57;
        return 0;
    }
    private int tr_196(char ch)
    {
        if(chin(ch,';','@')) return 143;
        if(chin(ch,'&','/')) return 143;
        if(cheq(ch,'`')) return 143;
        if(cheq(ch,'_')) return 95;
        if(chin(ch,'a','z')) return 95;
        if(chin(ch,'{','~')) return 143;
        if(cheq(ch,':')) return 130;
        if(cheq(ch,'%')) return 53;
        if(chin(ch,' ','$')) return 143;
        if(chin(ch,'0','9')) return 95;
        if(cheq(ch,'\n')) return 121;
        if(cheq(ch,'\t')) return 143;
        if(chin(ch,'A','Z')) return 95;
        if(chin(ch,'[','^')) return 143;
        return 0;
    }
    private int tr_197(char ch)
    {
        if(chin(ch,'&',')')) return 197;
        if(cheq(ch,':')) return 27;
        if(chin(ch,'+','9')) return 197;
        if(cheq(ch,'*')) return 97;
        if(cheq(ch,'%')) return 58;
        if(chin(ch,' ','$')) return 197;
        if(cheq(ch,'\n')) return 8;
        if(cheq(ch,'\t')) return 197;
        if(chin(ch,';','~')) return 197;
        return 0;
    }
    private int tr_198(char ch)
    {
        if(cheq(ch,'y')) return 192;
        if(chin(ch,' ','9')) return 57;
        if(chin(ch,'\t','\n')) return 57;
        if(chin(ch,'z','~')) return 57;
        if(chin(ch,';','x')) return 57;
        return 0;
    }
    protected edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatch getMatch(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t,edu.umn.cs.melt.copper.runtime.io.InputPosition pp,edu.umn.cs.melt.copper.runtime.io.InputPosition pf,java.util.ArrayList<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> l)
    {
        return newlong(t,pp,pf,l);
    }
    protected edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo getStateInfo(int state)
    {
        return staticStateInfo[state];
    }
    protected String runSemanticAction(edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal token)
    {
        String yytext = token.getLexeme();
        if(yytext == null) return "";
        return yytext;
    }


}
