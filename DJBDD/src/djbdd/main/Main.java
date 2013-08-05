/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.main;

import djbdd.BDD;
import djbdd.timemeasurer.TimeMeasurer;
import djbdd.io.*;
import djbdd.io.DimacsFileLoader;
import djbdd.io.Printer;
import djbdd.test.Tester;
import djbdd.io.SheFileLoader;
import java.util.*;
import java.io.*;

/**
 *
 * @author diegoj
 */
public class Main {
    
    /**
     * Load configuration for the loading of the DIMACS file from the commandline.
     */
    private static FileLoaderConfiguration loadDimacsConfig(String[] args){
        FileLoaderConfiguration config = new FileLoaderConfiguration();
        String text = "Printing a BDD from a dimacs file";
        // Print in file?
        config.outputInFile = args.length >= 4 && args[3].equals("file");
        if (config.outputInFile) {
            text += " in a file";
        }
        // Use apply algorithm?
        config.useApply = args.length >= 5 && args[4].equals("apply");
        if (config.useApply) {
            text += " using apply operation";
        }
        // Numer of clausules by each BDD
        if (args.length >= 6 && args[5].matches("\\d+")) {
            config.numberOfCNFByBDD = Integer.parseInt(args[5]);
            if(config.numberOfCNFByBDD<=0){
                config.numberOfCNFByBDD = 1;
            }
            if(config.numberOfCNFByBDD>1)
                text += " each "+config.numberOfCNFByBDD+" CNFs";
            else
                text += " each CNF";
        }
        else{
            text += " each CNF";
        }
        // Number of clausules, -1 implies this will get all clausules
        if (args.length >= 7 && args[6].matches("\\d+")) {
            config.numberOfClausules = Integer.parseInt(args[6]);
            if (config.numberOfClausules <= 0) {
                config.numberOfClausules = FileLoaderConfiguration.ALL_CLAUSULES;
            }
            if(config.numberOfClausules != FileLoaderConfiguration.ALL_CLAUSULES)
                text += " and getting " + config.numberOfClausules + " clausules. ";
            else
                text += " and getting all clausules. ";
        }
        else{
            text += " and getting all clausules. ";
        }
        config.text = text;
        return config;
    }
    
    /**
     * Load configuration for the loading of the S. She file from the commandline.
     */
    private static FileLoaderConfiguration loadSheConfig(String[] args){
        FileLoaderConfiguration config = new FileLoaderConfiguration();
        String text = "Printing a BDD from a S. She file";
        // Print in file?
        config.outputInFile = args.length >= 4 && args[3].equals("file");
        if (config.outputInFile) {
            text += " in a file";
        }
        // Numer of clausules by each BDD
        if (args.length >= 5 && args[4].matches("\\d+")) {
            config.numberOfCNFByBDD = Integer.parseInt(args[4]);
            if(config.numberOfCNFByBDD<=0){
                config.numberOfCNFByBDD = 1;
            }
            if(config.numberOfCNFByBDD>1)
                text += " each "+config.numberOfCNFByBDD+" CNFs";
            else
                text += " each clausule";
        }
        else{
            text += " each clausule";
        }
        // Number of clausules, -1 implies this will get all clausules
        if (args.length >= 6 && args[5].matches("\\d+")) {
            config.numberOfClausules = Integer.parseInt(args[5]);
            if (config.numberOfClausules <= 0) {
                config.numberOfClausules = FileLoaderConfiguration.ALL_CLAUSULES;
            }
            if(config.numberOfClausules != FileLoaderConfiguration.ALL_CLAUSULES)
                text += " and getting " + config.numberOfClausules + " clausules. ";
            else
                text += " and getting all clausules. ";
        }
        else{
            text += " and getting all clausules. ";
        }
        config.text = text;
        return config;
    }
    
    
    /**
     * Reads a dimacs file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param filename Name of the file containing the CNF in dimacs format.
     */
    protected static void runLoadDimacsFile(String filename, boolean useApplyAlgorithm){
        /*BDDDimacsLoader loader = new BDDDimacsLoader(filename);
        BDD bdd = null;
        if(useApplyAlgorithm)
            bdd = loader.loadFileUsingApplyAlgorithm(15);
        else
            bdd = loader.loadFile();
        System.out.println("Función " + bdd.function);
        //System.out.println(bdd.toString());
        // Evaluamos la función
        bdd.print();
        double[] P = bdd.P_xi();
        System.out.println("Probabilities");
        for(double p : P)
            System.out.print(p+", ");
        System.out.println("");    */
    }

