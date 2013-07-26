package checker.parser;

/** Specifies <code>&lt;=&gt;</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class Iff extends Token {

    final int value() {
	return Token.IFF;
    }

    public final String toString() {
	return "IFF";
    }
}
