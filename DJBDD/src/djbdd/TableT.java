/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import java.util.*;

/**
 * Wrapper of the hashmap that contains the vertices of a BDD.
 * @author diegoj
 */
public class TableT {
    
    public static int INITIAL_CAPACITY = 1000;
    public static final float LOAD_FACTOR = 0.75f;
    
    /** Hashmaps that wraps */
    HashMap<Integer,Vertex> T;
    
    /** Used to increase key creation performance */
    private int lastKey = 0;
    
    public TableT(){
        this.T = new HashMap<Integer,Vertex>(INITIAL_CAPACITY, LOAD_FACTOR);
    }
    
    public TableT(int initialCapacity){
        this.T = new HashMap<Integer,Vertex>(initialCapacity, LOAD_FACTOR);
    }
    
    public boolean containsKey(Integer key){
        return T.containsKey(key);
    }
    
    public Vertex get(int index){
        return T.get(index);
    }
    
    public Vertex put(int index, Vertex v){
        return T.put(index, v);
    }
    
    public void remove(int index){
        //Vertex removed = T.get(index);
        T.remove(index);
    }
    
    public Set<Integer> getKeySet(){
        return this.T.keySet();
    }    
        
    public Set<Integer> keySet(){
        return this.T.keySet();
    }
    
    public Integer getNextKey(){
        int index = lastKey;
        while(T.containsKey(index)){ index++; }
        lastKey = index+1;
        return index;
    }
    
    public ArrayList<Integer> getIndices(){
        return new ArrayList<Integer>(this.T.keySet());
    }
    
    public ArrayList<Vertex> getVertices(){
        return new ArrayList<Vertex>(this.T.values());
    }
    
    public Collection<Vertex> values(){
        return this.T.values();
    }
    
    
}
