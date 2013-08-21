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

// You can specify one variable ordering
String[] variable_ordering = {"a", "b", "c", "d", "e", "f"};

// Construction of a new BDD
BDD bdd = new BDD(function, variable_ordering);

// Printing the BDD in the standard output
bdd.print();

// You can print it as a image PNG using a dot library
Printer.printBDD(bdd1, "bdd_"+bdd1.size()+"_"+bdd.variable_ordering.toString());

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
The main program of this library is full of examples:

#### BDD printing #### 

```bash
java -jar BDD.jar --print --<format> <file>
```

Formats allowed: 

- **dimacs**: Dimacs CNF format. See http://www.cs.ubc.ca/~hoos/SATLIB/Benchmarks/SAT/satformat.ps or http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html.
- **she**: Steven She file. See https://code.google.com/p/linux-variability-analysis-tools/
- **cstyle**: C-style boolean expression preceded by a line with all variables separated by commas
- **djbdd**: DJBDD file. Don't see anything because there are no documentation yet.
Directory data has some examples of each format (look the extension).


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
- Apply
- Restrict

### TODOs ###
- Include dynamic variable ordering.
- Include a parallel apply.
- Review this README.


Bibliography
-------------
[1] Symbolic Boolean Manipulation with Ordered Binary Decision Diagrams, Randal E. Bryant. Carnegie Mellon University.

[2] Binary Decision Diagrams. Fabio SOMENZI. Department of Electrical and Computer Engineering. University of Colorado at Boulder.

[3] Efficient implementation of a BDD package, Karl S. Brace, Richard L. Rudell, Randal E. Bryant. 

[4] Implementation of an Efﬁcient Parallel BDD Package. Tony Stornetta, Forrest Brewer.

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
