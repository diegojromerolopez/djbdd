package djbdd.core;

import logger.*;
import djbdd.io.Printer;

import java.util.*;
import java.io.*;



/**
 * Wrapper of the hashmap that contains the vertices of a BDD.
 * @author diegoj
 */
public class TableT {
    
    /** Default initial capacity of the hash tables */
    public static int INITIAL_CAPACITY = 1000;
    
    /** Default load factor of the hash tables */
    public static final float LOAD_FACTOR = 0.75f;
    
    /** Should operations be verbose and print its results? */
    private static final boolean VERBOSE = false;
    
    /**
     * Vertex hashmap.
     * Contains every vertex of the graph given its index.
     */
    private HashMap<Integer,Vertex> T;
    
    /**
     * Uniqueness hashmap: keys are the string "var_i+-+low_i+-+high_i"
     */
    private HashMap<String,Vertex> U;
    
    /**
     * Variable vertices hash.
     * This hash contains the vertices of the graph grouped by its variables.
     */
    public HashMap<Integer,HashSet<Vertex>> V;
    
    /** Used to increase key creation performance */
    private int lastKey = 0;
    
    /** Vertex that represents the false value */
    public final Vertex False;
    
    /** Vertex that represents the true value */
    public final Vertex True;
    
    /** Swap operation counter */
    private long swapCounter = 0;
    
    /** Flag to start the swap counting */
    private final boolean COUNT_SWAPS = true;
    
    private void incSwapCounter(){
        if(this.COUNT_SWAPS){
            this.swapCounter++;
        }
    }
    
    public long getSwapCounter(){
        return this.swapCounter;
    }
    
    /**
     * Init the hashes wit initial capacity and load factor.
     * @param initialCapacity Initial capacity of the hash tables.
     * @param loadFactor Load factor of the hash tables.
     */
    private void initHashes(int initialCapacity, float loadFactor){
        this.T = new HashMap<Integer,Vertex>(initialCapacity, loadFactor);
        this.U = new HashMap<String,Vertex>(initialCapacity, loadFactor);
        
        this.V = new HashMap<Integer,HashSet<Vertex>>(initialCapacity, loadFactor);
    }
    
    /**
     * Constructor
     */
    public TableT(){
        this.initHashes(INITIAL_CAPACITY, LOAD_FACTOR);
        // Initialize the leaf vertices
        this.True = new Vertex(true);
        this.False = new Vertex(false);
        this.addNew(this.False);
        this.addNew(this.True);
        this.swapCounter = 0;
    }
    
    /**
     * Constructor: build the hash with an initial capacity.
     * @param initialCapacity Number of space elements that will be allocated in memory.
     */
    public TableT(int initialCapacity){
        this.initHashes(initialCapacity, LOAD_FACTOR);
        // Initialize the leaf vertices
        this.True = new Vertex(true);
        this.False = new Vertex(false);
        this.addNew(this.False);
        this.addNew(this.True);
        this.swapCounter = 0;
    }
    
    /**
     * Informs if an index exists in the hash.
     * @param key Index that will be tested for existence in the hash.
     * @return true if exists vertex whose index is key, false otherwise.
     */
    public synchronized boolean containsKey(Integer key){
        return this.T.containsKey(key);
    }

    /**
     * Informs if a vertex exists in the hash.
     * @param v Vertex that will be tested for existence in the hash.
     * @return true if exists this vertex (or some duplicate of it [{@link djbdd.Vertex#isDuplicate}]), false otherwise.
     */
    public synchronized boolean containsVertex(Vertex v){
        String uniqueKey = v.uniqueKey();
        return this.U.containsKey(uniqueKey);
    }

