/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    public static int INITIAL_CAPACITY = 1000;
    public static final float LOAD_FACTOR = 0.75f;
    
    /** Wrapped vertex hashmap */
    private HashMap<Integer,WeakReference<Vertex>> T;
    
    /** Uniqueness hashmap: keys are the string "var_i+-+low_i+-+high_i" */
    private HashMap<String,WeakReference<Vertex>> U;
    
    /** Used to increase key creation performance */
    private int lastKey = 0;
    
    public static TableT VERTEX_TABLE = null;
    
    public final Vertex False;
    public final Vertex True;
    
    protected final void initHashes(int initialCapacity, float loadFactor){
        this.T = new HashMap<Integer,WeakReference<Vertex>>(initialCapacity, loadFactor);
        this.U = new HashMap<String,WeakReference<Vertex>>(initialCapacity, loadFactor);
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
    
    public TableT(int initialCapacity){
        this.initHashes(initialCapacity, LOAD_FACTOR);
        // Initialize the leaf vertices
        this.True = new Vertex(true);
        this.False = new Vertex(false);
        this.addNew(this.False);
        this.addNew(this.True);
    }
    
    public synchronized boolean containsKey(Integer key){
        return this.T.containsKey(key);
    }
    
    public synchronized boolean containsVertex(Vertex v){
        String uniqueKey = v.uniqueKey();
        return this.U.containsKey(uniqueKey) && this.U.get(uniqueKey).get()!=null;
    }
    
    public synchronized boolean containsVertex(String vKey){
        return this.U.containsKey(vKey) && this.U.get(vKey).get()!=null;
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Addings */
    
    private synchronized void putInU(Vertex v){
        this.U.put(v.uniqueKey(), new WeakReference(v));
    }
    
    private synchronized void putInT(Vertex v){
        this.T.put(v.index, new WeakReference(v));
    }
    
    /**
     * Adds a new vertex to the table.
     * @param var_index Index of the variable that will have the vertex.
     * @param low Low index of the new vertex.
     * @param high High index of the new vertex.
     * @return Vertex New vertex inserted.
     */
    public synchronized Vertex addNew(int var_index, Vertex low, Vertex high){
        int index = this.getNextKey();
        Vertex v = new Vertex(index, var_index, low, high);
        this.putInT(v);
        this.putInU(v);
        return v;
    }
    
    /**
     * Adds a new vertex to the table.
     * @param index Index of the vertex that will have the vertex in the table T.
     * @param v New vertex.
     * @return Vertex New vertex inserted.
     */    
    private synchronized Vertex addNew(Vertex v){
        this.putInT(v);
        this.putInU(v);
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
        if(removed != null)
            U.remove(removed.uniqueKey());
        if(removed == null)
            return false;
        return true;
    }
    
    /**
     * Removes the vertex with a particular index.
     * @param vertex Vertex to delete.
     */
    public synchronized void remove(Vertex oldVertex){
        int index = oldVertex.index;
        this.remove(index);
    }

    /**************************************************************************/
    /**************************************************************************/
    /* Reduction */
    
    /**
     * Deletes a vertex from the BDD.
     * Updates the references of the vertices of the BDD accordingly our deletion.
     * Modifies the hash T
     * @param deletedVertex Vertex to be deleted.
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
    
    public synchronized void gc(){
        this.gc(true);
    }
    
   
    /**************************************************************************/
    /**************************************************************************/
    
    /* Getters */
    /**
     * Gets a vertex with a index.
     * @param index Index of the vertex in the table T.
     * @return Vertex whose index is index.
     */    
    public Vertex get(int index){
        WeakReference<Vertex> w = this.T.get(index);
        return w.get();
    }
    
    /**
     * Gets a vertex with the key given by vKey.
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
     * @return set with the indices of the T table.
     */
    public Set<Integer> getKeySet(){
        return this.T.keySet();
    }    

    /**
     * Gets the keyset of table T.
     * @return set with the indices of the T table.
     */
    public Set<Integer> keySet(){
        return this.T.keySet();
    }
    
    public synchronized Integer getNextKey(){
        int index = lastKey;
        while(this.T.containsKey(index)){ index++; }
        lastKey = index+1;
        return index;
    }
    
    public ArrayList<Integer> getIndices(){
        return new ArrayList<Integer>(this.T.keySet());
    }
    
    public ArrayList<Vertex> getVertices(){
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(this.T.size());
        for(WeakReference<Vertex> w : this.T.values()){
            Vertex v = w.get();
            if(v!=null)
                vertices.add(v);
        }
        return vertices;
    }
    
    public ArrayList<Vertex> values(){
        return this.getVertices();
    }
    
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
            s.append("\t");
            //s.append(v.parents());
            s.append("\n");
        }
        return s.toString();
    }
    
    public void print(){
        System.out.println(this.toString());
    }
    
    /**
     * Writes the String representation of the table to a writer.
     * @param writer PrintWriter that will be used to write String representation of the table in a file.
     */
    public void write(PrintWriter writer){
        ArrayList<String> variables = BDD.variables();
        StringBuilder s = new StringBuilder("u\tvar_i\tvar\tlow\thigh\n");
        for (Vertex v : this.values())
        {
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
            s.append("\n");
        }
        writer.println(s.toString());
        writer.flush();
    }
    
}
