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
public class BDDTreeGenerator extends Thread {
    
    BDD bdd;
    
    /**
     * Main recursively generation of the tree.
     * @param path ArrayList of variables that contains a boolean assignement for each variable.
     * @param U HashMap that ensures the uniqueness of the generated vertices.
     * @return Vertex of each level.
     */
    private Vertex generateTree(ArrayList<Boolean> path, HashMap<String,Vertex> U){
            int path_len = path.size();
            //System.out.println(path.toString());
            //System.out.println(path_len);
            //System.out.flush();
            if (path_len < this.bdd.present_variable_indices.size())
            {
                //System.out.println(path.toString());
                //int variable_index = this.variables.get(path_len);
                //System.out.println("EXISTE "+ path_len +" "+ this.variables.get(variable_index));
                //System.out.flush();
                
                // Low path
                ArrayList<Boolean> path_low = new ArrayList<Boolean>(path);
                path_low.add(false);
                Vertex v_low = this.generateTree(path_low, U);
                
                // High path
                ArrayList<Boolean> path_high = new ArrayList<Boolean>(path);
                path_high.add(true);
                Vertex v_high = this.generateTree(path_high, U);
                
                //////////////// Constraints:
                
                ////////
                //// 1.- Non-redundancy
                // If low and high are the same vertex, we do not create their
                // parent, because it is redundant. We return the child.
                if(v_low.index == v_high.index){
                    return v_low;
                }
                
                ////////
                //// 2.- Uniqueness
                // No two vertices have the same variable, and low and high
                // vertices
                        
                // Variable of the new vertex
                int var_index = this.bdd.present_variable_indices.get(path_len);
                
                // If exists a vertex with the same variable and the same
                // descendents, return that
                String vKey = var_index+"-"+v_low.index+"-"+v_high.index;
                if(U.containsKey(vKey)){
                    return U.get(vKey);
                }
                
                // There are no vertex with
                // this variable, and low and high vertices.
                // We create a new vertex
                int index = this.bdd.T.getNextKey();
                Vertex v = new Vertex(index, var_index, v_low, v_high);
                this.bdd.T.put(index, v);
                U.put(vKey, v);
                return v;
            }
            else if(path_len == this.bdd.present_variable_indices.size())
            {
                // reached leafes
                boolean value = this.bdd.evaluatePath(path);
                if(value){
                    this.bdd.T.put(1,this.bdd.True);
                    return this.bdd.True;
                }
                this.bdd.T.put(0,this.bdd.False);
                return this.bdd.False;
            }
            return null;
    }
    
    public BDDTreeGenerator(BDD bdd){
        this.bdd = bdd;
    }
    
    public void run(){
        ArrayList<Boolean> path = new ArrayList<Boolean>();
        HashMap<String,Vertex> U = new HashMap<String,Vertex>();
        this.generateTree(path, U);
    }
}
