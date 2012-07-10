package edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.xml;

import java.util.Hashtable;

class XMLSkinElements
{

	static enum Type
	{
		BRIDGE_PRODUCTIONS_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "BridgeProductions"; } },
		CHARACTER_RANGE_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "CharacterRange"; } },
		CHARACTER_SET_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "CharacterSet"; } },
		CHOICE_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Choice"; } },
		CLASS_AUXILIARY_CODE_ELEMENT			{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ClassAuxiliaryCode"; } },
		CLASS_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Class"; } },
		CLASS_NAME_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ClassName"; } },
		CODE_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Code"; } },
		CONCATENATION_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Concatenation"; } },
		COPPER_SPEC_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "CopperSpec"; } },
		DECLARATIONS_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Declarations"; } },
		DEFAULT_TERMINAL_CODE_ELEMENT			{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "DefaultTerminalCode"; } },
		DEFAULT_PRODUCTION_CODE_ELEMENT			{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "DefaultProductionCode"; } },
		DISAMBIGUATE_TO_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "DisambiguateTo"; } },
		DISAMBIGUATION_FUNCTION_ELEMENT			{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "DisambiguationFunction"; } },
		DOMINATES_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Dominates"; } },
		EMPTY_STRING_REGEX_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "EmptyString"; } },
		EXTENSION_GRAMMAR_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ExtensionGrammar"; } },
		EXTENSION_GRAMMARS_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ExtensionGrammars"; } },
		EXTENDED_PARSER_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ExtendedParser"; } },
		HOST_GRAMMAR_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "HostGrammar"; } },
		GRAMMARS_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Grammars"; } },
		GRAMMAR_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Grammar"; } },
		GRAMMAR_REF_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "GrammarRef"; } },
		IN_CLASSES_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "InClasses"; } },
		KLEENE_STAR_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "KleeneStar"; } },
		LAYOUT_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Layout"; } },
		LEFT_ASSOCIATIVE_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "LeftAssociative"; } },
		LHS_ELEMENT								{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "LHS"; } },
		MACRO_REF_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "MacroRef"; } },
		MARKING_TERMINALS_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "MarkingTerminals"; } },
		MEMBERS_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Members"; } },
		NON_ASSOCIATIVE_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "NonAssociative"; } },
		NONTERMINAL_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Nonterminal"; } },
		NONTERMINAL_REF_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "NonterminalRef"; } },
		OPERATOR_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Operator"; } },
		OPERATOR_CLASS_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "OperatorClass"; } },
		OPERATOR_CLASS_REF_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "OperatorClassRef"; } },
		PACKAGE_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Package"; } },
		PARSER_ATTRIBUTE_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ParserAttribute"; } },
		PARSER_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Parser"; } },
		PARSER_INIT_CODE_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ParserInitCode"; } },
		POST_PARSE_CODE_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "PostParseCode"; } },
		PP_ELEMENT								{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "PP"; } },
		PREAMBLE_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Preamble"; } },
		PRECEDENCE_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Precedence"; } },
		PREFIX_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Prefix"; } },
		PRODUCTION_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Production"; } },
		REGEX_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Regex"; } },
		RHS_ELEMENT								{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "RHS"; } },
		RIGHT_ASSOCIATIVE_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "RightAssociative"; } },
		SEMANTIC_ACTION_AUXILIARY_CODE_ELEMENT	{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "SemanticActionAuxiliaryCode"; } },
		SINGLE_CHARACTER_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "SingleCharacter"; } },
		START_LAYOUT_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "StartLayout"; } },
		START_SYMBOL_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "StartSymbol"; } },
		SUBMITS_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Submits"; } },
		TERMINAL_CLASS_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "TerminalClass"; } },
		TERMINAL_CLASS_REF_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "TerminalClassRef"; } },
		TERMINAL_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Terminal"; } },
		TERMINAL_REF_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "TerminalRef"; } },
		TYPE_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Type"; } };
		
		public abstract String getNamespace();
		public abstract String getName();
	
		public String toString() { return getName(); }
	}

	static final String COPPER_NAMESPACE = "http://melt.cs.umn.edu/copper/xmlns";
	
	static Hashtable<String,Type> nodeTypes;
	
	static
	{
		nodeTypes = new Hashtable<String,Type>();
		for(Type t : Type.values()) nodeTypes.put(t.getName(),t);
	}

}
