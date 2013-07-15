/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import java.util.*;

/**
 * Contains the apply computation.
 * @author diegoj
 */
public class BDDApply {
    /** First bdd */
    BDD bdd1;
    
    /** Second bdd */
    BDD bdd2;
    
    /** Resulting bdd */
    BDD bdd;
    
    /** Leaf vertex with True value */
    Vertex True;
    
    /** Leaf vertex with False value */
    Vertex False;
    
    /** Operation index */
    int operation;
    
    /** Cache table **/
    HashMap<String,Vertex> G;
    
    /** Resulting hash table containing the relations between vertices */
    TableT T;
    
    /** AND logic operation key */
    public final static int OP_AND = 1;
    
    /** OR logic operation key */
    public final static int OP_OR = 2;
    
    /** IF logic operation key */
    public final static int OP_IF = 3;
    
    /** IFF logic operation key */
    public final static int OP_IFF = 4;
    
    /** NAND logic operation key */
    public final static int OP_NAND=5;
    
    /** NOR logic operation key */
    public final static int OP_NOR=6;
    
    
    /**
     * Gets the operation from a human-readable string.
     * @param operation String that contains the operation in human-readable form.
     * @return int Integer key of the operation.
     */
    private static int getOperation(String operation)throws Exception{
         if(operation.equals("&&") || operation.equalsIgnoreCase("and") || operation.equalsIgnoreCase(".") || operation.equalsIgnoreCase("·"))
             return OP_AND;
         if(operation.equals("||") || operation.equalsIgnoreCase("or") || operation.equalsIgnoreCase("+"))
             return OP_OR;
         if(operation.equalsIgnoreCase("nor"))
             return OP_NOR;
         if(operation.equalsIgnoreCase("nand"))
             return OP_NAND;
         if(operation.equalsIgnoreCase("<=>") || operation.equalsIgnoreCase("iff") || operation.equalsIgnoreCase("<->"))
             return OP_IFF;
          if(operation.equalsIgnoreCase("=>") || operation.equalsIgnoreCase("if") || operation.equalsIgnoreCase("->"))
             return OP_IF;
         throw new Exception("Operator "+operation+" undefined");
    }
    
    /**
     * Construct the function string for the resulting BDD.
     * @return String Formula for the resulting BDD obtained of operating the other BDDs.
     */
    private String getFunction(){
        String function1 = bdd1.function.trim();
        String function2 = bdd2.function.trim();
        if(operation == OP_AND)
            return function1+" && "+function2;
        if(operation == OP_OR)
            return function1+" || "+function2;
        if(operation == OP_NOR)
            return "!("+function1+" || "+function2+")";
        if(operation == OP_NAND)
            return "!("+function1+" &&"+function2+")";
        if(operation == OP_IFF)
            return "("+function1+" || !("+function2+")) && (!("+function1+") || "+function2+")";
         if(operation == OP_IF)
            return "(!("+function1+") || ("+function2+"))";
        return "Undefined operator";
        //throw new Exception("Operator "+operation+" undefined");
    }
    
    
    /**
     * Constructs an BDDApply object, container of the Andersen design of apply function.
     * @param operation Operation in human-readable form.
     * @param bdd1 First bdd operand.
     * @param bdd2 Second bdd operand.
     */
    public BDDApply(String operation, BDD bdd1, BDD bdd2)throws Exception{
        this.bdd1 = bdd1;
        this.bdd2 = bdd2;
        
        this.operation = BDDApply.getOperation(operation);
        
        this.True = new Vertex(true);
        this.False = new Vertex(false);
    }
    
    
    /**
     * Computes the operation between two leaf vertices.
     * @param v1 A leaf vertex.
     * @param v2 Another leaf vertex.
     * @return boolean Result of compute the operation between the vertex values.
     */
    private boolean op(Vertex v1, Vertex v2){
        boolean v1Value = v1.value();
        boolean v2Value = v2.value();
        if(this.operation == OP_AND)
            return v1Value && v2Value;
        if(this.operation == OP_OR)
            return v1Value || v2Value;
        if(this.operation == OP_NOR)
            return !(v1Value || v2Value);
        if(this.operation == OP_NAND)
            return !(v1Value && v2Value);
        if(this.operation == OP_IFF)
            return (v1Value || !v2Value) && (!v1Value || v2Value);
        return false;
    }

    
    /**
     * Overload to simplify our existence.
     * Avoid calling the T table each time for bdd1 and bdd2.
     * @param v1 Index of the node in BDD1.
     * @param v2 Index of the node in BDD2.
     * @return Vertex Result of doing a recursive call to app.
     */
    private Vertex app(int v1, int v2){
        return this.app(this.bdd1.T.get(v1), this.bdd2.T.get(v2));
    }

    
    /**
     * Core of the apply algorithm.
     * Computes recursively a operation between two BDDs.
     * @param v1 Vertex of BDD1.
     * @param v2 Vertex of BDD2.
     * @return Vertex Result of doing a recursive call to app.
     */
    private Vertex app(Vertex v1, Vertex v2){
        //System.out.println(v1+"-"+v2);
        //System.out.flush();
        
        // Hash key of the computation of the subtree of these two vertices
        String key = "bdd1-"+v1.index+"+bdd2-"+v2.index;
        
        if( G.containsKey(key) )
            return G.get(key);
        
        if(v1.isLeaf() && v2.isLeaf())
        {
            if(this.op(v1,v2)){
                return this.True;
            }
            return this.False;
        }
        
        int var = -1;
        Vertex low = null;
        Vertex high = null;
        // v1.index < v2.index
        if (!v1.isLeaf() && (v2.isLeaf() || v1.variable < v2.variable)) {
            var = v1.variable;
            low = this.app(v1.low, v2.index);
            high = this.app(v1.high, v2.index);
        } else if (v1.isLeaf() || v1.variable > v2.variable) {
            var = v2.variable;
            low = this.app(v1.index, v2.low);
            high = this.app(v1.index, v2.high);
        } else {
            var = v1.variable;
            low = this.app(v1.low, v2.low);
            high = this.app(v1.high, v2.high);
        }

        // Create the resulting vertex
        int index = 2;
        while(this.T.containsKey(index)){ index++; };
        Vertex u = new Vertex(index, var, low.index, high.index);
        this.T.put(index, u);
        G.put(key, u);
        return u;
    }

    
    /**
     * Public call to execute the apply algorithm.
     * @return BDD BDD Tree with the operatian computed for bdd1 and bdd2.
     */
    public BDD run(){
        // Cache to avoid repeated computations
        this.G = new HashMap<String,Vertex>();
        
        // Table that contains the structure of or new BDD
        this.T = new TableT();
        
        // Leaf vertices
        this.T.put(0, this.False);
        this.T.put(1, this.True);
        String function = this.getFunction();
        
        // Fill this.T with vertices of bdd1 and bdd2
        this.app(bdd1.root, bdd2.root);
        
        // Construction of new BDD
        this.bdd = new BDD(this.T, function, bdd1.variables);
        
        // Return the new BDD computed
        return this.bdd;
    }
    
    
}
