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
    
    /** Percentage of population that will be selected in each iteration of the algorithm */
    private double selectionPercentage;
    
    ChromosomeComparator comparator;
    
    private void printPopulation(ArrayList<Chromosome> chromosomes){
        for(Chromosome c : chromosomes){
            c.print();
        }
    }
    
    public GeneticReductor(int populationSize, int generations, double selectionPercentage, double mutationProbability){
        super();
        this.numberOfGenerations = generations;
        this.populationSize = populationSize;
        this.selectionPercentage = mutationProbability;
        this.mutationProbability = mutationProbability;
        this.comparator = new ChromosomeComparator();
        
    }
    
    protected void generatePopulation(){
        this.population = new ArrayList<Chromosome>(this.populationSize);
        for(int i=0; i<this.populationSize; i++){
            this.population.add(new Chromosome());
        }
        if(VERBOSE){
            System.out.println("WE GOT "+this.population.size()+" chromosomes");
        }
    }
    
    protected ArrayList<Chromosome> select(){
        Collections.sort(this.population, this.comparator);
        double dSelectionSize = this.populationSize*this.selectionPercentage;
        int selectionSize = (int)Math.round(dSelectionSize);
        if(selectionSize%2==1){
            if(selectionSize+1 < this.populationSize){
                selectionSize += 1;
            }
            else{
                selectionSize -= 1;
            }
        }
        Collections.shuffle(this.population);
        return new ArrayList<Chromosome>(this.population.subList(0, selectionSize));
    }
    
  
    @Override
    public void run(){
        // First we generate the population
        this.generatePopulation();

        printPopulation(this.population);
        
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
                
                //System.out.println("WE got "+spawns.size()+" spawns");
                
                for(int i=0; i<spawns.size(); i++){
                    spawns.get(i).mutate(this.mutationProbability);
                }
                
                //System.out.println("We mutate the "+spawns.size()+" spawns");
                
                this.population.addAll(spawns);
                Collections.sort(this.population, comparator);
                
                //System.out.println("We got a new population of "+this.population.size()+" chromosomes");
                
                this.population = new ArrayList<Chromosome>(this.population.subList(0, this.populationSize));
                genI++;
                
                printPopulation(this.population);
        }
        
        Collections.sort(this.population, comparator);
        
        Chromosome bestSolution = this.population.get(0);
        bestSolution.computeTreeSize();
        
    }
    
}
