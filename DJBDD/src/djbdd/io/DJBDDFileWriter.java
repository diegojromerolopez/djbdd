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
public class DJBDDFileWriter {
    
    String inputFilename;
    String outputFilename;
    public static final int ITERATIONS = 100;
    
    public DJBDDFileWriter(String inputFilename, String outputFilename){
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
    }
    
    public void run(FileLoaderConfiguration config) {
        config.numberOfCNFByBDD = 1;
        BDDSheFileLoader loader = new BDDSheFileLoader(inputFilename);
        loader.init(config);
        int LOOPS = ITERATIONS;
        String[] _variables = loader.variables.toArray(new String[loader.variables.size()]);

        try {
            PrintWriter writer = new PrintWriter(this.outputFilename, "UTF-8");
            for (int i = 0; i < loader.bdd_formulas.size(); i++) {
                TimeMeasurer t = new TimeMeasurer("\nFMLA " + (i + 1) + "/" + loader.bdd_formulas.size());
                String formulaI = loader.bdd_formulas.get(i);
                if (config.verbose) {
                    System.out.println("Formula " + (i + 1) + "/" + loader.bdd_formulas.size() + ": " + formulaI);
                }
                String[] _variable_order = loader.getVariableOrder(formulaI);
                BDD bdd = new BDD(formulaI, _variables, _variable_order, config.useApplyInCreation);
                int j = 0;
                while (j < LOOPS) {
                    Collections.shuffle(Arrays.asList(_variable_order));
                    BDD bddI = new BDD(formulaI, _variables, _variable_order, config.useApplyInCreation);
                    j++;
                    if (bddI.size() < bdd.size()) {
                        bdd = bddI;
                    }
                }
                writer.println("# BEGIN BDD " + (i + 1));
                writer.print(bdd.toString());
                writer.println("# END BDD " + (i + 1));
                t.end();
                t.show();
            }

            writer.close();
        } catch (Exception e) {
            System.err.println("System has failed!");
            e.printStackTrace();
        }
    }
}
