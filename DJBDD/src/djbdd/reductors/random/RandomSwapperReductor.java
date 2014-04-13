package djbdd.reductors.random;

//import java.util.*;
import djbdd.reductors.ReductionAlgorithm;
import djbdd.core.*;
//import djbdd.io.Printer;

/**
 * Executes a number of random swaps and gets the best solution.
 * @author diegoj
 */
public class RandomSwapperReductor extends ReductionAlgorithm {
    
    /** Iterations of the algorithm */
    private int iterations;
    
    /** Best solution at the moment */
    private VariableList bestSolution;
    
    /** Graph size of the best solution at the moment */
    private int bestSolutionGraphSize;
    
    /**
     * Constructor of the reduction method.
     * @param iterations Number of iterations that the algorithm will be executed.
     */
    public RandomSwapperReductor(int iterations){
        super();
        this.iterations = iterations;
    }

    /**
     * Executes the optimization algorithm.
     */
    @Override
    public void execute(){
        // The best solution is the current solution
        this.bestSolution = new VariableList(BDD.variables());
        
        // The best solution BDD size is the current BDD size
        this.bestSolutionGraphSize = T.size();
       
        // We get a random variable each iteration
        int numVariables = this.bestSolution.size();
        int lastVariableIndex = numVariables-1;

        // For each iteration, we get a variable and swap it
        // with the next variable in the BDD graph
        int i=0;
        while(i<this.iterations)
        {
            // Get a random variable [0, n-1]
            int varIndex = random.Random.randInt(0, lastVariableIndex);
            // Execute the swap and calculate the size
            T.swapVariable(varIndex);
            int newSize = T.size();
            // If the swap gets a smaller BDD, stores the order and its size
            if(newSize < this.bestSolutionGraphSize){
                this.bestSolution = new VariableList(BDD.variables());
                this.bestSolutionGraphSize = newSize;
            }
            i++;
        }
        // Apply the best order obtained to the graph
        this.bestSolution.applyOrderToGraph();
    }
    
}
