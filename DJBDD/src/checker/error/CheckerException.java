package checker.error;

/** Superclass of all the exceptions thrown by the checker. 
 *
 * @author  Sarah Mount
 * @version 1.0
 */
public abstract class CheckerException extends RuntimeException {

    /** Constructs a new <code>CheckerException</code> with detail message 
     * to be presented to the user.
     *
     * @param msg the detail message.
     */
    public CheckerException(final String msg) { 
	super(msg); 
    }
}
