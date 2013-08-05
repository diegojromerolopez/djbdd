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
    //private ArrayList<String> variables;
    
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
        //this.formulas = new ArrayList<String>(this.formulas.subList(0, 200));
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
            //leep?
        }

        // Number of BDDs
        int num_bdds = 0;
        for(FileOptimizerThread thread : workers){
            num_bdds += thread.getBDDs().size();
        }
        writer.println("# BDDs: "+num_bdds);
        writer.println("");
        
        ////////////////////////////////////////////////////////////////////////
        // If we are a genious y correct the memory greedy BDDs
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

            ArrayList<ArrayList<BDD>> bddGroups = new ArrayList<ArrayList<BDD>>(groups.values());
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
            
            /*
            // Join the BDDs grouped by variables
            executor = Executors.newFixedThreadPool(numThreads);
            ArrayList<BDDGroupJoinerThread> joiners = new ArrayList<BDDGroupJoinerThread>();
            // We join each list of BDDs

            int i=1;
            ArrayList<ArrayList<BDD>> bddGroups = new ArrayList<ArrayList<BDD>>(groups.values());
            int numGroupsByThread = bddGroups.size()/numThreads;
            System.out.println("Threre are "+bddGroups.size()+" groups");
            for(i=0; i<numThreads; i++)
            {
                int start = i*numGroupsByThread;
                int end = start + numGroupsByThread;
                if(i==numThreads-1){
                    end = bddGroups.size();
                }

               ArrayList<ArrayList<BDD>> threadBddGroups = new ArrayList<ArrayList<BDD>>(bddGroups.subList(start, end));

                String sizeString = "Thread "+i+" ["+start+", "+end+"]: ";
                for(ArrayList<BDD> bdds : threadBddGroups)
                    sizeString += bdds.size()+", ";
                System.out.println(sizeString);

                Runnable worker = new BDDGroupJoinerThread(i, writer, threadBddGroups, "and");
                worker.run();
                //executor.execute(worker);
                //joiners.add((BDDGroupJoinerThread)worker);
            }*/

            //executor.shutdown();
            //executor.awaitTermination();
            //while (!executor.isTerminated()) {
                //leep?
            //}
            /*
            int BDD_I = 1;
            for(BDDGroupJoinerThread worker : joiners){
                ArrayList<BDD> bdds = worker.getBDDs();
                for(BDD bdd : bdds){
                    String bddName = "BDD "+BDD_I;
                    writer.println("# BEGIN "+bddName+"\n"+bdd.toString()+"# END "+bddName+"\n");
                    writer.flush();
                    BDD_I++;
                }
            }*/
        }
        
        // Close the file
        writer.close();

     }
}
