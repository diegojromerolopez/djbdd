/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors.genetic;

/**
 *
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
    
}
