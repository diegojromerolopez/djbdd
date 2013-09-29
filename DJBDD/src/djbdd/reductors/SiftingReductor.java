/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors;

import djbdd.io.*;

/**
 *
 * @author diegoj
 */
public class SiftingReductor extends ReductionAlgorithm {
    
    public void run(){
        int numVariables = this.VARIABLES.size();
        int lastVariablePosition = numVariables - 1;
        int size = this.T.size();
        // For each variable find its better position given that
        // the other variables are in fixed positions
        for(int varIndex=0; varIndex<numVariables; varIndex++){
            System.out.println("=============================================");
            System.out.println("STARTS "+varIndex);
            String var = this.VARIABLES.get(varIndex);
            int varIndexBestPosition = this.VARIABLES.getPositionOfVariable(varIndex);
            int varIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
            int newVarIndexPosition = varIndexPosition;
            boolean swapWasMade = false;
            Printer.printTableT("test15_"+this.VARIABLES.toString());
            int i=1;
            do{
                System.out.println("---------------------------------------------");
                System.out.println("---------------------------------------------");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Before swap "+i+": variable  "+varIndex+" ("+var+") is in position "+varIndexPosition);
                this.VARIABLES.print();
                
                swapWasMade = this.T.swap(varIndexPosition);
                
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                newVarIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
                if(swapWasMade){
                    System.out.println("After swap "+i+": variable "+varIndex+" ("+var+") is in position "+newVarIndexPosition);
                }else{
                    System.out.println("NO swap "+i+": variable "+varIndex+" ("+var+") is in position "+newVarIndexPosition);
                }
                this.VARIABLES.print();
                
                Printer.printTableT("test15_swap_"+i+"_swapped_var_at_"+varIndexPosition+"_("+var+")_"+this.VARIABLES.toString());
                this.T.gc();
                if(size > this.T.size()){
                    size = this.T.size();
                    varIndexBestPosition = varIndexPosition;
                }
                if(swapWasMade)
                    varIndexPosition++;
                i++;
            }while(swapWasMade && varIndexPosition < lastVariablePosition);
            
            // We go back
            do{
                System.out.println("Back!!! "+varIndex+" is in position "+varIndexPosition);
                swapWasMade = this.T.swapBack(varIndexPosition);
                this.T.gc();
                if(size > this.T.size()){
                    size = this.T.size();
                    varIndexBestPosition = varIndexPosition;
                    System.out.println("ALERT!!! When "+varIndex+" is at "+varIndexBestPosition+" the size is "+size);
                }
                if(swapWasMade)
                    varIndexPosition--;
            }while(swapWasMade);
            
            System.out.println("The best position is "+varIndexBestPosition);
            
            // Move to the best position
            varIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
            swapWasMade = true;
            while(swapWasMade && varIndexPosition < varIndexBestPosition){
                swapWasMade = this.T.swap(varIndexPosition);
                if(swapWasMade)
                    varIndexPosition++;
            }
              
            //*/
            System.out.println("ENDS "+varIndex);
            System.out.println("=============================================");
            return;
        }
    }
    
}
