/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import djbdd.timemeasurer.TimeMeasurer;
import djbdd.logic.*;

import java.util.*;
import java.io.*;
import java.util.regex.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

//import org.mvel2.MVEL;

/**
 * BDD object capable of execute boolean logic operations.
 * TODO: method to evaluate the BDD given a variable boolean assignation.
 * @author diegoj
 */
public class BDD {
    
    /** Hash table that contains the BDD tree itself */
    public static TableT T = null;
    
    /** All the variables that will be know by all BDDs in no particular order */
    private static ArrayList<String> VARIABLES = null;
    
    /** Maximum number of variables accepted in recursive creation */
    public static final int MAX_NUMBER_OF_VARIABLES_TO_LAUNCH_RECURSIVE_CREATION = 100;
    
    /** Use apply operation in constructor */
    public static final boolean USE_APPLY_IN_CONSTRUCTOR = false;
    
    /** Name of this BDD */
    String name = "";
    
    /** String representation of the boolean logic function of this BDD */
    public String function;
   
    /** Informs if a variable exists in this BDD */
    public HashMap<String,Boolean> variable_existence;
    
    /** Variable ordering, ith variable is in variable_ordering[i] position */
    public ArrayList<Integer> variable_ordering;
    
    /** List of indices of the present variables sorted by the variable_ordering list  */
    // NOT: it is important to note that this indices has the ordering given by
    // variable ordering, don't forget that
    public ArrayList<Integer> present_variable_indices;
   
    /** Hash table useful for doing apply */
    //public HashMap<Integer,ArrayList<Vertex>> levels;

    /** Root of the BDD tree */
    public Vertex root = null;
    
    /** Size of this BDD */
    private int size = -1;
    
    /** Informs if this BDD is a tautology (always true) */
    boolean isTautology = false;
    
    /** Informs if this BDD is a contradiction (always false) */
    boolean isContradiction = false;

    /************************* INIT BDD SYSTEM ********************************/
    
    /**
     * Initialize the table of nodes.
     */
    public static void initT(){
        // Creation of vertex table
        BDD.T = new TableT();
    }
    
    /**
     * Initialize variables
     * @param variables List of variables existing in the system.
     */
    private static void initVariables(ArrayList<String> variables){
        BDD.VARIABLES = variables;
    }
    
    /**
     * Initialize variables
     * @param variables List of variables existing in the system.
     */
    private static void initVariables(String[] variables){
        BDD.VARIABLES = new ArrayList<String>(Arrays.asList(variables));
    }

    /**
     * Initialize BDD system.
     * @param variables List of variables existing in the system.
     */
    public static void init(ArrayList<String> variables){
        BDD.initT();
        BDD.initVariables(variables);
     }
    
    /**
     * Initialize BDD system.
     * @param variables Array of variables existing in the system.
     */
    public static void init(String[] variables){
        BDD.initT();
        BDD.initVariables(variables);
    }
    
    /**
     * Get the variables defined for all BDDs.
     * @return List of variables defined in this enviroment.
     */
    public static ArrayList<String> variables(){
        return VARIABLES;
    }
    
    /************************* END INIT BDD SYSTEM ****************************/
    
    /**************************************************************************/
    /**************************************************************************/
    /* Root assignement */
    
