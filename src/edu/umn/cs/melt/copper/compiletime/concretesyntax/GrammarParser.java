package edu.umn.cs.melt.copper.compiletime.concretesyntax;

import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;


public class GrammarParser extends edu.umn.cs.melt.copper.compiletime.engines.lalr.LALREngine
{
    public GrammarParser(java.io.Reader reader,edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger logger)
    {
        scanner = new GrammarParserScanner(reader,logger);
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

    private static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal sym_0,sym_1,sym_2,sym_3,sym_4,sym_5,sym_6,sym_7,sym_8,sym_9,sym_10,sym_11,sym_12,sym_13,sym_14,sym_15,sym_16,sym_17,sym_18,sym_19,sym_20,sym_21,sym_22,sym_23,sym_24,sym_25,sym_26,sym_27,sym_28,sym_29,sym_30,sym_31,sym_32,sym_33,sym_34,sym_35,sym_36,sym_37,sym_38,sym_39,sym_40,sym_41,sym_42,sym_43,sym_44,sym_45,sym_46,sym_47,sym_48,sym_49,sym_50,sym_51,sym_52,sym_53,sym_54,sym_55,sym_56,sym_57,sym_58,sym_59;
    private static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal sym_60,sym_61,sym_62,sym_63,sym_64,sym_65,sym_66,sym_67,sym_68,sym_69,sym_70,sym_71,sym_72,sym_73,sym_74,sym_75,sym_76,sym_77,sym_78,sym_79,sym_80,sym_81,sym_82,sym_83,sym_84,sym_85,sym_86;
    @SuppressWarnings("unused")
    private static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production p_0,p_1,p_2,p_3,p_4,p_5,p_6,p_7,p_8,p_9,p_10,p_11,p_12,p_13,p_14,p_15,p_16,p_17,p_18,p_19,p_20,p_21,p_22,p_23,p_24,p_25,p_26,p_27,p_28,p_29,p_30,p_31,p_32,p_33,p_34,p_35,p_36,p_37,p_38,p_39,p_40,p_41,p_42,p_43,p_44,p_45,p_46,p_47,p_48,p_49,p_50,p_51,p_52,p_53,p_54,p_55,p_56;
    private static java.util.HashSet<edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal> group_0,group_1,group_2,group_3,group_4,group_5,group_6,group_7,group_8,group_9,group_10,group_11;

    public static ThisParseTable parseTable;

    private static class ThisParseTable extends edu.umn.cs.melt.copper.compiletime.parsetable.LazyGLRParseTable
    {
        public ThisParseTable()
        {
            super(174);
        }
        public void initShiftableUnion()
        {
            shiftableUnion = tset(sym_0,sym_1,sym_2,sym_3,sym_4,sym_5,sym_6,sym_7,sym_8,sym_9,sym_10,sym_11,sym_12,sym_13,sym_14,sym_15,sym_16,sym_17,sym_18,sym_19,sym_20,sym_21,sym_22,sym_23,sym_24,sym_25,sym_26,sym_27,sym_28,sym_29,sym_30,sym_31,sym_32,sym_33,sym_34,sym_35,sym_36,sym_37,sym_38,sym_39,sym_40,sym_41,sym_42,sym_43,sym_44,sym_45,sym_46,sym_47,sym_48,sym_50,sym_51,sym_52,sym_53,sym_54,sym_55,sym_56,sym_57,sym_58,sym_59);
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
        ^ ::= (*) GrammarFile $
        GrammarFile ::= (*) grammar_decl grammar_name_decl grammarname_tok spectype_decl spectypes grammar_version newline Grammar

    */
    public static void init_0()
    {
        addA(0,sym_55,sh(2));
        addG(0,sym_82,sh(1));
        addL(0,sym_49,sym_55);
    }
    /*
        ^ ::= GrammarFile (*) $

    */
    public static void init_1()
    {
        addA(1,sym_7,a());
        addL(1,sym_49,sym_7);
    }
    /*
        GrammarFile ::= grammar_decl (*) grammar_name_decl grammarname_tok spectype_decl spectypes grammar_version newline Grammar

    */
    public static void init_2()
    {
        addA(2,sym_53,sh(3));
        addL(2,sym_49,sym_53);
    }
    /*
        GrammarFile ::= grammar_decl grammar_name_decl (*) grammarname_tok spectype_decl spectypes grammar_version newline Grammar

    */
    public static void init_3()
    {
        addA(3,sym_54,sh(4));
        addL(3,sym_49,sym_54);
    }
    /*
        GrammarFile ::= grammar_decl grammar_name_decl grammarname_tok (*) spectype_decl spectypes grammar_version newline Grammar

    */
    public static void init_4()
    {
        addA(4,sym_59,sh(5));
        addL(4,sym_49,sym_59);
    }
    /*
        GrammarFile ::= grammar_decl grammar_name_decl grammarname_tok spectype_decl (*) spectypes grammar_version newline Grammar

    */
    public static void init_5()
    {
        addA(5,sym_58,sh(6));
        addL(5,sym_49,sym_58);
    }
    /*
        GrammarFile ::= grammar_decl grammar_name_decl grammarname_tok spectype_decl spectypes (*) grammar_version newline Grammar

    */
    public static void init_6()
    {
        addA(6,sym_57,sh(7));
        addL(6,sym_49,sym_57);
    }
    /*
        GrammarFile ::= grammar_decl grammar_name_decl grammarname_tok spectype_decl spectypes grammar_version (*) newline Grammar

    */
    public static void init_7()
    {
        addA(7,sym_56,sh(8));
        addL(7,sym_49,sym_56);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        GrammarFile ::= grammar_decl grammar_name_decl grammarname_tok spectype_decl spectypes grammar_version newline (*) Grammar
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_8()
    {
        addA(8,sym_22,sh(12));
        addA(8,sym_17,sh(10));
        addA(8,sym_18,sh(21));
        addA(8,sym_12,sh(28));
        addA(8,sym_36,sh(9));
        addA(8,sym_11,sh(27));
        addA(8,sym_10,sh(14));
        addA(8,sym_9,sh(24));
        addA(8,sym_32,sh(29));
        addA(8,sym_7,fr(p_56));
        addA(8,sym_25,sh(16));
        addG(8,sym_60,sh(26));
        addG(8,sym_68,sh(18));
        addG(8,sym_67,sh(22));
        addG(8,sym_65,sh(20));
        addG(8,sym_64,sh(13));
        addG(8,sym_85,sh(23));
        addG(8,sym_86,sh(25));
        addG(8,sym_73,sh(17));
        addG(8,sym_63,sh(19));
        addG(8,sym_62,sh(15));
        addG(8,sym_61,sh(11));
        addL(8,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        StartLine ::= start_decl (*) nonterm_tok layout_decl lbrace TSeq rbrace newline

    */
    public static void init_9()
    {
        addA(9,sym_0,sh(33));
        addL(9,sym_49,sym_0);
    }
    /*
        OpLine ::= operator_decl (*) terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline

    */
    public static void init_10()
    {
        addA(10,sym_1,sh(35));
        addL(10,sym_49,sym_1);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        Grammar ::= AttrLine (*) Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_11()
    {
        addA(11,sym_22,sh(12));
        addA(11,sym_17,sh(10));
        addA(11,sym_18,sh(21));
        addA(11,sym_12,sh(28));
        addA(11,sym_11,sh(27));
        addA(11,sym_36,sh(9));
        addA(11,sym_10,sh(14));
        addA(11,sym_9,sh(24));
        addA(11,sym_32,sh(29));
        addA(11,sym_7,fr(p_56));
        addA(11,sym_25,sh(16));
        addG(11,sym_60,sh(26));
        addG(11,sym_68,sh(18));
        addG(11,sym_67,sh(22));
        addG(11,sym_65,sh(20));
        addG(11,sym_64,sh(13));
        addG(11,sym_85,sh(23));
        addG(11,sym_86,sh(30));
        addG(11,sym_73,sh(17));
        addG(11,sym_63,sh(19));
        addG(11,sym_62,sh(15));
        addG(11,sym_61,sh(11));
        addL(11,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        ProdLine ::= prod_decl (*) prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_12()
    {
        addA(12,sym_3,sh(47));
        addL(12,sym_49,sym_3);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        Grammar ::= TLine (*) Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_13()
    {
        addA(13,sym_22,sh(12));
        addA(13,sym_17,sh(10));
        addA(13,sym_18,sh(21));
        addA(13,sym_12,sh(28));
        addA(13,sym_36,sh(9));
        addA(13,sym_11,sh(27));
        addA(13,sym_10,sh(14));
        addA(13,sym_9,sh(24));
        addA(13,sym_32,sh(29));
        addA(13,sym_7,fr(p_56));
        addA(13,sym_25,sh(16));
        addG(13,sym_60,sh(26));
        addG(13,sym_68,sh(18));
        addG(13,sym_67,sh(22));
        addG(13,sym_65,sh(20));
        addG(13,sym_64,sh(13));
        addG(13,sym_85,sh(23));
        addG(13,sym_86,sh(39));
        addG(13,sym_73,sh(17));
        addG(13,sym_63,sh(19));
        addG(13,sym_62,sh(15));
        addG(13,sym_61,sh(11));
        addL(13,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        TLine ::= t_decl (*) terminal_tok Regex_Root

    */
    public static void init_14()
    {
        addA(14,sym_1,sh(31));
        addL(14,sym_49,sym_1);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        Grammar ::= GroupLine (*) Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_15()
    {
        addA(15,sym_22,sh(12));
        addA(15,sym_17,sh(10));
        addA(15,sym_18,sh(21));
        addA(15,sym_12,sh(28));
        addA(15,sym_36,sh(9));
        addA(15,sym_11,sh(27));
        addA(15,sym_10,sh(14));
        addA(15,sym_9,sh(24));
        addA(15,sym_32,sh(29));
        addA(15,sym_7,fr(p_56));
        addA(15,sym_25,sh(16));
        addG(15,sym_60,sh(26));
        addG(15,sym_68,sh(18));
        addG(15,sym_67,sh(22));
        addG(15,sym_65,sh(20));
        addG(15,sym_64,sh(13));
        addG(15,sym_85,sh(23));
        addG(15,sym_86,sh(32));
        addG(15,sym_73,sh(17));
        addG(15,sym_63,sh(19));
        addG(15,sym_62,sh(15));
        addG(15,sym_61,sh(11));
        addL(15,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        AttrLine ::= attr_decl (*) attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline

    */
    public static void init_16()
    {
        addA(16,sym_5,sh(40));
        addL(16,sym_49,sym_5);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        Grammar ::= DefaultProdCodeLine (*) Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_17()
    {
        addA(17,sym_22,sh(12));
        addA(17,sym_17,sh(10));
        addA(17,sym_18,sh(21));
        addA(17,sym_12,sh(28));
        addA(17,sym_36,sh(9));
        addA(17,sym_11,sh(27));
        addA(17,sym_10,sh(14));
        addA(17,sym_9,sh(24));
        addA(17,sym_32,sh(29));
        addA(17,sym_7,fr(p_56));
        addA(17,sym_25,sh(16));
        addG(17,sym_60,sh(26));
        addG(17,sym_68,sh(18));
        addG(17,sym_67,sh(22));
        addG(17,sym_65,sh(20));
        addG(17,sym_64,sh(13));
        addG(17,sym_85,sh(23));
        addG(17,sym_86,sh(34));
        addG(17,sym_73,sh(17));
        addG(17,sym_63,sh(19));
        addG(17,sym_62,sh(15));
        addG(17,sym_61,sh(11));
        addL(17,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        Grammar ::= ProdLine (*) Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_18()
    {
        addA(18,sym_22,sh(12));
        addA(18,sym_17,sh(10));
        addA(18,sym_18,sh(21));
        addA(18,sym_12,sh(28));
        addA(18,sym_36,sh(9));
        addA(18,sym_11,sh(27));
        addA(18,sym_10,sh(14));
        addA(18,sym_9,sh(24));
        addA(18,sym_32,sh(29));
        addA(18,sym_7,fr(p_56));
        addA(18,sym_25,sh(16));
        addG(18,sym_60,sh(26));
        addG(18,sym_68,sh(18));
        addG(18,sym_67,sh(22));
        addG(18,sym_65,sh(20));
        addG(18,sym_64,sh(13));
        addG(18,sym_85,sh(23));
        addG(18,sym_86,sh(36));
        addG(18,sym_73,sh(17));
        addG(18,sym_63,sh(19));
        addG(18,sym_62,sh(15));
        addG(18,sym_61,sh(11));
        addL(18,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= DefaultTCodeLine (*) Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_19()
    {
        addA(19,sym_22,sh(12));
        addA(19,sym_17,sh(10));
        addA(19,sym_18,sh(21));
        addA(19,sym_12,sh(28));
        addA(19,sym_11,sh(27));
        addA(19,sym_36,sh(9));
        addA(19,sym_10,sh(14));
        addA(19,sym_9,sh(24));
        addA(19,sym_32,sh(29));
        addA(19,sym_7,fr(p_56));
        addA(19,sym_25,sh(16));
        addG(19,sym_60,sh(26));
        addG(19,sym_68,sh(18));
        addG(19,sym_67,sh(22));
        addG(19,sym_65,sh(20));
        addG(19,sym_64,sh(13));
        addG(19,sym_85,sh(23));
        addG(19,sym_86,sh(51));
        addG(19,sym_73,sh(17));
        addG(19,sym_63,sh(19));
        addG(19,sym_62,sh(15));
        addG(19,sym_61,sh(11));
        addL(19,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= TokLine (*) Grammar
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_20()
    {
        addA(20,sym_22,sh(12));
        addA(20,sym_17,sh(10));
        addA(20,sym_18,sh(21));
        addA(20,sym_12,sh(28));
        addA(20,sym_36,sh(9));
        addA(20,sym_11,sh(27));
        addA(20,sym_10,sh(14));
        addA(20,sym_9,sh(24));
        addA(20,sym_32,sh(29));
        addA(20,sym_7,fr(p_56));
        addA(20,sym_25,sh(16));
        addG(20,sym_60,sh(26));
        addG(20,sym_68,sh(18));
        addG(20,sym_67,sh(22));
        addG(20,sym_65,sh(20));
        addG(20,sym_64,sh(13));
        addG(20,sym_85,sh(23));
        addG(20,sym_86,sh(37));
        addG(20,sym_73,sh(17));
        addG(20,sym_63,sh(19));
        addG(20,sym_62,sh(15));
        addG(20,sym_61,sh(11));
        addL(20,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        DefaultTCodeLine ::= default_decl (*) t_decl code_decl embedded_code newline
        DefaultProdCodeLine ::= default_decl (*) prod_decl code_decl embedded_code newline

    */
    public static void init_21()
    {
        addA(21,sym_10,sh(42));
        addA(21,sym_22,sh(43));
        addL(21,sym_49,sym_22,sym_10);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= OpLine (*) Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_22()
    {
        addA(22,sym_22,sh(12));
        addA(22,sym_17,sh(10));
        addA(22,sym_18,sh(21));
        addA(22,sym_12,sh(28));
        addA(22,sym_36,sh(9));
        addA(22,sym_11,sh(27));
        addA(22,sym_10,sh(14));
        addA(22,sym_9,sh(24));
        addA(22,sym_32,sh(29));
        addA(22,sym_7,fr(p_56));
        addA(22,sym_25,sh(16));
        addG(22,sym_60,sh(26));
        addG(22,sym_68,sh(18));
        addG(22,sym_67,sh(22));
        addG(22,sym_65,sh(20));
        addG(22,sym_64,sh(13));
        addG(22,sym_85,sh(23));
        addG(22,sym_86,sh(38));
        addG(22,sym_73,sh(17));
        addG(22,sym_63,sh(19));
        addG(22,sym_62,sh(15));
        addG(22,sym_61,sh(11));
        addL(22,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        Grammar ::= NTLine (*) Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_23()
    {
        addA(23,sym_22,sh(12));
        addA(23,sym_17,sh(10));
        addA(23,sym_18,sh(21));
        addA(23,sym_12,sh(28));
        addA(23,sym_36,sh(9));
        addA(23,sym_11,sh(27));
        addA(23,sym_10,sh(14));
        addA(23,sym_9,sh(24));
        addA(23,sym_32,sh(29));
        addA(23,sym_7,fr(p_56));
        addA(23,sym_25,sh(16));
        addG(23,sym_60,sh(26));
        addG(23,sym_68,sh(18));
        addG(23,sym_67,sh(22));
        addG(23,sym_65,sh(20));
        addG(23,sym_64,sh(13));
        addG(23,sym_85,sh(23));
        addG(23,sym_86,sh(41));
        addG(23,sym_73,sh(17));
        addG(23,sym_63,sh(19));
        addG(23,sym_62,sh(15));
        addG(23,sym_61,sh(11));
        addL(23,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Grammar ::= comment_line (*) newline Grammar

    */
    public static void init_24()
    {
        addA(24,sym_56,sh(44));
        addL(24,sym_49,sym_56);
    }
    /*
        GrammarFile ::= grammar_decl grammar_name_decl grammarname_tok spectype_decl spectypes grammar_version newline Grammar (*)	[$]

    */
    public static void init_25()
    {
        addA(25,sym_7,fr(p_39));
        addL(25,sym_49,sym_7);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= StartLine (*) Grammar
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_26()
    {
        addA(26,sym_22,sh(12));
        addA(26,sym_17,sh(10));
        addA(26,sym_18,sh(21));
        addA(26,sym_12,sh(28));
        addA(26,sym_36,sh(9));
        addA(26,sym_11,sh(27));
        addA(26,sym_10,sh(14));
        addA(26,sym_9,sh(24));
        addA(26,sym_32,sh(29));
        addA(26,sym_7,fr(p_56));
        addA(26,sym_25,sh(16));
        addG(26,sym_60,sh(26));
        addG(26,sym_68,sh(18));
        addG(26,sym_67,sh(22));
        addG(26,sym_65,sh(20));
        addG(26,sym_64,sh(13));
        addG(26,sym_85,sh(23));
        addG(26,sym_86,sh(49));
        addG(26,sym_73,sh(17));
        addG(26,sym_63,sh(19));
        addG(26,sym_62,sh(15));
        addG(26,sym_61,sh(11));
        addL(26,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        TokLine ::= tok_decl (*) terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_27()
    {
        addA(27,sym_1,sh(48));
        addL(27,sym_49,sym_1);
    }
    /*
        NTSeq ::= (*)	[newline]
        NTSeq ::= (*) nonterm_tok NTSeq
        NTLine ::= nt_decl (*) NTSeq newline

    */
    public static void init_28()
    {
        addA(28,sym_56,fr(p_43));
        addA(28,sym_0,sh(45));
        addG(28,sym_84,sh(46));
        addL(28,sym_49,sym_0,sym_56);
    }
    /*
        GroupLine ::= ambiguous_decl (*) t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_29()
    {
        addA(29,sym_10,sh(50));
        addL(29,sym_49,sym_10);
    }
    /*
        Grammar ::= AttrLine Grammar (*)	[$]

    */
    public static void init_30()
    {
        addA(30,sym_7,fr(p_51));
        addL(30,sym_49,sym_7);
    }
    /*
        TLine ::= t_decl terminal_tok (*) Regex_Root
        Regex_Root ::= (*) colon newline
        Regex_Root ::= (*) colon Regex_R newline

    */
    public static void init_31()
    {
        addA(31,sym_38,sh(61));
        addG(31,sym_76,sh(60));
        addL(31,sym_49,sym_38);
    }
    /*
        Grammar ::= GroupLine Grammar (*)	[$]

    */
    public static void init_32()
    {
        addA(32,sym_7,fr(p_52));
        addL(32,sym_49,sym_7);
    }
    /*
        StartLine ::= start_decl nonterm_tok (*) layout_decl lbrace TSeq rbrace newline

    */
    public static void init_33()
    {
        addA(33,sym_24,sh(52));
        addL(33,sym_49,sym_24);
    }
    /*
        Grammar ::= DefaultProdCodeLine Grammar (*)	[$]

    */
    public static void init_34()
    {
        addA(34,sym_7,fr(p_54));
        addL(34,sym_49,sym_7);
    }
    /*
        OpLine ::= operator_decl terminal_tok (*) precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline

    */
    public static void init_35()
    {
        addA(35,sym_21,sh(62));
        addL(35,sym_49,sym_21);
    }
    /*
        Grammar ::= ProdLine Grammar (*)	[$]

    */
    public static void init_36()
    {
        addA(36,sym_7,fr(p_49));
        addL(36,sym_49,sym_7);
    }
    /*
        Grammar ::= TokLine Grammar (*)	[$]

    */
    public static void init_37()
    {
        addA(37,sym_7,fr(p_47));
        addL(37,sym_49,sym_7);
    }
    /*
        Grammar ::= OpLine Grammar (*)	[$]

    */
    public static void init_38()
    {
        addA(38,sym_7,fr(p_48));
        addL(38,sym_49,sym_7);
    }
    /*
        Grammar ::= TLine Grammar (*)	[$]

    */
    public static void init_39()
    {
        addA(39,sym_7,fr(p_46));
        addL(39,sym_49,sym_7);
    }
    /*
        AttrLine ::= attr_decl attrname_tok (*) attr_type_decl AttrTypeRoot code_decl embedded_code newline

    */
    public static void init_40()
    {
        addA(40,sym_28,sh(53));
        addL(40,sym_49,sym_28);
    }
    /*
        Grammar ::= NTLine Grammar (*)	[$]

    */
    public static void init_41()
    {
        addA(41,sym_7,fr(p_45));
        addL(41,sym_49,sym_7);
    }
    /*
        DefaultTCodeLine ::= default_decl t_decl (*) code_decl embedded_code newline

    */
    public static void init_42()
    {
        addA(42,sym_27,sh(55));
        addL(42,sym_49,sym_27);
    }
    /*
        DefaultProdCodeLine ::= default_decl prod_decl (*) code_decl embedded_code newline

    */
    public static void init_43()
    {
        addA(43,sym_27,sh(59));
        addL(43,sym_49,sym_27);
    }
    /*
        Grammar ::= (*)	[$]
        StartLine ::= (*) start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline
        OpLine ::= (*) operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline
        Grammar ::= (*) AttrLine Grammar
        ProdLine ::= (*) prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        Grammar ::= (*) TLine Grammar
        TLine ::= (*) t_decl terminal_tok Regex_Root
        Grammar ::= (*) GroupLine Grammar
        AttrLine ::= (*) attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline
        Grammar ::= (*) DefaultProdCodeLine Grammar
        Grammar ::= (*) ProdLine Grammar
        Grammar ::= (*) DefaultTCodeLine Grammar
        Grammar ::= (*) TokLine Grammar
        Grammar ::= comment_line newline (*) Grammar
        DefaultTCodeLine ::= (*) default_decl t_decl code_decl embedded_code newline
        Grammar ::= (*) OpLine Grammar
        Grammar ::= (*) NTLine Grammar
        Grammar ::= (*) comment_line newline Grammar
        DefaultProdCodeLine ::= (*) default_decl prod_decl code_decl embedded_code newline
        Grammar ::= (*) StartLine Grammar
        TokLine ::= (*) tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        NTLine ::= (*) nt_decl NTSeq newline
        GroupLine ::= (*) ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_44()
    {
        addA(44,sym_22,sh(12));
        addA(44,sym_17,sh(10));
        addA(44,sym_18,sh(21));
        addA(44,sym_12,sh(28));
        addA(44,sym_36,sh(9));
        addA(44,sym_11,sh(27));
        addA(44,sym_10,sh(14));
        addA(44,sym_9,sh(24));
        addA(44,sym_32,sh(29));
        addA(44,sym_7,fr(p_56));
        addA(44,sym_25,sh(16));
        addG(44,sym_60,sh(26));
        addG(44,sym_68,sh(18));
        addG(44,sym_67,sh(22));
        addG(44,sym_65,sh(20));
        addG(44,sym_64,sh(13));
        addG(44,sym_85,sh(23));
        addG(44,sym_86,sh(54));
        addG(44,sym_73,sh(17));
        addG(44,sym_63,sh(19));
        addG(44,sym_62,sh(15));
        addG(44,sym_61,sh(11));
        addL(44,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        NTSeq ::= (*)	[newline]
        NTSeq ::= (*) nonterm_tok NTSeq
        NTSeq ::= nonterm_tok (*) NTSeq

    */
    public static void init_45()
    {
        addA(45,sym_56,fr(p_43));
        addA(45,sym_0,sh(45));
        addG(45,sym_84,sh(58));
        addL(45,sym_49,sym_0,sym_56);
    }
    /*
        NTLine ::= nt_decl NTSeq (*) newline

    */
    public static void init_46()
    {
        addA(46,sym_56,sh(57));
        addL(46,sym_49,sym_56);
    }
    /*
        ProdLine ::= prod_decl prodname_tok (*) precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_47()
    {
        addA(47,sym_21,sh(56));
        addL(47,sym_49,sym_21);
    }
    /*
        TokLine ::= tok_decl terminal_tok (*) precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_48()
    {
        addA(48,sym_21,sh(64));
        addL(48,sym_49,sym_21);
    }
    /*
        Grammar ::= StartLine Grammar (*)	[$]

    */
    public static void init_49()
    {
        addA(49,sym_7,fr(p_50));
        addL(49,sym_49,sym_7);
    }
    /*
        GroupLine ::= ambiguous_decl t_decl (*) group_decl groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_50()
    {
        addA(50,sym_31,sh(63));
        addL(50,sym_49,sym_31);
    }
    /*
        Grammar ::= DefaultTCodeLine Grammar (*)	[$]

    */
    public static void init_51()
    {
        addA(51,sym_7,fr(p_53));
        addL(51,sym_49,sym_7);
    }
    /*
        StartLine ::= start_decl nonterm_tok layout_decl (*) lbrace TSeq rbrace newline

    */
    public static void init_52()
    {
        addA(52,sym_48,sh(65));
        addL(52,sym_49,sym_48);
    }
    /*
        AttrTypeRoot ::= (*) lbrack AttrTypeRoot rbrack
        AttrLine ::= attr_decl attrname_tok attr_type_decl (*) AttrTypeRoot code_decl embedded_code newline
        AttrTypeRoot ::= (*) attr_type_base

    */
    public static void init_53()
    {
        addA(53,sym_44,sh(76));
        addA(53,sym_30,sh(78));
        addG(53,sym_69,sh(77));
        addL(53,sym_49,sym_44,sym_30);
    }
    /*
        Grammar ::= comment_line newline Grammar (*)	[$]

    */
    public static void init_54()
    {
        addA(54,sym_7,fr(p_55));
        addL(54,sym_49,sym_7);
    }
    /*
        DefaultTCodeLine ::= default_decl t_decl code_decl (*) embedded_code newline

    */
    public static void init_55()
    {
        addA(55,sym_29,sh(84));
        addL(55,sym_49,sym_29);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl (*) precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_56()
    {
        addA(56,sym_4,sh(79));
        addL(56,sym_49,sym_4);
    }
    /*
        NTLine ::= nt_decl NTSeq newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_57()
    {
        addA(57,sym_22,fr(p_44));
        addA(57,sym_17,fr(p_44));
        addA(57,sym_18,fr(p_44));
        addA(57,sym_12,fr(p_44));
        addA(57,sym_11,fr(p_44));
        addA(57,sym_36,fr(p_44));
        addA(57,sym_10,fr(p_44));
        addA(57,sym_9,fr(p_44));
        addA(57,sym_32,fr(p_44));
        addA(57,sym_7,fr(p_44));
        addA(57,sym_25,fr(p_44));
        addL(57,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        NTSeq ::= nonterm_tok NTSeq (*)	[newline]

    */
    public static void init_58()
    {
        addA(58,sym_56,fr(p_42));
        addL(58,sym_49,sym_56);
    }
    /*
        DefaultProdCodeLine ::= default_decl prod_decl code_decl (*) embedded_code newline

    */
    public static void init_59()
    {
        addA(59,sym_29,sh(80));
        addL(59,sym_49,sym_29);
    }
    /*
        TLine ::= t_decl terminal_tok Regex_Root (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_60()
    {
        addA(60,sym_22,fr(p_4));
        addA(60,sym_17,fr(p_4));
        addA(60,sym_18,fr(p_4));
        addA(60,sym_12,fr(p_4));
        addA(60,sym_11,fr(p_4));
        addA(60,sym_36,fr(p_4));
        addA(60,sym_10,fr(p_4));
        addA(60,sym_9,fr(p_4));
        addA(60,sym_32,fr(p_4));
        addA(60,sym_7,fr(p_4));
        addA(60,sym_25,fr(p_4));
        addL(60,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_CHAR ::= (*) character
        Regex_UR ::= (*) wildcard
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_UR ::= (*) Regex_CHAR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_R ::= (*) Regex_DR
        Regex_Root ::= colon (*) newline
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_R ::= (*) Regex_DR bar Regex_R
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_Root ::= colon (*) Regex_R newline
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_DR ::= (*) Regex_UR question Regex_RR

    */
    public static void init_61()
    {
        addA(61,sym_42,sh(66));
        addA(61,sym_56,sh(74));
        addA(61,sym_44,sh(68));
        addA(61,sym_50,sh(67));
        addA(61,sym_52,sh(69));
        addA(61,sym_46,sh(70));
        addG(61,sym_77,sh(72));
        addG(61,sym_72,sh(71));
        addG(61,sym_71,sh(73));
        addG(61,sym_70,sh(75));
        addL(61,eps(),sym_50,sym_52,sym_56,sym_42,sym_44,sym_46);
    }
    /*
        OpLine ::= operator_decl terminal_tok precclass_decl (*) precclass_tok prec_decl prec_number assoc_decl assoctypes newline

    */
    public static void init_62()
    {
        addA(62,sym_4,sh(81));
        addL(62,sym_49,sym_4);
    }
    /*
        GroupLine ::= ambiguous_decl t_decl group_decl (*) groupname_tok code_decl embedded_code members_decl TSeq newline

    */
    public static void init_63()
    {
        addA(63,sym_6,sh(82));
        addL(63,sym_49,sym_6);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl (*) lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_64()
    {
        addA(64,sym_48,sh(83));
        addL(64,sym_49,sym_48);
    }
    /*
        TSeq ::= (*) terminal_tok TSeq
        StartLine ::= start_decl nonterm_tok layout_decl lbrace (*) TSeq rbrace newline
        TSeq ::= (*)	[rbrace]

    */
    public static void init_65()
    {
        addA(65,sym_45,fr(p_40));
        addA(65,sym_1,sh(106));
        addG(65,sym_83,sh(107));
        addL(65,sym_49,sym_1,sym_45);
    }
    /*
        Regex_UR ::= lparen (*) Regex_R rparen
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_CHAR ::= (*) character
        Regex_UR ::= (*) wildcard
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_UR ::= (*) Regex_CHAR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_R ::= (*) Regex_DR
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_R ::= (*) Regex_DR bar Regex_R
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_DR ::= (*) Regex_UR question Regex_RR

    */
    public static void init_66()
    {
        addA(66,sym_42,sh(66));
        addA(66,sym_44,sh(68));
        addA(66,sym_50,sh(67));
        addA(66,sym_52,sh(69));
        addA(66,sym_46,sh(70));
        addG(66,sym_77,sh(72));
        addG(66,sym_72,sh(71));
        addG(66,sym_71,sh(73));
        addG(66,sym_70,sh(85));
        addL(66,eps(),sym_50,sym_52,sym_42,sym_44,sym_46);
    }
    /*
        Regex_CHAR ::= escaped (*)	[plus, star, dash, question, bar, rbrack, lparen, lbrack, wildcard, rparen, escaped, character, newline]

    */
    public static void init_67()
    {
        addA(67,sym_47,fr(p_30));
        addA(67,sym_42,fr(p_30));
        addA(67,sym_41,fr(p_30));
        addA(67,sym_44,fr(p_30));
        addA(67,sym_37,fr(p_30));
        addA(67,sym_40,fr(p_30));
        addA(67,sym_39,fr(p_30));
        addA(67,sym_34,fr(p_30));
        addA(67,sym_33,fr(p_30));
        addA(67,sym_56,fr(p_30));
        addA(67,sym_50,fr(p_30));
        addA(67,sym_52,fr(p_30));
        addA(67,sym_46,fr(p_30));
        addL(67,eps(),sym_33,sym_34,sym_37,sym_39,sym_40,sym_41,sym_42,sym_44,sym_46,sym_47,sym_50,sym_52,sym_56);
    }
    /*
        Regex_UG ::= (*) Regex_CHAR dash Regex_CHAR
        Regex_G ::= (*) Regex_UG Regex_RG
        Regex_UR ::= lbrack (*) Regex_G rbrack
        Regex_CHAR ::= (*) escaped
        Regex_CHAR ::= (*) character
        Regex_UR ::= lbrack (*) not Regex_G rbrack
        Regex_UG ::= (*) Regex_CHAR
        Regex_UR ::= lbrack (*) colon termname colon rbrack

    */
    public static void init_68()
    {
        addA(68,sym_43,sh(103));
        addA(68,sym_50,sh(67));
        addA(68,sym_38,sh(104));
        addA(68,sym_52,sh(69));
        addG(68,sym_77,sh(100));
        addG(68,sym_80,sh(101));
        addG(68,sym_78,sh(102));
        addL(68,eps(),sym_50,sym_52,sym_38,sym_43);
    }
    /*
        Regex_CHAR ::= character (*)	[plus, star, dash, question, bar, rbrack, lparen, lbrack, wildcard, rparen, escaped, character, newline]

    */
    public static void init_69()
    {
        addA(69,sym_47,fr(p_31));
        addA(69,sym_42,fr(p_31));
        addA(69,sym_41,fr(p_31));
        addA(69,sym_44,fr(p_31));
        addA(69,sym_37,fr(p_31));
        addA(69,sym_40,fr(p_31));
        addA(69,sym_39,fr(p_31));
        addA(69,sym_34,fr(p_31));
        addA(69,sym_33,fr(p_31));
        addA(69,sym_56,fr(p_31));
        addA(69,sym_50,fr(p_31));
        addA(69,sym_52,fr(p_31));
        addA(69,sym_46,fr(p_31));
        addL(69,eps(),sym_33,sym_34,sym_37,sym_39,sym_40,sym_41,sym_42,sym_44,sym_46,sym_47,sym_50,sym_52,sym_56);
    }
    /*
        Regex_UR ::= wildcard (*)	[plus, star, escaped, character, question, bar, newline, lparen, lbrack, wildcard, rparen]

    */
    public static void init_70()
    {
        addA(70,sym_47,fr(p_21));
        addA(70,sym_42,fr(p_21));
        addA(70,sym_44,fr(p_21));
        addA(70,sym_40,fr(p_21));
        addA(70,sym_39,fr(p_21));
        addA(70,sym_34,fr(p_21));
        addA(70,sym_33,fr(p_21));
        addA(70,sym_56,fr(p_21));
        addA(70,sym_50,fr(p_21));
        addA(70,sym_52,fr(p_21));
        addA(70,sym_46,fr(p_21));
        addL(70,eps(),sym_33,sym_50,sym_34,sym_52,sym_39,sym_56,sym_40,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_RR ::= (*)	[newline, bar, rparen]
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_CHAR ::= (*) character
        Regex_UR ::= (*) wildcard
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_UR ::= (*) Regex_CHAR
        Regex_DR ::= Regex_UR (*) plus Regex_RR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_DR ::= Regex_UR (*) star Regex_RR
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_RR ::= (*) Regex_DR
        Regex_DR ::= Regex_UR (*) Regex_RR
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_DR ::= Regex_UR (*) question Regex_RR

    */
    public static void init_71()
    {
        addA(71,sym_47,fr(p_33));
        addA(71,sym_42,sh(66));
        addA(71,sym_44,sh(68));
        addA(71,sym_40,fr(p_33));
        addA(71,sym_39,sh(97));
        addA(71,sym_34,sh(94));
        addA(71,sym_33,sh(93));
        addA(71,sym_56,fr(p_33));
        addA(71,sym_50,sh(67));
        addA(71,sym_52,sh(69));
        addA(71,sym_46,sh(70));
        addG(71,sym_77,sh(72));
        addG(71,sym_79,sh(96));
        addG(71,sym_72,sh(71));
        addG(71,sym_71,sh(95));
        addL(71,eps(),sym_33,sym_34,sym_50,sym_52,sym_39,sym_40,sym_56,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        Regex_UR ::= Regex_CHAR (*)	[plus, star, escaped, character, question, bar, newline, lparen, lbrack, wildcard, rparen]

    */
    public static void init_72()
    {
        addA(72,sym_47,fr(p_18));
        addA(72,sym_42,fr(p_18));
        addA(72,sym_44,fr(p_18));
        addA(72,sym_40,fr(p_18));
        addA(72,sym_39,fr(p_18));
        addA(72,sym_34,fr(p_18));
        addA(72,sym_33,fr(p_18));
        addA(72,sym_56,fr(p_18));
        addA(72,sym_50,fr(p_18));
        addA(72,sym_52,fr(p_18));
        addA(72,sym_46,fr(p_18));
        addL(72,eps(),sym_33,sym_50,sym_34,sym_52,sym_39,sym_56,sym_40,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        Regex_R ::= Regex_DR (*)	[newline, rparen]
        Regex_R ::= Regex_DR (*) bar Regex_R

    */
    public static void init_73()
    {
        addA(73,sym_56,fr(p_11));
        addA(73,sym_40,sh(86));
        addA(73,sym_47,fr(p_11));
        addL(73,eps(),sym_40,sym_56,sym_47);
    }
    /*
        Regex_Root ::= colon newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_74()
    {
        addA(74,sym_22,fr(p_28));
        addA(74,sym_17,fr(p_28));
        addA(74,sym_18,fr(p_28));
        addA(74,sym_12,fr(p_28));
        addA(74,sym_11,fr(p_28));
        addA(74,sym_36,fr(p_28));
        addA(74,sym_10,fr(p_28));
        addA(74,sym_9,fr(p_28));
        addA(74,sym_32,fr(p_28));
        addA(74,sym_7,fr(p_28));
        addA(74,sym_25,fr(p_28));
        addL(74,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Regex_Root ::= colon Regex_R (*) newline

    */
    public static void init_75()
    {
        addA(75,sym_56,sh(98));
        addL(75,eps(),sym_56);
    }
    /*
        AttrTypeRoot ::= (*) lbrack AttrTypeRoot rbrack
        AttrTypeRoot ::= lbrack (*) AttrTypeRoot rbrack
        AttrTypeRoot ::= (*) attr_type_base

    */
    public static void init_76()
    {
        addA(76,sym_44,sh(76));
        addA(76,sym_30,sh(78));
        addG(76,sym_69,sh(105));
        addL(76,sym_49,sym_44,sym_30);
    }
    /*
        AttrLine ::= attr_decl attrname_tok attr_type_decl AttrTypeRoot (*) code_decl embedded_code newline

    */
    public static void init_77()
    {
        addA(77,sym_27,sh(87));
        addL(77,sym_49,sym_27);
    }
    /*
        AttrTypeRoot ::= attr_type_base (*)	[rbrack, code_decl]

    */
    public static void init_78()
    {
        addA(78,sym_41,fr(p_9));
        addA(78,sym_27,fr(p_9));
        addL(78,sym_49,sym_41,sym_27);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok (*) prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_79()
    {
        addA(79,sym_13,sh(91));
        addL(79,sym_49,sym_13);
    }
    /*
        DefaultProdCodeLine ::= default_decl prod_decl code_decl embedded_code (*) newline

    */
    public static void init_80()
    {
        addA(80,sym_56,sh(99));
        addL(80,sym_49,sym_56);
    }
    /*
        OpLine ::= operator_decl terminal_tok precclass_decl precclass_tok (*) prec_decl prec_number assoc_decl assoctypes newline

    */
    public static void init_81()
    {
        addA(81,sym_13,sh(92));
        addL(81,sym_49,sym_13);
    }
    /*
        GroupLine ::= ambiguous_decl t_decl group_decl groupname_tok (*) code_decl embedded_code members_decl TSeq newline

    */
    public static void init_82()
    {
        addA(82,sym_27,sh(90));
        addL(82,sym_49,sym_27);
    }
    /*
        PrecClassSeq ::= (*)	[rbrace]
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace (*) PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        PrecClassSeq ::= (*) precclass_tok PrecClassSeq

    */
    public static void init_83()
    {
        addA(83,sym_4,sh(89));
        addA(83,sym_45,fr(p_24));
        addG(83,sym_74,sh(88));
        addL(83,sym_49,sym_4,sym_45);
    }
    /*
        DefaultTCodeLine ::= default_decl t_decl code_decl embedded_code (*) newline

    */
    public static void init_84()
    {
        addA(84,sym_56,sh(108));
        addL(84,sym_49,sym_56);
    }
    /*
        Regex_UR ::= lparen Regex_R (*) rparen

    */
    public static void init_85()
    {
        addA(85,sym_47,sh(109));
        addL(85,eps(),sym_47);
    }
    /*
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_CHAR ::= (*) character
        Regex_UR ::= (*) wildcard
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_UR ::= (*) Regex_CHAR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_R ::= (*) Regex_DR
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_R ::= (*) Regex_DR bar Regex_R
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_R ::= Regex_DR bar (*) Regex_R

    */
    public static void init_86()
    {
        addA(86,sym_42,sh(66));
        addA(86,sym_44,sh(68));
        addA(86,sym_50,sh(67));
        addA(86,sym_52,sh(69));
        addA(86,sym_46,sh(70));
        addG(86,sym_77,sh(72));
        addG(86,sym_72,sh(71));
        addG(86,sym_71,sh(73));
        addG(86,sym_70,sh(110));
        addL(86,eps(),sym_50,sym_52,sym_42,sym_44,sym_46);
    }
    /*
        AttrLine ::= attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl (*) embedded_code newline

    */
    public static void init_87()
    {
        addA(87,sym_29,sh(113));
        addL(87,sym_49,sym_29);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq (*) rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_88()
    {
        addA(88,sym_45,sh(123));
        addL(88,sym_49,sym_45);
    }
    /*
        PrecClassSeq ::= (*)	[rbrace]
        PrecClassSeq ::= precclass_tok (*) PrecClassSeq
        PrecClassSeq ::= (*) precclass_tok PrecClassSeq

    */
    public static void init_89()
    {
        addA(89,sym_4,sh(89));
        addA(89,sym_45,fr(p_24));
        addG(89,sym_74,sh(126));
        addL(89,sym_49,sym_4,sym_45);
    }
    /*
        GroupLine ::= ambiguous_decl t_decl group_decl groupname_tok code_decl (*) embedded_code members_decl TSeq newline

    */
    public static void init_90()
    {
        addA(90,sym_29,sh(118));
        addL(90,sym_49,sym_29);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl (*) prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_91()
    {
        addA(91,sym_8,sh(120));
        addL(91,sym_49,sym_8);
    }
    /*
        OpLine ::= operator_decl terminal_tok precclass_decl precclass_tok prec_decl (*) prec_number assoc_decl assoctypes newline

    */
    public static void init_92()
    {
        addA(92,sym_8,sh(124));
        addL(92,sym_49,sym_8);
    }
    /*
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_RR ::= (*)	[bar, newline, rparen]
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_CHAR ::= (*) character
        Regex_UR ::= (*) wildcard
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_UR ::= (*) Regex_CHAR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_RR ::= (*) Regex_DR
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_DR ::= Regex_UR plus (*) Regex_RR
        Regex_DR ::= (*) Regex_UR question Regex_RR

    */
    public static void init_93()
    {
        addA(93,sym_42,sh(66));
        addA(93,sym_56,fr(p_33));
        addA(93,sym_44,sh(68));
        addA(93,sym_50,sh(67));
        addA(93,sym_40,fr(p_33));
        addA(93,sym_52,sh(69));
        addA(93,sym_46,sh(70));
        addA(93,sym_47,fr(p_33));
        addG(93,sym_77,sh(72));
        addG(93,sym_79,sh(117));
        addG(93,sym_72,sh(71));
        addG(93,sym_71,sh(95));
        addL(93,eps(),sym_50,sym_52,sym_56,sym_40,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_RR ::= (*)	[bar, newline, rparen]
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_CHAR ::= (*) character
        Regex_UR ::= (*) wildcard
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_UR ::= (*) Regex_CHAR
        Regex_DR ::= Regex_UR star (*) Regex_RR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_RR ::= (*) Regex_DR
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_DR ::= (*) Regex_UR question Regex_RR

    */
    public static void init_94()
    {
        addA(94,sym_42,sh(66));
        addA(94,sym_56,fr(p_33));
        addA(94,sym_44,sh(68));
        addA(94,sym_50,sh(67));
        addA(94,sym_40,fr(p_33));
        addA(94,sym_52,sh(69));
        addA(94,sym_46,sh(70));
        addA(94,sym_47,fr(p_33));
        addG(94,sym_77,sh(72));
        addG(94,sym_79,sh(125));
        addG(94,sym_72,sh(71));
        addG(94,sym_71,sh(95));
        addL(94,eps(),sym_50,sym_52,sym_56,sym_40,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        Regex_RR ::= Regex_DR (*)	[newline, bar, rparen]

    */
    public static void init_95()
    {
        addA(95,sym_56,fr(p_34));
        addA(95,sym_40,fr(p_34));
        addA(95,sym_47,fr(p_34));
        addL(95,eps(),sym_40,sym_56,sym_47);
    }
    /*
        Regex_DR ::= Regex_UR Regex_RR (*)	[bar, newline, rparen]

    */
    public static void init_96()
    {
        addA(96,sym_56,fr(p_15));
        addA(96,sym_40,fr(p_15));
        addA(96,sym_47,fr(p_15));
        addL(96,eps(),sym_56,sym_40,sym_47);
    }
    /*
        Regex_UR ::= (*) lparen Regex_R rparen
        Regex_RR ::= (*)	[bar, newline, rparen]
        Regex_CHAR ::= (*) escaped
        Regex_UR ::= (*) lbrack not Regex_G rbrack
        Regex_CHAR ::= (*) character
        Regex_UR ::= (*) wildcard
        Regex_DR ::= (*) Regex_UR plus Regex_RR
        Regex_UR ::= (*) Regex_CHAR
        Regex_UR ::= (*) lbrack Regex_G rbrack
        Regex_DR ::= (*) Regex_UR star Regex_RR
        Regex_RR ::= (*) Regex_DR
        Regex_UR ::= (*) lbrack colon termname colon rbrack
        Regex_DR ::= (*) Regex_UR Regex_RR
        Regex_DR ::= (*) Regex_UR question Regex_RR
        Regex_DR ::= Regex_UR question (*) Regex_RR

    */
    public static void init_97()
    {
        addA(97,sym_42,sh(66));
        addA(97,sym_56,fr(p_33));
        addA(97,sym_44,sh(68));
        addA(97,sym_50,sh(67));
        addA(97,sym_40,fr(p_33));
        addA(97,sym_52,sh(69));
        addA(97,sym_46,sh(70));
        addA(97,sym_47,fr(p_33));
        addG(97,sym_77,sh(72));
        addG(97,sym_79,sh(128));
        addG(97,sym_72,sh(71));
        addG(97,sym_71,sh(95));
        addL(97,eps(),sym_50,sym_52,sym_56,sym_40,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        Regex_Root ::= colon Regex_R newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, comment_line, attr_decl, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_98()
    {
        addA(98,sym_22,fr(p_29));
        addA(98,sym_17,fr(p_29));
        addA(98,sym_18,fr(p_29));
        addA(98,sym_12,fr(p_29));
        addA(98,sym_11,fr(p_29));
        addA(98,sym_36,fr(p_29));
        addA(98,sym_10,fr(p_29));
        addA(98,sym_9,fr(p_29));
        addA(98,sym_32,fr(p_29));
        addA(98,sym_7,fr(p_29));
        addA(98,sym_25,fr(p_29));
        addL(98,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_25,sym_9,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        DefaultProdCodeLine ::= default_decl prod_decl code_decl embedded_code newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_99()
    {
        addA(99,sym_22,fr(p_23));
        addA(99,sym_17,fr(p_23));
        addA(99,sym_18,fr(p_23));
        addA(99,sym_12,fr(p_23));
        addA(99,sym_11,fr(p_23));
        addA(99,sym_36,fr(p_23));
        addA(99,sym_10,fr(p_23));
        addA(99,sym_9,fr(p_23));
        addA(99,sym_32,fr(p_23));
        addA(99,sym_7,fr(p_23));
        addA(99,sym_25,fr(p_23));
        addL(99,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Regex_UG ::= Regex_CHAR (*) dash Regex_CHAR
        Regex_UG ::= Regex_CHAR (*)	[escaped, character, rbrack]

    */
    public static void init_100()
    {
        addA(100,sym_41,fr(p_36));
        addA(100,sym_50,fr(p_36));
        addA(100,sym_37,sh(114));
        addA(100,sym_52,fr(p_36));
        addL(100,eps(),sym_50,sym_52,sym_37,sym_41);
    }
    /*
        Regex_RG ::= (*) Regex_G
        Regex_G ::= Regex_UG (*) Regex_RG
        Regex_UG ::= (*) Regex_CHAR dash Regex_CHAR
        Regex_G ::= (*) Regex_UG Regex_RG
        Regex_CHAR ::= (*) escaped
        Regex_CHAR ::= (*) character
        Regex_UG ::= (*) Regex_CHAR
        Regex_RG ::= (*)	[rbrack]

    */
    public static void init_101()
    {
        addA(101,sym_41,fr(p_37));
        addA(101,sym_50,sh(67));
        addA(101,sym_52,sh(69));
        addG(101,sym_77,sh(100));
        addG(101,sym_80,sh(101));
        addG(101,sym_81,sh(116));
        addG(101,sym_78,sh(115));
        addL(101,eps(),sym_50,sym_52,sym_41);
    }
    /*
        Regex_UR ::= lbrack Regex_G (*) rbrack

    */
    public static void init_102()
    {
        addA(102,sym_41,sh(122));
        addL(102,eps(),sym_41);
    }
    /*
        Regex_UG ::= (*) Regex_CHAR dash Regex_CHAR
        Regex_G ::= (*) Regex_UG Regex_RG
        Regex_CHAR ::= (*) escaped
        Regex_CHAR ::= (*) character
        Regex_UG ::= (*) Regex_CHAR
        Regex_UR ::= lbrack not (*) Regex_G rbrack

    */
    public static void init_103()
    {
        addA(103,sym_50,sh(67));
        addA(103,sym_52,sh(69));
        addG(103,sym_77,sh(100));
        addG(103,sym_80,sh(101));
        addG(103,sym_78,sh(121));
        addL(103,eps(),sym_50,sym_52);
    }
    /*
        Regex_UR ::= lbrack colon (*) termname colon rbrack

    */
    public static void init_104()
    {
        addA(104,sym_51,sh(127));
        addL(104,eps(),sym_51);
    }
    /*
        AttrTypeRoot ::= lbrack AttrTypeRoot (*) rbrack

    */
    public static void init_105()
    {
        addA(105,sym_41,sh(111));
        addL(105,sym_49,sym_41);
    }
    /*
        TSeq ::= terminal_tok (*) TSeq
        TSeq ::= (*) terminal_tok TSeq
        TSeq ::= (*)	[newline, rbrace]

    */
    public static void init_106()
    {
        addA(106,sym_56,fr(p_40));
        addA(106,sym_45,fr(p_40));
        addA(106,sym_1,sh(106));
        addG(106,sym_83,sh(119));
        addL(106,sym_49,sym_1,sym_56,sym_45);
    }
    /*
        StartLine ::= start_decl nonterm_tok layout_decl lbrace TSeq (*) rbrace newline

    */
    public static void init_107()
    {
        addA(107,sym_45,sh(112));
        addL(107,sym_49,sym_45);
    }
    /*
        DefaultTCodeLine ::= default_decl t_decl code_decl embedded_code newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_108()
    {
        addA(108,sym_22,fr(p_3));
        addA(108,sym_17,fr(p_3));
        addA(108,sym_18,fr(p_3));
        addA(108,sym_12,fr(p_3));
        addA(108,sym_11,fr(p_3));
        addA(108,sym_36,fr(p_3));
        addA(108,sym_10,fr(p_3));
        addA(108,sym_9,fr(p_3));
        addA(108,sym_32,fr(p_3));
        addA(108,sym_7,fr(p_3));
        addA(108,sym_25,fr(p_3));
        addL(108,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Regex_UR ::= lparen Regex_R rparen (*)	[plus, star, escaped, character, question, bar, newline, lparen, lbrack, wildcard, rparen]

    */
    public static void init_109()
    {
        addA(109,sym_47,fr(p_17));
        addA(109,sym_42,fr(p_17));
        addA(109,sym_44,fr(p_17));
        addA(109,sym_40,fr(p_17));
        addA(109,sym_39,fr(p_17));
        addA(109,sym_34,fr(p_17));
        addA(109,sym_33,fr(p_17));
        addA(109,sym_56,fr(p_17));
        addA(109,sym_50,fr(p_17));
        addA(109,sym_52,fr(p_17));
        addA(109,sym_46,fr(p_17));
        addL(109,eps(),sym_33,sym_50,sym_34,sym_52,sym_39,sym_56,sym_40,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        Regex_R ::= Regex_DR bar Regex_R (*)	[newline, rparen]

    */
    public static void init_110()
    {
        addA(110,sym_56,fr(p_12));
        addA(110,sym_47,fr(p_12));
        addL(110,eps(),sym_56,sym_47);
    }
    /*
        AttrTypeRoot ::= lbrack AttrTypeRoot rbrack (*)	[rbrack, code_decl]

    */
    public static void init_111()
    {
        addA(111,sym_41,fr(p_10));
        addA(111,sym_27,fr(p_10));
        addL(111,sym_49,sym_41,sym_27);
    }
    /*
        StartLine ::= start_decl nonterm_tok layout_decl lbrace TSeq rbrace (*) newline

    */
    public static void init_112()
    {
        addA(112,sym_56,sh(129));
        addL(112,sym_49,sym_56);
    }
    /*
        AttrLine ::= attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code (*) newline

    */
    public static void init_113()
    {
        addA(113,sym_56,sh(130));
        addL(113,sym_49,sym_56);
    }
    /*
        Regex_UG ::= Regex_CHAR dash (*) Regex_CHAR
        Regex_CHAR ::= (*) escaped
        Regex_CHAR ::= (*) character

    */
    public static void init_114()
    {
        addA(114,sym_50,sh(67));
        addA(114,sym_52,sh(69));
        addG(114,sym_77,sh(136));
        addL(114,eps(),sym_50,sym_52);
    }
    /*
        Regex_RG ::= Regex_G (*)	[rbrack]

    */
    public static void init_115()
    {
        addA(115,sym_41,fr(p_38));
        addL(115,eps(),sym_41);
    }
    /*
        Regex_G ::= Regex_UG Regex_RG (*)	[rbrack]

    */
    public static void init_116()
    {
        addA(116,sym_41,fr(p_32));
        addL(116,eps(),sym_41);
    }
    /*
        Regex_DR ::= Regex_UR plus Regex_RR (*)	[newline, bar, rparen]

    */
    public static void init_117()
    {
        addA(117,sym_56,fr(p_14));
        addA(117,sym_40,fr(p_14));
        addA(117,sym_47,fr(p_14));
        addL(117,eps(),sym_40,sym_56,sym_47);
    }
    /*
        GroupLine ::= ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code (*) members_decl TSeq newline

    */
    public static void init_118()
    {
        addA(118,sym_35,sh(132));
        addL(118,sym_49,sym_35);
    }
    /*
        TSeq ::= terminal_tok TSeq (*)	[newline, rbrace]

    */
    public static void init_119()
    {
        addA(119,sym_56,fr(p_41));
        addA(119,sym_45,fr(p_41));
        addL(119,sym_49,sym_56,sym_45);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number (*) operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_120()
    {
        addA(120,sym_17,sh(133));
        addL(120,sym_49,sym_17);
    }
    /*
        Regex_UR ::= lbrack not Regex_G (*) rbrack

    */
    public static void init_121()
    {
        addA(121,sym_41,sh(131));
        addL(121,eps(),sym_41);
    }
    /*
        Regex_UR ::= lbrack Regex_G rbrack (*)	[plus, star, escaped, character, question, bar, newline, lparen, lbrack, wildcard, rparen]

    */
    public static void init_122()
    {
        addA(122,sym_47,fr(p_22));
        addA(122,sym_42,fr(p_22));
        addA(122,sym_44,fr(p_22));
        addA(122,sym_40,fr(p_22));
        addA(122,sym_39,fr(p_22));
        addA(122,sym_34,fr(p_22));
        addA(122,sym_33,fr(p_22));
        addA(122,sym_56,fr(p_22));
        addA(122,sym_50,fr(p_22));
        addA(122,sym_52,fr(p_22));
        addA(122,sym_46,fr(p_22));
        addL(122,eps(),sym_33,sym_50,sym_34,sym_52,sym_39,sym_56,sym_40,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace (*) prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_123()
    {
        addA(123,sym_13,sh(134));
        addL(123,sym_49,sym_13);
    }
    /*
        OpLine ::= operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number (*) assoc_decl assoctypes newline

    */
    public static void init_124()
    {
        addA(124,sym_20,sh(137));
        addL(124,sym_49,sym_20);
    }
    /*
        Regex_DR ::= Regex_UR star Regex_RR (*)	[newline, bar, rparen]

    */
    public static void init_125()
    {
        addA(125,sym_56,fr(p_13));
        addA(125,sym_40,fr(p_13));
        addA(125,sym_47,fr(p_13));
        addL(125,eps(),sym_40,sym_56,sym_47);
    }
    /*
        PrecClassSeq ::= precclass_tok PrecClassSeq (*)	[rbrace]

    */
    public static void init_126()
    {
        addA(126,sym_45,fr(p_25));
        addL(126,sym_49,sym_45);
    }
    /*
        Regex_UR ::= lbrack colon termname (*) colon rbrack

    */
    public static void init_127()
    {
        addA(127,sym_38,sh(135));
        addL(127,eps(),sym_38);
    }
    /*
        Regex_DR ::= Regex_UR question Regex_RR (*)	[newline, bar, rparen]

    */
    public static void init_128()
    {
        addA(128,sym_56,fr(p_16));
        addA(128,sym_40,fr(p_16));
        addA(128,sym_47,fr(p_16));
        addL(128,eps(),sym_40,sym_56,sym_47);
    }
    /*
        StartLine ::= start_decl nonterm_tok layout_decl lbrace TSeq rbrace newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_129()
    {
        addA(129,sym_22,fr(p_0));
        addA(129,sym_17,fr(p_0));
        addA(129,sym_18,fr(p_0));
        addA(129,sym_12,fr(p_0));
        addA(129,sym_11,fr(p_0));
        addA(129,sym_36,fr(p_0));
        addA(129,sym_10,fr(p_0));
        addA(129,sym_9,fr(p_0));
        addA(129,sym_32,fr(p_0));
        addA(129,sym_7,fr(p_0));
        addA(129,sym_25,fr(p_0));
        addL(129,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        AttrLine ::= attr_decl attrname_tok attr_type_decl AttrTypeRoot code_decl embedded_code newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_130()
    {
        addA(130,sym_22,fr(p_1));
        addA(130,sym_17,fr(p_1));
        addA(130,sym_18,fr(p_1));
        addA(130,sym_12,fr(p_1));
        addA(130,sym_11,fr(p_1));
        addA(130,sym_36,fr(p_1));
        addA(130,sym_10,fr(p_1));
        addA(130,sym_9,fr(p_1));
        addA(130,sym_32,fr(p_1));
        addA(130,sym_7,fr(p_1));
        addA(130,sym_25,fr(p_1));
        addL(130,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        Regex_UR ::= lbrack not Regex_G rbrack (*)	[plus, escaped, star, character, question, newline, bar, lparen, lbrack, wildcard, rparen]

    */
    public static void init_131()
    {
        addA(131,sym_47,fr(p_19));
        addA(131,sym_42,fr(p_19));
        addA(131,sym_44,fr(p_19));
        addA(131,sym_40,fr(p_19));
        addA(131,sym_39,fr(p_19));
        addA(131,sym_34,fr(p_19));
        addA(131,sym_33,fr(p_19));
        addA(131,sym_56,fr(p_19));
        addA(131,sym_50,fr(p_19));
        addA(131,sym_52,fr(p_19));
        addA(131,sym_46,fr(p_19));
        addL(131,eps(),sym_33,sym_34,sym_50,sym_52,sym_39,sym_40,sym_56,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        GroupLine ::= ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl (*) TSeq newline
        TSeq ::= (*) terminal_tok TSeq
        TSeq ::= (*)	[newline]

    */
    public static void init_132()
    {
        addA(132,sym_56,fr(p_40));
        addA(132,sym_1,sh(106));
        addG(132,sym_83,sh(140));
        addL(132,sym_49,sym_1,sym_56);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl (*) lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_133()
    {
        addA(133,sym_48,sh(138));
        addL(133,sym_49,sym_48);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl (*) submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_134()
    {
        addA(134,sym_14,sh(139));
        addL(134,sym_49,sym_14);
    }
    /*
        Regex_UR ::= lbrack colon termname colon (*) rbrack

    */
    public static void init_135()
    {
        addA(135,sym_41,sh(141));
        addL(135,eps(),sym_41);
    }
    /*
        Regex_UG ::= Regex_CHAR dash Regex_CHAR (*)	[escaped, character, rbrack]

    */
    public static void init_136()
    {
        addA(136,sym_41,fr(p_35));
        addA(136,sym_50,fr(p_35));
        addA(136,sym_52,fr(p_35));
        addL(136,eps(),sym_50,sym_52,sym_41);
    }
    /*
        OpLine ::= operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl (*) assoctypes newline

    */
    public static void init_137()
    {
        addA(137,sym_19,sh(142));
        addL(137,sym_49,sym_19);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace (*) TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        TSeq ::= (*) terminal_tok TSeq
        TSeq ::= (*)	[rbrace]

    */
    public static void init_138()
    {
        addA(138,sym_45,fr(p_40));
        addA(138,sym_1,sh(106));
        addG(138,sym_83,sh(146));
        addL(138,sym_49,sym_1,sym_45);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl (*) lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_139()
    {
        addA(139,sym_48,sh(143));
        addL(139,sym_49,sym_48);
    }
    /*
        GroupLine ::= ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq (*) newline

    */
    public static void init_140()
    {
        addA(140,sym_56,sh(144));
        addL(140,sym_49,sym_56);
    }
    /*
        Regex_UR ::= lbrack colon termname colon rbrack (*)	[plus, star, escaped, character, question, bar, newline, lparen, lbrack, wildcard, rparen]

    */
    public static void init_141()
    {
        addA(141,sym_47,fr(p_20));
        addA(141,sym_42,fr(p_20));
        addA(141,sym_44,fr(p_20));
        addA(141,sym_40,fr(p_20));
        addA(141,sym_39,fr(p_20));
        addA(141,sym_34,fr(p_20));
        addA(141,sym_33,fr(p_20));
        addA(141,sym_56,fr(p_20));
        addA(141,sym_50,fr(p_20));
        addA(141,sym_52,fr(p_20));
        addA(141,sym_46,fr(p_20));
        addL(141,eps(),sym_33,sym_50,sym_34,sym_52,sym_39,sym_56,sym_40,sym_42,sym_44,sym_46,sym_47);
    }
    /*
        OpLine ::= operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes (*) newline

    */
    public static void init_142()
    {
        addA(142,sym_56,sh(145));
        addL(142,sym_49,sym_56);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace (*) SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        SymSeq ::= (*) symbol_tok SymSeq
        SymSeq ::= (*)	[rbrace]

    */
    public static void init_143()
    {
        addA(143,sym_45,fr(p_27));
        addA(143,sym_2,sh(148));
        addG(143,sym_75,sh(147));
        addL(143,sym_49,sym_2,sym_45);
    }
    /*
        GroupLine ::= ambiguous_decl t_decl group_decl groupname_tok code_decl embedded_code members_decl TSeq newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_144()
    {
        addA(144,sym_22,fr(p_2));
        addA(144,sym_17,fr(p_2));
        addA(144,sym_18,fr(p_2));
        addA(144,sym_12,fr(p_2));
        addA(144,sym_11,fr(p_2));
        addA(144,sym_36,fr(p_2));
        addA(144,sym_10,fr(p_2));
        addA(144,sym_9,fr(p_2));
        addA(144,sym_32,fr(p_2));
        addA(144,sym_7,fr(p_2));
        addA(144,sym_25,fr(p_2));
        addL(144,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        OpLine ::= operator_decl terminal_tok precclass_decl precclass_tok prec_decl prec_number assoc_decl assoctypes newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_145()
    {
        addA(145,sym_22,fr(p_7));
        addA(145,sym_17,fr(p_7));
        addA(145,sym_18,fr(p_7));
        addA(145,sym_12,fr(p_7));
        addA(145,sym_11,fr(p_7));
        addA(145,sym_36,fr(p_7));
        addA(145,sym_10,fr(p_7));
        addA(145,sym_9,fr(p_7));
        addA(145,sym_32,fr(p_7));
        addA(145,sym_7,fr(p_7));
        addA(145,sym_25,fr(p_7));
        addL(145,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq (*) rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_146()
    {
        addA(146,sym_45,sh(149));
        addL(146,sym_49,sym_45);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq (*) rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_147()
    {
        addA(147,sym_45,sh(152));
        addL(147,sym_49,sym_45);
    }
    /*
        SymSeq ::= symbol_tok (*) SymSeq
        SymSeq ::= (*) symbol_tok SymSeq
        SymSeq ::= (*)	[newline, rbrace]

    */
    public static void init_148()
    {
        addA(148,sym_56,fr(p_27));
        addA(148,sym_45,fr(p_27));
        addA(148,sym_2,sh(148));
        addG(148,sym_75,sh(150));
        addL(148,sym_49,sym_2,sym_56,sym_45);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace (*) layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_149()
    {
        addA(149,sym_24,sh(151));
        addL(149,sym_49,sym_24);
    }
    /*
        SymSeq ::= symbol_tok SymSeq (*)	[newline, rbrace]

    */
    public static void init_150()
    {
        addA(150,sym_56,fr(p_26));
        addA(150,sym_45,fr(p_26));
        addL(150,sym_49,sym_56,sym_45);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl (*) lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_151()
    {
        addA(151,sym_48,sh(154));
        addL(151,sym_49,sym_48);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace (*) dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_152()
    {
        addA(152,sym_15,sh(153));
        addL(152,sym_49,sym_15);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl (*) lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_153()
    {
        addA(153,sym_48,sh(155));
        addL(153,sym_49,sym_48);
    }
    /*
        TSeq ::= (*) terminal_tok TSeq
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace (*) TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline
        TSeq ::= (*)	[rbrace]

    */
    public static void init_154()
    {
        addA(154,sym_45,fr(p_40));
        addA(154,sym_1,sh(106));
        addG(154,sym_83,sh(156));
        addL(154,sym_49,sym_1,sym_45);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace (*) SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline
        SymSeq ::= (*) symbol_tok SymSeq
        SymSeq ::= (*)	[rbrace]

    */
    public static void init_155()
    {
        addA(155,sym_45,fr(p_27));
        addA(155,sym_2,sh(148));
        addG(155,sym_75,sh(158));
        addL(155,sym_49,sym_2,sym_45);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq (*) rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_156()
    {
        addA(156,sym_45,sh(157));
        addL(156,sym_49,sym_45);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace (*) code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_157()
    {
        addA(157,sym_27,sh(160));
        addL(157,sym_49,sym_27);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq (*) rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_158()
    {
        addA(158,sym_45,sh(159));
        addL(158,sym_49,sym_45);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace (*) prefix_decl lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_159()
    {
        addA(159,sym_16,sh(161));
        addL(159,sym_49,sym_16);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl (*) embedded_code bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_160()
    {
        addA(160,sym_29,sh(162));
        addL(160,sym_49,sym_29);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl (*) lbrace TSeq rbrace code_decl embedded_code newline

    */
    public static void init_161()
    {
        addA(161,sym_48,sh(163));
        addL(161,sym_49,sym_48);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code (*) bnf_decl nonterm_tok goesto SymSeq newline

    */
    public static void init_162()
    {
        addA(162,sym_23,sh(164));
        addL(162,sym_49,sym_23);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace (*) TSeq rbrace code_decl embedded_code newline
        TSeq ::= (*) terminal_tok TSeq
        TSeq ::= (*)	[rbrace]

    */
    public static void init_163()
    {
        addA(163,sym_45,fr(p_40));
        addA(163,sym_1,sh(106));
        addG(163,sym_83,sh(165));
        addL(163,sym_49,sym_1,sym_45);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl (*) nonterm_tok goesto SymSeq newline

    */
    public static void init_164()
    {
        addA(164,sym_0,sh(166));
        addL(164,sym_49,sym_0);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq (*) rbrace code_decl embedded_code newline

    */
    public static void init_165()
    {
        addA(165,sym_45,sh(168));
        addL(165,sym_49,sym_45);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok (*) goesto SymSeq newline

    */
    public static void init_166()
    {
        addA(166,sym_26,sh(167));
        addL(166,sym_49,sym_26);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto (*) SymSeq newline
        SymSeq ::= (*) symbol_tok SymSeq
        SymSeq ::= (*)	[newline]

    */
    public static void init_167()
    {
        addA(167,sym_56,fr(p_27));
        addA(167,sym_2,sh(148));
        addG(167,sym_75,sh(170));
        addL(167,sym_49,sym_2,sym_56);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace (*) code_decl embedded_code newline

    */
    public static void init_168()
    {
        addA(168,sym_27,sh(169));
        addL(168,sym_49,sym_27);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl (*) embedded_code newline

    */
    public static void init_169()
    {
        addA(169,sym_29,sh(172));
        addL(169,sym_49,sym_29);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq (*) newline

    */
    public static void init_170()
    {
        addA(170,sym_56,sh(171));
        addL(170,sym_49,sym_56);
    }
    /*
        ProdLine ::= prod_decl prodname_tok precclass_decl precclass_tok prec_decl prec_number operator_decl lbrace TSeq rbrace layout_decl lbrace TSeq rbrace code_decl embedded_code bnf_decl nonterm_tok goesto SymSeq newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, attr_decl, comment_line, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_171()
    {
        addA(171,sym_22,fr(p_8));
        addA(171,sym_17,fr(p_8));
        addA(171,sym_18,fr(p_8));
        addA(171,sym_12,fr(p_8));
        addA(171,sym_11,fr(p_8));
        addA(171,sym_36,fr(p_8));
        addA(171,sym_10,fr(p_8));
        addA(171,sym_9,fr(p_8));
        addA(171,sym_32,fr(p_8));
        addA(171,sym_7,fr(p_8));
        addA(171,sym_25,fr(p_8));
        addL(171,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_9,sym_25,sym_10,sym_11,sym_12,sym_32);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code (*) newline

    */
    public static void init_172()
    {
        addA(172,sym_56,sh(173));
        addL(172,sym_49,sym_56);
    }
    /*
        TokLine ::= tok_decl terminal_tok precclass_decl lbrace PrecClassSeq rbrace prec_decl submit_decl lbrace SymSeq rbrace dominates_decl lbrace SymSeq rbrace prefix_decl lbrace TSeq rbrace code_decl embedded_code newline (*)	[operator_decl, default_decl, start_decl, prod_decl, $, comment_line, attr_decl, t_decl, tok_decl, nt_decl, ambiguous_decl]

    */
    public static void init_173()
    {
        addA(173,sym_22,fr(p_5));
        addA(173,sym_17,fr(p_5));
        addA(173,sym_18,fr(p_5));
        addA(173,sym_12,fr(p_5));
        addA(173,sym_11,fr(p_5));
        addA(173,sym_36,fr(p_5));
        addA(173,sym_10,fr(p_5));
        addA(173,sym_9,fr(p_5));
        addA(173,sym_32,fr(p_5));
        addA(173,sym_7,fr(p_5));
        addA(173,sym_25,fr(p_5));
        addL(173,sym_49,sym_17,sym_18,sym_36,sym_22,sym_7,sym_25,sym_9,sym_10,sym_11,sym_12,sym_32);
    }
    static
    {
        sym_0 = t("nonterm_tok");
        sym_1 = t("terminal_tok");
        sym_2 = t("symbol_tok");
        sym_3 = t("prodname_tok");
        sym_4 = t("precclass_tok");
        sym_5 = t("attrname_tok");
        sym_6 = t("groupname_tok");
        sym_7 = t("$");
        sym_8 = t("prec_number");
        sym_9 = t("comment_line");
        sym_10 = t("t_decl");
        sym_11 = t("tok_decl");
        sym_12 = t("nt_decl");
        sym_13 = t("prec_decl");
        sym_14 = t("submit_decl");
        sym_15 = t("dominates_decl");
        sym_16 = t("prefix_decl");
        sym_17 = t("operator_decl");
        sym_18 = t("default_decl");
        sym_19 = t("assoctypes");
        sym_20 = t("assoc_decl");
        sym_21 = t("precclass_decl");
        sym_22 = t("prod_decl");
        sym_23 = t("bnf_decl");
        sym_24 = t("layout_decl");
        sym_25 = t("attr_decl");
        sym_26 = t("goesto");
        sym_27 = t("code_decl");
        sym_28 = t("attr_type_decl");
        sym_29 = t("embedded_code");
        sym_30 = t("attr_type_base");
        sym_31 = t("group_decl");
        sym_32 = t("ambiguous_decl");
        sym_33 = t("plus");
        sym_34 = t("star");
        sym_35 = t("members_decl");
        sym_36 = t("start_decl");
        sym_37 = t("dash");
        sym_38 = t("colon");
        sym_39 = t("question");
        sym_40 = t("bar");
        sym_41 = t("rbrack");
        sym_42 = t("lparen");
        sym_43 = t("not");
        sym_44 = t("lbrack");
        sym_45 = t("rbrace");
        sym_46 = t("wildcard");
        sym_47 = t("rparen");
        sym_48 = t("lbrace");
        sym_49 = t("ws");
        sym_50 = t("escaped");
        sym_51 = t("termname");
        sym_52 = t("character");
        sym_53 = t("grammar_name_decl");
        sym_54 = t("grammarname_tok");
        sym_55 = t("grammar_decl");
        sym_56 = t("newline");
        sym_57 = t("grammar_version");
        sym_58 = t("spectypes");
        sym_59 = t("spectype_decl");
        sym_60 = nt("StartLine");
        sym_61 = nt("AttrLine");
        sym_62 = nt("GroupLine");
        sym_63 = nt("DefaultTCodeLine");
        sym_64 = nt("TLine");
        sym_65 = nt("TokLine");
        sym_66 = nt("^");
        sym_67 = nt("OpLine");
        sym_68 = nt("ProdLine");
        sym_69 = nt("AttrTypeRoot");
        sym_70 = nt("Regex_R");
        sym_71 = nt("Regex_DR");
        sym_72 = nt("Regex_UR");
        sym_73 = nt("DefaultProdCodeLine");
        sym_74 = nt("PrecClassSeq");
        sym_75 = nt("SymSeq");
        sym_76 = nt("Regex_Root");
        sym_77 = nt("Regex_CHAR");
        sym_78 = nt("Regex_G");
        sym_79 = nt("Regex_RR");
        sym_80 = nt("Regex_UG");
        sym_81 = nt("Regex_RG");
        sym_82 = nt("GrammarFile");
        sym_83 = nt("TSeq");
        sym_84 = nt("NTSeq");
        sym_85 = nt("NTLine");
        sym_86 = nt("Grammar");
        p_0 = p("StartLineMain",sym_60,sym_36,sym_0,sym_24,sym_48,sym_83,sym_45,sym_56);
        p_1 = p("AttrLineMain",sym_61,sym_25,sym_5,sym_28,sym_69,sym_27,sym_29,sym_56);
        p_2 = p("GroupLineMain",sym_62,sym_32,sym_10,sym_31,sym_6,sym_27,sym_29,sym_35,sym_83,sym_56);
        p_3 = p("DefaultTLineMain",sym_63,sym_18,sym_10,sym_27,sym_29,sym_56);
        p_4 = p("TLineMain",sym_64,sym_10,sym_1,sym_76);
        p_5 = p("TokLineMain",sym_65,sym_11,sym_1,sym_21,sym_48,sym_74,sym_45,sym_13,sym_14,sym_48,sym_75,sym_45,sym_15,sym_48,sym_75,sym_45,sym_16,sym_48,sym_83,sym_45,sym_27,sym_29,sym_56);
        p_6 = p("Capsule",sym_66,sym_82,sym_7);
        p_7 = p("OpLineMain",sym_67,sym_17,sym_1,sym_21,sym_4,sym_13,sym_8,sym_20,sym_19,sym_56);
        p_8 = p("ProdLineMain",sym_68,sym_22,sym_3,sym_21,sym_4,sym_13,sym_8,sym_17,sym_48,sym_83,sym_45,sym_24,sym_48,sym_83,sym_45,sym_27,sym_29,sym_23,sym_0,sym_26,sym_75,sym_56);
        p_9 = p("AttrTypeBase",sym_69,sym_30);
        p_10 = p("AttrTypeList",sym_69,sym_44,sym_69,sym_41);
        p_11 = p("RtoDR",sym_70,sym_71);
        p_12 = p("RtoDR_bar_R",sym_70,sym_71,sym_40,sym_70);
        p_13 = p("DRtoUR_star_RR",sym_71,sym_72,sym_34,sym_79);
        p_14 = p("DRtoUR_plus_RR",sym_71,sym_72,sym_33,sym_79);
        p_15 = p("DRtoUR_RR",sym_71,sym_72,sym_79);
        p_16 = p("DRtoUR_question_RR",sym_71,sym_72,sym_39,sym_79);
        p_17 = p("URtolp_R_rp",sym_72,sym_42,sym_70,sym_47);
        p_18 = p("URtoCHAR",sym_72,sym_77);
        p_19 = p("URtolb_not_G_rb",sym_72,sym_44,sym_43,sym_78,sym_41);
        p_20 = p("URtomacro",sym_72,sym_44,sym_38,sym_51,sym_38,sym_41);
        p_21 = p("URtowildcard",sym_72,sym_46);
        p_22 = p("URtolb_G_rb",sym_72,sym_44,sym_78,sym_41);
        p_23 = p("DefaultProdLineMain",sym_73,sym_18,sym_22,sym_27,sym_29,sym_56);
        p_24 = p("PrecClassSeqEps",sym_74);
        p_25 = p("PrecClassSeqMain",sym_74,sym_4,sym_74);
        p_26 = p("SymSeqMain",sym_75,sym_2,sym_75);
        p_27 = p("SymSeqEps",sym_75);
        p_28 = p("Roottoeps",sym_76,sym_38,sym_56);
        p_29 = p("RoottoR",sym_76,sym_38,sym_70,sym_56);
        p_30 = p("CHARtoescaped",sym_77,sym_50);
        p_31 = p("CHARtochar",sym_77,sym_52);
        p_32 = p("GtoUG_RG",sym_78,sym_80,sym_81);
        p_33 = p("RRtoeps",sym_79);
        p_34 = p("RRtoDR",sym_79,sym_71);
        p_35 = p("UGtoCHAR_dash_CHAR",sym_80,sym_77,sym_37,sym_77);
        p_36 = p("UGtoCHAR",sym_80,sym_77);
        p_37 = p("RGtoeps",sym_81);
        p_38 = p("RGtoG",sym_81,sym_78);
        p_39 = p("GrammarFiletoGrammar",sym_82,sym_55,sym_53,sym_54,sym_59,sym_58,sym_57,sym_56,sym_86);
        p_40 = p("TSeqEps",sym_83);
        p_41 = p("TSeqMain",sym_83,sym_1,sym_83);
        p_42 = p("NTSeqMain",sym_84,sym_0,sym_84);
        p_43 = p("NTSeqEps",sym_84);
        p_44 = p("NTLineMain",sym_85,sym_12,sym_84,sym_56);
        p_45 = p("GrammartoNTLine",sym_86,sym_85,sym_86);
        p_46 = p("GrammartoTLine",sym_86,sym_64,sym_86);
        p_47 = p("GrammartoTokLine",sym_86,sym_65,sym_86);
        p_48 = p("GrammartoOpLine",sym_86,sym_67,sym_86);
        p_49 = p("GrammartoProdLine",sym_86,sym_68,sym_86);
        p_50 = p("GrammartoStartLine",sym_86,sym_60,sym_86);
        p_51 = p("GrammartoAttrLine",sym_86,sym_61,sym_86);
        p_52 = p("GrammartoGroupLine",sym_86,sym_62,sym_86);
        p_53 = p("GrammartoDefaultTCodeLine",sym_86,sym_63,sym_86);
        p_54 = p("GrammartoDefaultProdCodeLine",sym_86,sym_73,sym_86);
        p_55 = p("GrammartoCommentLine",sym_86,sym_9,sym_56,sym_86);
        p_56 = p("GrammartoEps",sym_86);
        parseTable = new ThisParseTable();
        group_0 = tset(sym_52,sym_40);
        group_1 = tset(sym_52,sym_39);
        group_2 = tset(sym_52,sym_38);
        group_3 = tset(sym_52,sym_37);
        group_4 = tset(sym_34,sym_52);
        group_5 = tset(sym_33,sym_52);
        group_6 = tset(sym_52,sym_47);
        group_7 = tset(sym_52,sym_46);
        group_8 = tset(sym_52,sym_44);
        group_9 = tset(sym_52,sym_43);
        group_10 = tset(sym_52,sym_42);
        group_11 = tset(sym_52,sym_41);
    }
    public class Semantics extends edu.umn.cs.melt.copper.compiletime.engines.lalr.semantics.SemanticActionContainer
    {
        public Integer dotCounter;

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
            dotCounter = 0;
            dotCounter = 0;
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
                edu.umn.cs.melt.copper.runtime.io.InputPosition pos = (edu.umn.cs.melt.copper.runtime.io.InputPosition) _children[0];
	    RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.NON_TERMINAL,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,String>) _children[1]).second()),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "startLayout",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[4])),edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
		             "isStart",
		             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) true)));
            }
            else if(_prod.equals(p_1))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.PARSER_ATTRIBUTE,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "location",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "type",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "code",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[5])));

            }
            else if(_prod.equals(p_2))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DISAMBIGUATION_GROUP,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[3]),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "location",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "code",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[5])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "members",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[7])));
            }
            else if(_prod.equals(p_3))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(" defaultTermCode "),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "location",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "code",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])));
            }
            else if(_prod.equals(p_4))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "location",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "regex",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[2])));
            }
            else if(_prod.equals(p_5))
            {
                boolean noPrefix = false;
	    if(((java.util.LinkedList<String>) _children[17]).size() > 1) error(_pos,"Terminals cannot have more than one prefix");
	    else if(((java.util.LinkedList<String>) _children[17]).isEmpty())
	    {
	        noPrefix = true;
	    }
	    java.util.LinkedList<String> classes = (java.util.LinkedList<String>) _children[4];
	    edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode node = null;
	    for(String tClass : classes)
	    {
	        edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode newNode =
	         new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	          edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,
	          edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(tClass));
	        node = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(newNode,node);
	    }
	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
	           node,
	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "classes",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[4])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "submits",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[9])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "dominates",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[13])),
	            noPrefix ? null : edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "prefix",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) ((java.util.LinkedList<String>) _children[17]).getFirst())),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "code",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[20]))));
            }
            else if(_prod.equals(p_7))
            {
                edu.umn.cs.melt.copper.runtime.io.InputPosition pos = (edu.umn.cs.melt.copper.runtime.io.InputPosition) _children[0];
	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[3]),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "location",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null))),
	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "operatorClass",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "operatorPrecedence",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[5])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "operatorAssociativity",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[7]))));
            }
            else if(_prod.equals(p_8))
            {
                edu.umn.cs.melt.copper.runtime.io.InputPosition pos = (edu.umn.cs.melt.copper.runtime.io.InputPosition) _children[0];
	    boolean noOperator = false;
	    if(((java.util.LinkedList<String>) _children[8]).size() > 1) error(_pos,"Productions cannot have more than one custom operator");
	    else if(((java.util.LinkedList<String>) _children[8]).isEmpty()) noOperator = true;
	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.TERMINAL_CLASS,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[3]),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "location",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null))),
	           new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.PRODUCTION,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[1]),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "location",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null)),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "class",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[3])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "precedence",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[5])),
	            noOperator ? null : edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "operator",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) ((java.util.LinkedList<String>) _children[8]).getFirst())),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "layout",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[12])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "code",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[15])),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "LHS",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,(Object) ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,String>) _children[17]).second())),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "RHS",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[19]))));
            }
            else if(_prod.equals(p_9))
            {
                RESULT = _children[0];
            }
            else if(_prod.equals(p_10))
            {
                RESULT = "java.util.LinkedList< " + _children[1] + " >";
            }
            else if(_prod.equals(p_11))
            {
                RESULT = _children[0];
            }
            else if(_prod.equals(p_12))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice(
	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0],
	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);
            }
            else if(_prod.equals(p_13))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar(
	             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),
	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);
            }
            else if(_prod.equals(p_14))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
	             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),
	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar(
	             ((edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]).clone()),
	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);

            }
            else if(_prod.equals(p_15))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0],
	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[1]);
            }
            else if(_prod.equals(p_16))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation(
	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice(
	             new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString(),
	             (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[0]),
	            (edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex) _children[2]);
            }
            else if(_prod.equals(p_17))
            {
                RESULT = _children[1];
            }
            else if(_prod.equals(p_18))
            {
                RESULT = _children[0];
            }
            else if(_prod.equals(p_19))
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
            else if(_prod.equals(p_20))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.MacroHole(
	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal((String) _children[2]));
            }
            else if(_prod.equals(p_21))
            {
                edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet Newline =
		       edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.instantiate(
		        edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,'\n');
	    RESULT = Newline.invertSet();
            }
            else if(_prod.equals(p_22))
            {
                RESULT = _children[1];
            }
            else if(_prod.equals(p_23))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(" defaultProdCode "),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "location",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
	            edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	             "code",
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,_children[3])));
            }
            else if(_prod.equals(p_24))
            {
                RESULT = new java.util.LinkedList<String>();
            }
            else if(_prod.equals(p_25))
            {
                ((java.util.LinkedList<String>) _children[1]).addFirst((String) _children[0]);
	    RESULT = _children[1];
            }
            else if(_prod.equals(p_26))
            {
                ((java.util.LinkedList<String>) _children[1]).addFirst((String) _children[0]);
	    RESULT = _children[1];
            }
            else if(_prod.equals(p_27))
            {
                RESULT = new java.util.LinkedList<String>();
            }
            else if(_prod.equals(p_28))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();
            }
            else if(_prod.equals(p_29))
            {
                RESULT = _children[1];
            }
            else if(_prod.equals(p_30))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.instantiate(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,
			     (((String) _children[0]).toCharArray()));
            }
            else if(_prod.equals(p_31))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.instantiate(
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet.LOOSE_CHARACTERS,
			     (((String) _children[0]).toCharArray()));
            }
            else if(_prod.equals(p_32))
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
            else if(_prod.equals(p_33))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();
            }
            else if(_prod.equals(p_34))
            {
                RESULT = _children[0];
            }
            else if(_prod.equals(p_35))
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
            else if(_prod.equals(p_36))
            {
                RESULT = _children[0];
            }
            else if(_prod.equals(p_37))
            {
                RESULT = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString();
            }
            else if(_prod.equals(p_38))
            {
                RESULT = _children[0];
            }
            else if(_prod.equals(p_39))
            {
                edu.umn.cs.melt.copper.runtime.io.InputPosition pos = (edu.umn.cs.melt.copper.runtime.io.InputPosition) _children[0];
	    //String spectype = (String) _children[4];
	    String postParseCode = "";
	    //if(spectype.equals("LALR1-silver.haskell")) postParseCode = "printParseTree(System.out,false,(edu.umn.cs.melt.copper.runtime.parsetree.stripped.StrippedParseTreeNode) root,\"\");";
	    //else if(spectype.equals("LALR1-pretty")) postParseCode = "printParseTree(System.out,true,(edu.umn.cs.melt.copper.runtime.parsetree.stripped.StrippedParseTreeNode) root,\"\");";
	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	             edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.GRAMMAR_NAME,
	             edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol((String) _children[2]),
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	              "location",
	              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null))),
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	              "spectype",
	              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,_children[4])),
	            edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(
	            new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	             edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.DIRECTIVE,
	             edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(" postParseCode "),
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	              "location",
	              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,null)),
	             edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	              "code",
	              edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,(Object) postParseCode))),
	             _children[7]));

            }
            else if(_prod.equals(p_40))
            {
                RESULT = new java.util.LinkedList<String>();
            }
            else if(_prod.equals(p_41))
            {
                ((java.util.LinkedList<String>) _children[1]).addFirst((String) _children[0]);
	    RESULT = _children[1];
            }
            else if(_prod.equals(p_42))
            {
                edu.umn.cs.melt.copper.runtime.io.InputPosition pos = ((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,String>) _children[0]).first();
	    Object child0 = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode(
	                     edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort.NON_TERMINAL,	                     edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol.symbol(((edu.umn.cs.melt.copper.runtime.auxiliary.Pair<edu.umn.cs.melt.copper.runtime.io.InputPosition,String>) _children[0]).second()),
	                     edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(
	                      "location",
	                      edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(pos,null)));
	    RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(child0,_children[1]);
            }
            else if(_prod.equals(p_43))
            {
                RESULT = null;
            }
            else if(_prod.equals(p_44))
            {
                RESULT = _children[1];
            }
            else if(_prod.equals(p_45))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_46))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_47))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_48))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_49))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_50))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_51))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_52))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_53))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_54))
            {
                RESULT = edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode.cons(_children[0],_children[1]);
            }
            else if(_prod.equals(p_55))
            {
                RESULT = _children[2];
            }
            else if(_prod.equals(p_56))
            {
                RESULT = null;
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

            if(_terminal.getToken().equals(sym_0))
            {
                if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
  RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,lexeme);            }
            else if(_terminal.getToken().equals(sym_1))
            {
                if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
  RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_2))
            {
                if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
  RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_3))
            {
                if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
  RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_4))
            {
                if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
  RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_5))
            {
                if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
  RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_6))
            {
                if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
  RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_8))
            {
                RESULT = Integer.parseInt(lexeme);            }
            else if(_terminal.getToken().equals(sym_9))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_10))
            {
                RESULT = _pos;            }
            else if(_terminal.getToken().equals(sym_11))
            {
                RESULT = _pos;            }
            else if(_terminal.getToken().equals(sym_12))
            {
                RESULT = _pos;            }
            else if(_terminal.getToken().equals(sym_13))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_14))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_15))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_16))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_17))
            {
                RESULT = _pos;            }
            else if(_terminal.getToken().equals(sym_18))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_19))
            {
                   if(lexeme.equals("nonassoc")) RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_NONASSOC);
   else if(lexeme.equals("left")) RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_LEFT);
   else /* if(lexeme.equals("right")) */ RESULT = edu.umn.cs.melt.copper.runtime.auxiliary.Pair.cons(_pos,edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes.ASSOC_RIGHT);            }
            else if(_terminal.getToken().equals(sym_20))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_21))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_22))
            {
                RESULT = _pos;            }
            else if(_terminal.getToken().equals(sym_23))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_24))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_25))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_26))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_27))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_28))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_29))
            {
                RESULT = lexeme.substring(1,lexeme.length() - 1);            }
            else if(_terminal.getToken().equals(sym_30))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_31))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_32))
            {
                RESULT = _pos;            }
            else if(_terminal.getToken().equals(sym_33))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_34))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_35))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_36))
            {
                RESULT = _pos;            }
            else if(_terminal.getToken().equals(sym_37))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_38))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_39))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_40))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_41))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_42))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_43))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_44))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_45))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_46))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_47))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_48))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_49))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_50))
            {
                    char escapedChar = edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter.getRepresentedCharacter(lexeme);
    if(escapedChar == edu.umn.cs.melt.copper.runtime.io.ScannerBuffer.EOFIndicator) error(_pos,"Illegal escaped character");
    RESULT = String.valueOf(escapedChar);            }
            else if(_terminal.getToken().equals(sym_51))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_52))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_53))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_54))
            {
                if(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK)) logger.logTick(edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.AST_DOT_WINDOW,".");
  RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_55))
            {
                RESULT = _pos;            }
            else if(_terminal.getToken().equals(sym_56))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_57))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_58))
            {
                RESULT = lexeme;            }
            else if(_terminal.getToken().equals(sym_59))
            {
                RESULT = lexeme;            }
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
            else return null;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_0(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData bar = _layouts.get(qsm(sym_40.getId(),null,null,null,null));
            return bar;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_1(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData question = _layouts.get(qsm(sym_39.getId(),null,null,null,null));
            return question;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_2(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData colon = _layouts.get(qsm(sym_38.getId(),null,null,null,null));
            return colon;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_3(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData dash = _layouts.get(qsm(sym_37.getId(),null,null,null,null));
            return dash;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_4(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData star = _layouts.get(qsm(sym_34.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            return star;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_5(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData plus = _layouts.get(qsm(sym_33.getId(),null,null,null,null));
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            return plus;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_6(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData rparen = _layouts.get(qsm(sym_47.getId(),null,null,null,null));
            return rparen;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_7(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData wildcard = _layouts.get(qsm(sym_46.getId(),null,null,null,null));
            return wildcard;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_8(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData lbrack = _layouts.get(qsm(sym_44.getId(),null,null,null,null));
            return lbrack;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_9(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData not = _layouts.get(qsm(sym_43.getId(),null,null,null,null));
            return not;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_10(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData lparen = _layouts.get(qsm(sym_42.getId(),null,null,null,null));
            return lparen;
        }
        public edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData disambiguate_11(String lexeme,edu.umn.cs.melt.copper.compiletime.auxiliary.DynHashSet<edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData> _layouts)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperException
        {
            @SuppressWarnings("unused") edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData character = _layouts.get(qsm(sym_52.getId(),null,null,null,null));
            edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchData rbrack = _layouts.get(qsm(sym_41.getId(),null,null,null,null));
            return rbrack;
        }
    }

    public static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource parseGrammar(java.util.ArrayList< edu.umn.cs.melt.copper.runtime.auxiliary.Pair<String,java.io.Reader> > files,edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger logger)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperException
    {
        edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode node = null;
        for(edu.umn.cs.melt.copper.runtime.auxiliary.Pair<String,java.io.Reader> file : files)
        {
            edu.umn.cs.melt.copper.compiletime.engines.lalr.LALREngine engine = new edu.umn.cs.melt.copper.compiletime.concretesyntax.GrammarParser(file.second(),logger);
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


 class GrammarParserScanner extends edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScanner
{
    public GrammarParserScanner(java.io.Reader reader,edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger logger)
    {
        super(logger.isLoggable(edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort.TICK));
        this.buffer = ScannerBuffer.instantiate(reader);
        this.logger = logger;
        startState = 97;
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
    private static edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal t_1,t_2,t_3,t_4,t_5,t_6,t_7,t_8,t_9,t_10,t_11,t_12,t_13,t_14,t_15,t_16,t_17,t_18,t_19,t_20,t_21,t_22,t_23,t_24,t_25,t_26,t_27,t_28,t_29,t_30,t_31,t_32,t_33,t_34,t_35,t_36,t_37,t_38,t_39,t_40,t_41,t_42,t_43,t_44,t_45,t_46,t_47,t_48,t_49,t_50,t_51,t_52,t_53,t_54,t_55,t_56,t_57,t_58,t_59;
;
    private static void symAdd_0()
    {
        sas(1,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(1,t_59,t_58,t_40,t_57,t_10,t_56,t_55,t_54,t_5,t_53);
        sas(2,t_59,t_58,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(2,t_59,t_58,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(3,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_1);
        sps(3,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_1);
        sas(4,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(4,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_1);
        sas(5,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(5,t_59,t_58,t_40,t_57,t_10,t_56,t_55,t_54,t_5,t_53);
        sas(6,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_21,t_5,t_54,t_53);
        sps(6,t_59,t_58,t_10,t_57,t_11,t_56,t_21,t_55,t_54,t_5,t_53,t_35);
        sas(7,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(7,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(8,t_59,t_58,t_10,t_57,t_56,t_26,t_55,t_5,t_54,t_53);
        sps(8,t_59,t_58,t_10,t_57,t_26,t_56,t_55,t_54,t_5,t_53);
        sas(9,t_59,t_58,t_57,t_56,t_55,t_5,t_54,t_53,t_30);
        sps(9,t_59,t_58,t_57,t_56,t_55,t_54,t_5,t_53,t_30);
        sas(10,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(10,t_59,t_58,t_10,t_57,t_56,t_55,t_39,t_54,t_5,t_53);
        sas(11,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_2);
        sps(11,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(12,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(12,t_59,t_58,t_10,t_57,t_56,t_38,t_55,t_54,t_5,t_53);
        sas(13,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_6,t_53);
        sps(13,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_6,t_53);
        sas(14,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(14,t_59,t_58,t_10,t_57,t_56,t_38,t_55,t_54,t_5,t_53);
        sas(15,t_59,t_58,t_9,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(15,t_59,t_9,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(16,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(16,t_59,t_58,t_57,t_56,t_55,t_54,t_53,t_50,t_49,t_10,t_11,t_5,t_33);
        sas(17,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(17,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(18,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(18,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_47,t_44);
        sas(19,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(19,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_37);
        sas(20,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(20,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(21,t_10);
        sps(21,t_10);
        sas(22,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_31);
        sps(22,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(23,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(23,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(24,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(24,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(25,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(25,t_59,t_58,t_10,t_57,t_56,t_38,t_55,t_54,t_5,t_53);
        sas(26,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_29);
        sps(26,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_29);
        sas(27,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(27,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_37);
        sas(28,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(28,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_37);
        sas(29,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(29,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(30,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_30);
        sps(30,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_30);
        sas(31,t_59,t_58,t_57,t_56,t_11,t_55,t_5,t_54,t_20,t_53);
        sps(31,t_59,t_58,t_57,t_11,t_56,t_55,t_20,t_54,t_5,t_53);
        sas(32,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(32,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_37);
        sas(33,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_16);
        sps(33,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_16);
        sas(34,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_28);
        sps(34,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_28);
        sas(35,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_17);
        sps(35,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_17);
        sas(36,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_18);
        sps(36,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_18);
        sas(37,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(37,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(38,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(38,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_36,t_53);
        sas(39,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_22);
        sps(39,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_22);
        sas(40,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_23,t_53);
        sps(40,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_23,t_53);
        sas(41,t_59,t_24,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(41,t_59,t_24,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53);
        sas(42,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(42,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_34);
        sas(43,t_25,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(43,t_25,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53);
        sas(44,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(44,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(45,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(45,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_34);
        sas(46,t_59,t_58,t_10,t_57,t_27,t_56,t_55,t_5,t_54,t_53);
        sps(46,t_59,t_58,t_27,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(47,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(47,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_6,t_53,t_28);
        sas(48,t_10,t_11,t_19);
        sps(48,t_10,t_11,t_19);
        sas(49,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(49,t_42,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(50,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(50,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_30);
        sas(51,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(51,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(52,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(52,t_59,t_58,t_10,t_57,t_56,t_55,t_39,t_54,t_5,t_53,t_47,t_44);
        sas(53,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(53,t_59,t_58,t_57,t_56,t_55,t_54,t_53,t_47,t_44,t_10,t_11,t_5,t_39);
        sas(54,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(54,t_42,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(55,t_10,t_11);
        sps(55,t_10,t_11);
        sas(56,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(56,t_42,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(57,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(57,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(58,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(58,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(59,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(59,t_42,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(60,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(60,t_42,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(61,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_15);
        sps(61,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_15);
        sas(62,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_2);
        sps(62,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(63,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_14);
        sps(63,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_14);
        sas(64,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(64,t_42,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(65,t_59,t_10,t_56,t_11,t_5,t_54,t_53,t_13);
        sps(65,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_13);
        srs(65,t_58,t_57,t_55);
        sas(66,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_12);
        sps(66,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_12);
        sas(67,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(67,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(68,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_52);
        sps(68,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_52,t_1);
        sas(69,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(69,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(70,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(70,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(71,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(71,t_59,t_58,t_57,t_56,t_26,t_55,t_54,t_53,t_46,t_10,t_11,t_5,t_3);
        sas(72,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(72,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(73,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(73,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(74,t_10,t_46);
        sps(74,t_10,t_46);
        sas(75,t_59,t_58,t_9,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(75,t_59,t_9,t_58,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(76,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(76,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(77,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(77,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(78,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(78,t_59,t_43,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(79,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(79,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(80,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(80,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(81,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(81,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(82,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(82,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(83,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(83,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(84,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(84,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(85,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(85,t_59,t_58,t_10,t_57,t_11,t_56,t_38,t_55,t_54,t_5,t_53,t_32);
        sas(86,t_59,t_58,t_10,t_57,t_56,t_4,t_55,t_5,t_54,t_53);
        sps(86,t_59,t_58,t_10,t_57,t_56,t_4,t_55,t_54,t_5,t_53);
        sas(87,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(87,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(88,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(88,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(89,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_3);
        sps(89,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_3);
        sps(90,t_30);
        sas(91,t_8,t_10,t_51);
        sps(91,t_8,t_10,t_51);
        sas(92,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(92,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(93,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_2);
        sps(93,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(94,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(94,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_36,t_53);
        sas(95,t_10,t_7);
        sps(95,t_10,t_7);
        sas(96,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(96,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(97,t_8);
        sps(97,t_59,t_58,t_57,t_56,t_55,t_54,t_53,t_52,t_51,t_50,t_49,t_48,t_47,t_46,t_45,t_44,t_42,t_43,t_40,t_41,t_38,t_39,t_36,t_37,t_34,t_35,t_32,t_33,t_30,t_31,t_28,t_29,t_25,t_24,t_27,t_26,t_21,t_20,t_23,t_22,t_17,t_16,t_19,t_18,t_13,t_12,t_15,t_14,t_8,t_9,t_10,t_11,t_4,t_5,t_6,t_7,t_1,t_2,t_3);
        sas(98,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(98,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(99,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(99,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_29);
        sas(100,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(100,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(101,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(101,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(102,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(102,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_6,t_53);
        sas(103,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(103,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_29);
        sas(104,t_59,t_58,t_10,t_57,t_56,t_41,t_55,t_5,t_54,t_53);
        sps(104,t_59,t_58,t_10,t_57,t_41,t_56,t_55,t_54,t_5,t_53);
        sas(105,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(105,t_59,t_58,t_27,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53);
        sas(106,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(106,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(107,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(107,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_29);
        sas(108,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(108,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(109,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(109,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(110,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(110,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(111,t_59,t_42,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(111,t_42,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(112,t_59,t_43,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(112,t_59,t_43,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(113,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(113,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_48);
        sas(114,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(114,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(115,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(115,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_46);
        sas(116,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(116,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_28);
        sas(117,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(117,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_46);
        sas(118,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(118,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_28);
        sas(119,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(119,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_46);
        sas(120,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(120,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_6,t_53,t_28);
        sas(121,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(121,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_46);
        sas(122,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(122,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_6,t_53);
        sas(123,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(123,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_29);
        sas(124,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(124,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_46);
        sas(125,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(125,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_48);
        sas(126,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(126,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_46);
        sas(127,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(127,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_29);
        sas(128,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(128,t_59,t_58,t_57,t_56,t_55,t_54,t_53,t_48,t_10,t_40,t_11,t_4,t_5);
        sas(129,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(129,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_6,t_53);
        sas(130,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(130,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_29);
        sas(131,t_59,t_58,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(131,t_59,t_58,t_57,t_56,t_55,t_54,t_5,t_53,t_30);
        sas(132,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(132,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_6,t_53);
        sas(133,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(133,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_29);
        sas(134,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(134,t_59,t_58,t_27,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(135,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(135,t_59,t_58,t_27,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(136,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_50);
        sps(136,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_50);
        sas(137,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(137,t_59,t_43,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(138,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(138,t_59,t_58,t_27,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(139,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(139,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_30);
        sas(140,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(140,t_59,t_43,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(141,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(141,t_59,t_58,t_40,t_57,t_10,t_11,t_56,t_55,t_54,t_5,t_53);
        sas(142,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(142,t_59,t_58,t_27,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(143,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(143,t_59,t_43,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(144,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(144,t_59,t_58,t_27,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(145,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(145,t_59,t_43,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(146,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(146,t_59,t_43,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(147,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_47);
        sps(147,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_47);
        sas(148,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(148,t_59,t_58,t_40,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_37);
        sas(149,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(149,t_59,t_9,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53);
        sas(150,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(150,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_44);
        sas(151,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_44);
        sps(151,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_44);
        sas(152,t_8,t_10,t_11,t_51);
        sps(152,t_8,t_10,t_11,t_51);
        sas(153,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(153,t_59,t_58,t_10,t_57,t_26,t_56,t_55,t_54,t_5,t_53);
        sas(154,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_48);
        sps(154,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_48);
        sas(155,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(155,t_59,t_58,t_10,t_57,t_26,t_56,t_55,t_54,t_5,t_53);
        sas(156,t_10,t_7);
        sps(156,t_10,t_7);
        sas(157,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(157,t_59,t_58,t_10,t_57,t_26,t_56,t_55,t_54,t_5,t_53);
        sas(158,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(158,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_44);
        sas(159,t_10);
        sps(159,t_10,t_30);
        sas(160,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(160,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(161,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_45);
        sps(161,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_45);
        sas(162,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_49);
        sps(162,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_49);
        sas(163,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(163,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_34);
        sas(164,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(164,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(165,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(165,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_49);
        sas(166,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(166,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(167,t_10,t_51);
        sps(167,t_10,t_51);
        sas(168,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(168,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_48);
        sas(169,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(169,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(170,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(170,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_33);
        sas(171,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(171,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(172,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_33);
        sps(172,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_33);
        sas(173,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(173,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_34);
        sas(174,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_36,t_53);
        sps(174,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_36,t_53);
        sas(175,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(175,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(176,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(176,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_34);
        sas(177,t_30);
        sps(177,t_30);
        sas(178,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(178,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(179,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_35);
        sps(179,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_35);
        sas(180,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(180,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_49);
        sas(181,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(181,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_34);
        sas(182,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(182,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53);
        sas(183,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(183,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(184,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_34);
        sps(184,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_34);
        sas(185,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(185,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_34);
        sas(186,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(186,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_49);
        sas(187,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(187,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(188,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(188,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_32);
        sas(189,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_31);
        sps(189,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(190,t_9,t_10);
        sps(190,t_9,t_10);
        sas(191,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(191,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_50);
        sas(192,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(192,t_59,t_58,t_57,t_56,t_55,t_54,t_53,t_10,t_41,t_11,t_5,t_34,t_29);
        sas(193,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(193,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_50);
        sas(194,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(194,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(195,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(195,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(196,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(196,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_33);
        sas(197,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_32);
        sps(197,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_32);
        sas(198,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(198,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(199,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_51);
        sps(199,t_59,t_58,t_57,t_10,t_56,t_55,t_54,t_5,t_53,t_51);
        sas(200,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_31);
        sps(200,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(201,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(201,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_32);
        sas(202,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(202,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(203,t_51);
        sps(203,t_51);
        sas(204,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(204,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_3);
        sas(205,t_59,t_58,t_10,t_40,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(205,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(206,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(206,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_3);
        sas(207,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(207,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(208,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(208,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(209,t_10,t_30);
        sps(209,t_10,t_30);
        sas(210,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(210,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_3);
        sas(211,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_1);
        sps(211,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_1);
        sas(212,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53);
        sps(212,t_42,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53);
        sas(213,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(213,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(214,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(214,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(215,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(215,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_3);
        sas(216,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(216,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_47);
        sas(217,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(217,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(218,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(218,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(219,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(219,t_59,t_58,t_10,t_57,t_56,t_4,t_55,t_54,t_5,t_53);
        sas(220,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(220,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(221,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(221,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_47);
        sas(222,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(222,t_59,t_58,t_10,t_57,t_56,t_4,t_55,t_54,t_5,t_53);
        sas(223,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_52);
        sps(223,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_52,t_1);
        sas(224,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(224,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_47);
        sas(225,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_51);
        sps(225,t_59,t_58,t_57,t_10,t_11,t_56,t_55,t_54,t_5,t_53,t_51);
        sas(226,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(226,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(227,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(227,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_47);
        sas(228,t_59,t_58,t_10,t_40,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(228,t_59,t_58,t_40,t_10,t_57,t_56,t_55,t_54,t_5,t_53);
        sas(229,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(229,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(230,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53,t_37);
        sps(230,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_37);
        sas(231,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(231,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(232,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(232,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(233,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(233,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(234,t_59,t_58,t_57,t_56,t_55,t_5,t_54,t_53,t_51);
        sps(234,t_59,t_58,t_57,t_56,t_55,t_54,t_5,t_53,t_51);
        sas(235,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(235,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_1);
        sas(236,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(236,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(237,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(237,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_47);
        sas(238,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(238,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_2);
        sas(239,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(239,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_47);
        sas(240,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(240,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(241,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(241,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_3);
        sas(242,t_59,t_58,t_10,t_57,t_56,t_55,t_39,t_5,t_54,t_53);
        sps(242,t_59,t_58,t_10,t_57,t_56,t_55,t_39,t_54,t_5,t_53);
        sas(243,t_10);
        sps(243,t_10,t_46);
        sas(244,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(244,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(245,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(245,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_48);
        sas(246,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(246,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_3);
        sas(247,t_10);
        sps(247,t_10,t_46);
        sas(248,t_59,t_58,t_57,t_10,t_56,t_11,t_55,t_5,t_54,t_53,t_52);
        sps(248,t_59,t_58,t_10,t_57,t_11,t_56,t_55,t_54,t_5,t_53,t_52,t_1);
        sas(249,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(249,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_31);
        sas(250,t_59,t_58,t_10,t_57,t_56,t_55,t_5,t_54,t_53);
        sps(250,t_59,t_58,t_10,t_57,t_56,t_55,t_54,t_5,t_53,t_48);
        sas(251,t_59,t_58,t_10,t_57,t_56,t_38,t_55,t_5,t_54,t_53);
        sps(251,t_59,t_58,t_10,t_57,t_56,t_38,t_55,t_54,t_5,t_53);
    }

    private static edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo[] staticStateInfo;
    static
    {
        t_1 = t("grammar_version");
        t_2 = t("spectypes");
        t_3 = t("spectype_decl");
        t_4 = t("grammar_name_decl");
        t_5 = t("grammarname_tok");
        t_6 = t("grammar_decl");
        t_7 = t("newline");
        t_8 = t("ws");
        t_9 = t("escaped");
        t_10 = t("termname");
        t_11 = t("character");
        t_12 = t("wildcard");
        t_13 = t("rbrace");
        t_14 = t("lbrace");
        t_15 = t("rparen");
        t_16 = t("lparen");
        t_17 = t("rbrack");
        t_18 = t("lbrack");
        t_19 = t("not");
        t_20 = t("colon");
        t_21 = t("dash");
        t_22 = t("bar");
        t_23 = t("question");
        t_24 = t("star");
        t_25 = t("plus");
        t_26 = t("start_decl");
        t_27 = t("members_decl");
        t_28 = t("group_decl");
        t_29 = t("ambiguous_decl");
        t_30 = t("embedded_code");
        t_31 = t("attr_type_base");
        t_32 = t("code_decl");
        t_33 = t("attr_type_decl");
        t_34 = t("attr_decl");
        t_35 = t("goesto");
        t_36 = t("bnf_decl");
        t_37 = t("layout_decl");
        t_38 = t("precclass_decl");
        t_39 = t("prod_decl");
        t_40 = t("assoctypes");
        t_41 = t("assoc_decl");
        t_42 = t("operator_decl");
        t_43 = t("default_decl");
        t_44 = t("prefix_decl");
        t_45 = t("dominates_decl");
        t_46 = t("submit_decl");
        t_47 = t("prec_decl");
        t_48 = t("nt_decl");
        t_49 = t("tok_decl");
        t_50 = t("t_decl");
        t_51 = t("comment_line");
        t_52 = t("prec_number");
        t_53 = t("groupname_tok");
        t_54 = t("attrname_tok");
        t_55 = t("precclass_tok");
        t_56 = t("prodname_tok");
        t_57 = t("symbol_tok");
        t_58 = t("terminal_tok");
        t_59 = t("nonterm_tok");
        staticStateInfo = new edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo[252];
        for(int i = 0;i < 252;i++) staticStateInfo[i] = new edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo();
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
            case 199:
                return tr_199(ch);
            case 200:
                return tr_200(ch);
            case 201:
                return tr_201(ch);
            case 202:
                return tr_202(ch);
            case 203:
                return tr_203(ch);
            case 204:
                return tr_204(ch);
            case 205:
                return tr_205(ch);
            case 206:
                return tr_206(ch);
            case 207:
                return tr_207(ch);
            case 208:
                return tr_208(ch);
            case 209:
                return tr_209(ch);
            case 210:
                return tr_210(ch);
            case 211:
                return tr_211(ch);
            case 212:
                return tr_212(ch);
            case 213:
                return tr_213(ch);
            case 214:
                return tr_214(ch);
            case 215:
                return tr_215(ch);
            case 216:
                return tr_216(ch);
            case 217:
                return tr_217(ch);
            case 218:
                return tr_218(ch);
            case 219:
                return tr_219(ch);
            case 220:
                return tr_220(ch);
            case 221:
                return tr_221(ch);
            case 222:
                return tr_222(ch);
            case 223:
                return tr_223(ch);
            case 224:
                return tr_224(ch);
            case 225:
                return tr_225(ch);
            case 226:
                return tr_226(ch);
            case 227:
                return tr_227(ch);
            case 228:
                return tr_228(ch);
            case 229:
                return tr_229(ch);
            case 230:
                return tr_230(ch);
            case 231:
                return tr_231(ch);
            case 232:
                return tr_232(ch);
            case 233:
                return tr_233(ch);
            case 234:
                return tr_234(ch);
            case 235:
                return tr_235(ch);
            case 236:
                return tr_236(ch);
            case 237:
                return tr_237(ch);
            case 238:
                return tr_238(ch);
            case 239:
                return tr_239(ch);
            case 240:
                return tr_240(ch);
            case 241:
                return tr_241(ch);
            case 242:
                return tr_242(ch);
            case 243:
                return tr_243(ch);
            case 244:
                return tr_244(ch);
            case 245:
                return tr_245(ch);
            case 246:
                return tr_246(ch);
            case 247:
                return tr_247(ch);
            case 248:
                return tr_248(ch);
            case 249:
                return tr_249(ch);
            case 250:
                return tr_250(ch);
            case 251:
                return tr_251(ch);
        default: return 0;
        }
    }
    private int tr_1(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'i','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'h')) return 7;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','g')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_2(char ch)
    {
        if(chin(ch,'!','#')) return 2;
        if(chin(ch,'%',']')) return 2;
        if(chin(ch,'_','~')) return 2;
        return 0;
    }
    private int tr_3(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'%','/')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'0','9')) return 3;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_4(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'%','/')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'0','9')) return 211;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_5(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'h','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'g')) return 1;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','f')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_6(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'?',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,';','=')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,'>')) return 179;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_7(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 205;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_8(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_9(char ch)
    {
        if(chin(ch,'!','#')) return 2;
        if(chin(ch,'%',']')) return 2;
        if(chin(ch,'_','~')) return 2;
        return 0;
    }
    private int tr_10(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'e','~')) return 218;
        if(chin(ch,'_','c')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'d')) return 242;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_11(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%',',')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'-')) return 208;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'.','9')) return 218;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_12(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 251;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_13(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_14(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 12;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_15(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_16(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','x')) return 218;
        if(cheq(ch,'y')) return 196;
        if(cheq(ch,'o')) return 165;
        if(cheq(ch,'e')) return 191;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'z','~')) return 218;
        if(chin(ch,'f','n')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_17(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 20;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_18(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'d','e')) return 218;
        if(chin(ch,'g','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','b')) return 218;
        if(cheq(ch,'f')) return 150;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'c')) return 227;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_19(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 230;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_20(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 23;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_21(char ch)
    {
        if(chin(ch,' ','9')) return 21;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_22(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_23(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 24;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_24(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'y')) return 62;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'z','~')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','x')) return 218;
        return 0;
    }
    private int tr_25(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 14;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_26(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_27(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'y')) return 28;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'z','~')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','x')) return 218;
        return 0;
    }
    private int tr_28(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 32;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'_','n')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_29(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 17;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_30(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_31(char ch)
    {
        if(chin(ch,'!','#')) return 2;
        if(chin(ch,'%',']')) return 2;
        if(chin(ch,'_','~')) return 2;
        return 0;
    }
    private int tr_32(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'u')) return 19;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'v','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'_','t')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_33(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_34(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_35(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_36(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_37(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'o','~')) return 218;
        if(cheq(ch,'n')) return 244;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','m')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_38(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'g','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'f')) return 174;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','e')) return 218;
        return 0;
    }
    private int tr_39(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_40(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_41(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_42(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 45;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_43(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_44(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'m','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'l')) return 240;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','k')) return 218;
        return 0;
    }
    private int tr_45(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 184;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_46(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_47(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 120;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_48(char ch)
    {
        if(chin(ch,' ','9')) return 21;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_49(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 111;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_50(char ch)
    {
        if(chin(ch,'!','#')) return 50;
        if(chin(ch,';','?')) return 50;
        if(chin(ch,'A',']')) return 50;
        if(chin(ch,'\t','\n')) return 159;
        if(cheq(ch,'^')) return 159;
        if(chin(ch,'%','9')) return 50;
        if(cheq(ch,'@')) return 30;
        if(cheq(ch,':')) return 131;
        if(cheq(ch,'$')) return 159;
        if(cheq(ch,' ')) return 159;
        if(chin(ch,'_','~')) return 50;
        return 0;
    }
    private int tr_51(char ch)
    {
        if(chin(ch,'%','0')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'1')) return 11;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        if(chin(ch,'2','9')) return 218;
        return 0;
    }
    private int tr_52(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 10;
        if(cheq(ch,'e')) return 18;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'f','n')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_53(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 52;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_54(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 49;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'_','n')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_55(char ch)
    {
        if(chin(ch,' ','9')) return 21;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_56(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 54;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_57(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 226;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_58(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';','Q')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(cheq(ch,'R')) return 51;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        if(chin(ch,'S',']')) return 218;
        return 0;
    }
    private int tr_59(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 56;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_60(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 59;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_61(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_62(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_63(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_64(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 60;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_65(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_66(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_67(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';','K')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,'L')) return 58;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'M',']')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_68(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','-')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'/')) return 218;
        if(cheq(ch,'.')) return 4;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'0','9')) return 235;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_69(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'y')) return 104;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'z','~')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','x')) return 218;
        return 0;
    }
    private int tr_70(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 69;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_71(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'q','s')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','o')) return 218;
        if(cheq(ch,'u')) return 115;
        if(cheq(ch,'t')) return 153;
        if(cheq(ch,'p')) return 241;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'v','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_72(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 70;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_73(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','u')) return 218;
        if(cheq(ch,'v')) return 72;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'w','~')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_74(char ch)
    {
        if(chin(ch,' ','9')) return 21;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_75(char ch)
    {
        if(chin(ch,'!','#')) return 2;
        if(chin(ch,'%',']')) return 2;
        if(chin(ch,'_','~')) return 2;
        return 0;
    }
    private int tr_76(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 73;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_77(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 76;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_78(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 160;
        if(cheq(ch,'e')) return 140;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'f','n')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_79(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 77;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_80(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 79;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_81(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 82;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'_','n')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_82(char ch)
    {
        if(chin(ch,'d','~')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','b')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'c')) return 80;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_83(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 81;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_84(char ch)
    {
        if(chin(ch,'d','~')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','b')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'c')) return 114;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_85(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 188;
        if(cheq(ch,'l')) return 25;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'m','n')) return 218;
        if(chin(ch,'_','k')) return 218;
        return 0;
    }
    private int tr_86(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_87(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 88;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_88(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 84;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'_','n')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_89(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_90(char ch)
    {
        if(chin(ch,'A','~')) return 90;
        if(chin(ch,' ','?')) return 90;
        if(chin(ch,'\t','\n')) return 90;
        if(cheq(ch,'@')) return 177;
        return 0;
    }
    private int tr_91(char ch)
    {
        if(chin(ch,'!','9')) return 21;
        if(cheq(ch,' ')) return 91;
        if(cheq(ch,'\n')) return 21;
        if(cheq(ch,'\t')) return 91;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_92(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 87;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_93(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_94(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'o','~')) return 218;
        if(cheq(ch,'n')) return 38;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','m')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_95(char ch)
    {
        if(chin(ch,' ','9')) return 21;
        if(cheq(ch,'\n')) return 95;
        if(cheq(ch,'\t')) return 21;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_96(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 228;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_97(char ch)
    {
        if(chin(ch,'G','H')) return 182;
        if(chin(ch,'u','z')) return 182;
        if(chin(ch,'!','\"')) return 182;
        if(chin(ch,'h','k')) return 182;
        if(cheq(ch,'~')) return 182;
        if(chin(ch,';','>')) return 182;
        if(cheq(ch,'}')) return 65;
        if(cheq(ch,'|')) return 39;
        if(cheq(ch,'{')) return 63;
        if(chin(ch,'A','E')) return 182;
        if(cheq(ch,'t')) return 16;
        if(cheq(ch,'s')) return 71;
        if(cheq(ch,'r')) return 141;
        if(cheq(ch,'q')) return 182;
        if(cheq(ch,'p')) return 53;
        if(cheq(ch,'o')) return 212;
        if(cheq(ch,'n')) return 128;
        if(cheq(ch,'m')) return 105;
        if(cheq(ch,'l')) return 148;
        if(chin(ch,'T','Z')) return 182;
        if(cheq(ch,'g')) return 47;
        if(cheq(ch,'d')) return 78;
        if(cheq(ch,'c')) return 85;
        if(cheq(ch,'b')) return 94;
        if(cheq(ch,'a')) return 192;
        if(chin(ch,'%','\'')) return 182;
        if(cheq(ch,'^')) return 48;
        if(cheq(ch,']')) return 35;
        if(cheq(ch,'\\')) return 149;
        if(cheq(ch,'[')) return 36;
        if(cheq(ch,'S')) return 57;
        if(chin(ch,'M','R')) return 182;
        if(cheq(ch,'L')) return 198;
        if(cheq(ch,'I')) return 37;
        if(chin(ch,'J','K')) return 182;
        if(cheq(ch,'F')) return 44;
        if(cheq(ch,'@')) return 139;
        if(cheq(ch,'?')) return 40;
        if(cheq(ch,':')) return 31;
        if(cheq(ch,'0')) return 68;
        if(cheq(ch,'/')) return 182;
        if(cheq(ch,'.')) return 66;
        if(cheq(ch,'-')) return 6;
        if(cheq(ch,',')) return 182;
        if(chin(ch,'_','`')) return 182;
        if(cheq(ch,'+')) return 43;
        if(cheq(ch,'*')) return 41;
        if(cheq(ch,')')) return 61;
        if(cheq(ch,'(')) return 33;
        if(cheq(ch,'$')) return 55;
        if(cheq(ch,'#')) return 225;
        if(cheq(ch,' ')) return 152;
        if(chin(ch,'1','9')) return 248;
        if(cheq(ch,'\n')) return 156;
        if(chin(ch,'e','f')) return 182;
        if(cheq(ch,'\t')) return 152;
        return 0;
    }
    private int tr_98(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'g','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'f')) return 96;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','e')) return 218;
        return 0;
    }
    private int tr_99(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'u')) return 107;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'v','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'_','t')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_100(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 101;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_101(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'o','~')) return 218;
        if(cheq(ch,'n')) return 106;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','m')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_102(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 13;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_103(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 99;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'_','n')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_104(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_105(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 134;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_106(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 108;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_107(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 26;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_108(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 109;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_109(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 110;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_110(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 161;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_111(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_112(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_113(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'b','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 250;
        if(cheq(ch,'e')) return 214;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'a')) return 92;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'f','s')) return 218;
        return 0;
    }
    private int tr_114(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_115(char ch)
    {
        if(chin(ch,'c','~')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'b')) return 117;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','a')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_116(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','o')) return 218;
        if(chin(ch,'q','~')) return 218;
        if(cheq(ch,'p')) return 34;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_117(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'m')) return 119;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_118(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'u')) return 116;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'v','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'_','t')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_119(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 121;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_120(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 118;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 122;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'b','n')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_121(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 124;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_122(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'m')) return 132;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_123(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 133;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_124(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 126;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_125(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'o','~')) return 218;
        if(cheq(ch,'n')) return 113;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','m')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_126(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 247;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_127(char ch)
    {
        if(chin(ch,'c','~')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'b')) return 123;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','a')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_128(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 125;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 219;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'b','n')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_129(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 102;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_130(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'u')) return 103;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'v','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'_','t')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_131(char ch)
    {
        if(chin(ch,'!','#')) return 131;
        if(chin(ch,'A',']')) return 131;
        if(chin(ch,'\t','\n')) return 90;
        if(cheq(ch,'^')) return 90;
        if(chin(ch,'%','?')) return 131;
        if(cheq(ch,'@')) return 9;
        if(cheq(ch,'$')) return 90;
        if(cheq(ch,' ')) return 90;
        if(chin(ch,'_','~')) return 131;
        return 0;
    }
    private int tr_132(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'m')) return 129;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_133(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'h','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'g')) return 130;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','f')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_134(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'m')) return 135;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_135(char ch)
    {
        if(chin(ch,'c','~')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'b')) return 138;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','a')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_136(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_137(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 145;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_138(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 142;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_139(char ch)
    {
        if(chin(ch,'!','#')) return 50;
        if(chin(ch,';','?')) return 50;
        if(chin(ch,'A',']')) return 50;
        if(chin(ch,'\t','\n')) return 159;
        if(cheq(ch,'^')) return 159;
        if(chin(ch,'%','9')) return 50;
        if(cheq(ch,'@')) return 30;
        if(cheq(ch,':')) return 131;
        if(cheq(ch,'$')) return 159;
        if(cheq(ch,' ')) return 159;
        if(chin(ch,'_','~')) return 50;
        return 0;
    }
    private int tr_140(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'g','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'f')) return 137;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','e')) return 218;
        return 0;
    }
    private int tr_141(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 5;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_142(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 144;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_143(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'m','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'l')) return 146;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','k')) return 218;
        return 0;
    }
    private int tr_144(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 46;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_145(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'u')) return 143;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'v','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'_','t')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_146(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 112;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_147(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_148(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'b','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 98;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 27;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_149(char ch)
    {
        if(chin(ch,'!','#')) return 15;
        if(chin(ch,';',']')) return 15;
        if(cheq(ch,'^')) return 190;
        if(chin(ch,'%','9')) return 15;
        if(cheq(ch,':')) return 75;
        if(cheq(ch,'$')) return 190;
        if(cheq(ch,' ')) return 190;
        if(chin(ch,'_','~')) return 15;
        if(cheq(ch,'\n')) return 21;
        if(cheq(ch,'\t')) return 190;
        return 0;
    }
    private int tr_150(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 158;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_151(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_152(char ch)
    {
        if(chin(ch,'!','9')) return 21;
        if(cheq(ch,' ')) return 91;
        if(cheq(ch,'\n')) return 21;
        if(cheq(ch,'\t')) return 91;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_153(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 155;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_154(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_155(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 157;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_156(char ch)
    {
        if(chin(ch,' ','9')) return 21;
        if(cheq(ch,'\n')) return 95;
        if(cheq(ch,'\t')) return 21;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_157(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 8;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_158(char ch)
    {
        if(chin(ch,'_','w')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'x')) return 151;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'y','~')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_159(char ch)
    {
        if(chin(ch,'A','~')) return 159;
        if(chin(ch,';','?')) return 159;
        if(chin(ch,' ','9')) return 159;
        if(chin(ch,'\t','\n')) return 159;
        if(cheq(ch,'@')) return 209;
        if(cheq(ch,':')) return 90;
        return 0;
    }
    private int tr_160(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'m')) return 100;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_161(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_162(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_163(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 185;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_164(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 187;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_165(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'_','j')) return 218;
        if(chin(ch,'l','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'k')) return 180;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_166(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 164;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_167(char ch)
    {
        if(chin(ch,' ','9')) return 167;
        if(cheq(ch,':')) return 203;
        if(cheq(ch,'\n')) return 21;
        if(cheq(ch,'\t')) return 167;
        if(chin(ch,';','~')) return 167;
        return 0;
    }
    private int tr_168(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'m')) return 154;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_169(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','u')) return 218;
        if(cheq(ch,'v')) return 166;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'w','~')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_170(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 172;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_171(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'m','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'l')) return 169;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','k')) return 218;
        return 0;
    }
    private int tr_172(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_173(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'u')) return 42;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'v','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'_','t')) return 218;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_174(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_175(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'s')) return 236;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','r')) return 218;
        return 0;
    }
    private int tr_176(char ch)
    {
        if(chin(ch,'c','~')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'b')) return 173;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','a')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_177(char ch)
    {
        return 0;
    }
    private int tr_178(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 175;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_179(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_180(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 186;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_181(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 176;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_182(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_183(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'i','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'h')) return 178;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','g')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_184(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_185(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 181;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_186(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'o','~')) return 218;
        if(cheq(ch,'n')) return 162;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','m')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_187(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','-')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'.')) return 183;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'/','9')) return 218;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_188(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'e','~')) return 218;
        if(chin(ch,'_','c')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'d')) return 201;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_189(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_190(char ch)
    {
        if(chin(ch,' ','9')) return 21;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_191(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 193;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_192(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','r')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 163;
        if(cheq(ch,'s')) return 83;
        if(cheq(ch,'m')) return 127;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_193(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'m')) return 136;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_194(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'h','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'g')) return 200;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','f')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_195(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 171;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_196(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','o')) return 218;
        if(chin(ch,'q','~')) return 218;
        if(cheq(ch,'p')) return 170;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_197(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_198(char ch)
    {
        if(chin(ch,';','@')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'B',']')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,'A')) return 67;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_199(char ch)
    {
        if(chin(ch,'!','#')) return 199;
        if(chin(ch,';',']')) return 199;
        if(cheq(ch,'^')) return 167;
        if(chin(ch,'%','9')) return 199;
        if(cheq(ch,':')) return 234;
        if(cheq(ch,'$')) return 167;
        if(cheq(ch,' ')) return 167;
        if(chin(ch,'_','~')) return 199;
        if(cheq(ch,'\n')) return 21;
        if(cheq(ch,'\t')) return 167;
        return 0;
    }
    private int tr_200(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_201(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 197;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_202(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 189;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_203(char ch)
    {
        if(chin(ch,' ','~')) return 203;
        if(cheq(ch,'\t')) return 203;
        return 0;
    }
    private int tr_204(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','o')) return 218;
        if(chin(ch,'q','~')) return 218;
        if(cheq(ch,'p')) return 206;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_205(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_206(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 89;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_207(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'h','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'g')) return 213;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','f')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_208(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'q','r')) return 218;
        if(chin(ch,'_','o')) return 218;
        if(cheq(ch,'s')) return 195;
        if(cheq(ch,'p')) return 29;
        if(chin(ch,'t','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_209(char ch)
    {
        if(chin(ch,' ','9')) return 21;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,';','~')) return 21;
        return 0;
    }
    private int tr_210(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 215;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_211(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'%','/')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'0','9')) return 3;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_212(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','o')) return 218;
        if(chin(ch,'q','~')) return 218;
        if(cheq(ch,'p')) return 64;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_213(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 202;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_214(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_215(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'y')) return 204;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'z','~')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','x')) return 218;
        return 0;
    }
    private int tr_216(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'o','~')) return 218;
        if(cheq(ch,'n')) return 239;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','m')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_217(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'j','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'i')) return 220;
        if(chin(ch,'_','h')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_218(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_219(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'n','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'m')) return 222;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(chin(ch,'_','l')) return 218;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_220(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'o','~')) return 218;
        if(cheq(ch,'n')) return 194;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','m')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_221(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 216;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_222(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 86;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_223(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','-')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'/')) return 218;
        if(cheq(ch,'.')) return 4;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'0','9')) return 223;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_224(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'e','~')) return 218;
        if(chin(ch,'_','c')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'d')) return 221;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_225(char ch)
    {
        if(chin(ch,'!','#')) return 199;
        if(chin(ch,';',']')) return 199;
        if(cheq(ch,'^')) return 167;
        if(chin(ch,'%','9')) return 199;
        if(cheq(ch,':')) return 234;
        if(cheq(ch,'$')) return 167;
        if(cheq(ch,' ')) return 167;
        if(chin(ch,'_','~')) return 199;
        if(cheq(ch,'\n')) return 21;
        if(cheq(ch,'\t')) return 167;
        return 0;
    }
    private int tr_226(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 217;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_227(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 224;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_228(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_229(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'m','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'l')) return 232;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','k')) return 218;
        return 0;
    }
    private int tr_230(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_231(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'a')) return 233;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(chin(ch,'_','`')) return 218;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'b','~')) return 218;
        return 0;
    }
    private int tr_232(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'m','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'l')) return 93;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','k')) return 218;
        return 0;
    }
    private int tr_233(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 22;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_234(char ch)
    {
        if(chin(ch,'!','#')) return 234;
        if(cheq(ch,'^')) return 203;
        if(chin(ch,'%',']')) return 234;
        if(cheq(ch,'$')) return 203;
        if(cheq(ch,' ')) return 203;
        if(chin(ch,'_','~')) return 234;
        if(cheq(ch,'\t')) return 203;
        return 0;
    }
    private int tr_235(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','-')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'/')) return 218;
        if(cheq(ch,'.')) return 4;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'0','9')) return 235;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_236(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'_','j')) return 218;
        if(chin(ch,'l','~')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'k')) return 238;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_237(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 147;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_238(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 229;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_239(char ch)
    {
        if(chin(ch,'d','~')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','b')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'c')) return 237;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_240(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'p','~')) return 218;
        if(cheq(ch,'o')) return 231;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'_','n')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_241(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 246;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_242(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_243(char ch)
    {
        if(chin(ch,'p','~')) return 21;
        if(cheq(ch,'o')) return 74;
        if(chin(ch,' ','9')) return 21;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,';','n')) return 21;
        return 0;
    }
    private int tr_244(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'t')) return 249;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 218;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(chin(ch,'_','s')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_245(char ch)
    {
        if(chin(ch,'_','q')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'r')) return 168;
        if(chin(ch,'s','~')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_246(char ch)
    {
        if(chin(ch,'d','~')) return 218;
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'_','b')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'c')) return 210;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_247(char ch)
    {
        if(cheq(ch,'t')) return 243;
        if(chin(ch,' ','9')) return 21;
        if(chin(ch,'\t','\n')) return 21;
        if(chin(ch,'u','~')) return 21;
        if(chin(ch,';','s')) return 21;
        return 0;
    }
    private int tr_248(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','-')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'/')) return 218;
        if(cheq(ch,'.')) return 4;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'0','9')) return 223;
        if(chin(ch,'_','~')) return 218;
        return 0;
    }
    private int tr_249(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 207;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_250(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,'f','~')) return 218;
        if(chin(ch,'_','d')) return 218;
        if(chin(ch,';',']')) return 218;
        if(cheq(ch,'e')) return 245;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        return 0;
    }
    private int tr_251(char ch)
    {
        if(chin(ch,'!','#')) return 218;
        if(chin(ch,';',']')) return 218;
        if(chin(ch,'\t','\n')) return 21;
        if(cheq(ch,'^')) return 21;
        if(chin(ch,'%','9')) return 218;
        if(cheq(ch,':')) return 2;
        if(cheq(ch,'$')) return 21;
        if(cheq(ch,' ')) return 21;
        if(chin(ch,'_','~')) return 218;
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
