/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import java.util.*;

/**
 *
 * @author diegoj
 */
public class TableT {
    
    HashMap<Integer,Vertex> T;
    private int lastKey = 0;
    
    public TableT(){
        this.T = new HashMap<Integer,Vertex>();
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
        lastKey = index;
        return index;
    }
    
    public ArrayList<Integer> getIndices(){
        return new ArrayList<Integer>(this.T.keySet());
    }
    
    public ArrayList<Vertex> getVertices(){
        return new ArrayList<Vertex>(this.T.values());
    }
    
    
}
