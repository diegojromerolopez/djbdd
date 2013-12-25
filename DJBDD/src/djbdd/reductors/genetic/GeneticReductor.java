/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors.genetic;

import djbdd.core.*;
import djbdd.reductors.ReductionAlgorithm;
import java.util.*;

/**
 * Genetic algorithm that reduces the 
 * @author diegoj
 */
public class GeneticReductor extends ReductionAlgorithm {
    
    /** Chromosome population */
    private ArrayList<Chromosome> population;
    
    /** Size of the population */
    private int populationSize;
    
    /** Number of generations */
    private int numberOfGenerations;
    
    /** Probability of having a position altered in the process */
    private double mutationProbability;
    
    
    public GeneticReductor(int populationSize, int generations, double mutationProbability){
        super();
        this.numberOfGenerations = generations;
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        
    }
    
    protected void generatePopulation(){
        this.population = null;
    }
    
    protected ArrayList<Chromosome> select(){
        return null;
    }
    
  
    @Override
    public void run(){
        // First we generate the population
        this.generatePopulation();
        
        ChromosomeComparator comparator = new ChromosomeComparator();
        
        // Later, we spawn some generations
        int genI = 0;
        while(genI < this.numberOfGenerations){
        
                ArrayList<Chromosome> parents = this.select();
                ArrayList<Chromosome> spawns = new ArrayList<Chromosome>(parents.size()/2);
                for(int i=0; i<parents.size(); i+=2){
                    Chromosome parent1 = parents.get(i);
                    Chromosome parent2 = parents.get(i+1);
                    spawns.add(parent1.cross(parent2));
                }
                
                for(int i=0; i<spawns.size(); i++){
                    spawns.get(i).mutate(this.mutationProbability);
                }
                
                ArrayList<Chromosome> newPopulation = new ArrayList<Chromosome>(spawns.size()+parents.size());
                newPopulation.addAll(spawns);
                newPopulation.addAll(parents);
                Collections.sort(newPopulation, comparator);
                
                this.population = (ArrayList<Chromosome>)newPopulation.subList(0, this.populationSize);
                genI++;
        }
        
        Collections.sort(this.population, comparator);
        
        Chromosome bestSolution = this.population.get(0);
        bestSolution.computeTreeSize();
        
    }
    
}
