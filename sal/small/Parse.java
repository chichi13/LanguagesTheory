/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package sal.small;

import java.util.EnumSet;

import static sal.small.Tree.*;
import static sal.small.Main.*;
import static sal.small.Token.*;

/**  Reads the source and generates an AST (abstract syntax tree). There is a separate method for each grammar rule.
 * Each method called returns an AST for the statement it has processed.
 * The Syntax rules for each method is given with the method.
 */
public class Parse {


    /**
     * Parse a program.
     *  Grammar rule {@code program         : 'program' name ('/' name )*; statementList }
     *
     * @return AST for complete program.
     */
    public static Tree<Token> program() {
        // read the first token from the input
        scan();
        Tree<Token> t = statementList();	// a program consists of a sequence of statements
        mustBe(EOF);
        return t;
    }

    /**
     *  Return a list of parsed statements.
     *  Grammar rule {@code statementList   : ( ifStatement | whileStatement  | doStatement
     * 											| printStatement | assignment | readStatement | ...   )* }

     * @return AST for statementList.
     */
    public static Tree<Token> statementList() {
        Tree<Token> stList = list(STATEMENTLIST);
		for(;;) {
			while(skipToken(SEMICOLON) ) /* do nothing */;
			Tree<Token> aStatement;	// next statement
			Token token = currentToken();
			switch(token) {
			case IF:            aStatement = ifStatement(); 	break;
			case WHILE:         aStatement = whileStatement(); 	break;
			case DO:			aStatement = doStatement(); 	break;
			case PRINT:         aStatement = printStatement(); 	break;
			case IDENTIFIER:	aStatement = assignment(); 		break;
			case READ:          aStatement = readStatement(); 	break;
			case BREAK:
			case CONTINUE:		
								aStatement = leaf(token);
								scan();
								break;

             default :          return stList;	// exit method with list
			}
			// add next statement to list
			stList.addChild(aStatement);
        }
    }


    /**
     * Grammar rule  {@code   ifStatement     : 'if' expression 'then' statementList 'end' }
     * @return AST for ifStatement
     */
    public static Tree<Token> ifStatement() {
        scan(); // skip the 'if' token
        Tree<Token> t  = expression();
        mustBe(THEN);
        t = list(IF, t, statementList());
		// insert code for any number of 'elif's here
		
        if(skipToken(ELSE)) {
			t.addChild(null);			 // 'no test'
			t.addChild(statementList()); // 'else' statements
		}	
        mustBe(END);
        return t;
    }

    /**
     * Grammar rule  {@code   whileStatement     : 'while' expression 'do' statementList 'end' }
     * @return AST for ifStatement
     */
    public static Tree<Token> whileStatement() {
        scan(); // skip the 'while' token
        Tree<Token> t = expression();
        mustBe(DO);
        t = list(WHILE, t, statementList());
        mustBe(END);
        return t;
    }

    /**
     * Grammar rule  {@code   doStatement     : 'do' statementList ( 'end' | 'until' expression ) }
     * @return AST for ifStatement
     */
    public static Tree<Token> doStatement() {
        // The operations needed here a similar to 'while' above
        // 1. get the next token 
        
        // 2. declare an AST (Tree<Token>) object called body and 
        //    set it by calling statementList() and assigning the result to it.
		
        // 3.  your program has read everything upto either 'until' or 'end'
		// check if the next token is UNTIL, if so skip it
		// (see treatement of 'else' in 'if' statement above)
		
		// 4. if until is found, return the rest of the AST by using
		//  return list(UNTIL, expression(), body)
		// do you understand what is happening?   What will be the AST
		//  of a simple do/until example?

		// 5. if UNTIL wasn't found the next token should be END (use mustBe) 	
		//  
		// 6. Whether END was found or not, 
		// return list(WHILE, null, body) 
		// a while statement with no test
		
		// 7. and delete the line 'return null;' after this one  
		return null;
    }


    /**
     * Grammar rule {@code   readStatement   : 'read' name (, name)* }
     * @return AST for read statement
     * The read statement is made into a list of individual reads
    */
    public static Tree<Token> readStatement() {
        scan();  // skip the 'print' token
        Tree<Token> readList = list(STATEMENTLIST);
        do {
            String name = currentText();
            mustBe(IDENTIFIER);
            readList.addChild(leaf(isStringName(name) ? READ_STR : READ_INT, name));
        } while (skipToken(COMMA));
        return readList;
    }

