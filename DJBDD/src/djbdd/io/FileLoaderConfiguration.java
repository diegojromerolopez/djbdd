/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

/**
 *
 * @author diegoj
 */
public class FileLoaderConfiguration {
    /**  */
    public String text;
    
    /** Must saves a file with the BDD as image */
    public boolean outputInFile = true;
    
    /** Is verbose mode? */
    public boolean verbose = true;
    
    /** Number of clausules by BDD (1 by default) */
    public int numberOfCNFByBDD = 1;
    
    /** Number of clausules in total */
    public int numberOfClausules = -1;
      
    public boolean useApply = true;
    
    public boolean useApplyInCreation = false;
    
    public static final int ALL_CLAUSULES = -1;

    /**
     * Load configuration for the loading of the file from the commandline.
     */
    public static FileLoaderConfiguration loadFromArgs(String[] args){
        FileLoaderConfiguration config = new FileLoaderConfiguration();
        String text = "Printing a BDD from a file";
        // Print in file?
        config.outputInFile = args.length >= 3;
        if (config.outputInFile) {
            text += " in a file";
        }
        // Numer of clausules by each BDD
        if (args.length >= 4 && args[3].matches("\\d+")) {
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
        if (args.length >= 5 && args[4].matches("\\d+")) {
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


}
