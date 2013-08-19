package djbdd;

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
        int vInitialCapacity = initialCapacity;
        this.V = new HashMap<Integer,WeakHashMap<Vertex,Boolean>>(vInitialCapacity, loadFactor);
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
        if(this.containsVertex(v))
            return v;
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
        v.setVariable(variable);
        v.setLow(low);
        v.setHigh(high);
        this.putInU(v);
        this.V.get(v.variable).put(v, true);
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
    public synchronized void gc(boolean suggestSystemGC){
        if(suggestSystemGC){
            System.gc();
        }
        
        boolean verbose = false;
        if(verbose)
            System.out.println("<<<<<<<<<<<<<<< GC >>>>>>>>>>>>>>>>>");
        
        // Compact the T and U hashMaps
        ArrayList<Integer> keys = new ArrayList<Integer>(this.T.keySet());
        for(Integer key : keys){
            if(verbose){
                if(this.T.containsKey(key))
                    System.out.println("Key "+key+" exists");
                if(this.T.get(key).get()==null)
                    System.out.println("Key "+key+" MUST DIE");
            }
            
            if(this.T.containsKey(key) && this.T.get(key).get()==null){
                this.remove(key);
            }
        }
        if(verbose)
            System.out.println("<<<<<<<<<<<<<< END GC >>>>>>>>>>>>>>");
    }
    
    /**
     * Calls the garbage collector that deletes the references to dead objects.
     */    
    public synchronized void gc(){
        this.gc(true);
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
        WeakHashMap<Vertex, Boolean> verticesWithThatVariable = V.get(variable);
        return verticesWithThatVariable.keySet();
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Get other information */
    
    /**
     * Number of vertices.
     * @return Number of vertices currently in the table.
     */
    public int size(){
        return this.T.size();
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* I/O */
    
    /**
     * Returns the String representation of the table.
     * @return String representation of the table.
     */
    @Override
    public String toString(){
        ArrayList<String> variables = BDD.variables();
        StringBuilder s = new StringBuilder("u\tvar_i\tvar\tlow\thigh\n");
        for (Vertex v : this.values()) {
            String variable;
            if (v.variable() == Vertex.TRUE_VARIABLE || v.variable() == Vertex.FALSE_VARIABLE) {
                variable = Boolean.toString(v.value());
            }
            else{
                variable = variables.get(v.variable());
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
            s.append("\t");
            //s.append(v.parents());
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
    
    /**
     * Writes the String representation of the table to a writer.
     * @param writer PrintWriter that will be used to write String representation of the table in a file.
     */
    public void write(PrintWriter writer){
        ArrayList<String> variables = BDD.variables();
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
    
}
