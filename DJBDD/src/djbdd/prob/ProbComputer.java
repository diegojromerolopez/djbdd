/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.prob;

import djbdd.*;
import java.util.*;

/**
 *
 * @author diegoj
 */
public class ProbComputer {
    
    /** BDD whose variable probabilites will be computed */
    private final BDD bdd;
    private final ArrayList<Vertex> vertices;
    private final int size;
    private final HashMap<Integer,Integer> ids;
    private HashMap<Integer,Boolean> visited;
    
    public ProbComputer(BDD bdd){
        // BDD
        this.bdd = bdd;
        
        ArrayList<Vertex> bddVertices = bdd.vertices();
        
        // Number of vertices
        this.size = this.vertices.size();
        
        // Assign contigous ids to the vertices
        // First we sort the vertices in ascending index order
        TreeMap<Integer,Vertex> sorter = new TreeMap<Integer,Vertex>();
        for(Vertex v : bdd.vertices())
            sorter.put(v.index, v);
        // Later we assign to each one a new id that is contigous
        this.vertices = new ArrayList<Vertex>(this.size);
        this.ids = new HashMap<Integer,Integer>(this.size);
        int currentId = 0;
        for(Integer index : sorter.keySet())
        {
            this.vertices.add(bddVertices.get(index));
            this.ids.put(index,currentId);
            currentId++;
        }
        
        // Have been some vertex visited?
        this.visited = new HashMap<Integer,Boolean>();
        for(Vertex v : this.vertices)
            this.visited.put(v.index,false);
    }
    
    private int getId(Vertex v){
        return this.ids.get(v.index);
    }
    
    
}
