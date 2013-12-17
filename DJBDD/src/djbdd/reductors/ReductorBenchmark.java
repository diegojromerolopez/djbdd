/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors;

import djbdd.core.*;
import djbdd.io.*;
import java.io.*;

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
    
    /**
     * Gets the reduction algorithm from a string that identifies it.
     * @param algorithm 
     */
    private static ReductionAlgorithm initAlgorithm(String algorithm){
        ReductionAlgorithm reductor = null;
        if(algorithm.equals("exact")){
            reductor = new ExactReductor();
        }
        else if(algorithm.equals("sifting")){
            reductor = new SiftingReductor();
        }
        return reductor;
    }
    
   
    public ReductorBenchmark(String algorithm, String format, String file){
        // Read the BDD
        BDDReader reader = new BDDReader(format, file);
        this.bdd = reader.read();
        this.bdd.print(true);
        Printer.printBDD(this.bdd, "cocks.png");
        // Apply the algorithm
        this.initialBDDSize = this.bdd.size();
        this.algorithm = ReductorBenchmark.initAlgorithm(algorithm);
    }
    
    /**
     * Runs the optimization process.
     */
    public BDD run(){
        this.algorithm.run();
        this.reducedBDDSize = this.bdd.size();
        return this.bdd;
    }
    
    public int getInitialBDDSize(){
        return this.initialBDDSize;
    }
    
    public int getReducedBDDSize(){
        return this.reducedBDDSize;
    }
    
    public static void makeBenchmark(String algorithm, String format, String resourceName){
        File f = new File(resourceName);
           
        if (f.isFile()) {
            String file = resourceName;
            ReductorBenchmark reductor = new ReductorBenchmark(algorithm, format, file);
            reductor.run();
            System.out.println(file + " " + algorithm + " " + reductor.getInitialBDDSize() + " " + reductor.getReducedBDDSize());
        } else if (f.isDirectory()) {
            File[] listOfFiles = f.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];
                if (file.isFile()) {
                    String filename = file.getAbsolutePath();
                    ReductorBenchmark reductor = new ReductorBenchmark(algorithm, format, filename);
                    reductor.run();
                    System.out.println(filename + " " + algorithm + " " + reductor.getInitialBDDSize() + " " + reductor.getReducedBDDSize());
                    //return;
                }
            }
        }
        else{
            System.err.println(resourceName + " is not a file or directory");
        }
    
    }
}
