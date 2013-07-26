package checker.parser;

/** Specifies <code>(</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class LParen extends Token {

    final int value() {
	return Token.LPAREN;
    }

    public final String toString() {
	return "(";
    }
}
