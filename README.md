DJBDD
=============

What's this?
-------------
A Java 7 BDD package with the GPL 3 (with classpath linking  exception) license.

This package provides a Binary Decision Diagram library you can use to
make operations with boolean logical formulas and study its propierties.


Features
-------------
### Use of weak references ###
This package uses the WeakReference class introduced in Java 7. This way
I didn't have to implement a special garbage collector but use the Hotspot one.

You can earse the weak references using the method of TableT gc. There is a way to use
parallel garbage collection calling 

```java
// Maybe in the future we'll change that to something easier.
GCThread gcCollector = new GCThread();
Thread gcThread = new Thread(gcCollector);
gcThread.start();
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
I'm Diego J., my email is diegojromerolopez AT g-m-a-i-l.com. I'm from Spain and this is part of my master's final thesis.
