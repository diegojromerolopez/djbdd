package djbdd.reductors.sifting;

import djbdd.io.*;

import djbdd.reductors.ReductionAlgorithm;
import java.util.*;

/**
 * Runs the sifrint reductor invented by Rudell.
 * @author diegoj
 */
public class SiftingReductor extends ReductionAlgorithm {
    
    /**
     * Occurence of variables. Used to get the variables in the
     * reduction method in descendant order according to its occurrence.
     */
    protected TreeMap<Integer,Integer> variableOccurence;
    
    /** Order of variables */
    protected ArrayList<Integer> variableOrder;
    
    /** Number of vertices in the system */
    protected int size;
    
    /** Number of variables in the system */
    protected final int numVariables;
    
    /** Maximum variable position */
    protected final int lastVariablePosition;
    
    public static final int VARIABLES_WITH_MORE_VERTICES_BEFORE = 0;
    public static final int VARIABLES_WITH_RANDOM_ORDER = 1;
    public static final int VARIABLES_WITH_SAME_ORDER = 2;
    
    /**
     * Creates a count of how many vertices have a particular variable.
     * Stores this count in variableOccurence class member.
     */
    private void initVariableOcurrence(){
        variableOccurence = new TreeMap<Integer,Integer>();
        for(int variable=0; variable<this.numVariables; variable++){
            int varSize = 0;
            if(this.T.V.containsKey(variable))
                varSize = this.T.V.get(variable).size();
            variableOccurence.put(variable, varSize);
        }
    }
    
    /**
     * Gets a list of the variables in descending order according to their
     * associated number of vertices.
     */
    private void initVariableOrderDesc(){

       //Transfer as List and sort it
       ArrayList<Map.Entry<Integer, Integer>> l = new ArrayList<Map.Entry<Integer, Integer>>(variableOccurence.entrySet());
       Collections.sort(l, new Comparator<Map.Entry<Integer, Integer>>(){

         public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }});

