/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.main;

import djbdd.optimizer.FileOptimizer;
import djbdd.BDD;
import djbdd.timemeasurer.TimeMeasurer;
import djbdd.prob.*;
import djbdd.io.*;
import djbdd.io.DimacsFileLoader;
import djbdd.io.Printer;
import djbdd.test.Tester;
import djbdd.io.SheFileLoader;
import java.util.*;
import java.io.*;

/**
 * Basic Main that allows doing some basic operations on BDDs.
 * @author diegoj
 */
public class Main {

    /**
     * Load BDD from file.
     * @param format Format that will be used to load the BDD.
     * @param filename Filename that contains the BDD file.
     * @return BDD contained in the filename.
     */
    private static BDD loadBDDFromFile(String format, String filename){
        // Loading of a DIMACS file
        if(format.equals("dimacs")){
            DimacsFileLoader loader = new DimacsFileLoader(filename);
            BDD bdd = loader.run();
            return bdd;
        }
        
        // Loading of a She file
        if(format.equals("she")){
            SheFileLoader loader = new SheFileLoader(filename);
            BDD bdd = loader.run();
            return bdd;            
        }
        
        // Loading of a C-Style formula in BDD
        if(format.equals("cstyle") || format.equals("c-style")){
            CStyleFormulaFileLoader loader = new CStyleFormulaFileLoader(filename);
            BDD bdd = loader.run();
            return bdd;
        }
        
        // Loading of a BDD in a djbdd file
        if(format.equals("djbdd")){
            return BDD.fromFile(filename);
        }
        
        System.err.println("Type "+format+" not recognized");
        return null;
    }
    
    public static void main(String[] args) {
        // Si no hay 1 argumento, mostramos 
        if(args.length<1)
        {
            System.out.println("Main program of BDD in Java");
            System.out.println("1. BDD printing:");
            System.out.println("\tjava -jar BDD.jar --print --<format> <file>");
            System.out.println("2. BDD PNG image generation:");
            System.out.println("\tjava -jar BDD.jar --image --<format> <file>");
            System.out.println("3. Probability computation:");
            System.out.println("\tjava -jar DJBDD.jar --prob --<format> <file>");
            System.out.println("Formats allowed: ");
            System.out.println("\tdimacs\tDimacs CNF format. See http://www.cs.ubc.ca/~hoos/SATLIB/Benchmarks/SAT/satformat.ps or http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html.");
            System.out.println("\tshe\tSteven she file. See https://code.google.com/p/linux-variability-analysis-tools/");
            System.out.println("\tc-style\tC-style boolean expression preceded by a line with all variables separated by commas");
            System.out.println("\tdjbdd\tDJBDD file. Don't see anything because there are no documentation yet.");
            return;
        }
        
        // Main option selected for the user 
        String option = args[0];
        TimeMeasurer t = new TimeMeasurer(option);
        
        // Option switch
        if(option.equals("--runtests")){
                int testIndex = 0;
                if (args.length == 2)
                    testIndex = Integer.parseInt(args[1]);
                Tester.run(testIndex);
                return;
                
        }
        
        if(args.length < 3){
            System.err.println("Read the documents to use this program correctly");
            System.exit(-1);
        }
        
        // Reads a BDD file, and prints it
        if(option.equals("--print")){
            BDD bdd = Main.loadBDDFromFile(args[1], args[2]);
            boolean printBooleanVertices = true;
            bdd.print(printBooleanVertices);
            return;
        }
        
        // Reads a BDD file, and prints it as an image
        if(option.equals("--image")){
            BDD bdd = Main.loadBDDFromFile(args[1], args[2]);
            Printer.printBDD(bdd, args[2]+".png");
            return;
        }
        
        // Computes probabilities
        if(option.equals("--prob")){
            BDD bdd = Main.loadBDDFromFile(args[1], args[2]);
            ProbComputer computer = new ProbComputer(bdd);
            computer.run();
            return;
        }
        
        t.end();
        t.show();
    }
}
