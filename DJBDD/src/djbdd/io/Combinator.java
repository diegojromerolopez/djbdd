/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import djbdd.BDD;
import djbdd.timemeasurer.TimeMeasurer;
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
    public void run() {
        try {
            
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            
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
            
            // Initialize the BDD variables
            BDD.init(variables);
            
            // We get the first line
            // of the BDDS enumeration
            line = br.readLine();
            int num_bdds = Integer.parseInt(line.split(":")[1].trim());
            
            // New line
            line = br.readLine();

            // First line with useful information
            line = br.readLine();
            
            String outputFilename = "bdd_results.txt";
            PrintWriter writer = null;
            try {
                File f = new File(outputFilename);
                if (f.exists()) {
                    f.delete();
                }
                writer = new PrintWriter(new FileOutputStream(new File(outputFilename), true));
            } catch (Exception e) {
                System.err.println("Error creating the PrintWriter");
                e.printStackTrace();
            }
            
            // The idea is to be grouping the BDDs according to the variable
            // occurrence of its formulas
            StringBuilder sb = new StringBuilder();
            HashMap<Integer,BDD> groups = new HashMap<Integer,BDD>();
            int maxGroupId = 1;
            while (line != null) {
                if (!line.equals("")) {
                    if (line.charAt(0) == '#') {
                        // # BEGIN BDD \d+-\d+
                        System.out.println(line);
                        int groupId = Integer.parseInt(line.replace("# BEGIN BDD ", "").split("-")[0]);
                        line = br.readLine();
                        while (line != null && line.charAt(0) != '#') {
                            sb.append(line);
                            sb.append('\n');
                            line = br.readLine();
                        }
                        String bddString = sb.toString();
                        BDD bdd = BDD.fromString(bddString);
                        sb = new StringBuilder();
                        // If exists the group, apply the operation and store the result
                        if(groups.containsKey(groupId))
                        {
                            System.out.println("Grupo existente "+groupId);
                            BDD currentBdd = groups.get(groupId);
                            BDD bddRes = bdd.apply(this.operation, currentBdd);
                            groups.put(groupId, bddRes);
                            System.out.println("El grupo "+groupId+" tiene "+bddRes.size()+" nodos");
                        }
                        // If doesn't exist the group, insert the BDD
                        else
                        {
                            maxGroupId = groupId;
                            System.out.println("Nuevo grupo "+groupId);
                            groups.put(groupId, bdd);
                            System.out.println("Máximo grupo "+maxGroupId);
                            for(int i=0; i<maxGroupId; i++){
                                if(groups.containsKey(i)){
                                    writer.println("# BEGIN BDD "+i);
                                    groups.get(i).writeToFile(writer);
                                    writer.println("# END BDD "+i);
                                    groups.remove(i);
                                }
                            }
                            writer.flush();
                        }
                    }
                }
                line = br.readLine();
            }
             
            // If there are some bdd groups that are not writed to file
            // write them
            for (int i = 0; i < maxGroupId; i++) {
                if (groups.containsKey(i)) {
                    writer.print("# BEGIN BDD " + i);
                    groups.get(i).writeToFile(writer);
                    writer.println("# END BDD " + i);
                    groups.remove(i);
                }
            }
            writer.flush();
            writer.close();
            br.close();
        } catch (Exception e) {
            System.err.println("Something is wrong in Combinator.run");
            e.printStackTrace();
        }
        //return bdd;
    }
}
