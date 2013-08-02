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
 * Optimizes several formulas saving them in one file as BDDs.
 * @author diegoj
 */
public class FileOptimizer {
    
    /** File name where will be saved the BDDs */
    String outputFilename;
    
    /** Variables used for the formulas */
    private ArrayList<String> variables;
    
    /** Formulas each one of them will be one BDD */
    private ArrayList<String> formulas;
    
    // Constants
    /** Shall we use apply operation when creating the BDDs? We would be fools if not. */
    public static final boolean USE_APPLY_IN_BDD_CREATION = true;
    
    /** Show extra information? */
    public static final boolean VERBOSE = false;
    
    /** Number of threads used in the parallel loading */
    public static final int NUM_THREADS = 8;
    
    /**
     * Optimizes several formulas saving them in one file as BDDs.
     * @param variables List of variables used for the BDDs.
     * @param formulas List of formulas that will be translated to a optimized BDD.
     * @param outputFilename File that will contain the BDDs translated from the formulas.
     */
    public FileOptimizer(ArrayList<String>variables, ArrayList<String>formulas, String outputFilename){
        this.outputFilename = outputFilename;
        this.variables = variables;
        this.formulas = formulas;
    }
    
    /**
     * Execute optimization and make file with the BDDs
     */
    public void run() {
        // Generic part
        //int numThreads = Math.min(loader.numClausules, NUM_THREADS);
        int numThreads = NUM_THREADS;
        int numFormulasByThread = this.formulas.size()/numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ArrayList<FileOptimizerThread> workers = new ArrayList<FileOptimizerThread>();
        if(VERBOSE){
            System.out.println( "There are "+this.formulas.size()+" formulas" );
        }
        
        PrintWriter writer = null;
        try{
            File f = new File(this.outputFilename);
            if(f.exists())
                f.delete();
            writer = new PrintWriter(new FileOutputStream(new File(this.outputFilename),true));
        }catch(Exception e){
            System.err.println("Error creating the PrintWriter");
            e.printStackTrace();
        }
        
        // Number of variables
        writer.println("# Variables: "+this.variables.size());
        for(String variable : this.variables){
            writer.println(variable);
        }
        writer.println("");
        
        // Creating the optimized BDD file
        for (int i = 0; i < numThreads; i++)
        {
            int startFormulaIndex = i*numFormulasByThread;
            int endFormulaIndex = startFormulaIndex + numFormulasByThread;
            if(i==numThreads-1)
                endFormulaIndex = this.formulas.size();
            if(true){
                System.out.println("Thread "+i+" has clausules ["+startFormulaIndex+", "+endFormulaIndex+"]");
            }
            ArrayList<String> threadFormulas = new ArrayList<String>(this.formulas.subList(startFormulaIndex, endFormulaIndex));
            Runnable worker = new FileOptimizerThread(i, writer, threadFormulas, this.variables, USE_APPLY_IN_BDD_CREATION, "and");
            executor.execute(worker);
            workers.add((FileOptimizerThread)worker);
        }
        //System.exit(-1);
        executor.shutdown();
        while (!executor.isTerminated()) {
            //leep?
        }

        // Number of BDDs
        int num_bdds = 0;
        for(FileOptimizerThread thread : workers){
            num_bdds += thread.getBDDs().size();
        }
        writer.println("# BDDs: "+num_bdds);
        writer.println("");
        
        // Print the BDDs
        int i=1;
        for(FileOptimizerThread thread : workers){
            for(BDD bdd : thread.getBDDs()){
                thread.writeToFile(bdd, i);
                i++;
            }
        }
        
        // Close the file
        writer.close();

     }
}
