DJBDD
=============

What's this?
-------------
A Java 7 BDD package with the [GPL with classpath linking exception license](https://openjdk.java.net/legal/gplv2+ce.html) based on GPL version 2 or later, as you prefer. See [this explanation about the license](https://en.wikipedia.org/wiki/GPL_linking_exception). Howeer, if your use case requires other license, *I can re-license this project for you, please write me to my email (at the botton of this README)*.

This package provides a Binary Decision Diagram library you can use to
make operations with boolean logical formulas and study its properties.

This software has been developed by Diego J. Romero-López as a tool
for his [Master's Thesis](https://github.com/diegojromerolopez/djbdd/blob/master/doc/memoria.pdf?raw=true).

Hay una **versión en español de este documento** [aquí](https://github.com/diegojromerolopez/djbdd/blob/master/LEEME.md).

Introduction
-------------
A Binary Decision Diagram is a complete truth table of a boolean expression
in a reduced graph form. For an introduction, see
[Binary Decision Diagram on Wikipedia](http://en.wikipedia.org/wiki/Binary_decision_diagram).

This library provides all the operations you need to work with them.

How to use it
-------------

```java
// These are the boolean variables used in our formulas
// They can have any alphanumeric name you want starting with a letter and
// being unique variable names.
String[] variables={"a", "b", "c", "d", "e", "f"};

// You always have to initialize the BDD system before you create any
// BDD object. Be careful with that.
BDD.init(variables);

// The functions are specified as boolean expressions with Java syntax
// adding the operators -> (implication) and <-> (double implication)
String function = "(a && b) || (c && d)";

// The variable ordering is given by the order
// of variables in the array of strings 'variables'.

// Construction of a new BDD
BDD bdd = new BDD(function);

// Printing the BDD in the standard output
// The internal table node will be shown by stdout
bdd.print();

// You can print it as a image PNG using a dot library
Printer.printBDD(bdd, "bdd_"+bdd.size());

// Creating other BDDs:
String function2 = "(a && e) || f"
BDD bdd2 = new BDD(function2);
bdd2.print();

// Operations with BDDs

// This BDD is the logical AND between bdd and bdd2
BDD bdd3 = bdd.apply("and", bdd2);

// This BDD is the logical OR between bdd and bdd2
BDD bdd4 = bdd.apply("or", bdd2);

// Destroy explicitly bdd2
// In case we need to compute sizes, reduce the global graph or print it
bdd2.delete();

// If you think you can have few free memory,
// you would have to free by calling the garbage collector
BDD.gc();

```

### Complete tests ###
The main program of this library is full of examples.
The all-in-one jar version is located in **store/DJBDD.jar**,
the dependant on libraries jar one is in  **dist/DJBDD.jar**.
You should use **store/DJBDD.jar** for your experiments.

Easiest option is opening this project with [Netbeans](https://netbeans.org/).
There are several executing configurations:
- Two examples of computing consistency in the real world.
- Six benchmark of BDD-reducing algorithms.
- Tests that you can execute to verify BDD operations.

#### Basic Options

##### Output

###### BDD print as image

```bash
java -jar DJBDD.jar --image --<format> <file>
```
Note that this option will work only if you are in a Linux/UNIX system
with the [dot](http://linux.die.net/man/1/dot) tool to draw graphs in the path **/usr/bin/dot**.

###### BDD printing

```bash
java -jar DJBDD.jar --print --<format> <file>
```
Prints a BDD in the standard output.

###### Restriction on BDD formats allowed

- **dimacs**: Dimacs CNF format. See [SAT format](http://www.cs.ubc.ca/~hoos/SATLIB/Benchmarks/SAT/satformat.ps) or [CNF](http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html) for more information.
- **cstyle**: C-style boolean expression preceded by a line with all variables separated by commas. For example:
  - a && !b
  - a -> (!b && (c || d))
  - a <-> (b && !c)
  - a != (b && !c)

###### Restriction on variable naming

**IMPORTANT**: for each variable there can no be any other that contains it as substring from the left.
That is, if we have a variable with the name 'x1' we cannot use other variable with the name 'x11'.
It's not in my future plans to change that, so name your variables with names like:
- {x1}
- {x11}
- {x12}

###### Other notes

**Example source data**: directory **data** has some examples of each format (look the extension).

##### BDD reduction benchmarks
See below for a description of each reduction method.

```bash
# Sifting Algorithm
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks/ sifting
# Window Permutation Algorithm (window size = 2)
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks/ window_permutation window_size=2
# Random Swapper
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks/ random_swapper random_seed=121481 iterations=100
# Genetic Algorithm
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks-genetic genetic random_seed=10 population=8 generations=10 selection_percentage=0.2 mutation_probability=0.1
# Memetic Algorithm
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks-memetic memetic random_seed=121481 population=8 generations=10 selection_percentage=0.2 mutation_probability=0.1
## Iterative Sifting
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks/ isifting iterations=100
```


Features
-------------

### Shared hash table ###
All BDDs use the same hash table, sharing the vertices and subgraphs.
The goal for using this data structure is reducing the number of repeated vertices.
Each BDD has one root vertex though.

### Rich I/O API ###
This library provides method to load logical clausules in DIMACS format
and Java native, includen implication and double implication operators.

### Memory efficient ###
This library shares vertices between different BDDs.

### Vertices grouped by levels ###
You can access the vertices that has each variable in an efficient way. This will be used for BDD-reducing algorithms.

### Operations implemented ###
- Apply [6] [3].
- Restrict. [2].
- Swapping of two variables. [5].

### Implemented Reduction Algorithms ###
This package contains many reduction algoritms.
They are implemented in the **djbdd.reductors** package.

These reduction algorithms are implemented as children classes of **djbdd.reductors.ReductionAlgorithm**,
so they share the same API. This API contains a execute and run method. The first one must be overriden
for each particular reduction method while the second will act as a façade of execute. The run method
also measures the elapsed time in the reduction method for comparing the reduction algorithms

```java
// Some logic function used to build a BDD
String function1 = "(a && d) || (b && c)";
BDD bdd1 = new BDD(function1);

// Call to garbage collector & print the
// node table 
BDD.gc();
BDD.T.print();
        
Printer.printBDD(bdd1, "test15_bdd1_BEFORE_"+bdd1.size());

// Construct a reduction algorithm. For example SiftingReductor:
SiftingReductor reductor = new SiftingReductor();
// Start the reduction process
reductor.run();

// Call to garbage collector & print the
// node table (to compare with the first node table)
BDD.gc();
BDD.T.print();
```

It is important to note that these reduction methods reduce all the BDDs created
in the program because they reduce the vertex mesh by swapping vertex levels.

They are listed below:

#### Window Permutation
Developed in the class **djbdd.reductors.WindowPermutation**, this algorithm
was proposed by Fujita et al. & Ishiura et al. Richard L. Rudell described it
and compared with its own reduction method in [7]. This implementation is based
on his description.

```java
// Window size
int windowSize = 2;
WindowPermutationReductor reductor = new WindowPermutationReductor(windowSize);
reductor.run();
```

#### Rudell's Variable Sifting
This package contains a basic implementation of the variable reordering
proposed by Richard L. Rudell in [7]. This method try to find the best
position for each variable keeping fixed in their position the rest.

Use example:

```java
SiftingReductor reductor = new SiftingReductor();
reductor.run();
```

#### Random Swapper Reduction
We wanted to compare a pure random algorithm with our evolutionary algorithms
to test if our results are due to randomness or because this method truly works.
This reduction method is based in swapping two variables chosen at random in each iteration in the BDD tree.

```java
int iterations = 1000;
RandomSwapperReductor reductor = new RandomSwapperReductor(iterations);
reductor.run();
```

Note that this algorithm, the genetic algorithm and the memetic algorithm need
a random seed to be set before their execution.

#### Genetic Reduction
W. Lenders & C. Baier defined the genetic operators for developing a Genetic Algorithm
for this BDD reduction problem in [8]. We have implemeted a version of their approach
in the **GeneticReductor** class.

```java
// Number of chromosomes
int populationSize = 10;
// Number of generations of the algorithm
int generations = 1000;
// % of population selected
double selectionPercentage = 10;
// Probability of mutating a gene in a chromosome
double mutationProbability = 0.1;
GeneticReductor reductor = new GeneticReductor(populationSize, generations, selectionPercentage, mutationProbability);
reductor.run();
```

#### Memetic Reduction

Based on [8], we propose another algorithm that combines the Genetic Algorithm optimizing
each chromosome using the Rudell's Sifting Algorithm. Its name is **MemeticReductor** and its
parameters are the same than the GeneticReductor.

```java
// Number of chromosomes
int populationSize = 10;
// Number of generations of the algorithm
int generations = 1000;
// % of population selected
double selectionPercentage = 10;
// Probability of mutating a gene in a chromosome
double mutationProbability = 0.1;
MemeticReductor reductor = new MemeticReductor(populationSize, generations, selectionPercentage, mutationProbability);
reductor.run();
```

#### Iterative Sifting

This is our original method [9] that applies a serious of sifting of variables with hopes of finding the best position for each one.
**IterativeSiftingReductor** contains this reduction method and can be used this way:

```java
int iterations = 100;
IterativeSiftingReductor reductor = new IterativeSiftingReductor(iterations);
reductor.run();
```

A paper with the description of this method is pending evalution.

### TODOs ###
- Include a parallel apply.
- Parallelization of reduction methods.

Examples
-------------
### Code examples ###
Look the class Tester class in package djbdd.test, its full of examples.

Documentation
-------------
There is a pdf file called [manual.pdf](https://github.com/diegojromerolopez/djbdd/blob/master/doc/manual.pdf?raw=true) with some remarks about the implementation and how to use this library.
Currently it is a work in progress, but you can read the tests, the code or ask me any question. If you are interested, there is also the Master's Thesis
[Memoria del Trabajo Fin de Máster](https://github.com/diegojromerolopez/djbdd/blob/master/doc/memoria.pdf?raw=true) (only in Spanish).

Bibliography
-------------
[1] Symbolic Boolean Manipulation with Ordered Binary Decision Diagrams, Randal E. Bryant. Carnegie Mellon University.

[2] Binary Decision Diagrams. Fabio Somenzi.

[3] Efficient implementation of a BDD package, Karl S. Brace, Richard L. Rudell & Randal E. Bryant. 

[4] Implementation of an Efﬁcient Parallel BDD Package. Tony Stornetta & Forrest Brewer.

[5] Incremental  Reduction of Binary Decision Diagrams. R. Jacobi, N. Calazans & C. Trullemans.

[6] An Introduction to Binary Decision Diagrams. Henrik Reif Andersen.

[7] Dynamic variable ordering for ordered binary decision diagrams, Richard L. Rudell.

[8] Genetic Algorithms for the Variable Ordering Problem of Binary Decision Diagrams, W. Lenders & C. Baier.

[9] Iterative Sifting: A new approach to reduce BDD size. Diego J. Romero-López & Elena Ruiz-Larrocha. TBA.

Cites
-------------
*If you use DJBDD and like to appear here, please send me an email*.

- [CI Fallin](https://chrisfallin.com/) PhD thesis: [Finding and Exploiting Parallelism with Data-Structure-Aware Static and
Dynamic Analysis"](https://chrisfallin.com/pubs/cfallin-dissertation.pdf).

FAQ
-------------
### I don't understand what is a BDD ###
OK. The concept is not easy, read the Wikipedia page about them [Binary Decision Diagram at Wikipedia](http://en.wikipedia.org/wiki/Binary_decision_diagram) and later the reference [1] and [2].

### Do you use complement arcs? ###
No. I don't use them. I understand they can reduce the size of the graph, but I wanted to make a pure BDD library. Maybe I could include them in some future version.

### How can I import this project to my IDE? ###
I've used Netbeans (yeah, I know it is ancient but works for me). You should be able to open with whatever IDE you use.

### Can I use this library in Java5 or Java6? ###
I suppose. I have not used it in Java 6, but it should work.

### Are there some code examples? ###
Look in the tests (**djbdd.test.Tester** class) and in the benchmarks.

### I'm running the examples (or my custom code) and throws a exception ###
It depends on the exception type, but my money is on a memory-related exception.
This library is memory greedy, use **BDD.gc** or help me
to implement some reduction method on the vertex table :)

### Why don't you use dynamic variable ordering when creating the BDD? ###
I prefer building the BDD "as is" and relying in the developer using some
variable reordering algorithm, like Variable Sifting, implemented in this
package.

### Your code is inefficient/wrong/could be improved ###
I accept suggestions, critiques and comments you want to communicate me. 

### Why don't you use <whatever> method? ###
Maybe I don't know it, please send me an email asking me and pointing me
to a paper where it is explained. I will take a look to that.

### What is the license of this code? ###
This code is **GPL3 with classpath linking exception**. That's the same license than the OpenJDK7 one.
If you are unsure about this kind of license, [read about it in](http://en.wikipedia.org/wiki/GPL_linking_exception).

### Do you answer emails? ###
Of course I will answer questions, suggestions and comments about this library.

### Who are you? ###
I'm Diego J., my email is die_gojr_om-erol-op_ez AT g-m-a-i-l.com
(erase - and _ and replace AT with @). I'm from Spain and this is part of my master's final thesis.

