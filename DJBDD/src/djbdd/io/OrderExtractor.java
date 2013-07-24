/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;
import java.util.*;
import java.io.*;
import djbdd.*;
import djbdd.timemeasurer.TimeMeasurer;

/**
 *
 * @author diegoj
 */
public class OrderExtractor {
    
    String inputFilename;
    String outputFilename;
    
    public OrderExtractor(String inputFilename, String outputFilename){
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
    }
    
    public void run(FileLoaderConfiguration config){
        config.numberOfCNFByBDD = 1;
        BDDSheFileLoader loader = new BDDSheFileLoader(inputFilename); 
        loader.init(config);
        int LOOPS = 1;
        String[] _variables = loader.variables.toArray(new String[loader.variables.size()]);
        ArrayList<String> variable_orderings = new ArrayList<String>(loader.bdd_formulas.size());
        
        for(int i=0; i<loader.bdd_formulas.size(); i++)
        {
            TimeMeasurer t = new TimeMeasurer("\nFMLA " + (i+1)+"/"+loader.bdd_formulas.size());
            String formulaI = loader.bdd_formulas.get(i);
            if (config.verbose) {
                System.out.println("Formula " + (i + 1) + "/" + loader.bdd_formulas.size() + ": " + formulaI);
            }
            String[] _variable_order = loader.getVariableOrder(formulaI);
            BDD bdd = new BDD(formulaI, _variables, _variable_order);
            int j = 0;
            while(j < LOOPS){
                Collections.shuffle( Arrays.asList(_variable_order) );
                BDD bddI = new BDD(formulaI, _variables, _variable_order);
                j++;
                if(bddI.size() < bdd.size())
                    bdd = bddI;
            }
            String variable_ordering = "";
            for(String s:_variable_order)
                variable_ordering += s+",";
            variable_ordering = variable_ordering.substring(0, variable_ordering.length()-1);
            variable_orderings.add(variable_ordering);
            t.end();
            t.show();
       }
        try{
            PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
            for(String s : loader.named_variables)
                writer.println("@ "+s);
            for(String s : loader.integer_variables)
                writer.println("$ "+s);
            for(int i=0; i<loader.original_formulas.size(); i++)
                writer.println(loader.original_formulas.get(i)+" # "+variable_orderings.get(i).replace("#", ""));
            writer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }    
}
