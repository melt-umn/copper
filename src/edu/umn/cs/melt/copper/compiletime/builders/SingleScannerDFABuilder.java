package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.BitSet;
import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CharacterSetRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ChoiceRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ConcatenationRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.EmptyStringRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.KleeneStarRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.MacroHoleRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.RegexBeanVisitor;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

public class SingleScannerDFABuilder
{
	private ParserSpec spec;
	
	private TransitionLabelCalculator transitionLabelCalculator;
	private AutomatonGenerator automatonGenerator;

	private GeneralizedNFA nfa;
	
	private SingleScannerDFABuilder(ParserSpec spec)
	{
		this.spec = spec;
		
		this.transitionLabelCalculator = new TransitionLabelCalculator();
		this.automatonGenerator = new AutomatonGenerator();
	}
	
	public static GeneralizedDFA build(ParserSpec spec)
	{
		return new SingleScannerDFABuilder(spec).buildScannerDFA();
	}
	
	private GeneralizedDFA buildScannerDFA()
	{
		// Collect the sub-NFAs for all regexes.
		HashSet<SetOfCharsSyntax> allCharRanges = new HashSet<SetOfCharsSyntax>();
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			if(t == spec.getEOFTerminal()) continue;
			allCharRanges.addAll(spec.t.getRegex(t).acceptVisitor(transitionLabelCalculator));
		}
		nfa = new GeneralizedNFA(spec.terminals.cardinality(),allCharRanges.size());
		BitSet allStartStates = new BitSet();
		// Add accept symbols and gather start states for individual regexes.
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			if(t == spec.getEOFTerminal()) continue;
			Pair<Integer,BitSet> states = spec.t.getRegex(t).acceptVisitor(automatonGenerator);
			allStartStates.set(states.first());
			for(int j = states.second().nextSetBit(0);j >= 0;j = states.second().nextSetBit(j+1))
			{
				nfa.addAcceptSymbol(j,t);
			}
		}
		// Create a scanner-wide start state and add epsilon transitions
		// into the start states for all the individual regexes.
		int newStartState = nfa.addState();
		nfa.addEpsilonTransitions(newStartState,allStartStates);

		// DEBUG-X-BEGIN
		//System.err.println("\n====== NFA ======\n");
		//System.err.println("Start state: " + newStartState);
		//System.err.println(nfa.toString());
		// DEBUG-X-END		
		
		// Convert the NFA to a DFA.
		GeneralizedDFA dfa = nfa.determinize(newStartState);
		
		return dfa;
	}
	
	
	
	private class TransitionLabelCalculator implements RegexBeanVisitor<HashSet<SetOfCharsSyntax>, RuntimeException>
	{
		@Override
		public HashSet<SetOfCharsSyntax> visitChoiceRegex(ChoiceRegex bean)
		throws RuntimeException
		{
			HashSet<SetOfCharsSyntax> rv = new HashSet<SetOfCharsSyntax>();
			for(Regex sexp : bean.getSubexps()) rv.addAll(sexp.acceptVisitor(this));
			return rv;
		}

		@Override
		public HashSet<SetOfCharsSyntax> visitConcatenationRegex(ConcatenationRegex bean)
		throws RuntimeException
		{
			HashSet<SetOfCharsSyntax> rv = new HashSet<SetOfCharsSyntax>();
			for(Regex sexp : bean.getSubexps()) rv.addAll(sexp.acceptVisitor(this));
			return rv;
		}

		@Override
		public HashSet<SetOfCharsSyntax> visitKleeneStarRegex(KleeneStarRegex bean)
		throws RuntimeException
		{
			return bean.getSubexp().acceptVisitor(this);
		}

		@Override
		public HashSet<SetOfCharsSyntax> visitEmptyStringRegex(EmptyStringRegex bean)
		throws RuntimeException
		{
			return new HashSet<SetOfCharsSyntax>();
		}

		@Override
		public HashSet<SetOfCharsSyntax> visitCharacterSetRegex(CharacterSetRegex bean,SetOfCharsSyntax chars)
		throws RuntimeException
		{
			HashSet<SetOfCharsSyntax> rv = new HashSet<SetOfCharsSyntax>();
			rv.add(chars);
			return rv;
		}

		@Override
		public HashSet<SetOfCharsSyntax> visitMacroHoleRegex(MacroHoleRegex bean)
		throws RuntimeException
		{
			throw new UnsupportedOperationException("Undefined macro '" + bean.getMacroName() + "'");		
		}
	}
	
	private class AutomatonGenerator implements RegexBeanVisitor<Pair<Integer,BitSet>, RuntimeException>
	{
		@Override
		public Pair<Integer, BitSet> visitChoiceRegex(ChoiceRegex bean)
		throws RuntimeException
		{
			// Add a new start state.
			int newStartState = nfa.addState();
			BitSet accepts = new BitSet();
			Pair<Integer,BitSet> subs;
			// For each constituent of the choice:
			for(Regex sexp : bean.getSubexps())
			{
				// Generate its states.
				subs = sexp.acceptVisitor(this);
				// Make all of its accept states into accept states of
				// this automaton.
				accepts.or(subs.second());
				// Add an epsilon transition from this automaton's
				// start state to the constituent's.
				nfa.addEpsilonTransition(newStartState,subs.first());
			}
			return Pair.cons(newStartState,accepts);
		}

		@Override
		public Pair<Integer, BitSet> visitConcatenationRegex(ConcatenationRegex bean)
		throws RuntimeException
		{
			int newStartState = -1;
			Pair<Integer,BitSet> currentSub = null,prevSub = null;
			// For each constituent of the concatenation:
			for(Regex subexp : bean.getSubexps())
			{
				// Generate its states.
				currentSub = subexp.acceptVisitor(this);
				// If it is not the first constituent, add epsilon transitions
				// from all the accept states of the previous constituent to
				// the start state of this constituent.
				if(prevSub != null)
				{
					for(int i = prevSub.second().nextSetBit(0);i >= 0;i = prevSub.second().nextSetBit(i+1))
					{
						nfa.addEpsilonTransition(i,currentSub.first());
					}
				}
				else newStartState = currentSub.first();
				
				prevSub = currentSub;
			}
			return Pair.cons(newStartState,currentSub.second());
		}

		@Override
		public Pair<Integer, BitSet> visitKleeneStarRegex(KleeneStarRegex bean)
		throws RuntimeException
		{
			int newStartState = nfa.addState();
			BitSet newAccepts = new BitSet(newStartState + 1);
			newAccepts.set(newStartState);
			Pair<Integer,BitSet> sub = bean.getSubexp().acceptVisitor(this);
			nfa.addEpsilonTransition(newStartState,sub.first());
			for(int i = sub.second().nextSetBit(0);i >= 0;i = sub.second().nextSetBit(i+1))
			{
				nfa.addEpsilonTransition(i,newStartState);
			}
			return Pair.cons(newStartState,newAccepts);
		}

		@Override
		public Pair<Integer, BitSet> visitEmptyStringRegex(EmptyStringRegex bean)
		throws RuntimeException
		{
			// Add exactly one new state, which is both the start and accept state.
			int newState = nfa.addState();
			BitSet accepts = new BitSet(newState + 1);
			accepts.set(newState);
			return Pair.cons(newState,accepts);
		}

		@Override
		public Pair<Integer, BitSet> visitCharacterSetRegex(CharacterSetRegex bean, SetOfCharsSyntax chars)
		throws RuntimeException
		{
			int startState = nfa.addState();
			int acceptState = nfa.addState();
			nfa.addTransition(chars,startState,acceptState);
			BitSet accepts = new BitSet(acceptState + 1);
			accepts.set(acceptState);
			return Pair.cons(startState,accepts);
		}

		@Override
		public Pair<Integer, BitSet> visitMacroHoleRegex(MacroHoleRegex bean)
		throws RuntimeException
		{
			throw new UnsupportedOperationException("Undefined macro '" + bean.getMacroName() + "'");		
		}
		
	}
}
