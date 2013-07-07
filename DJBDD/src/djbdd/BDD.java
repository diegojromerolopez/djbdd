/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import java.util.*;
//import java.util.regex.*;
import org.mvel2.MVEL;

/**
 *
 * @author diegoj
 */
public class BDD {
    
    String function;
    ArrayList<String> variables;
    ArrayList<Boolean> variable_existence;
    ArrayList<Integer> present_variable_indices;
    
    TableT T;
    HashMap<String,Vertex> H;

    Vertex root = null;
    Vertex True;
    Vertex False;
    
    /**
     * Evaluates the formula given a path (an assignement of variables)
     * @param path ArrayList of variables that contains a boolean assignement for each variable.
     * @return boolean Evaluation of the formula using this assignment.
     */
    protected boolean evaluatePath(ArrayList<Boolean> path){
        // TODO: evaluatePath the formula
        String _function = this.function;
        for(int i=0; i<this.present_variable_indices.size(); i++){
            int variable_index = present_variable_indices.get(i);
            String variable = this.variables.get(variable_index);
            String value = path.get(i)?"true":"false";
            _function = _function.replaceAll(variable, value);
        }
        
        return (Boolean)MVEL.eval(_function);
    }
    
    /**
     * Main recursively generation of the tree.
     * @param path ArrayList of variables that contains a boolean assignement for each variable.
     * @return Vertex of each level.
     */
    private Vertex generateTreeFunction(ArrayList<Boolean> path){
            int path_len = path.size();
            //System.out.println(path.toString());
            //System.out.println(path_len);
            //System.out.flush();
            if (path_len < this.present_variable_indices.size())
            {
                //System.out.println(path.toString());
                //int variable_index = this.variables.get(path_len);
                //System.out.println("EXISTE "+ path_len +" "+ this.variables.get(variable_index));
                //System.out.flush();
                ArrayList<Boolean> path_low = new ArrayList<Boolean>(path);
                path_low.add(false);
                Vertex v_low = this.generateTreeFunction(path_low);
                
                ArrayList<Boolean> path_high = new ArrayList<Boolean>(path);
                path_high.add(true);
                Vertex v_high = this.generateTreeFunction(path_high);
                //System.out.println("Parent of "+v_low.id+" y "+v_high.id);
                // Create a new vertex
                int index = 2;
                while(T.containsKey(index)){
                    index++;
                }
                int var_index = this.present_variable_indices.get(path_len);
                Vertex v = new Vertex(index, var_index, v_low, v_high);
                this.T.put(index, v);
                return v;
            }
            else if(path_len == this.present_variable_indices.size())
            {
                // reached leafes
                boolean value = this.evaluatePath(path);
                if(value)
                    return this.True;
                return this.False;
            }
            //System.out.println("WRONG");
            return null;
    }
    
    /**
     * Deletes a vertex from the BDD.
     * Updates the references of the vertices of the BDD accordingly our deletion.
     * Modifies the hash T
     * @param deletedVertex Vertex to be deleted.
     */
    private void deleteRedundantVertex(Vertex deletedVertex){
        int low = deletedVertex.low;
        int high = deletedVertex.high;
        T.remove(deletedVertex.index);
        // Update the low and high pointers to the new values
        // (the low and high of the deleted vertex resp.)
        ArrayList<Integer> vertexIndices = new ArrayList<Integer>(T.keySet());
        for(Integer i : vertexIndices){
            Vertex v = T.get(i);
            if(v.low == deletedVertex.index)
                v.low = low;
            if(v.high == deletedVertex.index)
                v.high = high;
        }
    }
    
    /**
     * Delete all redundante vertices of the tree.
     * That is, all vertex with the same low and high values.
     * Of course modifies the T table.
     */
    private void deleteRedundantVertices(){
        // Non-redundancy: no vertex has same low and high
        ArrayList<Integer> vertixKeys = new ArrayList<Integer>(this.T.keySet());
        for(Integer i : vertixKeys){
            Vertex v = this.T.get(i);
            if(v.isRedundant()){
                //System.out.println("Eliminamos el "+v.index);
                this.deleteRedundantVertex(v);
            }
        }    
    }
    