    /**
     * Informs if a vertex unique key exists in the hash.
     * See {@link djbdd.Vertex#uniqueKey}.
     * @param vKey Vertex key that will be tested for existence in the hash.
     * @return true if exists vertex whose unique key is vKey, false otherwise.
     */
    public synchronized boolean containsVertex(String vKey){
        return this.U.containsKey(vKey);
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Addings */
    
    /**
     * Put a vertex in the table U.
     * @param v Vertex that will be added to U.
     * @see TableT#U
     */
    private synchronized void putInU(Vertex v){
        this.U.put(v.uniqueKey(), v);
    }

    /**
     * Put a vertex in the table T.
     * @param v Vertex that will be added to T.
     * @see TableT#T
     */
    private synchronized void putInT(Vertex v){
        this.T.put(v.index, v);
    }

    /**
     * Put a vertex in the table V.
     * @param v Vertex that will be added to V.
     * @see TableT#V
     */
    private synchronized void putInV(Vertex v){
        int variable = v.variable;
        if(!this.V.containsKey(variable))
        {
            this.V.put(variable, new HashSet<Vertex>());
        }
        this.V.get(variable).add(v);
    }
    
  
    /**
     * Adds a new vertex to the table.
     * @param var_index Index of the variable that will have the vertex.
     * @param low Low index of the new vertex.
     * @param high High index of the new vertex.
     * @return Vertex New vertex inserted.
     * @see Vertex#variable
     * @see Vertex#low
     * @see Vertex#high 
     */
    private synchronized Vertex addNew(int var_index, Vertex low, Vertex high){
        int index = this.getNextKey();
        Vertex v = new Vertex(index, var_index, low, high);
        this.putInT(v);
        this.putInU(v);
        this.putInV(v);
        return v;
    }
    
    /**
     * Adds a new vertex to the table.
     * @param index Index of the vertex that will have the vertex in the table T.
     * @param v New vertex.
     * @return Vertex New vertex inserted.
     * @see Vertex
     */    
    private synchronized Vertex addNew(Vertex v){
        this.putInT(v);
        this.putInU(v);
        this.putInV(v);
        return v;
    }

    /**
     * Adds a vertex to the table. If the vertex exists, it is not added.
     * @param var_index Index of the variable that will have the vertex.
     * @param low Low index of the new vertex.
     * @param high High index of the new vertex.
     * @return Added vertex to the table.
     */
    public synchronized Vertex add(int var_index, Vertex low, Vertex high){
        String uniqueKey = Vertex.computeUniqueKey(var_index, low, high);
        if(this.containsVertex(uniqueKey)){
            Vertex v = this.get(uniqueKey);
            if(v!=null)
                return this.get(uniqueKey);
        }
        return this.addNew(var_index, low, high);
    }
    
    /**
     * Adds a vertex to the table. If the vertex exists, it is not added.
     * @param v Vertex that will be added to the table.
     * @return Added vertex to the table.
     */
    public synchronized Vertex add(Vertex v){
        if(this.containsVertex(v)){
            return v;
        }
        return this.addNew(v);
    }
    
    /**
     * Sets a vertex with a variable, low and high.
     * Used in BDDSiftingReduce. It should not be used in other methods.
     * @param v Vertex that will be modified.
     * @param variable New variable for v.
     * @param low New low descendant for v. 
     * @param high New high descendant for v.
     */
    void setVertex(Vertex v, int variable, Vertex low, Vertex high) {
        this.V.get(v.variable).remove(v);
        this.U.remove(v.uniqueKey());
        v.setVariable(variable);
        v.setLow(low);
        v.setHigh(high);
        this.putInU(v);
        this.putInV(v);
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Deletions */
    
    /**
     * Removes the vertex with a particular index.
     * @param index Index of the Vertex to delete.
     */
    public synchronized boolean remove(int index){
        Vertex removed = T.get(index);
        T.remove(index);
        if(removed != null){
            U.remove(removed.uniqueKey());
            if(!V.containsKey(removed.variable)){
                System.err.println(removed.variable+" no existe en V");
                System.out.println(V);
                System.exit(-1);
            }
            V.get(removed.variable).remove(removed);
        }
        if(removed == null)
            return false;
        return true;
    }
    
    /**
     * Removes the vertex with a particular index.
     * @param oldVertex Vertex to delete.
     */
    public synchronized void remove(Vertex oldVertex){
        int index = oldVertex.index;
        this.remove(index);
    }
    
    /**
     * Clears completely the hash tables.
     * Erases all elements of the hash tables.
     */
    public synchronized void clear(){
        this.T.clear();
        this.U.clear();
        this.V.clear();
    }

    /**
     * Warning! Internal deprecated debug method, will be deleted in the future.
     * Tests if a vertex has parents or is a root vertex.
     */
    protected boolean vertexHasParents(Vertex vertex){
        if(vertex.numberOfRootedBDDs() > 0)
            return true;
        int vertexIndex = vertex.index;
        ArrayList<Integer> keys = new ArrayList<Integer>(this.T.keySet());
        int _size = keys.size();
        int deletions = 0;
        boolean thereIsADeletion = true;
        while(thereIsADeletion)
        {
            keys = new ArrayList<Integer>(this.T.keySet());
            thereIsADeletion = false;
            for(Integer key : keys){
                // For each Vertex that was erased there is an entry in
                // hash table T that weak-references to that and must be erased
                if(this.T.containsKey(key)){
                    Vertex vI = this.T.get(key);
                    if(vI != null){
                        if(vI.lowIndex() == vertex.index || vI.highIndex() == vertex.index)
                            return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Calls the garbage collector that deletes the references to dead objects.
     * @return Returns the size of the BDD graph.
     */
    public synchronized int gc(){
        //Log.println(VERBOSE, "<<<<<<<<<<<<<<< GC >>>>>>>>>>>>>>>>>");
        
        // Compact the hash maps
        //ArrayList<Integer> keys = new ArrayList<Integer>(this.T.keySet());
        ArrayList<Integer> keys = new ArrayList<Integer>(this.T.keySet());
        int _size = keys.size();
        int deletions = 0;
        boolean thereIsADeletion = true;
        while(thereIsADeletion)
        {
            keys = new ArrayList<Integer>(this.T.keySet());
            thereIsADeletion = false;
            for(Integer key : keys){
                // For each Vertex that was erased there is an entry in
                // hash table T that weak-references to that and must be erased

                if(this.T.containsKey(key)){
                    Vertex v = this.T.get(key);
                    //if(v == null || v.isOrphan() || !this.vertexHasParents(v)){
                    if(v == null || v.isOrphan()){
                        Log.println(VERBOSE, "DELETING "+v);
                        if(v!=null){
                            Vertex.decNumParentsOfVertex(v.low());
                            Vertex.decNumParentsOfVertex(v.high());
                        }
                        thereIsADeletion = true;
                        this.remove(key);
                        deletions++;
                        
                        //Log.println(VERBOSE, (v!=null), "DELETED: "+key+" "+v);
                        //Log.println(VERBOSE, (v==null), "DELETED: "+key+" NULL ");
                    }
                }
            }
        }

        //Log.println(VERBOSE, deletions+" vertices deleted");
        //Log.println(VERBOSE, "<<<<<<<<<<<<<< END GC >>>>>>>>>>>>>>");

        _size -= deletions;
        
        //Log.println(VERBOSE, "The size is "+_size);

        return _size;
    }
   
    /**************************************************************************/
    /**************************************************************************/
    
    /* Getters */
    
    /**
     * Gets a vertex with a index.
     * See {@link djbdd.TableT#getNextKey} and {@link djbdd.Vertex#index}.
     * @param index Index of the vertex in the table T.
     * @return Vertex whose index is index.
     */    
    public Vertex get(int index){
        Vertex v = this.T.get(index);
        return v;
    }
    
    /**
     * Gets a vertex with the key given by vKey.
     * See {@link djbdd.Vertex#uniqueKey}. 
     * @param vKey Unique key of the vertex.
     * @return Vertex with uniqueness key vKey.
     */
    public Vertex get(String vKey){
        Vertex v = this.U.get(vKey);
        return v;
    }
 
    
    /**************************************************************************/
    /**************************************************************************/
    /* Collection getters */
    
    /**
     * Gets the keyset of table T.
     * @return Set with the indices of the T table.
     */
    public Set<Integer> getKeySet(){
        return this.T.keySet();
    }    

    /**
     * Gets the keyset of table T.
     * @return Set with the indices of the T table.
     */
    public Set<Integer> keySet(){
        return this.T.keySet();
    }

    /**
     * Gets the next key for a vertex in this table.
     * @return Next key for a vertex in this table.
     */        
    public synchronized Integer getNextKey(){
        int index = lastKey;
        while(this.T.containsKey(index)){ index++; }
        lastKey = index+1;
        return index;
    }
    
    /**
     * Gets the indices of the vertices in the table.
     * @return List of indices of the vertices.
     */    
    public ArrayList<Integer> getIndices(){
        return new ArrayList<Integer>(this.T.keySet());
    }

    /**
     * Gets the vertices of the table.
     * @return List of vertices in the table.
     */
    public ArrayList<Vertex> getVertices(){
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(this.T.size());
        for(Vertex v : this.T.values()){
            if(v!=null){
                vertices.add(v);
            }
        }
        return vertices;
    }
    
    /**
     * Gets the vertices of the table.
     * @return List of vertices in the table.
     */
    public ArrayList<Vertex> values(){
        return this.getVertices();
    }
    
    /**
     * Gets vertices whose variable is a particular one.
     * @param variable Variable index whose vertices will be returned.
     * @return List of vertices with the variable identified by the variable index pass as parameter.
     */
    public Set<Vertex> getVerticesWhoseVariableIs(int variable){
        // If the variable has no vertices, we return the empty set
        if(!V.containsKey(variable)){
            return new TreeSet<Vertex>();
        }
        // We have some vertices with this variable
        HashSet<Vertex> verticesWithThatVariable = V.get(variable);
        HashSet<Vertex> validVertices = new HashSet<Vertex>(verticesWithThatVariable.size());
        for(Vertex v : verticesWithThatVariable){
            if(!v.isOrphan()){
                validVertices.add(v);
            }
        }
        return validVertices;
    }

    /**************************************************************************/
    /**************************************************************************/
    /* Swapping of variables */

    /**
     * This method tests if a vertex can be added to the tree.
     */
    private Vertex addWithoutRedundant(int var, Vertex low, Vertex high) {
        if (low.index == high.index) {
            return low;
        }
        return this.add(var, low, high);
    }
    
    public void updateNumParents(){
        boolean change = false;
        do{
            change = false;
            for(int index : this.T.keySet()){
                Vertex v = this.T.get(index);
                change = updateNumParents(v);
            }
        }while(change);
    }
    
    public boolean updateNumParents(Vertex v){
        if (v != null) {
            int computedNumParents = v.computeNumParents();
            if (computedNumParents != v.numberOfParents()) {
                if(true){
                    System.err.println(v+" was wrong "+v.num_parents+" != "+computedNumParents);
                    System.out.flush();
                    System.err.flush();
                    System.exit(-1);
                }
                v.num_parents = computedNumParents;
                return true;
            }
        }
        return false;
    }
    
    public boolean testNumParents(){
        boolean fuckedVertices = false;
        for(int index : this.T.keySet()){
            Vertex v = this.T.get(index);
            if(v!=null){
                int computedNumParents = v.computeNumParents();
                if(computedNumParents!=v.numberOfParents()){
                    System.err.println(v.debugToString()+" IS WRONG, should have "+computedNumParents);
                    fuckedVertices = true;
                }
            }
        }
        return fuckedVertices;
    }
    
    public boolean testNumParents(Vertex v, Vertex low, Vertex high, String caseText){
        int vNumParents = v.computeNumParents();
        int lowNumParents = low.computeNumParents();
        int highNumParents = high.computeNumParents();
        
        if(vNumParents != v.numberOfParents() || lowNumParents != low.numberOfParents() || highNumParents != high.numberOfParents()){
            System.err.println("==============================================");
            System.err.println("==============================================");
            System.err.println(caseText);
            if(vNumParents != v.numberOfParents()){
                System.err.println("v is WRONG");
                System.err.println(v.debugToString());
                System.err.println("Should be "+vNumParents+". It's "+v.numberOfParents());
            }

            if(lowNumParents != low.numberOfParents()){
                System.err.println("low is WRONG");
                System.err.println(low.debugToString());
                System.err.println("Should be "+lowNumParents+". It's "+low.numberOfParents());
            }

            if(highNumParents != high.numberOfParents()){
                System.err.println("high is WRONG");
                System.err.println(high.debugToString());
                System.err.println("Should be "+highNumParents+". It's "+high.numberOfParents());
            }
            System.err.println("----------------------------------------------");
            System.err.println("----------------------------------------------");
            System.err.flush();
            return false;
        }
        return true;
    }
    
    /**
     * Swaps a vertex with its descendants if they have the variable varJ.
     * @param v Vertex that will be swapped.
     * @param varJ Variable that MUST have the descendants of v to be swapped with it.
     * @return there was a swapping?
     */
    private boolean swapVertexWithDescendantsWithVariable(Vertex v, int varJ){
        boolean swapWasMade = false;
        int varI = v.variable;

        Vertex low = v.low();
        Vertex high = v.high();

        Vertex A = null;
        Vertex B = null;
        Vertex C = null;
        Vertex D = null;

        Vertex newLow = null;
        Vertex newHigh = null;

        // Case a:
        if (low != null && low.variable == varJ && (high == null || high.variable != varJ)) {
            Log.println(VERBOSE, "CASE A");
            A = v.low().low();
            B = v.low().high();
            C = v.high();
            newLow = addWithoutRedundant(varI, A, C);
            newHigh = addWithoutRedundant(varI, B, C);
            this.setVertex(v, varJ, newLow, newHigh);
            Log.println(VERBOSE, "{{{{{{{{{{{{{{{{{{{{{");
            Log.println(VERBOSE, v.debugToString());
            Log.println(VERBOSE, newLow.debugToString());
            Log.println(VERBOSE, newHigh.debugToString());
            Log.println(VERBOSE, "}}}}}}}}}}}}}}}}}}}}}");
            
            /*BDD.T.updateNumParents(v);
            BDD.T.updateNumParents(newLow);
            BDD.T.updateNumParents(newHigh);*/
            /* Test 
            testNumParents(v, newLow, newHigh, "CASE A");
            END Test */
            swapWasMade = true;
        }
        // Case b:
        else if ((low == null || low.variable != varJ) && (high != null && high.variable == varJ)) {
            Log.println(VERBOSE, "CASE B");
            A = v.low();
            B = v.high().low();
            C = v.high().high();
            newLow = addWithoutRedundant(varI, A, B);
            newHigh = addWithoutRedundant(varI, A, C);
            this.setVertex(v, varJ, newLow, newHigh);
            Log.println(VERBOSE, "{{{{{{{{{{{{{{{{{{{{{");
            Log.println(VERBOSE, v.debugToString());
            Log.println(VERBOSE, newLow.debugToString());
            Log.println(VERBOSE, newHigh.debugToString());
            Log.println(VERBOSE, "}}}}}}}}}}}}}}}}}}}}}");
            
            /*BDD.T.updateNumParents(v);
            BDD.T.updateNumParents(newLow);
            BDD.T.updateNumParents(newHigh);*/
            /* Test 
            testNumParents(v, newLow, newHigh, "CASE B");
            END Test */
            swapWasMade = true;
        }
        // Case c:
        else if ((low != null && low.variable == varJ) && (high != null && high.variable == varJ)) {
            Log.println(VERBOSE, "CASE C");
            A = v.low().low();
            B = v.low().high();
            C = v.high().low();            
            D = v.high().high();
            newLow = addWithoutRedundant(varI, A, C);
            newHigh = addWithoutRedundant(varI, B, D);
            this.setVertex(v, varJ, newLow, newHigh);
            Log.println(VERBOSE, "{{{{{{{{{{{{{{{{{{{{{");
            Log.println(VERBOSE, v.debugToString());
            Log.println(VERBOSE, newLow.debugToString());
            Log.println(VERBOSE, newHigh.debugToString());
            Log.println(VERBOSE, "}}}}}}}}}}}}}}}}}}}}}");
            
            /*BDD.T.updateNumParents(v);
            BDD.T.updateNumParents(newLow);
            BDD.T.updateNumParents(newHigh);*/
            /* Test 
            testNumParents(v, newLow, newHigh, "CASE C");
            END Test */
            swapWasMade = true;
        }
        // Case d:
        else if ((low == null || low.variable != varJ) && (high == null || high.variable != varJ)) {
            Log.println(VERBOSE, "CASE D");
            swapWasMade = false;
        }
        // Case e:
        else if ((low == null || low.variable != varJ) && high == null) {
            Log.println(VERBOSE, "CASE E");
            swapWasMade = false;
        }
        
        if(VERBOSE){
            if(!swapWasMade){
                Log.println(VERBOSE, "-> There was NO SWAP");
            }
        }
        
        return swapWasMade;    
     }
    
    /**
     * Swaps the variable at the level i with the one at the level i+1
     * @paran level Level that will be swapped with the next level.
     * @return informs if the swap was made. Returns true if there was a swap, false otherwise.
     */
    public boolean swap(int level){
        if(VERBOSE){
            BDD.variables().print();
        }
        
        VariableList variables = BDD.variables();
        
        // If is the last level, ignore
        if(level == variables.size()-1){
            return false;
        }
        
        int variableI = variables.getVariableInPosition(level);
        int variableJ = variables.getVariableInPosition(level+1);
        
        //Log.println(VERBOSE, "Let's swap "+variableI+" for "+variableJ+"\n");

        boolean vertexSwapWasMade = false;
        // In other case, start Rudell algorithm to swaps two levels
        TreeSet<Vertex> verticesOfLevel = new TreeSet<Vertex>(new VertexComparator());
        verticesOfLevel.addAll(this.getVerticesWhoseVariableIs(variableI));
        if(VERBOSE){
            Log.println(VERBOSE,"||||||||||||||||||||||||||||||||||||||||||||||||||");
            Log.println(VERBOSE,"We're going to swap level "+level);
            Log.println(VERBOSE,"FALSE IS "+this.False.debugToString());
            Log.println(VERBOSE, verticesOfLevel.size()+" "+verticesOfLevel);
            TreeSet<Vertex> verticesOfNextLevel = new TreeSet<Vertex>(new VertexComparator());
            verticesOfNextLevel.addAll(this.getVerticesWhoseVariableIs(variableJ));
            Log.println(VERBOSE, verticesOfNextLevel.size()+" "+verticesOfNextLevel);
            this.printVSize();
        }        
        for(Vertex v : verticesOfLevel){
            if(v.variable == variableI && !v.isOrphan()){
                Log.println(VERBOSE, "Swapping vertex "+v.debugToString());
                boolean swapWasMadeVertexV = this.swapVertexWithDescendantsWithVariable(v, variableJ);
                vertexSwapWasMade = (swapWasMadeVertexV || vertexSwapWasMade);
                if(VERBOSE){
                    Log.println(VERBOSE, "Swapping vertex "+v+" ENDED\n");
                    //Printer.printTableT("swapping "+vertex_i+" table of vertex.index = "+v.index);
                    //vertex_i++;
                }
            }
        }
        //BDD.T.updateNumParents();
        if(VERBOSE){
            this.printVSize();
            Log.println(VERBOSE, verticesOfLevel.size()+" "+verticesOfLevel);
        }
        
        
        
        // Swap the variables i and j
        BDD.variables().swapVariables(variableI, variableJ);
        
        // If we are in VERBOSE mode, print the variables and the graph
        if(VERBOSE){
            BDD.variables().print();
            //Printer.printTableT("Swap of variable "+variableI+" has been done");
        }
        
        // Count the vertices swaps
        if(vertexSwapWasMade){
            this.incSwapCounter();
        }
        return true;
    }
    
    /**
     * Swaps the variable with the next variable given the variable order.
     * @param variable Variable that will be swapped with the next one.
     * @return informs if the swap was made. Returns true if there was a swap, false otherwise.
     */
    public boolean swapVariable(int variable){
        VariableList variables = BDD.variables();
        int level = variables.getPositionOfVariable(variable);
        return this.swap(level);
    }
    
    /**
     * Swaps the variable from the level i to level i-1.
     * @paran level Level that will be swapped with the preceding level.
     * @return informs if the swap was made. Returns true if there was a swap, false otherwise.
     */
    public boolean swapBack(int level){
        if (level == 0)
            return false;
        return this.swap(level-1);
    }
    
    /**
     * Swaps the variable with the previous variable given the variable order.
     * @param variable Variable that will be swapped with the previous one.
     * @return informs if the swap was made. Returns true if there was a swap, false otherwise.
     */
    public boolean swapBackVariable(int variable){
        VariableList variables = BDD.variables();
        int level = variables.getPositionOfVariable(variable);
        return this.swapBack(level);
    }    

    
    /**************************************************************************/
    /**************************************************************************/
    /* Exchange two levels (uses the swap operation) */
    
    public void exchange(int levelI, int levelJ){
        VariableList variables = BDD.variables();
        
        int variableI = variables.getVariableInPosition(levelI);
        int variableJ = variables.getVariableInPosition(levelJ);
        
        // First we swap forward the variable at levelI to the position
        // of variable in levelJ
        for(int level=levelI; level<levelJ; level++){
            this.swap(level);
        }
        
        // Second, we swap back the variable at levelJ-1 to the position
        // of variable in levelI
        for(int level=levelJ-1; level>levelI; level--){
            this.swapBack(level);
        }
        
        // Swap the variables i and j
        BDD.variables().swapVariables(variableI, variableJ);
    }
    
    /**
     * Moves the variable to a new position.
     * @param varIndex Index of the variable to move ot its best position.
     * @param varNewPosition Future position of the variable with varIndex.
     */
    public void moveVariable(int varIndex, int varNewPosition){
        VariableList variables = BDD.variables();
        // Move to the best position
        int varPosition = variables.getPositionOfVariable(varIndex);
        boolean swapWasMade = true;
        // If we have got the variable before the new position we move forward
        // the variable
        if(varPosition < varNewPosition){
            while (swapWasMade && varPosition < varNewPosition) {
                swapWasMade = this.swap(varPosition);
                if (swapWasMade) {
                    varPosition++;
                }
            }
        }
        // Otherwise we move backward the variable
        else if(varPosition > varNewPosition){
            
            while (swapWasMade && varPosition > varNewPosition) {
                swapWasMade = this.swapBack(varPosition);
                if (swapWasMade) {
                    varPosition--;
                }
            }
        }
         
        /*
        if(varPosition < varNewPosition){
           
            do{
                swapWasMade = this.swapVariable(varIndex);
            }while(swapWasMade && variables.getPositionOfVariable(varIndex)<varNewPosition);
            
        }else if(varPosition > varNewPosition){
            System.out.println("swap back");
            do{
                swapWasMade = this.swapBackVariable(varIndex);
                varPosition = variables.getPositionOfVariable(varIndex);
                System.out.println(varPosition);
                System.out.println(swapWasMade);
            }while(swapWasMade && varPosition>varNewPosition);
        }*/
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Get other information */
    
    /**
     * Number of vertices.
     * @return Number of vertices currently in the table.
     */
    public int size(){
        return this.gc();
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* I/O */
    
    private String getVariableName(int variable){
        if (variable == Vertex.TRUE_VARIABLE){
            return "True";
        }
        if (variable == Vertex.FALSE_VARIABLE) {
            return "False";
        }
        return BDD.VARIABLES.get(variable);
    }
    
    private String getVertexVariableName(Vertex v){
        return this.getVariableName(v.variable());
    }
    
    /**
     * Returns the String representation of the table.
     * @return String representation of the table.
     */
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder("u\tvar_i\tvar\tlow\thigh\tparents\tbdds\n");
        for (Vertex v : this.values()) {
            String variable = this.getVertexVariableName(v);
            s.append(v.index);
            s.append("\t");
            s.append(v.variable);
            s.append("\t");
            s.append(variable);
            s.append("\t");
            s.append(v.lowIndex());
            s.append("\t");
            s.append(v.highIndex());
            s.append("\t");
            s.append(v.numberOfParents());
            s.append("\t");
            s.append(v.numberOfRootedBDDs());
            s.append("\n");
        }
        return s.toString();
    }
    
    /**
     * Prints the table hash to the terminal.
     */
    public void print(){
        System.out.println(this.toString());
    }
    
    public void printT(){
        System.out.println(this.toString());
    }
    
    public void printU(){
        StringBuilder s = new StringBuilder("key\tvar_i\tvar\tlow\thigh\n");
        for(String key : this.U.keySet()){
            Vertex v = this.U.get(key);
            if(v != null){
                String variable = this.getVertexVariableName(v);
                s.append(key);
                s.append("\t");
                s.append(v.variable);
                s.append("\t");
                s.append(variable);
                s.append("\t");
                s.append(v.lowIndex());
                s.append("\t");
                s.append(v.highIndex());
                s.append("\t");
                s.append("\n");
            }
        }
        System.out.println(s);
    }
    
    public void printV(){
        StringBuilder s = new StringBuilder("var_i (variable)\n");
        for (Integer var : this.V.keySet()) {
            s.append(var).append(" (").append(this.getVariableName(var)).append(") \n");
            HashSet<Vertex> vertices = this.V.get(var);
            s.append("\tkey\tvar_i\tvar\tlow\thigh\n");
            for (Vertex v : vertices) {
                if (v != null) {
                    String variable = this.getVertexVariableName(v);
                    s.append("\t");
                    s.append(v.index);
                    s.append("\t");
                    s.append(v.variable);
                    s.append("\t");
                    s.append(variable);
                    s.append("\t");
                    s.append(v.lowIndex());
                    s.append("\t");
                    s.append(v.highIndex());
                    s.append("\t");
                    s.append("\n");
                }
            }
        }
        System.out.println(s);
    }
    
    public void printVSize(){
        StringBuilder s = new StringBuilder("var_i (variable)\n");
        for (Integer var : this.V.keySet()) {
            s.append(var).append(" (").append(this.getVariableName(var)).append("): ");
            HashSet<Vertex> vertices = this.V.get(var);
            int size = 0;
            for (Vertex v : vertices) {
                if (v != null && !v.isOrphan()) {
                    size++;
                }
            }
            s.append(size).append("\n");
        }
        System.out.println(s);
    }
    
    public void debugPrint(){
        System.out.println("");
        System.out.println("Variables");
        BDD.VARIABLES.print();
        System.out.println("");
        System.out.println("V hash");
        this.printV();
        System.out.println("");
        System.out.println("U hash");
        this.printU();
        System.out.println("");
        System.out.println("T hash");
        System.out.println(this.toString());
    }
    
    /**
     * Writes the String representation of the table to a writer.
     * @param writer PrintWriter that will be used to write String representation of the table in a file.
     */
    public void write(PrintWriter writer){
        VariableList variables = BDD.variables();
        writer.println("u\tvar_i\tvar\tlow\thigh");
        for (Vertex v : this.values())
        {
            StringBuilder s = new StringBuilder("");
            String variable;
            if (v.variable == Vertex.TRUE_VARIABLE || v.variable == Vertex.FALSE_VARIABLE) {
                variable = Boolean.toString(v.value());
            }
            else{
                variable = variables.get(v.variable);
            }
            s.append(v.index);
            s.append("\t");
            s.append(v.variable);
            s.append("\t");
            s.append(variable);
            s.append("\t");
            s.append(v.lowIndex());
            s.append("\t");
            s.append(v.highIndex());
            //s.append("\t");
            //s.append(v.parents());
            writer.println(s.toString());
        }
        writer.flush();
    }
    
    /**
     * Reads from a bufferedreader a table with vertices.
     * @param br BufferedReader.
     */
    void fromBufferedReader(BufferedReader br){
        TreeMap<Integer,Vertex> tempT = new TreeMap<Integer,Vertex>();
        HashMap<Integer,Integer> lows = new HashMap<Integer,Integer>();
        HashMap<Integer,Integer> highs = new HashMap<Integer,Integer>();
        // Lets read the vertices
        String line = "";
        try{
            while ((line = br.readLine()) != null) {

                if (line.length() == 0) {
                    break;
                }

                // First loop, we init the vertices
                while ((line = br.readLine()) != null) {
                    if (line.length() == 0) {
                        break;
                    }
                    
                    String[] attributes = line.split("\\s+");
                    int index = Integer.parseInt(attributes[0]);
                    int varIndex = Integer.parseInt(attributes[1]);
                    Vertex v = null;
                    if(index == Vertex.FALSE_INDEX){
                        v = new Vertex(false);
                    }
                    else if(index == Vertex.TRUE_INDEX){
                        v = new Vertex(true);
                    }
                    else
                    {
                        v = new Vertex(index, varIndex, null, null);
                        int low = Integer.parseInt(attributes[3]);
                        lows.put(index, low);
                        int high = Integer.parseInt(attributes[4]);
                        highs.put(index, high);
                    }
                    
                    tempT.put(index, v);
                }
          
                // Second loop, we set low and high descendants
                for(Integer key : tempT.keySet()){
                    Vertex v = tempT.get(key);
                    if(!v.isLeaf()){
                        Vertex low = tempT.get(lows.get(v.index));
                        Vertex high = tempT.get(highs.get(v.index));
                        v.setHigh(high);
                        v.setLow(low);
                    }
                }
               
                for(Integer key : tempT.keySet()){
                    this.add(tempT.get(key));
                }
            }
        }
        catch(Exception e){
            System.err.println("Error in TableT.fromBufferedReader");
            e.printStackTrace();
        }
    }
    
}
