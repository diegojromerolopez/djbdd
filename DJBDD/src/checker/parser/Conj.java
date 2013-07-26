package checker.parser;

/** Specifies <code>AND</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class Conj extends Token {

    final int value() {
	return Token.CONJ;
    }

    public final String toString() {
	return "CONJ";
    }
}
