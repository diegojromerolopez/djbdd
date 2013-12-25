package djbdd.reductors.windowpermutation;

import djbdd.core.BDD;
import java.util.*;
import djbdd.core.VariableList;
import djbdd.reductors.ReductionAlgorithm;

/**
 * Fujita et al. & Ishiura et al. Algorithm.
 * Called by R. Rudell: "Windows permutation algorithm"
 * @author diegoj
 */
public class WindowPermutationReductor extends ReductionAlgorithm {
    
    /** Window size */
    private int windowSize;
    
    /** Save the best solution */
    private VariableList bestSolution;
    
    /** Save the best size */
    private int bestSize;
    
    /**
     * Constructs the window permutation reductor based on a window size.
     * @param windowSize Window size of the algorithm.
     */
    public WindowPermutationReductor(int windowSize){
        super();
        this.windowSize = windowSize;
        //this.permutations = new HashSet<String>();
        this.bestSolution = new VariableList(this.VARIABLES);
        this.bestSize = BDD.T.size();
    }
    
    /**
     * Search the best order for a variable.
     * Swap forward a variable until there are no possible swap, or
     * it has been a number of swaps equal to the window size.
     * @param variableIndex Variable that will be swapped forward as much as windowSize times.
     */
    private void searchBestOrderFromVariable(int variableIndex){
        boolean swapOK = true;
        for(int swaps=0; swapOK && swaps<this.windowSize-1; swaps++){
            swapOK = this.T.swapVariable(variableIndex);
            int newSize = this.T.size();
            if(newSize < this.bestSize){
                 this.bestSolution = new VariableList(this.VARIABLES);
                 this.bestSize = newSize;
                 if(VERBOSE){
                    System.out.println("Best size "+newSize);
                 }
            }
        }
    }
    
    /**
     * Search the best variable order given a subset of the variables.
     * @param startLevel left bound of the variables.
     * @param endLevel right bound of the variables.
     */
    private void searchBestOrderInWindow(int startLevel, int endLevel){
        ArrayList<Integer> variableIndices = new ArrayList<Integer>(endLevel-startLevel+1);
        for(int level=startLevel; level<endLevel; level++){
            int variableIndex = this.VARIABLES.getVariableInPosition(level);
            variableIndices.add(variableIndex);
        }
        for(Integer variableIndex : variableIndices){
            this.searchBestOrderFromVariable(variableIndex);
        }
    }
    
    /**
     * Assign the best order found in the algorithm.
     */
    private void assignBestOrder(){
        if(VERBOSE){
            System.out.println("Best solution is:");
            this.bestSolution.print();
            System.out.println(this.bestSize);
            System.out.println("-------------------------------------------");
        }
        int numberOfVariables = this.VARIABLES.size();
        for(int varIndex=0; varIndex<numberOfVariables; varIndex++){
            int varBestPosition = this.bestSolution.getPositionOfVariable(varIndex);
            this.T.moveVariable(varIndex, varBestPosition);
        }
    }
    
    /**
     * Runs the algorithm
     */
    @Override
    public void run(){
        int numberOfVariables = this.VARIABLES.size();
        int startLevel = 0;
        int endLevel = startLevel+this.windowSize;
        // We moves the interval util the right bound surpasses
        // the variable list right limit
        while(endLevel < numberOfVariables){
            if(VERBOSE){
                System.out.println("["+startLevel+","+endLevel+"]");
            }
            this.searchBestOrderInWindow(startLevel, endLevel);
            startLevel += 1;
            endLevel += 1;
        }
        // Assigns the best order found
        this.assignBestOrder();
    }
    
}
