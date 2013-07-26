package checker.parser;

/** Specifies <code>)</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class RParen extends Token {

    final int value() {
	return Token.RPAREN;
    }

    public final String toString() {
	return ")";
    }
}
