/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import djbdd.BDD;
import java.io.*;
import java.util.*;

/**
 * Combines several BDDs that appear in the BDD file
 * @author diegoj
 */
public class Combinator {
    
    /** File to be loaded */
    private String inputFile;
    
    /** Boolean operator used when loading the BDDs in the file */
    private String operation;
    
    /** Should output some useful information to stdout? */
    private boolean verbose = true;
    
    /**
     * The constructor of this Combinator.
     * @param inputFile File to combine in one BDD.
     * @param operator Boolean operation as in "and", "or", "->" or "<=>"
     */
    public Combinator(String inputFile, String operation){
        this.inputFile = inputFile;
        this.operation = operation;
    }
    
    /**
     * Loads a file and return the BDD gotten of applying the operation between them.
     * @return BDD BDD object.
     */
    public BDD run() {
        boolean firstBDD = true;
        BDD bdd = null;
        int i = 0;
        try {
            
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            StringBuilder sb = new StringBuilder();
            
            // First line must be " # Variables: <number of variables>
            String line = br.readLine();
            String[] _variables = line.split(":");
            int num_variables = Integer.parseInt(_variables[1].trim());
            
            // We get the variables
            ArrayList<String> variables = new ArrayList<String>(num_variables);
            line = br.readLine();
            int var_i = 0;
            while(line!=null && var_i<num_variables){
                variables.add(line);
                line = br.readLine();
                var_i++;
            }
            
            // We get the first line
            // of the BDDS enumeration
            line = br.readLine();
            
            // We get each of the BDDs
            String bddString = "";
            while (line != null) {
                if (!line.equals("")) {
                    if (line.charAt(0) == '#') {
                        line = br.readLine();
                        while (line != null && line.charAt(0) != '#') {
                            sb.append(line);
                            sb.append('\n');
                            line = br.readLine();
                        }
                        
                        // If is the first BDD, we don't make apply
                        if (firstBDD) {
                            i = 1;
                            bddString = sb.toString();
                            bdd = BDD.fromString(bddString, variables);
                            firstBDD = false;
                            sb = new StringBuilder();
                            if(verbose){
                                System.out.println("BDD " + i + " (" + bdd.size() + " vertices)");
                            }
                        
                        // For the next BDDs, we make apply between the current
                        // BDD and the "total" BDD
                        } else {
                            i++;
                            bddString = sb.toString();
                            //System.out.println("OTHER\n" + bddString);
                            BDD bddI = BDD.fromString(bddString, variables);
                            if(!bddI.isTautology() && operation.equals("and"))
                            {
                                //System.out.println("END");
                                bdd = bdd.apply(operation, bddI);
                                if(verbose){
                                    System.out.println("BDD " + i + " (" + bdd.size() + " vertices)");
                                    //System.out.println(bdd.toString());
                                }
                            }
                            sb = new StringBuilder();
                        }
                    }
                }
                line = br.readLine();
            }
            //String everything = sb.toString();
            br.close();
        } catch (Exception e) {
            System.err.println("Something is wrong in Combinator.run");
            e.printStackTrace();
        }
        return bdd;
    }
}
