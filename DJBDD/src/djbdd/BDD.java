/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import djbdd.timemeasurer.TimeMeasurer;
import djbdd.logic.*;
import org.antlr.runtime.tree.*;

import java.util.*;
import java.io.*;
import java.util.regex.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;

//import org.mvel2.MVEL;

/**
 * BDD object capable of execute boolean logic operations.
 * TODO: method to evaluate the BDD given a variable boolean assignation.
 * @author diegoj
 */
public class BDD {
    
    /** Maximum number of variables accepted in recursive creation */
    public static final int MAX_NUMBER_OF_VARIABLES_TO_LAUNCH_RECURSIVE_CREATION = 100;
    
    /** Use apply operation in constructor */
    public static final boolean USE_APPLY_IN_CONSTRUCTOR = false;
    
    /** Name of this BDD */
    String name = "";
    
    /** String representation of the boolean logic function of this BDD */
    public String function;
    
    /** All the variables that will be know by this BDD in no particular order */
    public ArrayList<String> variables;
    
    /** Informs if a variable exists in this BDD */
    public HashMap<String,Boolean> variable_existence;
    
    /** Variable ordering, ith variable is in variable_ordering[i] position */
    public ArrayList<Integer> variable_ordering;
    
    /** List of indices of the present variables sorted by the variable_ordering list  */
    // NOT: it is important to note that this indices has the ordering given by
    // variable ordering, don't forget that
    public ArrayList<Integer> present_variable_indices;
    
    /** Hash table that contains the BDD tree itself */
    public TableT T;
    
    /** Hash table useful for doing apply */
    public HashMap<String,Vertex> U;

    /** Root of the BDD tree */
    Vertex root = null;
    
    /** True leaf vertex */
    public Vertex True;
    
    /** False leaf vertex */
    public Vertex False;
    
    /** Informs if this BDD is a tautology (always true) */
    boolean isTautology = false;
    
    /** Informs if this BDD is a contradiction (always false) */
    boolean isContradiction = false;

    
    private void assign(BDD bdd){
        this.name = bdd.name;
        this.function = bdd.function;
        this.variables = bdd.variables;
        this.variable_existence = bdd.variable_existence;
        this.variable_ordering = bdd.variable_ordering;
        this.present_variable_indices = bdd.present_variable_indices;
        this.T = bdd.T;
        this.U = bdd.U;
        this.True = bdd.True;
        this.False = bdd.False;
        this.isTautology = bdd.isTautology;
        this.isContradiction = bdd.isContradiction;
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /**** TREE GENERATION ****/ 
    
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
            _function = _function.replaceAll(Pattern.quote(variable), value);
        }
        //System.out.println(_function);
        try{
            return BooleanEvaluator.run(_function);
        }catch(Exception e){
            System.err.println("ERROR evaluating "+this.function +". Its truth values were "+_function);
            e.printStackTrace();
            System.exit(-1);
        }
        return false;
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
    
    private static BDD optimizeORTreeGenerationFromAST(BDD bdd1, BDD bdd2){
        //System.out.println(bdd1.function + " OR " + bdd2.function);
        if (bdd1.isTautology) {
            //System.out.println("Reduction TRUE (1) IN OR");
            return bdd1;
        }
        if (bdd2.isTautology) {
            //System.out.println("Reduction TRUE (2) IN OR");
            return bdd2;
        }
        if (bdd1.isContradiction) {
            //System.out.println("Reduction FALSE (1) IN OR");
            return bdd2;
        }
        if (bdd2.isContradiction) {
            //System.out.println("Reduction FALSE (2) IN OR");
            return bdd1;
        }
        return null;
    
    }
    
    private static BDD optimizeANDTreeGenerationFromAST(BDD bdd1, BDD bdd2){
        //System.out.println(bdd1.function + " AND " + bdd2.function);
        if (bdd1.isTautology) {
            //System.out.println("Reduction TRUE (1) IN AND");
            return bdd2;
        }
        if (bdd2.isTautology) {
            //System.out.println("Reduction TRUE (2) IN AND");
            return bdd1;
        }
        if (bdd1.isContradiction) {
            //System.out.println("Reduction FALSE (1) IN AND");
            return bdd1;
        }
        if (bdd2.isContradiction) {
            //System.out.println("Reduction FALSE (2) IN AND");
            return bdd2;
        }
        return null;
    
    }
    
