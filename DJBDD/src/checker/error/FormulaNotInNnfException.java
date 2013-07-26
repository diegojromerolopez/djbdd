package checker.error;

/** Thrown by methods in the tautology checking algorithm, if the 
 * <code>checker.ast.Formula.ToNnf()</code> method fails.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see checker.ast.Formula#toNnf()
 */
public class FormulaNotInNnfException extends CheckerException {

    /** Constructs a new <code>FormulaNotInNnfException</code>
     * with detail message to be presented to the user.
     *
     * @param msg the detail message.
     */
    public FormulaNotInNnfException (final String msg) {
	super(msg);
    }
}
