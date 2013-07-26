package checker;

import checker.ast.Formula;
import checker.error.CheckerException;
import checker.parser.Parser;
import java.io.*;

/** Top level class of the tautology checker.
 *
 * Contains a command-line interface to the rest of the checker and
 * the tautology checking algorithm.
 *
 * The <code>main()</code> method calls <code>Parser.parse()</code> to
 * parse the contents of a file given on the command line and 
 * <code>check()</code> to tautology check the resulting abstract syntax 
 * tree.<p>
 *
 * To run the tautology checker (on a Linux platform), open an 
 * <code>xterm</code>, and type:<br> 
 * <code>java checker.Checker $filename</code><br> 
 * where <code>$filename</code> is a file containing the proposition you want
 * to check. Note that the <code>checker/</code> directory must be in your
 * <code>CLASSPATH</code>.<p>
 *
 * The grammar of the input language is as follows: <br>
 *
 * Prop ::= TT<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| FF<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| &lt;alpha&gt;&lt;alphanum&gt;*<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| ( Prop )<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| NOT Prop<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Prop AND Prop<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Prop OR Prop<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Prop =&gt; Prop<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Prop &lt;=&gt; Prop<p>
 *
 * Where &lt;alpha&gt; is an alphabetic characher and &lt;alphanum&gt;
 * is an alpha-numeric character.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see     checker.parser.Parser
 * @see     checker.ast.Formula
 */
public final class Checker { 

    /** If <code>true</code> debugging information is printed during
     * parsing. 
     */
    public static final boolean PARSE_DEBUG = false;

    /** If <code>true</code> debugging information is printed during
     * tautology checking.
     */
    public static final boolean DEBUG = true;

    /** Performs tautology checking on the contents of a file, specified
     * by the first argument on the command line. 
     *
     * Calls <code>checker.parser.Parser.parse()</code> to parse the
     * contents of <code>argv[0]</code> and 
     * <code>check(Formula formula)</code> to tautology check the resulting 
     * abstract syntax tree.
     *
     * @param argv[] expects a single filename from the command line.
     * @exception    FileNotFoundException if <code>argv[0]</code> isn't 
     *               present on the hard disk.
     * @exception    IOException if there's an input/output error during 
     *               parsing.
     *
     * @see          checker.parser.Parser#parse()
     * @see          #check(Formula formula)
     */
    public static void main(String argv[]) 
	throws CheckerException, FileNotFoundException, IOException {

	// Print a header
	System.out.println("\n\t A Tautology Checker "
			   + "for Propositional Calculus");
	System.out.println("\t------------------------------------------------");
	// Retrieve the name of the input file from the command line.
	String filename = argv[0];
	if (DEBUG) { // Print the contents of the input file.
	    System.out.print("\nInput file: ");
	    System.out.println((new File(filename)).toString());
	    System.out.println("----------");
	    BufferedReader in = 
		new BufferedReader
		    (new InputStreamReader(new FileInputStream(filename)));
	    while (in.ready()) {
		System.out.print((char)in.read());
	    }
	    in.close();
	}

	// Parse the input file and retrieve the resulting 
	// abstract syntax tree (AST).
	InputStream input = new FileInputStream(filename);
	Parser myParser = new Parser(input);
	input.close();
	Formula formula = myParser.getAST();
	if (DEBUG) { 
	    System.out.println("\nAbstract syntax tree:");
	    System.out.println("--------------------");
	    System.out.println(formula.toString());
	}

	// Tautology check the AST and print out a message, 
	// stating whether the AST is a tautology.
	if (check(formula)) {
	    System.out.println("\nYour formula is a tautology.\n");
	} else {
	    System.out.println("\nYour formula is not a tautology.\n");
	}
    }
  
    /** Apply the tautology checking algorithm to a <code>Formula</code>.
     *
     * @param formula the formula to be checked.
     * @return        <code>true</code> if <code>formula</code> is a 
     *                tautology, <code>false</code> otherwise.
     * @exception     CheckerException is raised if there's an error during 
     *                checking.
     *
     * @see           checker.ast.Formula
     */
    public static boolean check(Formula formula) throws CheckerException {
	// Remove implications
	formula = formula.removeImplications();
	if (DEBUG) { 
	    System.out.println("\nRESULT OF formula.removeImplications():");
	    System.out.println("--------------------------------------");
	    System.out.println(formula.toString()); 
	}

	// Convert to NNF
	formula = formula.toNnf();
	if (DEBUG) { 
	    System.out.println("\nRESULT OF formula.toNnf():");
	    System.out.println("-------------------------");
	    System.out.println(formula.toString()); 
	}

	// Convert to CNF
	formula = formula.nnfToCnf();
	if (DEBUG) { 
	    System.out.println("\nRESULT OF formula.nnfToCnf():");
	    System.out.println("----------------------------");
	    System.out.println(formula.toString()); 
	}

	// Simplify
	formula = formula.simplifyCnf();
	if (DEBUG) { 
	    System.out.println("\nRESULT OF formula.simplifyCnf():");
	    System.out.println("-------------------------------");
	    System.out.println(formula.toString()); 
	}

	// Test for truth
	return checker.ast.True.VALUE == formula;
    }

}
