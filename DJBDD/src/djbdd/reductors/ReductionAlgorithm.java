package djbdd.reductors;

import djbdd.*;

/**
 * Abstract class that representes the different reduction algorithms
 * implemented in our package.
 * @author diegoj
 */
public abstract class ReductionAlgorithm {
    
    /** Vertex table of the system (used for readibility) */
    protected TableT T;
    
    /** Variables of the system (used for readibility) */
    protected VariableList VARIABLES;
    
    /** */
    public static final boolean VERBOSE = false;
    
    /** Rudell's sifting algorightm */
    public static final int SIFTING = 1;
    
    /** Rudell's sifting algorightm */
    public static final int SIFTING_ALGORIGHTM = 1;
   
    public ReductionAlgorithm(){
        this.T = BDD.T;
        this.VARIABLES = BDD.variables();
    }
    
    public abstract void run();
    
}
