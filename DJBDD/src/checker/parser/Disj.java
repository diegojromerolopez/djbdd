package checker.parser;

/** Specifies <code>OR</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class Disj extends Token {

    final int value() {
	return Token.DISJ;
    }

    public final String toString() {
	return "DISJ";
    }
}
