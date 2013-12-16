/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors;

import djbdd.core.*;
import djbdd.io.*;

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
        BDDReader reader = new BDDReader(format, file);
        this.bdd = reader.read();
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
    
}
