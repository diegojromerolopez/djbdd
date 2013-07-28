package djbdd.logic;
// $ANTLR !Unknown version! Logic.g 2013-07-28 01:46:40

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class LogicParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "TRUE", "FALSE", "Space", "'<->'", "'->'", "'||'", "'&&'", "'!'", "'('", "')'"
    };
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
    // Logic.g:17:1: dimplication : implication ( '<->' implication )* ;
    public final LogicParser.dimplication_return dimplication() throws RecognitionException {
        LogicParser.dimplication_return retval = new LogicParser.dimplication_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal5=null;
        LogicParser.implication_return implication4 = null;

        LogicParser.implication_return implication6 = null;


        Object string_literal5_tree=null;

        try {
            // Logic.g:18:3: ( implication ( '<->' implication )* )
            // Logic.g:18:6: implication ( '<->' implication )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_implication_in_dimplication62);
            implication4=implication();

            state._fsp--;

            adaptor.addChild(root_0, implication4.getTree());
            // Logic.g:18:18: ( '<->' implication )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==8) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Logic.g:18:19: '<->' implication
            	    {
            	    string_literal5=(Token)match(input,8,FOLLOW_8_in_dimplication65); 
            	    string_literal5_tree = (Object)adaptor.create(string_literal5);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal5_tree, root_0);

            	    pushFollow(FOLLOW_implication_in_dimplication68);
            	    implication6=implication();

            	    state._fsp--;

            	    adaptor.addChild(root_0, implication6.getTree());

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

    public static class implication_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "implication"
    // Logic.g:21:1: implication : or ( '->' or )* ;
    public final LogicParser.implication_return implication() throws RecognitionException {
        LogicParser.implication_return retval = new LogicParser.implication_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal8=null;
        LogicParser.or_return or7 = null;

        LogicParser.or_return or9 = null;


        Object string_literal8_tree=null;

        try {
            // Logic.g:22:3: ( or ( '->' or )* )
            // Logic.g:22:6: or ( '->' or )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_in_implication88);
            or7=or();

            state._fsp--;

            adaptor.addChild(root_0, or7.getTree());
            // Logic.g:22:9: ( '->' or )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==9) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // Logic.g:22:10: '->' or
            	    {
            	    string_literal8=(Token)match(input,9,FOLLOW_9_in_implication91); 
            	    string_literal8_tree = (Object)adaptor.create(string_literal8);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal8_tree, root_0);

            	    pushFollow(FOLLOW_or_in_implication94);
            	    or9=or();

            	    state._fsp--;

            	    adaptor.addChild(root_0, or9.getTree());

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
    // $ANTLR end "implication"

    public static class or_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "or"
    // Logic.g:25:1: or : and ( '||' and )* ;
    public final LogicParser.or_return or() throws RecognitionException {
        LogicParser.or_return retval = new LogicParser.or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal11=null;
        LogicParser.and_return and10 = null;

        LogicParser.and_return and12 = null;


        Object string_literal11_tree=null;

        try {
            // Logic.g:26:3: ( and ( '||' and )* )
            // Logic.g:26:6: and ( '||' and )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_in_or114);
            and10=and();

            state._fsp--;

            adaptor.addChild(root_0, and10.getTree());
            // Logic.g:26:10: ( '||' and )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==10) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // Logic.g:26:11: '||' and
            	    {
            	    string_literal11=(Token)match(input,10,FOLLOW_10_in_or117); 
            	    string_literal11_tree = (Object)adaptor.create(string_literal11);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal11_tree, root_0);

            	    pushFollow(FOLLOW_and_in_or120);
            	    and12=and();

            	    state._fsp--;

            	    adaptor.addChild(root_0, and12.getTree());

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
    // $ANTLR end "or"

    public static class and_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and"
    // Logic.g:29:1: and : not ( '&&' not )* ;
    public final LogicParser.and_return and() throws RecognitionException {
        LogicParser.and_return retval = new LogicParser.and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal14=null;
        LogicParser.not_return not13 = null;

        LogicParser.not_return not15 = null;


        Object string_literal14_tree=null;

        try {
            // Logic.g:30:3: ( not ( '&&' not )* )
            // Logic.g:30:6: not ( '&&' not )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_not_in_and140);
            not13=not();

            state._fsp--;

            adaptor.addChild(root_0, not13.getTree());
            // Logic.g:30:10: ( '&&' not )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==11) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // Logic.g:30:11: '&&' not
            	    {
            	    string_literal14=(Token)match(input,11,FOLLOW_11_in_and143); 
            	    string_literal14_tree = (Object)adaptor.create(string_literal14);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal14_tree, root_0);

            	    pushFollow(FOLLOW_not_in_and146);
            	    not15=not();

            	    state._fsp--;

            	    adaptor.addChild(root_0, not15.getTree());

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
    // $ANTLR end "and"

    public static class not_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "not"
    // Logic.g:33:1: not : ( '!' atom | atom );
    public final LogicParser.not_return not() throws RecognitionException {
        LogicParser.not_return retval = new LogicParser.not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal16=null;
        LogicParser.atom_return atom17 = null;

        LogicParser.atom_return atom18 = null;


        Object char_literal16_tree=null;

        try {
            // Logic.g:34:3: ( '!' atom | atom )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==12) ) {
                alt5=1;
            }
            else if ( ((LA5_0>=ID && LA5_0<=FALSE)||LA5_0==13) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // Logic.g:34:6: '!' atom
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal16=(Token)match(input,12,FOLLOW_12_in_not168); 
                    char_literal16_tree = (Object)adaptor.create(char_literal16);
                    root_0 = (Object)adaptor.becomeRoot(char_literal16_tree, root_0);

                    pushFollow(FOLLOW_atom_in_not171);
                    atom17=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom17.getTree());

                    }
                    break;
                case 2 :
                    // Logic.g:35:6: atom
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_atom_in_not182);
                    atom18=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom18.getTree());

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
    // Logic.g:38:1: atom : ( ID | TRUE | FALSE | '(' expression ')' );
    public final LogicParser.atom_return atom() throws RecognitionException {
        LogicParser.atom_return retval = new LogicParser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID19=null;
        Token TRUE20=null;
        Token FALSE21=null;
        Token char_literal22=null;
        Token char_literal24=null;
        LogicParser.expression_return expression23 = null;


        Object ID19_tree=null;
        Object TRUE20_tree=null;
        Object FALSE21_tree=null;
        Object char_literal22_tree=null;
        Object char_literal24_tree=null;

        try {
            // Logic.g:39:3: ( ID | TRUE | FALSE | '(' expression ')' )
            int alt6=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt6=1;
                }
                break;
            case TRUE:
                {
                alt6=2;
                }
                break;
            case FALSE:
                {
                alt6=3;
                }
                break;
            case 13:
                {
                alt6=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // Logic.g:39:6: ID
                    {
                    root_0 = (Object)adaptor.nil();

                    ID19=(Token)match(input,ID,FOLLOW_ID_in_atom196); 
                    ID19_tree = (Object)adaptor.create(ID19);
                    adaptor.addChild(root_0, ID19_tree);


                    }
                    break;
                case 2 :
                    // Logic.g:40:6: TRUE
                    {
                    root_0 = (Object)adaptor.nil();

                    TRUE20=(Token)match(input,TRUE,FOLLOW_TRUE_in_atom203); 
                    TRUE20_tree = (Object)adaptor.create(TRUE20);
                    adaptor.addChild(root_0, TRUE20_tree);


                    }
                    break;
                case 3 :
                    // Logic.g:41:6: FALSE
                    {
                    root_0 = (Object)adaptor.nil();

                    FALSE21=(Token)match(input,FALSE,FOLLOW_FALSE_in_atom210); 
                    FALSE21_tree = (Object)adaptor.create(FALSE21);
                    adaptor.addChild(root_0, FALSE21_tree);


                    }
                    break;
                case 4 :
                    // Logic.g:42:6: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal22=(Token)match(input,13,FOLLOW_13_in_atom218); 
                    pushFollow(FOLLOW_expression_in_atom221);
                    expression23=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression23.getTree());
                    char_literal24=(Token)match(input,14,FOLLOW_14_in_atom223); 

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
    public static final BitSet FOLLOW_implication_in_dimplication62 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_8_in_dimplication65 = new BitSet(new long[]{0x0000000000003070L});
    public static final BitSet FOLLOW_implication_in_dimplication68 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_or_in_implication88 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_9_in_implication91 = new BitSet(new long[]{0x0000000000003070L});
    public static final BitSet FOLLOW_or_in_implication94 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_and_in_or114 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_10_in_or117 = new BitSet(new long[]{0x0000000000003070L});
    public static final BitSet FOLLOW_and_in_or120 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_not_in_and140 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_11_in_and143 = new BitSet(new long[]{0x0000000000003070L});
    public static final BitSet FOLLOW_not_in_and146 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_12_in_not168 = new BitSet(new long[]{0x0000000000003070L});
    public static final BitSet FOLLOW_atom_in_not171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_not182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_atom196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_atom203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_atom210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_atom218 = new BitSet(new long[]{0x0000000000003070L});
    public static final BitSet FOLLOW_expression_in_atom221 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_atom223 = new BitSet(new long[]{0x0000000000000002L});

}