     /**
     * Reads a dimacs file an prints the BDD contained in the file.
     * This method DOES NOT creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param filename Name of the file containing the CNF in dimacs format.
     */
    private static void printDimacsFile(String filename, FileLoaderConfiguration config){
        TimeMeasurer t = new TimeMeasurer("dimacs loading");
        DimacsFileLoader loader = new DimacsFileLoader(filename);
        System.out.println(config.text);
        BDD bdd = loader.loadFile(config);
        t.end();
        t.show();
        bdd.print();
        if(config.outputInFile){
            Printer printer = new Printer(bdd);
            printer.print("./"+filename);
        }
    }
    
         /**
     * Reads a dimacs file an prints the BDD contained in the file.
     * This method DOES NOT creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param filename Name of the file containing the CNF in dimacs format.
     */
    private static void printSheFile(String filename, FileLoaderConfiguration config){
        TimeMeasurer t = new TimeMeasurer("She loading");
        SheFileLoader loader = new SheFileLoader(filename);
        System.out.println(config.text);
        BDD bdd = loader.loadFile(config);
        t.end();
        t.show();
        bdd.print();
        bdd.toFile(filename+".bdd.txt");
        if(config.outputInFile){
            Printer printer = new Printer(bdd);
            printer.print("./"+filename);
        }
    }
    
     /**
     * Creates a BDD from a formula.
     * This method DOES NOT creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param String fmla Logic formula.
     * @param ArrayList<String> variables Variables used in the formula. Note that this parameter gives the order of them.
     */
    protected static void printFmla(String fmla, ArrayList<String> variables){
        BDD.initVariables(variables);
        BDD bdd = new BDD(fmla, false);
        Printer printer = new Printer(bdd);
        printer.print("./"+fmla);
    }
    
    /**
     * Convert a she file in an optimized file.
     * @param filename Name of the she file that will be converted.
     * @param config Configuration used in the she file loading.
     */
    protected static void convertSheFile(String filename, FileLoaderConfiguration config){
        TimeMeasurer t = new TimeMeasurer("She conversion");
        
        config.numberOfCNFByBDD = 1;
        config.useApplyInCreation = true;
        SheFileLoader loader = new SheFileLoader(filename);
        loader.init(config);
        System.out.println(config.text);
        
        BDD.initVariables(loader.variables);
        FileOptimizer extractor = new FileOptimizer(loader.bdd_formulas, filename+".bdd.txt");
        extractor.run();
        t.end();
        t.show();
    }
    
    /**
     * Generate the probabilities and shows them to user.
     */
    protected static void runFormula(String formula, ArrayList<String> variables){
        /*BDD bdd = new BDD(formula, variables);
        System.out.println(bdd.toString());
        // Evaluamos la función
        bdd.printAsTable();
        double[] P = bdd.P_xi();
        System.out.println("Probabilities");
        System.out.println(Arrays.toString(P));*/
    }
    
