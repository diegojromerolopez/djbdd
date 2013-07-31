// save it in a file called Logic.g
grammar Logic;

options {
  output=AST;
}

// parser/production rules start with a lower case letter
parse
  :  expression EOF!    // omit the EOF token
  ;

expression
  :  dimplication
  ;

dimplication
  :  implication ('<->'^ implication)*    // make `<->` the root
  ;

implication
  :  or ('->'^ or)*    // make `->` the root
  ;

or
  :  and ('||'^ and)*    // make `||` the root
  ;

and
  :  not ('&&'^ not)*      // make `&&` the root
  ;

not
  :  '!'^ atom    // make `~` the root
  |  atom
  ;

atom
  :  ID
  |  TRUE
  |  FALSE 
  |  '('! expression ')'!    // omit both `(` and `)`
  ;

// lexer/terminal rules start with an upper case letter
TRUE  : 'true';
FALSE  : 'false';
ID    : ('a'..'z' | 'A'..'Z' | '0'..'9' | '_' | '{' | '}')+;
Space : (' ' | '\t' | '\r' | '\n')+ {$channel=HIDDEN;};
