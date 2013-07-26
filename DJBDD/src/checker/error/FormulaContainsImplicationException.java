package checker.error;

/** Thrown by methods in the tautology checking algorithm, if the 
 * <code>checker.ast.Formula.removeImplications()</code> method fails.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see checker.ast.Formula#removeImplications()
 */
public class FormulaContainsImplicationException extends CheckerException {
    
    /** Constructs a new <code>FormulaContainsImplicationException</code>
     * with detail message to be presented to the user.
     *
     * @param msg the detail message.
     */
    public FormulaContainsImplicationException (final String msg) {
	super(msg);
    }
}