    public final void assignRoot(Vertex newRoot){
        if(this.root!=null && this.root.index == newRoot.index){
            //System.out.println("xxx");
            return;
        }
        
        this.root = newRoot;
        //System.out.println(this.root);
        if (this.root.isLeaf()) {
            this.isTautology = this.root == BDD.T.True;
            this.isContradiction = this.root == BDD.T.False;
        }
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* BDD assignement */
    
    /**
     * Assign operation of one BDD in another.
     * @param bdd BDD that will be copied in caller.
     */
    private void assignInTreeGeneration(BDD bdd){
        this.name = bdd.name;
        this.function = bdd.function;
        this.size = bdd.size;
        // The reference count is updated in bdd
        // we have no loger to re-update it
        this.root = bdd.root;
        this.variable_existence = bdd.variable_existence;
        this.variable_ordering = bdd.variable_ordering;
        this.present_variable_indices = bdd.present_variable_indices;
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
    }
    
    /**
     * Main recursively generation of the tree.
     * @param path ArrayList of variables that contains a boolean assignement for each variable.
     * @param U HashMap that ensures the uniqueness of the generated vertices.
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
                
                // Low path
                ArrayList<Boolean> path_low = new ArrayList<Boolean>(path);
                path_low.add(false);
                Vertex v_low = this.generateTreeFunction(path_low);
                
                // High path
                ArrayList<Boolean> path_high = new ArrayList<Boolean>(path);
                path_high.add(true);
                Vertex v_high = this.generateTreeFunction(path_high);
                
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
                return T.add(var_index, v_low, v_high);
                /*
                // If exists a vertex with the same variable and the same
                // descendents, return that
                String vKey = Vertex.computeUniqueKey(var_index, v_low.index, v_high.index);
                if(T.containsVertex(vKey)){
                    Vertex v = T.get(vKey);
                    //this.addVertexToLevel(v, path_len);
                    return v;
                }
                
                // There are no vertex with
                // this variable, and low and high vertices.
                // We create a new vertex
                int index = T.getNextKey();
                Vertex v = new Vertex(index, var_index, v_low, v_high);
                this.T.put(index, v);*/
                //this.addVertexToLevel(v, path_len);
                //return v;
            }
            else if(path_len == this.present_variable_indices.size())
            {
                // reached leafes
                boolean value = this.evaluatePath(path);
                if(value){
                    //this.T.put(1,this.True);
                    //this.addVertexToLevel(this.True, path_len);
                    return BDD.T.True;
                }
                //this.T.put(0,this.False);
                //this.addVertexToLevel(this.False, path_len);
                return BDD.T.False;
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
            BDD bdd = new BDD(tree.getText());
            //System.out.println(bdd.root);
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
                bdd = null;
                bddI = null;
            }