    private ArrayList<Vertex> getDuplicateVertices(Vertex v){
        
        ArrayList<Integer> vertixKeys = new ArrayList<Integer>(this.T.keySet());
        ArrayList<Vertex> duplicates = new ArrayList<Vertex>();
        
        for(int i : vertixKeys){
            if(i!=v.index){
                Vertex w = T.get(i);
                if(v.equals(w))
                    duplicates.add(w);
            }
        }
        return duplicates;
    }
    
    private ArrayList<Integer> getDuplicateVertexIndices(Vertex v){
        
        ArrayList<Integer> vertixKeys = new ArrayList<Integer>(this.T.keySet());
        ArrayList<Integer> duplicates = new ArrayList<Integer>();
        
        for(int k : vertixKeys){
            if(k!=Vertex.TRUE_INDEX && k!=Vertex.FALSE_INDEX && k != v.index){
                Vertex w = T.get(k);
                if(v.isDuplicate(w))
                    duplicates.add(w.index);
            }
        }
        return duplicates;
    }
    
    
    /**
     * Delete all duplicate vertices.
     */
    private void deleteDuplicateVertices(){
        // Uniqueness
        boolean change = false;
        do{
            change = false;
            ArrayList<Integer> vertexKeys = new ArrayList<Integer>(this.T.keySet());
            // For every vertex that is not True or False
            for(Integer k : vertexKeys){
                if(k!=Vertex.TRUE_INDEX && k!=Vertex.FALSE_INDEX){
                    Vertex v = T.get(k);
                    if(v != null){
                        // Get all of its duplicates
                        ArrayList<Integer> duplicateIndices = getDuplicateVertexIndices(v);
                        change = change || duplicateIndices.size()>0;
                        for(Integer d : duplicateIndices){
                            ArrayList<Integer> remining = new ArrayList<Integer>(this.T.keySet());
                            for(Integer q : remining){
                                Vertex vQ = T.get(q);
                                if(vQ.low == d)
                                    vQ.low = k;
                                if(vQ.high == d)
                                    vQ.high = k;
                            }
                            T.remove(d);
                        }
                    }
                }
            }
        }
        while(change);
    }
    
    
    /**
     * Assigns consecutive indices to the vertex.
     * It has no computational purpose, only estetic one. 
     */
    private void assignNewIndices(){
        ArrayList<Integer> vertixKeys = new ArrayList<Integer>(this.T.keySet());
        int i = 2;
        for(Integer k : vertixKeys){
            if(k > 1)
            {
                Vertex v = this.T.get(k);
                T.remove(k);
                for(Integer k2 : vertixKeys)
                {
                    if(k2 != k && T.containsKey(k2))
                    {
                        //System.out.println("Para el vértice "+k2+" sustituimos "+k+" -> "+i);
                        //System.out.flush();
                        Vertex other = T.get(k2);
                        if(other.high == k)
                            other.high = i;
                        if(other.low == k)
                            other.low = i;
                    }
                }
                v.index = i;
                T.put(i, v);
                i++;
            }
        }
    }
    
    private void assignRoot(){
        ArrayList<Integer> vertexKeys = new ArrayList<Integer>(this.T.keySet());
        this.root = this.T.get(Collections.max(vertexKeys));
    }
    
    private void updateH(){
        //System.out.flush();
        this.H = new HashMap<String,Vertex>();
        ArrayList<Integer>  indices = new ArrayList<Integer>(this.T.keySet());
        for(Integer index : indices){
            Vertex v = this.T.get(index);
            String vKey = v.keyH();
            //System.out.println(vKey +" -> "+v.toString());
            this.H.put(vKey, v);
        }
        //System.out.flush();
    }
    
