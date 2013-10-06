/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.optimizer;
import djbdd.core.BDD;
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
    
    /** Formulas each one of them will be one BDD */
    private ArrayList<String> formulas;
    
    protected boolean groupBDDsByVariables = false;
    
    // Constants
    /** Shall we use apply operation when creating the BDDs? We would be fools if not. */
    public static final boolean USE_APPLY_IN_BDD_CREATION = true;
    
    /** Show extra information? */
    public static final boolean VERBOSE = false;
    
    /** Maximum size of the BDD group to be joined using AND operator in one BDD */
    public static final int MAX_GROUP_SIZE_TO_APPLY_REDUCE = 20;
    
    /** Number of threads used in the parallel loading */
    public static final int NUM_THREADS = 8;
    
    /**
     * Optimizes several formulas saving them in one file as BDDs.
     * @param variables List of variables used for the BDDs.
     * @param formulas List of formulas that will be translated to a optimized BDD.
     * @param outputFilename File that will contain the BDDs translated from the formulas.
     */
    public FileOptimizer(ArrayList<String>formulas, String outputFilename){
        this.outputFilename = outputFilename;
        this.formulas = formulas;
    }
    
    /**
     * Execute optimization and make file with the BDDs
     */
    protected static void optimizeFormulas(ArrayList<String> formulas, String outputFilename, boolean groupBDDsByVariables) {
        // Generic part
        int numThreads = NUM_THREADS;
        if(NUM_THREADS > formulas.size())
            numThreads = 1;
        //logicFormulas = new ArrayList<String>(logicFormulas.subList(0, 100));
        int numFormulasByThread = formulas.size()/numThreads;
        ExecutorService executor; 
        ArrayList<FileOptimizerThread> workers = new ArrayList<FileOptimizerThread>();
        if(VERBOSE){
            System.out.println( "There are "+formulas.size()+" formulas" );
        }
        
        PrintWriter writer = null;
        try{
            File f = new File(outputFilename);
            if(f.exists())
                f.delete();
            writer = new PrintWriter(new FileOutputStream(new File(outputFilename),true));
        }catch(Exception e){
            System.err.println("Error creating the PrintWriter");
            e.printStackTrace();
        }
        
        // Number of variables
        writer.println("# Variables: "+BDD.variables().size());
        for(String variable : BDD.variables().list()){
            writer.println(variable);
        }
        writer.println("");
        System.out.flush();

        // GC Thread
        //GCThread gcCollector = new GCThread();
        //gcCollector.start();
        
        // Creating the optimized BDD file
        executor = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++)
        {
            int startFormulaIndex = i*numFormulasByThread;
            int endFormulaIndex = startFormulaIndex + numFormulasByThread;
            if(i==numThreads-1)
                endFormulaIndex = formulas.size();
            if(true){
                System.out.println("Thread "+i+" has clausules ["+startFormulaIndex+", "+endFormulaIndex+"]");
            }
            ArrayList<String> threadFormulas = new ArrayList<String>(formulas.subList(startFormulaIndex, endFormulaIndex));
            Runnable worker = new FileOptimizerThread(i, writer, threadFormulas, USE_APPLY_IN_BDD_CREATION);
            executor.execute(worker);
            workers.add((FileOptimizerThread)worker);
        }
        //System.exit(-1);
        executor.shutdown();
        while (!executor.isTerminated()) {
            //Sleep?
        }

        // Tree
        BDD.T.gc();
        writer.println("# Tree: "+BDD.T.size());
        BDD.T.write(writer);
        writer.println("");
        
        // Number of BDDs without applying reduction
        int num_bdds = 0;
        for(FileOptimizerThread thread : workers){
            num_bdds += thread.getBDDs().size();
        }
        System.out.println("We have got "+num_bdds+" without any reduction process");
        
        ArrayList<ArrayList<BDD>> bddGroups = new ArrayList<ArrayList<BDD>>(100);;
        ////////////////////////////////////////////////////////////////////////
        // Join the BDDs by variables
        if(!groupBDDsByVariables){
            ArrayList<BDD> bdds = new ArrayList<BDD>(1000);
            for (FileOptimizerThread thread : workers)
            {
                bdds.addAll(thread.getBDDs());
            }
            if(bdds.size()<20){
                BDD bdd = BDD.applyToAll("and", bdds, true);
                ArrayList<BDD> joinedBDDs = new ArrayList<BDD>(1);
                joinedBDDs.add(bdd);
                bdds = joinedBDDs;
            }
            
            bddGroups = new ArrayList<ArrayList<BDD>>(1);
            bddGroups.add(bdds);
        }
        else
        {
            boolean firstBDD = true;
            HashMap<TreeSet<Integer>, ArrayList<BDD>> groups = new HashMap<TreeSet<Integer>, ArrayList<BDD>>();
            // For each thread, we get its BDDs and assign to each one a BDD group
            // based in its variable.
            // At the end, we get functional disjoint BDD groups
            for (FileOptimizerThread thread : workers) {
                for (BDD bdd : thread.getBDDs()) {
                    TreeSet<Integer> vars = new TreeSet<Integer>(bdd.present_variable_indices);
                    if (firstBDD) {
                        ArrayList<BDD> bdds = new ArrayList<BDD>(1);
                        bdds.add(bdd);
                        groups.put(vars, bdds);
                        firstBDD = false;
                    } else {
                        boolean foundGroup = false;
                        for (TreeSet t : groups.keySet()) {
                            TreeSet<Integer> intersection = new TreeSet<Integer>(vars);
                            intersection.retainAll(t);
                            if (intersection.size() > 0) {
                                groups.get(t).add(bdd);
                                foundGroup = true;
                                break;
                            }
                        }
                        if (!foundGroup) {
                            ArrayList<BDD> bdds = new ArrayList<BDD>(1);
                            bdds.add(bdd);
                            groups.put(vars, bdds);
                        }
                    }
                }


                workers = null;
                // DEBUG printing
                if (true) {
                    System.out.println("---------------------------------------------");
                    System.out.println("There are " + groups.keySet().size() + " groups of BDDs");
                    int sum = 0;
                    for (TreeSet t : groups.keySet()) {
                        System.out.println("========================");
                        System.out.println(t.toString() + ": " + groups.get(t).size());
                        sum += groups.get(t).size();
                    }
                    System.out.println("---------------------------------------------");
                    System.out.println("There are a total of " + sum);
                }

                bddGroups = new ArrayList<ArrayList<BDD>>(groups.values());


                // We join the groups when they got a size of < 20 BDDs
                ArrayList<ArrayList<BDD>> resultantBDDGroups = new ArrayList<ArrayList<BDD>>(1000);
                for (int i = 0; i < bddGroups.size(); i++) {
                    ArrayList<BDD> bddGroup = bddGroups.get(i);
                    if (bddGroup.size() < MAX_GROUP_SIZE_TO_APPLY_REDUCE) {
                        System.out.println("FileOptimizer :: Group " + (i + 1) + "/" + bddGroups.size());
                        BDD bdd = BDD.applyToAll("and", bddGroup);
                        ArrayList<BDD> newBddGroup = new ArrayList<BDD>(1);
                        newBddGroup.add(bdd);
                        resultantBDDGroups.add(newBddGroup);
                    } else {
                        resultantBDDGroups.add(bddGroup);
                    }
                    BDD.T.gc();
                }

                bddGroups = resultantBDDGroups;
            }
        }
        
        BDD.T.gc();

        num_bdds = 0;
        for (ArrayList<BDD> bddGroup : bddGroups) {
            num_bdds += bddGroup.size();
        }

        if (num_bdds > 0) {
            System.out.println("Printing to file");
            writer.println("# BDDs: " + num_bdds);
            writer.println("");

            System.out.println("Hay " + bddGroups.size() + " grupos");
            int groupIndex = 1;
            for (ArrayList<BDD> bddGroup : bddGroups) {
                int bddIndex = 1;
                for (BDD bdd : bddGroup) {
                    System.out.println("Hay " + bddGroup.size() + " BDDs");
                    String bddName = "BDD " + groupIndex + "-" + bddIndex;
                    writer.println("# BEGIN " + bddName + "\n" + bdd.toString() + "# END " + bddName + "\n");
                    System.out.println(bddName + " printed to file");
                    writer.flush();
                    bddIndex++;
                }
                groupIndex++;
            }
        } else {
            System.err.println("Es 0 petosky");
            System.exit(-1);
        }

        
        // Close the file
        writer.close();
        //gcCollector.end();
     }
    
     public void run(){
         FileOptimizer.optimizeFormulas(this.formulas, this.outputFilename, this.groupBDDsByVariables);
     }
}
