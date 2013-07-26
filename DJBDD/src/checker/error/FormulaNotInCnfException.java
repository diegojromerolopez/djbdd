package checker.error;

/** Thrown by methods in the tautology checking algorithm, if the 
 * <code>checker.ast.Formula.nnfToCnf()</code> method fails.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see checker.ast.Formula#nnfToCnf()
 */
public class FormulaNotInCnfException extends CheckerException {

    /** Constructs a new <code>FormulaNotInCnfException</code>
     * with detail message to be presented to the user.
     *
     * @param msg the detail message.
     */
    public FormulaNotInCnfException (final String msg) {
	super(msg);
    }
}
