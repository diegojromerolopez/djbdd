DJBDD
=============

¿Qué es esto?
-------------
Es un paquete de Java 7 para trabajar con árboles binarios de decisión
(Binary Decision Diagrams, BDD). Este paquete tiene licencia GPL 3 con
excepción del enlazado de las clases.

Este paquete proporciona una librería para creación y tratamiento
de árboles binarios de decisión que puede usarse para representar,
minimizar y realizar operaciones con funciones de lógica Booleana.

Este software ha sido desarrollado de forma original y única por
Diego J. Romero López como herramienta para su [Trabajo Fin de Máster](https://github.com/diegojromerolopez/djbdd/blob/master/doc/memoria.pdf?raw=true).

There is an **English version of this README** [here](https://github.com/diegojromerolopez/djbdd/LEEME.md).

Introducción
-------------
Ún árbol binario de decisión es una representación de una tabla de verdad
en forma de grafo reducido. Para una introducción completa, vea
[Binary Decision Diagram on Wikipedia](http://en.wikipedia.org/wiki/Binary_decision_diagram).

Esta librería proporciona todas las operaciones que necesita para trabajar con ellos.

Cómo usarla
-------------

```java
// Se han de definir las variables Booleanas que pueden usarse en las fórmulas
// El formato de las variables es alfanumérico comenzando por caráceter alfabético.
// Han de ser únicas.
String[] variables={"a", "b", "c", "d", "e", "f"};

// Siempre se ha de iniciar el sistema antes de crear
// ningún objeto BDD. Si no lo hace no funcionará su software
BDD.init(variables);

// Las funciones se especifican como expresiones Booleanas en sintaxis Java
// (o estilo C). Además, se añaden los operadores de implicación (->)
// y de doble implicación (<->)
String function = "(a && b) || (c && d)";

// El array de Strings "variables" es el que
// especifica el orden inicial

// Construcción de un nuevo objeto BDD
BDD bdd = new BDD(function);

// Imprime el BDD en la salida estándar
// Se muestra la tabla de nodos por la salida estándar
bdd.print();

// Puede generar una imagen PNG usando
// la librería de UNIX/Linux dot
Printer.printBDD(bdd, "bdd_"+bdd.size());

// Creando otros BDD:
String function2 = "(a && e) || f"
BDD bdd2 = new BDD(function2);
bdd2.print();

// Operaciones con BDD

// bdd3 es el Y lógico entre bdd y bdd2
BDD bdd3 = bdd.apply("and", bdd2);

// bdd4 es el O lógico entre bdd y bdd2
BDD bdd4 = bdd.apply("or", bdd2);

// Destrucción explícita del bdd2
// para liberar espacio
bdd2.delete();

// LLamada al recolector de basura para liberar memoria
BDD.gc();

```

### Tests completos ###
El programa principal de esta librería tiene muchos ejemplos
El fichero jar completo está en **store/DJBDD.jar** y no depende
de librerías extras. El ubicado en **dist/DJBDD.jar** depende
de muchas librerías externas, por lo que se recomienda el uso
de **store/DJBDD.jar** en la ejecución de las pruebas.

La forma recomendada y la más sencilla, es abrir este
proyecto en [Netbeans](https://netbeans.org/).
Hay varias configuraciones de ejecución:
- Dos ejemplos de cálculo de consistencia lógica en el mundo real
- Seis benchmarks de algoritmos de reducción de tamaño de BDD.
- Pruebas que puede ejecutar para verificar las operaciones sobre los BDD.

#### Opciones básicas

##### Salida

###### Generar el BDD como imagen

```bash
java -jar DJBDD.jar --image --<formato> <fichero>
```
Tenga en cuenta que esta opción sólo funcionará si está en un sistema
LINUX/UNIX con la herramienta [dot](http://linux.die.net/man/1/dot)
en la ruta **/usr/bin/dot**.

###### Impresión por pantalla de BDD

```bash
java -jar DJBDD.jar --print --<formato> <fichero>
```
Muestra el BDD por la salida estándar.

###### Restricción de los formatos permitidos para carga de BDD

- **dimacs**: formato CNF de Dimacs. Vea [SAT format](http://www.cs.ubc.ca/~hoos/SATLIB/Benchmarks/SAT/satformat.ps) o [CNF](http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html) para más información.
- **cstyle**: expresiones de lógica Booleana con sintaxis del lenguaje de programación C precedidas por una línca con todas las variables separadas por comas. Por ejemplo:
  - a && !b
  - a -> (!b && (c || d))
  - a <-> (b && !c)
  - a != (b && !c)

###### Restricción en los nombres de variables

Para cada variable, aparte de que tenga un nombre único, no debe existir una variable que contenga
a otra como subcadena comenzando por la izquierda. Si necesariamente le ocurre este hecho, 
introduzca las variables con el siguiente formato:
- {x1}
- {x11}
- {x12}

###### Otras notas

**Datos de ejemplo**: el directorio **data** tiene algunos ejemplos para cada formato (look the extension).

##### Benchmarks de reducción de BDD
Vea más adelante para una descripción de cada método de reducción.

```bash
# Algoritmo Sifting de Rudell
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks/ sifting
# Algoritmo Window Permutation (tamaño de ventana de 2)
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks/ window_permutation window_size=2
# Intercambios aleatorios
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks/ random_swapper random_seed=121481 iterations=100
# Algoritmo Genético
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks-genetic genetic random_seed=10 population=8 generations=10 selection_percentage=0.2 mutation_probability=0.1
# Algoritmo Memético
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks-memetic memetic random_seed=121481 population=8 generations=10 selection_percentage=0.2 mutation_probability=0.1
## Iterative Sifting de Diego J. Romero-López
java -jar ./DJBDD/store/DJBDD.jar --memory-optimization-benchmark --dimacs ./data/benchmarks/ isifting iterations=100
```


Características
-------------

### Tabla hash compartida ###
Todos los BDD usan la misma tabla hash, de forma que comparten vértices
y subgrafos completos y logrando por ello un ahorro en número de vértices.
Por supuesto, cada BDD tiene su propio vértice raíz.

### Completa API de E/S ###
Esta librería proporciona métodos para cargar expresiones lógicas
desde el formato DIMACS y en sintaxis Java/C, extendiendo este último
incluyendo la implicación y la doble implicación.

### Eficiente en memoria ###
Esta librería comparte vértices entre BDD.

### Vértices agrupados en niveles ###
Se permite el acceso a los vértices por niveles (por variables) de una forma eficiente.
Esto facilita el desarrollo de algoritmos de reducción de BDD.

### Operaciones implementadas ###
- Apply [6] [3].
- Restrict. [2].
- Intercambio entre dos variables. [5].

### Implemented Reduction Algorithms ###
Este paquete contiene muchos algoritmos de reducción.
Están implementados en el subpaquete **djbdd.reductors**.

Estos algoritmos de reducción están implementados como clases hijas de **djbdd.reductors.ReductionAlgorithm**,
por lo que comparten la misma API. Esta API contiene dos métodos: **execute** y **run**.
El primero es el que ha de ser sobrescrito con el método concreto de reducción, mientras que el segundo
es el método público que se llamará desde la aplicación. El método run también genera mediciones de tiempo
útiles para medir la eficiencia de los algoritmos.

```java
// Función lógica usada para construir el BDD
String function1 = "(a && d) || (b && c)";
BDD bdd1 = new BDD(function1);

// Llamada al recolector de basura e
// impresión de la tabla de nodos
BDD.gc();
BDD.T.print();
        
Printer.printBDD(bdd1, "test15_bdd1_BEFORE_"+bdd1.size());

// Construcción de un algoritmo de reducción. Por ejemplo, SiftingReductor:
SiftingReductor reductor = new SiftingReductor();
// Inicia el proceso de reducción
reductor.run();

// Llamada al recolector de basura e
// impresión de la tabla de nodos para comparar
// con los resultados iniciales
BDD.gc();
BDD.T.print();
```

Es importante destacar que estos métodos de reducción reducen todos los BDD
creados en el programa puesto que se reducen la malla de vértices intercambiando
niveles de vértices.

Los métodos de reducción se listan a continuación:

#### Window Permutation
Desarrollado en la clase **djbdd.reductors.WindowPermutation**, este algoritmo
fue propuesto por Fujitam, Ishiura y otros. Richard L. Rudell lo describió
y comparó con su propio método de reducción en [7]. Esta implementación
está basada en su descripción.

```java
// Tamaño de ventana
int windowSize = 2;
WindowPermutationReductor reductor = new WindowPermutationReductor(windowSize);
reductor.run();
```

#### Sifting de Rudell

Implementación de la reordenación de variables propuesta por Richard L. Rudell in [7].
Este método trata de encontrar la mejor posición para cada variable suponiendo
el resto de variables fija. Es la heurística más común y rápida de implementar.

```java
SiftingReductor reductor = new SiftingReductor();
reductor.run();
```

#### Intercambios aleatorios
Algoritmo puramente aleatorio que sirve como base con la que comparar los
algoritmos evolutivos. Así, podemos saber si el funcionamiento de estos
métodos se debe a su calidad o simplemente a su aleatoriedad.

Este método de reducción está basado en intercambiar dos variables escogidas aleatoriamente.

```java
int iterations = 1000;
RandomSwapperReductor reductor = new RandomSwapperReductor(iterations);
reductor.run();
```

Notemos que todos los algoritmos aleatorios requieren de una semilla para funcionar correctamente.

#### Algoritmo genético
W. Lenders y C. Baier definieron unos operadores genéticos para desarrollar un algoritmo genético para
la reducción de BDD en [8]. Hemos implementado una versión de su aproximación en la clase **GeneticReductor**.

```java
// Número de cromosomas
int populationSize = 10;
// Número de generaciones del algoritmo
int generations = 1000;
// % de población seleccionada
double selectionPercentage = 10;
// Probabilidad de mutación
double mutationProbability = 0.1;
GeneticReductor reductor = new GeneticReductor(populationSize, generations, selectionPercentage, mutationProbability);
reductor.run();
```

#### Algoritmo memético

Basándonos en [8], proponsmos otro algoritmo que combina los algoritmos genéticos con un proceso de optimización
en cada cromosoma usando el algoritmo Sifting de Rudell. Su nombre es **MemeticReductor** y sus parámetros
son los mismos a los del **GeneticReductor**.

```java
// Número de cromosomas
int populationSize = 10;
// Número de generaciones del algoritmo
int generations = 1000;
// % de población seleccionada
double selectionPercentage = 10;
// Probabilidad de mutación
double mutationProbability = 0.1;
MemeticReductor reductor = new MemeticReductor(populationSize, generations, selectionPercentage, mutationProbability);
reductor.run();
```

#### Iterative Sifting

Este es nuestro método original, descrito en [9], que aplica una serie de movimientos de variables con la esperanza
de encontrar la mejor posición para cada una. **IterativeSiftingReductor** contiene este método de reducción y puede usarse
de la siguiente forma:

```java
int iterations = 100;
IterativeSiftingReductor reductor = new IterativeSiftingReductor(iterations);
reductor.run();
```

Un trabajo con la descripción de este algoritmo está pendiente de evaluación.

### TODOs ###
- Incluir un método apply paralelo.
- Paralelización de los métodos de reducción.

Ejemplos
-------------
### Code examples ###
Puede ver ejemplos en la clase **Tester** del paquete **djbdd.test**.

Documentación
-------------
Hay un fichero pdf (en inglés) llamado [manual.pdf](https://github.com/diegojromerolopez/djbdd/blob/master/doc/manual.pdf?raw=true)
con algunas notas y observaciones sobre la implementación de esta librería, además de sobre su uso.

Actualmente, sigo trabajando, pero la librería puede usarse. Si sigue interesado,
puede leer mi [Memoria del Trabajo Fin de Máster](https://github.com/diegojromerolopez/djbdd/blob/master/doc/memoria.pdf?raw=true).

Bibliografía
-------------
[1] Symbolic Boolean Manipulation with Ordered Binary Decision Diagrams, Randal E. Bryant.

[2] Binary Decision Diagrams. Fabio Somenzi.

[3] Efficient implementation of a BDD package, Karl S. Brace, Richard L. Rudell y Randal E. Bryant. 

[4] Implementation of an Efﬁcient Parallel BDD Package. Tony Stornetta, Forrest Brewer.

[5] Incremental  Reduction of Binary Decision Diagrams. R. Jacobi, N. Calazans y C. Trullemans.

[6] An Introduction to Binary Decision Diagrams. Henrik Reif Andersen.

[7] Dynamic variable ordering for ordered binary decision diagrams. Richard L. Rudell.

[8] Genetic Algorithms for the Variable Ordering Problem of Binary Decision Diagrams, W. Lenders y C. Baier.

[9] Iterative Sifting: A new approach to reduce BDD size. Diego J. Romero-López y Elena Ruiz-Larrocha. (Pendiente de publicación).

Preguntas más frecuentes
-------------
### No entiendo qué es un BDD ###
El concepto no es sencillo, lee el artículo de la Wikipedia [Binary Decision Diagram at Wikipedia](http://en.wikipedia.org/wiki/Binary_decision_diagram) y después las referencias [1] y [2].

### ¿Usas arcos negados? ###
No. No los uso. Entiendo que pueden reducir el tamaño del grafo, pero quería hacer una librería BDD pura. Quizás puedo hacerlo en la siguiente versión.

### ¿Puedo importar este proyecto a mi IDE? ###
Yo he usado Netbeans. Deberías poder importar el código fuente en el IDE que uses independientemente de cuál sea.

### ¿Puedo usar Java5 o Java6 en este software? ###
Supongo que sí. Sólo he usado Java 7, pero supongo que debería funcionar.

### ¿Hay algunos ejemplos de código? ###
Mira en **djbdd.test.Tester** y en los benchmarks.

### Estoy ejecutando los ejemplos (o mi propio código) y me lanza una excepción ###
Depende del tipo de excepción, pero apostaría a que es una excepción de falta de memoria.
Esta librería es muy voraz en cuanto al consumo de memoria, por lo que tendrás que
usar el método **BDD.gc** muy a menudo o ayudarme a implementar algún método
de reducción sobre la tabla de vértices.

### ¿Por qué no ordenas de forma dinámica las variables conforme vas creando el árbol? ###
Prefiero construir el BDD "tal y como es" y luego dar la opción al desarrollador
de usar algún algoritmo de reordenación para reducir el tamaño del BDD.

### Tu código es ineficiente/erróneo/puede mejorarse ###
Acepto sugerencias, críticas y comentarios si me los envías.

### ¿Por qué no has usado el método <tal>? ###
Porque no lo conozco. Por favor, si tienes alguna sugerencia sobre algún método o algoritmo que pueda
mejorar esta librería envíamelo a mi correo y le echaré un vistazo.

### ¿Cuál es la licencia de este código? ###
Este código tiene como licencia la GPL3 con excepción de enlazado de clases a otra aplicación (**GPL3 with classpath linking exception** en inglés).
Puedes ver qué significa GPL3 with classpath linking exception [aquí](http://en.wikipedia.org/wiki/GPL_linking_exception).
Es la misma licencia que la de OpenJDK7.

### ¿Contestas a los correos electrónicos? ###
Por supuesto contestaré preguntas, sugerencias y comentarios sobre esta librería.

### ¿Quién eres? ###
Me llamo Diego J., mi correo electróncio es die_gojr_om-erol-op_ez AT g-m-a-i-l.com
(sin los - ni _ y reemplazando AT con @). Soy de España y esto es parte de mi trabajo fin de máster
en Investigación en Ingeniería del Software.

