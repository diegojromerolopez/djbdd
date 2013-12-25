package djbdd.reductors.totalsearch;

import djbdd.core.BDD;
import djbdd.core.VariableList;
import djbdd.io.Printer;
import djbdd.reductors.sifting.SiftingReductor;

/**
 * Reduces the graph using a exact search algorithm.
 * Realizes a full search.
 * NOTE: expect this algorightm to be very slow.
 * @author diegoj
 */
public class TotalSearchReductor extends SiftingReductor {
  
    private VariableList bestSolution = null;
    private int bestSize = -1;
    
    public TotalSearchReductor(){
        super();
        BDD.gc();
        this.bestSolution = new VariableList(this.VARIABLES);
        this.bestSize = this.T.size();
    }
    
    private void updateBestSize(){
        int newSize = this.T.size();
        if(VERBOSE){
            System.out.println("|||||||||||||||||||||||||||||||||||||||||||||");
            System.out.println("NEW "+newSize);
            System.out.println("ACTUAL "+bestSize);
            System.out.println("ACTUAL: ");
            this.bestSolution.print();
            System.out.println(this.bestSize);
        }
        if(newSize < bestSize){
            this.bestSolution = new VariableList(this.VARIABLES);
            this.bestSize = newSize;
            if(VERBOSE){
                System.out.println("MEJORADO: ");
                this.bestSolution.print();
                System.out.println(this.bestSize);
            }
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
        BDD.gc();
    }
    
    @Override
    public void run(){
        Printer.printTableT("000.png");
        // For each variable find its better position given that
        // the other variables have moving positions
        bestSolution = this.VARIABLES;
        bestSize = T.size();
        for(int var1Index: this.variableOrder){
            this.findBestPositionForVariable(var1Index);
            this.updateBestSize();
            for(int var2Index: this.variableOrder){
                if(var1Index != var2Index){
                    this.findBestPositionForVariable(var2Index);
                    this.updateBestSize();
                }
            }
        }
        // The result have to be the best posible ordering
        this.assignBestOrder();
    }
    
}
