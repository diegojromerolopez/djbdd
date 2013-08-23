package djbdd.logic;
// $ANTLR !Unknown version! Logic.g 2013-08-23 17:42:51

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class LogicParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "TRUE", "FALSE", "Space", "'<->'", "'!='", "'->'", "'!->'", "'||'", "'&&'", "'!'", "'('", "')'"
    };
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int T__10=10;
    public static final int ID=4;
    public static final int FALSE=6;
    public static final int EOF=-1;
    public static final int TRUE=5;
    public static final int T__9=9;
    public static final int T__8=8;
    public static final int Space=7;

    // delegates
    // delegators


        public LogicParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public LogicParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return LogicParser.tokenNames; }
    public String getGrammarFileName() { return "Logic.g"; }


    public static class parse_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "parse"
    // Logic.g:9:1: parse : expression EOF ;
    public final LogicParser.parse_return parse() throws RecognitionException {
        LogicParser.parse_return retval = new LogicParser.parse_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        LogicParser.expression_return expression1 = null;


        Object EOF2_tree=null;

        try {
            // Logic.g:10:3: ( expression EOF )
            // Logic.g:10:6: expression EOF
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_parse27);
            expression1=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression1.getTree());
            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_parse29); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "parse"

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // Logic.g:13:1: expression : dimplication ;
    public final LogicParser.expression_return expression() throws RecognitionException {
        LogicParser.expression_return retval = new LogicParser.expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        LogicParser.dimplication_return dimplication3 = null;



        try {
            // Logic.g:14:3: ( dimplication )
            // Logic.g:14:6: dimplication
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_dimplication_in_expression48);
            dimplication3=dimplication();

            state._fsp--;

            adaptor.addChild(root_0, dimplication3.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class dimplication_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dimplication"
    // Logic.g:17:1: dimplication : isdifferent ( '<->' isdifferent )* ;
    public final LogicParser.dimplication_return dimplication() throws RecognitionException {
        LogicParser.dimplication_return retval = new LogicParser.dimplication_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal5=null;
        LogicParser.isdifferent_return isdifferent4 = null;

        LogicParser.isdifferent_return isdifferent6 = null;


        Object string_literal5_tree=null;

        try {
            // Logic.g:18:3: ( isdifferent ( '<->' isdifferent )* )
            // Logic.g:18:6: isdifferent ( '<->' isdifferent )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_isdifferent_in_dimplication62);
            isdifferent4=isdifferent();

            state._fsp--;

            adaptor.addChild(root_0, isdifferent4.getTree());
            // Logic.g:18:18: ( '<->' isdifferent )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==8) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Logic.g:18:19: '<->' isdifferent
            	    {
            	    string_literal5=(Token)match(input,8,FOLLOW_8_in_dimplication65); 
            	    string_literal5_tree = (Object)adaptor.create(string_literal5);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal5_tree, root_0);

            	    pushFollow(FOLLOW_isdifferent_in_dimplication68);
            	    isdifferent6=isdifferent();

            	    state._fsp--;

            	    adaptor.addChild(root_0, isdifferent6.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "dimplication"

    public static class isdifferent_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "isdifferent"
    // Logic.g:21:1: isdifferent : implication ( '!=' implication )* ;
    public final LogicParser.isdifferent_return isdifferent() throws RecognitionException {
        LogicParser.isdifferent_return retval = new LogicParser.isdifferent_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal8=null;
        LogicParser.implication_return implication7 = null;

        LogicParser.implication_return implication9 = null;


        Object string_literal8_tree=null;

        try {
            // Logic.g:22:3: ( implication ( '!=' implication )* )
            // Logic.g:22:6: implication ( '!=' implication )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_implication_in_isdifferent88);
            implication7=implication();

            state._fsp--;

            adaptor.addChild(root_0, implication7.getTree());
            // Logic.g:22:18: ( '!=' implication )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==9) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // Logic.g:22:19: '!=' implication
            	    {
            	    string_literal8=(Token)match(input,9,FOLLOW_9_in_isdifferent91); 
            	    string_literal8_tree = (Object)adaptor.create(string_literal8);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal8_tree, root_0);

            	    pushFollow(FOLLOW_implication_in_isdifferent94);
            	    implication9=implication();

            	    state._fsp--;

            	    adaptor.addChild(root_0, implication9.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "isdifferent"

    public static class implication_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "implication"
    // Logic.g:25:1: implication : notimplication ( '->' notimplication )* ;
    public final LogicParser.implication_return implication() throws RecognitionException {
        LogicParser.implication_return retval = new LogicParser.implication_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal11=null;
        LogicParser.notimplication_return notimplication10 = null;

        LogicParser.notimplication_return notimplication12 = null;


        Object string_literal11_tree=null;

        try {
            // Logic.g:26:3: ( notimplication ( '->' notimplication )* )
            // Logic.g:26:6: notimplication ( '->' notimplication )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_notimplication_in_implication114);
            notimplication10=notimplication();

            state._fsp--;

            adaptor.addChild(root_0, notimplication10.getTree());
            // Logic.g:26:21: ( '->' notimplication )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==10) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // Logic.g:26:22: '->' notimplication
            	    {
            	    string_literal11=(Token)match(input,10,FOLLOW_10_in_implication117); 
            	    string_literal11_tree = (Object)adaptor.create(string_literal11);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal11_tree, root_0);

            	    pushFollow(FOLLOW_notimplication_in_implication120);
            	    notimplication12=notimplication();

            	    state._fsp--;

            	    adaptor.addChild(root_0, notimplication12.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "implication"

    public static class notimplication_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "notimplication"
    // Logic.g:29:1: notimplication : or ( '!->' or )* ;
    public final LogicParser.notimplication_return notimplication() throws RecognitionException {
        LogicParser.notimplication_return retval = new LogicParser.notimplication_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal14=null;
        LogicParser.or_return or13 = null;

        LogicParser.or_return or15 = null;


        Object string_literal14_tree=null;

        try {
            // Logic.g:30:3: ( or ( '!->' or )* )
            // Logic.g:30:6: or ( '!->' or )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_in_notimplication142);
            or13=or();

            state._fsp--;

            adaptor.addChild(root_0, or13.getTree());
            // Logic.g:30:9: ( '!->' or )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==11) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // Logic.g:30:10: '!->' or
            	    {
            	    string_literal14=(Token)match(input,11,FOLLOW_11_in_notimplication145); 
            	    string_literal14_tree = (Object)adaptor.create(string_literal14);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal14_tree, root_0);

            	    pushFollow(FOLLOW_or_in_notimplication148);
            	    or15=or();

            	    state._fsp--;

            	    adaptor.addChild(root_0, or15.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "notimplication"

    public static class or_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "or"
    // Logic.g:33:1: or : and ( '||' and )* ;
    public final LogicParser.or_return or() throws RecognitionException {
        LogicParser.or_return retval = new LogicParser.or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal17=null;
        LogicParser.and_return and16 = null;

        LogicParser.and_return and18 = null;


        Object string_literal17_tree=null;

        try {
            // Logic.g:34:3: ( and ( '||' and )* )
            // Logic.g:34:6: and ( '||' and )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_in_or168);
            and16=and();

            state._fsp--;

            adaptor.addChild(root_0, and16.getTree());
            // Logic.g:34:10: ( '||' and )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==12) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // Logic.g:34:11: '||' and
            	    {
            	    string_literal17=(Token)match(input,12,FOLLOW_12_in_or171); 
            	    string_literal17_tree = (Object)adaptor.create(string_literal17);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal17_tree, root_0);

            	    pushFollow(FOLLOW_and_in_or174);
            	    and18=and();

            	    state._fsp--;

            	    adaptor.addChild(root_0, and18.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "or"

    public static class and_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and"
    // Logic.g:37:1: and : not ( '&&' not )* ;
    public final LogicParser.and_return and() throws RecognitionException {
        LogicParser.and_return retval = new LogicParser.and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal20=null;
        LogicParser.not_return not19 = null;

        LogicParser.not_return not21 = null;


        Object string_literal20_tree=null;

        try {
            // Logic.g:38:3: ( not ( '&&' not )* )
            // Logic.g:38:6: not ( '&&' not )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_not_in_and194);
            not19=not();

            state._fsp--;

            adaptor.addChild(root_0, not19.getTree());
            // Logic.g:38:10: ( '&&' not )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==13) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // Logic.g:38:11: '&&' not
            	    {
            	    string_literal20=(Token)match(input,13,FOLLOW_13_in_and197); 
            	    string_literal20_tree = (Object)adaptor.create(string_literal20);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal20_tree, root_0);

            	    pushFollow(FOLLOW_not_in_and200);
            	    not21=not();

            	    state._fsp--;

            	    adaptor.addChild(root_0, not21.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "and"

    public static class not_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "not"
    // Logic.g:41:1: not : ( '!' atom | atom );
    public final LogicParser.not_return not() throws RecognitionException {
        LogicParser.not_return retval = new LogicParser.not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal22=null;
        LogicParser.atom_return atom23 = null;

        LogicParser.atom_return atom24 = null;


        Object char_literal22_tree=null;

        try {
            // Logic.g:42:3: ( '!' atom | atom )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==14) ) {
                alt7=1;
            }
            else if ( ((LA7_0>=ID && LA7_0<=FALSE)||LA7_0==15) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // Logic.g:42:6: '!' atom
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal22=(Token)match(input,14,FOLLOW_14_in_not222); 
                    char_literal22_tree = (Object)adaptor.create(char_literal22);
                    root_0 = (Object)adaptor.becomeRoot(char_literal22_tree, root_0);

                    pushFollow(FOLLOW_atom_in_not225);
                    atom23=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom23.getTree());

                    }
                    break;
                case 2 :
                    // Logic.g:43:6: atom
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_atom_in_not236);
                    atom24=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom24.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "not"

    public static class atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // Logic.g:46:1: atom : ( ID | TRUE | FALSE | '(' expression ')' );
    public final LogicParser.atom_return atom() throws RecognitionException {
        LogicParser.atom_return retval = new LogicParser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID25=null;
        Token TRUE26=null;
        Token FALSE27=null;
        Token char_literal28=null;
        Token char_literal30=null;
        LogicParser.expression_return expression29 = null;


        Object ID25_tree=null;
        Object TRUE26_tree=null;
        Object FALSE27_tree=null;
        Object char_literal28_tree=null;
        Object char_literal30_tree=null;

        try {
            // Logic.g:47:3: ( ID | TRUE | FALSE | '(' expression ')' )
            int alt8=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt8=1;
                }
                break;
            case TRUE:
                {
                alt8=2;
                }
                break;
            case FALSE:
                {
                alt8=3;
                }
                break;
            case 15:
                {
                alt8=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // Logic.g:47:6: ID
                    {
                    root_0 = (Object)adaptor.nil();

                    ID25=(Token)match(input,ID,FOLLOW_ID_in_atom250); 
                    ID25_tree = (Object)adaptor.create(ID25);
                    adaptor.addChild(root_0, ID25_tree);


                    }
                    break;
                case 2 :
                    // Logic.g:48:6: TRUE
                    {
                    root_0 = (Object)adaptor.nil();

                    TRUE26=(Token)match(input,TRUE,FOLLOW_TRUE_in_atom257); 
                    TRUE26_tree = (Object)adaptor.create(TRUE26);
                    adaptor.addChild(root_0, TRUE26_tree);


                    }
                    break;
                case 3 :
                    // Logic.g:49:6: FALSE
                    {
                    root_0 = (Object)adaptor.nil();

                    FALSE27=(Token)match(input,FALSE,FOLLOW_FALSE_in_atom264); 
                    FALSE27_tree = (Object)adaptor.create(FALSE27);
                    adaptor.addChild(root_0, FALSE27_tree);


                    }
                    break;
                case 4 :
                    // Logic.g:50:6: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal28=(Token)match(input,15,FOLLOW_15_in_atom272); 
                    pushFollow(FOLLOW_expression_in_atom275);
                    expression29=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression29.getTree());
                    char_literal30=(Token)match(input,16,FOLLOW_16_in_atom277); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "atom"

    // Delegated rules


 

    public static final BitSet FOLLOW_expression_in_parse27 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_parse29 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dimplication_in_expression48 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_isdifferent_in_dimplication62 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_8_in_dimplication65 = new BitSet(new long[]{0x000000000000C070L});
    public static final BitSet FOLLOW_isdifferent_in_dimplication68 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_implication_in_isdifferent88 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_9_in_isdifferent91 = new BitSet(new long[]{0x000000000000C070L});
    public static final BitSet FOLLOW_implication_in_isdifferent94 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_notimplication_in_implication114 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_10_in_implication117 = new BitSet(new long[]{0x000000000000C070L});
    public static final BitSet FOLLOW_notimplication_in_implication120 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_or_in_notimplication142 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_11_in_notimplication145 = new BitSet(new long[]{0x000000000000C070L});
    public static final BitSet FOLLOW_or_in_notimplication148 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_and_in_or168 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_12_in_or171 = new BitSet(new long[]{0x000000000000C070L});
    public static final BitSet FOLLOW_and_in_or174 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_not_in_and194 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_and197 = new BitSet(new long[]{0x000000000000C070L});
    public static final BitSet FOLLOW_not_in_and200 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_14_in_not222 = new BitSet(new long[]{0x000000000000C070L});
    public static final BitSet FOLLOW_atom_in_not225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_not236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_atom250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_atom257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_atom264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_atom272 = new BitSet(new long[]{0x000000000000C070L});
    public static final BitSet FOLLOW_expression_in_atom275 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_atom277 = new BitSet(new long[]{0x0000000000000002L});

}