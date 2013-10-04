package djbdd;

import djbdd.io.Printer;

import java.util.*;
import java.io.*;
import java.lang.ref.WeakReference; 
import djbdd.timemeasurer.TimeMeasurer;

/**
 * Wrapper of the hashmap that contains the vertices of a BDD.
 * @author diegoj
 */
public class TableT {
    
    /** Default initial capacity of the hash tables */
    public static int INITIAL_CAPACITY = 1000;
    
    /** Default load factor of the hash tables */
    public static final float LOAD_FACTOR = 0.75f;
    
    /** Should operations be verbose? */
    private static final boolean VERBOSE = false;
    
    /**
     * Vertex hashmap.
     * Contains every vertex of the graph given its index.
     * Note the use of weakreferences, because when a Vertex has no parents, must be erased by the garbage collector.
     */
    private HashMap<Integer,WeakReference<Vertex>> T;
    
    /**
     * Uniqueness hashmap: keys are the string "var_i+-+low_i+-+high_i"
     * Note the use of weakreferences, because when a Vertex has no parents, must be erased by the garbage collector.
     */
    private HashMap<String,WeakReference<Vertex>> U;
    
    /**
     * Variable vertices hash.
     * This hash contains the vertices of the graph grouped by its variables.
     */
    public HashMap<Integer,WeakHashMap<Vertex,Boolean>> V;
    
    /** Used to increase key creation performance */
    private int lastKey = 0;
    
    /** Vertex that represents the false value */
    public final Vertex False;
    
    /** Vertex that represents the true value */
    public final Vertex True;
    
    /**
     * Init the hashes wit initial capacity and load factor.
     * @param initialCapacity Initial capacity of the hash tables.
     * @param loadFactor Load factor of the hash tables.
     */
    private void initHashes(int initialCapacity, float loadFactor){
        this.T = new HashMap<Integer,WeakReference<Vertex>>(initialCapacity, loadFactor);
        this.U = new HashMap<String,WeakReference<Vertex>>(initialCapacity, loadFactor);
        
        this.V = new HashMap<Integer,WeakHashMap<Vertex,Boolean>>(initialCapacity, loadFactor);
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
        return this.U.containsKey(uniqueKey) && this.U.get(uniqueKey).get()!=null;
    }

    /**
     * Informs if a vertex unique key exists in the hash.
     * See {@link djbdd.Vertex#uniqueKey}.
     * @param vKey Vertex key that will be tested for existence in the hash.
     * @return true if exists vertex whose unique key is vKey, false otherwise.
     */
    public synchronized boolean containsVertex(String vKey){
        return this.U.containsKey(vKey) && this.U.get(vKey).get()!=null;
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
        this.U.put(v.uniqueKey(), new WeakReference<Vertex>(v));
    }

    /**
     * Put a vertex in the table T.
     * @param v Vertex that will be added to T.
     * @see TableT#T
     */
    private synchronized void putInT(Vertex v){
        this.T.put(v.index, new WeakReference<Vertex>(v));
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
            this.V.put(variable, new WeakHashMap<Vertex,Boolean>());
        }
        this.V.get(variable).put(v,true);
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
        WeakReference<Vertex> rRemoved = T.get(index);
        Vertex removed = rRemoved.get();
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


    /**************************************************************************/
    /**************************************************************************/
    /* Reduction */
    
    /**
     * Deletes a vertex from the BDD.
     * Updates the references of the vertices of the BDD accordingly our deletion.
     * Modifies the hash T
     * @param deletedVertex Vertex to be deleted.
     * @deprecated Only called by deleteRedundantVertices.
     */
    private void deleteRedundantVertex(Vertex deletedVertex){
        Vertex low = deletedVertex.low();
        Vertex high = deletedVertex.high();
        T.remove(deletedVertex.index);
        // Update the low and high pointers to the new values
        // (the low and high of the deleted vertex resp.)
        for(WeakReference<Vertex> w : this.T.values()){
            Vertex v = w.get();
            if(v!=null){
                if(v.low() == deletedVertex)
                    v.setLow(low);
                if(v.high() == deletedVertex)
                    v.setHigh(high);
            }
        }
    }
    
