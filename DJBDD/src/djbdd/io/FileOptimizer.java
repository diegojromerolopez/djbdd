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
    
    /** Formulas each one of them will be one BDD */
    private ArrayList<String> formulas;
    
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
    public void run() {
        // Generic part
        int numThreads = NUM_THREADS;
        //this.formulas = new ArrayList<String>(this.formulas.subList(0, 100));
        int numFormulasByThread = this.formulas.size()/numThreads;
        ExecutorService executor; 
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
        writer.println("# Variables: "+BDD.variables().size());
        for(String variable : BDD.variables()){
            writer.println(variable);
        }
        writer.println("");
        System.out.flush();

        // GC Executor
        GCThread gcCollector = new GCThread();
        Thread gcThread = new Thread(gcCollector);
        gcThread.start();
        
        // Creating the optimized BDD file
        executor = Executors.newFixedThreadPool(numThreads);
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
            Runnable worker = new FileOptimizerThread(i, writer, threadFormulas, USE_APPLY_IN_BDD_CREATION, "and");
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
        
        ////////////////////////////////////////////////////////////////////////
        // If we are a genious & correct the memory greedy BDDs
        boolean GROUP_BY_VARIABLE_SETS = true;
        if(GROUP_BY_VARIABLE_SETS)
        {

            // Join the BDDs by variables
            boolean firstBDD = true;
            HashMap<TreeSet<Integer>,ArrayList<BDD>> groups = new HashMap<TreeSet<Integer>,ArrayList<BDD>>();
            for(FileOptimizerThread thread : workers){
                for(BDD bdd : thread.getBDDs()){
                    TreeSet<Integer> vars = new TreeSet<Integer>(bdd.present_variable_indices);
                    if(firstBDD){
                        ArrayList<BDD> bdds = new ArrayList<BDD>(1);
                        bdds.add(bdd);
                        groups.put(vars,bdds);
                        firstBDD = false;
                    }
                    else{
                        boolean foundGroup = false;
                        for(TreeSet t : groups.keySet()){
                            TreeSet<Integer> intersection = new TreeSet<Integer>(vars);
                            intersection.retainAll(t);
                            if(intersection.size()>0){
                                groups.get(t).add(bdd);
                                foundGroup = true;
                                break;
                            }
                        }
                        if(!foundGroup){
                            ArrayList<BDD> bdds = new ArrayList<BDD>(1);
                            bdds.add(bdd);
                            groups.put(vars,bdds);
                        }
                    }
                }
            }

            workers = null;
            // DEBUG printing
            if(true){
                System.out.println("---------------------------------------------");
                System.out.println("There are "+groups.keySet().size()+" groups of BDDs");
                int sum = 0;
                for (TreeSet t : groups.keySet()) {
                    System.out.println("========================");
                    System.out.println(t.toString() + ": " + groups.get(t).size());
                    sum += groups.get(t).size();
                    /*for (BDD bdd : groups.get(t)) {
                        bdd.print();
                    }*/
                }
                System.out.println("---------------------------------------------");
                System.out.println("There are a total of "+sum);
            }

            // We join the groups when they got a size of < 20 BDDs
            ArrayList<ArrayList<BDD>> bddGroups = new ArrayList<ArrayList<BDD>>(groups.values());
            ArrayList<ArrayList<BDD>> resultantBDDGroups = new ArrayList<ArrayList<BDD>>(1000);
            for(int i=0; i<bddGroups.size(); i++){
                ArrayList<BDD> bddGroup = bddGroups.get(i);
                if(bddGroup.size() < MAX_GROUP_SIZE_TO_APPLY_REDUCE){
                    System.out.println("Group "+i+"/"+bddGroups.size());
                    BDD bdd = bddGroup.get(0);
                    for(int j=1; j<bddGroup.size(); j++){
                        bdd = bdd.apply("and", bddGroup.get(j));
                        System.out.println("Group "+i+"/"+bddGroups.size()+": "+(j+1)+"/"+bddGroup.size());
                    }
                    ArrayList<BDD> newBddGroup = new ArrayList<BDD>(1);
                    newBddGroup.add(bdd);
                    resultantBDDGroups.add(newBddGroup);
                }else{
                    resultantBDDGroups.add(bddGroup);
                }
            }
            
            bddGroups = resultantBDDGroups;
            gcCollector.stop();
            BDD.T.gc();
            
            num_bdds = 0;
            for(ArrayList<BDD> bddGroup : bddGroups){
                num_bdds += bddGroup.size();
            }
            
            writer.println("# BDDs: "+num_bdds);
            writer.println("");
            
            int groupIndex = 1;
            for(ArrayList<BDD> bddGroup : bddGroups){
                int bddIndex = 1;
                for(BDD bdd : bddGroup){
                    String bddName = "BDD "+groupIndex+"-"+bddIndex;
                    writer.println("# BEGIN "+bddName+"\n"+bdd.toString()+"# END "+bddName+"\n");
                    writer.flush();
                    bddIndex++;
                }
                groupIndex++;
            }
        }
        
        // Close the file
        writer.close();

     }
}
