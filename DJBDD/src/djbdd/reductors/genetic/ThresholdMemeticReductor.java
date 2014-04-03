package djbdd.reductors.genetic;

import djbdd.core.*;
import djbdd.io.Printer;
import djbdd.reductors.ReductionAlgorithm;
import java.util.*;

/**
 * Genetic algorithm that reduces the size of the vertex graph using a genetic algorithm.
 * @author diegoj
 */
public class ThresholdMemeticReductor extends ReductionAlgorithm {
    
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
    
    protected int genI;
    
    protected final int THRESHOLD;
    
    public static final boolean VERBOSE = false;
    
    /**
     * Prints the population given as a parameter.
     * @param chromosomes Chromosomes that will be printed.
     */
    protected void printPopulation(ArrayList<Chromosome> chromosomes){
        for(Chromosome c : chromosomes){
            //c.print();
            System.out.println(c+" "+c.getGraphSize()+" ");
        }
    }
    
    /**
     * Constructor of the genetic algorithm.
     * @param populationSize Size of the population, that is, number of chromosomes.
     * @param generations Number of generations that will be executed.
     * @param selectionPercentage Percentage of selected chromosomes. 
     * @param mutationProbability Probability of mutation of each gene of each chromosome.
     */
    public ThresholdMemeticReductor(int populationSize, int generations, double selectionPercentage, double mutationProbability){
        super();
        this.numberOfGenerations = generations;
        this.populationSize = populationSize;
        this.selectionPercentage = selectionPercentage;
        this.mutationProbability = mutationProbability;
        this.comparator = new ChromosomeComparator();
        // We obtain the better size possible (gived by Ruddell's algorithm)
        // And use it as threshold
        BDD.reduce();
        this.THRESHOLD = T.size();
        Chromosome.THRESHOLD = this.THRESHOLD;
        if(VERBOSE){
            System.out.println("Our solution must be have at the most "+this.THRESHOLD+" nodes");
            BDD.variables().print();
            Printer.printTableT("T1");
        }
    }
    
    /**
     * Create the initial population.
     */
    protected void generatePopulation(){
        this.population = new ArrayList<Chromosome>(this.populationSize);
        
        // Initially, we add the best order to the population, this way
        // we can assure the best solution will be at least as good
        // as Ruddell's algorithm solution
        this.population.add(new Chromosome(BDD.variables()));
        for(int i=1; i<this.populationSize; i++){
            Chromosome cI = new Chromosome();
            this.population.add(cI);
        }
        
        // For debugging
        if(VERBOSE){
            System.out.println("WE GOT "+this.population.size()+" chromosomes");
            System.out.println("generatePopulation");
            this.printPopulation(this.population);
        }
    }
    
    /**
     * Select the parents of the population that will be crossed to create children.
     * @return Chromosome selected of the population that will be parents of the spawn.
     */    
    protected ArrayList<Chromosome> select(){
        double dSelectionSize = this.populationSize*this.selectionPercentage;
        int selectionSize = (int)Math.floor(dSelectionSize);
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
        
        // Later, we spawn some generations
        genI = 0;
        while (genI < this.numberOfGenerations) {
            if(VERBOSE){
                System.out.println("=============================================");
                System.out.println("=============================================");
                System.out.println("Generation "+(genI+1));
            }
            // Select the best parents of the population
            ArrayList<Chromosome> parents = this.select();

            if(VERBOSE){
                Collections.sort(parents, comparator);
                System.out.println("Parents");
                for(Chromosome c : parents){
                    System.out.println(c+": "+c.getGraphSize()+" ");
                }
            }

            if(VERBOSE){
                System.out.println("Children");
            }
            // This parents will spawn some children
            ArrayList<Chromosome> spawns = new ArrayList<Chromosome>(parents.size() / 2);
            for (int i = 0; i < parents.size(); i += 2) {
                Chromosome parent1 = parents.get(i);
                Chromosome parent2 = parents.get(i + 1);
                Chromosome spawn = parent1.cross(parent2);
                if(VERBOSE){
                    spawns.add(spawn);
                    System.out.println(spawn+": "+spawn.getGraphSize()+" ");
                }
            }

            if(VERBOSE){
                System.out.println("Mutation");
            }
            // The children are mutated
            for (int i = 0; i < spawns.size(); i++) {
                if(VERBOSE){
                    System.out.println(spawns.get(i)+": "+spawns.get(i).getGraphSize()+" ");
                }
                spawns.get(i).mutate(this.mutationProbability);
                if(VERBOSE){
                    System.out.println(spawns.get(i)+": "+spawns.get(i).getGraphSize()+" ");
                }
            }

            if(VERBOSE){
                
                Collections.sort(this.population, comparator);
                
                System.out.println("Pop before merge");
                for(Chromosome c : this.population){
                    System.out.println(c+": "+c.getGraphSize()+" ");
                }
            }
            
            
            // The population increases with the new children
            this.population.addAll(spawns);

            // We order the chromosomes in generated graph size ascendent order
            Collections.sort(this.population, comparator);
            
            if(VERBOSE){
                System.out.println("Population after merge");
                for(Chromosome c : this.population){
                    System.out.println(c+": "+c.getGraphSize()+" ");
                }
            }
            
            // We get the best elements of the population
            this.population = new ArrayList<Chromosome>(this.population.subList(0, this.populationSize));
            genI++;
            
            Chromosome bestSolution = this.population.get(0);
            if(VERBOSE){
                System.out.println("Best solution: size="+bestSolution.getGraphSize()+"; order="+bestSolution+" ");
            }
        }
        
        //printPopulation(this.population);
        Collections.sort(this.population, comparator);
        
        Chromosome bestSolution = this.population.get(0);
        if(VERBOSE){
            System.out.println("BEST SOLUTION");
            bestSolution.print();
        }
        
        // Deactivate the threshold restriction to apply the final order
        // to get the best solution
        Chromosome.THRESHOLD = 0;
        bestSolution.applyOrderToGraph();
        BDD.gc();
        if(VERBOSE){
            System.out.println("Size "+T.size());
            T.print();
            Printer.printTableT("T2");
        }
    }
    
}
