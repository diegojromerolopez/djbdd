/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors;

import djbdd.core.*;
import java.util.*;

class Chromosome{

    VariableList solution;
    int size;
}

/**
 * Genetic algorithm that reduces the 
 * @author diegoj
 */
public class GeneticReductor extends ReductionAlgorithm {
    
    /** Chromosome population */
    private ArrayList<Chromosome> population;
    
    /** Number of generations */
    private int numberOfGenerations;
    
    
    public GeneticReductor(int generations){
        super();
        this.numberOfGenerations = generations;
    }
    
    protected void generatePopulation(){
        this.population = null;
    }
    
    protected ArrayList<Chromosome> select(){
        return null;
    }
    
    protected Chromosome cross(Chromosome solA, Chromosome solB){
        return null;
    }
    
    protected Chromosome mutate(Chromosome a){
        return null;
    }
    
    @Override
    public void run(){
        // First we generate the population
        this.generatePopulation();
        
        // Later, we spawn some generations
        int genI = 0;
        while(genI < this.numberOfGenerations){
        
                ArrayList<Chromosome> parents = this.select();
        
        }
        
    }
    
}
