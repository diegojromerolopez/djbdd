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
    protected ArrayList<Chromosome> population;
    
    /** Size of the population */
    protected int populationSize;
    
    /** Number of generations */
    protected int numberOfGenerations;
    
    /** Probability of having a position altered in the process */
    protected double mutationProbability;
    
    /** Percentage of population that will be selected in each iteration of the algorithm */
    protected double selectionPercentage;
    
    /** Comparator between chromosomes */
    protected ChromosomeComparator comparator;
    
    /**
     * Prints the population given as a parameter.
     * @param chromosomes Chromosomes that will be printed.
     */
    protected void printPopulation(ArrayList<Chromosome> chromosomes){
        for(Chromosome c : chromosomes){
            c.print();
        }
    }
    
    /**
     * Constructor of the genetic algorithm.
     * @param populationSize Size of the population, that is, number of chromosomes.
     * @param generations Number of generations that will be executed.
     * @param selectionPercentage Percentage of selected chromosomes. 
     * @param mutationProbability Probability of mutation of each gene of each chromosome.
     */
    public GeneticReductor(int populationSize, int generations, double selectionPercentage, double mutationProbability){
        super();
        this.numberOfGenerations = generations;
        this.populationSize = populationSize;
        this.selectionPercentage = selectionPercentage;
        this.mutationProbability = mutationProbability;
        this.comparator = new ChromosomeComparator();
        
    }
    
    /**
     * Create the initial population.
     */
    protected void generatePopulation(){
        this.population = new ArrayList<Chromosome>(this.populationSize);
        this.population.add(new Chromosome(BDD.variables()));
        for(int i=1; i<this.populationSize; i++){
            this.population.add(new Chromosome());
        }
        if(VERBOSE){
            System.out.println("WE GOT "+this.population.size()+" chromosomes");
        }
    }
    
    /**
     * Select the parents of the population that will be crossed to create children.
     * @return Chromosome selected of the population that will be parents of the spawn.
     */    
    protected ArrayList<Chromosome> select(){
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
        Collections.shuffle(this.population, random.Random.getRandom());
        ArrayList<Chromosome> parents = new ArrayList<Chromosome>(this.population.subList(0, selectionSize));
        return parents;
    }
    
    /**
     * Executes the genetic algorithm.
     */
    @Override
    public void execute(){
        // First we generate the population
        this.generatePopulation();

        //printPopulation(this.population);
        
        // Later, we spawn some generations
        int genI = 0;
        while (genI < this.numberOfGenerations) {

            // Select the best parents of the population
            ArrayList<Chromosome> parents = this.select();

            // This parents will spawn some children
            ArrayList<Chromosome> spawns = new ArrayList<Chromosome>(parents.size() / 2);
            for (int i = 0; i < parents.size(); i += 2) {
                Chromosome parent1 = parents.get(i);
                Chromosome parent2 = parents.get(i + 1);
                spawns.add(parent1.cross(parent2));
            }

            // The children are mutated
            for (int i = 0; i < spawns.size(); i++) {
                spawns.get(i).mutate(this.mutationProbability);
            }

            // The population increases with the new children
            this.population.addAll(spawns);

            // We order the chromosomes in generated graph size ascendent order
            Collections.sort(this.population, comparator);

            // We get the best elements of the population
            this.population = new ArrayList<Chromosome>(this.population.subList(0, this.populationSize));
            genI++;

            //printPopulation(this.population);
        }
        
        //printPopulation(this.population);
        Collections.sort(this.population, comparator);
        
        Chromosome bestSolution = this.population.get(0);
        //System.out.println("BEST SOLUTION");
        //bestSolution.print();
        bestSolution.applyOrderToGraph();
        
    }
    
}
