package djbdd.core;

import java.util.*;

/**
 * Variables for all BDDs.
 * Container of the variables used in all the BDDs existing in our system.
 * @author diegoj
 */
public class VariableList {
    
    /** All the variables that will be know by all BDDs in no particular order */
    protected ArrayList<String> variables = null;
    
    /**
     * All the variables according to the order setted
     * obtained using the swap operator.
     */
    protected ArrayList<String> orderedVariables = null;
    
    /**
     * Global variable ordering.
     * That is, order[i] = val iff ith variable is in position valth.
     */
    protected ArrayList<Integer> order = null;
    
    /**
     * Position of variables.
     * That is, order[position] = var iff variable var is in position position.
     */
    protected ArrayList<Integer> variablePosition = null;
    
    /** Number of variables */
    protected int size = 0;
    
    /**
     * Init the list of ordered variables.
     */
    private void initOrderedVariables(){
        this.orderedVariables = new ArrayList<String>(this.size);
        for(int i=0; i<this.size; i++){
            orderedVariables.add("");
        }
        for(int varIndex=0; varIndex<this.size; varIndex++){
            int position = this.order.get(varIndex);
            String var = this.variables.get(varIndex);
            orderedVariables.set(position, var);
        }
    }
    
    private void initVariablePosition(){
        this.variablePosition = new ArrayList<Integer>(this.size);
        for(int i=0; i<this.size; i++){
            variablePosition.add(0);
        }
        for(int varIndex=0; varIndex<this.size; varIndex++){
            int position = this.order.get(varIndex);
            variablePosition.set(position, varIndex);
        }
    }
    
    /**
     * Construct the list of variables using a list of variables.
     * The order is the given order.
     * @param variables List of variables that will be used in the BDDs.
     */
    public VariableList(ArrayList<String> variables){
        this.variables = variables;
        this.size = variables.size();
        this.order = new ArrayList<Integer>(this.size);
        this.variablePosition = new ArrayList<Integer>(this.size);
        for(int i=0; i<this.size; i++){
            order.add(i);
        }
        this.initVariablePosition();
        this.initOrderedVariables();
    }

    /**
     * Construct the list of variables using a list of variables.
     * The order is a particular order. order[i] = pth <=> ith variable is in pth position.
     * @param variables List of variables that will be used in the BDDs.
     * @param order A list where each element shows the position the variable in this place has.
     */
    public VariableList(ArrayList<String> variables, ArrayList<Integer> order){
        this.variables = variables;
        this.size = variables.size();
        this.order = order;
        this.initVariablePosition();
        this.initOrderedVariables();
    }    

    /**
     * Construct the list of variables using an array of variables.
     * The order is the given order.
     * @param variables Array of variables that will be used in the BDDs.
     */
    public VariableList(String[] variables){
        this(new ArrayList<String>(Arrays.asList(variables)));
    }

    /**
     * Construct the list of variables using an array of variables.
     * The order is a particular order. order[i] = var <=> variable var is in ith position.
     * @param variables Array of variables that will be used in the BDDs.
     * @param order Array of ordered variables.
     */
    public VariableList(String[] variables, String[] order){
        this.size = variables.length;
        this.variables = new ArrayList<String>(Arrays.asList(variables));
        this.order = new ArrayList<Integer>(this.size);
        for(int i=0; i<this.size; i++){
            this.order.add(0);
        }
        for(int i=0; i<this.size; i++){
            String var = order[i];
            int index = this.variables.indexOf(var);
            this.order.set(index, i);
        }
        this.initVariablePosition();
        this.initOrderedVariables();
    }    

    /**
     * Construct the list of variables using the number of variables.
     * @param numberOfVariables Number of variables used in the system.
     */
    public VariableList(int numberOfVariables){
        this.size = numberOfVariables;
        this.variables = new ArrayList<String>(this.size);
        this.order = new ArrayList<Integer>(this.size);
        for(int i=0; i<this.size; i++){
            this.variables.add("{x"+i+"}");
            this.order.add(i);
        }
        this.initVariablePosition();
        this.initOrderedVariables();
    }
    
    /**
     * Copy constructor.
     * @param variableList Other object that will be copied in this object.
     */
    public VariableList(VariableList variableList){
        //System.out.println("VariableList copy constructor");
        this.size = variableList.size;
        this.variables = new ArrayList<String>(this.size);
        this.order = new ArrayList<Integer>(this.size);
        this.orderedVariables = new ArrayList<String>(this.size);
        this.variablePosition = new ArrayList<Integer>(this.size);
        for(int i=0; i<this.size; i++){
            this.variables.add(variableList.variables.get(i));
            this.order.add(variableList.order.get(i));
            this.orderedVariables.add(variableList.orderedVariables.get(i));
            this.variablePosition.add(variableList.variablePosition.get(i));
        }
    }
    
    /**
     * Gets the name of a variable identifed by its index.
     * @param var_index Unique index of the variable.
     * @return Variable with this index.
     */
    public String get(int var_index){
        return this.variables.get(var_index);
    }