            bdd = bddRes;
            //bdd.reduce();
        }
        //bdd.print();
        return bdd;
    }
   
   /**
    * Generate the Tree usin the APPLY operation.
    * The 'this' object IS MODIFIED.
    */
    private Vertex generateTreeUsingApply(){
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
        //int level = 0;
        BDD bdd = BDD.generateTreeFromAST(tree);
        this.assignInTreeGeneration(bdd);
        return this.root;
        //this.assignRoot();
        //this.updateLevels(this.root, level);
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
    private void initBDD(ArrayList<Integer> variable_ordering, boolean useApplyInCreation){
        TimeMeasurer t = new TimeMeasurer(" ::::::::::::. BDD constructor "+this.function+".::::::::::::");
        //TimeMeasurer _t = new TimeMeasurer(" :::::::: BDD preprocess :::::::");
        //this.function = function_str;
        this.initVariableOrder(variable_ordering);

        // Leaf vertices
        //this.False = new Vertex(false);
        //this.True = new Vertex(true);
        // HashMap
        //this.T = new TableT();

        // Levels
        //this.levels = new HashMap<Integer,ArrayList<Vertex>>(this.present_variable_indices.size());
        
        // If we insert the False and True vertices,
        // we can't have true or false BDDs
        //T.put(0, this.False);
        //T.put(1, this.True);
        // Generation of the BDD tree
        ArrayList<Boolean> path = new ArrayList<Boolean>(this.present_variable_indices.size());
        //HashMap<String,Vertex> U = new HashMap<String,Vertex>();
        
        // If the formula can be evaluated to true without creating all the tree
        // is a truth BDD, containing only the True vertex
        Vertex newRoot = null;
        if(!useApplyInCreation)
            newRoot = this.generateTreeFunction(path);
        else{
            newRoot = this.generateTreeUsingApply();
        }
        this.assignRoot(newRoot);
        //_t.end();
        //_t.show();
        //this.print();
        // Reduction of the BDD tree
        //this.reduce();
        if (T.keySet().size() == 1) {
            if(T.containsKey(1))
                this.isTautology = T.get(1) == BDD.T.True;
            if(T.containsKey(0))
                this.isContradiction = T.get(0) == BDD.T.False;
        }
        t.end().show();
    }
    
    /**
     * Constructor for use in this class.
     * NOT FOR PUBLIC USE.
     * Constructs a BDD for a single variable. Optimized to use in the creation of other BDDs.
     * @param variable Variable to build its simple BDD.
     */
    private BDD(String variable){
        this.function = variable;
        int var_index = -1;
        ArrayList<Integer> trivial_variable_ordering = new ArrayList<Integer>(VARIABLES.size());
        for(int i=0; i<VARIABLES.size(); i++){
            trivial_variable_ordering.add(i);
            if(variable.equals(VARIABLES.get(i)))
                var_index = i;
        }
        //System.out.println(var_index);
        this.assignRoot( BDD.T.add(var_index, BDD.T.False, BDD.T.True) );
        this.initVariableOrder(trivial_variable_ordering);
        //System.out.println("POLLAS");
        //System.out.println(this.root);
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
        this.initBDD(trivial_variable_ordering, useApplyInCreation);
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variable_ordering Order of the variables identified each one by its index, so if variable_ordering[i] = j, jth variable comes in ith position.
     */
    public BDD(String function_str, ArrayList<Integer> variable_ordering, boolean useApplyInCreation){
        this.function = function_str;
        this.initBDD(variable_ordering, useApplyInCreation);
    }

    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variable_ordering Order of the variables identified each one by its index, so if variable_ordering[i] = j, jth variable comes in ith position.
     */
    public BDD(String function_str, Integer[] variable_ordering, boolean useApplyInCreation){
        this.function = function_str;
        ArrayList<Integer> variable_ordering_list = new ArrayList<Integer>(Arrays.asList(variable_ordering));
        this.initBDD(variable_ordering_list, useApplyInCreation);
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
        this.initBDD(variable_ordering_list, useApplyInCreation);
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
        this.initBDD(_variable_ordering, useApplyInCreation);
    }
    
    /**
     * Constructor used in the apply operation.
     * NOT FOR PUBLIC USE.
     * Note that this does not need variable ordering.
     * @param T Table that contains the BDD tree.
     * @param function_str Boolean logic function in string form.
     * @param variable_ordering Variable ordering.
     */
    BDD(String function_str, Vertex root, ArrayList<Integer> variable_ordering){
        //TimeMeasurer t = new TimeMeasurer("BDD constructor from T");
        this.function = function_str;
        this.initVariableOrder(variable_ordering);
        // Reduction of the BDD tree
        //this.reduceForApply();
        this.assignRoot(root);
        //t.end().show();
    }
    
    /* END Constructors */
    /**************************************************************************/
    /**************************************************************************/
  
    /**************************************************************************/
    /**************************************************************************/
    
    /*
     * Apply algorithm
     * @param op Logical operation to be computed between this and bdd.
     * @param bdd BDD to operate with current BDD.
     * @return Resulting BDD to operate the current BDD and bdd.
     */
    public BDD apply(String op, BDD bdd){
        try
        {
            TimeMeasurer t = new TimeMeasurer(" AAAAAAAAAAAAAAAAAAAAAAAA apply AAAAAAAAAAAAA");
            BDDApply applicator = new BDDApply(op, this, bdd);
            BDD bddRes = applicator.run();
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
    
    /**
     * Apply one operation to a list of BDDs.
     * @param op Logical Operation to apply among BDDs.
     * @param bdds List of BDDs to reduce to only one using the logical operation.
     * @return BDD that results of operate the BDDs using the operation.
     */
    public static BDD applyToAll(String op, ArrayList<BDD> bdds){
        if(bdds.isEmpty()){
            System.err.println("This bdd was empty");
            return null;
        }
        if(bdds.size()==1){
            return bdds.get(0);
        }
        // We get the first BDD to operate the rest with it
        BDD bdd = bdds.get(0);
        for(int i=1; i<bdds.size(); i++){
            bdd = bdd.apply(op, bdds.get(i));
        }
        // Resultant BDD
        return bdd;
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
                return this.evaluateFromVertex(v.low(), truthAssignement);
            else
                return this.evaluateFromVertex(v.high(), truthAssignement);
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
        //System.out.println(this.root);
        return this.root;
    }
    
    /**
     * Return the number of vertices of this BDD.
     * @return Number of vertices of the BDD.
     */
    public static int totalSize(){
        return BDD.T.getVertices().size();
    }
    
    /**
     * Gets the size of the BDD taking a vertex as root.
     * @param v Vertex that will be taken as root.
     * @return int Size of the tree with v as root.
     */
    private int sizeFromVertex(Vertex v){
        if(v.isLeaf())
            return 1;
        return ( this.sizeFromVertex(v.lowVertex()) + this.sizeFromVertex(v.highVertex()) );
    }

    /**
     * Gets the size of the BDD.
     * @return int Number of vertices of the BDD.
     */
    public int size(){
        // If we have not computed the size
        if(this.size == -1){
            this.size = this.sizeFromVertex(this.root);
        }
        // Size is already computed
        return this.size;
    }
    
    public boolean isContradiction(){ return this.isContradiction; }
    public boolean isTautology(){ return this.isTautology; }
    
    /**
     * Return the vertices of the BDD.
     */
    private ArrayList<Vertex> vertices(Vertex v){
        if(v.isLeaf()){
            ArrayList<Vertex> vertices = new ArrayList<Vertex>(1);
            vertices.add(v);
            return vertices;
        }
        
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(1000);
        vertices.addAll( this.vertices(v.lowVertex()) );
        vertices.addAll( this.vertices(v.highVertex()) );
        return vertices;
    }
    
    /**
     * Return the vertices of the BDD.
     */
    public ArrayList<Vertex> vertices(){
        return this.vertices(this.root);
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Output zone */
    
    /**
     * Gets the string representation of a BDD.
     * @param verbose Must show all variables and other explicit information?
     * @return String String that contains the hastable representation of the BDD.
     */
    public String toString(boolean showVertices){
        String text = "";
        text = "BDD tree for "+this.function+"\n";
        text += "Variables: "+VARIABLES.size()+". ";
        text += "\n";
        text += "Variable ordering: ";
        for(Integer varI : this.present_variable_indices)
            text += varI+", ";
        text = text.substring(0, text.length()-2)+"\n";
        ArrayList<Vertex> vertices = this.vertices();
        text += "Vertices: "+vertices.size()+"\n";
        text += "Root vertex: "+this.root.index+"\n";
        if(showVertices){
            text += "u\tvar_i\tvar\tlow\thigh\n";
            for(Vertex v : vertices){
                String variable = Boolean.toString(v.value());
                if(v.variable > -1)
                    variable = VARIABLES.get(v.variable);
                text += v.index+"\t"+v.variable+"\t"+variable+"\t"+v.low()+"\t"+v.high()+"\n";
            }
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
    /*public void printLevels(){
        for(Integer l : this.levels.keySet()){
            ArrayList<Vertex> vertices = this.levels.get(l);
            System.out.println("Level "+l);
            for(Vertex v : vertices){
                System.out.println("Vertex"+v.toString());
            }
        }
        System.out.flush();
    }*/
    
    public void writeToFile(PrintWriter writer){
        writer.print(this.toString());
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
            // Tree formula
            String line = br.readLine();
            function = line.substring("BDD tree for".length());
            
            // Number of variables
            line = br.readLine();
            line = line.substring("Variables:".length());
            String[] varLine = line.split("\\.");
            int num_variables = Integer.parseInt(varLine[0].replace("\\s+","").trim());
            if(BDD.VARIABLES == null){
                BDD.VARIABLES = new ArrayList<String>(num_variables);
                for(int i=0; i<num_variables; i++)
                    BDD.VARIABLES.add("var_"+(i+1)+"");
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
            
            // Root vertex
            line = br.readLine().split(":")[1].trim();
            Vertex root = BDD.T.get(Integer.parseInt(line));
            
            // Header of the list of vertices
            line = br.readLine();
            
            /*
            // Each vertex
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
                        BDD.VARIABLES.set(variable, variableName);
                    }
                }
                i++;
            }*/
            
        BDD bdd = new BDD(function, root, present_variable_indices);
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
