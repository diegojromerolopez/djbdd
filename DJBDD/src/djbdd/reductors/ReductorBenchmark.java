/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors;

import djbdd.reductors.totalsearch.TotalSearchReductor;
import djbdd.reductors.windowpermutation.WindowPermutationReductor;
import djbdd.reductors.sifting.SiftingReductor;
import djbdd.reductors.genetic.GeneticReductor;
import djbdd.reductors.random.*;
import djbdd.core.*;
import djbdd.reductors.io.*;
import java.io.*;
import java.util.*;
import djbdd.io.Printer;
import djbdd.timemeasurer.TimeMeasurer;

/**
 *
 * @author diegoj
 */
public class ReductorBenchmark {
    
    /** Algorithm that will be used to reduce BDD size */
    private ReductionAlgorithm algorithm;
    
    /** BDD to be reduced in size */
    private BDD bdd;
    
    /** Initial size of the BDD */
    private int initialBDDSize = 0;
    
    /** Size of the BDD after the reduction process */
    private int reducedBDDSize = 0;
    
    /** Should the benchmark be verbose? */
    public static final boolean VERBOSE = false;
    
    private static void assertParameter(HashMap<String,String> params, String parameter, String explanation){
        if(!params.containsKey(parameter)){
            System.err.println(explanation);
            System.exit(-1);
        }
    }
    
    /**
     * Gets the reduction algorithm from a string that identifies it.
     * @param algorithm Algorithm that will be executed.
     * @param params Parameters of each algorith.
     */
    private static ReductionAlgorithm initAlgorithm(String algorithm, HashMap<String,String> params){
        ReductionAlgorithm reductor = null;
        if(algorithm.equals("total_search")){
            reductor = new TotalSearchReductor();
        }
        else if(algorithm.equals("sifting")){
            reductor = new SiftingReductor();
        }
        else if(algorithm.equals("sifting_sameorder")){
            reductor = new SiftingReductor(SiftingReductor.VARIABLES_WITH_SAME_ORDER);
        }
        else if(algorithm.equals("window_permutation")){
            assertParameter(params, "window_size", "Size of the window in the algorithm. Suggested values are 2, 3 or 4.");
            int window_size = Integer.parseInt(params.get("window_size"));
            reductor = new WindowPermutationReductor(window_size);
        }
        else if(algorithm.equals("genetic")){
            // Semilla aleatoria
            assertParameter(params, "random_seed", "Random seed.");
            int randomSeed = Integer.parseInt(params.get("random_seed"));
            random.Random.init(randomSeed);
            // Population size
            assertParameter(params, "population", "Number of chromosomes in the genetic algorithm.");
            int populationSize = Integer.parseInt(params.get("population"));
            // Generations (iterations of the algorithm)
            assertParameter(params, "generations", "Number of iterations of the genetic algorithm.");
            int generations = Integer.parseInt(params.get("generations"));
            // Selection percentage
            assertParameter(params, "selection_percentage", "Percentage of chromosomes selected in the algorithm.");
            double selectionPercentage = Double.parseDouble(params.get("selection_percentage"));
            if(selectionPercentage > 1)
                selectionPercentage = selectionPercentage / 100;
            // Mutation probability
            assertParameter(params, "mutation_probability", "Probability of mutation in each gene of each chromosome.");
            double mutationProbability = Double.parseDouble(params.get("mutation_probability"));
            if(mutationProbability > 1)
            {
                System.err.println("This value must be in the interval [0, 1]");
                System.exit(-1);
            }
            reductor = new GeneticReductor(populationSize, generations, selectionPercentage, mutationProbability);
        }
        else if(algorithm.equals("random_swapper")){
            // Semilla aleatoria
            assertParameter(params, "random_seed", "Random seed.");
            int randomSeed = Integer.parseInt(params.get("random_seed"));
            random.Random.init(randomSeed);
            // Iterations
            assertParameter(params, "iterations", "Iterations of the random swapper algorithm.");
            int iterations = Integer.parseInt(params.get("iterations"));
            reductor = new RandomSwapperReductor(iterations);
        }
        else{
            System.err.println("Algorithm not recognized");
            System.exit(-1);
        }
        return reductor;
    }
    
   
    public ReductorBenchmark(String algorithm, HashMap<String,String> params, String format, String file){
        // Read the BDD
        BDDReader reader = new BDDReader(format, file);
        this.bdd = reader.read();
        if(VERBOSE){
            this.bdd.print(true);
        }
        // Apply the algorithm
        this.initialBDDSize = this.bdd.size();
        //BDD.variables().print();
        //System.out.println(BDD.T.gc());
        this.algorithm = ReductorBenchmark.initAlgorithm(algorithm, params);
    }
    
    /**
     * Runs the optimization process.
     */
    public long run(){
        long elapsedTime = this.algorithm.run();
        //BDD.variables().print();
        //System.out.println(BDD.T.gc());
        this.reducedBDDSize = this.bdd.size();
        //Printer.printTableT("T");
        //Printer.printBDD(bdd, "BDD");
        return elapsedTime;
    }
    
    public int getInitialBDDSize(){
        return this.initialBDDSize;
    }
    
    public int getReducedBDDSize(){
        return this.reducedBDDSize;
    }
    
    private static final String SEP = "\t";
    
    private static void makeFileBenchmark(String algorithm, HashMap<String,String> params, String format, File file){
        String filepath = file.getAbsolutePath();
        String filename = file.getName();
        ReductorBenchmark reductor = new ReductorBenchmark(algorithm, params, format, filepath);
        long elapsedTime = reductor.run();
        System.out.println(filename + SEP + BDD.variables().size() + SEP + algorithm + SEP + reductor.getInitialBDDSize() + SEP + reductor.getReducedBDDSize() + SEP + BDD.T.getSwapCounter()+SEP+elapsedTime+SEP+""+TimeMeasurer.elapsedTimeAsHumanText(elapsedTime)+"");
    }
    
    public static void makeBenchmark(String algorithm, HashMap<String,String> params, String format, String resourceName){
        System.out.println("File"+SEP+"Nvars"+SEP+"Alg."+SEP+"init_size"+SEP+"reduc_size"+SEP+"Nswaps"+SEP+"eTime"+SEP+"eTimeHuman");
        File f = new File(resourceName);
        if (f.isFile()) {
            ReductorBenchmark.makeFileBenchmark(algorithm, params, format, f);
        } else if (f.isDirectory()) {
            File[] listOfFiles = f.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];
                if (file.isFile()) {
                    try{
                        ReductorBenchmark.makeFileBenchmark(algorithm, params, format, file);
                    }catch(java.lang.OutOfMemoryError e){
                        String filename = file.getName();
                        System.out.println(filename + SEP + BDD.variables().size() + SEP + algorithm + SEP + "-" + SEP + "-" + SEP + BDD.T.getSwapCounter()+SEP+"-"+SEP+"-");
                    }
                }
            }
        }
        else{
            System.err.println(resourceName + " is not a file or directory");
        }
    
    }
}
