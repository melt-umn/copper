===================
==== Operation ====
===================

The Copper parser generator may be invoked in one of three ways: on the
command line, through its ANT task, or through its Java API.

Documentation of the ANT task and Java API may be found in the Copper Javadoc
(see next section). Executing Copper from the command line with the "-?" switch
will bring up the usage information for Copper's command-line interface.

The parser generator is invoked from the command line via its executable JAR:

% java -jar CopperCompiler.jar

The most basic use

% java -jar CopperCompiler.jar -o parser-class.java parser-spec

This will take in a parser specification in a single file, parser-spec, and
output the result to parser-class.java.

=================
==== Javadoc ====
=================

Javadoc for Copper's public API, including its ANT task and method calls for
parser compilation, may also be built via the root directory ANT script by
building the "javadoc" target:

% ant javadoc

This will deposit Javadoc in the doc/javadoc directory.

==================
==== Examples ====
==================

Example parser specifications, along with an ANT script to build them, are
located in the tests/ directory.
