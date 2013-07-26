package checker.parser;

/** <code>Val</code> is the supertype used to represent values 
 * (<code>True</code>, <code>False</code> and <code>Atom</code>s).
 * This is particularly useful in the <code>shift()</code> method of the
 * <code>Parser</code>, which needs to differentiate between literals
 * and connectives.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see     checker.parser.Token
 * @see     checker.parser.Parser
 * @see     checker.parser.Parser#shift()
 * @see     Atom
 * @see     True
 * @see     False
 */
abstract class Val extends Token {}
