package checker.parser;

/** Specifies <code>FF</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class False extends Val {

    final int value() {
	return Token.FALSE;
    }

    public final String toString() {
	return "FALSE";
    }
}