    /**
     * Reduces the BDD deleting redundant and duplicate vertices.
     */
    public void reduce(){
        this.deleteRedundantVertices();
        //this.print();
        this.deleteDuplicateVertices();
        //this.assignNewIndices();
        // Asignamos H
        this.updateH();
        // Asignamos la raiz
        this.assignRoot();
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     */
    BDD(String function_str, ArrayList<String> variables){
        this.function = function_str;
        this.variables = variables;
        this.present_variable_indices = new ArrayList<Integer>();
        this.variable_existence = new ArrayList<Boolean>(variables.size( ));
        for(int i=0; i<variables.size(); i++){
            String var = variables.get(i);
            Boolean exists_variable = function.contains(var);
            this.variable_existence.add(exists_variable);
            if(exists_variable)
                this.present_variable_indices.add(i);
        }
        // Leaf vertices
        this.False = new Vertex(false);
        this.True = new Vertex(true);
        // HashMap
        this.T = new TableT();
        T.put(0, this.False);
        T.put(1, this.True);
        // Generation of the BDD tree
        ArrayList<Boolean> path = new ArrayList<Boolean>();
        this.generateTreeFunction(path);
        // Reduction of the BDD tree
        this.reduce();
    }
    
    BDD(TableT T, String function_str, ArrayList<String> variables){
        this.function = function_str;
        this.variables = variables;
        this.present_variable_indices = new ArrayList<Integer>();
        this.variable_existence = new ArrayList<Boolean>(variables.size( ));
        for(int i=0; i<variables.size(); i++){
            String var = variables.get(i);
            Boolean exists_variable = function.contains(var);
            this.variable_existence.add(exists_variable);
            if(exists_variable)
                this.present_variable_indices.add(i);
        }
        // Leaf vertices
        this.False = T.get(0);
        this.True = T.get(1);
        // HashMap
        this.T = T;
        // Reduction of the BDD tree
        this.reduce();
    }
    
    /**************************************************************************/
    /* Apply algorithm */
    
    public BDD apply(String op, BDD bdd2){
        try
        {
            BDDApply applicator = new BDDApply(op, this, bdd2);
            return applicator.run();
        }
        catch(Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
        return null;
    }
    
    /**************************************************************************/
    /* Operations */
    
    /**
     * Recursive evaluate function.
     * Evaluates the tree from a vertex.
     * @param v Start vertex from will start evaluation.
     * @param truthAssignement ArrayList of boolean where ith item coresponds to the truth value assigned to the ith variable.
     * @return boolean Value gotten of the evaluation of the tree starting in v.
     */
    protected boolean evaluateFromVertex(Vertex v, ArrayList<Boolean> truthAssignement){
        if(!v.isLeaf())
        {
            if(!truthAssignement.get(v.variable))
                return this.evaluateFromVertex(this.T.get(v.low), truthAssignement);
            else
                return this.evaluateFromVertex(this.T.get(v.high), truthAssignement);
        }
        else{
            return v.value();
        }
    }
    
    /**
     * Evaluates a BDD tree given a assignement to its variables.
     * @param truthAssignement ArrayList of boolean where ith item coresponds to the value of the ith variable.
     * @return boolean Value gotten of the evaluation of the tree. 
     */
    public boolean evaluate(ArrayList<Boolean> truthAssignement){
        return this.evaluateFromVertex(this.root, truthAssignement);
    }
    
    /**************************************************************************/
    /* Output zone */
    
    /**
     * Gets the string representation of a BDD.
     */
    @Override
    public String toString(){
        String text = "Tree for "+this.function+"\n";
        text += "Vertices: "+this.T.getVertices().size()+"\n";
        text += "u\tvar_i\tvar\tlow\thigh\n";
        ArrayList<Integer> vertixKeys = new ArrayList<Integer>(T.keySet());
        for(Integer i : vertixKeys){
            Vertex v = T.get(i);
            String variable = Boolean.toString(v.value());
            if(v.variable > -1)
                variable = this.variables.get(v.variable);
            text += i+"\t"+v.variable+"\t"+variable+"\t"+v.low+"\t"+v.high+"\n";
        }
        return text;
    }
    
    /**
     * Prints the BDD table.
     */
    public void print(){
        System.out.println(this.toString());
        System.out.flush();
    }
    
}
