/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors;

import djbdd.*;

/**
 *
 * @author diegoj
 */
public abstract class ReductionAlgorithm {
    
    public TableT T;
    public VariableList VARIABLES;
    public static final boolean VERBOSE = false;
    
   
    public ReductionAlgorithm(){
        this.T = BDD.T;
        this.VARIABLES = BDD.variables();
    }
    
    public abstract void run();
    
}
