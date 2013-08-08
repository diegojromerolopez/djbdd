/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import java.util.*;
import java.io.*;

import djbdd.BDD;

/**
 *
 * @author diegoj
 */
public class BDDGroupJoinerThread  implements Runnable {
    
    //private final ArrayList<BDD> resultingBDDs;
    private final ArrayList<ArrayList<BDD>> bddGroups;
    private final String operation;
    private final boolean verbose = true;
    private int index;
    private PrintWriter writer; 
    public static int BDD_I = 1;
    
    public BDDGroupJoinerThread(int index, PrintWriter writer, ArrayList<ArrayList<BDD>> threadBddGroups, String operation){
        this.bddGroups = threadBddGroups;
        this.operation = operation;
        this.index = index;
        //this.resultingBDDs = new ArrayList<BDD>(threadBddGroups.size());
        this.writer = writer;
    }
    
    private BDD joinBDDs(ArrayList<BDD> bdds, int groupIndex){
        BDD resultingBDD = null;
        int bdds_size = bdds.size();
        for(int i=0; i<bdds_size; i++){
            BDD bdd = bdds.get(i);
            if(i==0){
                resultingBDD = bdd;
            }
            else{
                resultingBDD = resultingBDD.apply(operation, bdd);
            }
            if(verbose){
                System.out.println("[Thread "+this.index+"]"+" BDD "+i+"/"+bdds.size()+" joined");
            }
        }
        this.writeToFile(resultingBDD, groupIndex);
        return resultingBDD;
    }
    
    public synchronized void writeToFile(BDD bdd, int i){
        String bddName = "BDD "+BDD_I+" (T "+this.index+" "+(i+1)+")";
        this.writer.println("# BEGIN "+bddName+"\n"+bdd.toString()+"# END "+bddName+"\n");
        this.writer.flush();
        BDD_I++;
    }
    
    
    @Override
    public void run() {
        int i = 1;
        for(ArrayList<BDD> bddGroup : bddGroups){
            BDD bddRes = this.joinBDDs(bddGroup, i);
            //this.resultingBDDs.add(bddRes);
            if(verbose){
                System.out.println("[Thread "+this.index+"]"+" BDD Group "+i+"/"+bddGroups.size()+" joined");
            }
            i++;
            bddGroup = null;
        }
    }
    
    //public ArrayList<BDD> getBDDs(){ return resultingBDDs; }
    
}
