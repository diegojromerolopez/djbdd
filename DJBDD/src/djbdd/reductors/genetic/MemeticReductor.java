/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors.genetic;

import java.util.*;

/**
 * Reduce the BDD using an hybrid evolutionario approach.
 * @author diegoj
 */
public class MemeticReductor extends GeneticReductor {
    
    /**
     * Constructor of the genetic algorithm.
     * @param populationSize Size of the population, that is, number of chromosomes.
     * @param generations Number of generations that will be executed.
     * @param selectionPercentage Percentage of selected chromosomes. 
     * @param mutationProbability Probability of mutation of each gene of each chromosome.
     */
    public MemeticReductor(int populationSize, int generations, double selectionPercentage, double mutationProbability){
        super(populationSize, generations, selectionPercentage, mutationProbability);
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
        int selectedSize = selectedChromosomes.size();
        
        Collections.sort(this.population, this.comparator);
        selectedChromosomes.add(this.population.get(0));
        selectedChromosomes.add(this.population.get(1));
        
        for(int i=0; i<selectedChromosomes.size(); i++){
            Chromosome selected = selectedChromosomes.get(i);
            selected.optimizeBySifting();
        }
        
        Collections.sort(selectedChromosomes, this.comparator);
        
        return new ArrayList<Chromosome>(selectedChromosomes.subList(0, selectedSize));
    }
    
}
