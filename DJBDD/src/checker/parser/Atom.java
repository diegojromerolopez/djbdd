package checker.parser;

/** Specifies atomic <code>Token</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
class Atom extends Val {

    final String literal;

    final int value() {
	return Token.ATOM;
    }

    Atom(final String literal) {
	this.literal = literal;
    }

    public final String toString() {
	return "Atom(" + this.literal +")";
    }
}
