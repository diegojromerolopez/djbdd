/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import java.util.*;
import java.io.*;
import djbdd.timemeasurer.TimeMeasurer;
import djbdd.BDD;

/**
 *
 * @author diegoj
 */
public class FileOptimizerThread implements Runnable {
    private int index;
    private ArrayList<String> formulas;
    private boolean verbose = true;
    private ArrayList<BDD> bdds;
    
    /** Should the thread join the BDDs using a logical operation? */
    private final boolean joinBDDs;
    
    /** BDD to store the total bdd, that is bdd_0 <op> bdd_1 <op> bdd_2 <op> .. <op> bdd_N  */
    private BDD joinedBDD;
    
    /** Operation used to join the BDD if joinBDDs is true */
    private String operation;
    
    /** Should me have to use apply in BDD creation? */
    public final boolean useApplyInCreation;
    
    /** Writer to file, shared with all threads */
    PrintWriter writer ;
    
    // Constants
    
    private static final int ITERATIONS = 10;
    private static final int MAX_VARIABLES_TO_MAKE_ITERATIONS = 200;
    private static int BDD_I = 1;
    private static int RANDOM_SEED = 1;
     
    public FileOptimizerThread(int index, PrintWriter writer, ArrayList<String> formulas, boolean useApplyInCreation){
        this.index = index;
        this.formulas = formulas;
        this.useApplyInCreation = useApplyInCreation;
        this.writer = writer;
        this.bdds = new ArrayList<BDD>(formulas.size());
        this.joinBDDs = false;
    }
    
    public FileOptimizerThread(int index, PrintWriter writer, ArrayList<String> formulas, boolean useApplyInCreation, String bddJoinOperation){
        this.index = index;
        this.formulas = formulas;
        this.useApplyInCreation = useApplyInCreation;
        this.writer = writer;
        this.bdds = new ArrayList<BDD>(formulas.size());
        this.operation = bddJoinOperation;
        this.joinBDDs = true;
    }
    
    public synchronized void writeToFile(BDD bdd, int i){
        String bddName = "BDD "+BDD_I+" (T "+this.index+" "+(i+1)+")";
        this.writer.println("# BEGIN "+bddName+"\n"+bdd.toString()+"# END "+bddName+"\n");
        this.writer.flush();
        BDD_I++;
    }
    
    public synchronized void writeBDDsToFile(){
        // Write the reduced bdds to a file
        for (int i = 0; i < bdds.size(); i++) {
            this.writeToFile(bdds.get(i), i);
        }
    }
    
    private String[] getInitialVariableOrder(String function){
        ArrayList<String> variable_order = new ArrayList<String>(BDD.variables().size());
        for(int i=0; i<BDD.variables().size(); i++){
            String var = BDD.variables().get(i);
            Boolean exists_variable = function.contains(var);
            if(exists_variable)
                variable_order.add(var);
        }
        return variable_order.toArray(new String[variable_order.size()]);
    }
    
    @Override
    public void run() {
        String[] _variables = BDD.variables().toArray(new String[BDD.variables().size()]);
        //String[] _variable_order = this.getInitialVariableOrder(formulas.get(0));
        
        try {
            //PrintWriter writer = new PrintWriter(this.outputFilename, "UTF-8");
            boolean firstBDD = false;
            for (int i = 0; i < formulas.size(); i++)
            {
                TimeMeasurer t = new TimeMeasurer("\nFMLA " + (i + 1) + "/" + formulas.size());
                String formulaI = formulas.get(i);
                if (verbose) {
                    System.out.println("T"+this.index+ " - Formula " + (i + 1) + "/" + formulas.size() + ": " + formulaI);
                }
                String[] _variable_order = this.getInitialVariableOrder(formulaI);
                // We make ITERATIONS loops, unless we got too many variables
                // in that case, we make 1 only extra iteration
                int LOOPS = ITERATIONS;
                if(_variable_order.length > MAX_VARIABLES_TO_MAKE_ITERATIONS)
                    LOOPS = 1;
                
                // BDD base
                TimeMeasurer t_loops = new TimeMeasurer(">>>>>>>>>>>> runned of "+_variable_order.length+" variables & "+BDD.T.size()+" <<<<<<<<<<", true);
                BDD bdd = new BDD(formulaI, _variable_order, useApplyInCreation);
                // Iterations to get a smaller BDD
                int j = 0;
                while (j < LOOPS) {
                    Collections.shuffle(Arrays.asList(_variable_order), new Random(RANDOM_SEED));
                    BDD bddI = new BDD(formulaI, _variable_order, useApplyInCreation);
                    j++;
                    if (bddI.size() < bdd.size()) {
                        bdd = bddI;
                    }
                }
                t_loops.end().show();
                bdds.add(bdd);
                t.end().show();
            }
            
            ArrayList<BDD> nonTrivialBDDs = new ArrayList<BDD>(bdds.size());
            ArrayList<BDD> trivialBDDs = new ArrayList<BDD>(bdds.size());
            for(BDD bdd : bdds){
                if(bdd.size()==1)
                    trivialBDDs.add(bdd);
                else
                    nonTrivialBDDs.add(bdd);
            }
            
            bdds = nonTrivialBDDs;
            
            for(BDD trivialBdd : trivialBDDs){
                System.out.println("TRIVIAL");
                bdds.set(0,bdds.get(0).apply(this.operation, trivialBdd));
            }
            
            /*
            if(this.joinBDDs){
                firstBDD = true;
                int MAX_SIZE_TO_JOIN = 1;
                ArrayList<BDD> remainingBDDs = new ArrayList<BDD>(this.bdds.size());
                for(int i=0; i<this.bdds.size(); i++){
                    BDD bddI = this.bdds.get(i);
                    boolean musJoinBddI = bddI.size() <= MAX_SIZE_TO_JOIN;
                    if (verbose) {
                        System.out.println("T"+this.index+ " - Should we join " + (i + 1) + "/" + formulas.size() + "?: " + (musJoinBddI?"Yes":"No"));
                    }
                    if(musJoinBddI){
                        if(firstBDD){
                            this.joinedBDD = bddI;
                            firstBDD = false;
                        }else {
                            this.joinedBDD = this.joinedBDD.apply(this.operation, bddI);
                        }
                        bddI = null;
                        this.bdds.set(i, null);
                    }else{
                        remainingBDDs.add(bddI);
                    }
                }
                remainingBDDs.add(this.joinedBDD);
                this.bdds = remainingBDDs;
            }*/
            
        } catch (Exception e) {
            System.err.println("System has failed!");
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    //public String getOutputFilename(){ return this.outputFilename; }
    
    public ArrayList<BDD> getBDDs(){ return this.bdds; }
  
    @Override
    public String toString(){
        return this.index +" thread with"+formulas.size()+" formulas";
    }
}