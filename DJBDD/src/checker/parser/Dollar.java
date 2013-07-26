package checker.parser;

/** Specifies <code>$</code> <code>Token</code>s, used to represent the
 * end of a file in the <code>Lexer</code> and the bottom of a stack
 * in the <code>Parser</code>.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see     checker.parser.Lexer
 * @see     checker.parser.Parser
 * @see     checker.parser.Parser#op_stack
 */
class Dollar extends Token {

    final int value() {
	return Token.DOLLAR;
    }

    public final String toString() {
	return "$";
    }
}