    /**
     * Grammar rules {@code printStatement  : 'print' printItem (',' printItem )* }
     * and          {@code printItem       : stringLiteral | expression }
     * @return AST for print statement
     * 
     * The print statement is made into a list of individual prints
     */
    public static Tree<Token> printStatement() {
        scan(); // skip the word 'print'
        Tree<Token> printList = list(STATEMENTLIST);
        do { Tree<Token> printExpr;
			if(currentToken() == STRING) {
				printExpr = leaf(STRING, currentText());
				scan();
			} else {
				printExpr = expression();
			}
			printList.addChild(list(PRINT, printExpr));
        } while (skipToken(COMMA));

        return printList;
        }

    /**
     * Grammar rule {@code assignStatement : name '=' expression }
     * @return AST.
     */
    public static Tree<Token> assignment() {
        Tree<Token> t =leaf(IDENTIFIER, currentText());
        Token token = scan();
        if(skipToken(INCREMENT, DECREMENT)) {
			t = list(token, t);
		} else {
			mustBe(ASSIGN); // skip over the = token
			t = list(ASSIGN, t, expression());
		}
        return t;
    }

     /**
     * Grammar rule {@code expression      : relopExpression }
     * @return AST.
     */
    public static Tree<Token> expression() {
        return relopExpression();
    }

    // start with lowest priority <, <= etc

 
 private static final EnumSet<Token> RELATIONALOPS = EnumSet.of(LE, LT, GE, GT, EQ, NE);
     /**
     *  Grammar rule {@code  relopExpression : addExpression [ ('<' | '<=' | '==' | '!=' | '<=' | '<' ) addExpression ] }
     * @return AST.
     */
    public static Tree<Token> relopExpression() {
        Tree<Token> t = addExpression();
        Token tok = currentToken();
        if(RELATIONALOPS.contains(tok)) {
            scan();
            t = list(tok, t, addExpression());
        }
        return t;

    }

    private static final EnumSet<Token> ADDOPS = EnumSet.of(PLUS, MINUS);

    /**
     * Grammar rule {@code addExpression   : multExpression ( ('+' | '-') multExpression )*}
     * @return AST.
     */
    public static Tree<Token> addExpression() {
        Tree<Token> t = multExpression();
        for(Token tok = currentToken(); ADDOPS.contains(tok); tok = currentToken()) {
            scan();
            t = list(tok, t, multExpression());
        }
        return t;
    }

    
    private static final EnumSet<Token> MULTOPS = EnumSet.of(TIMES, DIVIDE, MOD, SHR, SHL, SHRS);

    /**
     * Grammar rule {@code multExpression  : term ( ('*' | '/' | '%' ) term )* }
     * @return AST.
     */
    public static Tree<Token> multExpression() {
        Tree<Token> t = term();
        for(Token tok = currentToken(); MULTOPS.contains(tok); tok = currentToken()) {
            scan();
            t = list(tok, t, term());
        }
        return t;
    }

    /**
     * Grammar rule {@code term            : '(' expression ')' | name | number | '-' term}
     * @return AST.
     */
    public static Tree<Token> term() {
        Token token = currentToken();
        String value = currentText();
        Tree<Token> t = null;

        switch(token) {
            case LP:        scan();    // get next token
                            t = expression();
                            mustBe(RP);
                            return t;
            
			
            case IDENTIFIER:  t = leaf(token, value); break;


            case NUMBER :	
							{	if(value.charAt(0) == '#') {
									// convert string after '#' to binary, then back to decimal as a string
									value = Integer.toString(Integer.valueOf(value.substring(1), 16));
								}
								t = leaf(token, value);
								break; 
							}
			
	
            case MINUS:     scan();	// step over operator
							return list(token, term()); 

            default :       mustBe(IDENTIFIER, NUMBER, MINUS,
									LP, TO_INT, TO_STR, LEN_STR);  // didn't find the start of an expression - there has to be one;
							break;

        }
        scan();
        return t;
    }


}

