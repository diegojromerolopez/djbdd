/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import java.util.*;
import java.io.*;
//import java.util.regex.*;
//import org.mvel2.MVEL;

/**
 *
 * @author diegoj
 */
public class BDD {
    String name = "";
    String function;
    ArrayList<String> variables;
    HashMap<String,Boolean> variable_existence;
    
    /** Variable ordering, ith variable is in variable_ordering[i] position */
    ArrayList<Integer> variable_ordering;
    
    /** List of indices of the present variables sorted by the variable_ordering list  */
    ArrayList<Integer> present_variable_indices;
    
    /** Hash table that contains the BDD tree itself */
    TableT T;
    HashMap<String,Vertex> H;

    Vertex root = null;
    Vertex True;
    Vertex False;
    
    boolean isTautology = false;
    boolean isContradiction = false;
    
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
        
        return BooleanEvaluator.run(_function);
        //return (Boolean)MVEL.eval(_function);
    }
    
    /**
     * Main recursively generation of the tree.
     * @param path ArrayList of variables that contains a boolean assignement for each variable.
     * @param U HashMap that ensures the uniqueness of the generated vertices.
     * @return Vertex of each level.
     */
    private Vertex generateTreeFunction(ArrayList<Boolean> path, HashMap<String,Vertex> U){
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
                
                // Low path
                ArrayList<Boolean> path_low = new ArrayList<Boolean>(path);
                path_low.add(false);
                Vertex v_low = this.generateTreeFunction(path_low, U);
                
                // High path
                ArrayList<Boolean> path_high = new ArrayList<Boolean>(path);
                path_high.add(true);
                Vertex v_high = this.generateTreeFunction(path_high, U);
                
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
                int var_index = this.present_variable_indices.get(path_len);
                
                // If exists a vertex with the same variable and the same
                // descendents, return that
                String vKey = var_index+"-"+v_low.index+"-"+v_high.index;
                if(U.containsKey(vKey)){
                    return U.get(vKey);
                }
                
                // There are no vertex with
                // this variable, and low and high vertices.
                // We create a new vertex
                int index = T.getNextKey();
                Vertex v = new Vertex(index, var_index, v_low, v_high);
                this.T.put(index, v);
                U.put(vKey, v);
                return v;
            }
            else if(path_len == this.present_variable_indices.size())
            {
                // reached leafes
                boolean value = this.evaluatePath(path);
                if(value){
                    this.T.put(1,this.True);
                    return this.True;
                }
                this.T.put(0,this.False);
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
     * @return boolean True if there has been a deletion of a redundant vertex. False otherwise.
     */
    private boolean deleteRedundantVertices(){
        //TimeMeasurer t = new TimeMeasurer("---- deleteRedundantVertices ----");
        // Non-redundancy: no vertex has same low and high
        //ArrayList<Integer> vertixKeys = new ArrayList<Integer>(this.T.keySet());
        boolean deleted = false;
        for(Vertex v : this.T.getVertices()){
            //Vertex v = this.T.get(i);
            if(v.isRedundant()){
                //System.out.println("Eliminamos el "+v.index);
                deleted = true;
                this.deleteRedundantVertex(v);
            }
        }
        //t.end();
        //t.show();
        return deleted;
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
    private boolean deleteDuplicateVertices(){
        //TimeMeasurer t = new TimeMeasurer("++++++ deleteDuplicateVertices ++++++");
        // Uniqueness
        boolean _change = false;
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
                            _change = true;
                        }
                    }
                }
            }
        }
        while(change);
        //t.end().show();
        return _change;
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
    
    /**
     * Assign the root of the BDD.
     */
    private void assignRoot(){
        ArrayList<Integer> vertexKeys = new ArrayList<Integer>(this.T.keySet());
        this.root = this.T.get(Collections.max(vertexKeys));
    }
    
    /**
     * Updates the H table.
     */
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
        TimeMeasurer t = new TimeMeasurer("********* REDUCE *********");
        boolean change = false;
        do{
            change = this.deleteRedundantVertices();
            //this.print();
            change = change || this.deleteDuplicateVertices();
        }while(change);
        //this.assignNewIndices();
        // Asignamos H
        this.updateH();
        // Asignamos la raiz
        this.assignRoot();
        t.end().show();
    }

    /**************************************************************************/
    /**************************************************************************/
    /* Constructors */
    
    private void initVariableParameters(ArrayList<String> variables, ArrayList<Integer> variable_ordering){
        this.variables = variables;
        this.variable_ordering = variable_ordering;
        this.present_variable_indices = new ArrayList<Integer>();
        this.variable_existence = new HashMap<String,Boolean>();
        for(int i=0; i<variable_ordering.size(); i++){
            int variable_index = variable_ordering.get(i);
            String var = variables.get(variable_index);
            Boolean exists_variable = function.contains(var);
            this.variable_existence.put(var,exists_variable);
            if(exists_variable)
                this.present_variable_indices.add(variable_index);
        }
        /*
        System.out.println(variables);
        System.out.println(variable_ordering);
        System.out.println(present_variable_indices);
         * 
         */
    }
    
    
    private void init(String function_str, ArrayList<String> variables, ArrayList<Integer> variable_ordering){
        //TimeMeasurer t = new TimeMeasurer("BDD constructor");
        //TimeMeasurer _t = new TimeMeasurer(" :::::::: BDD preprocess :::::::");
        this.function = function_str;
        this.initVariableParameters(variables, variable_ordering);
        /*
        this.variables = variables;
        this.variable_ordering = variable_ordering;
        this.present_variable_indices = new ArrayList<Integer>();
        this.variable_existence = new ArrayList<Boolean>(variables.size( ));
        for(int i=0; i<variables.size(); i++){
            String var = variables.get(i);
            Boolean exists_variable = function.contains(var);
            this.variable_existence.add(exists_variable);
            if(exists_variable)
                this.present_variable_indices.add(i);
        }
         */
        // Leaf vertices
        this.False = new Vertex(false);
        this.True = new Vertex(true);
        // HashMap
        this.T = new TableT();
        
        // If we insert the False and True vertices,
        // we can't have true or false BDDs
        //T.put(0, this.False);
        //T.put(1, this.True);
        // Generation of the BDD tree
        ArrayList<Boolean> path = new ArrayList<Boolean>();
        HashMap<String,Vertex> U = new HashMap<String,Vertex>();
        this.generateTreeFunction(path, U);
        //_t.end();
        //_t.show();
        //this.print();
        // Reduction of the BDD tree
        this.reduce();
        if(T.keySet().size() == 1){
            this.isTautology =  T.get(1)==this.True;
            this.isContradiction = T.get(0)==this.False;
        }
        //t.end();
        //t.show();    
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     */
    BDD(String function_str, ArrayList<String> variables){
        // We use the trivial ordering,
        // that is the ith variable has the ith position
        ArrayList<Integer> trivial_variable_ordering = new ArrayList<Integer>(variables.size());
        for(int i=0; i<variables.size(); i++)
            trivial_variable_ordering.add(i);
        // Init the BDD
        this.init(function_str, variables, trivial_variable_ordering);
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     * @param variable_ordering Order of the variables identified each one by its index, so if variable_ordering[i] = j, jth variable comes in ith position.
     */
    BDD(String function_str, ArrayList<String> variables, ArrayList<Integer> variable_ordering){
        this.init(function_str, variables, variable_ordering);
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     * @param hash_variable_ordering Order of the variables identified each one by its name, so if variable_ordering[x134] = i, x134 is in ith position.
     */
    BDD(String function_str, ArrayList<String> variables, HashMap<String,Integer> hash_variable_ordering){
        ArrayList<Integer> _variable_ordering = new ArrayList<Integer>(variables.size());
        for(String variable : hash_variable_ordering.keySet()){
            Integer position = hash_variable_ordering.get(variable);
            Integer variable_index = variables.indexOf(variable);
            _variable_ordering.set(position, variable_index);
        }
        this.init(function_str, variables, _variable_ordering);
    }
    
    /**
     * Constructor used in the apply operation.
     * Note that this does not need variable ordering.
     * @param T Table that contains the BDD tree.
     * @param function_str Boolean logic function in string form.
     * @param variables Variables the BDD uses.
     */
    BDD(TableT T, String function_str, ArrayList<String> variables, ArrayList<Integer> variable_ordering){
        //TimeMeasurer t = new TimeMeasurer("BDD constructor from T");
        this.function = function_str;
        /*
        this.variables = variables;
        this.variable_ordering = variable_ordering;
        this.present_variable_indices = new ArrayList<Integer>();
        this.variable_existence = new ArrayList<Boolean>(variables.size( ));
        for(int i=0; i<variables.size(); i++){
            String var = variables.get(i);
            Boolean exists_variable = function.contains(var);
            this.variable_existence.add(exists_variable);
            if(exists_variable)
                this.present_variable_indices.add(i);
        }
         * 
         */
        this.initVariableParameters(variables, variable_ordering);
        // Leaf vertices
        this.False = T.get(0);
        this.True = T.get(1);
        // HashMap
        this.T = T;
        // Reduction of the BDD tree
        this.reduce();
        //t.end().show();
    }
    
    /* END Constructors */
    /**************************************************************************/
    /**************************************************************************/
    
    
    /**************************************************************************/
    /* Apply algorithm */
    
    public BDD apply(String op, BDD bdd2){
        try
        {
            TimeMeasurer t = new TimeMeasurer("apply");
            BDDApply applicator = new BDDApply(op, this, bdd2);
            t.end();
            t.show();          
            return applicator.run();
        }
        catch(Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
        // This code is not executed:
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
     * @param verbose Must show all variables and other explicit information?
     * @return String String that contains the hastable representation of the BDD.
     */
    public String toString(boolean verbose){
        String text = "";
        text = "Tree for "+this.function+"\n";
        if(verbose){
            text += "Variables: ";
            for(String var : this.variables)
                text += var+", ";
            text = text.substring(0, text.length()-2)+"\n";
        }
        else
        {
            text += "Variables: "+this.variables.size()+"\n";
        }
        ArrayList<Vertex> vertices = this.T.getVertices();
        text += "Vertices: "+vertices.size()+"\n";
        text += "u\tvar_i\tvar\tlow\thigh\n";
        for(Vertex v : vertices){
            String variable = Boolean.toString(v.value());
            if(v.variable > -1)
                variable = this.variables.get(v.variable);
            text += v.index+"\t"+v.variable+"\t"+variable+"\t"+v.low+"\t"+v.high+"\n";
        }
        return text;
    }

    
     /**
     * Gets the string representation of a BDD.
     * @return String String that contains the hastable representation of the BDD.
     */
    @Override
    public String toString(){
        return this.toString(false);
    }    
    
    /**
     * Prints the BDD table.
     */
    public void print(){
        System.out.println(this.toString());
        System.out.flush();
    }
    
     /**
     * Prints the BDD table to a file.
     */
    public void toFile(String path){
        try {
            // Create file 
            FileWriter fstream = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(this.toString());
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
