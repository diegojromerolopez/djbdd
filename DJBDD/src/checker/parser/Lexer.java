package checker.parser;

import checker.error.LexicalException;
import java.io.*;

/** Converts freeform text into <code>Token</code>s, which represent 
 * keywords of the input language or <code>Atom</code>s.<p>
 *
 * Keywords:<p>
 * <ul>
 * <li><code>TT</code>
 * <li><code>FF</code>
 * <li><code>(</code>
 * <li><code>)</code>
 * <li><code>NOT</code>
 * <li><code>AND</code>
 * <li><code>OR</code>
 * <li><code>=></code>
 * <li><code><=></code>
 * </ul>
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see     Token
 * @see     Parser
 * @see     checker.error.LexicalException
 */
class Lexer {

    /** Input reader. 
     *
     * @see #Lexer(InputStream input)
     */
    private BufferedReader reader;

    /** Current character grabbed from the <code>reader</code>. 
     *
     * @see #advance()
     */
    private char lex_char;

    /** Text of the current token.
     *
     * Will be converted into a <code>checker.parser.Token</code>. 
     * by <code>textToToken</code>.
     *
     * @see #lex()
     * @see #textToToken()
     */
    private String lex_text = null;

    /** <code>true</code> if <code>reader</code> is at the beginning
     * of it's input, false otherwise.
     * 
     * Set by <code>lex()</code>.
     *
     * @see #lex()
     */
    private boolean lex_start = true;

    /** <code>true</code> if <code>reader</code> has reached the end of
     * it's input, false otherwise.  
     *
     * Set by <code>advance()</code>.
     *
     * @see #advance()
     */
    private boolean isEof = false;

    /** Constructs a new <code>Lexer</code> with <code>InputStream</code> 
     * containing characters to be lexed into <code>Token</code>s.
     * 
     * @param input the <code>InputStream</code> to be lexed.
     *
     * @see         java.io.InputStream
     */
    Lexer(InputStream input) {
	reader = new BufferedReader(new InputStreamReader(input));
	lex_start = true;
	isEof = false;
    }

    /** Gets the next character from <code>reader</code>. 
     *
     * There is no easy way to detect whether the <code>reader</code> has 
     * reached the end of its input, so we test whether the next
     * <code>int</code> that's read can be converted to a <code>char</code> 
     * and use that to set the <code>isEof</code> attribute.
     *
     * @exception IOException if there's a problem with the 
     *            <code>reader</code>.
     *
     * @see       #isEof
     * @see       #lex_char
     * @see       #lex()
     */
    private final void advance() throws IOException {
	int next_char = reader.read();	
	if (next_char != (char)next_char) {
	    isEof = true;
	    return;
	}
	lex_char = (char)next_char;
	return;
    }

    /** Lexes the next token from the <code>reader</code>. 
     * 
     * First, <code>lex()</code> builds <code>lex_text</code> from the
     * characters held in <code>lex_char</code> after each call to
     * <code>advance()</code>; then it calls <code>textToToken</code>
     * to convert <code>lex_text</code> into the corresponding 
     * <code>Token</code>. Various lexical errors are caught; for example
     * an equals sign in the input stream that is not succeeded by a
     * greater-than sign.
     *
     * @exception IOException thrown if there's a problem with the
     *            <code>reader</code>.
     * @exception LexicalException thrown if the input contains characters
     *            that do not form a valid <code>Token</code>.
     * @return    next token lexed from the <code>reader</code>.
     *
     * @see       checker.error.LexicalException
     */
    Token lex() throws IOException, LexicalException {
	lex_text = null;

	// If we're at the start of the input stream,
	// advance and reset lex_start.
	if (lex_start) {
	    advance();
	    lex_start = false;
	}

	// If we're at the end of the input stream, return
	// the Eof Token.
	if (isEof) {
	    return new Dollar();
	}

	// Skip whitespace.
	while (Character.isWhitespace(lex_char) && !isEof) {
	    advance();
	}

	// Build lex_text.
	if (lex_char == '(') {
	    lex_text = "(";
	    advance();
	} else if (lex_char == ')') {
	    lex_text = ")";
	    advance();
	} else if (lex_char == '=') {
	    advance();
	    if (lex_char != '>') {
		throw new LexicalException("'=' without '>'.");
	    }
	    lex_text = "=>";
	    advance();
	} else if (lex_char == '<') {
	    advance();
	    if (lex_char != '=') {
		throw new LexicalException("'<' without '='.");
	    }
	    advance();
	    if (lex_char != '>') {
		throw new LexicalException("'<=' without '>'.");
	    }
	    lex_text = "<=>";
	    advance();
	} else if (Character.isLetter(lex_char) || Character.isDigit(lex_char) || lex_char == '_' || lex_char == '{'  || lex_char == '}') {
	    lex_text = (new Character(lex_char)).toString();
	    advance();
	    while ((Character.isLetterOrDigit(lex_char) || Character.isDigit(lex_char) || lex_char == '_' || lex_char == '{'  || lex_char == '}') && !isEof) {
		lex_text = lex_text + lex_char;  
		advance();
	    }
	} else if (isEof) { // Catches whitespace immediately before an Eof.
	    if (lex_text == null) {
		return new Dollar();
	    }
	} else {
	    throw new LexicalException("Unknown character in input: "
				       + lex_char);
	}

	// Debug code.
	if (checker.Checker.PARSE_DEBUG) {
	    System.out.println("Lexing: " + lex_text);
	}

	// Convert lex_text to a Token and return.
	return textToToken();
    }

    /** Converts <code>lex_text</code> into the corresponding
     * <code>Token</code>.
     *
     * @return <code>Token</code> corresponding to the contents of 
     *         <code>lex_text</code>.
     *
     * @see    #lex_text
     * @see    #lex()
     * @see    Token
     */
    private final Token textToToken() {
	if (lex_text.equals("TT")) {
	    return new True();
	} else if (lex_text.equals("FF")) {
	    return new False();
	} else if (lex_text.equals("(")) {
	    return new LParen();
	} else if (lex_text.equals(")")) {
	    return new RParen();
	} else if (lex_text.equals("NOT")) {
	    return new Neg();
	} else if (lex_text.equals("AND")) {
	    return new Conj();
	} else if (lex_text.equals("OR")) {
	    return new Disj();
	} else if (lex_text.equals("=>")) {
	    return new Impl();
	} else if (lex_text.equals("<=>")) {
	    return new Iff();
	} else {
	    return new Atom(lex_text);
	}
    }
    
}