    /**
     * Delete all redundante vertices of the tree.
     * That is, all vertex with the same low and high values.
     * Of course modifies the T table.
     * @return boolean True if there has been a deletion of a redundant vertex. False otherwise.
     * @deprecated Only called by reduce.
     */
    private boolean deleteRedundantVertices(){
        TimeMeasurer t = new TimeMeasurer("---- deleteRedundantVertices ----");
        boolean deleted = false;
        for(Vertex v : this.values()){
            //Vertex v = this.T.get(i);
            if(v.isRedundant()){
                //System.out.println("Eliminamos el "+v.index);
                deleted = true;
                this.deleteRedundantVertex(v);
            }
        }//*/
        t.end().show();
        return deleted;
    }
    
 
    /**
     * Gets all duplicate vertex indices.
     * @deprecated Only called by reduce.
     */
    private ArrayList<Integer> getDuplicateVertexIndices(Vertex v){
        
        ArrayList<Integer> vertixKeys = new ArrayList<Integer>(this.T.keySet());
        ArrayList<Integer> duplicates = new ArrayList<Integer>();
        
        for(int k : vertixKeys){
            if(k!=Vertex.TRUE_INDEX && k!=Vertex.FALSE_INDEX && k != v.index){
                WeakReference<Vertex> w = T.get(k);
                Vertex wVertex = w.get();
                if(wVertex!=null && v.isDuplicate(wVertex))
                    duplicates.add(wVertex.index);
            }
        }
        return duplicates;
    }
    
    
    /**
     * Delete all duplicate vertices.
     * @deprecated Only called by reduce.
     */
    private boolean deleteDuplicateVertices(){
        TimeMeasurer t = new TimeMeasurer("++++++ deleteDuplicateVertices ++++++");
        // Uniqueness
        boolean _change = false;
        boolean change = false;
        do{
            change = false;
            ArrayList<Integer> vertexKeys = new ArrayList<Integer>(this.T.keySet());
            // For every vertex that is not True or False
            for(Integer k : vertexKeys){
                if(k!=Vertex.TRUE_INDEX && k!=Vertex.FALSE_INDEX){
                    WeakReference<Vertex> rV = T.get(k);
                    Vertex v = rV.get();
                    if(v != null){
                        // Get all of its duplicates
                        ArrayList<Integer> duplicateIndices = getDuplicateVertexIndices(v);
                        change = change || duplicateIndices.size()>0;
                        for(Integer d : duplicateIndices){
                            ArrayList<Integer> remining = new ArrayList<Integer>(this.T.keySet());
                            for(Integer q : remining){
                                WeakReference<Vertex> rvQ = T.get(q);
                                Vertex vQ = rvQ.get();
                                if(vQ!=null){
                                    if(vQ.low().index == d)
                                        vQ.setLow(v);
                                    if(vQ.high().index == d)
                                        vQ.setHigh(v);
                                }
                            }
                            T.remove(d);
                            _change = true;
                        }
                    }
                }
            }
        }
        while(change);
        t.end().show();
        return _change;
    }
    
    
    /**
     * Reduces the BDD deleting redundant and duplicate vertices.
     * @deprecated There are no need to call this method.
     */
    public synchronized void reduce(){
        TimeMeasurer t = new TimeMeasurer("********* REDUCE *********");
        boolean change = false;
        do{
            change = this.deleteRedundantVertices();
            change = change || this.deleteDuplicateVertices();
        }while(change);
        //this.assignNewIndices();
        // Asignamos U
        //this.updateU();
        // Asignamos la raiz
        //this.assignRoot();
        t.end().show();
    }
    
