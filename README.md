### Description ###

Copper is a Java-based integrated scanner and parser generator developed by the [Minnesota Extensible Language Tools (MELT) research group](http://melt.cs.umn.edu) at the University of Minnesota with assistance from the National Science Foundation, IBM, the McKnight Foundation, and [Adventium Labs](http://www.adventiumlabs.com). It serves as the parsing back-end of [Silver](http://melt.cs.umn.edu/silver), another MELT tool.

Copper employs the LALR(1) parsing algorithm in conjunction with a modified scanning algorithm, [context-aware scanning](http://www.umsec.umn.edu/publications/Context-Aware-Scanning-Parsing-Extensible-Language), which uses parsing context to resolve lexical ambiguities. This allows for more declarative parser specifications, especially of embedded and extensible languages.

It also provides [an analysis](http://www.umsec.umn.edu/publications/Verifiable-Composition-Deterministic-Grammars) for use with extensible languages. The pass/fail analysis is applied independently to each language extension, and any combination of passing extensions is guaranteed to compile without parse-table conflicts. This lets any end-user pick and choose extensions in the same manner as libraries.

### Downloads and documentation ###

This site offers the following downloads:

  * JARs for Copper versions 0.6.0 and greater (see [CHANGELOG.md](CHANGELOG.md) for detailed Copper version information).
  * Copper's source code, including [example parser specifications](src/edu.umn.cs.melt.copper.test/resources/test/grammars)
  * A user manual (in [online](doc/manual/CopperUserManual.md) and PDF formats) intended for those who use Copper directly.
  * Javadoc for the APIs of Copper's parser compiler and parsers, intended primarily for developers who wish to use Copper as a back-end.

[MELT's Copper website](http://melt.cs.umn.edu/copper) contains:

  * Downloads for earlier versions of Copper.
  * [An online version of Copper's Javadoc](http://melt.cs.umn.edu/copper/current/javadoc).

### Papers ###

  * Van Wyk, E. and Schwerdfeger, A. _[Context-Aware Scanning for Parsing Extensible Languages](http://www.umsec.umn.edu/publications/Context-Aware-Scanning-Parsing-Extensible-Language)_. In _Proceedings of the Intl. Conf. on Generative Programming and Component Engineering_ (GPCE). ACM Press, October 2007.
  * Schwerdfeger, A. and Van Wyk, E. _[Verifiable Composition of Deterministic Grammars](http://www.umsec.umn.edu/publications/Verifiable-Composition-Deterministic-Grammars)_. In _Proceedings of the ACM SIGPLAN Conference on Programming Language Design and Implementation_ (PLDI). ACM Press, June 2009.
  * Schwerdfeger, A. and Van Wyk, E. _[Verifiable Parse Table Composition for Deterministic Parsing](http://www.umsec.umn.edu/publications/Verifiable-Parse-Table-Composition-Deterministic-P)_. In _Proceedings of the International Conference on Software Language Engineering_. LNCS, Springer-Verlag, February 2010.
  * Schwerdfeger, A. _[Context-Aware Scanning and Determinism-Preserving Grammar Composition, in Theory and Practice](http://purl.umn.edu/95605)_. Ph.D. thesis, University of Minnesota, July 2010.
  