   private static BDD optimizeTreeGenerationFromAST(String op, BDD bdd1, BDD bdd2){
       if (op.equals("||")) {
           return BDD.optimizeORTreeGenerationFromAST(bdd1, bdd2);
       } else if (op.equals("&&")) {
           return BDD.optimizeANDTreeGenerationFromAST(bdd1, bdd2);
       }
       return null;
   }
    
   private static BDD generateTreeFromAST(CommonTree tree, ArrayList<String> variables) {
      int childCount = tree.getChildCount();
      if (childCount == 0) {
          //System.out.println("Leaf " + tree.getText());
          return new BDD(tree.getText(), variables, false);
      }
      
      String op = tree.getText();
      //System.out.println("OP es "+op);
      List<CommonTree> children = (List<CommonTree>) tree.getChildren();
      ArrayList<BDD> bdds = new ArrayList<BDD>(childCount);
      for (CommonTree child : children) {
          BDD bddI = BDD.generateTreeFromAST(child, variables);
          //System.out.println("Función "+bddI.function);
          bdds.add(bddI);
      }
      BDD bdd = bdds.get(0);
      for(int i=1; i<bdds.size(); i++){
          BDD bddI = bdds.get(i);
          BDD bddRes = BDD.optimizeTreeGenerationFromAST(op, bdd, bddI);
          if(bddRes == null){
            bddRes = bdd.apply(op, bddI);
          }
          
          bdd = bddRes;
          bdd.reduce();
      }
      //bdd.print();
      return bdd;
    }
   