    /**
     * Combines some BDDs.
     */
    protected static void combineBDDFile(String inputFile, String operator){
        Combinator c = new Combinator(inputFile, operator);
        c.run();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Si no hay 1 argumento, mostramos 
        if(args.length<1)
        {
            System.out.println("Main program of BDD in Java");
            System.out.println("1. Probability computation:");
            System.out.println("\tFrom formula: java -jar BDD.jar --prob fmla <logic formula in java code> <variables in 'x1,x2,...,xN' format>");
            System.out.println("\tFrom dimacs file: java -jar BDD.jar --prob dimacs <filepath of dimacs file> [apply]");
            System.out.println("2. BDD printing:");
            System.out.println("\tFrom formula: java -jar BDD.jar --print fmla <logic formula in java code> <variables in 'x1,x2,...,xN' format>");
            System.out.println("\tFrom dimacs file: java -jar BDD.jar --print dimacs <filepath of dimacs file> [apply] [number of CNFs by BDD] [number of clausules]");
            System.out.println("\tFrom S. She file: java -jar BDD.jar --print she <filepath of She file> [number of CNFs by BDD] [number of clausules]");
            return;
        }
        String text = "";
        String option = args[0];
        TimeMeasurer t = new TimeMeasurer(option);
        if(option.equals("--runtests")){
                int testIndex = 0;
                if (args.length == 2)
                    testIndex = Integer.parseInt(args[1]);
                Tester.run(testIndex);
        }
        else if(option.equals("--print")){
                text = "Printing a formula";
                if(args[1].equalsIgnoreCase("fmla")){
                    text += " get from commandline";
                    System.out.println(text);
                    ArrayList<String> variables = new ArrayList<String>( Arrays.asList(args[3].split(",\\s*") ));
                    Main.printFmla(args[2], variables);
                }
                else if(args[1].equalsIgnoreCase("dimacs")){
                    FileLoaderConfiguration config = loadDimacsConfig(args);
                    Main.printDimacsFile(args[2], config);
                }
                else if(args[1].equalsIgnoreCase("she")){
                    FileLoaderConfiguration config = loadSheConfig(args);
                    Main.printSheFile(args[2], config);
                }
                else{
                    System.out.println("You are not using this software correctly");
                }
        // Convert formulas to bdd file
        }else if(option.equals("--djbdd-file-conversion") || option.equals("--convert")){
            text = "Converts a formula";
                if(args[1].equalsIgnoreCase("fmla")){
                    text += " get from commandline";
                    System.out.println("TODO");
                }
                else if(args[1].equalsIgnoreCase("dimacs")){
                    FileLoaderConfiguration config = loadDimacsConfig(args);
                     System.out.println("TODO");
                    //Main.extractDimacsFile(args[2], config);
                }
                else if(args[1].equalsIgnoreCase("she")){
                    FileLoaderConfiguration config = loadSheConfig(args);
                    Main.convertSheFile(args[2], config);
                }
                else{
                    System.out.println("You are not using this software correctly");
                }
        // Reads the converted file, operate its bdds and prints to other file
        }else if(option.equals("--combine-bdd-file") || option.equals("--combine")){
            System.out.println(args[1]);
            if(args.length<3){
                System.err.println("java -jar BDD.jar --combine <inputfile> <outputfile> [operator]");
                System.exit(-1);
            }
            String inputFile = args[1];
            String outputFile = args[2];
            String operator = "and";
            if(args.length >= 4)
                operator = args[3];
            text = "Reads a formula and applies ";
            //BDD bdd = Main.combineBDDFile(inputFile, operator);
            //bdd.toFile(outputFile);
            Main.combineBDDFile(inputFile, operator);
        }else if(option.equals("--prob")){
                text = "Computing probabilities";
                if(args[1].equalsIgnoreCase("fmla")){
                    text += " get from commandline";
                    System.out.println(text);
                    ArrayList<String> variables = new ArrayList<String>( Arrays.asList(args[3].split(",\\s*") ));
                    Main.runFormula(args[2], variables);
                }
                else if(args[1].equalsIgnoreCase("dimacs")){
                    boolean useApply = args.length==4 && args[3].equals("apply");
                    text += " get from dimacs file";
                    if(useApply)
                        text += " use apply operator in each CNF";
                    System.out.println(text);
                    Main.runLoadDimacsFile(args[2], useApply);
                }
                else{
                    System.out.println("You are not using this software correctly");
                }
        }
        t.end();
        t.show();
    }
}
