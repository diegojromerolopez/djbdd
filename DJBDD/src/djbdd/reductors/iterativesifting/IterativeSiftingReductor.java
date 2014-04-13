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
    
    /** Probability that will have the randomization of the variable order process each iteration */
    protected final double reinitializationProbability;
    
    /**
     * This algorithm needs a number of iterations and a reinitalization probability.
     * @param iterations Times that a variable will be sifted to its best position.
     * @param reinitializationProbability Probability of chosing a random variable for this iteration.
     */
    public IterativeSiftingReductor(int iterations, double reinitializationProbability){
        super();
        this.iterations = iterations;
        if(reinitializationProbability > 1.0)
            reinitializationProbability = reinitializationProbability / 10.0;
        this.reinitializationProbability = reinitializationProbability;
    }

    /**
     * This algorithm needs a number of iterations and a reinitalization probability.
     * This constructor call will asume that will be no reinitialization proccess.
     * @param iterations Times that a variable will be sifted to its best position.
     */
    public IterativeSiftingReductor(int iterations){
        this(iterations, -1.0);
        // This random seed is not used because
        // the variable list reinitialization probability is -1,
        // so it is set to a constant value (0, for example).
        random.Random.init(0);
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
            thereIsAnImprovement = false;
            // Iterations of the algorithm
            while(i< iterations){
                // What will be the variable list generation order method?

                // If the prob_i is less than the reinitialization probability,
                // the order will be a random one. 
                if(random.Random.rand() <= this.reinitializationProbability){
                    Chromosome c = new Chromosome();
                    c.applyOrderToGraph();
                }
                // Otherwise, will be the variables descendenly ordered according
                // to its number of vertices
                else
                {
                    this.initVariableOrderDesc();
                }

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