    private void generateTreeUsingApply(){
        //System.out.println("generateTreeUsingApply");
        LogicLexer lexer = new LogicLexer(new ANTLRStringStream(this.function));
        LogicParser parser = new LogicParser(new CommonTokenStream(lexer));
        
        // invoke the entry point of the parser (the parse() method) and get the AST
        CommonTree tree = null;
        try{
            tree = (CommonTree)parser.parse().getTree();
        }catch(Exception e){
            System.err.println("ERROR. Parsing of the expression "+this.function+" has failed. Detailed report:");
            e.printStackTrace();
        }
        //System.out.println("XXXXXXXXXXX");
        BDD bdd = BDD.generateTreeFromAST(tree, this.variables);
        this.assign(bdd);
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /**** TREE REDUCTION ****/ 
    
    /**
     * Deletes a vertex from the BDD.
     * Updates the references of the vertices of the BDD accordingly our deletion.
     * Modifies the hash T
     * @param deletedVertex Vertex to be deleted.
     */
    private void deleteRedundantVertex(Vertex deletedVertex){
        int low = deletedVertex.low();
        int high = deletedVertex.high();
        T.remove(deletedVertex.index);
        // Update the low and high pointers to the new values
        // (the low and high of the deleted vertex resp.)
        ArrayList<Integer> vertexIndices = new ArrayList<Integer>(T.keySet());
        for(Integer i : vertexIndices){
            Vertex v = T.get(i);
            if(v.low() == deletedVertex.index)
                v.setLow(low);
            if(v.high() == deletedVertex.index)
                v.setHigh(high);
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
        for(Vertex v : this.T.getVertices()){
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
                    Vertex v = T.get(k);
                    if(v != null){
                        // Get all of its duplicates
                        ArrayList<Integer> duplicateIndices = getDuplicateVertexIndices(v);
                        change = change || duplicateIndices.size()>0;
                        for(Integer d : duplicateIndices){
                            ArrayList<Integer> remining = new ArrayList<Integer>(this.T.keySet());
                            for(Integer q : remining){
                                Vertex vQ = T.get(q);
                                if(vQ.low() == d)
                                    vQ.setLow(k);
                                if(vQ.high() == d)
                                    vQ.setHigh(k);
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
     * Assign the root of the BDD.
     */
    private void assignRoot(){
        ArrayList<Integer> vertexKeys = new ArrayList<Integer>(this.T.keySet());
        this.root = this.T.get(Collections.max(vertexKeys));
    }
    
    /**
     * Updates the H table.
     */
    private void updateU(){
        //System.out.flush();
        this.U = new HashMap<String,Vertex>();
        for(Vertex v : this.T.getVertices()){
            this.U.put(v.uniqueKey(),v);
        }
    }
    
    /**
     * Reduces the BDD deleting redundant and duplicate vertices.
     */
    public void reduce(){
        TimeMeasurer t = new TimeMeasurer("********* REDUCE *********");
        boolean change = false;
        do{
            change = this.deleteRedundantVertices();
            change = change || this.deleteDuplicateVertices();
        }while(change);
        //this.assignNewIndices();
        // Asignamos U
        this.updateU();
        // Asignamos la raiz
        this.assignRoot();
        t.end().show();
    }
    
    private void reduceForApply(){
        TimeMeasurer t = new TimeMeasurer("********* REDUCE FOR APPLY *********");
        //this.assignNewIndices();
        // Asignamos U
        this.updateU();
        // Asignamos la raiz
        this.assignRoot();
        t.end().show();
    }

    
    /**************************************************************************/
    /**************************************************************************/
    /* Constructors */

    /**
     * Initialize parameters dependant of variables and variable ordering in BDD constructor.
     * @param variables Name of the variables and order of them in the BDD.
     * @param variable_ordering Order of the variables given this way: variable_ordering[i]=j => jth variable is in ith position.
     */
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
    
    /**
     * Initialize parameters of the BDD given by its constructor.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     * @param variable_ordering Order of the variables given this way: variable_ordering[i]=j => jth variable is in ith position.
     * @param useApplyInCreation 
     */
    private void init(ArrayList<String> variables, ArrayList<Integer> variable_ordering, boolean useApplyInCreation){
        TimeMeasurer t = new TimeMeasurer(" ::::::::::::. BDD constructor "+this.function+".::::::::::::");
        //TimeMeasurer _t = new TimeMeasurer(" :::::::: BDD preprocess :::::::");
        //this.function = function_str;
        this.initVariableParameters(variables, variable_ordering);

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
        ArrayList<Boolean> path = new ArrayList<Boolean>(this.present_variable_indices.size());
        HashMap<String,Vertex> U = new HashMap<String,Vertex>();
        
        // If the formula can be evaluated to true without creating all the tree
        // is a truth BDD, containing only the True vertex
        //System.out.println("Use apply "+useApplyInCreation);
        if(!useApplyInCreation)// && this.present_variable_indices.size() < MAX_NUMBER_OF_VARIABLES_TO_LAUNCH_RECURSIVE_CREATION)
            this.generateTreeFunction(path, U);
        else{
            this.generateTreeUsingApply();
        }
        //_t.end();
        //_t.show();
        //this.print();
        // Reduction of the BDD tree
        this.reduce();
        if (T.keySet().size() == 1) {
            this.isTautology = T.get(1) == this.True;
            this.isContradiction = T.get(0) == this.False;
        }
        t.end().show();
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     */
    public BDD(String function_str, ArrayList<String> variables, boolean useApplyInCreation){
        this.function = function_str;
        // We use the trivial ordering,
        // that is the ith variable has the ith position
        ArrayList<Integer> trivial_variable_ordering = new ArrayList<Integer>(variables.size());
        for(int i=0; i<variables.size(); i++)
            trivial_variable_ordering.add(i);
        // Init the BDD
        this.init(variables, trivial_variable_ordering, useApplyInCreation);
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     * @param variable_ordering Order of the variables identified each one by its index, so if variable_ordering[i] = j, jth variable comes in ith position.
     */
    public BDD(String function_str, ArrayList<String> variables, ArrayList<Integer> variable_ordering, boolean useApplyInCreation){
        this.function = function_str;
        this.init(variables, variable_ordering, useApplyInCreation);
    }

    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     * @param variable_ordering Order of the variables identified each one by its index, so if variable_ordering[i] = j, jth variable comes in ith position.
     */
    public BDD(String function_str, String[] variables, Integer[] variable_ordering, boolean useApplyInCreation){
        this.function = function_str;
        ArrayList<String> variable_list = new ArrayList<String>(Arrays.asList(variables));
        ArrayList<Integer> variable_ordering_list = new ArrayList<Integer>(Arrays.asList(variable_ordering));
        this.init(variable_list, variable_ordering_list, useApplyInCreation);
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     * @param variable_ordering Order of the variables identified its position. Thats it, if variable_order_by_position[i] = "a", variable "a" is in ith position.
     */
    public BDD(String function_str, String[] variables, String[] variable_order_by_position, boolean useApplyInCreation){
        this.function = function_str;
        ArrayList<String> variable_list = new ArrayList<String>(Arrays.asList(variables));
        ArrayList<Integer> variable_ordering_list = new ArrayList<Integer>(variable_order_by_position.length);
        for(int i=0; i<variable_order_by_position.length; i++){
            int indexOfVariable = variable_list.indexOf(variable_order_by_position[i]);
            variable_ordering_list.add(indexOfVariable);
        }
        //System.out.println(variable_list);
        //System.out.println(variable_ordering_list);
        this.init(variable_list, variable_ordering_list, useApplyInCreation);
        /*System.out.println(this.variables);
        System.out.println(this.variable_ordering);
        System.out.println(this.present_variable_indices);
        System.out.println(this.function);*/
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     * @param hash_variable_ordering Order of the variables identified each one by its name, so if variable_ordering[x134] = i, x134 is in ith position.
     */
    public BDD(String function_str, ArrayList<String> variables, HashMap<String,Integer> hash_variable_ordering, boolean useApplyInCreation){
        this.function = function_str;
        ArrayList<Integer> _variable_ordering = new ArrayList<Integer>(variables.size());
        // Create initial elements
        for(int i=0; i<variables.size(); i++){
            _variable_ordering.add(i);
        }
        // Assign order
        for(String variable : hash_variable_ordering.keySet()){
            Integer position = hash_variable_ordering.get(variable);
            Integer variable_index = variables.indexOf(variable);
            _variable_ordering.set(position, variable_index);
        }
        // Initialize variable parameters
        this.init(variables, _variable_ordering, useApplyInCreation);
    }
    
    /**
     * Constructor used in the apply operation.
     * Note that this does not need variable ordering.
     * @param T Table that contains the BDD tree.
     * @param function_str Boolean logic function in string form.
     * @param variables Variables the BDD uses.
     */
    public BDD(TableT T, String function_str, ArrayList<String> variables, ArrayList<Integer> variable_ordering){
        //TimeMeasurer t = new TimeMeasurer("BDD constructor from T");
        this.function = function_str;
        this.initVariableParameters(variables, variable_ordering);
        // Leaf vertices
        this.False = T.get(0);
        this.True = T.get(1);
        // HashMap
        this.T = T;
        // Reduction of the BDD tree
        this.reduceForApply();
        //t.end().show();
    }
    
    /* END Constructors */
    /**************************************************************************/
    /**************************************************************************/
    
    /*public static BDD factoryWithBetterVariableOrder(String function_str, String[] variables, String[] variable_order){
        BDD bdd = new BDD(function_str, variables, variable_order);
        int i=0;
        while(i<1){
            Collections.shuffle( Arrays.asList(variable_order) );
            BDD bddI = new BDD(function_str, variables, variable_order);
            System.out.println(bddI.size()+ "<?" +bdd.size());
            if(bddI.size() < bdd.size()){
                bdd = bddI;
                System.out.println("MEJORA");
            }
            i++;
        }
        return bdd;
    }*/
    
    /**************************************************************************/
    /**************************************************************************/
    /* Apply algorithm */
    
    public BDD apply(String op, BDD bdd2){
        try
        {
            TimeMeasurer t = new TimeMeasurer(" AAAAAAAAAAAAAAAAAAAAAAAA apply AAAAAAAAAAAAA");
            BDDApply applicator = new BDDApply(op, this, bdd2);     
            BDD bddRes = applicator.run();
            bddRes.reduceForApply();
            t.end().show();
            return bddRes;
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
    /**************************************************************************/
    /* Logic evaluation */
    
    /**
     * Recursive evaluate function.
     * Evaluates the tree from a vertex.
     * @param v Start vertex from will start evaluation.
     * @param truthAssignement ArrayList of boolean where ith item coresponds to the truth value assigned to the ith variable.
     * @return boolean Value gotten of the evaluation of the tree starting in v.
     */
    private boolean evaluateFromVertex(Vertex v, ArrayList<Boolean> truthAssignement){
        if(!v.isLeaf())
        {
            if(!truthAssignement.get(v.variable))
                return this.evaluateFromVertex(this.T.get(v.low()), truthAssignement);
            else
                return this.evaluateFromVertex(this.T.get(v.high()), truthAssignement);
        }
        else
        {
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
    /**************************************************************************/
    /* Minimization algorithms */
    
    
    /**************************************************************************/
    /**************************************************************************/
    /* Get information about BDD zone */
    
    public Vertex root(){
        return this.root;
    }
    
    /**
     * Return the number of vertices of this BDD.
     * @return Number of vertices of the BDD.
     */
    public int size(){
        return this.T.getVertices().size();
    }
    
    public ArrayList<String> variables(){
        return this.variables;
    }
    
    public ArrayList<String> getVariables(){
        return this.variables();
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Output zone */
    
    /**
     * Gets the string representation of a BDD.
     * @param verbose Must show all variables and other explicit information?
     * @return String String that contains the hastable representation of the BDD.
     */
    public String toString(boolean verbose){
        String text = "";
        text = "BDD tree for "+this.function+"\n";
        text += "Variables: "+this.variables.size()+". ";
        if(verbose){
            text += "";
            for(String var : this.variables)
                text += var+", ";
            text = text.substring(0, text.length()-2)+"\n";
        }
        text += "\n";
        text += "Variable ordering: ";
        for(Integer varI : this.variable_ordering)
            text += varI+", ";
        text = text.substring(0, text.length()-2)+"\n";
        ArrayList<Vertex> vertices = this.T.getVertices();
        text += "Vertices: "+vertices.size()+"\n";
        text += "u\tvar_i\tvar\tlow\thigh\n";
        for(Vertex v : vertices){
            String variable = Boolean.toString(v.value());
            if(v.variable > -1)
                variable = this.variables.get(v.variable);
            text += v.index+"\t"+v.variable+"\t"+variable+"\t"+v.low()+"\t"+v.high()+"\n";
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
            System.err.println("BDD "+this.function+" has create an error");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Read the BDD table from a BufferedReader.
     */
    public static BDD fromBufferedReader(BufferedReader br){
       String function = "";
        try {
            //BufferedReader br = new BufferedReader(new FileReader(path));
            // Tree formula
            String line = br.readLine();
            function = line.substring("BDD tree for".length());
            
            // Number of variables
            line = br.readLine();
            line = line.substring("Variables:".length());
            String[] varLine = line.split("\\.");
            int num_variables = Integer.parseInt(varLine[0].replace("\\s+","").trim());
            ArrayList<String> variables = new ArrayList<String>(num_variables);
            for(int i=0; i<num_variables; i++)
                variables.add("NONE");
            
            // Variable ordering
            ArrayList<Integer> present_variable_indices = new ArrayList<Integer>(num_variables);
            line = br.readLine().substring("Variable ordering:".length());
            String[] order = line.split(",\\s*");
            for(String o : order){
                o = o.trim();
                present_variable_indices.add(Integer.parseInt(o));
            }
            
            // Number of vertices
            line = br.readLine();
            
            // Header of the list of vertices
            line = br.readLine();
            
            // Each vertex
            TableT T = new TableT();
            while (line != null) {
                line = br.readLine();
                if (line != null) {
                    // Parameteres of the vertex, index, variable index, low & high
                    String[] vertexParameters = line.split("\\s+");
                    int index = Integer.parseInt(vertexParameters[0]);
                    int variable = Integer.parseInt(vertexParameters[1]);
                    int low = Integer.parseInt(vertexParameters[3]);
                    int high = Integer.parseInt(vertexParameters[4]);
                    Vertex v = new Vertex(index, variable, low, high);
                    T.put(index, v);
                    // If the vertex has ha real variable
                    if (variable >= 0) {
                        String variableName = vertexParameters[2];
                        variables.set(variable, variableName);
                    }
                }
            }
            
        BDD bdd = new BDD(T, function, variables, present_variable_indices);
        br.close();
        return bdd;
        }catch(Exception e){
            System.err.println("Error in BDD.fromBufferedReader. Is the BufferedReader null");
            System.err.println("BDD "+function+" has create an error");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(); 
        }
        return null;
    }
    
    
    /**
     * Read the BDD table from a file.
     */
    public static BDD fromFile(String path){
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));
            return BDD.fromBufferedReader(br);
        }
        catch(Exception e){
            System.err.println("Error in BDD.fromFile. Unable to create the BufferedReader, does the file exist?");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Read the BDD table from a string.
     */
    public static BDD fromString(String bddString){
        try{
            // convert String into InputStream
            InputStream is = new ByteArrayInputStream(bddString.getBytes());
            // read it with BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            return BDD.fromBufferedReader(br);
        }
        catch(Exception e){
            System.err.println("Error in BDD.fromString. Unable to create the BufferedReader, is the string null?");
            e.printStackTrace();
        }
        return null;
    }
    
}
