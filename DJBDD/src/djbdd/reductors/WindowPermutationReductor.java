package djbdd.reductors;

import djbdd.core.BDD;
import java.util.*;
import djbdd.core.VariableList;

/**
 * Fujita et al. & Ishiura et al. Algorithm.
 * Called by R. Rudell: "Windows permutation algorithm"
 * @author diegoj
 */
public class WindowPermutationReductor extends ReductionAlgorithm {
    
    /** Window size */
    private int windowSize;
    //private HashSet<String> permutations;
    private VariableList bestSolution;
    private int bestSize;
    
    public WindowPermutationReductor(int windowSize){
        super();
        this.windowSize = windowSize;
        //this.permutations = new HashSet<String>();
        this.bestSolution = new VariableList(this.VARIABLES);
        this.bestSize = BDD.T.size();
    }
    
    private VariableList getNextOrderForSwappingOfVariable(int variableIndex){
        
        
        
        return null;
    }
    
    private void searchBestOrderFromVariable(int variableIndex){
        boolean swapOK = true;
        for(int swaps=0; swapOK && swaps<this.windowSize-1; swaps++){
            
            
            swapOK = this.T.swapVariable(variableIndex);
            int newSize = this.T.size();
            if(newSize < this.bestSize){
                 this.bestSolution = this.VARIABLES;
                 this.bestSize = newSize;
            }
            
            
            //VariableList newOrder = this.getNextOrderForSwappingOfVariable(variableIndex);
            /*
            if(!this.permutations.contains(newOrder.toString())){
                this.permutations.add(newOrder.toString());
                swapOK = this.T.swapVariable(variableIndex);
                int newSize = this.T.size();
                if(newSize < this.bestSize){
                    this.bestSolution = newOrder;
                    this.bestSize = newSize;
                }
            }
            else{
                swapOK = true;
            }*/
        }
    }
    
    
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
    
    private void assignBestOrder(){
        int numberOfVariables = this.VARIABLES.size();
        for(int varIndex=0; varIndex<numberOfVariables; varIndex++){
            int varBestPosition = this.bestSolution.getPositionOfVariable(varIndex);
            this.T.moveVariable(varIndex, varBestPosition);
        }
    }
    
    @Override
    public void run(){
        int numberOfVariables = this.VARIABLES.size();
        int startLevel = 0;
        int endLevel = startLevel+this.windowSize;
        while(endLevel < numberOfVariables){
            this.searchBestOrderInWindow(startLevel, endLevel);
            endLevel += this.windowSize;
        }
        this.assignBestOrder();
    }
    
}
