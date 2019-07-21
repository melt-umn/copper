Build instructions
==================

JARs for Copper may be built via the Maven model located in the repository's
root directory, by building the `package` goal:

 `% mvn package`

This will place two JARs into the `target` directory:

* `CopperRuntime.jar` contains only the classes necessary to run a main-line
  Copper parser. It does not include the parser generator, or code to run
  parsers based on experimental or deprecated engines.
* `CopperCompiler.jar` contains all Copper's classes.

Once the JARs are built, you need only put one of them in your
classpath. Copper as built has no dependencies outside of the Java 7
standard class library.

If you will only be running Copper parsers (e.g., if you are running an
application that contains a Copper parser), you may use either JAR. If
you are developing Copper parsers or using experimental or deprecated
features, you must use `CopperCompiler.jar`.
