package djbdd.reductors.iterativesifting;

import random.Random;
import djbdd.core.*;
import djbdd.io.*;
import djbdd.reductors.sifting.SiftingReductor;
import djbdd.reductors.ReductionAlgorithm;

/*
 * This method takes some elements of evolution algorithms, like the chromosome,
 * that is used here as a container of a solution (that is, a variable order).
 */
import djbdd.reductors.genetic.Chromosome;

import java.util.*;


/**
 * Shifting method based algorithm that iterates getting in each step a variable
 * to move it to its best position.
 * What differs from the sifting method proposed by Ruddell is these features.
 * First, we iterates a predefined number of iterations.
 * Second, in each iteration we will be pick the variables to move to its best position in a given order.
 * This order will be gotten from one of the following methods:
 * The variables can be ordered in a descending orderer according to its number of vertices,
 * or randomly. The variable list order is determined by a reinitialization probability in each iteration.
 * @author Diego J. Romero López
 */
public class IterativeSiftingReductor extends SiftingReductor {
    
    /** Iterations of the proccess */
    protected final int iterations;
    

    /**
     * This algorithm needs a number of iterations and a reinitalization probability.
     * @param iterations Times that a variable will be sifted to its best position.
     */
    public IterativeSiftingReductor(int iterations){
        super();
        this.iterations = iterations;
    }

    
    /**
     * Executes the reduction method.
     */
    @Override
    public void execute() {
        // First, The best order is the initiall order
        Chromosome bestOrder = new Chromosome(BDD.variables());
        // and the best size is the initiall size
        int bestSize = BDD.T.size();
        
        // Iterations counter
        int i = 0;
        
        boolean thereIsAnImprovement = false;
        do{
            i = 0;
            thereIsAnImprovement = false;
            // Iterations of the algorithm
            while(i< iterations){
                // We order the variables each iteration
                this.initVariableOrderDesc();

                // For each variable, we obtain its best position and updates
                // the best solution if needed
                for (int varIndex : this.variableOrder) {
                    // Debug prints
                    if(VERBOSE){
                        System.out.println("Start searching for best position for variable "+varIndex);
                        this.VARIABLES.print();
                    }
                    this.findBestPositionForVariable(varIndex);
                    // Current size obtained for this sifting
                    int currentOrderSize = BDD.T.size();
                    // If our best size is worse that the size obtained by the sifting
                    if(bestSize > currentOrderSize){
                        bestOrder =  new Chromosome(BDD.variables());
                        bestSize = currentOrderSize;
                        thereIsAnImprovement = true;
                    }
                }
                i++;
            }
        }while(thereIsAnImprovement);
        
        // The best order is applied to the graph to finally obtain
        // the reduced BDD tree
        bestOrder.applyOrderToGraph();
    }
    
}
