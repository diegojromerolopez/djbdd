package djbdd.reductors.io;

import djbdd.io.*;
import djbdd.core.*;

    
/**
 * Loads a BDD from a file
 * @author diegoj
 */
public class BDDReader extends djbdd.io.BDDReader{
    
    /**
     * Constructs a BDDReader.
     * @param format File format: dimacs, she, or cstyle.
     * @param filename File path with the BDD in file-format.
     */
    public BDDReader(String format, String filename){
        super(format, filename);
    }

   /**
     * We want to specify the number of formulas for the benchmarks.
     * @return BDD with only some of the CNFs.
     */
    public BDD readDimacs(int numberOfReadCnfs){
            DimacsFileLoader loader = new DimacsFileLoader(filename);
            BDD bdd = loader.run(1, numberOfReadCnfs);
            return bdd;
    }
    
    /**
     * We only want 50 formulas for the benchmarks.
     * @return BDD with 50 first CNFs.
     */
    @Override
    public BDD readDimacs(){
        return this.readDimacs(20);
    }
       
    
}