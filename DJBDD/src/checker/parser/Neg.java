package checker.parser;

/** Specifies <code>NOT</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class Neg extends Token {

    final int value() {
	return Token.NEG;
    }

    public final String toString() {
	return "NEG";
    }
}
