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
    
    /** All the variables that will be know by all BDDs in no particular order */
    private static ArrayList<String> VARIABLES = null;
    
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
    
    /** Hash table useful for doing apply */
    public HashMap<Integer,ArrayList<Vertex>> levels;

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

    /**
     * Assign operation of one BDD in another.
     * @param bdd BDD that will be copied in caller.
     */
    private void assign(BDD bdd){
        this.name = bdd.name;
        this.function = bdd.function;
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
    
    
    /* LEVELS & WHATEVER  */
    /*
    protected void addVertexToLevel(Vertex v, int level){
        // If level doesn't exist, we create it
        if(!levels.containsKey(level))
            levels.put(level, new ArrayList<Vertex>(level*10));
        
        // We add the vertex to the level
        levels.get(level).add(v);
    }
    
    protected void addVerticesToLevel(Collection<Vertex> vertices, int level){
        // If level doesn't exist, we create it
        if(!levels.containsKey(level))
            levels.put(level, new ArrayList<Vertex>(level*10));
        
        // We add each vertex to the level
        for(Vertex v : vertices)
            levels.get(level).add(v);
    }
    
    protected void addAllVerticesToLevel(int level){
        // If level doesn't exist, we create it
        if(!levels.containsKey(level))
            levels.put(level, new ArrayList<Vertex>(level*10));
        
        // We add each vertex to the level
        for(Vertex v : this.T.values())
            levels.get(level).add(v);
    }
    
    private void updateLevels(Vertex v, int level){
        
        if(v.isLeaf()){
            return;
        }
        else{
            this.addVertexToLevel(v, level);
            updateLevels(this.T.get(v.low()), level+1);
            updateLevels(this.T.get(v.high()), level+1);
        }
    }
    
    private void swapLevel(int i){
        ArrayList<Vertex> levelI = this.levels.get(i);
        //ArrayList<Vertex> levelI1 = this.levels.get(i+1);
        
        for(Vertex v : levelI){
            int lowIndex = T.getNextKey();
            Vertex low = new Vertex(lowIndex,0,0,0);
            
            int highIndex = T.getNextKey();
            Vertex high = new Vertex(highIndex,0,0,0);
            
            int index = T.getNextKey();
            Vertex vReplacement = new Vertex(index, 0, low.index, high.index);
            this.T.put(v.index, vReplacement); 
        }
    }*/
    
    /* END LEVELS & WHATEVER */
    
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
            String variable = VARIABLES.get(variable_index);
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
                    //this.addVertexToLevel(v_low, path_len);
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
                    Vertex v = U.get(vKey);
                    //this.addVertexToLevel(v, path_len);
                    return v;
                }
                
                // There are no vertex with
                // this variable, and low and high vertices.
                // We create a new vertex
                int index = T.getNextKey();
                Vertex v = new Vertex(index, var_index, v_low, v_high);
                this.T.put(index, v);
                U.put(vKey, v);
                //this.addVertexToLevel(v, path_len);
                return v;
            }
            else if(path_len == this.present_variable_indices.size())
            {
                // reached leafes
                boolean value = this.evaluatePath(path);
                if(value){
                    this.T.put(1,this.True);
                    //this.addVertexToLevel(this.True, path_len);
                    return this.True;
                }
                this.T.put(0,this.False);
                //this.addVertexToLevel(this.False, path_len);
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
   
  
    /**
     * Generate the BDD tree using a Abstract Syntax Tree (based in ANTL3 library).
     * @param tree AST 
     * @param variables List with the variables of the tree.
     * @return BDD Binary Decision Tree for the formula described in the AST tree.
     */
    private static BDD generateTreeFromAST(CommonTree tree) {
        // Get the number of children of the tree
        int childCount = tree.getChildCount();
        
        // If we have a leaf, the node has a variable not an operation
        // we create the BDD using recursion (it's only one step deep)
        if (childCount == 0) {
            BDD bdd = new BDD(tree.getText(), false);
            return bdd;
        }
        
        // Otherwise, we get an operation node
        String op = tree.getText();
        
        // For each children, we recursively call generateTreeFromAST
        // And assign current node as parent of the subtree generated
        List<CommonTree> children = (List<CommonTree>) tree.getChildren();
        ArrayList<BDD> bdds = new ArrayList<BDD>(childCount);
        for (CommonTree child : children) {
            BDD bddI = BDD.generateTreeFromAST(child);
            //System.out.println("Función "+bddI.function);
            bdds.add(bddI);
        }
        
        // For ech children, we apply the operation given in their parent node
        // and construct a new BDD for the parention node
        BDD bdd = bdds.get(0);
        for (int i = 1; i < bdds.size(); i++) {
            BDD bddI = bdds.get(i);
            BDD bddRes = null;//BDD.optimizeTreeGenerationFromAST(op, bdd, bddI);
            if (bddRes == null) {
                bddRes = bdd.apply(op, bddI);
            }

            bdd = bddRes;
            bdd.reduce();
        }
        //bdd.print();
        return bdd;
    }
   
   /**
    * Generate the Tree usin the APPLY operation.
    * The 'this' object IS MODIFIED.
    */
    private void generateTreeUsingApply(){
        // Creating the lexer and the parser for the logic formula
        LogicLexer lexer = new LogicLexer(new ANTLRStringStream(this.function));
        LogicParser parser = new LogicParser(new CommonTokenStream(lexer));
        
        // Invoke the entry point of the parser (the parse() method) and get the AST
        CommonTree tree = null;
        try{
            tree = (CommonTree)parser.parse().getTree();
        }catch(Exception e){
            System.err.println("ERROR. Parsing of the expression "+this.function+" has failed. Detailed report:");
            e.printStackTrace();
        }
        
        // Call to create the BDD from an AST
        int level = 0;
        BDD bdd = BDD.generateTreeFromAST(tree);
        this.assign(bdd);
        this.assignRoot();
        //this.updateLevels(this.root, level);
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
        /*
        // Test results HAVE BEEN RIGHT, the root is RIGHT!
        // Test if root calculation is wrong O(N)
        for(Vertex v : this.T.values()){
            if(v.low() == this.root.index || v.high() == this.root.index){
                System.err.println("PETOSKY");
                System.exit(-1);
            }
        }*/
                
    }
    
    /**
     * Updates the H table.
     */
    private void updateU(){
        //System.out.flush();
        /*
        this.U = new HashMap<String,Vertex>();
        for(Vertex v : this.T.getVertices()){
            this.U.put(v.uniqueKey(),v);
        }*/
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
    private void initVariableOrder(ArrayList<Integer> variable_ordering){
        this.variable_ordering = variable_ordering;
        this.present_variable_indices = new ArrayList<Integer>();
        this.variable_existence = new HashMap<String,Boolean>();
        for(int i=0; i<variable_ordering.size(); i++){
            int variable_index = variable_ordering.get(i);
            String var = VARIABLES.get(variable_index);
            Boolean exists_variable = function.contains(var);
            this.variable_existence.put(var,exists_variable);
            if(exists_variable)
                this.present_variable_indices.add(variable_index);
        }
    }
    
    /**
     * Initialize parameters of the BDD given by its constructor.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variable_ordering Order of the variables given this way: variable_ordering[i]=j => jth variable is in ith position.
     * @param useApplyInCreation 
     */
    private void init(ArrayList<Integer> variable_ordering, boolean useApplyInCreation){
        TimeMeasurer t = new TimeMeasurer(" ::::::::::::. BDD constructor "+this.function+".::::::::::::");
        //TimeMeasurer _t = new TimeMeasurer(" :::::::: BDD preprocess :::::::");
        //this.function = function_str;
        this.initVariableOrder(variable_ordering);

        // Leaf vertices
        this.False = new Vertex(false);
        this.True = new Vertex(true);
        // HashMap
        this.T = new TableT();

        // Levels
        this.levels = new HashMap<Integer,ArrayList<Vertex>>(this.present_variable_indices.size());
        
        // If we insert the False and True vertices,
        // we can't have true or false BDDs
        //T.put(0, this.False);
        //T.put(1, this.True);
        // Generation of the BDD tree
        ArrayList<Boolean> path = new ArrayList<Boolean>(this.present_variable_indices.size());
        HashMap<String,Vertex> U = new HashMap<String,Vertex>();
        
        // If the formula can be evaluated to true without creating all the tree
        // is a truth BDD, containing only the True vertex
        if(!useApplyInCreation)
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
            if(T.containsKey(1))
                this.isTautology = T.get(1) == this.True;
            if(T.containsKey(0))
                this.isContradiction = T.get(0) == this.False;
        }
        t.end().show();
    }
    
    public static void initVariables(ArrayList<String> variables){
        BDD.VARIABLES = variables;
    }
    
    public static void initVariables(String[] variables){
        BDD.VARIABLES = new ArrayList<String>(Arrays.asList(variables));
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     */
    public BDD(String function_str, boolean useApplyInCreation){
        this.function = function_str;
        // We use the trivial ordering,
        // that is the ith variable has the ith position
        ArrayList<Integer> trivial_variable_ordering = new ArrayList<Integer>(VARIABLES.size());
        for(int i=0; i<VARIABLES.size(); i++)
            trivial_variable_ordering.add(i);
        // Init the BDD
        this.init(trivial_variable_ordering, useApplyInCreation);
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variable_ordering Order of the variables identified each one by its index, so if variable_ordering[i] = j, jth variable comes in ith position.
     */
    public BDD(String function_str, ArrayList<Integer> variable_ordering, boolean useApplyInCreation){
        this.function = function_str;
        this.init(variable_ordering, useApplyInCreation);
    }

    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variable_ordering Order of the variables identified each one by its index, so if variable_ordering[i] = j, jth variable comes in ith position.
     */
    public BDD(String function_str, Integer[] variable_ordering, boolean useApplyInCreation){
        this.function = function_str;
        ArrayList<Integer> variable_ordering_list = new ArrayList<Integer>(Arrays.asList(variable_ordering));
        this.init(variable_ordering_list, useApplyInCreation);
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variable_ordering Order of the variables identified its position. Thats it, if variable_order_by_position[i] = "a", variable "a" is in ith position.
     */
    public BDD(String function_str, String[] variable_order_by_position, boolean useApplyInCreation){
        this.function = function_str;
        ArrayList<Integer> variable_ordering_list = new ArrayList<Integer>(variable_order_by_position.length);
        for(int i=0; i<variable_order_by_position.length; i++){
            int indexOfVariable = VARIABLES.indexOf(variable_order_by_position[i]);
            variable_ordering_list.add(indexOfVariable);
        }
        //System.out.println(variable_list);
        //System.out.println(variable_ordering_list);
        this.init(variable_ordering_list, useApplyInCreation);
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
    public BDD(String function_str, HashMap<String,Integer> hash_variable_ordering, boolean useApplyInCreation){
        this.function = function_str;
        ArrayList<Integer> _variable_ordering = new ArrayList<Integer>(VARIABLES.size());
        // Create initial elements
        for(int i=0; i<VARIABLES.size(); i++){
            _variable_ordering.add(i);
        }
        // Assign order
        for(String variable : hash_variable_ordering.keySet()){
            Integer position = hash_variable_ordering.get(variable);
            Integer variable_index = VARIABLES.indexOf(variable);
            _variable_ordering.set(position, variable_index);
        }
        // Initialize variable parameters
        this.init(_variable_ordering, useApplyInCreation);
    }
    
    /**
     * Constructor used in the apply operation.
     * Note that this does not need variable ordering.
     * @param T Table that contains the BDD tree.
     * @param function_str Boolean logic function in string form.
     * @param variable_ordering Variable ordering.
     */
    public BDD(TableT T, String function_str, ArrayList<Integer> variable_ordering){
        //TimeMeasurer t = new TimeMeasurer("BDD constructor from T");
        this.function = function_str;
        this.initVariableOrder(variable_ordering);
        // Leaf vertices
        this.False = T.get(0);
        this.True = T.get(1);
        // HashMap
        this.T = T;
        // Reduction of the BDD tree
        this.reduceForApply();
        if (T.keySet().size() == 1) {
            if(T.containsKey(1))
                this.isTautology = T.get(1) == this.True;
            if(T.containsKey(0))
                this.isContradiction = T.get(0) == this.False;
        }
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
    
    public static ArrayList<String> variables(){
        return VARIABLES;
    }
    
    public ArrayList<String> getVariables(){
        return this.variables();
    }
    
    public boolean isContradiction(){ return this.isContradiction; }
    public boolean isTautology(){ return this.isTautology; }
    
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
        text += "Variables: "+VARIABLES.size()+". ";
        if(verbose){
            text += "";
            for(String var : VARIABLES)
                text += var+", ";
            text = text.substring(0, text.length()-2)+"\n";
        }
        text += "\n";
        text += "Variable ordering: ";
        for(Integer varI : this.present_variable_indices)
            text += varI+", ";
        text = text.substring(0, text.length()-2)+"\n";
        ArrayList<Vertex> vertices = this.T.getVertices();
        text += "Vertices: "+vertices.size()+"\n";
        text += "u\tvar_i\tvar\tlow\thigh\n";
        for(Vertex v : vertices){
            String variable = Boolean.toString(v.value());
            if(v.variable > -1)
                variable = VARIABLES.get(v.variable);
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
     * Prints the BDD table.
     */
    public void printLevels(){
        for(Integer l : this.levels.keySet()){
            ArrayList<Vertex> vertices = this.levels.get(l);
            System.out.println("Level "+l);
            for(Vertex v : vertices){
                System.out.println("Vertex"+v.toString());
            }
        }
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
    public static BDD fromBufferedReader(BufferedReader br, ArrayList<String> variables){
       String function = "";
        try {
            // Tree formula
            String line = br.readLine();
            function = line.substring("BDD tree for".length());
            
            // Number of variables
            line = br.readLine();
            line = line.substring("Variables:".length());
            String[] varLine = line.split("\\.");
            int num_variables = Integer.parseInt(varLine[0].replace("\\s+","").trim());
            if(variables == null){
                variables = new ArrayList<String>(num_variables);
                for(int i=0; i<num_variables; i++)
                    variables.add("var_"+(i+1)+"");
            }
            
            // Variable ordering
            ArrayList<Integer> present_variable_indices = new ArrayList<Integer>(num_variables);
            line = br.readLine();
          
            if(line.equals("Variable ordering")){
                // There are no present_variables, BDD is true or false
                // DO NOTHING!
            }
            else{
                line = line.substring("Variable ordering:".length()).trim();
                //System.out.println("VAR ORDER '"+line+"'");
                String[] order = line.split(",\\s*");
                for(String o : order){
                    o = o.trim();
                    present_variable_indices.add(Integer.parseInt(o));
                }
            }
            
            // Number of vertices
            line = br.readLine();
            String num_vertices_s = line.split("\\s+")[1].trim();
            int num_vertices = Integer.parseInt(num_vertices_s);
            
            // Header of the list of vertices
            line = br.readLine();
            
            // Each vertex
            TableT T = new TableT();
            int i=0;
            while (line != null && i<num_vertices) {
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
                i++;
            }
            
        BDD bdd = new BDD(T, function, present_variable_indices);
        br.close();
        return bdd;
        }catch(Exception e){
            System.err.println("Error in BDD.fromBufferedReader.");
            System.err.println("BDD "+function+" has create an error");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(); 
            System.exit(-1);
        }
        return null;
    }
    
    /**
     * Read the BDD table from a BufferedReader.
     */
    public static BDD fromBufferedReader(BufferedReader br){
        return BDD.fromBufferedReader(br, null);
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
    public static BDD fromString(String bddString, ArrayList<String> variables){
        try{
            // convert String into InputStream
            InputStream is = new ByteArrayInputStream(bddString.getBytes());
            // read it with BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            return BDD.fromBufferedReader(br, variables);
        }
        catch(Exception e){
            System.err.println("Error in BDD.fromString. Unable to create the BufferedReader, is the string null?");
            e.printStackTrace();
        }
        return null;
    }
    
}
