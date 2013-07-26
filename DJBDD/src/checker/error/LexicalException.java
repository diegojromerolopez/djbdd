package checker.error;

/** Thrown by the lexer, on encountering an error in its input.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see checker.parser.Lexer
 */
public class LexicalException extends CheckerException {

    /** Constructs a new <code>LexicalException</code>
     * with detail message to be presented to the user.
     *
     * @param msg the detail message.
     */
    public LexicalException(final String msg) {
	super(msg);
    }
}
