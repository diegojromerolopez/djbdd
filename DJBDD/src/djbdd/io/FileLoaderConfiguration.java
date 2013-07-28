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
    public boolean outputInFile = false;
    
    /** Is verbose mode? */
    public boolean verbose = true;
    
    /** Number of clausules by BDD (1 by default) */
    public int numberOfCNFByBDD = 1;
    
    /** Number of clausules in total */
    public int numberOfClausules = -1;
      
    public boolean useApply = true;
    
    public boolean useApplyInCreation = false;
    
    public static final int ALL_CLAUSULES = -1;
}
