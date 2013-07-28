package djbdd.logic;
// $ANTLR !Unknown version! Logic.g 2013-07-28 01:46:40

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class LogicLexer extends Lexer {
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int T__10=10;
    public static final int ID=4;
    public static final int FALSE=6;
    public static final int EOF=-1;
    public static final int T__9=9;
    public static final int TRUE=5;
    public static final int T__8=8;
    public static final int Space=7;

    // delegates
    // delegators

    public LogicLexer() {;} 
    public LogicLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public LogicLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "Logic.g"; }

    // $ANTLR start "T__8"
    public final void mT__8() throws RecognitionException {
        try {
            int _type = T__8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:3:6: ( '<->' )
            // Logic.g:3:8: '<->'
            {
            match("<->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__8"

    // $ANTLR start "T__9"
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:4:6: ( '->' )
            // Logic.g:4:8: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__9"

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:5:7: ( '||' )
            // Logic.g:5:9: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:6:7: ( '&&' )
            // Logic.g:6:9: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:7:7: ( '!' )
            // Logic.g:7:9: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:8:7: ( '(' )
            // Logic.g:8:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:9:7: ( ')' )
            // Logic.g:9:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:46:7: ( 'true' )
            // Logic.g:46:9: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:47:8: ( 'false' )
            // Logic.g:47:10: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:48:7: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '{' | '}' )+ )
            // Logic.g:48:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '{' | '}' )+
            {
            // Logic.g:48:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '{' | '}' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='{')||LA1_0=='}') ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Logic.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='{')||input.LA(1)=='}' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "Space"
    public final void mSpace() throws RecognitionException {
        try {
            int _type = Space;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Logic.g:49:7: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // Logic.g:49:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // Logic.g:49:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\t' && LA2_0<='\n')||LA2_0=='\r'||LA2_0==' ') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // Logic.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Space"

    public void mTokens() throws RecognitionException {
        // Logic.g:1:8: ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | TRUE | FALSE | ID | Space )
        int alt3=11;
        alt3 = dfa3.predict(input);
        switch (alt3) {
            case 1 :
                // Logic.g:1:10: T__8
                {
                mT__8(); 

                }
                break;
            case 2 :
                // Logic.g:1:15: T__9
                {
                mT__9(); 

                }
                break;
            case 3 :
                // Logic.g:1:20: T__10
                {
                mT__10(); 

                }
                break;
            case 4 :
                // Logic.g:1:26: T__11
                {
                mT__11(); 

                }
                break;
            case 5 :
                // Logic.g:1:32: T__12
                {
                mT__12(); 

                }
                break;
            case 6 :
                // Logic.g:1:38: T__13
                {
                mT__13(); 

                }
                break;
            case 7 :
                // Logic.g:1:44: T__14
                {
                mT__14(); 

                }
                break;
            case 8 :
                // Logic.g:1:50: TRUE
                {
                mTRUE(); 

                }
                break;
            case 9 :
                // Logic.g:1:55: FALSE
                {
                mFALSE(); 

                }
                break;
            case 10 :
                // Logic.g:1:61: ID
                {
                mID(); 

                }
                break;
            case 11 :
                // Logic.g:1:64: Space
                {
                mSpace(); 

                }
                break;

        }

    }


    protected DFA3 dfa3 = new DFA3(this);
    static final String DFA3_eotS =
        "\10\uffff\2\12\2\uffff\4\12\1\22\1\12\1\uffff\1\24\1\uffff";
    static final String DFA3_eofS =
        "\25\uffff";
    static final String DFA3_minS =
        "\1\11\7\uffff\1\162\1\141\2\uffff\1\165\1\154\1\145\1\163\1\60\1"+
        "\145\1\uffff\1\60\1\uffff";
    static final String DFA3_maxS =
        "\1\175\7\uffff\1\162\1\141\2\uffff\1\165\1\154\1\145\1\163\1\175"+
        "\1\145\1\uffff\1\175\1\uffff";
    static final String DFA3_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff\1\12\1\13\6\uffff\1"+
        "\10\1\uffff\1\11";
    static final String DFA3_specialS =
        "\25\uffff}>";
    static final String[] DFA3_transitionS = {
            "\2\13\2\uffff\1\13\22\uffff\1\13\1\5\4\uffff\1\4\1\uffff\1\6"+
            "\1\7\3\uffff\1\2\2\uffff\12\12\2\uffff\1\1\4\uffff\32\12\4\uffff"+
            "\1\12\1\uffff\5\12\1\11\15\12\1\10\7\12\1\3\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14",
            "\1\15",
            "",
            "",
            "\1\16",
            "\1\17",
            "\1\20",
            "\1\21",
            "\12\12\7\uffff\32\12\4\uffff\1\12\1\uffff\33\12\1\uffff\1\12",
            "\1\23",
            "",
            "\12\12\7\uffff\32\12\4\uffff\1\12\1\uffff\33\12\1\uffff\1\12",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | TRUE | FALSE | ID | Space );";
        }
    }
 

}