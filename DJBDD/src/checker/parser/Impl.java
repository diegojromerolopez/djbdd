package checker.parser;

/** Specifies <code>=&gt;</code> <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class Impl extends Token {

    final int value() {
	return Token.IMPL;
    }

    public final String toString() {
	return "IMPL";
    }
}
