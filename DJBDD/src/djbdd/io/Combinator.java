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
            String line = br.readLine();
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

                        if (firstBDD) {
                            i = 1;
                            bddString = sb.toString();
                            bdd = BDD.fromString(bddString);
                            firstBDD = false;
                            sb = new StringBuilder();
                            if(verbose)
                                System.out.println("BDD " + i + " (" + bdd.size() + " vertices)");

                        } else {
                            i++;
                            bddString = sb.toString();
                            //System.out.println("OTHER\n" + bddString);
                            BDD bddI = BDD.fromString(bddString);
                            //System.out.println("END");
                            bdd = bdd.apply(operation, bddI);
                            sb = new StringBuilder();
                            if(verbose)
                                System.out.println("BDD " + i + " (" + bdd.size() + " vertices)");
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
