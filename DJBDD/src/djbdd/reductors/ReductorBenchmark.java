package djbdd.reductors;

import djbdd.reductors.totalsearch.TotalSearchReductor;
import djbdd.reductors.windowpermutation.WindowPermutationReductor;
import djbdd.reductors.sifting.SiftingReductor;
import djbdd.reductors.iterativesifting.IterativeSiftingReductor;
import djbdd.reductors.genetic.GeneticReductor;
import djbdd.reductors.genetic.MemeticReductor;
import djbdd.reductors.genetic.ThresholdMemeticReductor;
import djbdd.reductors.random.*;
import djbdd.core.*;
import djbdd.io.Printer;
import djbdd.reductors.io.*;
import java.io.*;
import java.util.*;
import djbdd.timemeasurer.TimeMeasurer;

/**
 * Executes BDD size reduction benchmarks.
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
    
    /**
     * Assert parameter existence.
     * @param params Parameters of the algorithm.
     * @param parameter Parameter that we want to test for existence in params.
     * @param explanation Human text that will be shown in stderror as help if the parameter is not present.
     */
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
        else if(algorithm.equals("sifting_randomorder")){
            // Random seed
            assertParameter(params, "random_seed", "Random seed.");
            int randomSeed = Integer.parseInt(params.get("random_seed"));
            random.Random.init(randomSeed);
            reductor = new SiftingReductor(SiftingReductor.VARIABLES_WITH_RANDOM_ORDER);
        }
        else if(algorithm.equals("window_permutation")){
            assertParameter(params, "window_size", "Size of the window in the algorithm. Suggested values are 2, 3 or 4.");
            int window_size = Integer.parseInt(params.get("window_size"));
            reductor = new WindowPermutationReductor(window_size);
        }
        else if(algorithm.equals("genetic") || algorithm.equals("memetic") || algorithm.equals("tmemetic")){
            // Random seed
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
            if(algorithm.equals("genetic")){
                reductor = new GeneticReductor(populationSize, generations, selectionPercentage, mutationProbability);
            }
            else if(algorithm.equals("memetic")){
                reductor = new MemeticReductor(populationSize, generations, selectionPercentage, mutationProbability);
            }
            else if(algorithm.equals("tmemetic")){
                reductor = new ThresholdMemeticReductor(populationSize, generations, selectionPercentage, mutationProbability);
            }
        }
        else if(algorithm.equals("random_swapper")){
            // Random seed
            assertParameter(params, "random_seed", "Random seed is needed in this algorithm.");
            int randomSeed = Integer.parseInt(params.get("random_seed"));
            random.Random.init(randomSeed);
            // Iterations
            assertParameter(params, "iterations", "Iterations of the random swapper algorithm.");
            int iterations = Integer.parseInt(params.get("iterations"));
            reductor = new RandomSwapperReductor(iterations);
        }
        else if(algorithm.equals("isifting_without_reinitalization")){
            // Random seed (this parameter will not be used)
            int randomSeed = 10;
            random.Random.init(randomSeed);
            // Iterations
            assertParameter(params, "iterations", "Iterations of the algorithm.");
            int iterations = Integer.parseInt(params.get("iterations"));
            // Reductor creation
            reductor = new IterativeSiftingReductor(iterations);
        }
        else if(algorithm.equals("isifting")){
            // Random seed
            assertParameter(params, "random_seed", "Random seed is needed in this algorithm.");
            int randomSeed = Integer.parseInt(params.get("random_seed"));
            random.Random.init(randomSeed);
            // Iterations
            assertParameter(params, "iterations", "Iterations of the algorithm.");
            int iterations = Integer.parseInt(params.get("iterations"));
            // Probability of reinitilizing the order
            assertParameter(params, "reinit_probability", "Probability of the reinitialization of the variable order during the proccess.");
            double reinitProb = Double.parseDouble(params.get("reinit_probability"));
            // Reductor creation
            reductor = new IterativeSiftingReductor(iterations, reinitProb);
        }
        else{
            System.err.println("Algorithm not recognized");
            System.exit(-1);
        }
        return reductor;
    }
    
    /**
     * Constructor of the benchmark.
     * @param algorithm Name of the algorithm chosen.
     * @param params Extra parameters of that algorithm.
     * @param format File format used to embed the BDD.
     * @param file File path that contains a BDD in the specified format.
     */
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
     * @return Number of elapsed nanoseconds.
     */
    public long run(){
        long elapsedTime = 0;
        
        BDD.gc();
        if(VERBOSE){
            System.out.println("Initial size:"+BDD.T.size());
            BDD.T.print();
        }
        
        BDD.T.updateNumParents();
        elapsedTime = this.algorithm.run();
        
        this.reducedBDDSize = this.bdd.size();
        //System.out.println("BDD ("+this.reducedBDDSize+")");
        //this.bdd.print(true);
        //Printer.printTableT("sifting_"+BDD.T.size());

        BDD.gc();
        if(VERBOSE){
            System.out.println("Reduced size:"+BDD.T.size());
            BDD.T.print();
        }
        
        //Printer.printTableT("T");
        //Printer.printBDD(bdd, "BDD");
        return elapsedTime;
    }
    
    /**
     * Returns the original BDD size.
     * @return Number of vertices of the BDD before the reduction process.
     */
    public int getInitialBDDSize(){
        return this.initialBDDSize;
    }
    
    /**
     * Returns the reduced BDD size.
     * @return Number of vertices of the BDD after the reduction process.
     */
    public int getReducedBDDSize(){
        return this.reducedBDDSize;
    }
    
    /** Separator of shown results */
    private static final String SEP = "\t";
    
    //private static String[] RESULT_FIELDS = {"File","Nvars","InitSize","ReducSize","Nswaps","Time (ns)","Time (s)","Time (h:m:s)"};
    
    /** Data that will be show to the user */
    private static String[] RESULT_FIELDS = {"File","Nvars","InitSize","ReducSize","Nswaps", "Time (s)"};
    
    /**
     * Prints the header for a processed file.
     * @param algorithm Name of the used algorithm.
     */
    private static void printFileHeader(String algorithm){
        StringBuilder out = new StringBuilder("Benchmark using "+algorithm+" algorithm\n");
        for(String fieldName : RESULT_FIELDS){
            out.append(fieldName).append(SEP);
        }
        System.out.println(out.toString().trim());
    }
    
    /**
     * Prints the results for a processed file.
     * @param filename Name of the processed file.
     * @param reductor Reduction algorithm used.
     * @param elapsedTime Elapsed time in nanoseconds.
     */
    private static void printFileResults(String filename, ReductorBenchmark reductor, long elapsedTime){
        int numVariables = BDD.variables().size();
        int bddInitialSize = reductor.getInitialBDDSize();
        int bddReducedSize = reductor.getReducedBDDSize();
        long numSwaps = BDD.T.getSwapCounter();
        double elapsedSeconds = elapsedTime / 1000_000_000.0;
        String elapsedTimeHuman = TimeMeasurer.elapsedTimeAsHumanText(elapsedTime);
        
        // Show only the fields that are contained in
        // the configuration array RESULT_FIELDS
        StringBuilder s = new StringBuilder("");
        if(Arrays.asList(RESULT_FIELDS).contains("File"))
            s.append(filename).append(SEP);
        if(Arrays.asList(RESULT_FIELDS).contains("Nvars"))
            s.append(numVariables).append(SEP);
        if(Arrays.asList(RESULT_FIELDS).contains("InitSize"))
            s.append(bddInitialSize).append(SEP);
        if(Arrays.asList(RESULT_FIELDS).contains("ReducSize"))
            s.append(bddReducedSize).append(SEP);
        if(Arrays.asList(RESULT_FIELDS).contains("Nswaps"))
            s.append(numSwaps).append(SEP);
        if(Arrays.asList(RESULT_FIELDS).contains("Time (ns)"))
            s.append(elapsedTime).append(SEP);
        if(Arrays.asList(RESULT_FIELDS).contains("Time (s)"))
            s.append(elapsedSeconds).append(SEP);
        if(Arrays.asList(RESULT_FIELDS).contains("Time (h:m:s)"))
            s.append(elapsedTimeHuman).append(SEP);
        
        // Prints the data
        System.out.println(s.toString());
    }
    
    /**
     * Make the benchmark of a BDD in file format.
     * @param algorithm Name of the algorithm chosen.
     * @param params Extra parameters of that algorithm.
     * @param format File format used to embed the BDD.
     * @param file File that contains a BDD.
     */
    private static void makeFileBenchmark(String algorithm, HashMap<String,String> params, String format, File file){
        String filepath = file.getAbsolutePath();
        String filename = file.getName();
        ReductorBenchmark reductor = new ReductorBenchmark(algorithm, params, format, filepath);
        long elapsedTime = reductor.run();
        printFileResults(filename, reductor, elapsedTime);
    }

    /**
     * Make the benchmark of a BDD (in a file) or several BDDs (in a directory).
     * @param algorithm Name of the algorithm chosen.
     * @param params Extra parameters of that algorithm.
     * @param format File format used to embed the BDD.
     * @param resourceName File or directory path.
     */
    public static void makeBenchmark(String algorithm, HashMap<String,String> params, String format, String resourceName){
        printFileHeader(algorithm);
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
                        System.out.println(filename + SEP + BDD.variables().size() + SEP + "-" + SEP + "-" + SEP + BDD.T.getSwapCounter()+SEP+"-"+SEP+"-");
                    }
                    //BDD.T.clear();
                }
            }
        }
        else{
            System.err.println(resourceName + " is not a file or directory");
        }
    
    }
}
