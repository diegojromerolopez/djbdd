/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;
import java.util.*;
import java.io.*;
import djbdd.*;
import djbdd.timemeasurer.TimeMeasurer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;





/**
 *
 * @author diegoj
 */
public class FileOptimizer {
    
    String inputFilename;
    String outputFilename;
    public static final int NUM_THREADS = 8;
    
    public FileOptimizer(String inputFilename, String outputFilename){
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
    }
    
    /**
     * Execute optimization and make file with the BDDs
     */
    public void run(FileLoaderConfiguration config) {
        config.numberOfCNFByBDD = 1;
        config.useApplyInCreation = true;
        
        
        // TODO: parametrize loader
        BDDSheFileLoader loader = new BDDSheFileLoader(inputFilename);
        loader.init(config);
        
        // Generic part
        //int numThreads = Math.min(loader.numClausules, NUM_THREADS);
        int numThreads = NUM_THREADS;
        int numFormulasByThread = loader.bdd_formulas.size()/numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ArrayList<FileOptimizerThread> workers = new ArrayList<FileOptimizerThread>();
        if(config.verbose){
            System.out.println( "There are "+loader.bdd_formulas.size()+" formulas" );
        }
        for (int i = 0; i < numThreads; i++)
        {
            int startFormulaIndex = i*numFormulasByThread;
            int endFormulaIndex = startFormulaIndex + numFormulasByThread;
            if(i==numThreads-1)
                endFormulaIndex = loader.bdd_formulas.size();
            if(config.verbose){
                System.out.println("Thread "+i+" has clausules ["+startFormulaIndex+", "+endFormulaIndex+"]");
            }
            ArrayList<String> threadFormulas = new ArrayList<String>(loader.bdd_formulas.subList(startFormulaIndex, endFormulaIndex));
            Runnable worker = new FileOptimizerThread(i, outputFilename+".bdd.txt", threadFormulas, loader.variables, config.useApplyInCreation);
            executor.execute(worker);
            workers.add((FileOptimizerThread)worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            //leep?
        }

     }
}
