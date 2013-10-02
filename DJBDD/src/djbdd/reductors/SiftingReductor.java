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
            if(VERBOSE){
                System.out.println("=============================================");
                System.out.println("STARTS "+varIndex);
            }
            String var = this.VARIABLES.get(varIndex);
            int varIndexBestPosition = this.VARIABLES.getPositionOfVariable(varIndex);
            int varIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
            int newVarIndexPosition = varIndexPosition;
            boolean swapWasMade = false;
            if(VERBOSE){
                Printer.printTableT("test15_"+this.VARIABLES.toString());
            }
            int i=1;
            do{
                if(VERBOSE){
                    System.out.println("---------------------------------------------");
                    System.out.println("---------------------------------------------");
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println("Before swap "+i+": variable  "+varIndex+" ("+var+") is in position "+varIndexPosition);
                    System.out.flush();
                    //this.VARIABLES.print();
                }
                
                swapWasMade = this.T.swap(varIndexPosition);
                
                if(VERBOSE){
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
                newVarIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
                if(VERBOSE){
                    if(swapWasMade){
                        System.out.println("After swap "+i+": variable "+varIndex+" ("+var+") is in position "+newVarIndexPosition);
                    }else{
                        System.out.println("NO swap "+i+": variable "+varIndex+" ("+var+") is in position "+newVarIndexPosition);
                    }
                    this.VARIABLES.print();
                    Printer.printTableT("test15_swap_"+i+"_swapped_var_at_"+varIndexPosition+"_("+var+")_"+this.VARIABLES.toString());
                }

                int Tsize = this.T.gc();
                if(VERBOSE){
                    System.out.println(this.VARIABLES.getOrderedVariables()+" "+size);
                }
                if(size > Tsize){
                    size = Tsize;
                    varIndexBestPosition = varIndexPosition+1;
                    if(VERBOSE){
                        System.out.println(Tsize+" IS THE FUCKIN BEST WITH "+varIndex+" ("+var+") at "+varIndexBestPosition);
                    }
                }
                if(swapWasMade)
                    varIndexPosition++;
                i++;

            }while(swapWasMade && varIndexPosition < lastVariablePosition);
            
            // We go back
            do{
                if(VERBOSE){
                    System.out.println("Back!!! "+varIndex+" is in position "+varIndexPosition);
                }
                swapWasMade = this.T.swapBack(varIndexPosition);
                int Tsize = this.T.gc();
                
                if(VERBOSE){
                    System.out.println(this.VARIABLES.getOrderedVariables()+" "+size);
                }
                
                if(size > Tsize){
                    size = Tsize;
                    varIndexBestPosition = varIndexPosition+1;
                    if(VERBOSE){
                        System.out.println(Tsize+" IS THE FUCKIN BEST WITH "+varIndex+" ("+var+") at "+varIndexBestPosition);
                    }
                }
                if(swapWasMade)
                    varIndexPosition--;
            }while(swapWasMade);
            
            if(VERBOSE){
                System.out.println("The best position is "+varIndexBestPosition);
            }
            
            // Move to the best position
            varIndexPosition = this.VARIABLES.getPositionOfVariable(varIndex);
            swapWasMade = true;
            while(swapWasMade && varIndexPosition < varIndexBestPosition){
                swapWasMade = this.T.swap(varIndexPosition);
                if(swapWasMade)
                    varIndexPosition++;
            }
              
            //*/
            if(VERBOSE){
                System.out.println("ENDS "+varIndex);
                System.out.println("=============================================");
            }
            return;
        }
    }
    
}
