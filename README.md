DJBDD
=============

What's this?
-------------
A Java 7 BDD package with the GPL 3 (with classpath linking  exception) license.

This package provides a Binary Decision Diagram library you can use to
make operations with boolean logical formulas and study its propierties.

Introduction
-------------
A Binary Decision Diagram is a complete truth table of a boolean expression
in a reduced graph form. See http://en.wikipedia.org/wiki/Binary_decision_diagram.

This library provides all the operations you need to work with them.

Examples
-------------
### Code examples ###
Nowadays there is no too much example code (I plan to update the library in the future).
Look the class Tester class in package djbdd.test, its full of examples.

```java
// These are the boolean variables
String[] variables={"a", "b", "c", "d", "e", "f"};

// You always have to initialize the BDD system before you create any
// BDD object. Be careful with that.
BDD.init(variables);

// The functions are specified as boolean expressions with Java syntax
// adding the operators -> (implication) and <-> (double implication)
String function = "(a && b) || (c && d)";

// With the current implementation, you can only specify
// a static variable ordering, that is, in our example
// the variable ordering of array of strings 'variables'.

// Construction of a new BDD
BDD bdd = new BDD(function);

// Printing the BDD in the standard output
bdd.print();

// You can print it as a image PNG using a dot library
Printer.printBDD(bdd1, "bdd_"+bdd1.size());

// Other BDD
String function2 = "(a && e) || f"
BDD bdd2 = new BDD(function2);
bdd2.print();

// Operations with BDDs

// This BDD is the logical AND between bdd and bdd2
BDD bdd3 = bdd.apply("and", bdd2);

// This BDD is the logical OR between bdd and bdd2
BDD bdd4 = bdd.apply("or", bdd2);

// If you think you can have few free memory, you would have to free it
// calling to
BDD.gc();

// Or, you can call the garbage collector thread (explained later)

```

### Complete tests ###
The main program of this library is full of examples.
The all-in-one jar version is located in **store/DJBDD.jar**,
the dependant jar one is in  **dist/DJBDD.jar**.

You can run it without arguments to see the options.

#### Options
##### BDD print as image

```bash
java -jar DJBDD.jar --image --<format> <file>
```

##### BDD printing

```bash
java -jar DJBDD.jar --print --<format> <file>
```
Prints a BDD in the standard output.

##### BDD probability computation

```bash
java -jar DJBDD.jar --prob --<format> <file>
```
Computes the probabilities of the variables of a boolean formula using Binary Decision Diagrams.

#### Formats allowed

- **dimacs**: Dimacs CNF format. See http://www.cs.ubc.ca/~hoos/SATLIB/Benchmarks/SAT/satformat.ps or http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html.
- **she**: Steven She file. See https://code.google.com/p/linux-variability-analysis-tools/
- **cstyle**: C-style boolean expression preceded by a line with all variables separated by commas. For example:
  - a && !b
  - a -> (!b && (c || d))
  - a <-> (b && !c)
  - a != (b && !c)
- **djbdd**: DJBDD file. Is a textual file format that contains the vertices of the BDD as a list. So, the loading time is smaller than other methods.

#### Variable naming notes

**IMPORTANT**: for each variable there can no be any other that contains it as substring from the left.
That is, if we have a variable with the name 'x1' we cannot use other variable with the name 'x11'.
It's not in my future plans to change that, so name your variables with names like:
- {x1}
- {x11}
- {x12}

#### Example source data
Directory **data** has some examples of each format (look the extension).


Features
-------------
### Use of weak references ###
This package uses the WeakReference class introduced in Java 7. This way
I didn't have to implement a special garbage collector but use the Hotspot one.

You can erase the weak references using the method of TableT gc. There is a way to use
parallel garbage collection calling 

```java
// Maybe in the future we'll change that to something easier.
GCThread gcCollector = new GCThread();
gcCollector.start();
/*
	Your BDD code
*/
gcCollector.end();
```

### Shared hash table ###
All BDDs use the same hash table, sharing the vertices and subgraphs.
Each BDD has one root vertex though.

### Rich I/O API ###
This library provides method to load logical clausules in DIMACS format
and Java native, includen implication and double implication operators.

### Memory efficient ###
This library uses Java references between vertices and some tables of medium size.

### Vertices grouped by levels ###
You can access the vertices that has each variable in an efficient way. This will be used for BDD-reducing algorithms.

### Operations implemented ###
- Apply [6] [3].
- Restrict. [2].
- Swapping of two variables. [5].

### TODOs ###
- Include dynamic variable ordering. The swap operation is ready.
- Include a parallel apply.
- Review this README.


Bibliography
-------------
[1] Symbolic Boolean Manipulation with Ordered Binary Decision Diagrams, Randal E. Bryant. Carnegie Mellon University.

[2] Binary Decision Diagrams. Fabio SOMENZI. Department of Electrical and Computer Engineering. University of Colorado at Boulder.

[3] Efficient implementation of a BDD package, Karl S. Brace, Richard L. Rudell, Randal E. Bryant. 

[4] Implementation of an Efﬁcient Parallel BDD Package. Tony Stornetta, Forrest Brewer.

[5] Incremental  Reduction of Binary Decision Diagrams. R. Jacobi, N. Calazans, C. Trullemans.

[6] An Introduction to Binary Decision Diagrams. Henrik Reif Andersen.

FAQ
-------------
### I don't understand what is a BDD ###
OK. The concept is not easy, read the Wikipedia page about them (http://en.wikipedia.org/wiki/Binary_decision_diagram) and later the reference [1] and [2].

### Do you use complement arcs? ###
No. I don't use them. I understand they can reduce the size of the graph, but I wanted to make a pure BDD library. Maybe I could include them in some future version.

### How can I import this project to my IDE? ###
I've used Netbeans (yeah, I know it is ancient but works for me). You should be able to open with whatever IDE you use.

### Can I use this library in Java5 or Java6? ###
No. I'm afraid weak references were included in Java7 so, I'm sorry but no, you can't.

### Are there some code examples? ###
I'm working on that. Give some time.

### I'm running the examples (or my custom code) and throws a exception ###
It depends on the exception type, but my money is on a memory-related exception.
This library is memory greedy, use BDD.gc or the threaded garbage collector or help me
to implement some reduction method on the vertex table :)

### Why don't you use dynamic variable ordering? ###
I'm implementing that, but I'm finding some problems that are slowing me down.

### Your code is inefficient/wrong/could be improved ###
I accept suggestions, critiques and comments you want to communicate me. 

### Why don't you use whatever method? ###
Maybe I don't know it, please send me an email asking me and pointing me to a paper where it is explained. I will see that.

### What is the licence of this code? ###
This code is GPL3 with classpath linking exception. That's the same license than the OpenJDK7 one.

### Do you answer emails? ###
Of course I will answer questions, suggestions and comments about this library.

### Who are you? ###
I'm Diego J., my email is die_gojr_om-erol-op_ez AT g-m-a-i-l.com (erase - and _ and replace AT with @). I'm from Spain and this is part of my master's final thesis.