    /**
     * Gets the index of a variable in a particular position.
     * @param position Position of the variables.
     * @return Variable index.
     */    
    public int getVariableInPosition(int position){
        return this.variablePosition.get(position);
    }

    /**
     * Gets the position of a particular variable identified by its index.
     * @param var_index Unique index of the variable.
     * @return Position of the variable.
     */
    public int getPositionOfVariable(int var_index){
        return this.order.get(var_index);
    }    
    
    /**
     * Gets the order of the variables.
     * @return List whose elements contains the positions of each variable.
     */
    public ArrayList<Integer> getOrder(){
        return this.order;
    }

    /**
     * Gets the ordered variables.
     * @return List with the ordered variables.
     */    
    public ArrayList<String> getOrderedVariables(){
        return this.orderedVariables;
    }
    
    /**
     * Returns the original list of variables.
     * @return Original list of variables.
     */
    public ArrayList<String> list(){
        return this.variables;
    }
    
    /**
     * Returns the number of variables.
     * @return Number of variable in the system.
     */
    public int size(){
        return this.size;
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/
    
    /**
     * Informs if variable varI is before varJ according to our variable order.
     * @param varI Variable that has to be ahead of varJ to return true.
     * @param varJ The other variable.
     * @return true if varI comes before varJ, false otherwise.
     */
    public boolean variableComesBeforeThan(int varI, int varJ){
        int positionI = this.order.get(varI);
        int positionJ = this.order.get(varJ);
        return positionI < positionJ;
    }
    
    /**
     * Informs if variable varI is after varJ according to our variable order.
     * @param varI Variable that comes after varJ to return true.
     * @param varJ The other variable.
     * @return true if varI comes after varJ, false otherwise.* 
     */
    public boolean variableComesAfterThan(int varI, int varJ){
        int positionI = this.order.get(varI);
        int positionJ = this.order.get(varJ);
        return positionI > positionJ;
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/
    /* Swapping of variables (optimization process) */
    
    /**
     * Swaps two variables.
     * This method will only be called from the swap of two variables in TableT.
     * See {@link djbdd.TableT#swap}. 
     * @param variableI One variable.
     * @param variableJ Other variable. 
     */    
    void swapVariables(int variableI, int variableJ){
        // Order of variables
        int posI = this.order.get(variableI);
        int posJ = this.order.get(variableJ);
        this.order.set(variableI, posJ);
        this.order.set(variableJ, posI);
        this.variablePosition.set(posJ, variableI);
        this.variablePosition.set(posI, variableJ);
        // List of ordered variables
        Collections.swap(this.orderedVariables, posI, posJ);
    }
    
   
    /**
     * Apply order to graph.
     * Used in djbdd.reductors.genetic and djbdd.reductors.random.
     */
    public int applyOrderToGraph() {
        // First be test which variables need to be moved
        // Note that a variable that is not moved, at the end will be in its
        // desired position
        ArrayList<Integer> variablesToMove = new ArrayList<Integer>(this.size);
        for (int varIndex = 0; varIndex < this.size; varIndex++) {
            int varNewPosition = this.order.get(varIndex);
            if(varNewPosition != BDD.VARIABLES.order.get(varIndex)){
                variablesToMove.add(varIndex);
            }
        }
        // For each variable that needs to be moved, move it to its position
        for (int varIndex : variablesToMove) {
            int varPosition = this.order.get(varIndex);
            BDD.T.moveVariable(varIndex, varPosition);
        }
        
        // Returns the new size of the graph
        return BDD.T.size();
    }
    
        /**
     * Apply order to graph.
     * Used in djbdd.reductors.genetic and djbdd.reductors.random.
     */
    public int applyOrderToGraphWhenSizeLessThan(int maximumSizeOfTree) {
        // First be test which variables need to be moved
        // Note that a variable that is not moved, at the end will be in its
        // desired position
        ArrayList<Integer> variablesToMove = new ArrayList<Integer>(this.size);
        for (int varIndex = 0; varIndex < this.size; varIndex++) {
            int varNewPosition = this.order.get(varIndex);
            if(varNewPosition != BDD.VARIABLES.order.get(varIndex)){
                variablesToMove.add(varIndex);
            }
        }
        // For each variable that needs to be moved, move it to its position
        for (int varIndex : variablesToMove) {
            int varPosition = this.order.get(varIndex);
            BDD.T.moveVariable(varIndex, varPosition);
            int bddSize = BDD.T.size();
            if(bddSize > maximumSizeOfTree){
                return bddSize;
            }
        }
        
        // Returns the new size of the graph
        return BDD.T.size();
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/
    /* I/O */
    
    /**
     * Prints the list of variable, its order a the ordered variable list.
     */
    public void print(){
        System.out.println("Variables: " + this.variables);
        System.out.println("Variable position: " + this.order);
        System.out.println("Variable order: " + this.orderedVariables);
    }
    
    /**
     * Gests list of variables, its order a the ordered variable list.
     */
    public String toString(){
        return "" + this.orderedVariables;
    }
    
}
