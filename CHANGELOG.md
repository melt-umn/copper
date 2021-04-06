### Versions ###

---

# 1.0 #

---

## 1.0.0 ##

_Released April 6, 2021._

A few bug fixes and incremental improvements.

### Bug fixes ###

* Copper no longer enters into an infinite loop on grammars that produce LR DFAs with loop transitions.
* Copper now gracefully handles character-set regular expressions with empty ranges.

### Improvements ###

* The LR "context sets" of a grammar (first, follow, and nullable) are now included in XML dumps.
* The XML skin and Java API now support specially labeled disambiguation functions that can be appied to any subset of its members, provided that all such disambiguation functions have disjoint member sets.
* Disambiguation functions may now be expressly labeled as "glue" functions that resolve ambiguities involving an extension's marking terminal.

### Build changes ###

* Copper now requires Java version 7 to build and run.

---

# 0.8 #

---

## 0.8.0 ##

_Released November 28, 2018._

Some bug fixes and minor improvements, coupled with a significant refactoring of the build system and major new experimental functionality.

### Bug fixes ###

* Lexical ambiguities between layout terminals are now properly detected.
* Several confusions of singular and plural nouns in the final report generator have been corrected.
* The "real" character location reported at a syntax error is now consistent with the reported line-and-column location.
* Parse errors in XML specs are handled without an exception.
* The `lexeme` variable in terminal semantic actions is now generated correctly.

### Improvements ###

* XML parser dumps are now produced using a more efficient SAX process.
* Modular determinism analysis reports have been made more concise by
  showing only lookahead spillage that does not have the same cause as
  some follow spillage.
* Extension grammars are no longer restricted to only one bridge production.
* A new attribute `interfaceNames` has been added on parser specs, allowing
  them to specify a list of Java interfaces that their classes are to implement.
* The final report displayed at the end of parser compilation has been restructured to a more intuitive layout.

### Build refactoring ###

* Copper is now built using [Apache Maven](http://maven.apache.org), with separate bundles for Copper's compile time, runtime, and ANT task.
* A regression test harness has been introduced, and the example grammars moved [into it](https://github.com/melt-umn/copper/tree/master/src/edu.umn.cs.melt.copper.test/resources/test/grammars).
* Copper now requires Java version 6 to build and run.

### New experimental functionality ###

* Two new pipelines, `fragment` and `fragmentCompose`, have been added to implement the ideas of runtime parser composition laid out in our paper, _[Verifiable Parse Table Composition for Deterministic Parsing](http://www.umsec.umn.edu/publications/Verifiable-Parse-Table-Composition-Deterministic-P)_.

---

# 0.7 #

---

## 0.7.2 ##

_Released March 17, 2015._

A maintenance release:

  * Removed Mercurial dependencies in the build process to accommodate the project's hosting on GitHub.
  * Fixed bit-rot in the ANT task, which had relied on an undocumented feature in an older version of ANT.
  * Included the LaTeX source of Copper's user manual in the distribution.

## 0.7.1 ##

_Released November 17, 2013._

A mix of bug fixes and minor improvements.

### Bug fixes ###

  * The CUP skin again assigns production precedence (used to resolve reduce-reduce conflicts) such that productions specified earlier in the file have higher precedence.
  * "Nonterminal nonterminal" errors (nonterminal with no terminal derivations) are again displayed as errors rather than warnings.
  * Shift-reduce conflicts between one operator with precedence and one without are now left unresolved, rather than being automatically resolved in favor of the operator with precedence.
  * An error message is now displayed in all cases when a non-existent file is specified as input.
  * A scanner-generator bug causing some spurious reports of lexical ambiguities has been corrected.
  * A bug in the modular determinism analysis causing some spurious reports of follow spillage has been corrected.
  * An off-by-one error, which caused parsers to ignore the last character of files whose size is an exact multiple of 512, has been corrected.
  * Carriage returns (`\r`) are now valid whitespace in the CUP skin grammar.

### Improvements ###

  * The parser compiler's final report now includes separate counts of shift-reduce and reduce-reduce conflicts.
  * A new command-line switch, `-avoidRecompile`, has been introduced. When specified, it attempts to avoid unnecessary recompilation of a parser, by suppressing compilation if none of the designated input files have a later modification time than the designated output file.
  * The reporting of syntax errors has been improved. Previously errors were reported from the location following the last valid parser shift. They are now reported from the location following the last valid scan (i.e., after any valid layout tokens occurring before the unexpected input).


## 0.7.0 ##

_Released December 28, 2012._

A continuation of the re-design started in Copper 0.6, Copper 0.7 features:

  * A re-written compile-time that works more than twice as fast as, and in less than half the memory of, Copper 0.6's.
  * A re-written ANT task with access to all of Copper's command-line parameters.
  * Full integration of the new Java API into the parser generator.
  * Further optimizations to the parser runtime.
  * A sound implementation of the modular determinism analysis.
  * Unicode support in the CUP skin.
  * Improved error reporting.
  * Expanded support for multiple input skins, output targets, and compilation pipelines.

The new compile-time supports only the CUP skin and the Copper 0.6 XML schema for input, and the `single` parse engine for output. The old compile-time has been retained in the form of the `legacy` pipeline, in which the experimental parse engines are still supported.

**N.B.:** _Parsers and parser specifications written for earlier versions of Copper are not guaranteed to work correctly with Copper 0.7._


---

# 0.6 #

---

## 0.6.2 ##

_Released December 28, 2012._

A bug-fix release:

  * Using the empty string as a terminal regex now works properly.

---

## 0.6.1 ##

_Released May 16, 2012._

A bug-fix release:

  * The `-v` and `-vv` switches now work correctly in conjunction with the `-compose` switch.
  * Calling the `parse()` function repeatedly on a single parser object now works correctly, although parsers are still not reentrant.
  * Specifying more than one anonymous right-hand-side symbol in a production was being spuriously disallowed.
  * The parser for the XML specification format was failing to read any semantic-action and other code blocks in certain runtime environments.

Additionally, Copper's modular determinism analysis is now able to be run on grammars specified in the XML format.

Copper 0.6.1 will serve as the basis for the next major version of Copper, 0.7.


---

## 0.6.0 ##

_Released December 21, 2011._

A re-design of Copper featuring numerous improvements over Copper 0.5, including:

  * Much shorter parser compilation and run times.
  * A reworked (and fully documented) front end with parser compilation accessible by the command line, a Java API, or an ANT task.
  * A new XML-based parser specification format.
  * Syntax errors from parsers now contain structured information rather than just a string.

**N.B.:** _Parsers and parser specifications written for earlier versions of Copper are not guaranteed to work correctly with Copper 0.6. Specifically, Copper 0.6 is incompatible with earlier versions of [Silver](http://melt.cs.umn.edu/silver) built using Copper 0.5._


---

# 0.5 #

_Released October 9, 2008._

Copper 0.5 is no longer maintained, and its source code is available only by request. Executable JARs and documentation for Copper 0.5 may be downloaded at [MELT's Copper website](http://melt.cs.umn.edu).