       this.variableOrder = new ArrayList<Integer>(l.size());
       for(Map.Entry<Integer, Integer> e : l){
           this.variableOrder.add((Integer)e.getKey());
       }
    }
    
    private static int randomInt(int min, int max){
        return random.Random.randInt(min, max);
    }
    
    private void initVariableOrderRandom(){
        // Randomize the variable order
        HashMap<Integer,Boolean> selectedVariables = new HashMap<Integer,Boolean>();
        this.variableOrder = new ArrayList<Integer>(this.numVariables);
        for(int i=0; i<this.numVariables; i++){
            int selectedVariable = SiftingReductor.randomInt(0, this.lastVariablePosition);
            while(selectedVariables.containsKey(selectedVariable)){
                selectedVariable = SiftingReductor.randomInt(0, this.lastVariablePosition);
            }
            selectedVariables.put(selectedVariable, VERBOSE);
            this.variableOrder.add(selectedVariable);
        }
    }
    
    private void initVariableOrderWithSameOrder(){
        for(int i=0; i<this.numVariables; i++){
           this.variableOrder.add(i);
        }
    }
    
    /**
     * Constructor of the Rudell's reduction method.
     */
    public SiftingReductor(){
        super();
        // Construct the order of variables
        this.numVariables = this.VARIABLES.size();
        this.lastVariablePosition = this.numVariables - 1;
        this.size = this.T.size();
        // Init the order of variables
        this.initVariableOcurrence();
        this.initVariableOrderDesc();
    }
    
    public SiftingReductor(int variableOrder){
        super();
        // Construct the order of variables
        this.numVariables = this.VARIABLES.size();
        this.lastVariablePosition = this.numVariables - 1;
        this.size = this.T.size();
        // Init the order of variables
        if(variableOrder == VARIABLES_WITH_MORE_VERTICES_BEFORE){
            this.initVariableOcurrence();
            this.initVariableOrderDesc();
        }
        else if(variableOrder == VARIABLES_WITH_RANDOM_ORDER){
            this.initVariableOrderRandom();
        }
        else if(variableOrder == VARIABLES_WITH_SAME_ORDER){
            this.initVariableOrderWithSameOrder();
        }
    }

    /**
     * Finds the best position of a variable before its current position.
     * @param varIndex Index of the variable.
     * @return Best backward position found for variable with index varIndex.
     */
    protected int findBestBackwardPosition(int varIndex, int varIndexBestPosition){
        int varIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
        boolean swapWasMade = false;
        final String var = this.VARIABLES.get(varIndex);
        
        do {
            if (VERBOSE) {
                System.out.println("Back!!! " + varIndex + " is in position " + varIndexPosition);
            }
            swapWasMade = this.T.swapBack(varIndexPosition);
            int Tsize = this.T.gc();

            if (VERBOSE) {
                System.out.println(this.VARIABLES.getOrderedVariables() + " " + size);
            }

            if (Tsize < size) {
                size = Tsize;
                varIndexBestPosition = varIndexPosition - 1;
                if (VERBOSE) {
                    System.out.println(Tsize + " IS THE FUCKIN BEST WITH " + varIndex + " (" + var + ") at " + varIndexBestPosition);
                }
            }
            if (swapWasMade) {
                varIndexPosition--;
            }
        } while (swapWasMade);
        if(VERBOSE){
            System.out.println("END Back!!! " + varIndex + " is in position " + varIndexPosition);
        }
        return varIndexBestPosition;
    }
    
    /**
     * Finds the best position of a variable next to its current position.
     * @param varIndex Index of the variable.
     * @return Best position found for variable with index varIndex.
     */
    protected int findBestForwardPosition(int varIndex, int varIndexBestPosition){
        int varIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
        int newVarIndexPosition = varIndexPosition;
        boolean swapWasMade = false;
        final String var = this.VARIABLES.get(varIndex);
        
        int i = 1;
        do {
            if (VERBOSE) {
                System.out.println("---------------------------------------------");
                System.out.println("---------------------------------------------");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Forward!!!!!");
                System.out.println("Before swap " + i + ": variable  " + varIndex + " (" + var + ") is in position " + varIndexPosition);
                System.out.flush();
                //this.VARIABLES.print();
            }

            swapWasMade = this.T.swap(varIndexPosition);

            if (VERBOSE) {
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            }
            newVarIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
            if (VERBOSE) {
                if (swapWasMade) {
                    System.out.println("After swap " + i + ": variable " + varIndex + " (" + var + ") is in position " + newVarIndexPosition);
                } else {
                    System.out.println("NO swap " + i + ": variable " + varIndex + " (" + var + ") is in position " + newVarIndexPosition);
                }
                this.VARIABLES.print();
            }

            int Tsize = this.T.gc();
            if (VERBOSE) {
                System.out.println(this.VARIABLES.getOrderedVariables() + " " + size);
            }
            if (Tsize < size) {
                size = Tsize;
                varIndexBestPosition = varIndexPosition + 1;
                if (VERBOSE) {
                    System.out.println(Tsize + " IS THE FUCKIN BEST WITH " + varIndex + " (" + var + ") at " + varIndexBestPosition+" with size "+size);
                }
            }
            if (swapWasMade) {
                varIndexPosition++;
            }
            i++;

        } while (swapWasMade && varIndexPosition < lastVariablePosition);
        return varIndexBestPosition;
    }
    
   
    /**
     * Finds the best position of a variable supossing the others are fixed in
     * their positions.
     * @param varIndex Index of the variable to move to its best position.
     * @return Best position of the variables whose index is varIndex.
     */
    protected int findBestPositionForVariable(int varIndex){
        if (VERBOSE) {
            System.out.println("");
            System.out.println("=============================================");
            System.out.println("=============================================");
            System.out.println("STARTS " + varIndex + " (" + this.VARIABLES.get(varIndex) + ")");
            System.out.println("The size of the tree is "+T.size());
        }

        int varIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
        int varIndexBestPosition = varIndexPosition;
        if (VERBOSE) {
            Printer.printTableT("test15_" + this.VARIABLES.toString());
        }
        
        if(varIndexPosition <= this.numVariables/2){
            varIndexBestPosition = this.findBestBackwardPosition(varIndex, varIndexBestPosition);
            varIndexBestPosition = this.findBestForwardPosition(varIndex, varIndexBestPosition);
        }
        else
        {
            varIndexBestPosition = this.findBestForwardPosition(varIndex, varIndexBestPosition);
            varIndexBestPosition = this.findBestBackwardPosition(varIndex, varIndexBestPosition);
        }
        
        if (VERBOSE) {
            System.out.println("The position of variable "+varIndex+" ("+this.VARIABLES.get(varIndex)+") was "+varIndexPosition);
            System.out.println("The best position of variable "+varIndex+" ("+this.VARIABLES.get(varIndex)+") is "+varIndexBestPosition);
            System.out.println("Current position of variable "+varIndex+" ("+this.VARIABLES.get(varIndex)+") is "+this.VARIABLES.getPositionOfVariable(varIndex));
        }

        
        // Moves variable to best position
        if(varIndexBestPosition != this.VARIABLES.getPositionOfVariable(varIndex)){
            if(VERBOSE){
                System.out.println("Move variable "+ varIndex +" to "+varIndexBestPosition);
            }
            this.T.moveVariable(varIndex, varIndexBestPosition);
            if(VERBOSE){
                System.out.println("After moving, current position of variable "+varIndex+" ("+this.VARIABLES.get(varIndex)+") is "+this.VARIABLES.getPositionOfVariable(varIndex));
            }
        }
        //*/
        if (VERBOSE) {
            System.out.println("ENDS " + varIndex);
            System.out.println("The size of the tree is "+T.size()+" ¿¿"+size+"??");
            System.out.println("=============================================");
            System.out.println("=============================================");
        }
        return size;
    }
    
    
    /**
     * Executes the reduction method.
     */
    public void execute() {
        // For each variable find its better position given that
        // the other variables are in fixed positions
        //Printer.printTableT("sifting_before");
        for (int varIndex : this.variableOrder) {
            if(VERBOSE){
                System.out.println("Start searching for best position for variable "+varIndex);
                this.VARIABLES.print();
            }
            this.findBestPositionForVariable(varIndex);
        }
        //Printer.printTableT("sifting_after");
        //this.VARIABLES.print();
    }
    
}
