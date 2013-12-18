package djbdd.io;
import djbdd.core.*;

    
/**
 * Reader of BDDs from files
 * @author diegoj
 */
public class BDDReader {
    
    /** Format of the file to be read */
    protected String format;
    
    /** Path of the file */
    protected String filename;

    /**
     * Constructs a BDDReader.
     * @param format File format: dimacs, she, or cstyle.
     * @param filename File path with the BDD in file-format.
     */
    public BDDReader(String format, String filename){
        this.format = format;
        this.filename = filename;
    }

    /**
     * Reads the BDD in DIMACS file format.
     * @return The BDD contained in the file.
     */
    protected BDD readDimacs() {
        DimacsFileLoader loader = new DimacsFileLoader(filename);
        BDD bdd = loader.run();
        return bdd;
    }

    /**
     * Reads the BDD in She file format.
     * @return The BDD contained in the file.
     */
    protected BDD readShe() {
        SheFileLoader loader = new SheFileLoader(filename);
        BDD bdd = loader.run();
        return bdd;
    }
    
    /**
     * Reads the BDD in CStyle file format.
     * @return The BDD contained in the file.
     */
    protected BDD readCstyle() {
        CStyleFormulaFileLoader loader = new CStyleFormulaFileLoader(filename);
        BDD bdd = loader.run();
        return bdd;
    }
    
    /**
     * Reads the BDD from a file.
     * @return The BDD from a file.
     */
    public BDD read(){
        // Loading of a DIMACS file
        if(format.equals("dimacs")){
            return this.readDimacs();
        }
        
        // Loading of a She file
        if(format.equals("she")){
            return this.readShe();
        }
        
        // Loading of a C-Style formula in BDD
        if(format.equals("cstyle") || format.equals("c-style")){
            return this.readCstyle();
        }
        
        // Loading of a BDD in a djbdd file
        if(format.equals("djbdd")){
            return BDD.fromFile(filename);
        }
        
        System.err.println("Type "+format+" not recognized");
        return null;
    }
    
}
