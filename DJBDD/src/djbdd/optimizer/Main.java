/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.optimizer;

import djbdd.BDD;
import djbdd.timemeasurer.TimeMeasurer;
import djbdd.io.*;
import djbdd.io.SheFileLoader;
import java.util.*;

/**
 *
 * @author diegoj
 */
public class Main {
   
    /**
     * Convert a she file in an optimized file.
     * @param filename Name of the she file that will be converted.
     * @param config Configuration used in the she file loading.
     */
    private static void convertSheFile(String filename, FileLoaderConfiguration config){
        TimeMeasurer t = new TimeMeasurer("She conversion");
        
        config.numberOfCNFByBDD = 1;
        config.useApplyInCreation = true;
        SheFileLoader loader = new SheFileLoader(filename);
        ArrayList<String> bddFormulas = loader.getFormulas(config);

        //FileOptimizer extractor = new FileOptimizer(loader.bdd_formulas, filename+".bdd.txt");
        Separator extractor = new Separator(bddFormulas, filename+".bdd.txt");
        extractor.run();
        t.end();
        t.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String text = "Converts a formula";
        String fileType = args[1];
        text = "Converts a formula";
        String source = args[1];
        String inputFile = args[2];
        args = Arrays.copyOfRange(args, 3, args.length);
        FileLoaderConfiguration config = FileLoaderConfiguration.loadFromArgs(args);
        if (source.equalsIgnoreCase("fmla")) {
            text += " get from commandline";
            System.out.println("TODO");
        } else if (source.equalsIgnoreCase("dimacs")) {
            System.out.println("TODO");
            //Main.extractDimacsFile(args[2], config);
        } else if (source.equalsIgnoreCase("she")) {
            Main.convertSheFile(inputFile, config);
        } else {
            System.out.println("You are not using this software correctly. Use:");
            System.out.println("--convert she <She file>");
            System.out.println("For example --convert she ./data/2.6.12.she");
        }
                
    }
    
}
