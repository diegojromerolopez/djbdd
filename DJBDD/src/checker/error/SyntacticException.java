package checker.error;

/** Thrown by the parser, on encountering a syntax error in its input.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see checker.parser.Parser
 */
public class SyntacticException extends CheckerException {

    /** Constructs a new <code>SyntacticException</code>
     * with detail message to be presented to the user.
     *
     * @param msg the detail message.
     */
    public SyntacticException(final String msg) {
	super(msg);
    }
}
