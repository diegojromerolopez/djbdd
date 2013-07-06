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
    
    public TableT(){
        this.T = new HashMap<Integer,Vertex>();
    }
    
    public boolean containsKey(Integer key){
        return T.containsKey(key);
    }
    
    public Vertex get(int index){
        return T.get(index);
    }
    
    public void put(int index, Vertex v){
        T.put(index, v);
    }
    
    public void put(Vertex v){
        int index = 2;
        while(T.containsKey(index)){ index++; }
        T.put(index, v);
    }
    
    public void remove(int index){
        T.remove(index);
    }
    
    public Set<Integer> getKeySet(){
        return this.T.keySet();
    }    
        
    public Set<Integer> keySet(){
        return this.T.keySet();
    }
    
    public ArrayList<Integer> getIndices(){
        return new ArrayList<Integer>(this.T.keySet());
    }
    
    public ArrayList<Vertex> getVertices(){
        return new ArrayList<Vertex>(this.T.values());
    }
    
    
}
