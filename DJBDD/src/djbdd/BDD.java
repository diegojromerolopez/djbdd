package djbdd;

import djbdd.timemeasurer.TimeMeasurer;
import djbdd.logic.*;
import djbdd.reductors.*;

import java.util.*;
import java.io.*;

import java.util.regex.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

/**
 * BDD object capable of execute boolean logic operations.
 * TODO: method to evaluate the BDD given a variable boolean assignation.
 * @author diegoj
 */
public class BDD {
    
    /** Hash table that contains the BDD tree itself */
    public static TableT T = null;
    
    /** All the variables that will be know by all BDDs in no particular order */
    static VariableList VARIABLES = null;
    
    /** Maximum number of variables accepted in recursive creation */
    public static final int MAX_NUMBER_OF_VARIABLES_TO_LAUNCH_RECURSIVE_CREATION = 100;
    
    /** Use apply operation in constructor */
    public static final boolean USE_APPLY_IN_CREATION = true;
    
    /** Name of this BDD */
    String name = "";
    
    /** String representation of the boolean logic function of this BDD */
    public String function;
   
    /** Informs if a variable exists in this BDD */
    public HashMap<String,Boolean> variable_existence;
    
    /** List of indices of the present variables sorted by the variable_ordering list  */
    // NOTE: it is important to note that this indices has the ordering given by
    // VARIABLES
    public ArrayList<Integer> present_variable_indices;
   
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
        BDD.VARIABLES = new VariableList(variables);
    }
    
    /**
     * Initialize variables and its order
     * @param variables List of variables existing in the system.
     */
    private static void initVariables(ArrayList<String> variables, ArrayList<Integer> variableOrdering){
        BDD.VARIABLES = new VariableList(variables, variableOrdering);
    }
    
    /**
     * Initialize variables and its order
     * @param variables List of variables existing in the system.
     */
    /*private static void initVariables(String[] variables, String[] orderedVariables){
        BDD.VARIABLES = new VariableList(variables, orderedVariables);
    }*/ 

    
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
     * @param variables List of variables existing in the system.
     */
    public static void init(ArrayList<String> variables, ArrayList<Integer> variableOrdering){
        BDD.initT();
        BDD.initVariables(variables, variableOrdering);
     }    
    
    /**
     * Initialize BDD system.
     * @param variables Array of variables existing in the system.
     */
    public static void init(String[] variables){
        BDD.initT();
        // Convert the array of strings to an ArrayList<String>
        ArrayList<String> variableList = new ArrayList<String>(Arrays.asList(variables));
        BDD.initVariables(variableList);
    }
    
    /**
     * Get the variables defined for all BDDs.
     * @return List of variables defined in this enviroment.
     */
    public static VariableList variables(){
        return VARIABLES;
    }
    /*
    public static boolean variableIsLessThan(int var1, int var2){
        return BDD.VARIABLE_ORDERING.get(var1) < BDD.VARIABLE_ORDERING.get(var2);
    }
    
    public static boolean variableIsEqualTo(int var1, int var2){
        return BDD.VARIABLE_ORDERING.get(var1) == BDD.VARIABLE_ORDERING.get(var2);
    }
    
   public static boolean variableIsGreaterThan(int var1, int var2){
        return BDD.VARIABLE_ORDERING.get(var1) > BDD.VARIABLE_ORDERING.get(var2);
   }*/  
    
    /************************* END INIT BDD SYSTEM ****************************/
    
    /**************************************************************************/
    /**************************************************************************/
    /* Root assignement */
    
    public final void assignRoot(Vertex newRoot){
        if(this.root!=null && this.root.index == newRoot.index){
            return;
        }
        
        if(this.root!=null){
            this.root.decNumRootedBDDs();
        }
        
        this.root = newRoot;
        this.root.incNumRootedBDDs();
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
        this.root = bdd.root;
        this.variable_existence = bdd.variable_existence;
        //this.variable_ordering = bdd.variable_ordering;
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
     * This is the old method, please use the apply based one ({@link djbdd.BDD#generateTreeUsingApply})
     * @param path ArrayList of variables that contains a boolean assignement for each variable.
     * @param U HashMap that ensures the uniqueness of the generated vertices.
     * @return Vertex of each level.
     */
    private Vertex generateTreeFunction(ArrayList<Boolean> path){
            int path_len = path.size();
            if (path_len < this.present_variable_indices.size())
            {
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
                    return v_low;
                }
                
                ////////
                //// 2.- Uniqueness
                // No two vertices have the same variable, and low and high
                // vertices
                        
                // Variable of the new vertex
                int var_index = this.present_variable_indices.get(path_len);
                return T.add(var_index, v_low, v_high);
            }
            else if(path_len == this.present_variable_indices.size())
            {
                // Reached leafes
                boolean value = this.evaluatePath(path);
                if(value){
                    return BDD.T.True;
                }
                return BDD.T.False;
            }
            return null;
    }
    
    /**
     * Optimize OR operation between two BDDs.
     * @param bdd1 One BDD.
     * @param bdd2 The other BDD.
     * @return If return something different than null, this is the result of the OR operation between the BDDs.
     */
    private static BDD optimizeORTreeGenerationFromAST(BDD bdd1, BDD bdd2){
        if (bdd1.isTautology) {
            return bdd1;
        }
        if (bdd2.isTautology) {
            return bdd2;
        }
        if (bdd1.isContradiction) {
            return bdd2;
        }
        if (bdd2.isContradiction) {
            return bdd1;
        }
        return null;
    
    }
    
    /**
     * Optimize AND operation between two BDDs.
     * @param bdd1 One BDD.
     * @param bdd2 The other BDD.
     * @return If return something different than null, this is the result of the AND operation between the BDDs.
     */    
    private static BDD optimizeANDTreeGenerationFromAST(BDD bdd1, BDD bdd2){
        if (bdd1.isTautology) {
            return bdd2;
        }
        if (bdd2.isTautology) {
            return bdd1;
        }
        if (bdd1.isContradiction) {
            return bdd1;
        }
        if (bdd2.isContradiction) {
            return bdd2;
        }
        return null;
    
    }

    /**
     * Optimize operation between two BDDs.
     * @param op Operation to make between the two BDDs.
     * @param bdd1 One BDD.
     * @param bdd2 The other BDD.
     * @return If return something different than null, this is the result of the operation between the BDDs.
     */ 
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
    private static BDD generateTreeFromAST(Tree tree, boolean positiveTree) {
        // Get the number of children of the tree
        int childCount = tree.getChildCount();
        
        // If we have a leaf, the node has a variable not an operation
        // we create the BDD using recursion (it's only one step deep)
        if (childCount == 0) {
            BDD bdd = BDD.factoryFromVarible(tree.getText(), positiveTree);
            return bdd;
        }
        
        // Otherwise, we get an operation node
        String op = tree.getText();
        boolean positiveChild = !op.equals("!");
        
        // For each children, we recursively call generateTreeFromAST
        // And assign current node as parent of the subtree generated
        /*
        List<CommonTree> children = (List<CommonTree>) tree.getChildren();
        ArrayList<BDD> bdds = new ArrayList<BDD>(childCount);
        for (CommonTree child : children) {
            BDD bddI = BDD.generateTreeFromAST(child);
            bdds.add(bddI);
        }
        */
        ArrayList<BDD> bdds = new ArrayList<BDD>(childCount);
        for(int childI=0; childI<childCount; childI++)
        {
            Tree child = tree.getChild(childI);
            BDD bddI = BDD.generateTreeFromAST(child, positiveChild);
            bdds.add(bddI);
        }        
        
        // For ech children, we apply the operation given in their parent node
        // and construct a new BDD for the parention node
        String applyOp = op;
        if(!op.equals("!") && !positiveTree){
            applyOp = BooleanEvaluator.neg(op);
        }
        BDD bdd = bdds.get(0);
        for (int i = 1; i < bdds.size(); i++) {
            BDD bddI = bdds.get(i);
            BDD bddRes = null;//BDD.optimizeTreeGenerationFromAST(op, bdd, bddI);
            if (bddRes == null) {
                bddRes = bdd.apply(applyOp, bddI);
                bdd.delete();
                bddI.delete();
            }

            bdd = bddRes;
        }
        return bdd;
    }
   
   /**
    * Generate the Tree usin the APPLY operation.
    * The 'this' object IS MODIFIED.
    * @return Vertex that is root of the BDD generated.
    */
    private Vertex generateTreeUsingApply(){
        // Creating the lexer and the parser for the logic formula
        LogicLexer lexer = new LogicLexer(new ANTLRStringStream(this.function));
        LogicParser parser = new LogicParser(new CommonTokenStream(lexer));
        
        // Invoke the entry point of the parser (the parse() method) and get the AST
        Tree tree = null;
        try{
            tree = (Tree)parser.parse().getTree();
        }catch(Exception e){
            System.err.println("ERROR. Parsing of the expression "+this.function+" has failed. Detailed report:");
            e.printStackTrace();
        }
        
        // Call to create the BDD from an AST
        BDD bdd = BDD.generateTreeFromAST(tree, true);
        this.assignInTreeGeneration(bdd);
        bdd = null;
        return this.root;
    }
    
    
    /**************************************************************************/
    /**************************************************************************/
    /* Constructors */

    /**
     * Initialize parameters dependant of variables and variable ordering in BDD constructor.
     * @param variables Name of the variables and order of them in the BDD.
     * @param variable_ordering Order of the variables given this way: variable_ordering[i]=j => jth variable is in ith position.
     */
    /*private void initVariableOrder(ArrayList<Integer> variable_ordering){
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
    }*/
    
    /**
     * Initializes lists which tells the BDD what variables have.
     * Initialize the variable_existence and present_variable_indices lists.
     */
    private void initVariablePresence(){
        this.present_variable_indices = new ArrayList<Integer>();
        this.variable_existence = new HashMap<String,Boolean>();
        for(int variable_index=0; variable_index<BDD.VARIABLES.size(); variable_index++){
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
    private void initBDD(){
        TimeMeasurer t = new TimeMeasurer(" ::::::::::::. BDD constructor "+this.function+".::::::::::::");
        this.initVariablePresence();

        // Generation of the BDD tree
        ArrayList<Boolean> path = new ArrayList<Boolean>(this.present_variable_indices.size());
        
        // If the formula can be evaluated to true without creating all the tree
        // is a truth BDD, containing only the True vertex
        Vertex newRoot = null;
        if(!BDD.USE_APPLY_IN_CREATION)
            newRoot = this.generateTreeFunction(path);
        else{
            newRoot = this.generateTreeUsingApply();
        }
        this.assignRoot(newRoot);

        if (T.keySet().size() == 1) {
            if(T.containsKey(1))
                this.isTautology = T.get(1) == BDD.T.True;
            if(T.containsKey(0))
                this.isContradiction = T.get(0) == BDD.T.False;
        }
        t.end().show();
    }
    
    /**
     * Factory from a variable.
     * Constructs a BDD for a single variable. Optimized to use in the creation of other BDDs.
     * @param variable Variable to build its simple BDD.
     */
    private static BDD factoryFromVarible(String variable, boolean positiveVariable){
        BDD bdd = new BDD();
        if(positiveVariable)
            bdd.function = variable;
        else
            bdd.function = "!"+variable;
        int var_index = -1;
        //ArrayList<Integer> trivial_variable_ordering = new ArrayList<Integer>(VARIABLES.size());
        for (int i = 0; i < VARIABLES.size(); i++) {
            //trivial_variable_ordering.add(i);
            if (variable.equals(VARIABLES.get(i))) {
                var_index = i;
            }
        }
        // Cases:
        // 1.- Tautology
        if(variable.equals("true")){
            bdd.root = BDD.T.True;
            bdd.isTautology = true;
        }
        // 2.- Contradiction
        else if(variable.equals("false")){
            bdd.root = BDD.T.False;
            bdd.isContradiction = true;
        }
        // 3.- Lone variable
        else{
            Vertex low = null;
            Vertex high = null;
            if(positiveVariable){
                low = BDD.T.False;
                high = BDD.T.True;
            }
            else{
                low = BDD.T.True;
                high = BDD.T.False;
            }
            
            bdd.assignRoot( BDD.T.add(var_index, low, high) );
        }
        bdd.initVariablePresence();
        return bdd;
    }

    
    /**
     * Constructor for use in this class
     * Empty private constructor.
     * Use only in factories.
     */
    private BDD(){
    
    }
    
    /**
     * Constructor of BDD.
     * @param function_str String containing the boolean formula. Use Java representation of the formula. Don't forget using parentheses.
     * @param variables Name of the variables and order of them in the BDD.
     */
    public BDD(String function_str){
        this.function = function_str;
        // Init the BDD
        this.initBDD();
    }
    
    /**
     * Constructor used in the apply operation.
     * NOT FOR PUBLIC USE.
     * Note that this does not need variable ordering.
     * @param T Table that contains the BDD tree.
     * @param function_str Boolean logic function in string form.
     */
    BDD(String function_str, Vertex root){
        //TimeMeasurer t = new TimeMeasurer("BDD constructor from T");
        this.function = function_str;
        // Init lists that shows if a variable is in this BDD or else.
        this.initVariablePresence();
        // Root assignement of the BDD tree
        this.assignRoot(root);
        //t.end().show();
    }
    
    /* END Constructors */
    /**************************************************************************/
    /**************************************************************************/
  
    /**************************************************************************/
    /**************************************************************************/
    /* Destructor */

    /**
     * Frees the memory of a BDD.
     * @param bdd BDD that will be marked as garbage-collectable.
     */
    private static void destruct(BDD bdd){
        bdd = null;
    }
    
    /**
     * Frees the memory and makes the actions associated with the destruction of
     * this BDD.
     */
    public void delete(){
        this.root.decNumRootedBDDs();
        BDD.destruct(this);
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Apply algorithm */
    
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
     * @param callGC Should we call en each loop to the garbage collector?
     * @return BDD that results of operate the BDDs using the operation.
     */
    public static BDD applyToAll(String op, ArrayList<BDD> bdds, boolean callGC){
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
            System.out.println("BDD.applyToAll "+(i+1)+"/"+bdds.size());
            if(callGC)
                BDD.T.gc();
        }
        // Resultant BDD
        return bdd;
    }
    
     /**
     * Apply one operation to a list of BDDs.
     * @param op Logical Operation to apply among BDDs.
     * @param bdds List of BDDs to reduce to only one using the logical operation.
     * @return BDD that results of operate the BDDs using the operation.
     */
    public static BDD applyToAll(String op, ArrayList<BDD> bdds){
        return BDD.applyToAll(op, bdds, false);
    }

    /* END of Apply algorithm */
    /**************************************************************************/
    /**************************************************************************/
        
    
    /**************************************************************************/
    /**************************************************************************/
    /* Restrict algorithm */

    /**
     * Recursive call to get a BDD from the root Vertex.
     * @param vRoot Root vertex of sub-BDD.
     * @param assignement Boolean assignement.
     * @return Root vertex of the sub-BDD.
     */
    private Vertex restrict(Vertex vRoot, HashMap<Integer,Boolean> assignement){
        if(vRoot.isLeaf()){
            return T.add(vRoot);
        }
        
        Integer variable = vRoot.variable;
        if(assignement.containsKey(variable))
        {
            boolean value = assignement.get(variable);
            if(value){
                return this.restrict(vRoot.high(), assignement);
            }else{
                return this.restrict(vRoot.low(), assignement);
            }
        }
        else
        {
            Vertex low = this.restrict(vRoot.low(), assignement);
            Vertex high = this.restrict(vRoot.high(), assignement);
            if(low.index == high.index)
                return low;
            return T.add(variable, low, high);
        }
    }
    
    /**
     * Get a new BDD based on this BDD with a boolean assignement on some variables.
     * @param assignement Boolean assignement.
     * @return BDD with a boolean assignement based on this BDD.
     */
    public BDD restrict(HashMap<Integer,Boolean> assignement){
        Vertex restrictedBDDRoot = this.restrict(this.root,assignement);
        String rfunction = this.function;
        ArrayList<String> variables = BDD.VARIABLES.list();
        for(Map.Entry<Integer,Boolean> pair : assignement.entrySet()){
            String var = variables.get(pair.getKey());
            String value = pair.getValue().toString();
            rfunction = rfunction.replace(var, value);
        }
        return new BDD(rfunction, restrictedBDDRoot);
    }
    
    /* END of Restrict algorithm */
    /**************************************************************************/
    /**************************************************************************/
    
    
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
            if(!truthAssignement.get(v.variable()))
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
    /* Garbage collector */
    
    /**
     * Call the garbage collector to erase weak references.
     */
    public static int gc(){
        return BDD.T.gc();
    }
    
   
    /**************************************************************************/
    /**************************************************************************/
    /* Get information about BDD zone */

    /**
     * Return the root vertex.
     * @return The root vertex of the BDD.
     */
    public Vertex root(){
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
     * @param computedVertexIndices Set of traversed vertex index.
     */
    private void traverseFromVertex(Vertex v, HashSet<Integer> computedVertexIndices){
        if(!v.isLeaf()){
            this.traverseFromVertex(v.low(), computedVertexIndices);
            this.traverseFromVertex(v.high(), computedVertexIndices);
        }
        computedVertexIndices.add(v.index);
    }

    /**
     * Gets the size of the BDD.
     * @return int Number of vertices of the BDD.
     */
    public int size(){
        // If we have not computed the size
        HashSet<Integer> indices = new HashSet<Integer>();
        if(this.size == -1 || true){
            this.traverseFromVertex(this.root, indices);
            this.size = indices.size();
        }
        // Size is already computed
        return this.size;
    }
    
    /**
     * Informs if the current BDD is a contradiction (i.e. is always false).
     * @return true if the BDD is a contradiction.
     */
    public boolean isContradiction(){ return this.isContradiction; }
    
    /**
     * Informs if the current BDD is a tautology  (i.e. is always true).
     * @return true if the BDD is a tautology.
     */
    public boolean isTautology(){ return this.isTautology; }
    
    /**
     * Return the vertices of the BDD from a vertex.
     * @param v Root vertex, used to get the descendant tree.
     * @return List of vertices descendants of a root.
     */
    private HashMap<String,Vertex> vertices(Vertex v){
        if(v.isLeaf()){
            HashMap<String,Vertex> vertices = new HashMap<String,Vertex>(1);
            vertices.put(v.uniqueKey(),v);
            return vertices;
        }
        
        HashMap<String,Vertex> vertices = new HashMap<String,Vertex>(1000);
        vertices.put(v.uniqueKey(),v);
        vertices.putAll( this.vertices(v.low()) );
        vertices.putAll( this.vertices(v.high()) );
        return vertices;
    }
    
    /**
     * Return the vertices of the BDD.
     * @return List of vertices of the BDD.
     */
    public ArrayList<Vertex> vertices(){
        return new ArrayList<Vertex>(this.vertices(this.root).values());
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Reductions of the BDDs in our enviroment */
    
    /**
     * Reduce the global graph of vertices using some algorithm identified by the type parameter.
     * @param type Type of the reduction algorithm.
     */
    public static void reduce(int type){
        // Destroy the not used vertices
        BDD.gc();
        // Reduce the multirooted graph
        ReductionAlgorithm reductor = null;
        if(type == ReductionAlgorithm.SIFTING){
            reductor = new SiftingReductor();
        }
        reductor.run();
        // Destroy the not used vertices
        BDD.gc();
    }
    
    /**
     * Reduce the global graph of vertices of vertices using Rudell's
     * variable reordering.
     */
    public static void reduce(){
        BDD.reduce(ReductionAlgorithm.SIFTING);
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
        StringBuilder text = new StringBuilder("");
        text.append("BDD tree for ").append(this.function).append("\n");
        text.append("Variables: ").append(VARIABLES.size()).append(". ").append("\n");
        text.append("Present variables: ");
        int varIndex = 0;
        int lastElementIndex = this.present_variable_indices.size()-1;
        for(Integer varI : this.present_variable_indices){
            text.append(varI);
            if(varIndex < lastElementIndex)
                text.append(", ");
            varIndex++;
        }
        text.append("\n");
        ArrayList<Vertex> vertices = this.vertices();
        text.append("Vertices: ").append(vertices.size()).append("\n");
        text.append("Root vertex: ").append(this.root.index).append("\n");
        if(showVertices){
            text.append("u\tvar_i\tvar\tlow\thigh\n");
            for(Vertex v : vertices){
                String variable = Boolean.toString(v.value());
                if(v.variable() > -1)
                    variable = VARIABLES.get(v.variable());
                text.append(v.index).append("\t").append(v.variable()).append("\t").append(variable).append("\t").append(v.lowIndex()).append("\t").append(v.highIndex()).append("\n");
            }
        }
        return text.toString();
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
     * @param printVertices Should we print vertices?
     */
    public void print(boolean printVertices){
        System.out.println(this.toString(printVertices));
        System.out.flush();
    }    
    
    
    /**
     * Write BDD to file.
     * @param writer PrintWriter where the BDD will be written.
     */
    public void writeToFile(PrintWriter writer){
        writer.print(this.toString());
    }
    
    /**
     * Prints the BDD table to a file.
     * @param path Path of the file that will contain the BDD.
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
            System.err.println("BDD "+this.function+" has created an error");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Read the BDD table from a BufferedReader.
     * @param br BufferedReader that contained the BDD.
     * @return BDD contained in br.
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
            int num_variables = Integer.parseInt(varLine[0].replace("\\s+", "").trim());
            if (BDD.VARIABLES == null)
            {
                ArrayList<String> variables = new ArrayList<String>(num_variables);
                for (int i = 0; i < num_variables; i++) {
                    variables.add("{var_" + (i + 1) + "}");
                }
                BDD.init(variables);
            }

            // Variable ordering
            ArrayList<Integer> present_variable_indices = new ArrayList<Integer>(num_variables);
            line = br.readLine();

            if (line.equals("Variable ordering")) {
                // There are no present_variables, BDD is true or false
                // DO NOTHING!
            } else {
                line = line.substring("Variable ordering:".length()).trim();
                //System.out.println("VAR ORDER '"+line+"'");
                String[] order = line.split(",\\s*");
                for (String o : order) {
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
            int rootIndex = Integer.parseInt(line);
            
            // If the root is null, we have to read the vertices
            if(!BDD.T.containsKey(rootIndex)){
                BDD.T.fromBufferedReader(br);
            }
            
            Vertex root = BDD.T.get(rootIndex);

            BDD bdd = new BDD(function, root);
            br.close();
            return bdd;
        } catch (Exception e) {
            System.err.println("Error in BDD.fromBufferedReader.");
            System.err.println("BDD " + function + " has create an error");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
    
    /**
     * Read the BDD table from a file.
     * @param path File path of the file that contains the BDD.
     * @return BDD contained in path.
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
     * @param bddString String that contains the BDD.
     * @return BDD object with the BDD contained in bddString.
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
