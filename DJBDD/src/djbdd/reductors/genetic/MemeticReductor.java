package djbdd.reductors.genetic;

import djbdd.io.Printer;
import java.util.*;

/**
 * Reduce the BDD using an hybrid evolutionario approach.
 * @author diegoj
 */
public class MemeticReductor extends GeneticReductor {
    
    /** Defines the number of optimizations suffered for the chromosomes in the selection process. */
    private int optimizationIterations;
    
    /**
     * Constructor of the genetic algorithm.
     * @param populationSize Size of the population, that is, number of chromosomes.
     * @param generations Number of generations that will be executed.
     * @param selectionPercentage Percentage of selected chromosomes. 
     * @param mutationProbability Probability of mutation of each gene of each chromosome.
     */
    public MemeticReductor(int populationSize, int generations, double selectionPercentage, double mutationProbability){
        super(populationSize, generations, selectionPercentage, mutationProbability);
        this.optimizationIterations = 1;
    }
    
   /**
     * Constructor of the genetic algorithm.
     * @param populationSize Size of the population, that is, number of chromosomes.
     * @param generations Number of generations that will be executed.
     * @param selectionPercentage Percentage of selected chromosomes. 
     * @param mutationProbability Probability of mutation of each gene of each chromosome.
     * @param optimizationIterations Number of times that chromosomes will be optimized.
     */
    public MemeticReductor(int populationSize, int generations, double selectionPercentage, double mutationProbability, int optimizationIterations){
        super(populationSize, generations, selectionPercentage, mutationProbability);
        this.optimizationIterations = optimizationIterations;
    }
    
    /**
     * Select operation.
     * Add a process of optimization to each chromosome selected by
     * the Genetic Algorithm standard selection process.
     * @return List with the selected chromosomes.
     */
    @Override
    protected ArrayList<Chromosome> select(){
        ArrayList<Chromosome> selectedChromosomes = super.select();
        //int selectedSize = selectedChromosomes.size();
                
        for(int i=0; i<selectedChromosomes.size(); i++){
            Chromosome selected = selectedChromosomes.get(i);
            //int beforeSifting = selected.getGraphSize();
            //int afterSifting = 0;
            int optimizationIteration = 0;
            do{
                selected.optimize();
                //afterSifting = selected.getGraphSize();
                optimizationIteration++;
            }while(optimizationIteration < this.optimizationIterations);
        }
        return selectedChromosomes;
    }
    
}
