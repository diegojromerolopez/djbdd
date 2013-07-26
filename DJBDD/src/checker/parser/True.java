package checker.parser;

/** Specifies <code>TT</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class True extends Val {

    final int value() {
	return Token.TRUE;
    }

    public final String toString() {
	return "TRUE";
    }
}
