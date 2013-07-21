/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;
/**
 
 * NOT USED
 
 */
import java.util.*;

/**
 * Reduction operation for BDDs
 * @author diegoj
 */
public class BDDReduce {
    
    BDD bdd;
    
    public BDDReduce(BDD bdd){
        this.bdd = bdd;
    }

    /**
     * Delete non-unique vertices.
     * A vertex v is non-unique if existes any other with these same atributes:
     *   v.variable
     *   v.low
     *   v.high
     * @return true if there has been a deletion, false otherwise.
     */
    private boolean makeVertexUnique(){
        
        return false;
    }
    
    private void makeVertexNonRedundant(Vertex v){
        if(!v.isRedundant())
        {
            //return v;
        }
        else
        {
            this.bdd.T.remove(v.index);
            for(Vertex w : this.bdd.T.getVertices()){
                if(w.low == v.index) w.low = v.low;
                if(w.high == v.index) w.high = v.low;
                this.makeVertexNonRedundant(w);
            }
        }
    }
    
    /**
     * Delete redundant vertices.
     * A vertex is redundant if v.low == v.high
     * @return true if there has been a deletion, false otherwise.
     */
    private boolean deleteRedundantVertices(){
        boolean anyDeletion = false;
        
        HashMap<Integer,Integer> R = new HashMap<Integer,Integer>();
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(this.bdd.T.getVertices());
        
        HashMap<Integer,ArrayList<Integer>> lowParents = new HashMap<Integer,ArrayList<Integer>>();
        HashMap<Integer,ArrayList<Integer>> highParents = new HashMap<Integer,ArrayList<Integer>>();
        for(Vertex v : vertices){
            if(!lowParents.containsKey(v.low)){
                lowParents.put(v.low, new ArrayList<Integer>());    
            }
            ArrayList<Integer> lParents = lowParents.get(v.low);
            lParents.add(v.index);
            if(!highParents.containsKey(v.high)){
                highParents.put(v.high, new ArrayList<Integer>());    
            }
            ArrayList<Integer> hParents = lowParents.get(v.low);
            hParents.add(v.index);
            
        }
        
        while(!vertices.isEmpty()){
            Vertex vCurrent = vertices.remove(0);
            while(vCurrent.isRedundant()){
                this.bdd.T.remove(vCurrent.index);
                // low y high y esa mierda
                vCurrent = vCurrent.getLowParent(this.bdd.T);
            }
        }
        
        for(Vertex v : vertices){
            if(v.isRedundant()){
                this.bdd.T.remove(v.index);
                R.put(v.index,v.low);
                anyDeletion = true;
            }
        }
        
        for(Integer index : R.keySet()){
            int value = R.get(index);
            while(R.get(index) == value){
                //if(R.containsKey(value)
            }
        }
        
        return anyDeletion;
    }
    
    /**
     * Runs the reduction.
     */
    public void run(){
        boolean BDDHasBeenReduced = false;
        do
        {
            BDDHasBeenReduced = this.deleteRedundantVertices();
            BDDHasBeenReduced = BDDHasBeenReduced && this.makeVertexUnique();
        }
        while(BDDHasBeenReduced);
    }
    
}
