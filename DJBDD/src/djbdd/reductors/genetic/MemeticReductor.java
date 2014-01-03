package djbdd.reductors.genetic;

import djbdd.io.Printer;
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
        T.gc();
        ArrayList<Chromosome> selectedChromosomes = super.select();
        int selectedSize = selectedChromosomes.size();
        //return selectedChromosomes;
        
        //Collections.sort(this.population, this.comparator);
        //selectedChromosomes.add(this.population.get(0));
        //selectedChromosomes.add(this.population.get(1));
        int optimizations = 1;
        for(int i=0; i<selectedChromosomes.size(); i++){
            Chromosome selected = selectedChromosomes.get(i);
            int beforeSifting = selected.getGraphSize();
            int afterSifting = 0;
            int optimizationIteration = 0;
            do{
            //Printer.printTableT(genI+"_0");
                selected.optimize();
                afterSifting = selected.getGraphSize();
                optimizationIteration++;
                T.gc();
            //Printer.printTableT(genI+"_1");
            }while(optimizationIteration < optimizations);
        }
        
        
        return selectedChromosomes;
        /*
        Collections.sort(selectedChromosomes, this.comparator);
        
        return new ArrayList<Chromosome>(selectedChromosomes.subList(0, selectedSize));
         * 
         */
    }
    
}