    /**
     * Calls the garbage collector that deletes the references to dead objects.
     */
    public synchronized int gc(){
        if(VERBOSE)
            System.out.println("<<<<<<<<<<<<<<< GC >>>>>>>>>>>>>>>>>");
        
        // Compact the hash maps
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
                    Vertex v = this.T.get(key).get();
                    if(v == null || v.isOrphan()){
                        if(VERBOSE){
                            System.out.println("DELETING "+v);
                        }
                        if(v!=null){
                            Vertex.decNumParentsOfVertex(v.low());
                            Vertex.decNumParentsOfVertex(v.high());
                        }
                        thereIsADeletion = true;
                        this.remove(key);
                        deletions++;
                        if(VERBOSE){
                            if(v!=null){
                                System.out.println("DELETED: "+key+" "+v);
                            }else{
                                System.out.println("DELETED: "+key+" NULL");
                            }
                        }
                    }
                }
            }
        }
        if(VERBOSE){
            System.out.println(deletions+" vertices deleted");
            System.out.println("<<<<<<<<<<<<<< END GC >>>>>>>>>>>>>>");
        }
        _size -= deletions;
        if(VERBOSE){
            System.out.println("The size is "+_size);
        }
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
        WeakReference<Vertex> w = this.T.get(index);
        return w.get();
    }
    
    /**
     * Gets a vertex with the key given by vKey.
     * See {@link djbdd.Vertex#uniqueKey}. 
     * @param vKey Unique key of the vertex.
     * @return Vertex with uniqueness key vKey.
     */
    public Vertex get(String vKey){
        WeakReference<Vertex> w = this.U.get(vKey);
        return w.get();
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
        for(WeakReference<Vertex> w : this.T.values()){
            Vertex v = w.get();
            if(v!=null)
                vertices.add(v);
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
            return new HashSet<Vertex>();
        }
        // We have some vertices with this variable
        WeakHashMap<Vertex, Boolean> verticesWithThatVariable = V.get(variable);
        return verticesWithThatVariable.keySet();
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
        
        /*Vertex A = null;
        Vertex B = null;
        if (!low.isLeaf()) {
            A = low.low();
            B = low.high();
        } else {
            A = low;
            B = low;
        }

        Vertex C = null;
        Vertex D = null;
        if (!high.isLeaf()) {
            C = high.low();
            D = high.high();
        } else {
            C = high;
            D = high;
        }*/

        Vertex newLow = null;
        Vertex newHigh = null;

        // Case a:
        if (low != null && low.variable == varJ && (high == null || high.variable != varJ)) {
            if(VERBOSE){
                System.out.println("CASE A");
                System.out.flush();
            }
            A = v.low().low();
            B = v.low().high();
            C = v.high();
            newLow = addWithoutRedundant(varI, A, C);
            newHigh = addWithoutRedundant(varI, B, C);
            this.setVertex(v, varJ, newLow, newHigh);
            swapWasMade = true;
        }
        // Case b:
        else if ((low == null || low.variable != varJ) && (high != null && high.variable == varJ)) {
            if(VERBOSE){
                System.out.println("CASE B");
                System.out.flush();
            }
            A = v.low();
            B = v.high().low();
            C = v.high().high();
            /*
            System.out.println(A);
            System.out.println(B);
            System.out.println(C);*/
            newLow = addWithoutRedundant(varI, A, B);
            newHigh = addWithoutRedundant(varI, A, C);
            //System.out.println(newLow);
            //System.out.println(newHigh);
            this.setVertex(v, varJ, newLow, newHigh);
            swapWasMade = true;
        }
        // Case c:
        else if ((low != null && low.variable == varJ) && (high != null && high.variable == varJ)) {
            if(VERBOSE){
                System.out.println("CASE C");
                System.out.flush();
            }
            A = v.low().low();
            B = v.low().high();
            C = v.high().low();            
            D = v.high().high();
            newLow = addWithoutRedundant(varI, A, C);
            newHigh = addWithoutRedundant(varI, B, D);
            this.setVertex(v, varJ, newLow, newHigh);
            swapWasMade = true;
        }
        // Case d:
        else if ((low == null || low.variable != varJ) && (high == null || high.variable != varJ)) {
            if(VERBOSE){
                System.out.println("CASE D");
                System.out.flush();
            }
            swapWasMade = false;
        }
        // Case e:
        else if ((low == null || low.variable != varJ) && high == null) {
            if(VERBOSE){
                System.out.println("CASE E");
                System.out.flush();
            }
            swapWasMade = false;
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
        if(level == variables.size()-1)
            return false;
    
        int variableI = variables.getVariableInPosition(level);
        int variableJ = variables.getVariableInPosition(level+1);
        
        if(VERBOSE){
            System.out.println("Let's swap "+variableI+" for "+variableJ+"\n");
            System.out.flush();
        }
        
        boolean swapWasMade = false;
        // In other case, start Rudell algorithm to swaps two levels
        HashSet<Vertex> verticesOfLevel = new HashSet<Vertex>(this.getVerticesWhoseVariableIs(variableI));
        int vertex_i=0;
        for(Vertex v : verticesOfLevel){
            if(v.variable == variableI){
                if(VERBOSE){
                    System.out.println("Swapping vertex "+v);
                }
                swapWasMade = swapWasMade || this.swapVertexWithDescendantsWithVariable(v, variableJ);
                if(VERBOSE){
                    System.out.println("Swapping vertex "+v+" ENDED");
                    System.out.println("");
                    System.out.flush();
                    Printer.printTableT("swapping "+vertex_i+" table of "+v.index);
                    vertex_i++;
                }
            }
        }
    
        if(VERBOSE){
            //Printer.printTableT("Before Swap of variable "+variableI+" has been done in variables");
        }
        
        BDD.variables().swapVariables(variableI, variableJ);
        if(VERBOSE){
            BDD.variables().print();
        }
        if(VERBOSE){
            Printer.printTableT("Swap of variable "+variableI+" has been done");
        }
        return swapWasMade;
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
            WeakReference<Vertex> w = this.U.get(key);
            Vertex v = w.get();
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
                //s.append(v.parents());
                s.append("\n");
            }
        }
        System.out.println(s);
    }
    
    public void printV(){
        StringBuilder s = new StringBuilder("var_i (variable)\n");
        for (Integer var : this.V.keySet()) {
            s.append(var).append(" (").append(this.getVariableName(var)).append(") \n");
            WeakHashMap ws = this.V.get(var);
            Set<Vertex> vertices = ws.keySet();
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
                    //s.append(v.parents());
                    s.append("\n");
                }
            }
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
                        v.setHigh(low);
                        v.setLow(high);
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
