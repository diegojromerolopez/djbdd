package djbdd.reductors;

import djbdd.core.BDD;
import djbdd.core.TableT;
import djbdd.core.VariableList;

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
    
    /** Should the process be verbose and prints a debug trace in the stdout? */
    public static final boolean VERBOSE = false;
    
    /** Window permutationalgorightm */
    public static final int WINDOW_PERMUTATION = 0;
    
    /** Window permutationalgorightm */
    public static final int WINDOW_PERMUTATION_ALGORITHM = 0;
    
    /** Rudell's sifting algorightm */
    public static final int SIFTING = 1;
    
    /** Rudell's sifting algorightm */
    public static final int SIFTING_ALGORIGHTM = 1;

    /** Total search algorightm */
    public static final int TOTAL_SEARCH = 2;
    
    /** Total search algorightm */
    public static final int TOTAL_SEARCH_ALGORIGHTM = 2;

    /** Genetic algorightm */
    public static final int GENETIC_ALGORITHM = 3;

    /** Random swp algorightm */
    public static final int RANDOM_SWAP = 4;

    /** Random swap algorightm */
    public static final int RANDOM_SWAP_ALGORITHM = 4;
    
    /**
     * Constructor of the reduction algorigthm.
     */
    public ReductionAlgorithm(){
        this.T = BDD.T;
        this.VARIABLES = BDD.variables();
    }
    
    public abstract void run();
    
}
