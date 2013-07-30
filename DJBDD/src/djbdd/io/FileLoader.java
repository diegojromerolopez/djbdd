/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import  djbdd.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author diegoj
 */
public class FileLoader {
    
    ArrayList<String> inputFiles;
    ArrayList<BDD> bdds;
    BDD bdd;
    
    
    public FileLoader(ArrayList<String> inputFiles){
        this.inputFiles = inputFiles;
    }
    
    private BDD loadFile(String file, String operator){
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            boolean isFirst = true;
            bdd = null;
            while (line != null) {
                //int bddIndex = 0;
                String bddString = "";
                if(line.matches("#\\s*BEGIN\\s*BDD\\s*\\d+")){    
                    line = br.readLine();
                    while (line != null && line.charAt(0)!='#'){
                        bddString += line;
                        line = br.readLine();
                    }
                }
                BDD bddI = BDD.fromString(bddString);
                if(isFirst){
                    isFirst = false;
                    bdd = bddI;
                }
                else{
                    bdd = bdd.apply(operator, bddI);
                }
                //bdds.add(bdd);
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bdd;
    }
}
